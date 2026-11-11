
package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

public record MapleLakeFeature() implements Feature {

    public static final MapCodec<MapleLakeFeature> CODEC =
            MapCodec.unit(new MapleLakeFeature());

    private static final int SIZE = 32;

    private static final int MIN_WIDTH = 5;
    private static final int MAX_WIDTH = 20;

    private static final int MIN_DEPTH = 1;
    private static final int MAX_DEPTH = 3;

    private static final int MAX_HEIGHT_VARIATION = 2;

    private static final int BASIN_THICKNESS = 3;
    private static final int WALL_THICKNESS = 3;

    // Erosion only affects exposed outer wall sections.
    private static final float EDGE_EROSION_CHANCE = 0.65F;
    private static final int MAX_EROSION_DEPTH = 2;

    private static final BlockState WATER =
            Blocks.WATER.defaultBlockState();

    @Override
    public MapCodec<MapleLakeFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level,
                         ChunkGenerator generator,
                         RandomSource random,
                         BlockPos origin) {

        int baseX = origin.getX() - SIZE / 2;
        int baseZ = origin.getZ() - SIZE / 2;

        boolean[] basin = new boolean[SIZE * SIZE];
        int[] wallDistance = new int[SIZE * SIZE];

        int[] groundHeights = new int[SIZE * SIZE];
        int[] surfaceHeights = new int[SIZE * SIZE];

        int[] wallTops = new int[SIZE * SIZE];

        double centerX = SIZE / 2.0;
        double centerZ = SIZE / 2.0;

        int width = MIN_WIDTH
                + random.nextInt(MAX_WIDTH - MIN_WIDTH + 1);

        int length = MIN_WIDTH
                + random.nextInt(MAX_WIDTH - MIN_WIDTH + 1);

        double radiusX = width / 2.0;
        double radiusZ = length / 2.0;

        double phase1 = random.nextDouble() * Math.PI * 2.0;
        double phase2 = random.nextDouble() * Math.PI * 2.0;

        // ------------------------------------------------------------
        // 1. Generate an irregular elliptical basin.
        // ------------------------------------------------------------

        for (int x = 1; x < SIZE - 1; x++) {
            for (int z = 1; z < SIZE - 1; z++) {

                double dx = (x - centerX) / radiusX;
                double dz = (z - centerZ) / radiusZ;

                double distance = Math.sqrt(dx * dx + dz * dz);
                double angle = Math.atan2(dz, dx);

                double outline =
                        1.0
                                + Math.sin(angle * 3.0 + phase1) * 0.035
                                + Math.sin(angle * 5.0 + phase2) * 0.025;

                if (distance <= outline) {
                    basin[index(x, z)] = true;
                }
            }
        }

        // ------------------------------------------------------------
        // 2. Sample terrain and validate the basin.
        // ------------------------------------------------------------

        int lowestHeight = Integer.MAX_VALUE;
        int highestHeight = Integer.MIN_VALUE;

        int heightTotal = 0;
        int basinColumns = 0;

        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {

                int worldX = baseX + x;
                int worldZ = baseZ + z;

                int groundY = level.getHeight(
                        Heightmap.Types.OCEAN_FLOOR_WG,
                        worldX,
                        worldZ
                ) - 1;

                int surfaceY = level.getHeight(
                        Heightmap.Types.WORLD_SURFACE_WG,
                        worldX,
                        worldZ
                ) - 1;

                groundHeights[index(x, z)] = groundY;
                surfaceHeights[index(x, z)] = surfaceY;

                if (!basin[index(x, z)]) {
                    continue;
                }

                lowestHeight = Math.min(lowestHeight, groundY);
                highestHeight = Math.max(highestHeight, groundY);

                heightTotal += groundY;
                basinColumns++;

                if (surfaceY - groundY > 5) {
                    return false;
                }
            }
        }

        if (basinColumns == 0
                || highestHeight - lowestHeight > MAX_HEIGHT_VARIATION) {
            return false;
        }

        int waterLevel = Math.round(
                (float) heightTotal / basinColumns
        );

        if (waterLevel < level.getMinY() + MAX_DEPTH + 3
                || waterLevel >= level.getMaxY() - 2) {
            return false;
        }

        // ------------------------------------------------------------
        // 3. Calculate the distance of every wall column from the basin.
        //
        // 1 = inner wall
        // 2 = middle wall
        // 3 = outer wall
        // ------------------------------------------------------------

        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {

                if (basin[index(x, z)]) {
                    continue;
                }

                int nearestDistance = WALL_THICKNESS + 1;

                for (int dx = -WALL_THICKNESS;
                     dx <= WALL_THICKNESS;
                     dx++) {

                    for (int dz = -WALL_THICKNESS;
                         dz <= WALL_THICKNESS;
                         dz++) {

                        int distance = Math.abs(dx) + Math.abs(dz);

                        if (distance == 0
                                || distance > WALL_THICKNESS) {
                            continue;
                        }

                        int nx = x + dx;
                        int nz = z + dz;

                        if (nx < 0 || nx >= SIZE
                                || nz < 0 || nz >= SIZE) {
                            continue;
                        }

                        if (basin[index(nx, nz)]) {
                            nearestDistance = Math.min(
                                    nearestDistance,
                                    distance
                            );
                        }
                    }
                }

                if (nearestDistance <= WALL_THICKNESS) {
                    wallDistance[index(x, z)] = nearestDistance;
                }
            }
        }

        int wallBottom = waterLevel - MAX_DEPTH - 2;

        // ------------------------------------------------------------
        // 4. Prevalidate all retaining wall columns.
        // ------------------------------------------------------------

        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {

                if (wallDistance[index(x, z)] == 0) {
                    continue;
                }

                int groundY = groundHeights[index(x, z)];
                int surfaceY = surfaceHeights[index(x, z)];

                if (surfaceY - groundY > 5
                        || groundY < level.getMinY()
                        || surfaceY >= level.getMaxY()) {
                    return false;
                }
            }
        }

// ------------------------------------------------------------
// 5. Build the wall height map.
//
// The inner wall is always intact.
// The middle wall is always intact.
// Only the outermost wall can erode, and only when exposed
// to air on one of its horizontal sides.
// ------------------------------------------------------------

        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {

                int distance = wallDistance[index(x, z)];

                if (distance == 0) {
                    continue;
                }

                int wallTop = waterLevel;

                // Only erode the outermost layer.
                if (distance == WALL_THICKNESS
                        && hasExposedAirSide(
                        level,
                        baseX,
                        baseZ,
                        x,
                        z,
                        waterLevel,
                        wallDistance,
                        basin
                )
                        && random.nextFloat() < EDGE_EROSION_CHANCE) {

                    int erosionDepth = 1
                            + random.nextInt(MAX_EROSION_DEPTH);

                    wallTop -= erosionDepth;
                }

                wallTops[index(x, z)] = wallTop;
            }
        }

        // ------------------------------------------------------------
        // 6. Construct the retaining walls.
        // ------------------------------------------------------------

        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {

                if (wallDistance[index(x, z)] == 0) {
                    continue;
                }

                int worldX = baseX + x;
                int worldZ = baseZ + z;

                int wallTop = wallTops[index(x, z)];

                for (int y = wallBottom; y <= wallTop; y++) {

                    level.setBlock(
                            new BlockPos(worldX, y, worldZ),
                            randomLakebedBlock(random),
                            2
                    );
                }
            }
        }

        // ------------------------------------------------------------
        // 7. Carve the lake basin and create the lakebed.
        // ------------------------------------------------------------

        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {

                if (!basin[index(x, z)]) {
                    continue;
                }

                int worldX = baseX + x;
                int worldZ = baseZ + z;

                int groundY = groundHeights[index(x, z)];
                int surfaceY = surfaceHeights[index(x, z)];

                double dx = (x - centerX) / radiusX;
                double dz = (z - centerZ) / radiusZ;

                double distance = Math.sqrt(dx * dx + dz * dz);

                double centerFactor = Math.max(
                        0.0,
                        Math.min(1.0, 1.0 - distance)
                );

                int depth = MIN_DEPTH + (int) Math.round(
                        centerFactor * (MAX_DEPTH - MIN_DEPTH)
                );

                int floorY = waterLevel - depth;

                int bottomY = floorY - BASIN_THICKNESS + 1;

                int fillBottom = Math.min(
                        bottomY,
                        groundY + 1
                );

                if (fillBottom < level.getMinY()) {
                    continue;
                }

                // Remove terrain and vegetation from the basin.
                for (int y = fillBottom; y <= surfaceY; y++) {

                    level.setBlock(
                            new BlockPos(worldX, y, worldZ),
                            Blocks.AIR.defaultBlockState(),
                            2
                    );
                }

                // Three-block-thick lakebed.
                for (int y = fillBottom; y <= floorY; y++) {

                    level.setBlock(
                            new BlockPos(worldX, y, worldZ),
                            randomLakebedBlock(random),
                            2
                    );
                }

                // Fill with water.
                for (int y = floorY + 1; y <= waterLevel; y++) {

                    level.setBlock(
                            new BlockPos(worldX, y, worldZ),
                            WATER,
                            2
                    );
                }

                // --------------------------------------------------------
                // 7b. CLEAR VEGETATION ABOVE THE WATER.
                //
                // This intentionally clears the entire vertical column
                // within the basin footprint up through the old terrain
                // surface, plus a small margin.
                //
                // Trees or foliage hanging over the lake are removed
                // instead of being left floating.
                // --------------------------------------------------------

                int cleanupTop = Math.min(
                        level.getMaxY() - 1,
                        Math.max(surfaceY + 8, waterLevel + 8)
                );

                for (int y = waterLevel + 1; y <= cleanupTop; y++) {

                    level.setBlock(
                            new BlockPos(worldX, y, worldZ),
                            Blocks.AIR.defaultBlockState(),
                            2
                    );
                }
            }
        }

        // ------------------------------------------------------------
        // 8. Add submerged seagrass.
        // ------------------------------------------------------------

        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {

                if (!basin[index(x, z)]) {
                    continue;
                }

                int worldX = baseX + x;
                int worldZ = baseZ + z;

                double dx = (x - centerX) / radiusX;
                double dz = (z - centerZ) / radiusZ;

                double distance = Math.sqrt(dx * dx + dz * dz);

                double centerFactor = Math.max(
                        0.0,
                        Math.min(1.0, 1.0 - distance)
                );

                int depth = MIN_DEPTH + (int) Math.round(
                        centerFactor * (MAX_DEPTH - MIN_DEPTH)
                );

                if (depth < 2 || random.nextFloat() >= 0.25F) {
                    continue;
                }

                int floorY = waterLevel - depth;

                BlockPos plantPos = new BlockPos(
                        worldX,
                        floorY + 1,
                        worldZ
                );

                if (level.getBlockState(plantPos).is(Blocks.WATER)
                        && level.getBlockState(plantPos.above()).is(Blocks.WATER)) {

                    level.setBlock(
                            plantPos,
                            Blocks.SEAGRASS.defaultBlockState(),
                            2
                    );
                }
            }
        }

        return true;
    }

    private static boolean hasExposedAirSide(
            WorldGenLevel level,
            int baseX,
            int baseZ,
            int x,
            int z,
            int waterLevel,
            int[] wallDistance,
            boolean[] basin) {

        int[][] directions = {
                { 1, 0 },
                { -1, 0 },
                { 0, 1 },
                { 0, -1 }
        };

        for (int[] direction : directions) {

            int nx = x + direction[0];
            int nz = z + direction[1];

            if (nx < 0 || nx >= SIZE
                    || nz < 0 || nz >= SIZE) {
                continue;
            }

            // Ignore neighboring lake and retaining-wall columns.
            if (basin[index(nx, nz)]
                    || wallDistance[index(nx, nz)] != 0) {
                continue;
            }

            BlockPos neighbor = new BlockPos(
                    baseX + nx,
                    waterLevel,
                    baseZ + nz
            );

            if (level.getBlockState(neighbor).isAir()) {
                return true;
            }
        }

        return false;
    }

    private static int index(int x, int z) {
        return x * SIZE + z;
    }

    private static BlockState randomLakebedBlock(RandomSource random) {
        return switch (random.nextInt(3)) {
            case 0 -> Blocks.GRAVEL.defaultBlockState();
            case 1 -> Blocks.STONE.defaultBlockState();
            default -> Blocks.ANDESITE.defaultBlockState();
        };
    }
}
