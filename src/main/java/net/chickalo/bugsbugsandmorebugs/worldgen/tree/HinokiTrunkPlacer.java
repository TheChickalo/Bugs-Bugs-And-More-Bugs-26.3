package net.chickalo.bugsbugsandmorebugs.worldgen.tree;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;

/** Straight spruce-like trunk, occasional full-wood surface roots and local podzol. */
public final class HinokiTrunkPlacer extends StraightTrunkPlacer {
    public static final MapCodec<HinokiTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance).apply(instance, HinokiTrunkPlacer::new));

    public HinokiTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTrunkPlacerTypes.HINOKI_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(WorldGenLevel level,
            BiConsumer<BlockPos, BlockState> trunkSetter, RandomSource random,
            int treeHeight, BlockPos origin, TreeFeature tree) {
        // Make sure the extra columns are clear before placing any trunk logs.
        // Otherwise generation beside terrain or another tree can leave half a trunk.
        for (int dx = 0; dx <= 1; dx++) {
            for (int dz = 0; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                if (level.getBlockState(origin.offset(dx, -1, dz)).isAir()) {
                    return List.of();
                }
                for (int y = 0; y < treeHeight; y++) {
                    if (!TreeFeature.validTreePos(level, origin.offset(dx, y, dz))) {
                        return List.of();
                    }
                }
            }
        }

        var crown = super.placeTrunk(level, trunkSetter, random, treeHeight, origin, tree);
        if (crown.isEmpty()) return crown;

        // Fill the other three columns to make a proper 2 x 2 trunk all the
        // way to the top (not just a thick lower stump).
        for (int dx = 0; dx <= 1; dx++) {
            for (int dz = 0; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                placeBelowTrunkBlock(level, trunkSetter, random, origin.offset(dx, -1, dz), tree);
                for (int y = 0; y < treeHeight; y++) {
                    placeLog(level, trunkSetter, random, origin.offset(dx, y, dz), tree);
                }
            }
        }

        // Full bark on every face: short buttress roots flush with the tree base.
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (random.nextFloat() >= 0.65F) continue;
            BlockPos first = origin.relative(direction);
            putRoot(level, trunkSetter, first);
            if (random.nextFloat() < 0.23F) {
                putRoot(level, trunkSetter, first.relative(direction));
            }
        }

        // Patchy ground conversion only around successfully grown trees. Preserve
        // slopes and avoid replacing rock, paths or water in the highland biome.
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                if (dx * dx + dz * dz > 18 || random.nextFloat() > 0.46F) continue;
                for (int dy = 1; dy >= -2; dy--) {
                    BlockPos soil = origin.offset(dx, dy - 1, dz);
                    BlockState state = level.getBlockState(soil);
                    if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT)
                            || state.is(ModBlocks.GRASSY_LOAM) || state.is(ModBlocks.LOAM)) {
                        level.setBlock(soil, Blocks.PODZOL.defaultBlockState(), 2);
                        break;
                    }
                }
            }
        }
        // Double-trunk attachments center foliage over the full 2 x 2 stem.
        return List.of(new FoliagePlacer.FoliageAttachment(crown.get(0).pos(), 0, true));
    }

    private static void putRoot(WorldGenLevel level,
                                BiConsumer<BlockPos, BlockState> trunkSetter, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!level.getBlockState(pos.below()).isAir()
                && (state.isAir() || state.canBeReplaced())) {
            trunkSetter.accept(pos, ModBlocks.HINOKI_WOOD.defaultBlockState());
        }
    }
}
