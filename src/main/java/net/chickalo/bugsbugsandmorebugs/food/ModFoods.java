package net.chickalo.bugsbugsandmorebugs.food;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

public class ModFoods {
    public static final FoodProperties DAIKON = new FoodProperties.Builder().nutrition(2).saturationModifier(0.5f).build();

    public static final Consumable DAIKON_CONSUMABLE = Consumables.defaultFood()
            .consumeSeconds(1.8f).build();
}
