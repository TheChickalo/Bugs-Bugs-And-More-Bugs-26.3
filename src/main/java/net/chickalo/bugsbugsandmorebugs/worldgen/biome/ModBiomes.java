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
    public static final ResourceKey<Biome> UNDERHANG = registerBiomeKey("underhang");
    public static final ResourceKey<Biome> MAPLE_FOREST = registerBiomeKey("maple_forest");
    public static final ResourceKey<Biome> HIGHLAND_KARST = registerBiomeKey("highland_karst");


    // New Elsewhere biome families. See README for climate-adjacency limitations.
    public static final ResourceKey<Biome> STONE_FOREST = registerBiomeKey("stone_forest");
    public static final ResourceKey<Biome> GINKGO_FOREST = registerBiomeKey("ginkgo_forest");
    public static final ResourceKey<Biome> HINOKI_HIGHLANDS = registerBiomeKey("hinoki_highlands");
    public static final ResourceKey<Biome> VOLCANIC_PEAK = registerBiomeKey("volcanic_peak");

    public static void registerBiomes() {
        // Elsewhere biomes are not injected into the Overworld.
    }

    public static void bootstrap(BootstrapContext<Biome> context) {
        var carvers = context.lookup(Registries.CARVER);
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        register(context, STONE_FOREST, ModOverworldBiomes.stoneForest(placedFeatures, carvers));
        register(context, GINKGO_FOREST, ModOverworldBiomes.ginkgoForest(placedFeatures, carvers));
        register(context, HINOKI_HIGHLANDS, ModOverworldBiomes.hinokiHighlands(placedFeatures, carvers));
        register(context, VOLCANIC_PEAK, ModOverworldBiomes.volcanicPeak(placedFeatures, carvers));
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
