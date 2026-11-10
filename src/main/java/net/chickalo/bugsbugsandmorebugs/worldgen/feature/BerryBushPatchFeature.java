package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Generates a small, irregular cluster of a specified berry bush.
 * The feature checks survival at each candidate position, so bushes only
 * appear on suitable ground and do not replace existing vegetation.
 */
public record BerryBushPatchFeature(Block bush) implements Feature {
    public static final MapCodec<BerryBushPatchFeature> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BuiltInRegistries.BLOCK.byNameCodec()
                            .fieldOf("bush")
                            .forGetter(BerryBushPatchFeature::bush)
            ).apply(instance, BerryBushPatchFeature::new));

    @Override
    public MapCodec<BerryBushPatchFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator,
                         RandomSource random, BlockPos origin) {
        int attempts = 7 + random.nextInt(5);
        int radius = 2 + random.nextInt(3);
        int placed = 0;

        for (int i = 0; i < attempts; i++) {
            int dx = random.nextInt(radius * 2 + 1) - radius;
            int dz = random.nextInt(radius * 2 + 1) - radius;
            BlockPos pos = origin.offset(dx, 0, dz);

            // A small amount of vertical adjustment accommodates uneven ground.
            if (!level.isEmptyBlock(pos)) {
                pos = pos.above();
            }
            if (!level.isEmptyBlock(pos)) continue;

            BlockState state = bush.defaultBlockState()
                    .setValue(SweetBerryBushBlock.AGE, random.nextInt(3));
            if (!state.canSurvive(level, pos)) continue;

            level.setBlock(pos, state, 2);
            placed++;
        }

        return placed > 0;
    }
}
