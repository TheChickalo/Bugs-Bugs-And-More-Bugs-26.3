package net.chickalo.bugsbugsandmorebugs.entity.snail;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/** Self-contained registration, so the existing entity and block registries are not overwritten. */
public final class SnailContent {
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, path); }
    public static final ResourceKey<EntityType<?>> SNAIL_KEY = ResourceKey.create(Registries.ENTITY_TYPE, id("snail"));
    public static final EntityType<SnailEntity> SNAIL = Registry.register(BuiltInRegistries.ENTITY_TYPE, id("snail"),
            FabricEntityType.Builder.createMob(SnailEntity::new, MobCategory.CREATURE,
                    builder -> builder.spawnPlacement(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SnailEntity::canSpawn))
                    .sized(1.35F, 1.0F).build(SNAIL_KEY));
    private static final ResourceKey<Block> SLIME_KEY = ResourceKey.create(Registries.BLOCK, id("snail_slime"));
    public static final SnailSlimeBlock SLIME = (SnailSlimeBlock) Registry.register(BuiltInRegistries.BLOCK, id("snail_slime"),
            new SnailSlimeBlock(BlockBehaviour.Properties.of().setId(SLIME_KEY).noCollision().noOcclusion().strength(0.15F).sound(SoundType.SLIME_BLOCK)));
    private static final ResourceKey<Item> EGG_KEY = ResourceKey.create(Registries.ITEM, id("snail_spawn_egg"));
    public static final Item SPAWN_EGG = Registry.register(BuiltInRegistries.ITEM, id("snail_spawn_egg"),
            new SpawnEggItem(new Item.Properties().setId(EGG_KEY).spawnEgg(SNAIL)));

    public static final Item SNAIL_SHELL = item("snail_shell", new Item.Properties());
    public static final Item SNAIL_MEAT = item("snail_meat", new Item.Properties());
    public static final Item ESCARGOT = item("escargot", new Item.Properties().food(
            new FoodProperties.Builder().nutrition(1).saturationModifier(0.1F).build(),
            Consumables.defaultFood().onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.NAUSEA, 100), 1.0F)).build()));
    private static Item item(String name, Item.Properties properties) {
        return Registry.register(BuiltInRegistries.ITEM, id(name), new Item(properties.setId(ResourceKey.create(Registries.ITEM, id(name)))));
    }

    private SnailContent() {}

    public static void register() {
        FabricDefaultAttributeRegistry.register(SNAIL, SnailEntity.createAttributes());
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(output -> output.accept(SPAWN_EGG));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(output -> { output.accept(SNAIL_SHELL); output.accept(SNAIL_MEAT); });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(output -> output.accept(ESCARGOT));
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(ModBiomes.ANCIENT_CEDAR_RAINFOREST, ModBiomes.MOSSY_HINOKI_GROVE), MobCategory.CREATURE, SNAIL, 18, 1, 3);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(ModBiomes.WILLOW_BACKWATERS, ModBiomes.WILLOW_HUMMOCKS, ModBiomes.WISTERIA_VEILWOOD, ModBiomes.MAPLE_FOREST), MobCategory.CREATURE, SNAIL, 10, 1, 2);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(ModBiomes.BAMBOO_MARSH), MobCategory.CREATURE, SNAIL, 4, 1, 2);
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            var pos = hit.getBlockPos();
            var state = level.getBlockState(pos);
            var held = player.getItemInHand(hand);
            if (player.isSpectator() || !state.is(SLIME) || !held.is(ItemTags.HOES)) return InteractionResult.PASS;
            if (!level.isClientSide()) {
                int count = state.getValue(SnailSlimeBlock.LAYERS);
                if (level.removeBlock(pos, false)) {
                    if (!player.isCreative()) {
                        Block.popResource(level, pos, new ItemStack(Items.SLIME_BALL, count));
                        held.hurtAndBreak(1, player, hand);
                    }
                    level.playSound(null, pos, SoundEvents.HOE_TILL.value(), SoundSource.BLOCKS, 0.7F, 1.1F);
                    level.levelEvent(2001, pos, Block.getId(state));
                }
            }
            return InteractionResult.SUCCESS;
        });
    }
}
