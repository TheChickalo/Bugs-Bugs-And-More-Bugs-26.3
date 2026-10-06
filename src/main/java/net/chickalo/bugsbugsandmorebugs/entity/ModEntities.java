package net.chickalo.bugsbugsandmorebugs.entity;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.chickalo.bugsbugsandmorebugs.entity.custom.AntWorkerEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {
    public static final ResourceKey<EntityType<?>> ANTWORKER_KEY = ResourceKey.create(Registries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "antworker"));

    public static final EntityType<AntWorkerEntity> ANTWORKER = Registry.register(BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "antworker"),
            EntityType.Builder.of(AntWorkerEntity::new, MobCategory.CREATURE).sized(0.35f, 0.2f).build(ANTWORKER_KEY));

    public static void registerModEntities() {
        BugsBugsAndMoreBugs.LOGGER.info("Registering Mod Entities for " + BugsBugsAndMoreBugs.MOD_ID);
    }
}
