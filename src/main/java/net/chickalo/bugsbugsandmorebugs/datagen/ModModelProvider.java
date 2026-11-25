package net.chickalo.bugsbugsandmorebugs.datagen;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.block.custom.BarberryBushBlock;
import net.chickalo.bugsbugsandmorebugs.block.custom.DaikonCropBlock;
import net.chickalo.bugsbugsandmorebugs.block.custom.HaskapBerryBushBlock;
import net.chickalo.bugsbugsandmorebugs.item.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TexturedModel;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        // Elsewhere secondary stone models
        blockModelGenerators.family(ModBlocks.DACITE)
                .stairs(ModBlocks.DACITE_STAIRS)
                .slab(ModBlocks.DACITE_SLAB)
                .wall(ModBlocks.DACITE_WALL);
        blockModelGenerators.family(ModBlocks.POLISHED_DACITE)
                .stairs(ModBlocks.POLISHED_DACITE_STAIRS)
                .slab(ModBlocks.POLISHED_DACITE_SLAB)
                .wall(ModBlocks.POLISHED_DACITE_WALL);
        blockModelGenerators.family(ModBlocks.CHERT)
                .stairs(ModBlocks.CHERT_STAIRS)
                .slab(ModBlocks.CHERT_SLAB)
                .wall(ModBlocks.CHERT_WALL);
        blockModelGenerators.family(ModBlocks.POLISHED_CHERT)
                .stairs(ModBlocks.POLISHED_CHERT_STAIRS)
                .slab(ModBlocks.POLISHED_CHERT_SLAB)
                .wall(ModBlocks.POLISHED_CHERT_WALL);
        blockModelGenerators.family(ModBlocks.SERPENTINITE)
                .stairs(ModBlocks.SERPENTINITE_STAIRS)
                .slab(ModBlocks.SERPENTINITE_SLAB)
                .wall(ModBlocks.SERPENTINITE_WALL);
        blockModelGenerators.family(ModBlocks.POLISHED_SERPENTINITE)
                .stairs(ModBlocks.POLISHED_SERPENTINITE_STAIRS)
                .slab(ModBlocks.POLISHED_SERPENTINITE_SLAB)
                .wall(ModBlocks.POLISHED_SERPENTINITE_WALL);

        blockModelGenerators.family(ModBlocks.AMPHIBOLITE)
                .stairs(ModBlocks.AMPHIBOLITE_STAIRS)
                .slab(ModBlocks.AMPHIBOLITE_SLAB)
                .wall(ModBlocks.AMPHIBOLITE_WALL);
        blockModelGenerators.family(ModBlocks.POLISHED_AMPHIBOLITE)
                .stairs(ModBlocks.POLISHED_AMPHIBOLITE_STAIRS)
                .slab(ModBlocks.POLISHED_AMPHIBOLITE_SLAB)
                .wall(ModBlocks.POLISHED_AMPHIBOLITE_WALL);
        blockModelGenerators.family(ModBlocks.AMPHIBOLITE_BRICKS)
                .stairs(ModBlocks.AMPHIBOLITE_BRICK_STAIRS)
                .slab(ModBlocks.AMPHIBOLITE_BRICK_SLAB)
                .wall(ModBlocks.AMPHIBOLITE_BRICK_WALL);
        ModBlocks.BASE_ORES.forEach(blockModelGenerators::createTrivialCube);
        ModBlocks.MINERAL_STORAGE_BLOCKS.forEach(blockModelGenerators::createTrivialCube);
        blockModelGenerators.createTrivialCube(ModBlocks.MOSSY_RHYOLITE);
        blockModelGenerators.createTrivialCube(ModBlocks.CHISELED_AMPHIBOLITE);
        blockModelGenerators.createTrivialCube(ModBlocks.CHISELED_AMPHIBOLITE_BRICKS);
        blockModelGenerators.createTrivialCube(ModBlocks.LAPILLI);

        blockModelGenerators.createTrivialCube(ModBlocks.TEMP_PORTAL);

        blockModelGenerators.createTrivialCube(ModBlocks.INFESTED_DIRT);
        blockModelGenerators.family(ModBlocks.COBBLED_RHYOLITE)
                .stairs(ModBlocks.COBBLED_RHYOLITE_STAIRS)
                .slab(ModBlocks.COBBLED_RHYOLITE_SLAB)
                .wall(ModBlocks.COBBLED_RHYOLITE_WALL);
        blockModelGenerators.family(ModBlocks.RHYOLITE)
                .stairs(ModBlocks.RHYOLITE_STAIRS)
                .slab(ModBlocks.RHYOLITE_SLAB)
                .wall(ModBlocks.RHYOLITE_WALL);
        blockModelGenerators.family(ModBlocks.POLISHED_RHYOLITE)
                .stairs(ModBlocks.POLISHED_RHYOLITE_STAIRS)
                .slab(ModBlocks.POLISHED_RHYOLITE_SLAB)
                .wall(ModBlocks.POLISHED_RHYOLITE_WALL);
        blockModelGenerators.family(ModBlocks.RHYOLITE_BRICKS)
                .stairs(ModBlocks.RHYOLITE_BRICK_STAIRS)
                .slab(ModBlocks.RHYOLITE_BRICK_SLAB)
                .wall(ModBlocks.RHYOLITE_BRICK_WALL);
        blockModelGenerators.createTrivialCube(ModBlocks.CHISELED_RHYOLITE);
        blockModelGenerators.createTrivialCube(ModBlocks.CRACKED_RHYOLITE_BRICKS);
        blockModelGenerators.family(ModBlocks.SCHIST)
                .stairs(ModBlocks.SCHIST_STAIRS)
                .slab(ModBlocks.SCHIST_SLAB)
                .wall(ModBlocks.SCHIST_WALL);
        blockModelGenerators.family(ModBlocks.COBBLED_SCHIST)
                .stairs(ModBlocks.COBBLED_SCHIST_STAIRS)
                .slab(ModBlocks.COBBLED_SCHIST_SLAB)
                .wall(ModBlocks.COBBLED_SCHIST_WALL);
        blockModelGenerators.family(ModBlocks.POLISHED_SCHIST)
                .stairs(ModBlocks.POLISHED_SCHIST_STAIRS)
                .slab(ModBlocks.POLISHED_SCHIST_SLAB)
                .wall(ModBlocks.POLISHED_SCHIST_WALL);
        blockModelGenerators.family(ModBlocks.SCHIST_BRICKS)
                .stairs(ModBlocks.SCHIST_BRICKS_STAIRS)
                .slab(ModBlocks.SCHIST_BRICKS_SLAB)
                .wall(ModBlocks.SCHIST_BRICKS_WALL);
        blockModelGenerators.createTrivialCube(ModBlocks.CHISELED_SCHIST);
        blockModelGenerators.createTrivialCube(ModBlocks.CRACKED_SCHIST_BRICKS);
        blockModelGenerators.family(ModBlocks.ECLOGITE)
                .stairs(ModBlocks.ECLOGITE_STAIRS)
                .slab(ModBlocks.ECLOGITE_SLAB)
                .wall(ModBlocks.ECLOGITE_WALL);
        blockModelGenerators.family(ModBlocks.COBBLED_ECLOGITE)
                .stairs(ModBlocks.COBBLED_ECLOGITE_STAIRS)
                .slab(ModBlocks.COBBLED_ECLOGITE_SLAB)
                .wall(ModBlocks.COBBLED_ECLOGITE_WALL);
        blockModelGenerators.family(ModBlocks.POLISHED_ECLOGITE)
                .stairs(ModBlocks.POLISHED_ECLOGITE_STAIRS)
                .slab(ModBlocks.POLISHED_ECLOGITE_SLAB)
                .wall(ModBlocks.POLISHED_ECLOGITE_WALL);
        blockModelGenerators.family(ModBlocks.ECLOGITE_BRICKS)
                .stairs(ModBlocks.ECLOGITE_BRICKS_STAIRS)
                .slab(ModBlocks.ECLOGITE_BRICKS_SLAB)
                .wall(ModBlocks.ECLOGITE_BRICKS_WALL);
        blockModelGenerators.createTrivialCube(ModBlocks.CHISELED_ECLOGITE);
        blockModelGenerators.createTrivialCube(ModBlocks.CRACKED_ECLOGITE_BRICKS);
        blockModelGenerators.createTrivialCube(ModBlocks.LOAM);
        blockModelGenerators.createTrivialCube(ModBlocks.ASHSTONE);
        blockModelGenerators.createTrivialCube(ModBlocks.SHATTERSTONE);
        blockModelGenerators.createTrivialCube(ModBlocks.MOSSY_LIMESTONE);
        blockModelGenerators.family(ModBlocks.RUBBLED_LIMESTONE)
                .stairs(ModBlocks.RUBBLED_LIMESTONE_STAIRS)
                .slab(ModBlocks.RUBBLED_LIMESTONE_SLAB)
                .wall(ModBlocks.RUBBLED_LIMESTONE_WALL);
        blockModelGenerators.family(ModBlocks.LIMESTONE)
                .stairs(ModBlocks.LIMESTONE_STAIRS)
                .slab(ModBlocks.LIMESTONE_SLAB)
                .wall(ModBlocks.LIMESTONE_WALL);
        blockModelGenerators.family(ModBlocks.POLISHED_LIMESTONE)
                .stairs(ModBlocks.POLISHED_LIMESTONE_STAIRS)
                .slab(ModBlocks.POLISHED_LIMESTONE_SLAB)
                .wall(ModBlocks.POLISHED_LIMESTONE_WALL);
        blockModelGenerators.family(ModBlocks.LIMESTONE_BRICKS)
                .stairs(ModBlocks.LIMESTONE_BRICK_STAIRS)
                .slab(ModBlocks.LIMESTONE_BRICK_SLAB)
                .wall(ModBlocks.LIMESTONE_BRICK_WALL);
        blockModelGenerators.createTrivialCube(ModBlocks.WEATHERED_LIMESTONE);
        blockModelGenerators.createRotatedPillarWithHorizontalVariant(ModBlocks.LIMESTONE_PILLAR, TexturedModel.COLUMN_ALT, TexturedModel.COLUMN_HORIZONTAL_ALT);

        blockModelGenerators.createTrivialCube(ModBlocks.RENDZINA);
        blockModelGenerators.createTrivialCube(ModBlocks.LITHOSOL);

        blockModelGenerators.woodProvider(ModBlocks.HINOKI_LOG)
                .log(ModBlocks.HINOKI_LOG).wood(ModBlocks.HINOKI_WOOD);
        blockModelGenerators.woodProvider(ModBlocks.STRIPPED_HINOKI_LOG)
                .log(ModBlocks.STRIPPED_HINOKI_LOG).wood(ModBlocks.STRIPPED_HINOKI_WOOD);
        blockModelGenerators.family(ModBlocks.HINOKI_PLANKS)
                .stairs(ModBlocks.HINOKI_STAIRS).slab(ModBlocks.HINOKI_SLAB)
                .button(ModBlocks.HINOKI_BUTTON).pressurePlate(ModBlocks.HINOKI_PRESSURE_PLATE)
                .fence(ModBlocks.HINOKI_FENCE).fenceGate(ModBlocks.HINOKI_FENCE_GATE);
        blockModelGenerators.createDoor(ModBlocks.HINOKI_DOOR);
        blockModelGenerators.createTrapdoor(ModBlocks.HINOKI_TRAPDOOR);
        blockModelGenerators.createTrivialCube(ModBlocks.HINOKI_LEAVES);
        blockModelGenerators.createPlantWithDefaultItem(ModBlocks.HINOKI_SAPLING,
                ModBlocks.POTTED_HINOKI_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);

        blockModelGenerators.woodProvider(ModBlocks.GINKGO_LOG).log(ModBlocks.GINKGO_LOG).wood(ModBlocks.GINKGO_WOOD);
        blockModelGenerators.woodProvider(ModBlocks.STRIPPED_GINKGO_LOG).log(ModBlocks.STRIPPED_GINKGO_LOG).wood(ModBlocks.STRIPPED_GINKGO_WOOD);
        blockModelGenerators.createDoor(ModBlocks.GINKGO_DOOR);
        blockModelGenerators.createTrapdoor(ModBlocks.GINKGO_TRAPDOOR);
        blockModelGenerators.family(ModBlocks.GINKGO_PLANKS)
                .stairs(ModBlocks.GINKGO_STAIRS).slab(ModBlocks.GINKGO_SLAB)
                .button(ModBlocks.GINKGO_BUTTON).pressurePlate(ModBlocks.GINKGO_PRESSURE_PLATE)
                .fence(ModBlocks.GINKGO_FENCE).fenceGate(ModBlocks.GINKGO_FENCE_GATE);
        blockModelGenerators.createTrivialCube(ModBlocks.GINKGO_LEAVES);
        blockModelGenerators.createPlantWithDefaultItem(ModBlocks.GINKGO_SAPLING,
                ModBlocks.POTTED_GINKGO_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);

        blockModelGenerators.woodProvider(ModBlocks.MAPLE_LOG).log(ModBlocks.MAPLE_LOG).wood(ModBlocks.MAPLE_WOOD);
        blockModelGenerators.woodProvider(ModBlocks.STRIPPED_MAPLE_LOG).log(ModBlocks.STRIPPED_MAPLE_LOG).wood(ModBlocks.STRIPPED_MAPLE_WOOD);
        blockModelGenerators.createDoor(ModBlocks.MAPLE_DOOR);
        blockModelGenerators.createTrapdoor(ModBlocks.MAPLE_TRAPDOOR);
        blockModelGenerators.family(ModBlocks.MAPLE_PLANKS)
                        .stairs(ModBlocks.MAPLE_STAIRS)
                        .slab(ModBlocks.MAPLE_SLAB)
                        .button(ModBlocks.MAPLE_BUTTON)
                        .pressurePlate(ModBlocks.MAPLE_PRESSURE_PLATE)
                        .fence(ModBlocks.MAPLE_FENCE)
                        .fenceGate(ModBlocks.MAPLE_FENCE_GATE);
        blockModelGenerators.createTrivialCube(ModBlocks.MAPLE_LEAVES);

        blockModelGenerators.createPlantWithDefaultItem(ModBlocks.MAPLE_SAPLING, ModBlocks.POTTED_MAPLE_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);

        blockModelGenerators.createCropBlock(ModBlocks.DAIKON_CROP, DaikonCropBlock.AGE, 0, 1, 2, 3);
        blockModelGenerators.createCrossBlock(ModBlocks.HASKAP_BERRY_BUSH, BlockModelGenerators.PlantType.NOT_TINTED, HaskapBerryBushBlock.AGE,0, 1, 2, 3);
        blockModelGenerators.createCrossBlock(ModBlocks.BARBERRY_BUSH, BlockModelGenerators.PlantType.NOT_TINTED, BarberryBushBlock.AGE,0, 1, 2, 3);
        blockModelGenerators.createPlantWithDefaultItem(ModBlocks.GLOWSHROOM, ModBlocks.POTTED_GLOWSHROOM, BlockModelGenerators.PlantType.NOT_TINTED);
        blockModelGenerators.createMushroomBlock(ModBlocks.GLOWSHROOM_BLOCK);

        blockModelGenerators.createCrossBlock(ModBlocks.BELLFLOWERS, BlockModelGenerators.PlantType.NOT_TINTED);
        blockModelGenerators.createCrossBlockWithDefaultItem(ModBlocks.ALPINE_GRASS, BlockModelGenerators.PlantType.NOT_TINTED);
        blockModelGenerators.createCrossBlockWithDefaultItem(ModBlocks.HAKONE_GRASS, BlockModelGenerators.PlantType.NOT_TINTED);
        blockModelGenerators.createCrossBlockWithDefaultItem(ModBlocks.IRIS_FLOWER, BlockModelGenerators.PlantType.NOT_TINTED);
        // Lotus flower model, item and blockstate are manually maintained in main/resources
        // so the cross sits at pad level rather than hovering one block above it.

    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        ModItems.MINING_MATERIALS.forEach(item -> itemModelGenerators.generateFlatItem(item, ModelTemplates.FLAT_ITEM));
        itemModelGenerators.generateFlatItem(ModItems.EXOSKELETON, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.AMEISENHAUFEN_MUSIC_DISC, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.DAIKON, ModelTemplates.FLAT_ITEM);

    }
}
