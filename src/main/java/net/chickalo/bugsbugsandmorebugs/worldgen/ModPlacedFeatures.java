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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.core.BlockPos;
import java.util.ArrayList;

import java.util.List;


public class ModPlacedFeatures {
    // Smaller biome variants reuse the registered tree / plant configurations.
    public static final ResourceKey<PlacedFeature> GINKGO_ANCIENT_STAND_PLACED_KEY = registerKey("ginkgo_ancient_stand_placed");
    public static final ResourceKey<PlacedFeature> GINKGO_SCATTERED_PLACED_KEY = registerKey("ginkgo_scattered_placed");
    public static final ResourceKey<PlacedFeature> GINKGO_MIXED_PLACED_KEY = registerKey("ginkgo_mixed_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_SPARSE_PLACED_KEY = registerKey("maple_sparse_placed");
    public static final ResourceKey<PlacedFeature> ELSEWHERE_FLORA_DENSE_PLACED_KEY = registerKey("elsewhere_flora_dense_placed");
    public static final ResourceKey<PlacedFeature> WETLAND_REEDS_DENSE_PLACED_KEY = registerKey("wetland_reeds_dense_placed");
    public static final ResourceKey<PlacedFeature> BAMBOO_POOLS_MARSH_PLACED_KEY = registerKey("bamboo_pools_marsh_placed");
    public static final ResourceKey<PlacedFeature> HIGHLAND_MEADOW_LUSH_PLACED_KEY = registerKey("highland_meadow_lush_placed");
    public static final ResourceKey<PlacedFeature> GLOWSHROOM_DENSE_PLACED_KEY = registerKey("glowshroom_dense_placed");
    public static final ResourceKey<PlacedFeature> HUGE_GLOWSHROOM_GROVE_PLACED_KEY = registerKey("huge_glowshroom_grove_placed");
    public static final ResourceKey<PlacedFeature> HINOKI_BOULDER_PLACED_KEY = registerKey("hinoki_boulder_placed");
    public static final ResourceKey<PlacedFeature> VOLCANIC_MAGMA_PATCH_PLACED_KEY = registerKey("volcanic_magma_patch_placed");

    public static final ResourceKey<PlacedFeature> GINKGO_SMALL_PLACED_KEY = registerKey("ginkgo_small_placed");
    public static final ResourceKey<PlacedFeature> GINKGO_TREE_PLACED_KEY = registerKey("ginkgo_tree_placed");
    public static final ResourceKey<PlacedFeature> GINKGO_OLD_GROWTH_PLACED_KEY = registerKey("ginkgo_old_growth_placed");

    public static final ResourceKey<PlacedFeature> BAMBOO_GROVE_PLACED_KEY = registerKey("bamboo_grove_placed");
    public static final ResourceKey<PlacedFeature> BAMBOO_FERNS_PLACED_KEY = registerKey("bamboo_ferns_placed");
    public static final ResourceKey<PlacedFeature> BAMBOO_POOLS_PLACED_KEY = registerKey("bamboo_pools_placed");
    public static final ResourceKey<PlacedFeature> WETLAND_REEDS_PLACED_KEY = registerKey("wetland_reeds_placed");
    public static final ResourceKey<PlacedFeature> ELSEWHERE_FLORA_PLACED_KEY = registerKey("elsewhere_flora_placed");
    public static final ResourceKey<PlacedFeature> LAVA_CLEANUP_PLACED_KEY = registerKey("lava_cleanup_placed");
    public static final ResourceKey<PlacedFeature> HINOKI_TREE_PLACED_KEY = registerKey("hinoki_tree_placed");
    public static final ResourceKey<PlacedFeature> HINOKI_SMALL_PLACED_KEY = registerKey("hinoki_small_placed");
    public static final ResourceKey<PlacedFeature> HINOKI_FERN_PLACED_KEY = registerKey("hinoki_fern_placed");
    public static final ResourceKey<PlacedFeature> HINOKI_GRASS_PLACED_KEY = registerKey("hinoki_grass_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_PLACED_KEY = registerKey("maple_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_LAKE_PLACED_KEY = registerKey("maple_lake_placed");
    public static final ResourceKey<PlacedFeature> LAKE_POND_PLACED_KEY = registerKey("lake_pond_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_BAMBOO_PLACED_KEY = registerKey("maple_bamboo_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_BUSH_PLACED_KEY = registerKey("maple_bush_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_SHORT_GRASS_PLACED_KEY = registerKey("maple_short_grass_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_TALL_GRASS_PLACED_KEY = registerKey("maple_tall_grass_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_LARGE_FERN_PLACED_KEY = registerKey("maple_large_fern_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_RED_SHRUB_PLACED_KEY = registerKey("maple_red_shrub_placed");
    public static final ResourceKey<PlacedFeature> HASKAP_BUSH_PLACED_KEY = registerKey("haskap_bush_placed");
    public static final ResourceKey<PlacedFeature> BARBERRY_BUSH_PLACED_KEY = registerKey("barberry_bush_placed");
    public static final ResourceKey<PlacedFeature> GLOWSHROOM_PATCH_PLACED_KEY = registerKey("glowshroom_patch_placed");
    public static final ResourceKey<PlacedFeature> GLOWSHROOM_TREE_BASE_PLACED_KEY = registerKey("glowshroom_tree_base_placed");
    public static final ResourceKey<PlacedFeature> HUGE_GLOWSHROOM_PLACED_KEY = registerKey("huge_glowshroom_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_WATER_SHORE_PLACED_KEY = registerKey("maple_water_shore_placed");
    public static final ResourceKey<PlacedFeature> MAPLE_RIVERBED_MUD_PLACED_KEY = registerKey("maple_riverbed_mud_placed");
    public static final ResourceKey<PlacedFeature> HIGHLAND_MEADOW_PATCH_PLACED_KEY = registerKey("highland_meadow_patch_placed");
    public static final ResourceKey<PlacedFeature> KARST_OUTCROP_PLACED_KEY = registerKey("karst_outcrop_placed");
    public static final ResourceKey<PlacedFeature> KARST_GROUND_PATCH_PLACED_KEY = registerKey("karst_ground_patch_placed");
    public static final ResourceKey<PlacedFeature> KARST_ANIMAL_TRAIL_PLACED_KEY = registerKey("karst_animal_trail_placed");


    public static final ResourceKey<PlacedFeature> STONE_FOREST_SPIRES_PLACED_KEY = registerKey("stone_forest_spires_placed");
    public static final ResourceKey<PlacedFeature> AMPHIBOLITE_DEPOSIT_PLACED_KEY = registerKey("amphibolite_deposit_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        var configuredFeatures = context.lookup(Registries.FEATURE);

        // Deliberately lighter vegetation densities for minor biome variants.
        register(context, GINKGO_ANCIENT_STAND_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.GINKGO_OLD_GROWTH_KEY),
                VegetationPlacements.treePlacement(CountPlacement.of(1), ModBlocks.GINKGO_SAPLING));
        register(context, GINKGO_SCATTERED_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.GINKGO_SMALL_KEY),
                VegetationPlacements.treePlacement(CountPlacement.of(1), ModBlocks.GINKGO_SAPLING));
        register(context, GINKGO_MIXED_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.GINKGO_TREE_KEY),
                VegetationPlacements.treePlacement(CountPlacement.of(2), ModBlocks.GINKGO_SAPLING));
        register(context, MAPLE_SPARSE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.MAPLE_KEY),
                VegetationPlacements.treePlacement(CountPlacement.of(4), ModBlocks.MAPLE_SAPLING));
        register(context, ELSEWHERE_FLORA_DENSE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.ELSEWHERE_FLORA_KEY),
                List.of(CountPlacement.of(4), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        register(context, WETLAND_REEDS_DENSE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.WETLAND_REEDS_KEY),
                List.of(CountPlacement.of(3), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        register(context, BAMBOO_POOLS_MARSH_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.BAMBOO_POOLS_KEY),
                List.of(RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        register(context, HIGHLAND_MEADOW_LUSH_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.HIGHLAND_MEADOW_PATCH_KEY),
                List.of(CountPlacement.of(6), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        register(context, GLOWSHROOM_DENSE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.GLOWSHROOM_PATCH_KEY),
                List.of(CountPlacement.of(3), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        register(context, HUGE_GLOWSHROOM_GROVE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.HUGE_GLOWSHROOM_KEY),
                List.of(RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        register(context, VOLCANIC_MAGMA_PATCH_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.VOLCANIC_MAGMA_PATCH_KEY),
                List.of(RarityFilter.onAverageOnceEvery(12), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        register(context, HINOKI_BOULDER_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.HINOKI_BOULDER_KEY),
                List.of(RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));



        register(context, BAMBOO_GROVE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.MAPLE_BAMBOO_KEY),
                List.of(CountPlacement.of(32), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        register(context, BAMBOO_FERNS_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.LARGE_FERN),
                List.of(CountPlacement.of(10), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        register(context, BAMBOO_POOLS_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.BAMBOO_POOLS_KEY),
                List.of(RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        register(context, WETLAND_REEDS_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.WETLAND_REEDS_KEY),
                List.of(CountPlacement.of(1), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));


        register(context, LAVA_CLEANUP_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.LAVA_CLEANUP_KEY),
                List.of(CountPlacement.of(1), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, ELSEWHERE_FLORA_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.ELSEWHERE_FLORA_KEY),
                List.of(CountPlacement.of(1), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // Amphibolite: clusters in metamorphic country rock, like tuff deposits.
        // Tall Stone Forest pinnacles; intentionally biome-limited.
        register(context, STONE_FOREST_SPIRES_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.STONE_FOREST_SPIRES_KEY),
                List.of(
                        CountPlacement.of(2),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));

        register(context, AMPHIBOLITE_DEPOSIT_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.AMPHIBOLITE_DEPOSIT_KEY),
                List.of(
                        CountPlacement.of(3),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(
                                net.minecraft.world.level.levelgen.VerticalAnchor.absolute(-55),
                                net.minecraft.world.level.levelgen.VerticalAnchor.absolute(-18)),
                        BiomeFilter.biome()
                ));


        // Ginkgo spacing: fewer trees than the Maple canopy so the golden crowns read clearly.
        register(context, GINKGO_SMALL_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.GINKGO_SMALL_KEY),
                VegetationPlacements.treePlacement(CountPlacement.of(3), ModBlocks.GINKGO_SAPLING));
        register(context, GINKGO_TREE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.GINKGO_TREE_KEY),
                VegetationPlacements.treePlacement(CountPlacement.of(5), ModBlocks.GINKGO_SAPLING));
        // A few monumental specimens per forest, not one in every chunk.
        register(context, GINKGO_OLD_GROWTH_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.GINKGO_OLD_GROWTH_KEY),
                List.of(RarityFilter.onAverageOnceEvery(9), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // Dense evergreen woodland; separate from Maple Forest's tree attempts.
        register(context, HINOKI_TREE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.HINOKI_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(7, 0.2f, 2),
                        ModBlocks.HINOKI_SAPLING));

        register(context, HINOKI_SMALL_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.HINOKI_SMALL_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.25f, 1),
                        ModBlocks.HINOKI_SAPLING));

        // Small understory clusters instead of Maple's near-solid grass coverage.
        register(context, HINOKI_FERN_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.LARGE_FERN),
                List.of(CountPlacement.of(5), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        register(context, HINOKI_GRASS_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.GRASS),
                List.of(CountPlacement.of(3), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // A dense canopy: roughly three times the previous baseline attempt count.
        register(context, MAPLE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.MAPLE_KEY),
                        VegetationPlacements.treePlacement(PlacementUtils.countExtra(12, 0.1f, 3),
                        ModBlocks.MAPLE_SAPLING));


        // Bamboo keeps vanilla's growth configuration and noise distribution,
        // but appears in occasional groves rather than throughout every chunk.
        register(context, MAPLE_BAMBOO_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.MAPLE_BAMBOO_KEY),
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
                        CountPlacement.of(548),
                        OffsetPlacement.ofTriangle(5, 3),
                        BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
                ));

        // Haskap and barberry grow in distinct natural clusters throughout the understory.
        register(context, HASKAP_BUSH_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.HASKAP_BUSH_PATCH_KEY),
                        List.of(
                        CountPlacement.of(2),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));

        register(context, BARBERRY_BUSH_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.BARBERRY_BUSH_PATCH_KEY),
                        List.of(
                        CountPlacement.of(2),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));

        // Grass is the continuous base layer; the larger count creates near-continuous patches.
        register(context, MAPLE_SHORT_GRASS_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.GRASS),
                        List.of(
                        CountPlacement.of(3),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome(),
                        CountPlacement.of(248),
                        OffsetPlacement.ofTriangle(7, 3),
                        BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
                ));

        // Tall grass and large ferns form a dense, bonemeal-like understory.
        register(context, MAPLE_TALL_GRASS_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.TALL_GRASS),
                        List.of(
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome(),
                        CountPlacement.of(170),
                        OffsetPlacement.ofTriangle(7, 3),
                        BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
                ));

        register(context, MAPLE_LARGE_FERN_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.LARGE_FERN),
                        List.of(
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome(),
                        CountPlacement.of(150),
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
                        CountPlacement.of(20),
                        OffsetPlacement.ofTriangle(7, 3),
                        BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
                        ));


        // Occasional surface lakes with a mixed gravel, andesite, and stone shoreline.
        register(context, LAKE_POND_PLACED_KEY, configuredFeatures.getOrThrow(ModFeatures.MAPLE_LAKE_KEY),
                List.of(RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, MAPLE_LAKE_PLACED_KEY, configuredFeatures.getOrThrow(ModFeatures.MAPLE_LAKE_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(6),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));

        register(context, GLOWSHROOM_PATCH_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.GLOWSHROOM_PATCH_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(7),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));


        register(context, GLOWSHROOM_TREE_BASE_PLACED_KEY,
                configuredFeatures.getOrThrow(
                        ModFeatures.GLOWSHROOM_TREE_BASE_KEY
                ),
                List.of(
                        CountPlacement.of(12),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                )
        );


        register(context, HUGE_GLOWSHROOM_PLACED_KEY,
                configuredFeatures.getOrThrow(
                        ModFeatures.HUGE_GLOWSHROOM_KEY
                ),
                List.of(
                        RarityFilter.onAverageOnceEvery(24),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                )
        );

        register(context, MAPLE_WATER_SHORE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.MAPLE_WATER_SHORE_KEY),
                List.of(
                        CountPlacement.of(8),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));

        register(context, MAPLE_RIVERBED_MUD_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.MAPLE_RIVERBED_MUD_KEY),
                List.of(
                        CountPlacement.of(1),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE
                ));

        register(context,
                HIGHLAND_MEADOW_PATCH_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.HIGHLAND_MEADOW_PATCH_KEY),
                List.of(
                        CountPlacement.of(3),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                )
        );

        register(context,
                KARST_OUTCROP_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.KARST_OUTCROP_KEY),
                List.of(
                        CountPlacement.of(2),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                )
        );

        register(context,
                KARST_GROUND_PATCH_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.KARST_GROUND_PATCH_KEY),
                List.of(
                        CountPlacement.of(1),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                )
        );

        register(context,
                KARST_ANIMAL_TRAIL_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.KARST_ANIMAL_TRAIL_KEY),
                List.of(
                        CountPlacement.of(1),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                )
        );

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

    private static List<PlacementModifier> withGroundCheck(
            List<PlacementModifier> modifiers) {

        List<PlacementModifier> result =
                new ArrayList<>(modifiers);

        result.add(
                BlockPredicateFilter.forPredicate(
                        BlockPredicate.matchesBlocks(
                                Blocks.STONE,
                                Blocks.DIRT,
                                Blocks.GRASS_BLOCK
                        )                )
        );

        return List.copyOf(result);
    }
}
