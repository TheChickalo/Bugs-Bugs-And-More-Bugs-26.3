package net.chickalo.bugsbugsandmorebugs.food;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;

public class ModFoods {
    public static final FoodProperties DAIKON = new FoodProperties.Builder().nutrition(2).saturationModifier(0.5f).build();
    public static final FoodProperties BARBERRIES = new FoodProperties.Builder().nutrition(1).saturationModifier(0.4f).build();
    public static final FoodProperties HASKAP_BERRIES = new FoodProperties.Builder().nutrition(3).saturationModifier(0.2f).build();


    public static final FoodProperties PERSIMMON_FRUIT = new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build();
    public static final Consumable PERSIMMON_FRUIT_CONSUMABLE = Consumables.defaultFood().build();

    public static final FoodProperties COOKED_RICE =
            new FoodProperties.Builder().nutrition(3).saturationModifier(0.45f).build();
    public static final Consumable COOKED_RICE_CONSUMABLE = Consumables.defaultFood().build();

    public static final FoodProperties ONIGIRI =
            new FoodProperties.Builder().nutrition(4).saturationModifier(0.55f).build();
    public static final Consumable ONIGIRI_CONSUMABLE = Consumables.defaultFood().build();

    public static final FoodProperties FRIED_RICE =
            new FoodProperties.Builder().nutrition(8).saturationModifier(0.75f).build();
    public static final Consumable FRIED_RICE_CONSUMABLE = Consumables.defaultFood().build();

    public static final FoodProperties MOCHI =
            new FoodProperties.Builder().nutrition(3).saturationModifier(0.5f).build();
    public static final Consumable MOCHI_CONSUMABLE = Consumables.defaultFood().build();

    public static final Consumable DAIKON_CONSUMABLE = Consumables.defaultFood()
            .consumeSeconds(1.8f).build();
    public static final Consumable BARBERRIES_CONSUMABLE = Consumables.defaultFood()
            .build();
    public static final Consumable HASKAP_BERRIES_CONSUMABLE = Consumables.defaultFood()
            .build();

}
