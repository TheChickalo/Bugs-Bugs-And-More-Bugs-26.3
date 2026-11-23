package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;

import net.minecraft.core.Direction;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Adds a visible, irregular 2-3-block-wide woodland shoreline around calm,
 * shallow water in the Maple Forest, with clay and mossy cobblestone mixed
 * into both the riverbed and the bank. Also adds occasional low mossy rocks
 * and fallen maple logs at the water edge. Boulder columns extend into
 * the riverbed. All dirt-type soil exposed under shallow-to-moderate depth
 * water becomes mud; existing stone, clay and gravel are preserved.
 * Does NOT carve terrain or add water.
 *
 * For biome-crossing rivers, this decorates areas reached by Maple Forest
 * placement attempts; it does not rewrite vanilla River biome surface rules.
 */
public record MapleWaterShoreFeature() implements Feature {

    public static final MapCodec<MapleWaterShoreFeature> CODEC =
            MapCodec.unit(new MapleWaterShoreFeature());

    private static final int SHORE_RADIUS = 3;
    private static final int MAX_SCAN_DEPTH = 36;

    @Override
    public MapCodec<MapleWaterShoreFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator,
                         RandomSource random, BlockPos origin) {

        // Each placement produces a coherent, softly irregular patch.
        double radius = 6.0 + random.nextDouble() * 2.0;
        double phase = random.nextDouble() * Math.PI * 2.0;
        int patchRadius = (int) Math.ceil(radius);
        int bound = patchRadius + SHORE_RADIUS;
        int gridSize = bound * 2 + 1;

        BlockPos[][] water = new BlockPos[gridSize][gridSize];
        BlockState mainBankMaterial = randomBankBlock(random);

        // Cache nearby top-of-water positions so shore checks do not rescan
        // the same world column repeatedly.
        for (int dx = -bound; dx <= bound; dx++) {
            for (int dz = -bound; dz <= bound; dz++) {
                if (dx * dx + dz * dz > (radius + SHORE_RADIUS) * (radius + SHORE_RADIUS)) {
                    continue;
                }
                water[dx + bound][dz + bound] = findTopWater(
                        level, origin.getX() + dx, origin.getZ() + dz);
            }
        }

        int changed = 0;
        List<BlockPos> edgeSites = new ArrayList<>();

        for (int dx = -patchRadius; dx <= patchRadius; dx++) {
            for (int dz = -patchRadius; dz <= patchRadius; dz++) {
                double angle = Math.atan2(dz, dx);
                double outline = radius * (1.0 + 0.11 * Math.sin(3 * angle + phase));
                if (dx * dx + dz * dz > outline * outline) {
                    continue;
                }

                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;
                BlockPos topWater = water[dx + bound][dz + bound];

                // Every exposed dirt-type underwater bed block becomes mud,
                // even when the water is just one block deep. We preserve
                // existing gravel, stone, clay and other natural materials.
                if (topWater != null) {
                    BlockPos bed = waterBedBelow(level, topWater, 12);
                    if (bed != null && isSoil(level.getBlockState(bed))) {
                        if (level.setBlock(bed, Blocks.MUD.defaultBlockState(), 2)) {
                            changed++;
                        }
                    }
                    continue;
                }

                // On land, locate the first ground surface below canopy.
                BlockPos soil = findTopSoil(level, x, z);
                if (soil == null) {
                    continue;
                }

                // Determine proximity to shallow, source-water columns and
                // reject high banks or cliffs (more than two blocks above water).
                int nearestSquared = Integer.MAX_VALUE;
                for (int ox = -SHORE_RADIUS; ox <= SHORE_RADIUS; ox++) {
                    for (int oz = -SHORE_RADIUS; oz <= SHORE_RADIUS; oz++) {
                        int distSquared = ox * ox + oz * oz;
                        if (distSquared == 0 || distSquared > SHORE_RADIUS * SHORE_RADIUS) {
                            continue;
                        }

                        BlockPos nearbyWater = water[dx + ox + bound][dz + oz + bound];
                        if (nearbyWater == null
                                || Math.abs(soil.getY() - nearbyWater.getY()) > 2
                                || shallowBedBelow(level, nearbyWater) == null) {
                            continue;
                        }
                        nearestSquared = Math.min(nearestSquared, distSquared);
                    }
                }

                if (nearestSquared == Integer.MAX_VALUE) {
                    continue;
                }

                // Close to the edge almost always changes; farther soil
                // gradually transitions back into the surrounding forest.
                float probability = nearestSquared <= 2 ? 0.95F
                        : nearestSquared <= 4 ? 0.66F : 0.26F;
                if (random.nextFloat() >= probability) {
                    continue;
                }

                BlockState material = random.nextFloat() < 0.58F
                        ? mainBankMaterial : randomBankBlock(random);
                if (level.setBlock(soil, material, 2)) {
                    changed++;
                    // Only the inner two-block shoreline is suitable for
                    // shoreline decorations; farther banks stay open.
                    if (nearestSquared <= 4) {
                        edgeSites.add(soil);
                    }
                }
            }
        }

        // Decorations are deliberately uncommon; these probabilities are per
        // successful shoreline PATCH, not per chunk.
        if (!edgeSites.isEmpty()) {
            if (random.nextFloat() < 0.42F) {
                changed += tryPlaceBoulder(level, random, edgeSites);
            }
            if (random.nextFloat() < 0.13F) {
                changed += tryPlaceFallenLog(level, random, edgeSites);
            }
        }

        return changed > 0;
    }

    /**
     * Anchored mossy boulders. Unlike the old flat bank-height footprint,
     * each rock column extends DOWN through source water to a solid riverbed.
     * We validate every proposed block before placing anything: the boulder
     * is all-or-nothing, so an obstruction cannot leave a floating fragment.
     */
    private static int tryPlaceBoulder(WorldGenLevel level, RandomSource random,
                                       List<BlockPos> sites) {
        for (int attempt = 0; attempt < 14; attempt++) {
            BlockPos bank = sites.get(random.nextInt(sites.size()));
            double radius = 1.1 + random.nextDouble() * 0.65;
            int crownY = bank.getY() + 1;

            List<BlockPos> rocks = new ArrayList<>();
            int footprintColumns = 0;
            int submergedColumns = 0;
            boolean obstructed = false;

            // Build uneven columns rather than a floating horizontal slab.
            footprint:
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    double distance = Math.sqrt(dx * dx + dz * dz);
                    if (distance > radius + (random.nextDouble() - 0.5) * 0.20) {
                        continue;
                    }

                    int x = bank.getX() + dx;
                    int z = bank.getZ() + dz;
                    int topY = crownY;

                    // The outer rim slopes down to blend into the bank.
                    if (distance > 0.9 && random.nextFloat() < 0.55F) {
                        topY--;
                    } else if (distance < 0.6 && random.nextFloat() < 0.20F) {
                        topY++; // Occasionally a slightly higher central hump.
                    }

                    BlockPos footing = findRockFooting(level, x, z, topY);
                    if (footing == null) {
                        obstructed = true;
                        break footprint;
                    }
                    if (footing.getY() >= topY) {
                        continue; // Existing terrain is already this high.
                    }

                    boolean submerged = false;
                    for (int y = footing.getY() + 1; y <= topY; y++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (!canReplaceRockSpace(level, pos)) {
                            obstructed = true;
                            break footprint;
                        }
                        if (level.getBlockState(pos).is(Blocks.WATER)) {
                            submerged = true;
                        }
                        rocks.add(pos);
                    }

                    footprintColumns++;
                    if (submerged) {
                        submergedColumns++;
                    }
                }
            }

            // Target true waterside rocks. Reject unattached/obstructed shapes,
            // and land-only clusters that should not be called river boulders.
            if (obstructed || footprintColumns < 3 || submergedColumns == 0) {
                continue;
            }

            for (BlockPos pos : rocks) {
                level.setBlock(pos, boulderMaterial(random), 2);
            }
            return rocks.size();
        }
        return 0;
    }

    /**
     * Find the actual stone/soil floor under a proposed rock column.
     * We can descend through AIR, replaceable plants and source WATER, but
     * must stop at the first solid block. At most 5 blocks are searched,
     * keeping these low-profile formations restricted to shallow water.
     */
    private static BlockPos findRockFooting(WorldGenLevel level,
                                            int x, int z, int topY) {
        int minY = Math.max(level.getMinY() + 1, topY - 5);
        if (topY >= level.getMaxY() || topY < level.getMinY() + 1) {
            return null;
        }
        for (int y = topY; y >= minY; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(pos);
            if (isNaturalGround(state)) {
                return pos;
            }
            if (!canReplaceRockSpace(level, pos)) {
                return null;
            }
        }
        return null;
    }

    /**
     * Short X- or Z-aligned maple trunks. Validate the entire log before
     * changing the world, so failed attempts leave no floating fragments.
     */
    private static int tryPlaceFallenLog(WorldGenLevel level, RandomSource random,
                                         List<BlockPos> sites) {
        for (int attempt = 0; attempt < 12; attempt++) {
            BlockPos bank = sites.get(random.nextInt(sites.size()));
            Direction.Axis axis = random.nextBoolean()
                    ? Direction.Axis.X : Direction.Axis.Z;
            int length = 3 + random.nextInt(3); // 3-5 blocks
            int start = -random.nextInt(length);
            List<BlockPos> positions = new ArrayList<>();
            int supports = 0;
            boolean valid = true;

            for (int i = 0; i < length; i++) {
                int offset = start + i;
                BlockPos pos = axis == Direction.Axis.X
                        ? bank.offset(offset, 1, 0)
                        : bank.offset(0, 1, offset);
                BlockState current = level.getBlockState(pos);

                // Never remove another trunk, canopy, mushroom or water.
                if (!canReplaceLandSpace(level, pos)) {
                    valid = false;
                    break;
                }

                boolean supported = isNaturalGround(level.getBlockState(pos.below()));
                if (supported) {
                    supports++;
                } else if (i != 0 && i != length - 1) {
                    // A single overhanging end is okay; suspended centers aren't.
                    valid = false;
                    break;
                }
                positions.add(pos);
            }

            if (!valid || supports < length - 1) {
                continue;
            }

            BlockState log = ModBlocks.MAPLE_LOG.defaultBlockState()
                    .setValue(RotatedPillarBlock.AXIS, axis);
            for (BlockPos pos : positions) {
                level.setBlock(pos, log, 2);
            }
            return positions.size();
        }
        return 0;
    }

    private static BlockState boulderMaterial(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.62F) return ModBlocks.MOSSY_RHYOLITE.defaultBlockState();
        if (roll < 0.82F) return ModBlocks.COBBLED_RHYOLITE.defaultBlockState();
        if (roll < 0.94F) return ModBlocks.AMPHIBOLITE.defaultBlockState();
        return Blocks.MOSS_BLOCK.defaultBlockState();
    }

    private static boolean canReplaceLandSpace(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return level.getFluidState(pos).isEmpty()
                && !state.is(BlockTags.LOGS)
                && !state.is(BlockTags.LEAVES)
                && !state.is(ModBlocks.GLOWSHROOM)
                && (state.isAir() || state.canBeReplaced());
    }

    private static boolean canReplaceRockSpace(WorldGenLevel level, BlockPos pos) {
        if (canReplaceLandSpace(level, pos)) {
            return true;
        }
        // Only displace still source water, not flowing water or plants.
        return level.getBlockState(pos).is(Blocks.WATER)
                && level.getFluidState(pos).isSource();
    }

    private static boolean isNaturalGround(BlockState state) {
        if (state.is(ModBlocks.MOSSY_RHYOLITE) || state.is(ModBlocks.COBBLED_RHYOLITE)
                || state.is(ModBlocks.RHYOLITE) || state.is(ModBlocks.AMPHIBOLITE)) return true;
        return isSoil(state)
                || state.is(Blocks.MOSS_BLOCK)
                || state.is(Blocks.MOSSY_COBBLESTONE)
                || state.is(Blocks.COBBLESTONE)
                || net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(state, Blocks.STONE)
                || state.is(Blocks.ANDESITE)
                || state.is(Blocks.TUFF)
                || state.is(Blocks.GRAVEL)
                || state.is(Blocks.CLAY)
                || state.is(Blocks.MUD)
                || state.is(Blocks.SAND);
    }

    /** Finds surface source water beneath leaves, but never searches into caves. */
    private static BlockPos findTopWater(WorldGenLevel level, int x, int z) {
        int topY = Math.min(level.getMaxY() - 1,
                level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z));
        int bottomY = Math.max(level.getMinY() + 1, topY - MAX_SCAN_DEPTH);

        for (int y = topY; y >= bottomY; y--) {
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
            return null;
        }
        return null;
    }

    /** Finds exposed topsoil, skipping foliage but protecting trees and rock. */
    private static BlockPos findTopSoil(WorldGenLevel level, int x, int z) {
        int topY = Math.min(level.getMaxY() - 1,
                level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z));
        int bottomY = Math.max(level.getMinY() + 1, topY - MAX_SCAN_DEPTH);

        for (int y = topY; y >= bottomY; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(pos);

            if (state.is(BlockTags.LEAVES) || state.isAir() || state.canBeReplaced()) {
                continue;
            }
            // Never remove trunks, bamboo, rock shelves, or underwater blocks.
            return isSoil(state) ? pos : null;
        }
        return null;
    }

    /** Restrict bank decoration to genuinely shallow 1-4-block water. */
    private static BlockPos shallowBedBelow(WorldGenLevel level, BlockPos topWater) {
        return waterBedBelow(level, topWater, 4);
    }

    /**
     * Finds the first solid bed block beneath a continuous source-water column.
     * The depth limit is broader for mud than for shoreline decoration.
     * Never turns caves or flowing-water pockets into riverbed targets.
     */
    private static BlockPos waterBedBelow(WorldGenLevel level,
                                          BlockPos topWater, int maxDepth) {
        BlockPos cursor = topWater;
        int depth = 0;
        while (depth <= maxDepth && level.getBlockState(cursor).is(Blocks.WATER)) {
            if (!level.getFluidState(cursor).isSource()) {
                return null;
            }
            depth++;
            cursor = cursor.below();
        }
        if (depth == 0 || depth > maxDepth || cursor.getY() <= level.getMinY()) {
            return null;
        }
        return cursor;
    }

    private static boolean isSoil(BlockState state) {
        return net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(state, Blocks.DIRT)
                || net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(state, Blocks.GRASS_BLOCK)
                || (state.is(Blocks.COARSE_DIRT) || state.is(ModBlocks.COARSE_LOAM))
                || state.is(Blocks.PODZOL)
                || (state.is(Blocks.ROOTED_DIRT) || state.is(ModBlocks.ROOTED_LOAM));
    }

    private static BlockState randomBankBlock(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.29F) return Blocks.MOSS_BLOCK.defaultBlockState();
        if (roll < 0.56F) return ModBlocks.MOSSY_RHYOLITE.defaultBlockState();
        if (roll < 0.75F) return ModBlocks.AMPHIBOLITE.defaultBlockState();
        if (roll < 0.86F) return ModBlocks.LAPILLI.defaultBlockState();
        if (roll < 0.94F) return Blocks.CLAY.defaultBlockState();
        if (roll < 0.97F) return ModBlocks.COARSE_LOAM.defaultBlockState();
        return ModBlocks.RHYOLITE.defaultBlockState();
    }
}
