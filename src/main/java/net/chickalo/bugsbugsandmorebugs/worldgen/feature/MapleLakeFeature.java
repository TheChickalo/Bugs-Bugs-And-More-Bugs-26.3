package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Sealed, irregular ponds. The larger Lake biome basins are formed by terrain
 * density itself; these are smaller pockets on adjacent dry ground.
 *
 * Crucially, there is no 32x32 all-or-nothing test. Several shapes are tried
 * inside the current chunk and the waterline is placed BELOW the natural rim.
 */
public record MapleLakeFeature() implements Feature {
    public static final MapCodec<MapleLakeFeature> CODEC = MapCodec.unit(new MapleLakeFeature());
    private static final BlockState WATER = Blocks.WATER.defaultBlockState();
    private static final int SIDE = 16;
    @Override public MapCodec<MapleLakeFeature> codec() { return CODEC; }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        final int chunkX = Math.floorDiv(origin.getX(), SIDE) * SIDE;
        final int chunkZ = Math.floorDiv(origin.getZ(), SIDE) * SIDE;

        for (int candidate = 0; candidate < 4; candidate++) {
            int cx = chunkX + 7 + random.nextInt(3);
            int cz = chunkZ + 7 + random.nextInt(3);
            double radiusX = 3.0 + random.nextDouble() * 1.5;
            double radiusZ = 3.0 + random.nextDouble() * 1.5;
            double phase = random.nextDouble() * Math.PI * 2.0;
            int[][] ground = new int[SIDE][SIDE];
            boolean[][] wet = new boolean[SIDE][SIDE];
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            int area = 0;
            boolean valid = true;

            for (int ix = 1; ix < SIDE - 1 && valid; ix++) {
                for (int iz = 1; iz < SIDE - 1; iz++) {
                    int x = chunkX + ix, z = chunkZ + iz;
                    double xx = (x - cx) / radiusX;
                    double zz = (z - cz) / radiusZ;
                    double angle = Math.atan2(zz, xx);
                    double outline = 1.0 + 0.07 * Math.sin(4.0 * angle + phase);
                    if (xx * xx + zz * zz > outline * outline) continue;

                    int y = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z) - 1;
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    // Don't try to carve another pond inside an existing lake.
                    if (y < 60 || !level.getFluidState(pos.above()).isEmpty()
                            || !naturalSoil(state) || y >= level.getMaxY() - 8) {
                        valid = false;
                        break;
                    }
                    wet[ix][iz] = true;
                    ground[ix][iz] = y;
                    min = Math.min(min, y);
                    max = Math.max(max, y);
                    area++;
                }
            }
            if (!valid || area < 24 || max - min > 6
                    || min <= level.getMinY() + 6) continue;

            // A closed rim is essential: never construct a pond on a slope
            // where a neighbor outside the footprint sits below its waterline.
            final int surface = min;
            for (int ix = 1; ix < SIDE - 1 && valid; ix++) {
                for (int iz = 1; iz < SIDE - 1 && valid; iz++) {
                    if (!wet[ix][iz]) continue;
                    for (int ox = -1; ox <= 1 && valid; ox++) {
                        for (int oz = -1; oz <= 1; oz++) {
                            if (Math.abs(ox) + Math.abs(oz) != 1) continue;
                            int nx = ix + ox, nz = iz + oz;
                            if (nx < 0 || nx >= SIDE || nz < 0 || nz >= SIDE) {
                                valid = false;
                                break;
                            }
                            if (!wet[nx][nz]
                                    && level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG,
                                    chunkX + nx, chunkZ + nz) - 1 < surface) {
                                valid = false;
                                break;
                            }
                        }
                    }
                }
            }
            if (!valid) continue;

            for (int ix = 1; ix < SIDE - 1; ix++) {
                for (int iz = 1; iz < SIDE - 1; iz++) {
                    if (!wet[ix][iz]) continue;
                    int x = chunkX + ix, z = chunkZ + iz;
                    double d = Math.sqrt(Math.pow((x - cx) / radiusX, 2)
                            + Math.pow((z - cz) / radiusZ, 2));
                    int depth = 1 + (int) Math.floor(2.0 * Math.max(0, 1.0 - d));
                    int floor = surface - depth;
                    for (int y = floor + 1; y <= ground[ix][iz] + 1; y++) {
                        level.setBlock(new BlockPos(x, y, z),
                                y <= surface ? WATER : Blocks.AIR.defaultBlockState(), 2);
                    }
                    BlockState bed = random.nextInt(5) == 0 ? Blocks.CLAY.defaultBlockState()
                            : random.nextInt(5) == 0 ? Blocks.GRAVEL.defaultBlockState()
                            : Blocks.MUD.defaultBlockState();
                    for (int y = floor - 2; y <= floor; y++)
                        level.setBlock(new BlockPos(x, y, z), bed, 2);
                }
            }
            return true;
        }
        return false;
    }

    private static boolean naturalSoil(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT)
                || state.is(Blocks.PODZOL) || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.MUD) || state.is(Blocks.CLAY)
                || state.is(Blocks.GRAVEL) || state.is(Blocks.SAND)
                || state.is(Blocks.STONE)
                || state.is(ModBlocks.LOAM) || state.is(ModBlocks.GRASSY_LOAM)
                || state.is(ModBlocks.RHYOLITE) || state.is(ModBlocks.MOSSY_RHYOLITE)
                || state.is(ModBlocks.LIMESTONE) || state.is(ModBlocks.WEATHERED_LIMESTONE);
    }
}
