package net.chickalo.bugsbugsandmorebugs.datagen;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.block.custom.BarberryBushBlock;
import net.chickalo.bugsbugsandmorebugs.block.custom.DaikonCropBlock;
import net.chickalo.bugsbugsandmorebugs.block.custom.HaskapBerryBushBlock;
import net.chickalo.bugsbugsandmorebugs.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

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
        dropSelf(ModBlocks.MAPLE_SAPLING);
        dropSelf(ModBlocks.MAPLE_TRAPDOOR);
        dropSelf(ModBlocks.MAPLE_STAIRS);
        add(ModBlocks.MAPLE_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.MAPLE_BUTTON);
        dropSelf(ModBlocks.MAPLE_BUTTON);

        add(ModBlocks.MAPLE_DOOR, this::createDoorTable);

        add(ModBlocks.POTTED_MAPLE_SAPLING, block -> createPotFlowerItemTable(ModBlocks.MAPLE_SAPLING));

        add(ModBlocks.MAPLE_LEAVES,
                block -> createLeavesDrops(
                        block,
                        ModBlocks.MAPLE_SAPLING,
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

        this.add(ModBlocks.BARBERRY_BUSH, block -> this.applyExplosionDecay(block,
                LootTable.lootTable().withPool(LootPool.lootPool()
                        .when(MatchBlock.blockMatches(this.blocks, ModBlocks.BARBERRY_BUSH,
                                StatePropertiesPredicate.Builder.properties().hasProperty(BarberryBushBlock.AGE, 3)))
                        .add(LootItem.lootTableItem(ModItems.BARBERRIES))
                        .apply(SetItemCountFunction.setCount(ContextIntProviders.between(4, 6)))
                        .apply(ApplyBonusCount.addUniformBonusCount(this.enchantments.getOrThrow(Enchantments.FORTUNE)))
                ).withPool(LootPool.lootPool().when(
                        MatchBlock.blockMatches(this.blocks, ModBlocks.BARBERRY_BUSH,
                                StatePropertiesPredicate.Builder.properties().hasProperty(BarberryBushBlock.AGE, 2))
                ).add(LootItem.lootTableItem(ModItems.BARBERRIES))
                        .apply(SetItemCountFunction.setCount(ContextIntProviders.between(2, 3)))
                        .apply(ApplyBonusCount.addUniformBonusCount(this.enchantments.getOrThrow(Enchantments.FORTUNE)))))
        );

        this.add(ModBlocks.HASKAP_BERRY_BUSH, block -> this.applyExplosionDecay(block,
                LootTable.lootTable().withPool(LootPool.lootPool()
                        .when(MatchBlock.blockMatches(this.blocks, ModBlocks.HASKAP_BERRY_BUSH,
                                StatePropertiesPredicate.Builder.properties().hasProperty(HaskapBerryBushBlock.AGE, 3)))
                        .add(LootItem.lootTableItem(ModItems.HASKAP_BERRIES))
                        .apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 2)))
                        .apply(ApplyBonusCount.addUniformBonusCount(this.enchantments.getOrThrow(Enchantments.FORTUNE)))
                ).withPool(LootPool.lootPool().when(
                                MatchBlock.blockMatches(this.blocks, ModBlocks.HASKAP_BERRY_BUSH,
                                        StatePropertiesPredicate.Builder.properties().hasProperty(HaskapBerryBushBlock.AGE, 2))
                        ).add(LootItem.lootTableItem(ModItems.HASKAP_BERRIES))
                        .apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 1)))
                        .apply(ApplyBonusCount.addUniformBonusCount(this.enchantments.getOrThrow(Enchantments.FORTUNE)))))
        );

    }
}