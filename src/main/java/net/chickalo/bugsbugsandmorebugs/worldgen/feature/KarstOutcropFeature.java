package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomeFamilies;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Karst outcrops concentrated in widely spaced, naturally shaped limestone deposits.
 *
 * Configurable dimensions permit later reuse for the Karst Plateau and
 * Stone Forest without rewriting the feature. This feature adds rocks;
 * it does not change terrain noise or carve caves.
 *
 * Generation is staged in a map and validated before any blocks are placed.
 * Each proposed column extends below the natural surface, preventing
 * suspended slabs and giving the outcrop a buried, eroded appearance.
 */
public record KarstOutcropFeature(
        int minHeight,
        int maxHeight,
        int maxRadius,
        int clusterChance
) implements Feature {

    public static final MapCodec<KarstOutcropFeature> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.intRange(1, 32).fieldOf("min_height")
                            .forGetter(KarstOutcropFeature::minHeight),
                    Codec.intRange(1, 40).fieldOf("max_height")
                            .forGetter(KarstOutcropFeature::maxHeight),
                    Codec.intRange(1, 6).fieldOf("max_radius")
                            .forGetter(KarstOutcropFeature::maxRadius),
                    Codec.intRange(0, 100).fieldOf("cluster_chance")
                            .forGetter(KarstOutcropFeature::clusterChance)
            ).apply(instance, KarstOutcropFeature::new));

    public KarstOutcropFeature {
        if (minHeight > maxHeight) {
            throw new IllegalArgumentException("minHeight cannot exceed maxHeight");
        }
    }

    @Override
    public MapCodec<KarstOutcropFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(
            WorldGenLevel level,
            ChunkGenerator generator,
            RandomSource random,
            BlockPos origin
    ) {
        // Only attempt rocks inside a deterministic, large-scale deposit zone.
        // The same world coordinates resolve to the same deposit center from
        // neighboring chunks, avoiding artificial chunk-shaped clusters.
        double deposit = depositStrength(origin.getX(), origin.getZ());
        if (deposit <= 0.0) return false;

        // Sparse at the edge, abundant near the deposit's center.
        if (random.nextDouble() > 0.22 + 0.78 * Math.sqrt(deposit)) {
            return false;
        }

        BlockPos mainGround = findDryGround(level, origin.getX(), origin.getZ());
        if (mainGround == null) return false;

        // A deposit often produces several adjacent pinnacles per attempt.
        int count = random.nextInt(100) < clusterChance
                ? 2 + random.nextInt(3) : 1;

        Map<BlockPos, BlockState> planned = new LinkedHashMap<>();
        List<BlockPos> centers = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int x = origin.getX() + (i == 0 ? 0 : random.nextInt(7) - 3);
            int z = origin.getZ() + (i == 0 ? 0 : random.nextInt(7) - 3);
            BlockPos ground = findDryGround(level, x, z);
            if (ground == null || Math.abs(ground.getY() - mainGround.getY()) > 3) {
                continue;
            }

            Map<BlockPos, BlockState> part = planPinnacle(level, random, ground);
            if (!part.isEmpty()) {
                planned.putAll(part);
                centers.add(ground);
            }
        }

        if (planned.isEmpty()) return false;

        // Last check: do not partly overwrite unrelated blocks or water.
        for (BlockPos pos : planned.keySet()) {
            if (!insideWorld(level, pos)
                    || !level.getFluidState(pos).isEmpty()
                    || !canReplaceWithRock(level.getBlockState(pos))) {
                return false;
            }
        }

        planned.forEach((pos, state) -> level.setBlock(pos, state, 2));
        scatterRubble(level, random, centers);
        return true;
    }

    private Map<BlockPos, BlockState> planPinnacle(
            WorldGenLevel level,
            RandomSource random,
            BlockPos center
    ) {
        Map<BlockPos, BlockState> part = new LinkedHashMap<>();

        int first = minHeight + random.nextInt(maxHeight - minHeight + 1);
        int second = minHeight + random.nextInt(maxHeight - minHeight + 1);
        // Mostly low rocks, with occasional dramatic taller pinnacles.
        int height = random.nextInt(5) == 0
                ? Math.max(first, second) : Math.min(first, second);

        // Taller formations should have a real footprint, not a single
        // seven-block-tall vertical pole.
        int radiusX = 1 + random.nextInt(maxRadius);
        if (height >= 5 && maxRadius >= 2) radiusX = Math.max(2, radiusX);
        int radiusZ = Math.max(1, Math.min(maxRadius, radiusX + random.nextInt(3) - 1));
        if (height >= 5 && maxRadius >= 2) radiusZ = Math.max(2, radiusZ);
        double angle = random.nextDouble() * Math.PI * 2.0;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        int searchRadius = Math.max(radiusX, radiusZ) + 1;

        for (int dx = -searchRadius; dx <= searchRadius; dx++) {
            for (int dz = -searchRadius; dz <= searchRadius; dz++) {
                double rotatedX = dx * cos + dz * sin;
                double rotatedZ = -dx * sin + dz * cos;
                double distance = Math.sqrt(
                        rotatedX * rotatedX / (radiusX * radiusX)
                                + rotatedZ * rotatedZ / (radiusZ * radiusZ)
                );

                // Softly chipped perimeter instead of a square or perfect ellipse.
                double edge = 1.04 + (random.nextDouble() - 0.5) * 0.23;
                if (distance > edge) continue;

                int x = center.getX() + dx;
                int z = center.getZ() + dz;
                BlockPos ground = findDryGround(level, x, z);
                if (ground == null || Math.abs(ground.getY() - center.getY()) > 2) {
                    continue;
                }

                double shape = 1.0 - 0.87 * Math.pow(Math.min(1.0, distance), 1.5);
                int exposedHeight = Math.max(1,
                        (int) Math.round(height * shape));
                if (distance < 0.65 && !(dx == 0 && dz == 0)) {
                    exposedHeight = Math.max(1, exposedHeight + random.nextInt(3) - 1);
                }
                if (dx == 0 && dz == 0) exposedHeight = height;

                // Start BELOW the existing ground, so no column floats.
                // Surface is the first air block above 'ground'.
                int bottom = ground.getY() - 1;
                int top = ground.getY() + exposedHeight;
                boolean validColumn = true;
                for (int y = bottom; y <= top; y++) {
                    BlockPos at = new BlockPos(x, y, z);
                    if (!insideWorld(level, at)
                            || !level.getFluidState(at).isEmpty()
                            || !canReplaceWithRock(level.getBlockState(at))) {
                        validColumn = false;
                        break;
                    }
                }
                if (!validColumn) continue;

                // Keep the buried core mostly limestone, with slight weathering
                // on exposed faces. Small connected color patches remain natural.
                BlockState columnMaterial = rockMaterial(random);
                for (int y = bottom; y <= top; y++) {
                    BlockPos at = new BlockPos(x, y, z);
                    BlockState material = y < ground.getY()
                            ? ModBlocks.LIMESTONE.defaultBlockState()
                            : columnMaterial;
                    if (y == top && random.nextInt(6) == 0) {
                        material = ModBlocks.WEATHERED_LIMESTONE.defaultBlockState();
                    }
                    part.put(at, material);
                }
            }
        }
        return part;
    }

    // ~104-block macro-cells with jittered (not grid-centered) deposits.
    // Most of the biome stays open; rock formations become abundant locally.
    // This is a spatial mask, NOT a terrain-height modifier.
    private static final int DEPOSIT_SPACING = 104;

    private static double depositStrength(int x, int z) {
        int cellX = Math.floorDiv(x, DEPOSIT_SPACING);
        int cellZ = Math.floorDiv(z, DEPOSIT_SPACING);
        double best = 0.0;

        // Look into neighboring cells so a deposit crosses cell boundaries
        // smoothly, even when a chunk starts in an adjacent macro-cell.
        for (int cx = cellX - 1; cx <= cellX + 1; cx++) {
            for (int cz = cellZ - 1; cz <= cellZ + 1; cz++) {
                long h = hash(cx, cz, 0x6A09E667F3BCC909L);
                if ((h & 7L) == 0L) continue; // some cells have no deposit

                int centerX = cx * DEPOSIT_SPACING + 16
                        + (int) Math.floorMod(h >>> 11, 73L);
                int centerZ = cz * DEPOSIT_SPACING + 16
                        + (int) Math.floorMod(h >>> 29, 73L);
                double radius = 19.0 + Math.floorMod(h >>> 47, 11L);

                double dx = x - centerX;
                double dz = z - centerZ;
                if (Math.abs(dx) > radius * 1.25 || Math.abs(dz) > radius * 1.25) continue;
                double angle = Math.atan2(dz, dx);
                double phase = (h & 0xFFFFL) * (Math.PI * 2 / 65536.0);
                double outline = 1.0
                        + 0.14 * Math.sin(3.0 * angle + phase)
                        + 0.07 * Math.cos(5.0 * angle - phase);

                double strength = 1.0 - Math.sqrt(dx * dx + dz * dz)
                        / (radius * outline);
                if (strength > best) best = strength;
            }
        }
        return Math.max(0.0, Math.min(1.0, best));
    }

    private static long hash(long x, long z, long salt) {
        long h = salt ^ (x * 0x9E3779B97F4A7C15L)
                ^ (z * 0xC2B2AE3D27D4EB4FL);
        h ^= h >>> 30;
        h *= 0xBF58476D1CE4E5B9L;
        h ^= h >>> 27;
        h *= 0x94D049BB133111EBL;
        return h ^ (h >>> 31);
    }

    private static BlockState rockMaterial(RandomSource random) {
        int choice = random.nextInt(100);
        if (choice < 70) return ModBlocks.LIMESTONE.defaultBlockState();
        if (choice < 94) return ModBlocks.WEATHERED_LIMESTONE.defaultBlockState();
        if (choice < 98) return ModBlocks.RUBBLED_LIMESTONE.defaultBlockState();
        return ModBlocks.MOSSY_LIMESTONE.defaultBlockState();
    }

    private static BlockPos findDryGround(WorldGenLevel level, int x, int z) {
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1;
        BlockPos ground = new BlockPos(x, y, z);
        if (!insideWorld(level, ground)
                || !insideWorld(level, ground.below())
                || !isNaturalGround(level.getBlockState(ground))
                || !isNaturalGround(level.getBlockState(ground.below()))
                || !level.getFluidState(ground).isEmpty()
                || !level.getFluidState(ground.above()).isEmpty()
                || !ModBiomeFamilies.rockOutcrop(level.getBiome(ground))) {
            return null;
        }
        return ground;
    }

    private static boolean isNaturalGround(BlockState state) {
        return net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(state, Blocks.GRASS_BLOCK)
                || net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(state, Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT)
                || net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(state, Blocks.STONE)
                || state.is(Blocks.ANDESITE)
                || state.is(ModBlocks.LIMESTONE)
                || state.is(ModBlocks.WEATHERED_LIMESTONE)
                || state.is(ModBlocks.RUBBLED_LIMESTONE);
    }

    private static boolean canReplaceWithRock(BlockState state) {
        return state.isAir()
                || isNaturalGround(state)
                || state.is(Blocks.SHORT_GRASS)
                || state.is(Blocks.FERN)
                || state.is(Blocks.SNOW);
    }

    private static boolean insideWorld(WorldGenLevel level, BlockPos pos) {
        return pos.getY() >= level.getMinY() && pos.getY() < level.getMaxY();
    }

    private static void scatterRubble(
            WorldGenLevel level,
            RandomSource random,
            List<BlockPos> centers
    ) {
        for (BlockPos center : centers) {
            int attempts = 4 + random.nextInt(8);
            for (int i = 0; i < attempts; i++) {
                int dx = random.nextInt(7) - 3;
                int dz = random.nextInt(7) - 3;
                int dist = dx * dx + dz * dz;
                if (dist < 2 || dist > 10 || random.nextInt(3) == 0) continue;
                BlockPos ground = findDryGround(level,
                        center.getX() + dx, center.getZ() + dz);
                if (ground == null || !net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(level.getBlockState(ground), Blocks.GRASS_BLOCK)) {
                    continue;
                }
                if (!level.getBlockState(ground.above()).isAir()) continue;
                level.setBlock(ground, ModBlocks.RUBBLED_LIMESTONE.defaultBlockState(), 2);
            }
        }
    }
}
