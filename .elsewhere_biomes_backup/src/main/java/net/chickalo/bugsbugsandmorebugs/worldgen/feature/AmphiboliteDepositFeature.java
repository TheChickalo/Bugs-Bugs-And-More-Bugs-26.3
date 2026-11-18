package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

public record AmphiboliteDepositFeature() implements Feature {
    public static final MapCodec<AmphiboliteDepositFeature> CODEC =
            MapCodec.unit(new AmphiboliteDepositFeature());

    @Override
    public MapCodec<AmphiboliteDepositFeature> codec() {
        return CODEC;
    }


    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator,
                         RandomSource random, BlockPos origin) {
        int rx = 2 + random.nextInt(4);
        int ry = 2 + random.nextInt(3);
        int rz = 2 + random.nextInt(4);
        boolean placed = false;
        for (int dx = -rx; dx <= rx; dx++) {
            for (int dy = -ry; dy <= ry; dy++) {
                for (int dz = -rz; dz <= rz; dz++) {
                    double distance = (double)dx*dx/(rx*rx)
                            + (double)dy*dy/(ry*ry)
                            + (double)dz*dz/(rz*rz);
                    if (distance > 1.05 - random.nextDouble()*0.15) continue;
                    BlockPos at = origin.offset(dx, dy, dz);
                    if (at.getY() < level.getMinY() || at.getY() >= level.getMaxY()) continue;
                    var state = level.getBlockState(at);
                    // Preserve the solid disaster layer and existing caves.
                    if (!state.is(ModBlocks.SCHIST) && !state.is(ModBlocks.ECLOGITE)) continue;
                    placed |= level.setBlock(at, ModBlocks.AMPHIBOLITE.defaultBlockState(), 2);
                }
            }
        }
        return placed;
    }
}
