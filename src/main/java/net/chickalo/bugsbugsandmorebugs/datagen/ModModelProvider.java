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
        blockModelGenerators.createTrivialCube(ModBlocks.INFESTED_DIRT);

        blockModelGenerators.woodProvider(ModBlocks.MAPLE_LOG).log(ModBlocks.MAPLE_LOG).wood(ModBlocks.MAPLE_WOOD);
        blockModelGenerators.woodProvider(ModBlocks.STRIPPED_MAPLE_LOG).log(ModBlocks.STRIPPED_MAPLE_LOG).wood(ModBlocks.STRIPPED_MAPLE_WOOD);

//        blockModelGenerators.createTrivialCube(ModBlocks.MAPLE_PLANKS);
        blockModelGenerators.createTrivialBlock(ModBlocks.MAPLE_LEAVES, TexturedModel.LEAVES);

        blockModelGenerators.createDoor(ModBlocks.MAPLE_DOOR);
        blockModelGenerators.createTrapdoor(ModBlocks.MAPLE_TRAPDOOR);
        blockModelGenerators.family(ModBlocks.MAPLE_PLANKS)
                        .stairs(ModBlocks.MAPLE_STAIRS)
                        .slab(ModBlocks.MAPLE_SLAB)
                        .button(ModBlocks.MAPLE_BUTTON)
                        .pressurePlate(ModBlocks.MAPLE_PRESSURE_PLATE);

        blockModelGenerators.createPlantWithDefaultItem(ModBlocks.MAPLE_SAPLING, ModBlocks.POTTED_MAPLE_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);

        blockModelGenerators.createCropBlock(ModBlocks.DAIKON_CROP, DaikonCropBlock.AGE, 0, 1, 2, 3);
        blockModelGenerators.createCrossBlock(ModBlocks.HASKAP_BERRY_BUSH, BlockModelGenerators.PlantType.NOT_TINTED, HaskapBerryBushBlock.AGE,0, 1, 2, 3);
        blockModelGenerators.createCrossBlock(ModBlocks.BARBERRY_BUSH, BlockModelGenerators.PlantType.NOT_TINTED, BarberryBushBlock.AGE,0, 1, 2, 3);



    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.generateFlatItem(ModItems.EXOSKELETON, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.AMEISENHAUFEN_MUSIC_DISC, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.DAIKON, ModelTemplates.FLAT_ITEM);

    }
}
