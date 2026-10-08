package net.chickalo.bugsbugsandmorebugs.block;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.chickalo.bugsbugsandmorebugs.block.custom.BarberryBushBlock;
import net.chickalo.bugsbugsandmorebugs.block.custom.DaikonCropBlock;
import net.chickalo.bugsbugsandmorebugs.block.custom.HaskapBerryBushBlock;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.sounds.AmbientLeavesBlockSoundPlayer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Function;

public class ModBlocks {
    public static final Block INFESTED_DIRT = registerBlock("infested_dirt",
            properties -> new Block(properties.strength(0.25F)
                    .sound(SoundType.GRAVEL)));

    public static final Block MAPLE_LOG = registerBlock("maple_log",
            properties -> new RotatedPillarBlock(properties.strength(2f).instrument(NoteBlockInstrument.BASS)
                    .sound(SoundType.WOOD).ignitedByLava()));
    public static final Block MAPLE_WOOD = registerBlock("maple_wood",
            properties -> new RotatedPillarBlock(properties.strength(2f).instrument(NoteBlockInstrument.BASS)
                    .sound(SoundType.WOOD).ignitedByLava()));
    public static final Block STRIPPED_MAPLE_LOG = registerBlock("stripped_maple_log",
            properties -> new RotatedPillarBlock(properties.strength(2f).instrument(NoteBlockInstrument.BASS)
                    .sound(SoundType.WOOD).ignitedByLava()));
    public static final Block STRIPPED_MAPLE_WOOD = registerBlock("stripped_maple_wood",
            properties -> new RotatedPillarBlock(properties.strength(2f).instrument(NoteBlockInstrument.BASS)
                    .sound(SoundType.WOOD).ignitedByLava()));

    public static final Block MAPLE_PLANKS = registerBlock("maple_planks",
            properties -> new Block(properties.mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava()));

    public static final Block MAPLE_LEAVES = registerBlock("maple_leaves",
            properties -> new UntintedParticleLeavesBlock(0.01f, ParticleTypes.CHERRY_LEAVES, AmbientLeavesBlockSoundPlayer.noAmbientSound(), properties
                    .mapColor(MapColor.PLANT).strength(0.2F).randomTicks().sound(SoundType.POPLAR_LEAVES)
                    .noOcclusion().isValidSpawn(Blocks::ocelotOrParrot).isSuffocating(Blocks::never)
                    .ignitedByLava().pushReaction(PushReaction.POPPED).isRedstoneConductor(Blocks::never)));

    public static final Block DAIKON_CROP = registerBlockWithoutBlockItem("daikon_crop",
            properties -> new DaikonCropBlock(properties.noCollision().randomTicks().instabreak().sound(SoundType.CROP)
                    .pushReaction(PushReaction.POPPED)));

    public static final Block HASKAP_BERRY_BUSH = registerBlockWithoutBlockItem("haskap_berry_bush",
            properties -> new HaskapBerryBushBlock(properties.noCollision().instabreak().sound(SoundType.SWEET_BERRY_BUSH)
                    .pushReaction(PushReaction.POPPED)));

    public static final Block BARBERRY_BUSH = registerBlockWithoutBlockItem("barberry_bush",
            properties -> new BarberryBushBlock(properties.noCollision().instabreak().sound(SoundType.SWEET_BERRY_BUSH)
                    .pushReaction(PushReaction.POPPED)));




    public static ResourceKey<Block> getRK(Block block) {
        return BuiltInRegistries.BLOCK.getResourceKey(block).get();
    }

    private static Block registerBlock(String name, Function<BlockBehaviour.Properties, Block> function) {
        Block toRegister = function.apply(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name))));
        registerBlockItem(name, toRegister);
        return Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name), toRegister);
    }

    private static Block registerBlockWithoutBlockItem(String name, Function<BlockBehaviour.Properties, Block> function) {
        Block toRegister = function.apply(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name))));;
        return Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name), toRegister);
    }



    private static void registerBlockItem(String name, Block block) {
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name),
                new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix()
                        .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name)))));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(output -> {
            output.accept(block);
        });

    }

    public static void registerModBlocks() {
        BugsBugsAndMoreBugs.LOGGER.info("Registering Mod Blocks for " + BugsBugsAndMoreBugs.MOD_ID);
    }
}
