package net.chickalo.bugsbugsandmorebugs.worldgen.biome;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.region.NetherRegion;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.region.OverworldRegion;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import terrablender.api.EndBiomeRegistry;
import terrablender.api.Regions;

public class ModBiomes {
    public static final ResourceKey<Biome> BAMBOO_LOWLANDS = registerBiomeKey("bamboo_lowlands");
    public static final ResourceKey<Biome> GRASSY_PLAINS = registerBiomeKey("grassy_plains");
    public static final ResourceKey<Biome> OCEAN = registerBiomeKey("ocean");
    public static final ResourceKey<Biome> RIVER = registerBiomeKey("river");
    public static final ResourceKey<Biome> ESTUARY = registerBiomeKey("estuary");

    public static final ResourceKey<Biome> UNDERHANG = registerBiomeKey("underhang");
    public static final ResourceKey<Biome> MAPLE_FOREST = registerBiomeKey("maple_forest");
    public static final ResourceKey<Biome> HIGHLAND_KARST = registerBiomeKey("highland_karst");


    // New Elsewhere biome families. See README for climate-adjacency limitations.
    public static final ResourceKey<Biome> OLD_GROWTH_GINKGO_FOREST = registerBiomeKey("old_growth_ginkgo_forest");
    public static final ResourceKey<Biome> SPARSE_GINKGO_WOODLAND = registerBiomeKey("sparse_ginkgo_woodland");
    public static final ResourceKey<Biome> MIXED_AUTUMN_FOREST = registerBiomeKey("mixed_autumn_forest");
    public static final ResourceKey<Biome> MOSSY_HINOKI_GROVE = registerBiomeKey("mossy_hinoki_grove");
    public static final ResourceKey<Biome> BAMBOO_MARSH = registerBiomeKey("bamboo_marsh");
    public static final ResourceKey<Biome> HIGHLAND_WILDFLOWER_MEADOW = registerBiomeKey("highland_wildflower_meadow");
    public static final ResourceKey<Biome> LIMESTONE_SCRUBLAND = registerBiomeKey("limestone_scrubland");
    public static final ResourceKey<Biome> GLOWSHROOM_GROVE = registerBiomeKey("glowshroom_grove");
    public static final ResourceKey<Biome> ASHEN_FOOTHILLS = registerBiomeKey("ashen_foothills");
    public static final ResourceKey<Biome> MISTY_KARST_FEN = registerBiomeKey("misty_karst_fen");

    public static final ResourceKey<Biome> STONE_FOREST = registerBiomeKey("stone_forest");
    public static final ResourceKey<Biome> GINKGO_FOREST = registerBiomeKey("ginkgo_forest");
    public static final ResourceKey<Biome> HINOKI_HIGHLANDS = registerBiomeKey("hinoki_highlands");
    public static final ResourceKey<Biome> VOLCANIC_PEAK = registerBiomeKey("volcanic_peak");
    public static final ResourceKey<Biome> SNOWY_PEAK = registerBiomeKey("snowy_peak");

    public static final ResourceKey<Biome> WISTERIA_VEILWOOD = registerBiomeKey("wisteria_veilwood");
    public static final ResourceKey<Biome> WILLOW_BACKWATERS = registerBiomeKey("willow_backwaters");
    public static final ResourceKey<Biome> ANCIENT_CEDAR_RAINFOREST = registerBiomeKey("ancient_cedar_rainforest");
    public static final ResourceKey<Biome> PERSIMMON_WOODLANDS = registerBiomeKey("persimmon_woodlands");
    public static final ResourceKey<Biome> SILKFLOWER_SAVANNA = registerBiomeKey("silkflower_savanna");

    // Named forest sub-biomes: actual registry entries, discoverable by /locate biome.
    public static final ResourceKey<Biome> VIOLET_CURTAINS = registerBiomeKey("violet_curtains");
    public static final ResourceKey<Biome> TANGLED_ARBORS = registerBiomeKey("tangled_arbors");
    public static final ResourceKey<Biome> PETAL_GLADES = registerBiomeKey("petal_glades");

    public static final ResourceKey<Biome> OXBOW_BANKS = registerBiomeKey("oxbow_banks");
    public static final ResourceKey<Biome> SEDGE_POOLS = registerBiomeKey("sedge_pools");
    public static final ResourceKey<Biome> WILLOW_HUMMOCKS = registerBiomeKey("willow_hummocks");

    public static final ResourceKey<Biome> ELDER_STANDS = registerBiomeKey("elder_stands");
    public static final ResourceKey<Biome> NURSE_LOG_HOLLOWS = registerBiomeKey("nurse_log_hollows");
    public static final ResourceKey<Biome> RAINWASHED_GULLIES = registerBiomeKey("rainwashed_gullies");

    public static final ResourceKey<Biome> AMBER_GROVES = registerBiomeKey("amber_groves");
    public static final ResourceKey<Biome> GOLDEN_MEADOWS = registerBiomeKey("golden_meadows");
    public static final ResourceKey<Biome> BRAMBLE_TERRACES = registerBiomeKey("bramble_terraces");

    public static final ResourceKey<Biome> BLOSSOM_DRIFTS = registerBiomeKey("blossom_drifts");
    public static final ResourceKey<Biome> FEATHERGRASS_PRAIRIE = registerBiomeKey("feathergrass_prairie");
    public static final ResourceKey<Biome> SUNWASHED_RISES = registerBiomeKey("sunwashed_rises");

    public static void registerBiomes() {
        // Elsewhere biomes are not injected into the Overworld.
    }

    public static void bootstrap(BootstrapContext<Biome> context) {
        var carvers = context.lookup(Registries.CARVER);
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        register(context, WISTERIA_VEILWOOD, ModOverworldBiomes.wisteriaVeilwood(placedFeatures, carvers));
        register(context, WILLOW_BACKWATERS, ModOverworldBiomes.willowBackwaters(placedFeatures, carvers));
        register(context, ANCIENT_CEDAR_RAINFOREST, ModOverworldBiomes.ancientCedarRainforest(placedFeatures, carvers));
        register(context, PERSIMMON_WOODLANDS, ModOverworldBiomes.persimmonWoodlands(placedFeatures, carvers));
        register(context, SILKFLOWER_SAVANNA, ModOverworldBiomes.silkflowerSavanna(placedFeatures, carvers));

        // Each local microhabitat uses the parent species and flora registry,
        // but its own biome ID. ForestHabitatFeature then chooses the exact motif.
        register(context, VIOLET_CURTAINS, ModOverworldBiomes.wisteriaVeilwood(placedFeatures, carvers));
        register(context, TANGLED_ARBORS, ModOverworldBiomes.wisteriaVeilwood(placedFeatures, carvers));
        register(context, PETAL_GLADES, ModOverworldBiomes.wisteriaVeilwood(placedFeatures, carvers));
        register(context, OXBOW_BANKS, ModOverworldBiomes.willowBackwaters(placedFeatures, carvers));
        register(context, SEDGE_POOLS, ModOverworldBiomes.willowBackwaters(placedFeatures, carvers));
        register(context, WILLOW_HUMMOCKS, ModOverworldBiomes.willowBackwaters(placedFeatures, carvers));
        register(context, ELDER_STANDS, ModOverworldBiomes.ancientCedarRainforest(placedFeatures, carvers));
        register(context, NURSE_LOG_HOLLOWS, ModOverworldBiomes.ancientCedarRainforest(placedFeatures, carvers));
        register(context, RAINWASHED_GULLIES, ModOverworldBiomes.ancientCedarRainforest(placedFeatures, carvers));
        register(context, AMBER_GROVES, ModOverworldBiomes.persimmonWoodlands(placedFeatures, carvers));
        register(context, GOLDEN_MEADOWS, ModOverworldBiomes.persimmonWoodlands(placedFeatures, carvers));
        register(context, BRAMBLE_TERRACES, ModOverworldBiomes.persimmonWoodlands(placedFeatures, carvers));
        register(context, BLOSSOM_DRIFTS, ModOverworldBiomes.silkflowerSavanna(placedFeatures, carvers));
        register(context, FEATHERGRASS_PRAIRIE, ModOverworldBiomes.silkflowerSavanna(placedFeatures, carvers));
        register(context, SUNWASHED_RISES, ModOverworldBiomes.silkflowerSavanna(placedFeatures, carvers));

        register(context, OLD_GROWTH_GINKGO_FOREST, ModOverworldBiomes.oldGrowthGinkgoForest(placedFeatures, carvers));
        register(context, SPARSE_GINKGO_WOODLAND, ModOverworldBiomes.sparseGinkgoWoodland(placedFeatures, carvers));
        register(context, MIXED_AUTUMN_FOREST, ModOverworldBiomes.mixedAutumnForest(placedFeatures, carvers));
        register(context, MOSSY_HINOKI_GROVE, ModOverworldBiomes.mossyHinokiGrove(placedFeatures, carvers));
        register(context, BAMBOO_MARSH, ModOverworldBiomes.bambooMarsh(placedFeatures, carvers));
        register(context, HIGHLAND_WILDFLOWER_MEADOW, ModOverworldBiomes.highlandWildflowerMeadow(placedFeatures, carvers));
        register(context, LIMESTONE_SCRUBLAND, ModOverworldBiomes.limestoneScrubland(placedFeatures, carvers));
        register(context, GLOWSHROOM_GROVE, ModOverworldBiomes.glowshroomGrove(placedFeatures, carvers));
        register(context, ASHEN_FOOTHILLS, ModOverworldBiomes.ashenFoothills(placedFeatures, carvers));
        register(context, MISTY_KARST_FEN, ModOverworldBiomes.mistyKarstFen(placedFeatures, carvers));

        register(context, STONE_FOREST, ModOverworldBiomes.stoneForest(placedFeatures, carvers));
        register(context, GINKGO_FOREST, ModOverworldBiomes.ginkgoForest(placedFeatures, carvers));
        register(context, HINOKI_HIGHLANDS, ModOverworldBiomes.hinokiHighlands(placedFeatures, carvers));
        register(context, VOLCANIC_PEAK, ModOverworldBiomes.volcanicPeak(placedFeatures, carvers));
        register(context, SNOWY_PEAK, ModOverworldBiomes.snowyPeak(placedFeatures, carvers));
        register(context, BAMBOO_LOWLANDS, ModOverworldBiomes.bambooLowlands(placedFeatures, carvers));
        register(context, GRASSY_PLAINS, ModOverworldBiomes.grassyPlains(placedFeatures, carvers));
        register(context, OCEAN, ModOverworldBiomes.ocean(placedFeatures, carvers));
        register(context, RIVER, ModOverworldBiomes.river(placedFeatures, carvers));
        register(context, ESTUARY, ModOverworldBiomes.estuary(placedFeatures, carvers));
        register(context, UNDERHANG, ModOverworldBiomes.underhang(placedFeatures, carvers));
        register(context, MAPLE_FOREST , ModOverworldBiomes.mapleForest(placedFeatures, carvers));
        register(context, HIGHLAND_KARST, ModOverworldBiomes.highlandKarst(placedFeatures, carvers)
        );

    }

    private static void register(BootstrapContext<Biome> context, ResourceKey<Biome> key, Biome biome) {
        context.register(key, biome);
    }

    private static ResourceKey<Biome> registerBiomeKey(String name) {
        return ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name));
    }
}
