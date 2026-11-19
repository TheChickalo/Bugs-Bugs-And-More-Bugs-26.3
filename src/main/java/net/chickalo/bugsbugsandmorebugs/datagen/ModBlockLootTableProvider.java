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
        add(ModBlocks.ALPINE_GRASS, block -> createShearsOnlyDrop(block));
        add(ModBlocks.HAKONE_GRASS, block -> createShearsOnlyDrop(block));
        dropSelf(ModBlocks.IRIS_FLOWER);
        dropSelf(ModBlocks.GINKGO_FALLEN_LEAVES);
        dropSelf(ModBlocks.DUCKWEED);
        dropSelf(ModBlocks.LOTUS);
        dropSelf(ModBlocks.LOTUS_FLOWER);
        dropSelf(ModBlocks.CATTAILS);
        dropSelf(ModBlocks.REEDS);
        // Elsewhere secondary stone drops
        dropSelf(ModBlocks.DACITE);
        dropSelf(ModBlocks.DACITE_STAIRS);
        add(ModBlocks.DACITE_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.DACITE_WALL);
        dropSelf(ModBlocks.POLISHED_DACITE);
        dropSelf(ModBlocks.POLISHED_DACITE_STAIRS);
        add(ModBlocks.POLISHED_DACITE_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.POLISHED_DACITE_WALL);
        dropSelf(ModBlocks.CHERT);
        dropSelf(ModBlocks.CHERT_STAIRS);
        add(ModBlocks.CHERT_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.CHERT_WALL);
        dropSelf(ModBlocks.POLISHED_CHERT);
        dropSelf(ModBlocks.POLISHED_CHERT_STAIRS);
        add(ModBlocks.POLISHED_CHERT_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.POLISHED_CHERT_WALL);
        dropSelf(ModBlocks.SERPENTINITE);
        dropSelf(ModBlocks.SERPENTINITE_STAIRS);
        add(ModBlocks.SERPENTINITE_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.SERPENTINITE_WALL);
        dropSelf(ModBlocks.POLISHED_SERPENTINITE);
        dropSelf(ModBlocks.POLISHED_SERPENTINITE_STAIRS);
        add(ModBlocks.POLISHED_SERPENTINITE_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.POLISHED_SERPENTINITE_WALL);

        dropSelf(ModBlocks.MOSSY_RHYOLITE);
        dropSelf(ModBlocks.AMPHIBOLITE);
        dropSelf(ModBlocks.POLISHED_AMPHIBOLITE);
        dropSelf(ModBlocks.AMPHIBOLITE_BRICKS);
        dropSelf(ModBlocks.CHISELED_AMPHIBOLITE);
        dropSelf(ModBlocks.CHISELED_AMPHIBOLITE_BRICKS);
        dropSelf(ModBlocks.AMPHIBOLITE_STAIRS);
        add(ModBlocks.AMPHIBOLITE_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.AMPHIBOLITE_WALL);
        dropSelf(ModBlocks.POLISHED_AMPHIBOLITE_STAIRS);
        add(ModBlocks.POLISHED_AMPHIBOLITE_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.POLISHED_AMPHIBOLITE_WALL);
        dropSelf(ModBlocks.AMPHIBOLITE_BRICK_STAIRS);
        add(ModBlocks.AMPHIBOLITE_BRICK_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.AMPHIBOLITE_BRICK_WALL);
        dropSelf(ModBlocks.LAPILLI);

        dropSelf(ModBlocks.TEMP_PORTAL);
        dropSelf(ModBlocks.LOAM);
        dropSelf(ModBlocks.ASHSTONE);
        dropSelf(ModBlocks.SHATTERSTONE);
        add(ModBlocks.GRASSY_LOAM, createSingleItemTableWithSilkTouch(ModBlocks.GRASSY_LOAM, ModBlocks.LOAM));
        dropSelf(ModBlocks.COBBLED_RHYOLITE);
        dropSelf(ModBlocks.POLISHED_RHYOLITE);
        add(
                ModBlocks.RHYOLITE,
                createSingleItemTableWithSilkTouch(
                        ModBlocks.RHYOLITE,
                        ModBlocks.COBBLED_RHYOLITE
                )
        );
        dropSelf(ModBlocks.CHISELED_RHYOLITE);
        dropSelf(ModBlocks.RHYOLITE_WALL);
        dropSelf(ModBlocks.RHYOLITE_STAIRS);
        add(ModBlocks.RHYOLITE_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.COBBLED_RHYOLITE_WALL);
        dropSelf(ModBlocks.COBBLED_RHYOLITE_STAIRS);
        add(ModBlocks.COBBLED_RHYOLITE_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.POLISHED_RHYOLITE_WALL);
        dropSelf(ModBlocks.POLISHED_RHYOLITE_STAIRS);
        add(ModBlocks.POLISHED_RHYOLITE_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.RHYOLITE_BRICKS);
        dropSelf(ModBlocks.RHYOLITE_BRICK_STAIRS);
        dropSelf(ModBlocks.RHYOLITE_BRICK_WALL);
        add(ModBlocks.RHYOLITE_BRICK_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.CRACKED_RHYOLITE_BRICKS);

        add(ModBlocks.SCHIST,
                createSingleItemTableWithSilkTouch(ModBlocks.SCHIST, ModBlocks.COBBLED_SCHIST));
        dropSelf(ModBlocks.COBBLED_SCHIST);
        dropSelf(ModBlocks.POLISHED_SCHIST);
        dropSelf(ModBlocks.SCHIST_BRICKS);
        dropSelf(ModBlocks.CHISELED_SCHIST);
        dropSelf(ModBlocks.CRACKED_SCHIST_BRICKS);
        dropSelf(ModBlocks.SCHIST_STAIRS);
        dropSelf(ModBlocks.SCHIST_WALL);
        add(ModBlocks.SCHIST_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.COBBLED_SCHIST_STAIRS);
        dropSelf(ModBlocks.COBBLED_SCHIST_WALL);
        add(ModBlocks.COBBLED_SCHIST_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.POLISHED_SCHIST_STAIRS);
        dropSelf(ModBlocks.POLISHED_SCHIST_WALL);
        add(ModBlocks.POLISHED_SCHIST_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.SCHIST_BRICKS_STAIRS);
        dropSelf(ModBlocks.SCHIST_BRICKS_WALL);
        add(ModBlocks.SCHIST_BRICKS_SLAB, this::createSlabItemTable);

        add(ModBlocks.ECLOGITE,
                createSingleItemTableWithSilkTouch(ModBlocks.ECLOGITE, ModBlocks.COBBLED_ECLOGITE));
        dropSelf(ModBlocks.COBBLED_ECLOGITE);
        dropSelf(ModBlocks.POLISHED_ECLOGITE);
        dropSelf(ModBlocks.ECLOGITE_BRICKS);
        dropSelf(ModBlocks.CHISELED_ECLOGITE);
        dropSelf(ModBlocks.CRACKED_ECLOGITE_BRICKS);
        dropSelf(ModBlocks.ECLOGITE_STAIRS);
        dropSelf(ModBlocks.ECLOGITE_WALL);
        add(ModBlocks.ECLOGITE_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.COBBLED_ECLOGITE_STAIRS);
        dropSelf(ModBlocks.COBBLED_ECLOGITE_WALL);
        add(ModBlocks.COBBLED_ECLOGITE_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.POLISHED_ECLOGITE_STAIRS);
        dropSelf(ModBlocks.POLISHED_ECLOGITE_WALL);
        add(ModBlocks.POLISHED_ECLOGITE_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.ECLOGITE_BRICKS_STAIRS);
        dropSelf(ModBlocks.ECLOGITE_BRICKS_WALL);
        add(ModBlocks.ECLOGITE_BRICKS_SLAB, this::createSlabItemTable);

        add(
                ModBlocks.LIMESTONE,
                createSingleItemTableWithSilkTouch(
                        ModBlocks.LIMESTONE,
                        ModBlocks.RUBBLED_LIMESTONE
                )
        );
        dropSelf(ModBlocks.WEATHERED_LIMESTONE);
        dropSelf(ModBlocks.LIMESTONE_PILLAR);
        dropSelf(ModBlocks.POLISHED_LIMESTONE);
        dropSelf(ModBlocks.RUBBLED_LIMESTONE);
        add(ModBlocks.LIMESTONE_BRICK_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.LIMESTONE_BRICKS);
        dropSelf(ModBlocks.LIMESTONE_BRICK_STAIRS);
        dropSelf(ModBlocks.LIMESTONE_BRICK_WALL);
        add(ModBlocks.LIMESTONE_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.LIMESTONE_STAIRS);
        dropSelf(ModBlocks.MOSSY_LIMESTONE);
        dropSelf(ModBlocks.LIMESTONE_WALL);
        add(ModBlocks.POLISHED_LIMESTONE_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.POLISHED_LIMESTONE_STAIRS);
        dropSelf(ModBlocks.POLISHED_LIMESTONE_WALL);
        add(ModBlocks.RUBBLED_LIMESTONE_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.RUBBLED_LIMESTONE_STAIRS);
        dropSelf(ModBlocks.RUBBLED_LIMESTONE_WALL);

        dropSelf(ModBlocks.RENDZINA);
        dropSelf(ModBlocks.LITHOSOL);

        // Hinoki wood family and foliage drops.
        dropSelf(ModBlocks.HINOKI_LOG);
        dropSelf(ModBlocks.HINOKI_WOOD);
        dropSelf(ModBlocks.STRIPPED_HINOKI_LOG);
        dropSelf(ModBlocks.STRIPPED_HINOKI_WOOD);
        dropSelf(ModBlocks.HINOKI_PLANKS);
        dropSelf(ModBlocks.HINOKI_SAPLING);
        dropSelf(ModBlocks.HINOKI_TRAPDOOR);
        dropSelf(ModBlocks.HINOKI_STAIRS);
        add(ModBlocks.HINOKI_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.HINOKI_BUTTON);
        dropSelf(ModBlocks.HINOKI_PRESSURE_PLATE);
        dropSelf(ModBlocks.HINOKI_FENCE);
        dropSelf(ModBlocks.HINOKI_FENCE_GATE);
        add(ModBlocks.HINOKI_DOOR, this::createDoorTable);
        add(ModBlocks.POTTED_HINOKI_SAPLING,
                block -> createPotFlowerItemTable(ModBlocks.HINOKI_SAPLING));
        add(ModBlocks.HINOKI_LEAVES,
                block -> createLeavesDrops(block, ModBlocks.HINOKI_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES));

        // Ginkgo family loot tables (same mechanical conventions as Maple).
        dropSelf(ModBlocks.GINKGO_LOG);
        dropSelf(ModBlocks.GINKGO_WOOD);
        dropSelf(ModBlocks.STRIPPED_GINKGO_LOG);
        dropSelf(ModBlocks.STRIPPED_GINKGO_WOOD);
        dropSelf(ModBlocks.GINKGO_PLANKS);
        dropSelf(ModBlocks.GINKGO_SAPLING);
        dropSelf(ModBlocks.GINKGO_TRAPDOOR);
        dropSelf(ModBlocks.GINKGO_STAIRS);
        add(ModBlocks.GINKGO_SLAB, this::createSlabItemTable);
        dropSelf(ModBlocks.GINKGO_BUTTON);
        dropSelf(ModBlocks.GINKGO_PRESSURE_PLATE);
        dropSelf(ModBlocks.GINKGO_FENCE);
        dropSelf(ModBlocks.GINKGO_FENCE_GATE);
        add(ModBlocks.GINKGO_DOOR, this::createDoorTable);
        add(ModBlocks.POTTED_GINKGO_SAPLING, block -> createPotFlowerItemTable(ModBlocks.GINKGO_SAPLING));
        add(ModBlocks.GINKGO_LEAVES,
                block -> createLeavesDrops(block, ModBlocks.GINKGO_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES));

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
        dropSelf(ModBlocks.MAPLE_PRESSURE_PLATE);
        dropSelf(ModBlocks.MAPLE_FENCE);
        dropSelf(ModBlocks.MAPLE_FENCE_GATE);

        add(ModBlocks.MAPLE_DOOR, this::createDoorTable);

        add(ModBlocks.POTTED_MAPLE_SAPLING, block -> createPotFlowerItemTable(ModBlocks.MAPLE_SAPLING));

        dropSelf(ModBlocks.GLOWSHROOM);

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

        dropSelf(ModBlocks.BELLFLOWERS);
        dropSelf(ModBlocks.DWARF_BAMBOO);
        dropSelf(ModBlocks.BUSH_CLOVER);
        // vv change for grass
        add(ModBlocks.SILVER_GRASS, block -> createShearsOnlyDrop(block));

    }
}
