package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Narrow, winding dirt paths in Highland Karst, suggestive of game trails.
 *
 * Samples a single continuous world-coordinate field, rather than drawing
 * random per-chunk line segments. A feature invocation edits exactly its
 * OWN 16x16 chunk footprint, to avoid writing into distant chunks. Replacing
 * surface blocks (not adding above them) prevents floating paths.
 *
 * The trail network is inferred from noise; no mobs actually use it yet.
 */
public record KarstAnimalTrailFeature() implements Feature {
    public static final MapCodec<KarstAnimalTrailFeature> CODEC =
            MapCodec.unit(new KarstAnimalTrailFeature());

    @Override
    public MapCodec<KarstAnimalTrailFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(
            WorldGenLevel level,
            ChunkGenerator generator,
            RandomSource random,
            BlockPos origin
    ) {
        // CountPlacement.of(1) triggers this once per decorated chunk;
        // snap to the same chunk grid regardless of the sampled origin.
        int minX = Math.floorDiv(origin.getX(), 16) * 16;
        int minZ = Math.floorDiv(origin.getZ(), 16) * 16;
        int changed = 0;

        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = minX + dx;
                int z = minZ + dz;

                double width = pathHalfWidth(x, z);
                if (width <= 0) continue;

                // Calculate a local gradient so the trail is approximately
                // 2-3 BLOCKS wide even as the noise field grows flatter.
                double value = pathField(x, z);
                double gx = (pathField(x + 1, z) - pathField(x - 1, z)) * 0.5;
                double gz = (pathField(x, z + 1) - pathField(x, z - 1)) * 0.5;
                double gradient = Math.sqrt(gx * gx + gz * gz);
                if (gradient < 0.006) continue;
                double distance = Math.abs(value) / gradient;
                if (distance > width) continue;

                // Break up a minority of path-edge pixels so the boundaries
                // remain organic, without destroying the central route.
                if (distance > width * 0.78
                        && hash01(x, z, 91) < 0.30) continue;

                int y = surfaceY(level, x, z);
                if (y <= level.getMinY() + 2 || y >= level.getMaxY() - 2) continue;

                BlockPos ground = new BlockPos(x, y - 1, z);
                BlockPos above = ground.above();
                if (!level.getBiome(ground).is(ModBiomes.HIGHLAND_KARST)) continue;
                if (!net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(level.getBlockState(ground), Blocks.GRASS_BLOCK)) continue;
                if (!level.getFluidState(above).isEmpty()) continue;
                if (!level.getFluidState(ground).isEmpty()) continue;

                BlockState air = level.getBlockState(above);
                if (!(air.isAir() || air.is(Blocks.SHORT_GRASS) || air.is(Blocks.FERN))) {
                    continue;
                }

                // Trails skirt abrupt cliffs; allow mild rolling slopes.
                if (Math.abs(surfaceY(level, x + 1, z) - y) > 2
                        || Math.abs(surfaceY(level, x - 1, z) - y) > 2
                        || Math.abs(surfaceY(level, x, z + 1) - y) > 2
                        || Math.abs(surfaceY(level, x, z - 1) - y) > 2) continue;

                BlockState support = level.getBlockState(ground.below());
                if (support.isAir() || support.canBeReplaced()
                        || !level.getFluidState(ground.below()).isEmpty()) {
                    continue;
                }

                BlockState replacement = trailMaterial(x, z);
                if (level.setBlock(ground, replacement, 2)) changed++;
            }
        }
        return changed > 0;
    }

    private static int surfaceY(WorldGenLevel level, int x, int z) {
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
    }

    // Contours of a continuous, gently warped noise field form the trails.
    private static double pathField(int x, int z) {
        double wx = 7.0 * noise(x / 83.0, z / 83.0, 410);
        double wz = 7.0 * noise(x / 83.0, z / 83.0, 411);
        return noise((x + wx) / 42.0, (z + wz) / 42.0, 412)
                + 0.19 * noise(x / 16.0, z / 16.0, 413);
    }

    private static double pathHalfWidth(int x, int z) {
        // Large regions have no trails, leaving quiet open grassland.
        double region = noise(x / 160.0, z / 160.0, 500);
        if (region < -0.42) return 0;
        double strength = Math.max(0.0, Math.min(1.0, (region + 0.42) / 0.32));
        return 0.65 + 0.75 * strength;
    }

    private static BlockState trailMaterial(int x, int z) {
        // Coherent reddish pockets rather than checkerboard red sand.
        double sandy = noise(x / 11.0, z / 11.0, 600);
        double p = hash01(x, z, 601);
        if (sandy > 0.26 && p < 0.58) {
            return Blocks.RED_SAND.defaultBlockState();
        }
        if (p < 0.72) return Blocks.COARSE_DIRT.defaultBlockState();
        if (p < 0.85) return Blocks.ROOTED_DIRT.defaultBlockState();
        if (p < 0.94) return ModBlocks.RUBBLED_LIMESTONE.defaultBlockState();
        return Blocks.RED_SAND.defaultBlockState();
    }

    private static double noise(double x, double z, long salt) {
        long ix = (long) Math.floor(x);
        long iz = (long) Math.floor(z);
        double tx = x - ix;
        double tz = z - iz;
        tx = tx * tx * (3 - 2 * tx);
        tz = tz * tz * (3 - 2 * tz);
        double a = hash01(ix, iz, salt) * 2 - 1;
        double b = hash01(ix + 1, iz, salt) * 2 - 1;
        double c = hash01(ix, iz + 1, salt) * 2 - 1;
        double d = hash01(ix + 1, iz + 1, salt) * 2 - 1;
        return lerp(lerp(a, b, tx), lerp(c, d, tx), tz);
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private static double hash01(long x, long z, long salt) {
        long h = salt ^ (x * 0x9E3779B97F4A7C15L)
                ^ (z * 0xC2B2AE3D27D4EB4FL);
        h ^= h >>> 30;
        h *= 0xBF58476D1CE4E5B9L;
        h ^= h >>> 27;
        h *= 0x94D049BB133111EBL;
        h ^= h >>> 31;
        return (h >>> 11) * 0x1.0p-53;
    }
}
