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
        // Tall, upright golden ginkgo fan: narrow lower canopy, widening upper
        // half, irregular twiggy outline. No floating horizontal cube layers.
        // Every row intersects the upper trunk so leaves stay supported.
        int variant = random.nextInt(3);
        int layers = canopyLayers;
        for (int layer = 0; layer < layers; layer++) {
            float t = (float)layer / Math.max(1, layers - 1);
            float r;
            if (variant == 0) {          // upright pyramidal young/middle-aged
                r = 0.28f + 0.66f * t;
            } else if (variant == 1) {   // fan: upper crown clearly broadest
                r = 0.18f + 0.86f * (float)Math.sqrt(t);
            } else {                    // irregular, slightly asymmetrical mature
                r = 0.42f + 0.52f * t + 0.10f * (float)Math.sin(t * 9);
            }
            if (t > 0.85f) r -= 0.13f * ((t - 0.85f)/0.15f);
            int radius = Math.max(1,Math.round(leafRadius*r));
            placeLeavesRow(level,setter,random,tree,
                attachment.pos().below(layers-1-layer),radius,0,attachment.doubleTrunk());
        }
    }
    @Override public int foliageHeight(RandomSource random, int treeHeight, TreeFeature tree) {
        return canopyLayers;
    }
    @Override
    protected boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz,
            int currentRadius, boolean doubleTrunk) {
        int ax=Math.abs(dx), az=Math.abs(dz);
        return (ax==currentRadius && az==currentRadius)
                || ((ax==currentRadius || az==currentRadius) && random.nextInt(10)==0);
    }
}
