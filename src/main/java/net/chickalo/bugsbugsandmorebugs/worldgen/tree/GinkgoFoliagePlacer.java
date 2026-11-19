package net.chickalo.bugsbugsandmorebugs.worldgen.tree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

/**
 * Ginkgo crown: narrower lower foliage widening toward the upper layers.
 * The two configured variants use a different maximum radius and layer count.
 * Does not recolor the foliage texture (already golden).
 */
public final class GinkgoFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<GinkgoFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            foliagePlacerParts(instance).and(Codec.intRange(5, 11).fieldOf("canopy_layers")
                    .forGetter(p -> p.canopyLayers)).apply(instance, GinkgoFoliagePlacer::new));

    private final int canopyLayers;
    public GinkgoFoliagePlacer(IntProvider radius, IntProvider offset, int canopyLayers) {
        super(radius, offset);
        this.canopyLayers = canopyLayers;
    }
    @Override protected FoliagePlacerType<?> type() { return ModFoliagePlacerTypes.GINKGO_FOLIAGE_PLACER; }
    @Override
    protected void createFoliage(WorldGenLevel level, FoliageSetter setter, RandomSource random,
            TreeFeature tree, int treeHeight, FoliageAttachment attachment,
            int foliageHeight, int leafRadius, int offset) {
        // The ginkgo's fan-shaped crown is a cluster of uneven, separated foliage
        // lobes, not uniform square layers. Keeps the lower trunk visible.
        final int lobes = 3 + random.nextInt(2);
        final double phase = random.nextDouble() * Math.PI * 2.0;
        for (int layer = 0; layer < canopyLayers; layer++) {
            float t = (float) layer / Math.max(1, canopyLayers - 1);
            var row = attachment.pos().below(canopyLayers - 1 - layer);
            int core = (t < 0.28f || t > 0.85f) ? 1 : 2;
            placeLeavesRow(level, setter, random, tree, row, core, 0, false);
            if (t < 0.20f || t > 0.95f) continue;
            int spread = Math.max(1, Math.round((0.35f + 0.65f * t) * (leafRadius - 1)));
            for (int i = 0; i < lobes; i++) {
                if (random.nextInt(7) == 0) continue;
                double a = phase + i * (Math.PI * 2.0 / lobes) + (random.nextDouble() - 0.5) * 0.30;
                int dx = Math.round((float)(Math.cos(a) * spread));
                int dz = Math.round((float)(Math.sin(a) * spread));
                int radius = t > 0.77f ? 1 + random.nextInt(2) : 2;
                placeLeavesRow(level, setter, random, tree, row.offset(dx, 0, dz),
                        radius, 0, false);
            }
        }
    }
    @Override public int foliageHeight(RandomSource random, int treeHeight, TreeFeature tree) {
        return canopyLayers;
    }
    @Override
    protected boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz,
            int currentRadius, boolean doubleTrunk) {
        int ax=Math.abs(dx), az=Math.abs(dz);
        boolean corner = ax == currentRadius && az == currentRadius;
        boolean edge = ax == currentRadius || az == currentRadius;
        return corner || (edge && random.nextInt(4) == 0)
                || (currentRadius > 2 && ax + az > currentRadius * 3 / 2
                    && random.nextInt(7) == 0);
    }
}
