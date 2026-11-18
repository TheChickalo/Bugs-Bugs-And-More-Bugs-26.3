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
 * Small, irregular bands of exposed limestone and bare soil in Highland Karst.
 * Occasional compact red-sand pockets add a warm accent to soil-heavy patches.
 * Replaces exposed grass with natural soil and rock patches and adds shallow
 * rendzina/lithosol horizons under some soils. Never alters the terrain height.
 * Place this AFTER limestone outcrops and BEFORE grass/flower patches.
 */
public record KarstGroundPatchFeature() implements Feature {

    public static final MapCodec<KarstGroundPatchFeature> CODEC =
            MapCodec.unit(new KarstGroundPatchFeature());

    @Override
    public MapCodec<KarstGroundPatchFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level,
                         ChunkGenerator generator,
                         RandomSource random,
                         BlockPos origin) {
        int radiusX = 4 + random.nextInt(5); // 4-8 blocks
        int radiusZ = 2 + random.nextInt(3); // 2-4 blocks
        int bound = Math.max(radiusX, radiusZ);

        // Rotate the patch so it resembles a natural, broken strip of soil/rock.
        double direction = random.nextDouble() * Math.PI * 2.0;
        double cos = Math.cos(direction);
        double sin = Math.sin(direction);
        double phase = random.nextDouble() * Math.PI * 2.0;

        // Favor rockier patches on uneven terrain and soil on gentler slopes.
        int centerY = terrainY(level, origin.getX(), origin.getZ());
        int slope = 0;
        slope = Math.max(slope, Math.abs(centerY - terrainY(level, origin.getX() + 3, origin.getZ())));
        slope = Math.max(slope, Math.abs(centerY - terrainY(level, origin.getX() - 3, origin.getZ())));
        slope = Math.max(slope, Math.abs(centerY - terrainY(level, origin.getX(), origin.getZ() + 3)));
        slope = Math.max(slope, Math.abs(centerY - terrainY(level, origin.getX(), origin.getZ() - 3)));

        boolean rocky = random.nextFloat() < (slope >= 3 ? 0.78F : 0.46F);

        // Only some of the gentler, soil-heavy patches contain red sand.
        // Give that sand one small, irregular center instead of sprinkling
        // individual bright-orange blocks throughout the whole patch.
        boolean sandyPocket = !rocky && random.nextFloat() < 0.42F;
        double sandU = (random.nextDouble() - 0.5) * radiusX * 0.55;
        double sandV = (random.nextDouble() - 0.5) * radiusZ * 0.45;
        double sandRadiusU = 1.5 + random.nextDouble() * 1.1;
        double sandRadiusV = 1.1 + random.nextDouble() * 0.8;

        int changed = 0;

        for (int dx = -bound; dx <= bound; dx++) {
            for (int dz = -bound; dz <= bound; dz++) {
                double u = dx * cos + dz * sin;
                double v = -dx * sin + dz * cos;
                double normalized = Math.sqrt(
                        u * u / (radiusX * radiusX)
                        + v * v / (radiusZ * radiusZ));

                // Softly irregular perimeter, with gaps around its edges.
                double angle = Math.atan2(v, u);
                double edge = 1.0 + 0.12 * Math.sin(3.0 * angle + phase)
                        + 0.06 * Math.sin(5.0 * angle - phase);
                if (normalized > edge) continue;
                double fraction = normalized / edge;
                if (random.nextDouble() > 0.72 - fraction * 0.28) continue;

                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;
                int topY = terrainY(level, x, z);
                if (topY <= level.getMinY() + 1 || topY >= level.getMaxY() - 1) continue;

                BlockPos top = new BlockPos(x, topY, z);
                BlockPos ground = top.below();

                // Do not touch caves, water, outcrops, or any other biome.
                if (!level.getBiome(ground).is(ModBiomes.HIGHLAND_KARST)
                        && !level.getBiome(ground).is(ModBiomes.STONE_FOREST)) continue;
                if (!net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(level.getBlockState(ground), Blocks.GRASS_BLOCK)) continue;
                if (!level.getFluidState(top).isEmpty()) continue;
                BlockState above = level.getBlockState(top);
                if (!(above.isAir() || above.is(Blocks.SHORT_GRASS) || above.is(Blocks.FERN))) {
                    continue;
                }

                BlockState replacement = pickMaterial(random, rocky);

                if (sandyPocket) {
                    double du = (u - sandU) / sandRadiusU;
                    double dv = (v - sandV) / sandRadiusV;
                    double sandDistanceSquared = du * du + dv * dv;

                    // Soft edges around a compact red-sand pocket.
                    // Sand is gravity-affected: only place it on supported,
                    // dry ground so no falling blocks are left behind.
                    BlockPos below = ground.below();
                    BlockState support = level.getBlockState(below);
                    boolean supported = !support.isAir()
                            && !support.canBeReplaced()
                            && level.getFluidState(below).isEmpty();

                    if (supported && sandDistanceSquared < 1.0
                            && random.nextDouble() < 0.88 - 0.38 * sandDistanceSquared) {
                        replacement = Blocks.RED_SAND.defaultBlockState();
                    }
                }

                if (level.setBlock(ground, replacement, 2)) {
                    changed++;

                    // Only soil-like surface patches receive a shallow soil profile.
                    // The rocky portions stay as exposed limestone.
                    if (!rocky && (replacement.is(ModBlocks.RENDZINA)
                            || replacement.is(ModBlocks.LITHOSOL)
                            || replacement.is(Blocks.RED_SAND))) {
                        changed += placeSoilHorizon(level, ground, random);
                    }
                }
            }
        }

        return changed > 0;
    }

    private static int terrainY(WorldGenLevel level, int x, int z) {
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
    }

    /**
     * Add a shallow, game-stylized karst soil profile under exposed soil:
     * surface -> rendzina -> lithosol -> rendzina -> weathered limestone.
     * The profile stops immediately at caves, fluids, ores, or other blocks
     * that shouldn't be modified by a surface decoration feature.
     */
    private static int placeSoilHorizon(
            WorldGenLevel level, BlockPos surface, RandomSource random) {
        int changed = 0;
        int depthLimit = 3 + random.nextInt(2); // 3-4 layers below surface

        for (int depth = 1; depth <= depthLimit; depth++) {
            BlockPos pos = surface.below(depth);
            if (pos.getY() <= level.getMinY()) break;

            BlockState old = level.getBlockState(pos);
            if (!isCommonSubsoil(old) || !level.getFluidState(pos).isEmpty()) {
                break;
            }

            BlockState next = switch (depth) {
                case 1 -> ModBlocks.RENDZINA.defaultBlockState();
                case 2 -> ModBlocks.LITHOSOL.defaultBlockState();
                case 3 -> ModBlocks.RENDZINA.defaultBlockState();
                default -> random.nextFloat() < 0.65F
                        ? ModBlocks.WEATHERED_LIMESTONE.defaultBlockState()
                        : ModBlocks.LIMESTONE.defaultBlockState();
            };

            if (!old.equals(next) && level.setBlock(pos, next, 2)) {
                changed++;
            }
        }
        return changed;
    }

    private static boolean isCommonSubsoil(BlockState state) {
        // Never replace ores, caves, water, existing exposed formations,
        // biome-specific surfaces, or player-created decorative blocks.
        return net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(state, Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.ROOTED_DIRT)
                || net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(state, Blocks.STONE)
                || state.is(ModBlocks.RENDZINA)
                || state.is(ModBlocks.LITHOSOL);
    }

    private static BlockState pickMaterial(RandomSource random, boolean rocky) {
        float p = random.nextFloat();
        if (rocky) {
            if (p < 0.46F) return ModBlocks.RUBBLED_LIMESTONE.defaultBlockState();
            if (p < 0.68F) return ModBlocks.LIMESTONE.defaultBlockState();
            if (p < 0.85F) return ModBlocks.WEATHERED_LIMESTONE.defaultBlockState();
            if (p < 0.95F) return ModBlocks.LITHOSOL.defaultBlockState();
            return ModBlocks.RENDZINA.defaultBlockState();
        }
        if (p < 0.64F) return ModBlocks.RENDZINA.defaultBlockState();
        if (p < 0.80F) return ModBlocks.LITHOSOL.defaultBlockState();
        if (p < 0.88F) return Blocks.COARSE_DIRT.defaultBlockState();
        if (p < 0.94F) return Blocks.ROOTED_DIRT.defaultBlockState();
        if (p < 0.98F) return ModBlocks.RUBBLED_LIMESTONE.defaultBlockState();
        return ModBlocks.WEATHERED_LIMESTONE.defaultBlockState();
    }
}
