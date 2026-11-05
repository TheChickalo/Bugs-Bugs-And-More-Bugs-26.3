package net.chickalo.bugsbugsandmorebugs;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.entity.ModEntities;
import net.chickalo.bugsbugsandmorebugs.entity.custom.AntWorkerEntity;
import net.chickalo.bugsbugsandmorebugs.item.ModItems;
import net.chickalo.bugsbugsandmorebugs.registries.ModFlammableBlocks;
import net.chickalo.bugsbugsandmorebugs.registries.ModStrippableBlocks;
import net.chickalo.bugsbugsandmorebugs.sound.ModSounds;
import net.chickalo.bugsbugsandmorebugs.worldgen.tree.ModFoliagePlacerTypes;
import net.chickalo.bugsbugsandmorebugs.worldgen.tree.ModTrunkPlacerTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BugsBugsAndMoreBugs implements ModInitializer {
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

		FabricDefaultAttributeRegistry.register(
				ModEntities.ANTWORKER,
				AntWorkerEntity.createAttributes()
		);
	}
}