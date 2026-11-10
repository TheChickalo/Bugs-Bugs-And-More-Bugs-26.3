package net.chickalo.bugsbugsandmorebugs.worldgen;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

/**
 * Placed-feature registrations for the mod's biomes.
 *
 * Each biome-facing placement has its own ResourceKey. In particular, Maple
 * Forest does not directly reference the vanilla BAMBOO placed-feature key.
 * It registers a mod-owned placed feature that points to vanilla's bamboo
 * configured feature, avoiding reuse of the same placed-feature graph node.
 */
public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> MAPLE_PLACED_KEY = registerKey("maple_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_BAMBOO_PLACED_KEY = registerKey("maple_bamboo_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_BUSH_PLACED_KEY = registerKey("maple_bush_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_TALL_GRASS_PLACED_KEY = registerKey("maple_tall_grass_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_LARGE_FERN_PLACED_KEY = registerKey("maple_large_fern_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        var configuredFeatures = context.lookup(Registries.FEATURE);

        // Main canopy: slightly denser than the previous 3-attempt baseline.
        register(context, MAPLE_PLACED_KEY, configuredFeatures.getOrThrow(ModFeatures.MAPLE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(4, 0.1f, 2),
                        ModBlocks.MAPLE_SAPLING));

        // Bamboo keeps vanilla's growth configuration and noise distribution,
        // but appears in occasional groves rather than throughout every chunk.
        register(context, MAPLE_BAMBOO_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.BAMBOO_SOME_PODZOL),
                List.of(
                        RarityFilter.onAverageOnceEvery(4),
                        NoiseBasedCountPlacement.of(160, 80.0, 0.3),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));

        // These placements follow vanilla's patch placement patterns.
        register(context, MAPLE_BUSH_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.BUSH),
                List.of(
                        RarityFilter.onAverageOnceEvery(4),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome(),
                        CountPlacement.of(24),
                        OffsetPlacement.ofTriangle(5, 3),
                        BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
                ));

        register(context, MAPLE_TALL_GRASS_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.TALL_GRASS),
                List.of(
                        RarityFilter.onAverageOnceEvery(5),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome(),
                        CountPlacement.of(96),
                        OffsetPlacement.ofTriangle(7, 3),
                        BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
                ));

        register(context, MAPLE_LARGE_FERN_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.LARGE_FERN),
                List.of(
                        RarityFilter.onAverageOnceEvery(5),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome(),
                        CountPlacement.of(96),
                        OffsetPlacement.ofTriangle(7, 3),
                        BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
                ));
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name));
    }

    private static void register(BootstrapContext<PlacedFeature> context,
                                 ResourceKey<PlacedFeature> key,
                                 Holder<Feature> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
