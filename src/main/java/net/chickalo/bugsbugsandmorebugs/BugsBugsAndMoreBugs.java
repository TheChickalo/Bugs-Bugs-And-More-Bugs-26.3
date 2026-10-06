package net.chickalo.bugsbugsandmorebugs;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.entity.ModEntities;
import net.chickalo.bugsbugsandmorebugs.entity.custom.AntWorkerEntity;
import net.chickalo.bugsbugsandmorebugs.item.ModItems;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BugsBugsAndMoreBugs implements ModInitializer {
	public static final String MOD_ID = "bugsbugsandmorebugs";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();

		ModEntities.registerModEntities();

		FabricDefaultAttributeRegistry.register(ModEntities.ANTWORKER, AntWorkerEntity.createAttributes());
	}
}
