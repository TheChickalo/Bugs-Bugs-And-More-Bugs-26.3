package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generates two distinct families of giant glowshrooms with individually
 * randomized proportions, curved stems, irregular caps, and optional offshoots.
 * The complete structure is checked before a single block is placed.
 */
public record HugeGlowshroomFeature(Block cap, Block stem) implements Feature {

    public static final MapCodec<HugeGlowshroomFeature> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BuiltInRegistries.BLOCK.byNameCodec()
                            .fieldOf("cap")
                            .forGetter(HugeGlowshroomFeature::cap),
                    BuiltInRegistries.BLOCK.byNameCodec()
                            .fieldOf("stem")
                            .forGetter(HugeGlowshroomFeature::stem)
            ).apply(instance, HugeGlowshroomFeature::new));

    // Eight compass directions, including diagonals.
    private static final int[][] DIRECTIONS = {
            {1, 0}, {1, 1}, {0, 1}, {-1, 1},
            {-1, 0}, {-1, -1}, {0, -1}, {1, -1}
    };

    @Override
    public MapCodec<HugeGlowshroomFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator,
                         RandomSource random, BlockPos origin) {
        BlockPos base = findBase(level, origin);
        if (base == null) return false;

        // If one growth profile collides with surrounding foliage or terrain,
        // try another before giving up. No partial mushrooms get left behind.
        for (int attempt = 0; attempt < 2; attempt++) {
            Map<BlockPos, BlockState> blocks = new LinkedHashMap<>();

            if (random.nextBoolean()) {
                buildSquat(blocks, base, random);
            } else {
                buildTall(blocks, base, random);
            }

            if (!hasRoom(level, blocks)) continue;

            blocks.forEach((pos, state) -> level.setBlock(pos, state, 2));
            return true;
        }
        return false;
    }

    private boolean hasRoom(WorldGenLevel level, Map<BlockPos, BlockState> blocks) {
        for (BlockPos pos : blocks.keySet()) {
            if (pos.getY() < level.getMinY() || pos.getY() >= level.getMaxY()) {
                return false;
            }
            BlockState existing = level.getBlockState(pos);
            if (!level.getFluidState(pos).isEmpty()) return false;
            if (!(existing.isAir() || existing.canBeReplaced()
                    || existing.is(BlockTags.LEAVES))) {
                return false;
            }
        }
        return true;
    }

    private void buildSquat(Map<BlockPos, BlockState> blocks,
                            BlockPos base, RandomSource random) {
        int height = 4 + random.nextInt(4); // 4-7
        int leanX = random.nextInt(3) - 1;
        int leanZ = random.nextInt(3) - 1;

        // A slender central stalk with a low, organic root flare.
        // The previous constant radius made the stem look like a thick neck.
        BlockPos[] centers = drawStem(blocks, base, height,
                0, leanX, leanZ);
        addSquatRootFlare(blocks, centers, random);

        // Even the narrowest cap is at least seven blocks wide, compared
        // with a ground-level stem of approximately three blocks.
        // The two axes never differ by more than one block.
        int radiusX = 3 + random.nextInt(2); // 3-4
        int radiusZ = Math.max(3, Math.min(4,
                radiusX + random.nextInt(3) - 1));
        BlockPos capCenter = centers[height - 1].above().offset(
                random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
        addCap(blocks, capCenter, radiusX, radiusZ, random);

        // A minority of squat mushrooms get one low, miniature offshoot.
        if (random.nextInt(4) == 0) {
            int rootY = 1 + random.nextInt(Math.max(1, height - 2));
            addBranch(blocks, centers[rootY], 2 + random.nextInt(2),
                    random.nextInt(2), random);
        }
    }

    /**
     * Give squat glowshrooms a tapered base rather than a constant-width stalk.
     * The ground layer is roughly three blocks across, the next layer has
     * one or two short roots, and every layer above that is just the slender
     * central stem. Random missing sides break up the squared-off collar.
     */
    private void addSquatRootFlare(Map<BlockPos, BlockState> blocks,
                                   BlockPos[] centers, RandomSource random) {
        int[][] cardinal = {
                {1, 0}, {0, 1}, {-1, 0}, {0, -1}
        };
        BlockState stemState = stem.defaultBlockState();

        int firstUpperRoot = random.nextInt(cardinal.length);
        int secondUpperRoot = random.nextInt(4) == 0
                ? (firstUpperRoot + 1 + random.nextInt(3)) % 4
                : -1;
        int missingGroundRoot = random.nextInt(cardinal.length);

        for (int i = 0; i < cardinal.length; i++) {
            int dx = cardinal[i][0];
            int dz = cardinal[i][1];
            boolean upperRoot = i == firstUpperRoot || i == secondUpperRoot;

            // Three or four short roots meet the ground, never a solid 5x5 base.
            // Every upper root also has a lower block supporting it.
            if (i != missingGroundRoot || upperRoot) {
                blocks.put(centers[0].offset(dx, 0, dz), stemState);
            }

            // Just one or two directions reach the second layer: gentle taper.
            if (upperRoot) {
                blocks.put(centers[1].offset(dx, 0, dz), stemState);
            }
        }
    }

    private void buildTall(Map<BlockPos, BlockState> blocks,
                           BlockPos base, RandomSource random) {
        int height = 9 + random.nextInt(7); // 9-15
        int leanX = random.nextInt(5) - 2; // up to two blocks of lean
        int leanZ = random.nextInt(5) - 2;

        BlockPos[] centers = drawStem(blocks, base, height,
                0, leanX, leanZ);

        // Tall mushrooms keep a relatively compact crown.
        int radiusX = 2 + random.nextInt(2); // 2-3
        int radiusZ = Math.max(2, Math.min(3,
                radiusX + random.nextInt(3) - 1));
        addCap(blocks, centers[height - 1].above(),
                radiusX, radiusZ, random);

        // Independent sizes, heights and directions for each side cap.
        int branches = random.nextInt(5); // 0-4
        for (int i = 0; i < branches; i++) {
            int rootY = 2 + random.nextInt(height - 4);
            int length = 2 + random.nextInt(4); // 2-5
            int rise = 1 + random.nextInt(3); // 1-3
            addBranch(blocks, centers[rootY], length, rise, random);
        }
    }

    /** Draw a gently bending stem, connecting all offsets face-to-face. */
    private BlockPos[] drawStem(Map<BlockPos, BlockState> blocks,
                                BlockPos base, int height, int radius,
                                int leanX, int leanZ) {
        BlockPos[] centers = new BlockPos[height];
        BlockPos previous = null;

        for (int y = 0; y < height; y++) {
            double fraction = height <= 1 ? 0 : (double) y / (height - 1);
            double curve = fraction * fraction;
            BlockPos center = base.offset(
                    (int) Math.round(leanX * curve), y,
                    (int) Math.round(leanZ * curve));

            if (previous != null) {
                // Connect the previous layer to the new one before shifting.
                connect(blocks, previous, center);
            }

            fillStemCrossSection(blocks, center, radius);
            centers[y] = center;
            previous = center;
        }
        return centers;
    }

    private void fillStemCrossSection(Map<BlockPos, BlockState> blocks,
                                      BlockPos center, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz <= radius * radius + 0.2) {
                    blocks.put(center.offset(dx, 0, dz), stem.defaultBlockState());
                }
            }
        }
    }

    private void addBranch(Map<BlockPos, BlockState> blocks,
                           BlockPos root, int length, int rise,
                           RandomSource random) {
        int[] direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
        BlockPos previous = root;

        for (int step = 1; step <= length; step++) {
            int up = (int) Math.round((double) rise * step / length);
            BlockPos next = root.offset(
                    direction[0] * step, up, direction[1] * step);
            connect(blocks, previous, next);
            previous = next;
        }

        // A small, mostly circular offshoot cap.
        int radius = 1 + random.nextInt(2);
        int otherRadius = random.nextInt(4) == 0
                ? Math.max(1, radius - 1) : radius;
        addCap(blocks, previous.above(), radius, otherRadius, random);
    }

    /** Manhattan path ensures diagonal branches and leaning stems are attached. */
    private void connect(Map<BlockPos, BlockState> blocks,
                         BlockPos from, BlockPos to) {
        BlockPos current = from;
        while (current.getY() != to.getY()) {
            current = current.offset(0, Integer.signum(to.getY() - current.getY()), 0);
            blocks.put(current, stem.defaultBlockState());
        }
        while (current.getX() != to.getX()) {
            current = current.offset(Integer.signum(to.getX() - current.getX()), 0, 0);
            blocks.put(current, stem.defaultBlockState());
        }
        while (current.getZ() != to.getZ()) {
            current = current.offset(0, 0, Integer.signum(to.getZ() - current.getZ()));
            blocks.put(current, stem.defaultBlockState());
        }
    }

    /**
     * Controlled cap silhouettes: rounded, low-domed, or subtly bell-shaped.
     * A narrow lower edge creates an overhanging rim instead of a giant blob.
     * No layer shifts or excessive lobes, so the shape stays mushroom-like.
     */
    private void addCap(Map<BlockPos, BlockState> blocks,
                        BlockPos center, int radiusX, int radiusZ,
                        RandomSource random) {
        int style = random.nextInt(3); // rounded, low dome, gentle bell
        int topLayer = style == 1 ? 1 : 2;
        double phaseA = random.nextDouble() * Math.PI * 2;
        double phaseB = random.nextDouble() * Math.PI * 2;
        double waviness = 0.012 + random.nextDouble() * 0.028;

        for (int layer = -1; layer <= topLayer; layer++) {
            double scale;
            if (layer == -1) {
                scale = style == 2 ? 0.90 : 0.82;
            } else if (layer == 0) {
                scale = 1.0;
            } else if (layer == 1) {
                scale = style == 1 ? 0.83 : (style == 2 ? 0.76 : 0.80);
            } else {
                scale = style == 2 ? 0.40 : 0.48;
            }

            for (int dx = -radiusX; dx <= radiusX; dx++) {
                for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                    double angle = Math.atan2(dz, dx);
                    double edge = scale * (1.0
                            + waviness * Math.sin(angle * 5.0 + phaseA)
                            + waviness * 0.4 * Math.cos(angle * 3.0 + phaseB));
                    double ellipse = (double) dx * dx / (radiusX * radiusX)
                            + (double) dz * dz / (radiusZ * radiusZ);
                    if (ellipse > edge * edge) continue;

                    // Keep just the outside lower rim on larger caps;
                    // the solid disc above it makes the roof continuous.
                    if (layer == -1 && radiusX >= 2 && radiusZ >= 2) {
                        double innerEdge = Math.max(0.0, edge - 0.26);
                        if (ellipse < innerEdge * innerEdge) continue;
                    }

                    blocks.put(center.offset(dx, layer, dz), cap.defaultBlockState());
                }
            }
        }
    }

    private BlockPos findBase(WorldGenLevel level, BlockPos origin) {
        // Bonemeal growth starts at the small mushroom's coordinates.
        if (isSoil(level.getBlockState(origin.below()))) return origin;

        // Natural generation can begin on top of the maple canopy.
        int x = origin.getX();
        int z = origin.getZ();
        int top = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
        for (int y = top; y > level.getMinY(); y--) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(pos);
            if (state.isAir() || state.canBeReplaced() || state.is(BlockTags.LEAVES)) {
                continue;
            }
            if (!isSoil(state)) return null; // don't generate in caves or trees
            return pos.above();
        }
        return null;
    }

    private boolean isSoil(BlockState state) {
        return net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(state, Blocks.GRASS_BLOCK)
                || net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(state, Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.ROOTED_DIRT)
                || state.is(Blocks.MYCELIUM)
                || state.is(Blocks.MOSS_BLOCK);
    }
}
