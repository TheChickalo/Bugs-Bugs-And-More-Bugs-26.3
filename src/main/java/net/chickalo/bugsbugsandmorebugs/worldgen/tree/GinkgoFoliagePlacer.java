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
 * Airy upward-opening ginkgo fans, with lobed outlines rather than
 * horizontally repeated solid squares. Leaves stay anchored to the bole.
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

    @Override protected FoliagePlacerType<?> type() {
        return ModFoliagePlacerTypes.GINKGO_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(WorldGenLevel level, FoliageSetter setter, RandomSource random,
            TreeFeature tree, int treeHeight, FoliageAttachment attachment,
            int foliageHeight, int leafRadius, int offset) {
        int shape = random.nextInt(3);
        for (int layer = 0; layer < canopyLayers; layer++) {
            double t = (double) layer / Math.max(1, canopyLayers - 1);
            double width = shape == 0 ? Math.pow(t, 0.75)
                    : shape == 1 ? Math.sqrt(t) : 0.18 + 0.82 * t;
            double factor = 0.18 + 0.85 * width - (t > 0.83 ? (t - 0.83) : 0);
            // Irregular notches between several rising golden leaf sprays.
            if (layer > 2 && layer < canopyLayers - 1 && layer % 3 == 0)
                factor *= 0.84;
            int r = Math.max(1, Math.round((float) (leafRadius * factor)));
            placeLeavesRow(level, setter, random, tree,
                    attachment.pos().below(canopyLayers - 1 - layer),
                    r, 0, attachment.doubleTrunk());
        }
    }

    @Override public int foliageHeight(RandomSource random, int treeHeight, TreeFeature tree) {
        return canopyLayers;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz,
            int currentRadius, boolean doubleTrunk) {
        if (currentRadius <= 1) return false;
        double angle = Math.atan2(dz, dx);
        double lobe = 0.81 + 0.11 * Math.sin(5 * angle) + 0.08 * Math.cos(3 * angle);
        double distance = Math.sqrt((double) dx * dx + (double) dz * dz) / currentRadius;
        return distance > lobe || (distance > lobe - 0.12 && random.nextInt(8) == 0);
    }
}
