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
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

/**
 * Placed-feature registrations for the mod's biomes.
 *
 * Each biome-facing placement has its own ResourceKey. In particular, do not
 * add the vanilla BAMBOO placed-feature key directly to Maple Forest: using a
 * mod-owned key lets this biome place the vanilla bamboo configured feature
 * without sharing the same placed-feature node with Bamboo Jungle.
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

        // Reuse vanilla configured features, but give each placement a unique key.
        register(context, MAPLE_BAMBOO_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.BAMBOO_SOME_PODZOL),
                List.of(
                        NoiseBasedCountPlacement.of(32, 80.0, 0.3),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));

        register(context, MAPLE_BUSH_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.BUSH),
                VegetationPlacements.worldSurfaceSquaredWithCount(5));

        register(context, MAPLE_TALL_GRASS_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.TALL_GRASS),
                VegetationPlacements.worldSurfaceSquaredWithCount(24));

        register(context, MAPLE_LARGE_FERN_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.LARGE_FERN),
                VegetationPlacements.worldSurfaceSquaredWithCount(5));
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
