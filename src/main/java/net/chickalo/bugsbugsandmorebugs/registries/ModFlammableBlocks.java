package net.chickalo.bugsbugsandmorebugs.registries;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;

public class ModFlammableBlocks {
    // All existing wood components should burn like vanilla planks.
    private static void registerWoodComponents(net.minecraft.world.level.block.Block... blocks) {
        var registry = FlammableBlockRegistry.getDefaultInstance();
        for (var block : blocks) registry.add(block, 5, 20);
    }

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





        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.WISTERIA_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.WISTERIA_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_WISTERIA_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_WISTERIA_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.WISTERIA_PLANKS, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.WISTERIA_LEAVES, 30, 60);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.WILLOW_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.WILLOW_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_WILLOW_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_WILLOW_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.WILLOW_PLANKS, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.WILLOW_LEAVES, 30, 60);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.CEDAR_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.CEDAR_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_CEDAR_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_CEDAR_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.CEDAR_PLANKS, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.CEDAR_LEAVES, 30, 60);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.PERSIMMON_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.PERSIMMON_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_PERSIMMON_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_PERSIMMON_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.PERSIMMON_PLANKS, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.PERSIMMON_LEAVES, 30, 60);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.MIMOSA_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.MIMOSA_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_MIMOSA_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_MIMOSA_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.MIMOSA_PLANKS, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.MIMOSA_LEAVES, 30, 60);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.WILLOW_ROOTS, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.HANGING_WISTERIA_BLOSSOMS, 30, 60);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.HANGING_WILLOW_LEAVES, 30, 60);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.CLIMBING_WISTERIA_VINE, 15, 100);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.FLOWERING_MIMOSA_LEAVES, 30, 60);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.MOSSY_CEDAR_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.CEDAR_ROOTS, 5, 5);

        registerWoodComponents(
                ModBlocks.GINKGO_STAIRS,
                ModBlocks.GINKGO_SLAB,
                ModBlocks.GINKGO_DOOR,
                ModBlocks.GINKGO_TRAPDOOR,
                ModBlocks.GINKGO_BUTTON,
                ModBlocks.GINKGO_PRESSURE_PLATE,
                ModBlocks.GINKGO_FENCE,
                ModBlocks.GINKGO_FENCE_GATE,
                ModBlocks.MAPLE_STAIRS,
                ModBlocks.MAPLE_SLAB,
                ModBlocks.MAPLE_DOOR,
                ModBlocks.MAPLE_TRAPDOOR,
                ModBlocks.MAPLE_BUTTON,
                ModBlocks.MAPLE_PRESSURE_PLATE,
                ModBlocks.MAPLE_FENCE,
                ModBlocks.MAPLE_FENCE_GATE,
                ModBlocks.HINOKI_DOOR,
                ModBlocks.HINOKI_TRAPDOOR,
                ModBlocks.HINOKI_BUTTON,
                ModBlocks.HINOKI_PRESSURE_PLATE
        );
    }
}
