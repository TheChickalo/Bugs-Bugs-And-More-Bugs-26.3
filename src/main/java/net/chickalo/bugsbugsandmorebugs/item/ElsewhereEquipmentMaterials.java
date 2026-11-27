package net.chickalo.bugsbugsandmorebugs.item;

import java.util.Map;
import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;

/** Elsewhere equipment tiers. Keep repair ingredients separate from vanilla metals. */
public final class ElsewhereEquipmentMaterials {
    private ElsewhereEquipmentMaterials() {}

    public static final ToolMaterial FERRUM = tool("ferrum", ToolMaterial.IRON, 250, 6.0F, 2.0F, 14);
    public static final ToolMaterial CHALCOR = tool("chalcor", ToolMaterial.COPPER, 190, 5.0F, 1.0F, 13);
    public static final ToolMaterial ELECTRUM = tool("electrum", ToolMaterial.GOLD, 64, 12.0F, 0.0F, 22);
    public static final ToolMaterial INDIUM = tool("indium", ToolMaterial.DIAMOND, 1100, 8.0F, 3.0F, 14);

    public static final ArmorMaterial FERRUM_ARMOR = armor("ferrum", 15, 2, 6, 5, 2, 9, 0.0F);
    public static final ArmorMaterial CHALCOR_ARMOR = armor("chalcor", 11, 2, 4, 3, 1, 12, 0.0F);
    public static final ArmorMaterial ELECTRUM_ARMOR = armor("electrum", 8, 2, 5, 3, 1, 25, 0.0F);
    public static final ArmorMaterial INDIUM_ARMOR = armor("indium", 27, 3, 7, 6, 3, 14, 1.0F);

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, path);
    }

    private static TagKey<Item> repair(String name) {
        return TagKey.create(Registries.ITEM, id("repairs_" + name + "_equipment"));
    }

    private static ToolMaterial tool(String name, ToolMaterial tier, int durability,
                                     float speed, float damage, int enchantability) {
        return new ToolMaterial(tier.incorrectBlocksForDrops(), durability, speed, damage,
                enchantability, repair(name));
    }

    private static ArmorMaterial armor(String name, int durability, int helmet, int chest,
                                       int legs, int boots, int enchantability, float toughness) {
        return new ArmorMaterial(durability,
                Map.of(ArmorType.HELMET, helmet, ArmorType.CHESTPLATE, chest,
                        ArmorType.LEGGINGS, legs, ArmorType.BOOTS, boots),
                enchantability, SoundEvents.ARMOR_EQUIP_IRON, toughness, 0.0F,
                repair(name), ResourceKey.create(EquipmentAssets.ROOT_ID, id(name)));
    }
}
