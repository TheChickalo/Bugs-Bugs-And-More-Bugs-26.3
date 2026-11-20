package net.chickalo.bugsbugsandmorebugs.worldgen.tree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

/**
 * Rounded, separately shaped ginkgo foliage clusters, not concentric
 * layers or symmetrical rays. Trunk branches carry compact leaf tufts,
 * with intentional negative space between them.
 */
public final class GinkgoFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<GinkgoFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            foliagePlacerParts(instance).and(Codec.intRange(5, 11)
                    .fieldOf("canopy_layers").forGetter(p -> p.canopyLayers))
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
        // Each attachment represents a woody branch tip. Treat its canopy
        // as a loose spray of distinct small tufts, offset in height and azimuth.
        int clumps = leafRadius <= 2 ? 4 + random.nextInt(2) : 3 + random.nextInt(3);
        double heading = random.nextDouble() * Math.PI * 2.0;
        int driftX = random.nextInt(3) - 1;
        int driftZ = random.nextInt(3) - 1;

        for (int k = 0; k < clumps; k++) {
            double angle = heading + (Math.PI * 2.0 * k / clumps)
                    + (random.nextDouble() - 0.5) * 0.75;
            int reach = 1 + random.nextInt(Math.max(2, leafRadius));
            int dx = (int)Math.round(Math.cos(angle) * reach) + driftX;
            int dz = (int)Math.round(Math.sin(angle) * reach) + driftZ;
            int down = random.nextInt(Math.max(2, canopyLayers - 2));
            BlockPos center = attachment.pos().offset(dx, -down, dz);
            int layers = 2 + random.nextInt(3);
            int breadth = (leafRadius >= 4 && random.nextInt(3) == 0) ? 3 : 2;

            for (int layer = 0; layer < layers; layer++) {
                int y = layer - 1;
                int r = layer == 0 || layer == layers - 1 ? 1 : breadth;
                if (layers == 4 && layer == 2) r = breadth;
                // Small asymmetrical edges; avoid mechanically complete canopies.
                placeLeavesRow(level, setter, random, tree,
                        center.above(y).offset(layer == 2 ? Integer.signum(dx) : 0,
                                0, layer == 2 ? Integer.signum(dz) : 0),
                        r, 0, attachment.doubleTrunk());
            }
        }

        // Tie visible leaves to each branch to keep crowns from looking detached.
        for (int y = 0; y <= 1; y++)
            placeLeavesRow(level, setter, random, tree, attachment.pos().below(y),
                    1, 0, attachment.doubleTrunk());
    }

    @Override public int foliageHeight(RandomSource random, int treeHeight, TreeFeature tree) {
        return canopyLayers;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz,
            int currentRadius, boolean doubleTrunk) {
        if (currentRadius <= 1) return false;
        double dist = dx * dx + dz * dz;
        return dist > (currentRadius + 0.18) * (currentRadius + 0.18)
                || (dist > (currentRadius - 0.42) * (currentRadius - 0.42)
                    && random.nextInt(5) == 0);
    }
}
