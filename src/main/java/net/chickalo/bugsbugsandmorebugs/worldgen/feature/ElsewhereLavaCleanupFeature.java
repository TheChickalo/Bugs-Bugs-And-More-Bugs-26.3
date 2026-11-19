package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Clear the hardcoded deep lava below Y=-54 independently of the aquifer
 * lava noise function. Preserve negative-Y caves and the Underhang void.
 * Palette probing skips empty or lava-free chunk sections cheaply.
 */
public record ElsewhereLavaCleanupFeature() implements Feature {
    public static final MapCodec<ElsewhereLavaCleanupFeature> CODEC =
            MapCodec.unit(new ElsewhereLavaCleanupFeature());

    @Override public MapCodec<ElsewhereLavaCleanupFeature> codec() { return CODEC; }

    @Override public boolean place(WorldGenLevel level, ChunkGenerator generator,
                                   RandomSource random, BlockPos origin) {
        int chunkX = Math.floorDiv(origin.getX(), 16);
        int chunkZ = Math.floorDiv(origin.getZ(), 16);
        var chunk = level.getChunk(chunkX, chunkZ);
        var sections = chunk.getSections();
        int low = level.getMinY();
        int high = Math.min(-54, level.getMaxY() - 1);
        int cleared = 0;
        for (int i = 0; i < sections.length; i++) {
            int from = low + i * 16;
            if (from > high) break;
            if (from + 15 < low || !sections[i].maybeHas(state -> state.is(Blocks.LAVA))) continue;
            int end = Math.min(high, from + 15);
            for (int y = Math.max(low, from); y <= end; y++) {
                for (int x = chunkX * 16; x < chunkX * 16 + 16; x++) {
                    for (int z = chunkZ * 16; z < chunkZ * 16 + 16; z++) {
                        BlockPos at = new BlockPos(x, y, z);
                        if (chunk.getBlockState(at).is(Blocks.LAVA)
                                && level.setBlock(at, Blocks.AIR.defaultBlockState(), 2)) cleared++;
                    }
                }
            }
        }
        return cleared > 0;
    }
}
