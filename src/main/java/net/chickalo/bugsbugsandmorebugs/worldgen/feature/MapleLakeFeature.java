package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * A broad, irregular lake with a multi-block rocky shoreline.
 *
 * The water basin uses overlapping ellipsoids. The shoreline is then
 * expanded horizontally, with a fresh material choice for every block.
 */
public record MapleLakeFeature() implements Feature {
    public static final MapCodec<MapleLakeFeature> CODEC =
            MapCodec.unit(new MapleLakeFeature());

    private static final int SIZE = 24;
    private static final int HEIGHT = 8;
    private static final int SHORE_WIDTH = 3;

    private static final BlockState WATER = Blocks.WATER.defaultBlockState();

    @Override
    public MapCodec<MapleLakeFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator,
                         RandomSource random, BlockPos origin) {
        if (origin.getY() <= level.getMinY() + 5) {
            return false;
        }

        BlockPos start = origin.offset(-SIZE / 2, -4, -SIZE / 2);
        boolean[] basin = new boolean[SIZE * SIZE * HEIGHT];

        int lobes = 4 + random.nextInt(4);
        for (int i = 0; i < lobes; i++) {
            double rx = 3.0 + random.nextDouble() * 6.0;
            double ry = 2.0 + random.nextDouble() * 4.0;
            double rz = 3.0 + random.nextDouble() * 6.0;
            double cx = 2.0 + rx / 2.0 + random.nextDouble() * (SIZE - rx - 4.0);
            double cy = 2.0 + ry / 2.0 + random.nextDouble() * (HEIGHT - ry - 2.0);
            double cz = 2.0 + rz / 2.0 + random.nextDouble() * (SIZE - rz - 4.0);

            for (int x = 1; x < SIZE - 1; x++) {
                for (int z = 1; z < SIZE - 1; z++) {
                    for (int y = 1; y < HEIGHT - 1; y++) {
                        double dx = (x - cx) / (rx / 2.0);
                        double dy = (y - cy) / (ry / 2.0);
                        double dz = (z - cz) / (rz / 2.0);
                        if (dx * dx + dy * dy + dz * dz < 1.0) {
                            basin[index(x, z, y)] = true;
                        }
                    }
                }
            }
        }

        boolean hasWater = false;
        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {
                for (int y = 0; y < HEIGHT; y++) {
                    if (!basin[index(x, z, y)]) continue;

                    BlockPos pos = start.offset(x, y, z);
                    BlockState existing = level.getBlockState(pos);

                    // Reject lakes that cut into existing bodies of water or caves.
                    if (y >= 4 && existing.liquid()) return false;
                    if (y < 4 && !existing.isSolid() && !existing.is(WATER.getBlock())) return false;
                }
            }
        }

        // Fill the basin. The upper half is air, as in vanilla's lake feature.
        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {
                for (int y = 0; y < HEIGHT; y++) {
                    if (!basin[index(x, z, y)]) continue;
                    BlockPos pos = start.offset(x, y, z);
                    if (y < 4) {
                        level.setBlock(pos, WATER, 2);
                        hasWater = true;
                    } else {
                        level.setBlock(pos, Blocks.CAVE_AIR.defaultBlockState(), 2);
                        markAboveForPostProcessing(level, pos);
                    }
                }
            }
        }

        if (!hasWater) return false;

        // Expand the shoreline outward by up to three blocks horizontally.
        // Every individual block gets its own random material selection.
        for (int x = 1; x < SIZE - 1; x++) {
            for (int z = 1; z < SIZE - 1; z++) {
                for (int y = 0; y < 4; y++) {
                    if (basin[index(x, z, y)]) continue;

                    int distance = horizontalDistanceToBasin(basin, x, z, y);
                    if (distance < 1 || distance > SHORE_WIDTH) continue;

                    BlockPos pos = start.offset(x, y, z);
                    BlockState existing = level.getBlockState(pos);

                    if (existing.is(Blocks.GRASS_BLOCK)
                            || existing.is(Blocks.DIRT)
                            || existing.is(Blocks.COARSE_DIRT)
                            || existing.is(Blocks.ROOTED_DIRT)
                            || existing.is(Blocks.MUD)
                            || existing.is(Blocks.STONE)
                            || existing.is(Blocks.ANDESITE)
                            || existing.is(Blocks.GRAVEL)) {
                        level.setBlock(pos, randomShoreBlock(random), 2);
                        markAboveForPostProcessing(level, pos);
                    }
                }
            }
        }

        return true;
    }

    private static int index(int x, int z, int y) {
        return (x * SIZE + z) * HEIGHT + y;
    }

    private static int horizontalDistanceToBasin(boolean[] basin, int x, int z, int y) {
        for (int distance = 1; distance <= SHORE_WIDTH; distance++) {
            for (int dx = -distance; dx <= distance; dx++) {
                for (int dz = -distance; dz <= distance; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != distance) continue;
                    int nx = x + dx;
                    int nz = z + dz;
                    if (nx >= 0 && nx < SIZE && nz >= 0 && nz < SIZE
                            && basin[index(nx, nz, y)]) {
                        return distance;
                    }
                }
            }
        }
        return -1;
    }

    private static BlockState randomShoreBlock(RandomSource random) {
        return switch (random.nextInt(3)) {
            case 0 -> Blocks.GRAVEL.defaultBlockState();
            case 1 -> Blocks.ANDESITE.defaultBlockState();
            default -> Blocks.STONE.defaultBlockState();
        };
    }
}
