package net.chickalo.bugsbugsandmorebugs.datagen;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.tags.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends FabricTagsProvider.BlockTagsProvider {
    public ModBlockTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.getRK(ModBlocks.RHYOLITE))
                .add(ModBlocks.getRK(ModBlocks.COBBLED_RHYOLITE))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_RHYOLITE))
                .add(ModBlocks.getRK(ModBlocks.RHYOLITE_BRICKS))
                .add(ModBlocks.getRK(ModBlocks.RHYOLITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.RHYOLITE_SLAB))
                .add(ModBlocks.getRK(ModBlocks.RHYOLITE_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.COBBLED_RHYOLITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.COBBLED_RHYOLITE_SLAB))
                .add(ModBlocks.getRK(ModBlocks.COBBLED_RHYOLITE_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_RHYOLITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_RHYOLITE_SLAB))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_RHYOLITE_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.RHYOLITE_BRICK_WALL))
                .add(ModBlocks.getRK(ModBlocks.RHYOLITE_BRICK_SLAB))
                .add(ModBlocks.getRK(ModBlocks.RHYOLITE_BRICK_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.CHISELED_RHYOLITE))
                .add(ModBlocks.getRK(ModBlocks.CRACKED_RHYOLITE_BRICKS))
                .add(ModBlocks.getRK(ModBlocks.LIMESTONE))
                .add(ModBlocks.getRK(ModBlocks.RUBBLED_LIMESTONE))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_LIMESTONE))
                .add(ModBlocks.getRK(ModBlocks.WEATHERED_LIMESTONE))
                .add(ModBlocks.getRK(ModBlocks.LIMESTONE_PILLAR))
                .add(ModBlocks.getRK(ModBlocks.LIMESTONE_BRICK_WALL))
                .add(ModBlocks.getRK(ModBlocks.LIMESTONE_BRICK_SLAB))
                .add(ModBlocks.getRK(ModBlocks.LIMESTONE_BRICK_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.LIMESTONE_WALL))
                .add(ModBlocks.getRK(ModBlocks.LIMESTONE_SLAB))
                .add(ModBlocks.getRK(ModBlocks.LIMESTONE_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_LIMESTONE_WALL))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_LIMESTONE_SLAB))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_LIMESTONE_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.RUBBLED_LIMESTONE_WALL))
                .add(ModBlocks.getRK(ModBlocks.RUBBLED_LIMESTONE_SLAB))
                .add(ModBlocks.getRK(ModBlocks.RUBBLED_LIMESTONE_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.MOSSY_LIMESTONE))
                .add(ModBlocks.getRK(ModBlocks.LITHOSOL));




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
                .add(ModBlocks.getRK(ModBlocks.COBBLED_RHYOLITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_RHYOLITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.RHYOLITE_BRICK_WALL))
                .add(ModBlocks.getRK(ModBlocks.LIMESTONE_WALL))
                .add(ModBlocks.getRK(ModBlocks.RUBBLED_LIMESTONE_WALL))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_LIMESTONE_WALL))
                .add(ModBlocks.getRK(ModBlocks.LIMESTONE_BRICK_WALL));

        tag(BlockTags.FLOWERS)
                .add(ModBlocks.getRK(ModBlocks.BELLFLOWERS));




    }
}
