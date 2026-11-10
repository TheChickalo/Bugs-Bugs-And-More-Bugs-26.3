package net.chickalo.bugsbugsandmorebugs.item;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.datagen.ModJukeboxSongs;
import net.chickalo.bugsbugsandmorebugs.entity.ModEntities;
import net.chickalo.bugsbugsandmorebugs.food.ModFoods;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.item.component.Compostable;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

import java.util.List;
import java.util.Optional;

import java.util.function.Function;

public class ModItems {
    public static final Item EXOSKELETON = registerItem("exoskeleton", Item::new);
    public static final Item AMEISENHAUFEN_MUSIC_DISC = registerItem("ameisenhaufen_music_disc", properties -> new Item(properties.jukeboxPlayable(ModJukeboxSongs.AMEISENHAUFEN_KEY).stacksTo(1).rarity(Rarity.RARE)));
    public static final Item ANTWORKER_SPAWN_EGG = registerItem("antworker_spawn_egg",
            properties -> new SpawnEggItem(properties.spawnEgg(ModEntities.ANTWORKER)));
    public static final Item DAIKON = registerItem("daikon", properties -> new Item(properties.food(ModFoods.DAIKON, ModFoods.DAIKON_CONSUMABLE).component(DataComponents.COMPOSTABLE, new Compostable(new ResolvableInt.Constant(65)))));
    public static final Item DAIKON_SEEDS = registerItem("daikon_seeds",
            properties -> new BlockItem(ModBlocks.DAIKON_CROP, properties.useItemDescriptionPrefix().component(DataComponents.COMPOSTABLE, new Compostable(new ResolvableInt.Constant(30)))));
    public static final Item HASKAP_BERRIES = registerItem("haskap_berries",
            properties -> new BlockItem(ModBlocks.HASKAP_BERRY_BUSH, properties.useItemDescriptionPrefix().food(ModFoods.HASKAP_BERRIES, ModFoods.HASKAP_BERRIES_CONSUMABLE).component(DataComponents.COMPOSTABLE, new Compostable(new ResolvableInt.Constant(30)))));
    public static final Item BARBERRIES = registerItem("barberries",
            properties -> new BlockItem(ModBlocks.BARBERRY_BUSH, properties.useItemDescriptionPrefix().food(ModFoods.BARBERRIES, ModFoods.BARBERRIES_CONSUMABLE).component(DataComponents.COMPOSTABLE, new Compostable(new ResolvableInt.Constant(30)))));

    public static final Item REINFORCED_SHIELD =
            registerItem("reinforced_shield", properties -> new ShieldItem(properties.durability(504).delayedComponent(
                    DataComponents.BLOCKS_ATTACKS, context -> new BlocksAttacks(0.25F,
                                    1.0F, List.of(new BlocksAttacks.DamageReduction(
                                            90.0F, Optional.empty(), 0.0F, 1.0F)),
                                    new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
                                    Optional.of(context.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
                                    Optional.of(SoundEvents.SHIELD_BLOCK), Optional.of(SoundEvents.SHIELD_BREAK)))
                    .component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK))
            );

    private static Item registerItem(String name, Function<Item.Properties, Item> function) {
        return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name),
                function.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name)))));
    }

    public static ResourceKey<Item> getRK(Item item) {
        return BuiltInRegistries.ITEM.getResourceKey(item).get();
    }

    public static void registerModItems() {
        BugsBugsAndMoreBugs.LOGGER.info("Registering Mod Items for " + BugsBugsAndMoreBugs.MOD_ID);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(output -> {
            output.accept(EXOSKELETON);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(output -> {
            output.accept(ModBlocks.RHYOLITE);
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(output -> {
            output.accept(ModBlocks.COBBLED_RHYOLITE);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(output -> {
            output.accept(ANTWORKER_SPAWN_EGG);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(output -> {
            output.accept(REINFORCED_SHIELD);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(output -> {
            output.accept(AMEISENHAUFEN_MUSIC_DISC);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(output -> {
            output.accept(ModBlocks.MAPLE_LOG);
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(output -> {
            output.accept(ModBlocks.MAPLE_PLANKS);
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(output -> {
            output.accept(ModBlocks.MAPLE_WOOD);
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(output -> {
            output.accept(ModBlocks.STRIPPED_MAPLE_LOG);
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(output -> {
            output.accept(ModBlocks.STRIPPED_MAPLE_WOOD);
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(output -> {
            output.accept(ModBlocks.MAPLE_PLANKS);
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(output -> {
            output.accept(ModBlocks.MAPLE_LEAVES);
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(output -> {
            output.accept(ModItems.DAIKON_SEEDS);
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(output -> {
            output.accept(ModItems.DAIKON);
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(output -> {
            output.accept(ModItems.BARBERRIES);
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(output -> {
            output.accept(ModItems.HASKAP_BERRIES);
        });

    }

}
