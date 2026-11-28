package net.chickalo.bugsbugsandmorebugs.entity.cavebeetle;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.chickalo.bugsbugsandmorebugs.worldgen.dimension.ModDimensions;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.levelgen.Heightmap;

public final class CaveBeetleContent {
    public static Identifier id(String name) { return Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name); }
    public static final ResourceKey<EntityType<?>> KEY = ResourceKey.create(Registries.ENTITY_TYPE, id("cave_beetle"));
    // AMBIENT uses the cave-friendly spawn budget; the mob itself is entirely passive.
    public static final EntityType<CaveBeetleEntity> BEETLE = Registry.register(BuiltInRegistries.ENTITY_TYPE, id("cave_beetle"),
        FabricEntityType.Builder.createMob(CaveBeetleEntity::new, MobCategory.AMBIENT,
            builder -> builder.spawnPlacement(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CaveBeetleEntity::canSpawn))
            .sized(0.9F, 0.55F).build(KEY));
    public static final ResourceKey<Item> EGG_KEY = ResourceKey.create(Registries.ITEM, id("cave_beetle_spawn_egg"));
    public static final Item SPAWN_EGG = Registry.register(BuiltInRegistries.ITEM, id("cave_beetle_spawn_egg"),
        new SpawnEggItem(new Item.Properties().setId(EGG_KEY).spawnEgg(BEETLE)));
    private CaveBeetleContent() {}
    public static void register() {
        FabricDefaultAttributeRegistry.register(BEETLE, CaveBeetleEntity.createAttributes());
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(out -> out.accept(SPAWN_EGG));
        BiomeModifications.addSpawn(context -> context.canGenerateIn(ModDimensions.ELSEWHEREDIM_KEY),
            MobCategory.AMBIENT, BEETLE, 12, 1, 3);
    }
}

