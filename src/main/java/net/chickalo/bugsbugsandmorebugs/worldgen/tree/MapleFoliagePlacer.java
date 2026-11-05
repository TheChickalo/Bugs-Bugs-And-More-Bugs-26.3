
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

public class MapleFoliagePlacer extends FoliagePlacer {

    public static final MapCodec<MapleFoliagePlacer> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    foliagePlacerParts(instance).and(
                            Codec.intRange(0, 16)
                                    .fieldOf("height")
                                    .forGetter(inst -> inst.height)
                    ).apply(instance, MapleFoliagePlacer::new));

    private final int height;

    public MapleFoliagePlacer(
            IntProvider radius,
            IntProvider offset,
            int height) {
        super(radius, offset);
        this.height = height;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return ModFoliagePlacerTypes.MAPLE_FOLIAGE_PLACER_FOLIAGE_PLACER_TYPE;
    }

    @Override
    protected void createFoliage(
            WorldGenLevel level,
            FoliageSetter foliageSetter,
            RandomSource random,
            TreeFeature config,
            int treeHeight,
            FoliageAttachment foliageAttachment,
            int foliageHeight,
            int leafRadius,
            int offset) {

        int[] canopyRadii = {2, 3, 4, 3, 1};

        for (int i = 0; i < canopyRadii.length; i++) {
            int radius = canopyRadii[i];

            placeLeavesRow(
                    level,
                    foliageSetter,
                    random,
                    config,
                    foliageAttachment.pos().above(i),
                    radius,
                    0,
                    foliageAttachment.doubleTrunk()
            );
        }
    }

    @Override
    public int foliageHeight(
            RandomSource random,
            int treeHeight,
            TreeFeature tree) {
        return Math.max(height, 5);
    }

    @Override
    protected boolean shouldSkipLocation(
            RandomSource random,
            int dx,
            int y,
            int dz,
            int currentRadius,
            boolean doubleTrunk) {

        int x = Math.abs(dx);
        int z = Math.abs(dz);

        // Remove sharp square corners.
        if (x == currentRadius && z == currentRadius) {
            return true;
        }

        // Give the outer canopy a slightly uneven silhouette.
        if ((x == currentRadius || z == currentRadius)
                && random.nextFloat() < 0.10f) {
            return true;
        }

        return false;
    }
}
