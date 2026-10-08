package net.chickalo.bugsbugsandmorebugs.datagen;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.block.custom.DaikonCropBlock;
import net.chickalo.bugsbugsandmorebugs.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;

import java.util.concurrent.CompletableFuture;

public class ModBlockLootTableProvider extends FabricBlockLootSubProvider {

    public ModBlockLootTableProvider(
            FabricPackOutput packOutput,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        super(packOutput, registriesFuture);
    }

    @Override
    public void generate() {

        dropSelf(ModBlocks.MAPLE_LOG);
        dropSelf(ModBlocks.MAPLE_WOOD);
        dropSelf(ModBlocks.STRIPPED_MAPLE_LOG);
        dropSelf(ModBlocks.STRIPPED_MAPLE_WOOD);
        dropSelf(ModBlocks.MAPLE_PLANKS);

        // vv will drop saplings later
        add(ModBlocks.MAPLE_LEAVES,
                block -> createLeavesDrops(
                        block,
                        ModBlocks.MAPLE_LEAVES,
                        NORMAL_LEAVES_SAPLING_CHANCES
                ));

        this.add(ModBlocks.DAIKON_CROP, this.createCropDrops(
                ModBlocks.DAIKON_CROP,
                ModItems.DAIKON,
                ModItems.DAIKON_SEEDS,
                MatchBlock.blockMatches(
                        this.blocks,
                        ModBlocks.DAIKON_CROP,
                        StatePropertiesPredicate.Builder.properties()
                                .hasProperty(DaikonCropBlock.AGE, DaikonCropBlock.MAX_AGE)
                )
        ));
    }
}