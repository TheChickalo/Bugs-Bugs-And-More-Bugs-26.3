package net.chickalo.bugsbugsandmorebugs.worldgen.tree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Ginkgos have short surface buttresses. Ancient trees support the crown
 * with a full 2x2 straight bole, not just a four-block stump.
 */
public final class GinkgoTrunkPlacer extends StraightTrunkPlacer {
    public static final MapCodec<GinkgoTrunkPlacer> CODEC =
            RecordCodecBuilder.mapCodec(instance -> trunkPlacerParts(instance)
                    .and(Codec.BOOL.fieldOf("thick").forGetter(p -> p.thick))
                    .apply(instance, GinkgoTrunkPlacer::new));

    private final boolean thick;

    public GinkgoTrunkPlacer(int baseHeight, int heightRandA, int heightRandB,
                              boolean thick) {
        super(baseHeight, heightRandA, heightRandB);
        this.thick = thick;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTrunkPlacerTypes.GINKGO_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(
            WorldGenLevel level, BiConsumer<BlockPos, BlockState> trunkSetter,
            RandomSource random, int treeHeight, BlockPos origin, TreeFeature tree) {
        int width = thick ? 2 : 1;
        if (thick) {
            // Check the entire extra trunk area before writing any tree blocks.
            for (int dx = 0; dx < width; dx++) {
                for (int dz = 0; dz < width; dz++) {
                    if (dx == 0 && dz == 0) continue;
                    BlockPos base = origin.offset(dx, 0, dz);
                    if (level.getBlockState(base.below()).isAir()
                            || !level.getFluidState(base.below()).isEmpty()) return List.of();
                    for (int y = 0; y < treeHeight; y++) {
                        if (!TreeFeature.validTreePos(level, base.above(y))) return List.of();
                    }
                }
            }
        }

        List<FoliagePlacer.FoliageAttachment> crown =
                super.placeTrunk(level, trunkSetter, random, treeHeight, origin, tree);
        if (crown.isEmpty()) return crown;

        if (thick) {
            for (int dx = 0; dx < width; dx++) {
                for (int dz = 0; dz < width; dz++) {
                    if (dx == 0 && dz == 0) continue;
                    BlockPos base = origin.offset(dx, 0, dz);
                    placeBelowTrunkBlock(level, trunkSetter, random, base.below(), tree);
                    for (int y = 0; y < treeHeight; y++) {
                        placeLog(level, trunkSetter, random, base.above(y), tree);
                    }
                }
            }
        }

        // Keep the roots low and irregular, with air gaps between directions.
        // These use fully barked ginkgo wood, and never protrude above Y+0.
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (random.nextFloat() > (thick ? 0.83F : 0.60F)) continue;
            int x = direction == Direction.EAST ? width - 1 :
                    direction == Direction.WEST ? 0 : random.nextInt(width);
            int z = direction == Direction.SOUTH ? width - 1 :
                    direction == Direction.NORTH ? 0 : random.nextInt(width);
            BlockPos first = origin.offset(x, 0, z).relative(direction);
            if (placeRoot(level, trunkSetter, first)
                    && random.nextFloat() < (thick ? 0.40F : 0.20F)) {
                placeRoot(level, trunkSetter, first.relative(direction));
            }
        }

        // Horizontal limbs give the crown a supported, layered silhouette.
        // Every successful limb tip receives its own rounded leaf attachment.
        // Place the upper limbs near the crown so no branch ends are bare.
        List<FoliagePlacer.FoliageAttachment> attachments = new java.util.ArrayList<>();
        attachments.add(new FoliagePlacer.FoliageAttachment(
                crown.get(0).pos(), 0, thick));

        int branchCount = treeHeight < 10 ? 3 : thick ? 7 : 5;
        double rotation = random.nextDouble() * Math.PI * 2;
        for (int i = 0; i < branchCount; i++) {
            double angle = rotation + i * Math.PI * 2.0 / branchCount
                    + (random.nextDouble() - 0.5) * 0.27;
            int reach = treeHeight < 10 ? 1 + random.nextInt(2)
                    : thick ? 3 + random.nextInt(2) : 2 + random.nextInt(2);
            int branchY = treeHeight - 3 - random.nextInt(thick ? 4 : 3);
            branchY = Math.max(2, branchY);

            int bx = Math.cos(angle) >= 0 ? width - 1 : 0;
            int bz = Math.sin(angle) >= 0 ? width - 1 : 0;
            BlockPos previous = origin.offset(bx, branchY, bz);
            BlockPos tip = previous;
            int placed = 0;
            for (int step = 1; step <= reach; step++) {
                int dx = (int) Math.round(Math.cos(angle) * step);
                int dz = (int) Math.round(Math.sin(angle) * step);
                int rise = step >= 3 ? 1 : 0;
                BlockPos next = previousForBranch(origin, bx + dx,
                        branchY + rise, bz + dz);
                if (next.equals(previous)) continue;
                BlockState there = level.getBlockState(next);
                if (!there.is(ModBlocks.GINKGO_LOG)
                        && !TreeFeature.validTreePos(level, next)) break;
                if (!there.is(ModBlocks.GINKGO_LOG)) {
                    Direction.Axis axis = Math.abs(next.getX() - previous.getX())
                            >= Math.abs(next.getZ() - previous.getZ())
                            ? Direction.Axis.X : Direction.Axis.Z;
                    BlockState log = ModBlocks.GINKGO_LOG.defaultBlockState()
                            .setValue(net.minecraft.world.level.block.RotatedPillarBlock.AXIS, axis);
                    trunkSetter.accept(next, log);
                }
                tip = next;
                previous = next;
                placed++;
            }
            if (placed > 0) {
                attachments.add(new FoliagePlacer.FoliageAttachment(tip, 0, false));
            }
        }
        return attachments;
    }

    private static BlockPos previousForBranch(BlockPos origin, int x, int y, int z) {
        return origin.offset(x, y, z);
    }

    private static boolean placeRoot(WorldGenLevel level,
            BiConsumer<BlockPos, BlockState> setter, BlockPos at) {
        BlockState state = level.getBlockState(at);
        if ((!state.isAir() && !state.canBeReplaced())
                || level.getBlockState(at.below()).isAir()
                || !level.getFluidState(at).isEmpty()
                || !level.getFluidState(at.below()).isEmpty()) return false;
        setter.accept(at, ModBlocks.GINKGO_WOOD.defaultBlockState());
        return true;
    }
}
