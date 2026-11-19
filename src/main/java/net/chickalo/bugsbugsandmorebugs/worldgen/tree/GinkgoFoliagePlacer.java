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
 * Distinct rising leaf sprays instead of one vertically stacked slab.
 * Each branch tip produces an asymmetric, outward-opening crown.
 * Medium and old-growth trees have several real log branches.
 */
public final class GinkgoFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<GinkgoFoliagePlacer> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    foliagePlacerParts(instance).and(Codec.intRange(5, 11)
                            .fieldOf("canopy_layers")
                            .forGetter(p -> p.canopyLayers))
                            .apply(instance, GinkgoFoliagePlacer::new));

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
        final int arms = leafRadius >= 4 ? 4 + random.nextInt(2) : 3 + random.nextInt(2);
        final double rotation = random.nextDouble() * Math.PI * 2;
        final int spread = Math.max(1, leafRadius - 1);

        // Each spray starts low near its log attachment, rises and opens outward.
        for (int arm = 0; arm < arms; arm++) {
            double angle = rotation + arm * Math.PI * 2 / arms
                    + (random.nextDouble() - 0.5) * 0.34;
            double ux = Math.cos(angle), uz = Math.sin(angle);
            int bottomSkip = random.nextInt(3);
            int topSkip = random.nextInt(2);

            for (int row = bottomSkip; row < canopyLayers - topSkip; row++) {
                double t = (double) row / Math.max(1, canopyLayers - 1);
                int reach = Math.max(1, Math.round((float) (0.3 + spread * (0.20 + 0.76 * t))));
                int dx = (int) Math.round(ux * reach);
                int dz = (int) Math.round(uz * reach);
                int radius = t < 0.25 || row >= canopyLayers - 2
                        ? 1 : Math.min(2, Math.max(1, leafRadius - 1));
                // Skip select exterior layers to leave visible gaps between sprays.
                if (t > 0.32 && (arm + row) % 7 == 0) continue;
                placeLeavesRow(level, setter, random, tree,
                        attachment.pos().below(canopyLayers - 1 - row).offset(dx, 0, dz),
                        radius, 0, attachment.doubleTrunk());
            }
        }

        // Tie the sprays to the actual trunk and branch tip with a small tuft.
        for (int y = 0; y < Math.min(3, canopyLayers); y++) {
            placeLeavesRow(level, setter, random, tree,
                    attachment.pos().below(y), 1, 0, attachment.doubleTrunk());
        }
    }

    @Override public int foliageHeight(RandomSource random, int treeHeight, TreeFeature tree) {
        return canopyLayers;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz,
            int currentRadius, boolean doubleTrunk) {
        if (currentRadius <= 1) return false;
        // Rounded little sprays, never squared-off leaf planes.
        return (double) dx * dx + (double) dz * dz > currentRadius * currentRadius + 0.1
                || (Math.abs(dx) + Math.abs(dz) >= currentRadius * 2 - 1
                    && random.nextInt(6) == 0);
    }
}
