package net.chickalo.bugsbugsandmorebugs.item;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.chickalo.bugsbugsandmorebugs.entity.ModEntities;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;

import java.util.List;
import java.util.Optional;

import java.util.function.Function;

public class ModItems {
    public static final Item EXOSKELETON = registerItem("exoskeleton", Item::new);
    public static final Item ANTWORKER_SPAWN_EGG = registerItem("antworker_spawn_egg",
            properties -> new SpawnEggItem(properties.spawnEgg(ModEntities.ANTWORKER)));
    public static final Item REINFORCED_SHIELD =
            registerItem("reinforced_shield", properties ->
                    new ShieldItem(
                            properties
                                    .durability(504)
                                    .delayedComponent(
                                            DataComponents.BLOCKS_ATTACKS,
                                            context -> new BlocksAttacks(
                                                    0.25F,
                                                    1.0F,
                                                    List.of(
                                                            new BlocksAttacks.DamageReduction(
                                                                    90.0F,
                                                                    Optional.empty(),
                                                                    0.0F,
                                                                    1.0F
                                                            )
                                                    ),
                                                    new BlocksAttacks.ItemDamageFunction(
                                                            3.0F,
                                                            1.0F,
                                                            1.0F
                                                    ),
                                                    Optional.of(
                                                            context.getOrThrow(
                                                                    DamageTypeTags.BYPASSES_SHIELD
                                                            )
                                                    ),
                                                    Optional.of(SoundEvents.SHIELD_BLOCK),
                                                    Optional.of(SoundEvents.SHIELD_BREAK)
                                            )
                                    )
                                    .component(
                                            DataComponents.BREAK_SOUND,
                                            SoundEvents.SHIELD_BREAK
                                    )
                    )
            );
    private static Item registerItem(String name, Function<Item.Properties, Item> function) {
        return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name),
                function.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name)))));
    }

    public static void registerModItems() {
        BugsBugsAndMoreBugs.LOGGER.info("Registering Mod Items for " + BugsBugsAndMoreBugs.MOD_ID);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(output -> {
            output.accept(EXOSKELETON);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(output -> {
            output.accept(ANTWORKER_SPAWN_EGG);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(output -> {
            output.accept(REINFORCED_SHIELD);
        });

    }

}
