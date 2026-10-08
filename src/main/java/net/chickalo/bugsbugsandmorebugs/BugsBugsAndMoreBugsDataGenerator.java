package net.chickalo.bugsbugsandmorebugs;

import net.chickalo.bugsbugsandmorebugs.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class BugsBugsAndMoreBugsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		var pack = fabricDataGenerator.createPack();

		pack.addProvider(ModModelProvider::new);

		ModBlockTagsProvider blockTagsProvider = pack.addProvider(ModBlockTagsProvider::new);
		pack.addProvider((output, registries) -> new ModItemTagsProvider(output, registries, blockTagsProvider));

		pack.addProvider(ModBlockLootTableProvider::new);
		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider(ModSoundsProvider::new);
		pack.addProvider(ModRegistryDataProvider::new);
	}

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		registryBuilder.add(Registries.JUKEBOX_SONG, ModJukeboxSongs::bootstrap);
	}
}
