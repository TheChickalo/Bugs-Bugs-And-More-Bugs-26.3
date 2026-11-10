package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Vanilla-style bamboo generation that never overwrites an occupied block
 * when adding the decorative top.
 */
public record ProtectedBambooFeature(float probability) implements Feature {
    private static final BlockState BAMBOO_TRUNK = Blocks.BAMBOO.defaultBlockState()
            .setValue(BambooStalkBlock.AGE, 1)
            .setValue(BambooStalkBlock.LEAVES, BambooLeaves.NONE)
            .setValue(BambooStalkBlock.STAGE, 0);

    private static final BlockState BAMBOO_FINAL_LARGE = BAMBOO_TRUNK
            .setValue(BambooStalkBlock.LEAVES, BambooLeaves.LARGE)
            .setValue(BambooStalkBlock.STAGE, 1);
    private static final BlockState BAMBOO_TOP_LARGE =
            BAMBOO_TRUNK.setValue(BambooStalkBlock.LEAVES, BambooLeaves.LARGE);
    private static final BlockState BAMBOO_TOP_SMALL =
            BAMBOO_TRUNK.setValue(BambooStalkBlock.LEAVES, BambooLeaves.SMALL);

    public static final MapCodec<ProtectedBambooFeature> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.floatRange(0.0F, 1.0F)
                            .fieldOf("probability")
                            .forGetter(ProtectedBambooFeature::probability)
            ).apply(instance, ProtectedBambooFeature::new));

    @Override
    public MapCodec<ProtectedBambooFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator,
                         RandomSource random, BlockPos origin) {
        BlockPos.MutableBlockPos bambooPos = origin.mutable();
        BlockPos.MutableBlockPos podzolPos = origin.mutable();

        if (!level.isEmptyBlock(bambooPos)
                || !Blocks.BAMBOO.defaultBlockState().canSurvive(level, bambooPos)) {
            return false;
        }

        int height = random.nextInt(12) + 5;

        if (random.nextFloat() < probability) {
            int radius = random.nextInt(4) + 1;
            for (int x = origin.getX() - radius; x <= origin.getX() + radius; x++) {
                for (int z = origin.getZ() - radius; z <= origin.getZ() + radius; z++) {
                    int dx = x - origin.getX();
                    int dz = z - origin.getZ();
                    if (dx * dx + dz * dz > radius * radius) continue;

                    podzolPos.set(x,
                            level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1,
                            z);
                    if (level.getBlockState(podzolPos)
                            .is(BlockTags.BENEATH_BAMBOO_PODZOL_REPLACEABLE)) {
                        level.setBlock(podzolPos, Blocks.PODZOL.defaultBlockState(), 2);
                    }
                }
            }
        }

        int placedHeight = 0;
        for (int i = 0; i < height && level.isEmptyBlock(bambooPos); i++) {
            if (!BAMBOO_TRUNK.canSurvive(level, bambooPos)) break;
            level.setBlock(bambooPos, BAMBOO_TRUNK, 2);
            placedHeight++;
            bambooPos.move(Direction.UP);
        }

        // Vanilla writes these three blocks even when bamboo stopped at a log.
        // Only add the leafy top when all three target positions are empty.
        if (placedHeight >= 3
                && level.isEmptyBlock(bambooPos)
                && level.isEmptyBlock(bambooPos.above())
                && level.isEmptyBlock(bambooPos.above(2))) {
            level.setBlock(bambooPos, BAMBOO_FINAL_LARGE, 2);
            level.setBlock(bambooPos.above(), BAMBOO_TOP_LARGE, 2);
            level.setBlock(bambooPos.above(2), BAMBOO_TOP_SMALL, 2);
        } else if (placedHeight >= 3) {
            BlockPos top = origin.above(placedHeight - 1);
            if (level.getBlockState(top).is(Blocks.BAMBOO)) {
                BlockState state = level.getBlockState(top);
                level.setBlock(top, state.setValue(BambooStalkBlock.LEAVES, BambooLeaves.LARGE)
                        .setValue(BambooStalkBlock.STAGE, 1), 2);
            }
        }

        return placedHeight > 0;
    }
}
