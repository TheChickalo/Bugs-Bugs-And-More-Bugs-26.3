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
    }
}