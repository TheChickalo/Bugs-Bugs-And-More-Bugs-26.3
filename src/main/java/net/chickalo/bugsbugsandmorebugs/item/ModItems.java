package net.chickalo.bugsbugsandmorebugs.item;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.datagen.ModJukeboxSongs;
import net.chickalo.bugsbugsandmorebugs.entity.ModEntities;
import net.chickalo.bugsbugsandmorebugs.food.ModFoods;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.ArmorType;
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
    public static final Item CANNEL = registerItem("cannel", p -> new Item(p.component(DataComponents.COOKING_FUEL, new CookingFuel(
                    ContextIntProviders.COOKING_TIME_COAL, ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER))));
    public static final Item RAW_FERRUM = registerItem("raw_ferrum", Item::new);
    public static final Item FERRUM_INGOT = registerItem("ferrum_ingot", Item::new);
    public static final Item RAW_CHALCOR = registerItem("raw_chalcor", Item::new);
    public static final Item CHALCOR_INGOT = registerItem("chalcor_ingot", Item::new);
    public static final Item RAW_ELECTRUM = registerItem("raw_electrum", Item::new);
    public static final Item ELECTRUM_INGOT = registerItem("electrum_ingot", Item::new);
    public static final Item CORUNDUM_DUST = registerItem("corundum_dust", Item::new);
    public static final Item RAW_INDIUM = registerItem("raw_indium", Item::new);
    public static final Item INDIUM_INGOT = registerItem("indium_ingot", Item::new);

    public static final List<Item> MINING_MATERIALS = List.of(
            CANNEL,
            RAW_FERRUM,
            FERRUM_INGOT,
            RAW_CHALCOR,
            CHALCOR_INGOT,
            RAW_ELECTRUM,
            ELECTRUM_INGOT,
            CORUNDUM_DUST,
            RAW_INDIUM,
            INDIUM_INGOT);

    public static final Item FERRUM_PICKAXE = registerItem("ferrum_pickaxe", p -> new Item(p.pickaxe(ElsewhereEquipmentMaterials.FERRUM, 1.0F, -2.8F)));
    public static final Item FERRUM_AXE = registerItem("ferrum_axe", p -> new Item(p.axe(ElsewhereEquipmentMaterials.FERRUM, 5.0F, -3.1F)));
    public static final Item FERRUM_SHOVEL = registerItem("ferrum_shovel", p -> new Item(p.shovel(ElsewhereEquipmentMaterials.FERRUM, 1.5F, -3.0F)));
    public static final Item FERRUM_HOE = registerItem("ferrum_hoe", p -> new Item(p.hoe(ElsewhereEquipmentMaterials.FERRUM, -2.0F, -1.0F)));
    public static final Item FERRUM_BROADSWORD = registerItem("ferrum_broadsword", p -> new Item(p.sword(ElsewhereEquipmentMaterials.FERRUM, 5.0F, -2.8F)));
    public static final Item FERRUM_HALBERD = registerItem("ferrum_halberd", p -> new Item(p.sword(ElsewhereEquipmentMaterials.FERRUM, 4.0F, -2.6F)));
    public static final Item FERRUM_HELMET = registerItem("ferrum_helmet", p -> new Item(p.humanoidArmor(ElsewhereEquipmentMaterials.FERRUM_ARMOR, ArmorType.HELMET).durability(ArmorType.HELMET.getDurability(ElsewhereEquipmentMaterials.FERRUM_ARMOR.durability()))));
    public static final Item FERRUM_CHESTPLATE = registerItem("ferrum_chestplate", p -> new Item(p.humanoidArmor(ElsewhereEquipmentMaterials.FERRUM_ARMOR, ArmorType.CHESTPLATE).durability(ArmorType.CHESTPLATE.getDurability(ElsewhereEquipmentMaterials.FERRUM_ARMOR.durability()))));
    public static final Item FERRUM_LEGGINGS = registerItem("ferrum_leggings", p -> new Item(p.humanoidArmor(ElsewhereEquipmentMaterials.FERRUM_ARMOR, ArmorType.LEGGINGS).durability(ArmorType.LEGGINGS.getDurability(ElsewhereEquipmentMaterials.FERRUM_ARMOR.durability()))));
    public static final Item FERRUM_BOOTS = registerItem("ferrum_boots", p -> new Item(p.humanoidArmor(ElsewhereEquipmentMaterials.FERRUM_ARMOR, ArmorType.BOOTS).durability(ArmorType.BOOTS.getDurability(ElsewhereEquipmentMaterials.FERRUM_ARMOR.durability()))));
    public static final Item CHALCOR_PICKAXE = registerItem("chalcor_pickaxe", p -> new Item(p.pickaxe(ElsewhereEquipmentMaterials.CHALCOR, 1.0F, -2.8F)));
    public static final Item CHALCOR_AXE = registerItem("chalcor_axe", p -> new Item(p.axe(ElsewhereEquipmentMaterials.CHALCOR, 5.0F, -3.1F)));
    public static final Item CHALCOR_SHOVEL = registerItem("chalcor_shovel", p -> new Item(p.shovel(ElsewhereEquipmentMaterials.CHALCOR, 1.5F, -3.0F)));
    public static final Item CHALCOR_HOE = registerItem("chalcor_hoe", p -> new Item(p.hoe(ElsewhereEquipmentMaterials.CHALCOR, -1.0F, -2.0F)));
    public static final Item CHALCOR_BROADSWORD = registerItem("chalcor_broadsword", p -> new Item(p.sword(ElsewhereEquipmentMaterials.CHALCOR, 5.0F, -2.8F)));
    public static final Item CHALCOR_HALBERD = registerItem("chalcor_halberd", p -> new Item(p.sword(ElsewhereEquipmentMaterials.CHALCOR, 4.0F, -2.6F)));
    public static final Item CHALCOR_HELMET = registerItem("chalcor_helmet", p -> new Item(p.humanoidArmor(ElsewhereEquipmentMaterials.CHALCOR_ARMOR, ArmorType.HELMET).durability(ArmorType.HELMET.getDurability(ElsewhereEquipmentMaterials.CHALCOR_ARMOR.durability()))));
    public static final Item CHALCOR_CHESTPLATE = registerItem("chalcor_chestplate", p -> new Item(p.humanoidArmor(ElsewhereEquipmentMaterials.CHALCOR_ARMOR, ArmorType.CHESTPLATE).durability(ArmorType.CHESTPLATE.getDurability(ElsewhereEquipmentMaterials.CHALCOR_ARMOR.durability()))));
    public static final Item CHALCOR_LEGGINGS = registerItem("chalcor_leggings", p -> new Item(p.humanoidArmor(ElsewhereEquipmentMaterials.CHALCOR_ARMOR, ArmorType.LEGGINGS).durability(ArmorType.LEGGINGS.getDurability(ElsewhereEquipmentMaterials.CHALCOR_ARMOR.durability()))));
    public static final Item CHALCOR_BOOTS = registerItem("chalcor_boots", p -> new Item(p.humanoidArmor(ElsewhereEquipmentMaterials.CHALCOR_ARMOR, ArmorType.BOOTS).durability(ArmorType.BOOTS.getDurability(ElsewhereEquipmentMaterials.CHALCOR_ARMOR.durability()))));
    public static final Item ELECTRUM_PICKAXE = registerItem("electrum_pickaxe", p -> new Item(p.pickaxe(ElsewhereEquipmentMaterials.ELECTRUM, 1.0F, -2.8F)));
    public static final Item ELECTRUM_AXE = registerItem("electrum_axe", p -> new Item(p.axe(ElsewhereEquipmentMaterials.ELECTRUM, 5.0F, -3.1F)));
    public static final Item ELECTRUM_SHOVEL = registerItem("electrum_shovel", p -> new Item(p.shovel(ElsewhereEquipmentMaterials.ELECTRUM, 1.5F, -3.0F)));
    public static final Item ELECTRUM_HOE = registerItem("electrum_hoe", p -> new Item(p.hoe(ElsewhereEquipmentMaterials.ELECTRUM, 0.0F, -3.0F)));
    public static final Item ELECTRUM_BROADSWORD = registerItem("electrum_broadsword", p -> new Item(p.sword(ElsewhereEquipmentMaterials.ELECTRUM, 5.0F, -2.8F)));
    public static final Item ELECTRUM_HALBERD = registerItem("electrum_halberd", p -> new Item(p.sword(ElsewhereEquipmentMaterials.ELECTRUM, 4.0F, -2.6F)));
    public static final Item ELECTRUM_HELMET = registerItem("electrum_helmet", p -> new Item(p.humanoidArmor(ElsewhereEquipmentMaterials.ELECTRUM_ARMOR, ArmorType.HELMET).durability(ArmorType.HELMET.getDurability(ElsewhereEquipmentMaterials.ELECTRUM_ARMOR.durability()))));
    public static final Item ELECTRUM_CHESTPLATE = registerItem("electrum_chestplate", p -> new Item(p.humanoidArmor(ElsewhereEquipmentMaterials.ELECTRUM_ARMOR, ArmorType.CHESTPLATE).durability(ArmorType.CHESTPLATE.getDurability(ElsewhereEquipmentMaterials.ELECTRUM_ARMOR.durability()))));
    public static final Item ELECTRUM_LEGGINGS = registerItem("electrum_leggings", p -> new Item(p.humanoidArmor(ElsewhereEquipmentMaterials.ELECTRUM_ARMOR, ArmorType.LEGGINGS).durability(ArmorType.LEGGINGS.getDurability(ElsewhereEquipmentMaterials.ELECTRUM_ARMOR.durability()))));
    public static final Item ELECTRUM_BOOTS = registerItem("electrum_boots", p -> new Item(p.humanoidArmor(ElsewhereEquipmentMaterials.ELECTRUM_ARMOR, ArmorType.BOOTS).durability(ArmorType.BOOTS.getDurability(ElsewhereEquipmentMaterials.ELECTRUM_ARMOR.durability()))));
    public static final Item INDIUM_PICKAXE = registerItem("indium_pickaxe", p -> new Item(p.pickaxe(ElsewhereEquipmentMaterials.INDIUM, 1.0F, -2.8F)));
    public static final Item INDIUM_AXE = registerItem("indium_axe", p -> new Item(p.axe(ElsewhereEquipmentMaterials.INDIUM, 5.0F, -3.1F)));
    public static final Item INDIUM_SHOVEL = registerItem("indium_shovel", p -> new Item(p.shovel(ElsewhereEquipmentMaterials.INDIUM, 1.5F, -3.0F)));
    public static final Item INDIUM_HOE = registerItem("indium_hoe", p -> new Item(p.hoe(ElsewhereEquipmentMaterials.INDIUM, -3.0F, 0.0F)));
    public static final Item INDIUM_BROADSWORD = registerItem("indium_broadsword", p -> new Item(p.sword(ElsewhereEquipmentMaterials.INDIUM, 5.0F, -2.8F)));
    public static final Item INDIUM_HALBERD = registerItem("indium_halberd", p -> new Item(p.sword(ElsewhereEquipmentMaterials.INDIUM, 4.0F, -2.6F)));
    public static final Item INDIUM_HELMET = registerItem("indium_helmet", p -> new Item(p.humanoidArmor(ElsewhereEquipmentMaterials.INDIUM_ARMOR, ArmorType.HELMET).durability(ArmorType.HELMET.getDurability(ElsewhereEquipmentMaterials.INDIUM_ARMOR.durability()))));
    public static final Item INDIUM_CHESTPLATE = registerItem("indium_chestplate", p -> new Item(p.humanoidArmor(ElsewhereEquipmentMaterials.INDIUM_ARMOR, ArmorType.CHESTPLATE).durability(ArmorType.CHESTPLATE.getDurability(ElsewhereEquipmentMaterials.INDIUM_ARMOR.durability()))));
    public static final Item INDIUM_LEGGINGS = registerItem("indium_leggings", p -> new Item(p.humanoidArmor(ElsewhereEquipmentMaterials.INDIUM_ARMOR, ArmorType.LEGGINGS).durability(ArmorType.LEGGINGS.getDurability(ElsewhereEquipmentMaterials.INDIUM_ARMOR.durability()))));
    public static final Item INDIUM_BOOTS = registerItem("indium_boots", p -> new Item(p.humanoidArmor(ElsewhereEquipmentMaterials.INDIUM_ARMOR, ArmorType.BOOTS).durability(ArmorType.BOOTS.getDurability(ElsewhereEquipmentMaterials.INDIUM_ARMOR.durability()))));

    public static final List<Item> ELSEWHERE_TOOLS = List.of(FERRUM_PICKAXE, FERRUM_AXE, FERRUM_SHOVEL, FERRUM_HOE, CHALCOR_PICKAXE, CHALCOR_AXE, CHALCOR_SHOVEL, CHALCOR_HOE, ELECTRUM_PICKAXE, ELECTRUM_AXE, ELECTRUM_SHOVEL, ELECTRUM_HOE, INDIUM_PICKAXE, INDIUM_AXE, INDIUM_SHOVEL, INDIUM_HOE);
    public static final List<Item> ELSEWHERE_COMBAT = List.of(FERRUM_BROADSWORD, FERRUM_HALBERD, FERRUM_HELMET, FERRUM_CHESTPLATE, FERRUM_LEGGINGS, FERRUM_BOOTS, CHALCOR_BROADSWORD, CHALCOR_HALBERD, CHALCOR_HELMET, CHALCOR_CHESTPLATE, CHALCOR_LEGGINGS, CHALCOR_BOOTS, ELECTRUM_BROADSWORD, ELECTRUM_HALBERD, ELECTRUM_HELMET, ELECTRUM_CHESTPLATE, ELECTRUM_LEGGINGS, ELECTRUM_BOOTS, INDIUM_BROADSWORD, INDIUM_HALBERD, INDIUM_HELMET, INDIUM_CHESTPLATE, INDIUM_LEGGINGS, INDIUM_BOOTS);

    public static final Item EXOSKELETON = registerItem("exoskeleton", Item::new);
    public static final Item AMEISENHAUFEN_MUSIC_DISC = registerItem("ameisenhaufen_music_disc", properties -> new Item(properties.jukeboxPlayable(ModJukeboxSongs.AMEISENHAUFEN_KEY).stacksTo(1).rarity(Rarity.RARE)));
    public static final Item ANTWORKER_SPAWN_EGG = registerItem("antworker_spawn_egg",
            properties -> new SpawnEggItem(properties.spawnEgg(ModEntities.ANTWORKER)));
    public static final Item DAIKON = registerItem("daikon", properties -> new Item(properties.food(ModFoods.DAIKON, ModFoods.DAIKON_CONSUMABLE).component(DataComponents.COMPOSTABLE, new Compostable(new ResolvableInt.Constant(65)))));
    public static final Item DAIKON_SEEDS = registerItem("daikon_seeds",
            properties -> new BlockItem(ModBlocks.DAIKON_CROP, properties.useItemDescriptionPrefix().component(DataComponents.COMPOSTABLE, new Compostable(new ResolvableInt.Constant(30)))));
    public static final Item RICE = registerItem("rice", Item::new);
    public static final Item RICE_SEEDS = registerItem("rice_seeds",
            properties -> new BlockItem(ModBlocks.RICE_CROP, properties.useItemDescriptionPrefix()
                    .component(DataComponents.COMPOSTABLE, new Compostable(new ResolvableInt.Constant(30)))));
    public static final Item COOKED_RICE = registerItem("cooked_rice", properties ->
            new Item(properties.food(ModFoods.COOKED_RICE, ModFoods.COOKED_RICE_CONSUMABLE)));
    public static final Item ONIGIRI = registerItem("onigiri", properties ->
            new Item(properties.food(ModFoods.ONIGIRI, ModFoods.ONIGIRI_CONSUMABLE)));
    public static final Item FRIED_RICE = registerItem("fried_rice", properties ->
            new Item(properties.food(ModFoods.FRIED_RICE, ModFoods.FRIED_RICE_CONSUMABLE)));
    public static final Item MOCHI = registerItem("mochi", properties ->
            new Item(properties.food(ModFoods.MOCHI, ModFoods.MOCHI_CONSUMABLE)));

    public static final Item HASKAP_BERRIES = registerItem("haskap_berries",
            properties -> new BlockItem(ModBlocks.HASKAP_BERRY_BUSH, properties.useItemDescriptionPrefix().food(ModFoods.HASKAP_BERRIES, ModFoods.HASKAP_BERRIES_CONSUMABLE).component(DataComponents.COMPOSTABLE, new Compostable(new ResolvableInt.Constant(30)))));
    public static final Item BARBERRIES = registerItem("barberries",
            properties -> new BlockItem(ModBlocks.BARBERRY_BUSH, properties.useItemDescriptionPrefix().food(ModFoods.BARBERRIES, ModFoods.BARBERRIES_CONSUMABLE).component(DataComponents.COMPOSTABLE, new Compostable(new ResolvableInt.Constant(30)))));


    public static final Item PERSIMMON_FRUIT = registerItem("persimmon_fruit", properties -> new Item(properties.food(ModFoods.PERSIMMON_FRUIT, ModFoods.PERSIMMON_FRUIT_CONSUMABLE).component(DataComponents.COMPOSTABLE, new Compostable(new ResolvableInt.Constant(65)))));
    public static final Item MIMOSA_SEEDPODS = registerItem("mimosa_seedpods", properties -> new Item(properties.component(DataComponents.COMPOSTABLE, new Compostable(new ResolvableInt.Constant(30)))));

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
            MINING_MATERIALS.forEach(output::accept);
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


        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(output -> output.accept(PERSIMMON_FRUIT));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(output -> output.accept(MIMOSA_SEEDPODS));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(output -> {
            output.accept(REINFORCED_SHIELD);
            ELSEWHERE_COMBAT.forEach(output::accept);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(output -> {
            output.accept(AMEISENHAUFEN_MUSIC_DISC);
            ELSEWHERE_TOOLS.forEach(output::accept);
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

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(output -> {
            output.accept(RICE);
            output.accept(RICE_SEEDS);
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(output -> {
            output.accept(COOKED_RICE);
            output.accept(ONIGIRI);
            output.accept(FRIED_RICE);
            output.accept(MOCHI);
        });

    }

}
