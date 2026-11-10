package net.chickalo.bugsbugsandmorebugs.datagen;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.tags.ModTags;
import net.fabricmc.fabric.api.client.rendering.v1.FabricOrderedSubmitNodeCollector;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends FabricTagsProvider.BlockTagsProvider {
    public ModBlockTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.getRK(ModBlocks.RHYOLITE))
                .add(ModBlocks.getRK(ModBlocks.COBBLED_RHYOLITE));

        tag(ModTags.Blocks.MAPLE_LOGS)
                .add(ModBlocks.getRK(ModBlocks.MAPLE_LOG))
                .add(ModBlocks.getRK(ModBlocks.MAPLE_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_MAPLE_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_MAPLE_LOG));

        tag(BlockTags.LOGS)
                .addTag(ModTags.Blocks.MAPLE_LOGS);

        tag(BlockTags.PLANKS)
                .add(ModBlocks.getRK(ModBlocks.MAPLE_PLANKS));

        tag(BlockTags.LEAVES)
                .add(ModBlocks.getRK(ModBlocks.MAPLE_LEAVES));

        tag(BlockTags.SAPLINGS)
                .add(ModBlocks.getRK(ModBlocks.MAPLE_SAPLING));

        tag(BlockTags.CROPS)
                .add(ModBlocks.getRK(ModBlocks.DAIKON_CROP));

        tag(BlockTags.WOODEN_FENCES)
                .add(ModBlocks.getRK(ModBlocks.MAPLE_FENCE));

        tag(BlockTags.FENCE_GATES)
                .add(ModBlocks.getRK(ModBlocks.MAPLE_FENCE_GATE));

        tag(BlockTags.WOODEN_BUTTONS)
                .add(ModBlocks.getRK(ModBlocks.MAPLE_BUTTON));

        tag(BlockTags.WOODEN_DOORS)
                .add(ModBlocks.getRK(ModBlocks.MAPLE_DOOR));

        tag(BlockTags.WOODEN_TRAPDOORS)
                .add(ModBlocks.getRK(ModBlocks.MAPLE_TRAPDOOR));

        tag(BlockTags.WALLS)
                .add(ModBlocks.getRK(ModBlocks.RHYOLITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.COBBLED_RHYOLITE_WALL));





    }
}
