package net.chickalo.bugsbugsandmorebugs;

import net.chickalo.bugsbugsandmorebugs.datagen.ModBlockLootTableProvider;
import net.chickalo.bugsbugsandmorebugs.datagen.ModBlockTagsProvider;
import net.chickalo.bugsbugsandmorebugs.datagen.ModModelProvider;
import net.chickalo.bugsbugsandmorebugs.datagen.ModRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class BugsBugsAndMoreBugsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		var pack = fabricDataGenerator.createPack();

		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModBlockTagsProvider::	new);
		pack.addProvider(ModBlockLootTableProvider::new);
		pack.addProvider(ModRecipeProvider::new);
	}
}
