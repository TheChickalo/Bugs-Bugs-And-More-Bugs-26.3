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
    public static final ResourceKey<Biome> MAPLE_FOREST = registerBiomeKey("maple_forest");

    public static void registerBiomes() {
        Regions.register(new OverworldRegion(Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "bugsbugsandmorebugs_overworld"), 20));
    }

    public static void bootstrap(BootstrapContext<Biome> context) {
        var carvers = context.lookup(Registries.CARVER);
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        register(context, MAPLE_FOREST , ModOverworldBiomes.mapleForest(placedFeatures, carvers));
    }

    private static void register(BootstrapContext<Biome> context, ResourceKey<Biome> key, Biome biome) {
        context.register(key, biome);
    }

    private static ResourceKey<Biome> registerBiomeKey(String name) {
        return ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name));
    }
}
