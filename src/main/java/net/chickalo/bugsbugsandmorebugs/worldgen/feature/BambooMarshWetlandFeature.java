package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.block.custom.ThreeBlockWetlandPlantBlock;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Damp, uneven mud flats punctuated by tiny contained pools, floating pads,
 * and water-rooted reeds. Designed only for low-elevation Bamboo Marsh.
 *
 * Water is placed ONLY after checking an entire natural-soil rim at that
 * waterline and a solid floor beneath every pond cell. This prevents cliff
 * waterfalls, cave leaks, and unsupported water columns.
 */
public record BambooMarshWetlandFeature() implements Feature {
    public static final MapCodec<BambooMarshWetlandFeature> CODEC =
            MapCodec.unit(new BambooMarshWetlandFeature());

    @Override public MapCodec<BambooMarshWetlandFeature> codec() { return CODEC; }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator,
                         RandomSource random, BlockPos origin) {
        int changed = 0;
        int cx = origin.getX(), cz = origin.getZ();

        // Patchy mud rather than a perfectly flat or uniformly brown biome.
        for (int i = 0; i < 27; i++) {
            int x = cx + random.nextInt(15) - 7;
            int z = cz + random.nextInt(15) - 7;
            int y = surfaceY(level, x, z);
            if (y < 58 || y > 103) continue;
            BlockPos ground = new BlockPos(x, y, z);
            if (!isMarsh(level, ground) || !isNaturalSoil(level.getBlockState(ground))
                    || !level.getFluidState(ground).isEmpty()
                    || !level.getBlockState(ground.above()).isAir()
                    || level.getBlockState(ground.below()).isAir()) continue;

            float roll = random.nextFloat();
            BlockState replacement = roll < 0.62F ? Blocks.MUD.defaultBlockState()
                    : roll < 0.70F ? Blocks.ROOTED_DIRT.defaultBlockState()
                    : roll < 0.78F ? Blocks.COARSE_DIRT.defaultBlockState() : null;
            if (replacement != null && level.setBlock(ground, replacement, 2)) changed++;
        }

        // Favor several small hollows, not one overlarge artificial lake.
        for (int attempt = 0; attempt < 4; attempt++) {
            int x = cx + random.nextInt(11) - 5;
            int z = cz + random.nextInt(11) - 5;
            int radius = random.nextFloat() < 0.68F ? 1 : 2;
            if (!safePoolSite(level, x, z, radius)) continue;
            int y = surfaceY(level, x, z);
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz > radius * radius + (radius == 1 ? 1 : 0)) continue;
                    BlockPos at = new BlockPos(x + dx, y, z + dz);
                    BlockPos bed = at.below();
                    if (level.setBlock(bed, random.nextFloat() < 0.84F
                            ? Blocks.MUD.defaultBlockState()
                            : Blocks.CLAY.defaultBlockState(), 2)) changed++;
                    if (level.setBlock(at, Blocks.WATER.defaultBlockState(), 2)) changed++;

                    // A few reeds rise directly out of the water; pads float
                    // above it, never replacing the source water itself.
                    if (random.nextInt(7) == 0) {
                        Block reed = random.nextBoolean() ? ModBlocks.CATTAILS : ModBlocks.REEDS;
                        if (reed instanceof ThreeBlockWetlandPlantBlock plant
                                && plant.placeNaturally(level, at)) changed++;
                    } else if (random.nextInt(5) == 0) {
                        Block pad = random.nextBoolean() ? ModBlocks.DUCKWEED : ModBlocks.LOTUS;
                        BlockState state = pad.defaultBlockState();
                        if (level.getBlockState(at.above()).isAir()
                                && state.canSurvive(level, at.above())
                                && level.setBlock(at.above(), state, 2)) changed++;
                    }
                }
            }
        }
        return changed > 0;
    }

    private static boolean safePoolSite(WorldGenLevel level, int x, int z, int radius) {
        int y = surfaceY(level, x, z);
        if (y < 58 || y > 102) return false;
        int limit = radius + 1;
        for (int dx = -limit; dx <= limit; dx++) {
            for (int dz = -limit; dz <= limit; dz++) {
                int square = dx * dx + dz * dz;
                if (square > limit * limit) continue;
                int bx = x + dx, bz = z + dz;
                int actualY = surfaceY(level, bx, bz);
                BlockPos site = new BlockPos(bx, actualY, bz);
                if (!isMarsh(level, site) || !isNaturalSoil(level.getBlockState(site))
                        || actualY < y || actualY > y + 1
                        || !level.getFluidState(site).isEmpty()
                        || level.getBlockState(site.below()).isAir()
                        || !level.getFluidState(site.below()).isEmpty()) return false;

                // Every submerged cell has an unobstructed air block above it.
                // Rim cells must already contain solid banks, not open caves.
                if (square <= radius * radius + (radius == 1 ? 1 : 0)) {
                    if (actualY != y || !level.getBlockState(site.above()).isAir()) return false;
                } else if (level.getBlockState(new BlockPos(bx, y, bz)).isAir()) return false;
            }
        }
        return true;
    }

    private static boolean isMarsh(WorldGenLevel level, BlockPos pos) {
        return level.getBiome(pos).is(ModBiomes.BAMBOO_MARSH);
    }

    private static int surfaceY(WorldGenLevel level, int x, int z) {
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
    }

    private static boolean isNaturalSoil(BlockState state) {
        return state.is(Blocks.MUD) || state.is(Blocks.DIRT)
                || state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.ROOTED_DIRT)
                || state.is(Blocks.COARSE_DIRT) || state.is(Blocks.PODZOL)
                || state.is(Blocks.CLAY) || state.is(ModBlocks.GRASSY_LOAM)
                || state.is(ModBlocks.LOAM);
    }
}
