package net.chickalo.bugsbugsandmorebugs.registries;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;

public class ModFlammableBlocks {
    public static void registerFlammableBlocks() {
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.GINKGO_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.GINKGO_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_GINKGO_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_GINKGO_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.GINKGO_PLANKS, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.GINKGO_LEAVES, 30, 60);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.MAPLE_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.MAPLE_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_MAPLE_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_MAPLE_WOOD, 5, 5);

        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.MAPLE_PLANKS, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.MAPLE_LEAVES, 30, 60);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.HINOKI_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.HINOKI_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_HINOKI_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_HINOKI_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.HINOKI_PLANKS, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.HINOKI_STAIRS, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.HINOKI_SLAB, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.HINOKI_FENCE, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.HINOKI_FENCE_GATE, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.HINOKI_LEAVES, 30, 60);





    }
}
