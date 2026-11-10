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
 * Each biome-facing placement has its own ResourceKey. This is important for
 * features such as bamboo: Maple Forest gets a mod-owned placed-feature key
 * that points to the vanilla configured feature, rather than sharing the
 * vanilla Bamboo Jungle placed-feature graph node.
 */
public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> MAPLE_PLACED_KEY = registerKey("maple_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_LAKE_PLACED_KEY = registerKey("maple_lake_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_BAMBOO_PLACED_KEY = registerKey("maple_bamboo_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_BUSH_PLACED_KEY = registerKey("maple_bush_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_SHORT_GRASS_PLACED_KEY = registerKey("maple_short_grass_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_TALL_GRASS_PLACED_KEY = registerKey("maple_tall_grass_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_LARGE_FERN_PLACED_KEY = registerKey("maple_large_fern_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_RED_SHRUB_PLACED_KEY = registerKey("maple_red_shrub_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        var configuredFeatures = context.lookup(Registries.FEATURE);

        // A dense canopy: roughly three times the previous baseline attempt count.
        register(context, MAPLE_PLACED_KEY, configuredFeatures.getOrThrow(ModFeatures.MAPLE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(12, 0.1f, 3),
                        ModBlocks.MAPLE_SAPLING));

        // Occasional surface lakes with a mixed gravel, andesite, and stone shoreline.
        register(context, MAPLE_LAKE_PLACED_KEY, configuredFeatures.getOrThrow(ModFeatures.MAPLE_LAKE_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(10),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));

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

        // Frequent low shrubs create a dense understory.
        register(context, MAPLE_BUSH_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.BUSH),
                List.of(
                        RarityFilter.onAverageOnceEvery(2),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome(),
                        CountPlacement.of(96),
                        OffsetPlacement.ofTriangle(5, 3),
                        BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
                ));

        // Grass is the continuous base layer; the larger count creates near-continuous patches.
        register(context, MAPLE_SHORT_GRASS_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.GRASS),
                List.of(
                        CountPlacement.of(3),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome(),
                        CountPlacement.of(48),
                        OffsetPlacement.ofTriangle(7, 3),
                        BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
                ));

        // Tall grass and ferns fill gaps between the low ground cover.
        register(context, MAPLE_TALL_GRASS_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.TALL_GRASS),
                List.of(
                        RarityFilter.onAverageOnceEvery(2),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome(),
                        CountPlacement.of(80),
                        OffsetPlacement.ofTriangle(7, 3),
                        BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
                ));

        register(context, MAPLE_LARGE_FERN_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.LARGE_FERN),
                List.of(
                        RarityFilter.onAverageOnceEvery(2),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome(),
                        CountPlacement.of(80),
                        OffsetPlacement.ofTriangle(7, 3),
                        BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
                ));

        // Red shrubs become a visible accent rather than an occasional rarity.
        register(context, MAPLE_RED_SHRUB_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.RED_SHRUB),
                List.of(
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome(),
                        CountPlacement.of(24),
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
