package net.chickalo.bugsbugsandmorebugs.food;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;

public class ModFoods {
    public static final FoodProperties DAIKON = new FoodProperties.Builder().nutrition(2).saturationModifier(0.5f).build();
    public static final FoodProperties BARBERRIES = new FoodProperties.Builder().nutrition(1).saturationModifier(0.4f).build();
    public static final FoodProperties HASKAP_BERRIES = new FoodProperties.Builder().nutrition(3).saturationModifier(0.2f).build();

    public static final Consumable DAIKON_CONSUMABLE = Consumables.defaultFood()
            .consumeSeconds(1.8f).build();
    public static final Consumable BARBERRIES_CONSUMABLE = Consumables.defaultFood()
            .build();
    public static final Consumable HASKAP_BERRIES_CONSUMABLE = Consumables.defaultFood()
            .build();

}
