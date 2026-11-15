
package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

public record GlowshroomPatchFeature(Block mushroom)
        implements Feature {

    public static final MapCodec<GlowshroomPatchFeature> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BuiltInRegistries.BLOCK.byNameCodec()
                            .fieldOf("mushroom")
                            .forGetter(GlowshroomPatchFeature::mushroom)
            ).apply(instance, GlowshroomPatchFeature::new));

    @Override
    public MapCodec<GlowshroomPatchFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level,
                         ChunkGenerator generator,
                         RandomSource random,
                         BlockPos origin) {

        // Choose the ring's size.
        double radius = 2.0 + random.nextDouble() * 2.5;

        // Number of possible mushroom positions.
        int attempts = 18 + random.nextInt(9);

        // Randomly rotate each ring.
        double rotation = random.nextDouble() * Math.PI * 2;

        // Leave one randomly positioned opening.
        double gapCenter = random.nextDouble() * Math.PI * 2;
        double gapWidth = 0.25 + random.nextDouble() * 0.3;

        int centerY = level.getHeight(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                origin.getX(), origin.getZ()
        );

        BlockState mushroomState = mushroom.defaultBlockState();
        int placed = 0;

        for (int i = 0; i < attempts; i++) {

            // Evenly spaced angles with some randomness.
            double angle = rotation
                    + (2 * Math.PI * (i
                    + (random.nextDouble() - 0.5) * 0.8)
                    / attempts);

            // Measure angular distance from the missing section.
            double distanceFromGap = Math.atan2(
                    Math.sin(angle - gapCenter),
                    Math.cos(angle - gapCenter)
            );

            if (Math.abs(distanceFromGap) < gapWidth) {
                continue;
            }

            // Additional random gaps.
            if (random.nextFloat() > 0.77f) {
                continue;
            }

            // Give each mushroom a slightly different radius.
            double distance = radius
                    + (random.nextDouble() - 0.5) * 1.6;

            int dx = (int) Math.round(Math.cos(angle) * distance);
            int dz = (int) Math.round(Math.sin(angle) * distance);

            int x = origin.getX() + dx;
            int z = origin.getZ() + dz;

            // Find the ground beneath each candidate.
            int y = level.getHeight(
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    x, z
            );

            // Avoid rings climbing steep terrain.
            if (Math.abs(y - centerY) > 3) {
                continue;
            }

            BlockPos pos = new BlockPos(x, y, z);

            if (!level.isEmptyBlock(pos)) {
                continue;
            }

            BlockState ground = level.getBlockState(pos.below());

            // Grow on natural forest-floor blocks.
            if (!ground.is(BlockTags.DIRT)
                    && !ground.is(Blocks.MOSS_BLOCK)) {
                continue;
            }

            // Respect the mushroom's normal survival rules.
            if (!mushroomState.canSurvive(level, pos)) {
                continue;
            }

            level.setBlock(pos, mushroomState, 2);
            placed++;
        }

        return placed > 0;
    }
}
