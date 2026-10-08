package net.chickalo.bugsbugsandmorebugs.datagen;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.item.ModItems;
import net.chickalo.bugsbugsandmorebugs.tags.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.references.ItemIds;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {
    public ModItemTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture, BlockTagsProvider blockTagsProvider) {
        super(output, registryLookupFuture, blockTagsProvider);
    }



    @Override
    protected void addTags(HolderLookup.Provider registries) {

        tag(ModTags.Items.MAPLE_LOGS)
                .add(ModItems.getRK(ModBlocks.MAPLE_LOG.asItem()))
                .add(ModItems.getRK(ModBlocks.MAPLE_WOOD.asItem()))
                .add(ModItems.getRK(ModBlocks.STRIPPED_MAPLE_WOOD.asItem()))
                .add(ModItems.getRK(ModBlocks.STRIPPED_MAPLE_LOG.asItem()));


        tag(ItemTags.LOGS_THAT_BURN)
                .addTag(ModTags.Items.MAPLE_LOGS);

        tag(ItemTags.PLANKS)
                .add(ModItems.getRK(ModBlocks.MAPLE_PLANKS.asItem()));

        tag(ItemTags.LEAVES)
                .add(ModItems.getRK(ModBlocks.MAPLE_LEAVES.asItem()));
    }
}
