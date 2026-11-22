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
        // Elsewhere secondary rock mining tags
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.getRK(ModBlocks.DACITE))
                .add(ModBlocks.getRK(ModBlocks.DACITE_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.DACITE_SLAB))
                .add(ModBlocks.getRK(ModBlocks.DACITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_DACITE))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_DACITE_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_DACITE_SLAB))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_DACITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.CHERT))
                .add(ModBlocks.getRK(ModBlocks.CHERT_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.CHERT_SLAB))
                .add(ModBlocks.getRK(ModBlocks.CHERT_WALL))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_CHERT))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_CHERT_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_CHERT_SLAB))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_CHERT_WALL))
                .add(ModBlocks.getRK(ModBlocks.SERPENTINITE))
                .add(ModBlocks.getRK(ModBlocks.SERPENTINITE_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.SERPENTINITE_SLAB))
                .add(ModBlocks.getRK(ModBlocks.SERPENTINITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_SERPENTINITE))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_SERPENTINITE_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_SERPENTINITE_SLAB))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_SERPENTINITE_WALL));

        tag(BlockTags.WALLS)
                .add(ModBlocks.getRK(ModBlocks.DACITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_DACITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.CHERT_WALL))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_CHERT_WALL))
                .add(ModBlocks.getRK(ModBlocks.SERPENTINITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_SERPENTINITE_WALL));


        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.getRK(ModBlocks.MOSSY_RHYOLITE))
                .add(ModBlocks.getRK(ModBlocks.AMPHIBOLITE))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_AMPHIBOLITE))
                .add(ModBlocks.getRK(ModBlocks.AMPHIBOLITE_BRICKS))
                .add(ModBlocks.getRK(ModBlocks.CHISELED_AMPHIBOLITE))
                .add(ModBlocks.getRK(ModBlocks.CHISELED_AMPHIBOLITE_BRICKS))
                .add(ModBlocks.getRK(ModBlocks.AMPHIBOLITE_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.AMPHIBOLITE_SLAB))
                .add(ModBlocks.getRK(ModBlocks.AMPHIBOLITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_AMPHIBOLITE_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_AMPHIBOLITE_SLAB))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_AMPHIBOLITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.AMPHIBOLITE_BRICK_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.AMPHIBOLITE_BRICK_SLAB))
                .add(ModBlocks.getRK(ModBlocks.AMPHIBOLITE_BRICK_WALL));

        tag(BlockTags.WALLS)
                .add(ModBlocks.getRK(ModBlocks.AMPHIBOLITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_AMPHIBOLITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.AMPHIBOLITE_BRICK_WALL));

        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.getRK(ModBlocks.LAPILLI));

        tag(BlockTags.STONE_ORE_REPLACEABLES).add(ModBlocks.getRK(ModBlocks.AMPHIBOLITE));

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



        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.getRK(ModBlocks.SCHIST))
                .add(ModBlocks.getRK(ModBlocks.COBBLED_SCHIST))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_SCHIST))
                .add(ModBlocks.getRK(ModBlocks.SCHIST_BRICKS))
                .add(ModBlocks.getRK(ModBlocks.CHISELED_SCHIST))
                .add(ModBlocks.getRK(ModBlocks.CRACKED_SCHIST_BRICKS))
                .add(ModBlocks.getRK(ModBlocks.SCHIST_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.SCHIST_SLAB))
                .add(ModBlocks.getRK(ModBlocks.SCHIST_WALL))
                .add(ModBlocks.getRK(ModBlocks.COBBLED_SCHIST_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.COBBLED_SCHIST_SLAB))
                .add(ModBlocks.getRK(ModBlocks.COBBLED_SCHIST_WALL))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_SCHIST_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_SCHIST_SLAB))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_SCHIST_WALL))
                .add(ModBlocks.getRK(ModBlocks.SCHIST_BRICKS_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.SCHIST_BRICKS_SLAB))
                .add(ModBlocks.getRK(ModBlocks.SCHIST_BRICKS_WALL));

        tag(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                .add(ModBlocks.getRK(ModBlocks.SCHIST));

        tag(BlockTags.WALLS)
                .add(ModBlocks.getRK(ModBlocks.SCHIST_WALL))
                .add(ModBlocks.getRK(ModBlocks.COBBLED_SCHIST_WALL))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_SCHIST_WALL))
                .add(ModBlocks.getRK(ModBlocks.SCHIST_BRICKS_WALL));

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.getRK(ModBlocks.ECLOGITE))
                .add(ModBlocks.getRK(ModBlocks.COBBLED_ECLOGITE))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_ECLOGITE))
                .add(ModBlocks.getRK(ModBlocks.ECLOGITE_BRICKS))
                .add(ModBlocks.getRK(ModBlocks.CHISELED_ECLOGITE))
                .add(ModBlocks.getRK(ModBlocks.CRACKED_ECLOGITE_BRICKS))
                .add(ModBlocks.getRK(ModBlocks.ECLOGITE_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.ECLOGITE_SLAB))
                .add(ModBlocks.getRK(ModBlocks.ECLOGITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.COBBLED_ECLOGITE_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.COBBLED_ECLOGITE_SLAB))
                .add(ModBlocks.getRK(ModBlocks.COBBLED_ECLOGITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_ECLOGITE_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_ECLOGITE_SLAB))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_ECLOGITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.ECLOGITE_BRICKS_STAIRS))
                .add(ModBlocks.getRK(ModBlocks.ECLOGITE_BRICKS_SLAB))
                .add(ModBlocks.getRK(ModBlocks.ECLOGITE_BRICKS_WALL));

        tag(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                .add(ModBlocks.getRK(ModBlocks.ECLOGITE));

        tag(BlockTags.WALLS)
                .add(ModBlocks.getRK(ModBlocks.ECLOGITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.COBBLED_ECLOGITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.POLISHED_ECLOGITE_WALL))
                .add(ModBlocks.getRK(ModBlocks.ECLOGITE_BRICKS_WALL));

        tag(BlockTags.DIRT)
                .add(ModBlocks.getRK(ModBlocks.LOAM))
                .add(ModBlocks.getRK(ModBlocks.GRASSY_LOAM));
        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.getRK(ModBlocks.LOAM))
                .add(ModBlocks.getRK(ModBlocks.GRASSY_LOAM));
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.getRK(ModBlocks.ASHSTONE))
                .add(ModBlocks.getRK(ModBlocks.SHATTERSTONE));
        tag(BlockTags.STONE_ORE_REPLACEABLES)
                .add(ModBlocks.getRK(ModBlocks.RHYOLITE));





        // Custom Ginkgo wood + golden foliage; includes terrain survival for native grass.
        tag(ModTags.Blocks.GINKGO_LOGS)
                .add(ModBlocks.getRK(ModBlocks.GINKGO_LOG))
                .add(ModBlocks.getRK(ModBlocks.GINKGO_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_GINKGO_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_GINKGO_WOOD));
        tag(BlockTags.LOGS).addTag(ModTags.Blocks.GINKGO_LOGS);
        tag(BlockTags.PLANKS).add(ModBlocks.getRK(ModBlocks.GINKGO_PLANKS));
        tag(BlockTags.LEAVES).add(ModBlocks.getRK(ModBlocks.GINKGO_LEAVES));
        tag(BlockTags.SAPLINGS).add(ModBlocks.getRK(ModBlocks.GINKGO_SAPLING));
        tag(BlockTags.GRASS_BLOCKS).add(ModBlocks.getRK(ModBlocks.GRASSY_LOAM));
        tag(BlockTags.WOODEN_FENCES).add(ModBlocks.getRK(ModBlocks.GINKGO_FENCE));
        tag(BlockTags.FENCE_GATES).add(ModBlocks.getRK(ModBlocks.GINKGO_FENCE_GATE));
        tag(BlockTags.WOODEN_BUTTONS).add(ModBlocks.getRK(ModBlocks.GINKGO_BUTTON));
        tag(BlockTags.WOODEN_DOORS).add(ModBlocks.getRK(ModBlocks.GINKGO_DOOR));
        tag(BlockTags.WOODEN_TRAPDOORS).add(ModBlocks.getRK(ModBlocks.GINKGO_TRAPDOOR));

        // Register the Hinoki wood family as real leaf-supporting logs.
        tag(BlockTags.LOGS)
                .add(ModBlocks.getRK(ModBlocks.HINOKI_LOG))
                .add(ModBlocks.getRK(ModBlocks.HINOKI_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_HINOKI_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_HINOKI_WOOD));
        tag(BlockTags.LEAVES).add(ModBlocks.getRK(ModBlocks.HINOKI_LEAVES));
        tag(BlockTags.SAPLINGS).add(ModBlocks.getRK(ModBlocks.HINOKI_SAPLING));
        tag(BlockTags.PREVENTS_NEARBY_LEAF_DECAY)
                .add(ModBlocks.getRK(ModBlocks.HINOKI_LOG))
                .add(ModBlocks.getRK(ModBlocks.HINOKI_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_HINOKI_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_HINOKI_WOOD));

        // Ginkgo trunks must support the golden foliage's distance calculations.
        tag(BlockTags.PREVENTS_NEARBY_LEAF_DECAY)
                .add(ModBlocks.getRK(ModBlocks.GINKGO_LOG))
                .add(ModBlocks.getRK(ModBlocks.GINKGO_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_GINKGO_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_GINKGO_WOOD));

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




        tag(BlockTags.LOGS).add(ModBlocks.getRK(ModBlocks.WISTERIA_LOG))
                .add(ModBlocks.getRK(ModBlocks.WISTERIA_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_WISTERIA_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_WISTERIA_WOOD));
        tag(BlockTags.PREVENTS_NEARBY_LEAF_DECAY)
                .add(ModBlocks.getRK(ModBlocks.WISTERIA_LOG))
                .add(ModBlocks.getRK(ModBlocks.WISTERIA_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_WISTERIA_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_WISTERIA_WOOD));
        tag(BlockTags.PLANKS).add(ModBlocks.getRK(ModBlocks.WISTERIA_PLANKS));
        tag(BlockTags.LEAVES).add(ModBlocks.getRK(ModBlocks.WISTERIA_LEAVES));
        tag(BlockTags.SAPLINGS).add(ModBlocks.getRK(ModBlocks.WISTERIA_SAPLING));
        tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.getRK(ModBlocks.WISTERIA_LOG))
                .add(ModBlocks.getRK(ModBlocks.WISTERIA_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_WISTERIA_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_WISTERIA_WOOD))
                .add(ModBlocks.getRK(ModBlocks.WISTERIA_PLANKS));
        tag(BlockTags.LOGS).add(ModBlocks.getRK(ModBlocks.WILLOW_LOG))
                .add(ModBlocks.getRK(ModBlocks.WILLOW_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_WILLOW_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_WILLOW_WOOD));
        tag(BlockTags.PREVENTS_NEARBY_LEAF_DECAY)
                .add(ModBlocks.getRK(ModBlocks.WILLOW_LOG))
                .add(ModBlocks.getRK(ModBlocks.WILLOW_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_WILLOW_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_WILLOW_WOOD));
        tag(BlockTags.PLANKS).add(ModBlocks.getRK(ModBlocks.WILLOW_PLANKS));
        tag(BlockTags.LEAVES).add(ModBlocks.getRK(ModBlocks.WILLOW_LEAVES));
        tag(BlockTags.SAPLINGS).add(ModBlocks.getRK(ModBlocks.WILLOW_SAPLING));
        tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.getRK(ModBlocks.WILLOW_LOG))
                .add(ModBlocks.getRK(ModBlocks.WILLOW_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_WILLOW_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_WILLOW_WOOD))
                .add(ModBlocks.getRK(ModBlocks.WILLOW_PLANKS));
        tag(BlockTags.LOGS).add(ModBlocks.getRK(ModBlocks.CEDAR_LOG))
                .add(ModBlocks.getRK(ModBlocks.CEDAR_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_CEDAR_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_CEDAR_WOOD));
        tag(BlockTags.PREVENTS_NEARBY_LEAF_DECAY)
                .add(ModBlocks.getRK(ModBlocks.CEDAR_LOG))
                .add(ModBlocks.getRK(ModBlocks.CEDAR_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_CEDAR_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_CEDAR_WOOD));
        tag(BlockTags.PLANKS).add(ModBlocks.getRK(ModBlocks.CEDAR_PLANKS));
        tag(BlockTags.LEAVES).add(ModBlocks.getRK(ModBlocks.CEDAR_LEAVES));
        tag(BlockTags.SAPLINGS).add(ModBlocks.getRK(ModBlocks.CEDAR_SAPLING));
        tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.getRK(ModBlocks.CEDAR_LOG))
                .add(ModBlocks.getRK(ModBlocks.CEDAR_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_CEDAR_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_CEDAR_WOOD))
                .add(ModBlocks.getRK(ModBlocks.CEDAR_PLANKS));
        tag(BlockTags.LOGS).add(ModBlocks.getRK(ModBlocks.PERSIMMON_LOG))
                .add(ModBlocks.getRK(ModBlocks.PERSIMMON_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_PERSIMMON_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_PERSIMMON_WOOD));
        tag(BlockTags.PREVENTS_NEARBY_LEAF_DECAY)
                .add(ModBlocks.getRK(ModBlocks.PERSIMMON_LOG))
                .add(ModBlocks.getRK(ModBlocks.PERSIMMON_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_PERSIMMON_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_PERSIMMON_WOOD));
        tag(BlockTags.PLANKS).add(ModBlocks.getRK(ModBlocks.PERSIMMON_PLANKS));
        tag(BlockTags.LEAVES).add(ModBlocks.getRK(ModBlocks.PERSIMMON_LEAVES));
        tag(BlockTags.SAPLINGS).add(ModBlocks.getRK(ModBlocks.PERSIMMON_SAPLING));
        tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.getRK(ModBlocks.PERSIMMON_LOG))
                .add(ModBlocks.getRK(ModBlocks.PERSIMMON_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_PERSIMMON_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_PERSIMMON_WOOD))
                .add(ModBlocks.getRK(ModBlocks.PERSIMMON_PLANKS));
        tag(BlockTags.LOGS).add(ModBlocks.getRK(ModBlocks.MIMOSA_LOG))
                .add(ModBlocks.getRK(ModBlocks.MIMOSA_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_MIMOSA_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_MIMOSA_WOOD));
        tag(BlockTags.PREVENTS_NEARBY_LEAF_DECAY)
                .add(ModBlocks.getRK(ModBlocks.MIMOSA_LOG))
                .add(ModBlocks.getRK(ModBlocks.MIMOSA_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_MIMOSA_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_MIMOSA_WOOD));
        tag(BlockTags.PLANKS).add(ModBlocks.getRK(ModBlocks.MIMOSA_PLANKS));
        tag(BlockTags.LEAVES).add(ModBlocks.getRK(ModBlocks.MIMOSA_LEAVES));
        tag(BlockTags.SAPLINGS).add(ModBlocks.getRK(ModBlocks.MIMOSA_SAPLING));
        tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.getRK(ModBlocks.MIMOSA_LOG))
                .add(ModBlocks.getRK(ModBlocks.MIMOSA_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_MIMOSA_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_MIMOSA_WOOD))
                .add(ModBlocks.getRK(ModBlocks.MIMOSA_PLANKS));
        // Roots provide genuine structural support for willow leaf curtains.
        tag(BlockTags.PREVENTS_NEARBY_LEAF_DECAY).add(ModBlocks.getRK(ModBlocks.WILLOW_ROOTS));
        tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.getRK(ModBlocks.WILLOW_ROOTS));
        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.getRK(ModBlocks.SILT));
        tag(BlockTags.FLOWERS).add(ModBlocks.getRK(ModBlocks.WILD_VIOLET))
                .add(ModBlocks.getRK(ModBlocks.MARSH_MARIGOLD))
                .add(ModBlocks.getRK(ModBlocks.MARSH_MARIGOLD_WATER));

        tag(BlockTags.LOGS).add(ModBlocks.getRK(ModBlocks.CEDAR_ROOTS)).add(ModBlocks.getRK(ModBlocks.MOSSY_CEDAR_LOG));
        tag(BlockTags.PREVENTS_NEARBY_LEAF_DECAY).add(ModBlocks.getRK(ModBlocks.CEDAR_ROOTS)).add(ModBlocks.getRK(ModBlocks.MOSSY_CEDAR_LOG));
        tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.getRK(ModBlocks.CEDAR_ROOTS)).add(ModBlocks.getRK(ModBlocks.MOSSY_CEDAR_LOG));
        tag(BlockTags.LEAVES).add(ModBlocks.getRK(ModBlocks.FLOWERING_MIMOSA_LEAVES));
        tag(BlockTags.REPLACEABLE_BY_TREES).add(ModBlocks.getRK(ModBlocks.CLUBMOSS_SPRIG));
        tag(BlockTags.FLOWERS).add(ModBlocks.getRK(ModBlocks.ANEMONE_FLOWERS)).add(ModBlocks.getRK(ModBlocks.MIMOSA_BLOSSOMS)).add(ModBlocks.getRK(ModBlocks.PINK_CLOVER));

        tag(ModTags.Blocks.WISTERIA_LOGS)
                .add(ModBlocks.getRK(ModBlocks.WISTERIA_LOG))
                .add(ModBlocks.getRK(ModBlocks.WISTERIA_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_WISTERIA_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_WISTERIA_WOOD));
        tag(ModTags.Blocks.WILLOW_LOGS)
                .add(ModBlocks.getRK(ModBlocks.WILLOW_LOG))
                .add(ModBlocks.getRK(ModBlocks.WILLOW_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_WILLOW_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_WILLOW_WOOD));
        tag(ModTags.Blocks.CEDAR_LOGS)
                .add(ModBlocks.getRK(ModBlocks.CEDAR_ROOTS))
                .add(ModBlocks.getRK(ModBlocks.MOSSY_CEDAR_LOG))
                .add(ModBlocks.getRK(ModBlocks.CEDAR_LOG))
                .add(ModBlocks.getRK(ModBlocks.CEDAR_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_CEDAR_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_CEDAR_WOOD));
        tag(ModTags.Blocks.PERSIMMON_LOGS)
                .add(ModBlocks.getRK(ModBlocks.PERSIMMON_LOG))
                .add(ModBlocks.getRK(ModBlocks.PERSIMMON_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_PERSIMMON_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_PERSIMMON_WOOD));
        tag(ModTags.Blocks.MIMOSA_LOGS)
                .add(ModBlocks.getRK(ModBlocks.MIMOSA_LOG))
                .add(ModBlocks.getRK(ModBlocks.MIMOSA_WOOD))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_MIMOSA_LOG))
                .add(ModBlocks.getRK(ModBlocks.STRIPPED_MIMOSA_WOOD));

    }
}
