package net.chickalo.bugsbugsandmorebugs.worldgen.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Interval;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;

/**
 * Seeded, cell-based islands with a strictly bounded footprint.
 * Reproducible at chunk borders and independent of chunk generation order.
 */
public record SkyArchipelagoDensityFunction(String mode) implements DensityFunction {
    public static final MapCodec<SkyArchipelagoDensityFunction> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(Codec.STRING.fieldOf("mode").forGetter(SkyArchipelagoDensityFunction::mode))
                    .apply(i, SkyArchipelagoDensityFunction::new));

    // Max footprint diameter 144 blocks. Even maximally jittered neighbours
    // have at least 24 blocks of air between their horizontal bounds.
    public static final int CELL_SIZE = 192;
    public static final int MAX_RADIUS = 72;
    public static final int CENTER_JITTER = 12;
    private static final int MIN_RADIUS = 18;
    // A deterministic 3x3-cell archipelago: one seed island plus an
    // outward chain with strictly declining continuation probabilities.
    private static final int CLUSTER_CELLS = 3;
    private static final int[] RING_X = {1, 2, 2, 2, 1, 0, 0, 0};
    private static final int[] RING_Z = {0, 0, 1, 2, 2, 2, 1, 0};
    private static final float[] NEXT_ISLAND_CHANCE = {
            0.96F, 0.88F, 0.75F, 0.60F, 0.42F, 0.28F, 0.14F, 0.07F
    };
    private static final long SALT_X = 0x9E3779B97F4A7C15L;
    private static final long SALT_Z = 0xC2B2AE3D27D4EB4FL;
    private static final Identifier SEED_ID = Identifier.fromNamespaceAndPath(
            BugsBugsAndMoreBugs.MOD_ID, "sky_archipelago_cells_v2");

    public SkyArchipelagoDensityFunction {
        if (!mode.equals("mask") && !mode.equals("solid")) {
            throw new IllegalArgumentException("Sky archipelago mode must be mask or solid: " + mode);
        }
    }

    @Override
    public DensitySampler compileSampler(DensityFunction.CompileContext context) {
        // Both "mask" and "solid" request the SAME named per-world stream.
        RandomSource random = context.createRandom(SEED_ID);
        return new IslandSampler(mode, random.nextLong());
    }

    @Override
    public DensityFunction rewriteChildren(DfRewriteRule rule) {
        return this;
    }

    @Override
    public Interval range() {
        return Interval.of(-3.0F, 3.0F);
    }

    @Override
    public @DensityFunction.Axes int domainAxes() {
        return mode.equals("mask") ? DensityFunction.AXIS_X | DensityFunction.AXIS_Z : DensityFunction.ALL_AXES;
    }

    @Override
    public MapCodec<SkyArchipelagoDensityFunction> codec() {
        return CODEC;
    }

    /** Standalone coordinate sampler, also used by shape regression tests. */
    public static float densityAt(long worldSeed, String mode, int x, int y, int z) {
        if (mode.equals("solid") && (y < 95 || y > 300)) {
            return -3.0F;
        }

        int gx = Math.floorDiv(x, CELL_SIZE);
        int gz = Math.floorDiv(z, CELL_SIZE);
        float best = -3.0F;

        for (int dcx = -1; dcx <= 1; dcx++) {
            for (int dcz = -1; dcz <= 1; dcz++) {
                int cxCell = gx + dcx;
                int czCell = gz + dcz;
                int baseX = cxCell * CELL_SIZE + CELL_SIZE / 2;
                int baseZ = czCell * CELL_SIZE + CELL_SIZE / 2;
                if (Math.abs(x - baseX) > MAX_RADIUS + CENTER_JITTER
                        || Math.abs(z - baseZ) > MAX_RADIUS + CENTER_JITTER) {
                    continue;
                }

                if (!clusterContains(worldSeed, cxCell, czCell)) {
                    continue;
                }
                long h = mix64(worldSeed ^ (cxCell * SALT_X) ^ (czCell * SALT_Z));

                float cx = baseX + (unit(h ^ 0x4452D97C3A8B55D1L) * 2F - 1F) * CENTER_JITTER;
                float cz = baseZ + (unit(h ^ 0x7C159E3779B945D1L) * 2F - 1F) * CENTER_JITTER;
                float u = unit(h ^ 0xD1342543DE82EF95L);
                float radius = MIN_RADIUS + (MAX_RADIUS - MIN_RADIUS) * u * u;
                float ax = 0.73F + 0.27F * unit(h ^ 0xAB53DE82D61CF542L);
                float az = 0.73F + 0.27F * unit(h ^ 0x71C2E51B47A9D552L);

                float sx = (x - cx) / (radius * ax);
                float sz = (z - cz) / (radius * az);
                float angle = (float) Math.atan2(sz, sx);
                float phase = 6.2831853F * unit(h ^ 0xC4B38F2761AD9E52L);
                // Always >=1: asymmetric shorelines can only SHRINK the capped footprint.
                float wobble = 1F + 0.10F * (0.5F + 0.5F * (float) Math.sin(angle * 5F + phase));
                float r = (float) Math.sqrt(sx * sx + sz * sz) * wobble;
                if (r >= 1F) {
                    continue;
                }
                if (mode.equals("mask")) {
                    best = Math.max(best, 1F - r);
                    continue;
                }

                // Different island heights, roughly top Y180–277.
                float top = 187F + 83F * unit(h ^ 0x9A9A2F6C7B1E5D31L);
                top += 4F * (float) Math.sin((x - cx) * 0.068F + phase);
                top += 3F * (float) Math.cos((z - cz) * 0.074F - phase);

                float core = Math.max(0F, 1F - r);
                float thickness = 9F + 58F * (float) Math.pow(core, 1.45) + 9F * (float) Math.pow(core, 5);
                // Three narrow drooping rock formations below each underside.
                for (int spike = 0; spike < 3; spike++) {
                    float px = (unit(h ^ (SALT_X + 101L * spike)) * 1.4F - 0.7F) * radius;
                    float pz = (unit(h ^ (SALT_Z + 131L * spike)) * 1.4F - 0.7F) * radius;
                    float dist = (float) Math.hypot(x - cx - px, z - cz - pz);
                    float taper = Math.max(0F, 1F - dist / (radius * 0.26F));
                    thickness += (11F + 16F * unit(h ^ (SALT_X + 157L * spike))) * taper * taper;
                }

                float horizontal = (1F - r) * 3F;
                float upper = (top - y) / 8F;
                float lower = (y - (top - thickness)) / 8F;
                best = Math.max(best, Math.min(horizontal, Math.min(upper, lower)));
            }
        }

        return Math.max(-3F, Math.min(3F, best));
    }

    /**
     * Derive cluster membership from the world seed and macro-cell alone,
     * never from chunk placement order. The center is seeded first; each
     * next adjacent cell requires ALL earlier continuation rolls to pass.
     * Those rolls have decreasing odds, so a cluster naturally tapers off.
     */
    private static boolean clusterContains(long worldSeed, int cellX, int cellZ) {
        int regionX = Math.floorDiv(cellX, CLUSTER_CELLS);
        int regionZ = Math.floorDiv(cellZ, CLUSTER_CELLS);
        int localX = Math.floorMod(cellX, CLUSTER_CELLS);
        int localZ = Math.floorMod(cellZ, CLUSTER_CELLS);
        long region = mix64(worldSeed ^ ((long)regionX * SALT_X)
                ^ ((long)regionZ * SALT_Z) ^ 0x4B1D5EEDC14A2B79L);

        // A few macro-regions remain completely empty, breaking up long
        // chains and leaving ocean sky between neighbouring archipelagos.
        if (unit(region ^ 0x2CA7F1D3E452B619L) > 0.94F) return false;
        if (localX == 1 && localZ == 1) return true;

        int ringIndex = -1;
        for (int i = 0; i < RING_X.length; i++) {
            if (localX == RING_X[i] && localZ == RING_Z[i]) {
                ringIndex = i;
                break;
            }
        }
        if (ringIndex == -1) return false;
        int start = (int)((region >>> 7) & 7);
        boolean reverse = ((region >>> 12) & 1L) != 0;
        int rank = 1 + Math.floorMod(reverse
                ? start - ringIndex : ringIndex - start, RING_X.length);
        for (int step = 1; step <= rank; step++) {
            long roll = region ^ (SALT_X * (step + 13L)) ^ SALT_Z;
            if (unit(roll) > NEXT_ISLAND_CHANCE[step - 1]) return false;
        }
        return true;
    }

    private static float unit(long value) {
        return (mix64(value) >>> 40) * (1F / (1 << 24));
    }

    private static long mix64(long z) {
        z = (z ^ (z >>> 30)) * 0xbf58476d1ce4e5b9L;
        z = (z ^ (z >>> 27)) * 0x94d049bb133111ebL;
        return z ^ (z >>> 31);
    }

    private record IslandSampler(String mode, long worldSeed) implements DensitySampler {
        @Override
        public float sampleValue(SamplerContext context, int blockX, int blockY, int blockZ) {
            return SkyArchipelagoDensityFunction.densityAt(worldSeed, mode, blockX, blockY, blockZ);
        }

        @Override
        public void sampleVolume(SamplerContext context, DensityBuffer outputBuffer, DensityVolume volume) {
            DensitySampler.sampleVolumeNaive(context, outputBuffer, volume, this);
        }
    }
}
