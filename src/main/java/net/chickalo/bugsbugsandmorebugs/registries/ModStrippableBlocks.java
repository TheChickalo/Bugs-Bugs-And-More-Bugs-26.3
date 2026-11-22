package net.chickalo.bugsbugsandmorebugs.registries;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.fabricmc.fabric.api.item.v1.BlockTransformerHelper;

public class ModStrippableBlocks {

    public static void registerStrippableBlocks() {
        BlockTransformerHelper.registerStripping(ModBlocks.GINKGO_LOG, ModBlocks.STRIPPED_GINKGO_LOG);
        BlockTransformerHelper.registerStripping(ModBlocks.GINKGO_WOOD, ModBlocks.STRIPPED_GINKGO_WOOD);

        BlockTransformerHelper.registerStripping(
                ModBlocks.MAPLE_LOG,
                ModBlocks.STRIPPED_MAPLE_LOG
        );

        BlockTransformerHelper.registerStripping(
                ModBlocks.MAPLE_WOOD,
                ModBlocks.STRIPPED_MAPLE_WOOD
        );
        BlockTransformerHelper.registerStripping(ModBlocks.HINOKI_LOG, ModBlocks.STRIPPED_HINOKI_LOG);
        BlockTransformerHelper.registerStripping(ModBlocks.HINOKI_WOOD, ModBlocks.STRIPPED_HINOKI_WOOD);
        BlockTransformerHelper.registerStripping(ModBlocks.WISTERIA_LOG, ModBlocks.STRIPPED_WISTERIA_LOG);
        BlockTransformerHelper.registerStripping(ModBlocks.WISTERIA_WOOD, ModBlocks.STRIPPED_WISTERIA_WOOD);
        BlockTransformerHelper.registerStripping(ModBlocks.WILLOW_LOG, ModBlocks.STRIPPED_WILLOW_LOG);
        BlockTransformerHelper.registerStripping(ModBlocks.WILLOW_WOOD, ModBlocks.STRIPPED_WILLOW_WOOD);
        BlockTransformerHelper.registerStripping(ModBlocks.CEDAR_LOG, ModBlocks.STRIPPED_CEDAR_LOG);
        BlockTransformerHelper.registerStripping(ModBlocks.CEDAR_WOOD, ModBlocks.STRIPPED_CEDAR_WOOD);
        BlockTransformerHelper.registerStripping(ModBlocks.PERSIMMON_LOG, ModBlocks.STRIPPED_PERSIMMON_LOG);
        BlockTransformerHelper.registerStripping(ModBlocks.PERSIMMON_WOOD, ModBlocks.STRIPPED_PERSIMMON_WOOD);
        BlockTransformerHelper.registerStripping(ModBlocks.MIMOSA_LOG, ModBlocks.STRIPPED_MIMOSA_LOG);
        BlockTransformerHelper.registerStripping(ModBlocks.MIMOSA_WOOD, ModBlocks.STRIPPED_MIMOSA_WOOD);
    }
}