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

    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.generateFlatItem(ModItems.EXOSKELETON, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.AMEISENHAUFEN_MUSIC_DISC, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.DAIKON, ModelTemplates.FLAT_ITEM);

    }
}
