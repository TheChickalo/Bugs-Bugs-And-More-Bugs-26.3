package net.chickalo.bugsbugsandmorebugs;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.entity.ModEntities;
import net.chickalo.bugsbugsandmorebugs.entity.custom.AntWorkerEntity;
import net.chickalo.bugsbugsandmorebugs.item.ModItems;
import net.chickalo.bugsbugsandmorebugs.registries.ModFlammableBlocks;
import net.chickalo.bugsbugsandmorebugs.registries.ModStrippableBlocks;
import net.chickalo.bugsbugsandmorebugs.sound.ModSounds;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.chickalo.bugsbugsandmorebugs.worldgen.tree.ModFoliagePlacerTypes;
import net.chickalo.bugsbugsandmorebugs.worldgen.tree.ModTrunkPlacerTypes;
import net.chickalo.bugsbugsandmorebugs.worldgen.tree.ModTreeDecoratorTypes;
import net.chickalo.bugsbugsandmorebugs.worldgen.feature.ModFeatureTypes;
import net.fabricmc.api.ModInitializer;
import net.chickalo.bugsbugsandmorebugs.worldgen.dimension.ModDimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import terrablender.api.TerraBlenderApi;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.chickalo.bugsbugsandmorebugs.worldgen.ModPlacedFeatures;

public class BugsBugsAndMoreBugs implements ModInitializer, TerraBlenderApi {
    public static final String MOD_ID = "bugsbugsandmorebugs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModSounds.registerSound();
        ModEntities.registerModEntities();
        ModFlammableBlocks.registerFlammableBlocks();
        ModStrippableBlocks.registerStrippableBlocks();
        ModTrunkPlacerTypes.registerModTrunkPlacerTypes();
        ModFoliagePlacerTypes.registerModFoliagePlacerTypes();
        ModTreeDecoratorTypes.register();
        ModFeatureTypes.register();
        // Genuine vanilla rain is global in Minecraft 26.3, not a separate
        // per-biome weather state. Refresh it while someone is in Elsewhere.
        // No commands or game rules are modified in the player's other worlds.
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if(server.getTickCount()%200!=0)return;
            boolean exploringElsewhere=server.getPlayerList().getPlayers().stream()
                    .anyMatch(player -> player.level().dimension()
                            .equals(ModDimensions.ELSEWHEREDIM_LEVEL_KEY));
            if(exploringElsewhere)server.setWeatherParameters(0,12000,true,false);
        });
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.RIVER),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.MAPLE_RIVERBED_MUD_PLACED_KEY
        );
        FabricDefaultAttributeRegistry.register(
                ModEntities.ANTWORKER,
                AntWorkerEntity.createAttributes()
        );
    }

    @Override
    public void onTerraBlenderInitialized() {
        ModBiomes.registerBiomes();
    }
}
