package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Handles the separate vanilla minecraft:river biome when its channel runs
 * near a Maple Forest. This is a bed-only feature: it never changes water,
 * land banks, boulders, vegetation, clay, gravel or stone.
 */
public record MapleRiverbedMudFeature() implements Feature {

    public static final MapCodec<MapleRiverbedMudFeature> CODEC =
            MapCodec.unit(new MapleRiverbedMudFeature());

    private static final int CHUNK_SIZE = 16;
    private static final int MAX_WATER_DEPTH = 12;
    private static final int MAX_SEARCH = 48;

    @Override
    public MapCodec<MapleRiverbedMudFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator,
                         RandomSource random, BlockPos origin) {

        // Snap to the 16x16 chunk containing the chosen placement origin.
        int minX = origin.getX() & ~15;
        int minZ = origin.getZ() & ~15;

        // We deliberately do not transform unrelated vanilla rivers.
        // Sample the biome in and immediately around this chunk, without
        // roaming across arbitrary distances during world generation.
        if (!isNearMaple(level, minX, minZ)) {
            return false;
        }

        int changed = 0;
        for (int dx = 0; dx < CHUNK_SIZE; dx++) {
            for (int dz = 0; dz < CHUNK_SIZE; dz++) {
                int x = minX + dx;
                int z = minZ + dz;
                BlockPos topWater = findSurfaceWater(level, x, z);
                if (topWater == null || !level.getBiome(topWater).is(Biomes.RIVER)) {
                    continue;
                }

                BlockPos bed = findBed(level, topWater);
                if (bed != null && isDirt(level.getBlockState(bed))) {
                    if (level.setBlock(bed, Blocks.MUD.defaultBlockState(), 2)) {
                        changed++;
                    }
                }
            }
        }
        return changed > 0;
    }

    private static boolean isNearMaple(WorldGenLevel level, int x0, int z0) {
        // Sample a 5x5 grid. Maximum search spans only this chunk and
        // neighboring chunks; 8-block spacing accommodates biome borders.
        for (int dx = -8; dx <= 24; dx += 8) {
            for (int dz = -8; dz <= 24; dz += 8) {
                int x = x0 + dx;
                int z = z0 + dz;
                int y = Math.min(level.getMaxY() - 1,
                        level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1);
                if (y > level.getMinY()
                        && level.getBiome(new BlockPos(x, y, z))
                        .is(ModBiomes.MAPLE_FOREST)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static BlockPos findSurfaceWater(WorldGenLevel level, int x, int z) {
        int top = Math.min(level.getMaxY() - 1,
                level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z));
        int bottom = Math.max(level.getMinY() + 1, top - MAX_SEARCH);
        for (int y = top; y >= bottom; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(pos);
            if (state.is(Blocks.WATER)) {
                return level.getFluidState(pos).isSource() ? pos : null;
            }
            if (state.isAir() || state.canBeReplaced()
                    || state.is(BlockTags.LEAVES)
                    || state.is(BlockTags.LOGS)) {
                continue;
            }
            return null; // First solid ground: don't dig into caves.
        }
        return null;
    }

    private static BlockPos findBed(WorldGenLevel level, BlockPos topWater) {
        BlockPos pos = topWater;
        int depth = 0;
        while (depth <= MAX_WATER_DEPTH && level.getBlockState(pos).is(Blocks.WATER)) {
            if (!level.getFluidState(pos).isSource()) {
                return null;
            }
            pos = pos.below();
            depth++;
        }
        if (depth == 0 || depth > MAX_WATER_DEPTH || pos.getY() <= level.getMinY()) {
            return null;
        }
        return pos;
    }

    private static boolean isDirt(BlockState state) {
        return net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(state, Blocks.DIRT)
                || net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(state, Blocks.GRASS_BLOCK)
                || (state.is(Blocks.COARSE_DIRT) || state.is(ModBlocks.COARSE_LOAM))
                || state.is(Blocks.PODZOL)
                || (state.is(Blocks.ROOTED_DIRT) || state.is(ModBlocks.ROOTED_LOAM));
    }
}
