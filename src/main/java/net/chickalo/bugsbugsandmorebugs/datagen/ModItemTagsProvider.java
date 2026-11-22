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

        tag(ModTags.Items.GINKGO_LOGS)
                .add(ModItems.getRK(ModBlocks.GINKGO_LOG.asItem()))
                .add(ModItems.getRK(ModBlocks.GINKGO_WOOD.asItem()))
                .add(ModItems.getRK(ModBlocks.STRIPPED_GINKGO_WOOD.asItem()))
                .add(ModItems.getRK(ModBlocks.STRIPPED_GINKGO_LOG.asItem()));
        tag(ItemTags.LOGS_THAT_BURN).addTag(ModTags.Items.GINKGO_LOGS);
        tag(ItemTags.PLANKS).add(ModItems.getRK(ModBlocks.GINKGO_PLANKS.asItem()));
        tag(ItemTags.LEAVES).add(ModItems.getRK(ModBlocks.GINKGO_LEAVES.asItem()));

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

        tag(ModTags.Items.WISTERIA_LOGS)
                .add(ModItems.getRK(ModBlocks.WISTERIA_LOG.asItem()))
                .add(ModItems.getRK(ModBlocks.WISTERIA_WOOD.asItem()))
                .add(ModItems.getRK(ModBlocks.STRIPPED_WISTERIA_LOG.asItem()))
                .add(ModItems.getRK(ModBlocks.STRIPPED_WISTERIA_WOOD.asItem()));
        tag(ItemTags.LOGS_THAT_BURN).addTag(ModTags.Items.WISTERIA_LOGS);
        tag(ItemTags.PLANKS).add(ModItems.getRK(ModBlocks.WISTERIA_PLANKS.asItem()));
        tag(ItemTags.LEAVES).add(ModItems.getRK(ModBlocks.WISTERIA_LEAVES.asItem()));

        tag(ModTags.Items.WILLOW_LOGS)
                .add(ModItems.getRK(ModBlocks.WILLOW_LOG.asItem()))
                .add(ModItems.getRK(ModBlocks.WILLOW_WOOD.asItem()))
                .add(ModItems.getRK(ModBlocks.STRIPPED_WILLOW_LOG.asItem()))
                .add(ModItems.getRK(ModBlocks.STRIPPED_WILLOW_WOOD.asItem()));
        tag(ItemTags.LOGS_THAT_BURN).addTag(ModTags.Items.WILLOW_LOGS);
        tag(ItemTags.PLANKS).add(ModItems.getRK(ModBlocks.WILLOW_PLANKS.asItem()));
        tag(ItemTags.LEAVES).add(ModItems.getRK(ModBlocks.WILLOW_LEAVES.asItem()));

        tag(ModTags.Items.CEDAR_LOGS)
                .add(ModItems.getRK(ModBlocks.CEDAR_LOG.asItem()))
                .add(ModItems.getRK(ModBlocks.CEDAR_WOOD.asItem()))
                .add(ModItems.getRK(ModBlocks.STRIPPED_CEDAR_LOG.asItem()))
                .add(ModItems.getRK(ModBlocks.STRIPPED_CEDAR_WOOD.asItem()));
        tag(ItemTags.LOGS_THAT_BURN).addTag(ModTags.Items.CEDAR_LOGS);
        tag(ItemTags.PLANKS).add(ModItems.getRK(ModBlocks.CEDAR_PLANKS.asItem()));
        tag(ItemTags.LEAVES).add(ModItems.getRK(ModBlocks.CEDAR_LEAVES.asItem()));

        tag(ModTags.Items.PERSIMMON_LOGS)
                .add(ModItems.getRK(ModBlocks.PERSIMMON_LOG.asItem()))
                .add(ModItems.getRK(ModBlocks.PERSIMMON_WOOD.asItem()))
                .add(ModItems.getRK(ModBlocks.STRIPPED_PERSIMMON_LOG.asItem()))
                .add(ModItems.getRK(ModBlocks.STRIPPED_PERSIMMON_WOOD.asItem()));
        tag(ItemTags.LOGS_THAT_BURN).addTag(ModTags.Items.PERSIMMON_LOGS);
        tag(ItemTags.PLANKS).add(ModItems.getRK(ModBlocks.PERSIMMON_PLANKS.asItem()));
        tag(ItemTags.LEAVES).add(ModItems.getRK(ModBlocks.PERSIMMON_LEAVES.asItem()));

        tag(ModTags.Items.MIMOSA_LOGS)
                .add(ModItems.getRK(ModBlocks.MIMOSA_LOG.asItem()))
                .add(ModItems.getRK(ModBlocks.MIMOSA_WOOD.asItem()))
                .add(ModItems.getRK(ModBlocks.STRIPPED_MIMOSA_LOG.asItem()))
                .add(ModItems.getRK(ModBlocks.STRIPPED_MIMOSA_WOOD.asItem()));
        tag(ItemTags.LOGS_THAT_BURN).addTag(ModTags.Items.MIMOSA_LOGS);
        tag(ItemTags.PLANKS).add(ModItems.getRK(ModBlocks.MIMOSA_PLANKS.asItem()));
        tag(ItemTags.LEAVES).add(ModItems.getRK(ModBlocks.MIMOSA_LEAVES.asItem()));

    }
}
