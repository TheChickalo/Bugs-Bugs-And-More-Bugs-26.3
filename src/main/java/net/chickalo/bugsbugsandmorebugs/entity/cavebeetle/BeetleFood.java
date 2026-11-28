package net.chickalo.bugsbugsandmorebugs.entity.cavebeetle;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** 26.3 compostability is a data component, not the old ComposterBlock map.
 * Tags allow modpack authors to tune deterministic beetle nutrition independently. */
public final class BeetleFood {
    private BeetleFood() {}
    private static TagKey<Item> tag(String name) { return TagKey.create(Registries.ITEM, CaveBeetleContent.id(name)); }
    public static final TagKey<Item> SMALL = tag("beetle_food_small");
    public static final TagKey<Item> NORMAL = tag("beetle_food_normal");
    public static final TagKey<Item> RICH = tag("beetle_food_rich");
    public static final TagKey<Item> DENSE = tag("beetle_food_dense");
    public static int points(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        if (stack.is(DENSE)) return 4;
        if (stack.is(RICH)) return 3;
        if (stack.is(NORMAL)) return 2;
        if (stack.is(SMALL)) return 1;
        return stack.has(DataComponents.COMPOSTABLE) ? 2 : 0;
    }
}

