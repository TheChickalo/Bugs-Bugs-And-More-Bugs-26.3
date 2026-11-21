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
 * Compact round ginkgo crowns carried by physical wooden limbs.
 * Complete top/bottom leaf caps cover branch tips; circular middle layers
 * avoid the wide, flat, blocky blob shape of the earlier foliage placer.
 */
public final class GinkgoFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<GinkgoFoliagePlacer> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    foliagePlacerParts(instance).and(Codec.intRange(5, 11)
                            .fieldOf("canopy_layers").forGetter(p -> p.canopyLayers))
                            .apply(instance, GinkgoFoliagePlacer::new));

    private final int canopyLayers;

    public GinkgoFoliagePlacer(IntProvider radius, IntProvider offset, int canopyLayers) {
        super(radius, offset);
        this.canopyLayers = canopyLayers;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return ModFoliagePlacerTypes.GINKGO_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(WorldGenLevel level, FoliageSetter setter,
            RandomSource random, TreeFeature tree, int treeHeight,
            FoliageAttachment attachment, int foliageHeight, int leafRadius, int offset) {
        int radius = Math.max(1, Math.min(3, leafRadius));
        BlockPos center = attachment.pos();

        // A round, solid, stepped tuft; never leave its highest log tip exposed.
        // Overlapping tufts form a naturally lobed crown around separate limbs.
        for (int y = -2; y <= 2; y++) {
            int r = Math.abs(y) == 2 ? Math.max(1, radius - 1) : radius;
            if (Math.abs(y) == 1 && radius == 3) r = 2;
            placeLeavesRow(level, setter, random, tree,
                    center.above(y), r, 0, attachment.doubleTrunk());
        }
    }

    @Override
    public int foliageHeight(RandomSource random, int treeHeight, TreeFeature tree) {
        return canopyLayers;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int dx, int y,
            int dz, int currentRadius, boolean doubleTrunk) {
        // Circular cross-sections, not complete square 5x5 and 7x7 layers.
        return dx * dx + dz * dz > (currentRadius + 0.15) * (currentRadius + 0.15);
    }
}
