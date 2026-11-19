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

/** Tapered Hinoki canopy, with full conical tiers instead of random leaf blobs. */
public final class HinokiFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<HinokiFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            foliagePlacerParts(instance)
                    .and(Codec.intRange(10, 24).fieldOf("layers").forGetter(p -> p.layers))
                    .apply(instance, HinokiFoliagePlacer::new));

    private final int layers;

    public HinokiFoliagePlacer(IntProvider radius, IntProvider offset, int layers) {
        super(radius, offset);
        this.layers = layers;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return ModFoliagePlacerTypes.HINOKI_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(WorldGenLevel level, FoliageSetter setter, RandomSource random,
                                 TreeFeature tree, int treeHeight, FoliageAttachment attachment,
                                 int foliageHeight, int leafRadius, int offset) {
        // Top starts at radius 1. The lower rows widen toward a broad base.
        // Every fourth row is tucked inward to create visible vanilla-style tiers.
        for (int row = 0; row < foliageHeight; row++) {
            int radius = Math.min(leafRadius, 1 + row / 3);
            if (row >= 4 && row % 4 == 3) {
                radius = Math.max(1, radius - 1);
            }
            placeLeavesRow(level, setter, random, tree, attachment.pos().below(row),
                    radius, 0, attachment.doubleTrunk());
        }
    }

    @Override
    public int foliageHeight(RandomSource random, int treeHeight, TreeFeature tree) {
        return Math.min(layers, Math.max(10, treeHeight - 2));
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz,
                                         int currentRadius, boolean doubleTrunk) {
        int x = Math.abs(dx), z = Math.abs(dz);
        return (x == currentRadius && z == currentRadius)
                || ((x == currentRadius || z == currentRadius) && random.nextInt(18) == 0);
    }
}
