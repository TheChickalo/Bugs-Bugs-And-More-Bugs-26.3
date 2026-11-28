package net.chickalo.bugsbugsandmorebugs.block;

import net.minecraft.core.component.DataComponents;
import java.util.List;
import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.chickalo.bugsbugsandmorebugs.block.custom.*;
import net.chickalo.bugsbugsandmorebugs.worldgen.ModFeatures;
import net.chickalo.bugsbugsandmorebugs.worldgen.tree.ModTreeGrowers;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.item.v1.BlockTransformerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.sounds.AmbientLeavesBlockSoundPlayer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Function;

public class ModBlocks {
    public static final Block INFESTED_DIRT = registerBlock("infested_dirt",
            properties -> new Block(properties.strength(0.25F)
                    .sound(SoundType.GRAVEL)));

    // TEMPORARY PORTAL BLOCK, DELETE LATER
    public static final Block TEMP_PORTAL = registerBlock("portal",
            properties -> new TempPortal(properties.strength(3f)));


    public static final Block RHYOLITE_CANNEL_ORE = registerOreBlock("rhyolite_cannel_ore", 3.0F, SoundType.STONE, 1, 2);
    public static final Block SCHIST_CANNEL_ORE = registerOreBlock("schist_cannel_ore", 4.5F, SoundType.STONE, 1, 2);
    public static final Block CANNEL_BLOCK = registerMineralBlock("cannel_block", 5.0F, SoundType.STONE);
    public static final Block RHYOLITE_FERRUM_ORE = registerOreBlock("rhyolite_ferrum_ore", 3.0F, SoundType.STONE, 1, 2);
    public static final Block SCHIST_FERRUM_ORE = registerOreBlock("schist_ferrum_ore", 4.5F, SoundType.STONE, 1, 2);
    public static final Block FERRUM_BLOCK = registerMineralBlock("ferrum_block", 5.0F, SoundType.METAL);
    public static final Block RHYOLITE_CHALCOR_ORE = registerOreBlock("rhyolite_chalcor_ore", 3.0F, SoundType.STONE, 1, 2);
    public static final Block SCHIST_CHALCOR_ORE = registerOreBlock("schist_chalcor_ore", 4.5F, SoundType.STONE, 1, 2);
    public static final Block CHALCOR_BLOCK = registerMineralBlock("chalcor_block", 5.0F, SoundType.METAL);
    public static final Block RHYOLITE_ELECTRUM_ORE = registerOreBlock("rhyolite_electrum_ore", 3.0F, SoundType.STONE, 2, 3);
    public static final Block SCHIST_ELECTRUM_ORE = registerOreBlock("schist_electrum_ore", 4.5F, SoundType.STONE, 2, 3);
    public static final Block ELECTRUM_BLOCK = registerMineralBlock("electrum_block", 5.0F, SoundType.METAL);
    public static final Block RHYOLITE_CORUNDUM_ORE = registerOreBlock("rhyolite_corundum_ore", 3.0F, SoundType.STONE, 3, 7);
    public static final Block SCHIST_CORUNDUM_ORE = registerOreBlock("schist_corundum_ore", 4.5F, SoundType.STONE, 3, 7);
    public static final Block CORUNDUM_BLOCK = registerMineralBlock("corundum_block", 5.0F, SoundType.STONE);
    public static final Block RHYOLITE_INDIUM_ORE = registerOreBlock("rhyolite_indium_ore", 3.0F, SoundType.STONE, 3, 5);
    public static final Block SCHIST_INDIUM_ORE = registerOreBlock("schist_indium_ore", 4.5F, SoundType.STONE, 3, 5);
    public static final Block INDIUM_BLOCK = registerMineralBlock("indium_block", 5.0F, SoundType.METAL);

    public static final List<Block> BASE_ORES = List.of(
            RHYOLITE_CANNEL_ORE,
            SCHIST_CANNEL_ORE,
            RHYOLITE_FERRUM_ORE,
            SCHIST_FERRUM_ORE,
            RHYOLITE_CHALCOR_ORE,
            SCHIST_CHALCOR_ORE,
            RHYOLITE_ELECTRUM_ORE,
            SCHIST_ELECTRUM_ORE,
            RHYOLITE_CORUNDUM_ORE,
            SCHIST_CORUNDUM_ORE,
            RHYOLITE_INDIUM_ORE,
            SCHIST_INDIUM_ORE);
    public static final List<Block> MINERAL_STORAGE_BLOCKS = List.of(
            CANNEL_BLOCK,
            FERRUM_BLOCK,
            CHALCOR_BLOCK,
            ELECTRUM_BLOCK,
            CORUNDUM_BLOCK,
            INDIUM_BLOCK);

    // Natural limestone deposits, using the supplied 16x16 assets.
    public static final Block FLOWSTONE = registerBlock("flowstone", p -> new Block(p.strength(1.5F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block RIMSTONE = registerBlock("rimstone", p -> new Block(p.strength(1.2F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final Block CAVE_ROOT_STRANDS = registerBlock("cave_root_strands", p -> new CaveHangingBlock(p.noCollision().noOcclusion().instabreak().sound(SoundType.VINE)));
    public static final Block FLOWSTONE_CURTAIN = registerBlock("flowstone_curtain", p -> new CaveHangingBlock(p.noCollision().noOcclusion().strength(0.6F).sound(SoundType.STONE)));
    public static final Block GLOWWORM_THREADS = registerBlock("glowworm_threads", p -> new CaveHangingBlock(p.noCollision().noOcclusion().instabreak().sound(SoundType.VINE).lightLevel(s -> 5)));
    public static final Block FUNGAL_MAT = registerBlock("fungal_mat", p -> new CarpetBlock(p.noOcclusion().strength(0.1F).sound(SoundType.MOSS_CARPET)));
    public static final Block GLOWSHROOM_CLUSTER = registerBlock("glowshroom_cluster", p -> new CaveClusterBlock(p.noCollision().noOcclusion().instabreak().sound(SoundType.FUNGUS).lightLevel(s -> 8)));
    public static final Block LIMESTONE_SPELEOTHEM = registerBlock("limestone_speleothem", p -> new CaveSpeleothemBlock(p.noCollision().noOcclusion().strength(1.2F).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    // Underground flora. Separate ceiling plants from floor vegetation.
    public static final Block LANTERN_VINE = registerBlock("lantern_vine", p -> new LanternPlantBlock(true,p.noCollision().noOcclusion().instabreak().sound(SoundType.VINE)));
    public static final Block LANTERN_BULB = registerBlock("lantern_bulb", p -> new LanternPlantBlock(false,p.noCollision().noOcclusion().instabreak().sound(SoundType.VINE).lightLevel(s -> 14)));
    public static final Block CAVE_FERN = registerBlock("cave_fern", p -> new CaveClusterBlock(p.noCollision().noOcclusion().instabreak().sound(SoundType.GRASS)));
    public static final Block CAVE_MOSS = registerBlock("cave_moss", p -> new Block(p.strength(0.1F).sound(SoundType.MOSS)));
    public static final Block CAVE_LILY = registerBlock("cave_lily", p -> new CaveClusterBlock(p.noCollision().noOcclusion().instabreak().sound(SoundType.GRASS)));
    public static final Block CRYSTAL_CLUSTER = registerBlock("crystal_cluster", p -> new CaveClusterBlock(p.noCollision().noOcclusion().strength(1.0F).sound(SoundType.AMETHYST_CLUSTER).requiresCorrectToolForDrops()));
    public static final Block SUBTERRANEAN_WATERWEED = registerBlock("subterranean_waterweed", p -> new SubterraneanWaterweedBlock(p.noCollision().noOcclusion().instabreak().sound(SoundType.GRASS)));

    // Rhyolite family
    public static final Block RHYOLITE = registerBlock("rhyolite",
            properties -> new Block(properties.strength(3f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block COBBLED_RHYOLITE = registerBlock("cobbled_rhyolite",
            properties -> new Block(properties.strength(3f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block CHISELED_RHYOLITE = registerBlock("chiseled_rhyolite",
            properties -> new Block(properties.strength(3f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block RHYOLITE_BRICKS = registerBlock("rhyolite_bricks",
            properties -> new Block(properties.strength(3f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block CRACKED_RHYOLITE_BRICKS = registerBlock("cracked_rhyolite_bricks",
            properties -> new Block(properties.strength(3f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block RHYOLITE_STAIRS = registerBlock("rhyolite_stairs",
            properties -> new StairBlock(ModBlocks.RHYOLITE.defaultBlockState(),
                    properties.strength(3f)));
    public static final Block RHYOLITE_SLAB = registerBlock("rhyolite_slab",
            properties -> new SlabBlock(
                    properties.strength(3f)));
    public static final Block RHYOLITE_WALL = registerBlock("rhyolite_wall",
            properties -> new WallBlock(
                    properties.strength(3f)));
    public static final Block COBBLED_RHYOLITE_STAIRS = registerBlock("cobbled_rhyolite_stairs",
            properties -> new StairBlock(ModBlocks.RHYOLITE.defaultBlockState(),
                    properties.strength(3f)));
    public static final Block COBBLED_RHYOLITE_SLAB = registerBlock("cobbled_rhyolite_slab",
            properties -> new SlabBlock(
                    properties.strength(3f)));
    public static final Block COBBLED_RHYOLITE_WALL = registerBlock("cobbled_rhyolite_wall",
            properties -> new WallBlock(
                    properties.strength(3f)));
    public static final Block POLISHED_RHYOLITE = registerBlock("polished_rhyolite",
            properties -> new Block(properties.strength(3f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block POLISHED_RHYOLITE_STAIRS = registerBlock("polished_rhyolite_stairs",
            properties -> new StairBlock(ModBlocks.RHYOLITE.defaultBlockState(),
                    properties.strength(3f)));
    public static final Block POLISHED_RHYOLITE_SLAB = registerBlock("polished_rhyolite_slab",
            properties -> new SlabBlock(
                    properties.strength(3f)));
    public static final Block POLISHED_RHYOLITE_WALL = registerBlock("polished_rhyolite_wall",
            properties -> new WallBlock(
                    properties.strength(3f)));
    public static final Block RHYOLITE_BRICK_STAIRS = registerBlock("rhyolite_brick_stairs",
            properties -> new StairBlock(ModBlocks.RHYOLITE.defaultBlockState(),
                    properties.strength(3f)));
    public static final Block RHYOLITE_BRICK_SLAB = registerBlock("rhyolite_brick_slab",
            properties -> new SlabBlock(
                    properties.strength(3f)));
    public static final Block RHYOLITE_BRICK_WALL = registerBlock("rhyolite_brick_wall",
            properties -> new WallBlock(
                    properties.strength(3f)));

    // Schist family
    public static final Block SCHIST = registerBlock("schist",
            properties -> new Block(properties.strength(3.5f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block COBBLED_SCHIST = registerBlock("cobbled_schist",
            properties -> new Block(properties.strength(3.5f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block POLISHED_SCHIST = registerBlock("polished_schist",
            properties -> new Block(properties.strength(3.5f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block SCHIST_BRICKS = registerBlock("schist_bricks",
            properties -> new Block(properties.strength(3.5f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block CHISELED_SCHIST = registerBlock("chiseled_schist",
            properties -> new Block(properties.strength(3.5f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block CRACKED_SCHIST_BRICKS = registerBlock("cracked_schist_bricks",
            properties -> new Block(properties.strength(3.5f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block SCHIST_STAIRS = registerBlock("schist_stairs",
            properties -> new StairBlock(SCHIST.defaultBlockState(), properties.strength(3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block SCHIST_SLAB = registerBlock("schist_slab",
            properties -> new SlabBlock(properties.strength(3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block SCHIST_WALL = registerBlock("schist_wall",
            properties -> new WallBlock(properties.strength(3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block COBBLED_SCHIST_STAIRS = registerBlock("cobbled_schist_stairs",
            properties -> new StairBlock(COBBLED_SCHIST.defaultBlockState(), properties.strength(3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block COBBLED_SCHIST_SLAB = registerBlock("cobbled_schist_slab",
            properties -> new SlabBlock(properties.strength(3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block COBBLED_SCHIST_WALL = registerBlock("cobbled_schist_wall",
            properties -> new WallBlock(properties.strength(3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block POLISHED_SCHIST_STAIRS = registerBlock("polished_schist_stairs",
            properties -> new StairBlock(POLISHED_SCHIST.defaultBlockState(), properties.strength(3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block POLISHED_SCHIST_SLAB = registerBlock("polished_schist_slab",
            properties -> new SlabBlock(properties.strength(3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block POLISHED_SCHIST_WALL = registerBlock("polished_schist_wall",
            properties -> new WallBlock(properties.strength(3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block SCHIST_BRICKS_STAIRS = registerBlock("schist_bricks_stairs",
            properties -> new StairBlock(SCHIST_BRICKS.defaultBlockState(), properties.strength(3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block SCHIST_BRICKS_SLAB = registerBlock("schist_bricks_slab",
            properties -> new SlabBlock(properties.strength(3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block SCHIST_BRICKS_WALL = registerBlock("schist_bricks_wall",
            properties -> new WallBlock(properties.strength(3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    // eventually the deepdeepslate version of rhyolite will go here

    // Eclogite family (Elsewhere deepest stone)
    public static final Block ECLOGITE = registerBlock("eclogite",
            properties -> new Block(properties.strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block COBBLED_ECLOGITE = registerBlock("cobbled_eclogite",
            properties -> new Block(properties.strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block POLISHED_ECLOGITE = registerBlock("polished_eclogite",
            properties -> new Block(properties.strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block ECLOGITE_BRICKS = registerBlock("eclogite_bricks",
            properties -> new Block(properties.strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block CHISELED_ECLOGITE = registerBlock("chiseled_eclogite",
            properties -> new Block(properties.strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block CRACKED_ECLOGITE_BRICKS = registerBlock("cracked_eclogite_bricks",
            properties -> new Block(properties.strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block ECLOGITE_STAIRS = registerBlock("eclogite_stairs",
            properties -> new StairBlock(ECLOGITE.defaultBlockState(), properties.strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block ECLOGITE_SLAB = registerBlock("eclogite_slab",
            properties -> new SlabBlock(properties.strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block ECLOGITE_WALL = registerBlock("eclogite_wall",
            properties -> new WallBlock(properties.strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block COBBLED_ECLOGITE_STAIRS = registerBlock("cobbled_eclogite_stairs",
            properties -> new StairBlock(COBBLED_ECLOGITE.defaultBlockState(), properties.strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block COBBLED_ECLOGITE_SLAB = registerBlock("cobbled_eclogite_slab",
            properties -> new SlabBlock(properties.strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block COBBLED_ECLOGITE_WALL = registerBlock("cobbled_eclogite_wall",
            properties -> new WallBlock(properties.strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block POLISHED_ECLOGITE_STAIRS = registerBlock("polished_eclogite_stairs",
            properties -> new StairBlock(POLISHED_ECLOGITE.defaultBlockState(), properties.strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block POLISHED_ECLOGITE_SLAB = registerBlock("polished_eclogite_slab",
            properties -> new SlabBlock(properties.strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block POLISHED_ECLOGITE_WALL = registerBlock("polished_eclogite_wall",
            properties -> new WallBlock(properties.strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block ECLOGITE_BRICKS_STAIRS = registerBlock("eclogite_bricks_stairs",
            properties -> new StairBlock(ECLOGITE_BRICKS.defaultBlockState(), properties.strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block ECLOGITE_BRICKS_SLAB = registerBlock("eclogite_bricks_slab",
            properties -> new SlabBlock(properties.strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block ECLOGITE_BRICKS_WALL = registerBlock("eclogite_bricks_wall",
            properties -> new WallBlock(properties.strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final Block LOAM = registerBlock("loam", properties -> new Block(properties.strength(0.5F).sound(SoundType.GRAVEL).instrument(NoteBlockInstrument.SNARE)));
    public static final Block COARSE_LOAM = registerBlock("coarse_loam", p -> new Block(p.strength(0.5F).sound(SoundType.GRAVEL)));
    public static final Block ROOTED_LOAM = registerBlock("rooted_loam", p -> new Block(p.strength(0.5F).sound(SoundType.GRAVEL)));
    public static final Block TILLED_LOAM = registerBlock("tilled_loam", p -> new TilledLoamBlock(p.strength(0.6F).sound(SoundType.GRAVEL).randomTicks().noOcclusion()));
    public static final Block GRASSY_LOAM = registerBlock("grassy_loam", properties -> new net.chickalo.bugsbugsandmorebugs.block.custom.GrassyLoamBlock(properties.strength(0.6F).sound(SoundType.GRASS).randomTicks()));
    public static final Block ASHSTONE = registerBlock("ashstone", properties -> new Block(properties.strength(2.0F, 4.0F).requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block SHATTERSTONE = registerBlock("shatterstone", properties -> new Block(properties.strength(4.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    // Limestone family (maybe inherit more sandstone esque properties in the future idk)
    public static final Block LIMESTONE = registerBlock("limestone",
            properties -> new Block(properties.strength(0.8f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block RUBBLED_LIMESTONE = registerBlock("rubbled_limestone",
            properties -> new Block(properties.strength(0.6f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block MOSSY_LIMESTONE = registerBlock("mossy_limestone",
            properties -> new Block(properties.strength(0.8f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block POLISHED_LIMESTONE = registerBlock("polished_limestone",
            properties -> new Block(properties.strength(2f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block WEATHERED_LIMESTONE = registerBlock("weathered_limestone",
            properties -> new Block(properties.strength(0.6f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block LIMESTONE_PILLAR = registerBlock("limestone_pillar",
            properties -> new RotatedPillarBlock(properties.strength(3f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block LIMESTONE_BRICKS = registerBlock("limestone_bricks",
            properties -> new Block(properties.strength(3f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block LIMESTONE_BRICK_STAIRS = registerBlock("limestone_brick_stairs",
            properties -> new StairBlock(ModBlocks.RHYOLITE.defaultBlockState(),
                    properties.strength(3f)));
    public static final Block LIMESTONE_BRICK_SLAB = registerBlock("limestone_brick_slab",
            properties -> new SlabBlock(
                    properties.strength(3f)));
    public static final Block LIMESTONE_BRICK_WALL = registerBlock("limestone_brick_wall",
            properties -> new WallBlock(
                    properties.strength(3f)));
    public static final Block LIMESTONE_STAIRS = registerBlock("limestone_stairs",
            properties -> new StairBlock(ModBlocks.RHYOLITE.defaultBlockState(),
                    properties.strength(3f)));
    public static final Block LIMESTONE_SLAB = registerBlock("limestone_slab",
            properties -> new SlabBlock(
                    properties.strength(3f)));
    public static final Block LIMESTONE_WALL = registerBlock("limestone_wall",
            properties -> new WallBlock(
                    properties.strength(3f)));
    public static final Block RUBBLED_LIMESTONE_STAIRS = registerBlock("rubbled_limestone_stairs",
            properties -> new StairBlock(ModBlocks.RHYOLITE.defaultBlockState(),
                    properties.strength(0.6f)));
    public static final Block RUBBLED_LIMESTONE_SLAB = registerBlock("rubbled_limestone_slab",
            properties -> new SlabBlock(
                    properties.strength(0.6f)));
    public static final Block RUBBLED_LIMESTONE_WALL = registerBlock("rubbled_limestone_wall",
            properties -> new WallBlock(
                    properties.strength(0.6f)));
    public static final Block POLISHED_LIMESTONE_STAIRS = registerBlock("polished_limestone_stairs",
            properties -> new StairBlock(ModBlocks.RHYOLITE.defaultBlockState(),
                    properties.strength(3f)));
    public static final Block POLISHED_LIMESTONE_SLAB = registerBlock("polished_limestone_slab",
            properties -> new SlabBlock(
                    properties.strength(3f)));
    public static final Block POLISHED_LIMESTONE_WALL = registerBlock("polished_limestone_wall",
            properties -> new WallBlock(
                    properties.strength(3f)));

    // Rendzina & Lithosol (possible future updates...?)
    public static final Block RENDZINA = registerBlock("rendzina",
            properties -> new Block(properties.strength(0.5f)
                    .sound(SoundType.GRAVEL).instrument(NoteBlockInstrument.SNARE)));
    public static final Block LITHOSOL = registerBlock("lithosol",
            properties -> new Block(properties.strength(0.8f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));

    // Elsewhere: Mossy Rhyolite and Amphibolite family
    public static final Block MOSSY_RHYOLITE = registerBlock("mossy_rhyolite",
            properties -> new Block(properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block AMPHIBOLITE = registerBlock("amphibolite",
            properties -> new Block(properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block POLISHED_AMPHIBOLITE = registerBlock("polished_amphibolite",
            properties -> new Block(properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block AMPHIBOLITE_BRICKS = registerBlock("amphibolite_bricks",
            properties -> new Block(properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block CHISELED_AMPHIBOLITE = registerBlock("chiseled_amphibolite",
            properties -> new Block(properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block CHISELED_AMPHIBOLITE_BRICKS = registerBlock("chiseled_amphibolite_bricks",
            properties -> new Block(properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));
    public static final Block AMPHIBOLITE_STAIRS = registerBlock("amphibolite_stairs",
            properties -> new StairBlock(ModBlocks.AMPHIBOLITE.defaultBlockState(), properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block AMPHIBOLITE_SLAB = registerBlock("amphibolite_slab",
            properties -> new SlabBlock(properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block AMPHIBOLITE_WALL = registerBlock("amphibolite_wall",
            properties -> new WallBlock(properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block POLISHED_AMPHIBOLITE_STAIRS = registerBlock("polished_amphibolite_stairs",
            properties -> new StairBlock(ModBlocks.POLISHED_AMPHIBOLITE.defaultBlockState(), properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block POLISHED_AMPHIBOLITE_SLAB = registerBlock("polished_amphibolite_slab",
            properties -> new SlabBlock(properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block POLISHED_AMPHIBOLITE_WALL = registerBlock("polished_amphibolite_wall",
            properties -> new WallBlock(properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block AMPHIBOLITE_BRICK_STAIRS = registerBlock("amphibolite_brick_stairs",
            properties -> new StairBlock(ModBlocks.AMPHIBOLITE_BRICKS.defaultBlockState(), properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block AMPHIBOLITE_BRICK_SLAB = registerBlock("amphibolite_brick_slab",
            properties -> new SlabBlock(properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block AMPHIBOLITE_BRICK_WALL = registerBlock("amphibolite_brick_wall",
            properties -> new WallBlock(properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final Block LAPILLI = registerBlock("lapilli",
            properties -> new FallingBlock(properties
                    .strength(0.5F)
                    .sound(SoundType.GRAVEL)) {
                @Override
                public int getDustColor(
                        BlockState state,
                        BlockGetter level,
                        BlockPos pos
                ) {
                    return 0x827A80;
                }
            });


    // Maple family    // Elsewhere secondary rock families (natural and polished)
    public static final Block DACITE = registerBlock("dacite", properties -> new Block(properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block DACITE_STAIRS = registerBlock("dacite_stairs", properties -> new StairBlock(DACITE.defaultBlockState(), properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block DACITE_SLAB = registerBlock("dacite_slab", properties -> new SlabBlock(properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block DACITE_WALL = registerBlock("dacite_wall", properties -> new WallBlock(properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final Block POLISHED_DACITE = registerBlock("polished_dacite", properties -> new Block(properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block POLISHED_DACITE_STAIRS = registerBlock("polished_dacite_stairs", properties -> new StairBlock(POLISHED_DACITE.defaultBlockState(), properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block POLISHED_DACITE_SLAB = registerBlock("polished_dacite_slab", properties -> new SlabBlock(properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block POLISHED_DACITE_WALL = registerBlock("polished_dacite_wall", properties -> new WallBlock(properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final Block CHERT = registerBlock("chert", properties -> new Block(properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block CHERT_STAIRS = registerBlock("chert_stairs", properties -> new StairBlock(CHERT.defaultBlockState(), properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block CHERT_SLAB = registerBlock("chert_slab", properties -> new SlabBlock(properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block CHERT_WALL = registerBlock("chert_wall", properties -> new WallBlock(properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final Block POLISHED_CHERT = registerBlock("polished_chert", properties -> new Block(properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block POLISHED_CHERT_STAIRS = registerBlock("polished_chert_stairs", properties -> new StairBlock(POLISHED_CHERT.defaultBlockState(), properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block POLISHED_CHERT_SLAB = registerBlock("polished_chert_slab", properties -> new SlabBlock(properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block POLISHED_CHERT_WALL = registerBlock("polished_chert_wall", properties -> new WallBlock(properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final Block SERPENTINITE = registerBlock("serpentinite", properties -> new Block(properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block SERPENTINITE_STAIRS = registerBlock("serpentinite_stairs", properties -> new StairBlock(SERPENTINITE.defaultBlockState(), properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block SERPENTINITE_SLAB = registerBlock("serpentinite_slab", properties -> new SlabBlock(properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block SERPENTINITE_WALL = registerBlock("serpentinite_wall", properties -> new WallBlock(properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final Block POLISHED_SERPENTINITE = registerBlock("polished_serpentinite", properties -> new Block(properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block POLISHED_SERPENTINITE_STAIRS = registerBlock("polished_serpentinite_stairs", properties -> new StairBlock(POLISHED_SERPENTINITE.defaultBlockState(), properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block POLISHED_SERPENTINITE_SLAB = registerBlock("polished_serpentinite_slab", properties -> new SlabBlock(properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final Block POLISHED_SERPENTINITE_WALL = registerBlock("polished_serpentinite_wall", properties -> new WallBlock(properties.strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));


    public static final Block MAPLE_LOG = registerBlock("maple_log",
            properties -> new RotatedPillarBlock(properties.strength(2f).instrument(NoteBlockInstrument.BASS)
                    .sound(SoundType.WOOD).ignitedByLava()));
    public static final Block MAPLE_WOOD = registerBlock("maple_wood",
            properties -> new RotatedPillarBlock(properties.strength(2f).instrument(NoteBlockInstrument.BASS)
                    .sound(SoundType.WOOD).ignitedByLava()));
    public static final Block STRIPPED_MAPLE_LOG = registerBlock("stripped_maple_log",
            properties -> new RotatedPillarBlock(properties.strength(2f).instrument(NoteBlockInstrument.BASS)
                    .sound(SoundType.WOOD).ignitedByLava()));
    public static final Block STRIPPED_MAPLE_WOOD = registerBlock("stripped_maple_wood",
            properties -> new RotatedPillarBlock(properties.strength(2f).instrument(NoteBlockInstrument.BASS)
                    .sound(SoundType.WOOD).ignitedByLava()));
    public static final Block MAPLE_PLANKS = registerBlock("maple_planks",
            properties -> new Block(properties.mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block MAPLE_LEAVES = registerBlock("maple_leaves",
            properties -> new MapleLeavesBlock(
                    0.01f,
                    ParticleTypes.CHERRY_LEAVES,
                    AmbientLeavesBlockSoundPlayer.noAmbientSound(),
                    properties
                            .mapColor(MapColor.PLANT)
                            .strength(0.2F)
                            .randomTicks()
                            .sound(SoundType.POPLAR_LEAVES)
                            .noOcclusion()
                            .isValidSpawn(Blocks::ocelotOrParrot)
                            .isSuffocating(Blocks::never)
                            .ignitedByLava()
                            .pushReaction(PushReaction.POPPED)
                            .isRedstoneConductor(Blocks::never)
            ));
    public static final Block MAPLE_SAPLING = registerBlock("maple_sapling",
            properties -> new SaplingBlock(ModTreeGrowers.MAPLE, properties
                    .mapColor(MapColor.PLANT).noCollision().randomTicks().instabreak()
                    .sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));

    public static final Block MAPLE_STAIRS = registerBlock("maple_stairs",
            properties -> new StairBlock(ModBlocks.MAPLE_PLANKS.defaultBlockState(),
                    properties.strength(3f)));
    public static final Block MAPLE_SLAB = registerBlock("maple_slab",
            properties -> new SlabBlock(
                    properties.strength(3f)));
    public static final Block MAPLE_DOOR = registerBlock("maple_door",
            properties -> new DoorBlock(BlockSetType.CHERRY, properties.strength(3f).noOcclusion()));
    public static final Block MAPLE_TRAPDOOR = registerBlock("maple_trapdoor",
            properties -> new TrapDoorBlock(BlockSetType.CHERRY, properties.strength(3f).noOcclusion()));
    public static final Block MAPLE_BUTTON = registerBlock("maple_button",
            properties -> new ButtonBlock(BlockSetType.CHERRY, 15, properties.strength(3f).noCollision()));
    public static final Block MAPLE_PRESSURE_PLATE = registerBlock("maple_pressure_plate",
            properties -> new PressurePlateBlock(BlockSetType.CHERRY, properties.mapColor(MapColor.COLOR_ORANGE).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(3f).noCollision().ignitedByLava().pushReaction(PushReaction.POPPED)));
    public static final Block MAPLE_FENCE = registerBlock("maple_fence",
            properties -> new FenceBlock(properties.strength(2f)));
    public static final Block MAPLE_FENCE_GATE = registerBlock("maple_fence_gate",
            properties -> new FenceGateBlock(WoodType.CHERRY, properties.strength(2f)));
    public static final Block POTTED_MAPLE_SAPLING = registerBlockWithoutBlockItem("potted_maple_sapling",
            properties -> new FlowerPotBlock(MAPLE_SAPLING, properties
                    .instabreak().noOcclusion().pushReaction(PushReaction.POPPED)));

    // Ginkgo family: golden foliage, medium-height stands and occasional old-growth giants.
    public static final Block GINKGO_LOG = registerBlock("ginkgo_log", properties -> new RotatedPillarBlock(properties
            .strength(2f).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block GINKGO_WOOD = registerBlock("ginkgo_wood", properties -> new RotatedPillarBlock(properties
            .strength(2f).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block STRIPPED_GINKGO_LOG = registerBlock("stripped_ginkgo_log", properties -> new RotatedPillarBlock(properties
            .strength(2f).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block STRIPPED_GINKGO_WOOD = registerBlock("stripped_ginkgo_wood", properties -> new RotatedPillarBlock(properties
            .strength(2f).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block GINKGO_PLANKS = registerBlock("ginkgo_planks", properties -> new Block(properties
            .mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.BASS)
            .strength(2f, 3f).sound(SoundType.WOOD).ignitedByLava()));
    // Use ordinary leaf decay; the golden pixel art stays untinted.
    public static final Block GINKGO_LEAVES = registerBlock("ginkgo_leaves", properties -> new MapleLeavesBlock(0.0F, ParticleTypes.CHERRY_LEAVES,
            AmbientLeavesBlockSoundPlayer.noAmbientSound(), properties.mapColor(MapColor.COLOR_YELLOW)
            .strength(0.2F).randomTicks().sound(SoundType.POPLAR_LEAVES).noOcclusion()
            .isValidSpawn(Blocks::ocelotOrParrot).isSuffocating(Blocks::never)
            .ignitedByLava().pushReaction(PushReaction.POPPED)
            .isRedstoneConductor(Blocks::never)));
    public static final Block GINKGO_SAPLING = registerBlock("ginkgo_sapling", properties -> new SaplingBlock(
            ModTreeGrowers.GINKGO, properties.mapColor(MapColor.PLANT).noCollision().randomTicks()
            .instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));
    public static final Block GINKGO_STAIRS = registerBlock("ginkgo_stairs", properties -> new StairBlock(
            ModBlocks.GINKGO_PLANKS.defaultBlockState(), properties.strength(3f)));
    public static final Block GINKGO_SLAB = registerBlock("ginkgo_slab", properties -> new SlabBlock(properties.strength(3f)));
    public static final Block GINKGO_DOOR = registerBlock("ginkgo_door", properties -> new DoorBlock(
            BlockSetType.CHERRY, properties.strength(3f).noOcclusion()));
    public static final Block GINKGO_TRAPDOOR = registerBlock("ginkgo_trapdoor", properties -> new TrapDoorBlock(
            BlockSetType.CHERRY, properties.strength(3f).noOcclusion()));
    public static final Block GINKGO_BUTTON = registerBlock("ginkgo_button", properties -> new ButtonBlock(
            BlockSetType.CHERRY, 15, properties.strength(3f).noCollision()));
    public static final Block GINKGO_PRESSURE_PLATE = registerBlock("ginkgo_pressure_plate", properties -> new PressurePlateBlock(
            BlockSetType.CHERRY, properties.mapColor(MapColor.COLOR_YELLOW).forceSolidOn()
            .instrument(NoteBlockInstrument.BASS).strength(3f).noCollision()
            .ignitedByLava().pushReaction(PushReaction.POPPED)));
    public static final Block GINKGO_FENCE = registerBlock("ginkgo_fence", properties -> new FenceBlock(properties.strength(2f)));
    public static final Block GINKGO_FENCE_GATE = registerBlock("ginkgo_fence_gate", properties -> new FenceGateBlock(
            WoodType.CHERRY, properties.strength(2f)));
    public static final Block POTTED_GINKGO_SAPLING = registerBlockWithoutBlockItem("potted_ginkgo_sapling",
            properties -> new FlowerPotBlock(GINKGO_SAPLING, properties.instabreak()
                    .noOcclusion().pushReaction(PushReaction.POPPED)));


    // Hinoki cypress: cool evergreen foliage and a full spruce-like wood palette.
    public static final Block HINOKI_LOG = registerBlock("hinoki_log",
            properties -> new RotatedPillarBlock(properties.strength(2.0F).sound(SoundType.WOOD)
                    .instrument(NoteBlockInstrument.BASS).ignitedByLava()));
    public static final Block HINOKI_WOOD = registerBlock("hinoki_wood",
            properties -> new RotatedPillarBlock(properties.strength(2.0F).sound(SoundType.WOOD)
                    .instrument(NoteBlockInstrument.BASS).ignitedByLava()));
    public static final Block STRIPPED_HINOKI_LOG = registerBlock("stripped_hinoki_log",
            properties -> new RotatedPillarBlock(properties.strength(2.0F).sound(SoundType.WOOD)
                    .instrument(NoteBlockInstrument.BASS).ignitedByLava()));
    public static final Block STRIPPED_HINOKI_WOOD = registerBlock("stripped_hinoki_wood",
            properties -> new RotatedPillarBlock(properties.strength(2.0F).sound(SoundType.WOOD)
                    .instrument(NoteBlockInstrument.BASS).ignitedByLava()));
    public static final Block HINOKI_PLANKS = registerBlock("hinoki_planks",
            properties -> new Block(properties.mapColor(MapColor.WOOD).strength(2.0F, 3.0F)
                    .instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block HINOKI_LEAVES = registerBlock("hinoki_leaves",
            properties -> new HinokiLeavesBlock(0.0F, ParticleTypes.CHERRY_LEAVES,
                    AmbientLeavesBlockSoundPlayer.noAmbientSound(), properties.mapColor(MapColor.PLANT)
                    .strength(0.2F).randomTicks().sound(SoundType.POPLAR_LEAVES).noOcclusion()
                    .isValidSpawn(Blocks::ocelotOrParrot).isSuffocating(Blocks::never)
                    .ignitedByLava().pushReaction(PushReaction.POPPED)
                    .isRedstoneConductor(Blocks::never)));
    public static final Block HINOKI_SAPLING = registerBlock("hinoki_sapling",
            properties -> new SaplingBlock(ModTreeGrowers.HINOKI,
                    properties.mapColor(MapColor.PLANT).noCollision().randomTicks()
                            .instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));
    public static final Block POTTED_HINOKI_SAPLING = registerBlockWithoutBlockItem("potted_hinoki_sapling",
            properties -> new FlowerPotBlock(HINOKI_SAPLING,
                    properties.instabreak().noOcclusion().pushReaction(PushReaction.POPPED)));
    public static final Block HINOKI_STAIRS = registerBlock("hinoki_stairs",
            properties -> new StairBlock(HINOKI_PLANKS.defaultBlockState(),
                    properties.strength(3.0F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block HINOKI_SLAB = registerBlock("hinoki_slab",
            properties -> new SlabBlock(properties.strength(3.0F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block HINOKI_DOOR = registerBlock("hinoki_door",
            properties -> new DoorBlock(BlockSetType.SPRUCE,
                    properties.strength(3.0F).noOcclusion().sound(SoundType.WOOD).ignitedByLava()));
    public static final Block HINOKI_TRAPDOOR = registerBlock("hinoki_trapdoor",
            properties -> new TrapDoorBlock(BlockSetType.SPRUCE,
                    properties.strength(3.0F).noOcclusion().sound(SoundType.WOOD).ignitedByLava()));
    public static final Block HINOKI_BUTTON = registerBlock("hinoki_button",
            properties -> new ButtonBlock(BlockSetType.SPRUCE, 30,
                    properties.strength(0.5F).noCollision().sound(SoundType.WOOD)));
    public static final Block HINOKI_PRESSURE_PLATE = registerBlock("hinoki_pressure_plate",
            properties -> new PressurePlateBlock(BlockSetType.SPRUCE,
                    properties.mapColor(MapColor.WOOD).forceSolidOn().strength(0.5F).noCollision()
                            .sound(SoundType.WOOD).ignitedByLava()));
    public static final Block HINOKI_FENCE = registerBlock("hinoki_fence",
            properties -> new FenceBlock(properties.strength(2.0F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block HINOKI_FENCE_GATE = registerBlock("hinoki_fence_gate",
            properties -> new FenceGateBlock(WoodType.SPRUCE,
                    properties.strength(2.0F).sound(SoundType.WOOD).ignitedByLava()));

    // Glowshroom Family
    public static final Block GLOWSHROOM = registerBlock("glowshroom", properties -> new GlowshroomBlock(
            ModFeatures.HUGE_GLOWSHROOM_KEY, properties.noCollision().instabreak().randomTicks()
            .sound(SoundType.FUNGUS).lightLevel(state -> 12)));
    public static final Block POTTED_GLOWSHROOM = registerBlockWithoutBlockItem("potted_glowshroom",
            properties -> new FlowerPotBlock(GLOWSHROOM, properties
                    .instabreak().noOcclusion().pushReaction(PushReaction.POPPED)));
    public static final Block GLOWSHROOM_BLOCK = registerBlock(
            "glowshroom_block",
            properties -> new HugeMushroomBlock(properties.strength(0.2f)
                    .sound(SoundType.WOOD).lightLevel(state -> 10)));

    // Exactly three blocks high: one waterlogged base, two emergent segments.
    public static final Block CATTAILS = registerBlock("cattails",
            p -> new ThreeBlockWetlandPlantBlock(p.noCollision().instabreak()
                    .sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));
    public static final Block REEDS = registerBlock("reeds",
            p -> new ThreeBlockWetlandPlantBlock(p.noCollision().instabreak()
                    .sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));

    // Small vegetation from the already supplied 16x16 texture atlas.
    public static final Block ALPINE_GRASS = registerBlock("alpine_grass",
            p -> new FlowerBlock(MobEffects.WATER_BREATHING, 5.0F,
                    p.noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));
    public static final Block HAKONE_GRASS = registerBlock("hakone_grass",
            p -> new FlowerBlock(MobEffects.WATER_BREATHING, 5.0F,
                    p.noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));
    public static final Block IRIS_FLOWER = registerBlock("iris_flower",
            p -> new FlowerBlock(MobEffects.WATER_BREATHING, 5.0F,
                    p.noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));
    public static final Block GINKGO_FALLEN_LEAVES = registerBlock("ginkgo_fallen_leaves",
            p -> new LeafLitterBlock(p.noCollision().strength(0.1f).sound(SoundType.GRASS)));
    // Duckweed grows in 1-4 floating patches, like leaf litter, without removing water.
    public static final Block DUCKWEED = registerBlock("duckweed",
            p -> new FloatingLitterBlock(p.noCollision().instabreak().sound(SoundType.GRASS)));
    public static final Block LOTUS = registerBlock("lotus",
            p -> new LotusPadBlock(p.noCollision().instabreak().sound(SoundType.GRASS)));
    public static final Block LOTUS_FLOWER = registerBlock("lotus_flower",
            p -> new LotusFlowerBlock(p.noCollision().instabreak().sound(SoundType.GRASS)));

    // Crops & Bushes
    public static final Block RICE_CROP = registerBlockWithoutBlockItem("rice_crop",
            properties -> new RiceCropBlock(properties.noCollision().randomTicks().instabreak()
                    .sound(SoundType.CROP).pushReaction(PushReaction.POPPED)));
    public static final Block DAIKON_CROP = registerBlockWithoutBlockItem("daikon_crop",
            properties -> new DaikonCropBlock(properties.noCollision().randomTicks().instabreak().sound(SoundType.CROP)
                    .pushReaction(PushReaction.POPPED)));
    public static final Block HASKAP_BERRY_BUSH = registerBlockWithoutBlockItem("haskap_berry_bush",
            properties -> new HaskapBerryBushBlock(properties.noCollision().instabreak().sound(SoundType.SWEET_BERRY_BUSH)
                    .pushReaction(PushReaction.POPPED)));
    public static final Block BARBERRY_BUSH = registerBlockWithoutBlockItem("barberry_bush",
            properties -> new BarberryBushBlock(properties.noCollision().instabreak().sound(SoundType.SWEET_BERRY_BUSH)
                    .pushReaction(PushReaction.POPPED)));
    public static final Block BELLFLOWERS = registerBlock("bellflowers",
            properties -> new FlowerBlock(
                    MobEffects.WATER_BREATHING, 5.0F,
                    properties.noCollision()
                            .instabreak()
                            .sound(SoundType.GRASS)
                            .pushReaction(PushReaction.POPPED)
            ));
    public static final VariableHeightPlantBlock SILVER_GRASS =
            (VariableHeightPlantBlock) registerBlock("silver_grass",
                    properties -> new VariableHeightPlantBlock(2,
                            properties.noCollision().instabreak().randomTicks()
                                    .sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));
    public static final VariableHeightPlantBlock BUSH_CLOVER =
            (VariableHeightPlantBlock) registerBlock("bush_clover",
                    properties -> new VariableHeightPlantBlock(2,
                            properties.noCollision().instabreak().randomTicks()
                                    .sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));
    public static final VariableHeightPlantBlock DWARF_BAMBOO =
            (VariableHeightPlantBlock) registerBlock("dwarf_bamboo",
                    properties -> new VariableHeightPlantBlock(4,
                            properties.noCollision().instabreak().randomTicks().sound(SoundType.BAMBOO).pushReaction(PushReaction.POPPED)));


    // NEW ELSEWHERE FOREST FAMILIES

    public static final Block WISTERIA_LOG = registerBlock("wisteria_log", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block WISTERIA_WOOD = registerBlock("wisteria_wood", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block STRIPPED_WISTERIA_LOG = registerBlock("stripped_wisteria_log", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block STRIPPED_WISTERIA_WOOD = registerBlock("stripped_wisteria_wood", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block WISTERIA_PLANKS = registerBlock("wisteria_planks", p -> new Block(p.strength(2F,3F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block WISTERIA_LEAVES = registerBlock("wisteria_leaves", p -> new ExtendedForestLeavesBlock(0F, ParticleTypes.CHERRY_LEAVES,
            AmbientLeavesBlockSoundPlayer.noAmbientSound(), p.strength(0.2F).randomTicks().noOcclusion()
            .ignitedByLava().sound(SoundType.GRASS)));
    public static final Block WISTERIA_SAPLING = registerBlock("wisteria_sapling", p -> new SaplingBlock(ModTreeGrowers.WISTERIA,
            p.noCollision().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));

    public static final Block WILLOW_LOG = registerBlock("willow_log", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block WILLOW_WOOD = registerBlock("willow_wood", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block STRIPPED_WILLOW_LOG = registerBlock("stripped_willow_log", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block STRIPPED_WILLOW_WOOD = registerBlock("stripped_willow_wood", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block WILLOW_PLANKS = registerBlock("willow_planks", p -> new Block(p.strength(2F,3F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block WILLOW_LEAVES = registerBlock("willow_leaves", p -> new ExtendedForestLeavesBlock(0F, ParticleTypes.CHERRY_LEAVES,
            AmbientLeavesBlockSoundPlayer.noAmbientSound(), p.strength(0.2F).randomTicks().noOcclusion()
            .ignitedByLava().sound(SoundType.GRASS)));
    public static final Block WILLOW_SAPLING = registerBlock("willow_sapling", p -> new SaplingBlock(ModTreeGrowers.WILLOW,
            p.noCollision().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));

    public static final Block CEDAR_LOG = registerBlock("cedar_log", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block CEDAR_WOOD = registerBlock("cedar_wood", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block STRIPPED_CEDAR_LOG = registerBlock("stripped_cedar_log", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block STRIPPED_CEDAR_WOOD = registerBlock("stripped_cedar_wood", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block CEDAR_PLANKS = registerBlock("cedar_planks", p -> new Block(p.strength(2F,3F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block CEDAR_LEAVES = registerBlock("cedar_leaves", p -> new ExtendedForestLeavesBlock(0F, ParticleTypes.CHERRY_LEAVES,
            AmbientLeavesBlockSoundPlayer.noAmbientSound(), p.strength(0.2F).randomTicks().noOcclusion()
            .ignitedByLava().sound(SoundType.GRASS)));
    public static final Block CEDAR_SAPLING = registerBlock("cedar_sapling", p -> new SaplingBlock(ModTreeGrowers.CEDAR,
            p.noCollision().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));

    public static final Block PERSIMMON_LOG = registerBlock("persimmon_log", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block PERSIMMON_WOOD = registerBlock("persimmon_wood", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block STRIPPED_PERSIMMON_LOG = registerBlock("stripped_persimmon_log", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block STRIPPED_PERSIMMON_WOOD = registerBlock("stripped_persimmon_wood", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block PERSIMMON_PLANKS = registerBlock("persimmon_planks", p -> new Block(p.strength(2F,3F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block PERSIMMON_LEAVES = registerBlock("persimmon_leaves", p -> new ExtendedForestLeavesBlock(0F, ParticleTypes.CHERRY_LEAVES,
            AmbientLeavesBlockSoundPlayer.noAmbientSound(), p.strength(0.2F).randomTicks().noOcclusion()
            .ignitedByLava().sound(SoundType.GRASS)));
    public static final Block PERSIMMON_SAPLING = registerBlock("persimmon_sapling", p -> new SaplingBlock(ModTreeGrowers.PERSIMMON,
            p.noCollision().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));

    public static final Block MIMOSA_LOG = registerBlock("mimosa_log", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block MIMOSA_WOOD = registerBlock("mimosa_wood", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block STRIPPED_MIMOSA_LOG = registerBlock("stripped_mimosa_log", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block STRIPPED_MIMOSA_WOOD = registerBlock("stripped_mimosa_wood", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block MIMOSA_PLANKS = registerBlock("mimosa_planks", p -> new Block(p.strength(2F,3F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block MIMOSA_LEAVES = registerBlock("mimosa_leaves", p -> new ExtendedForestLeavesBlock(0F, ParticleTypes.CHERRY_LEAVES,
            AmbientLeavesBlockSoundPlayer.noAmbientSound(), p.strength(0.2F).randomTicks().noOcclusion()
            .ignitedByLava().sound(SoundType.GRASS)));
    public static final Block MIMOSA_SAPLING = registerBlock("mimosa_sapling", p -> new SaplingBlock(ModTreeGrowers.MIMOSA,
            p.noCollision().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));

    // Elsewhere botanical palette. All textures already exist in the repository.
    public static final Block SILT = registerBlock("silt", p -> new Block(p.strength(0.55F).sound(SoundType.MUD)));
    public static final Block WILLOW_ROOTS = registerBlock("willow_roots", p -> new ForestRootsBlock(p.noOcclusion().strength(1.5F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block HANGING_WILLOW_LEAVES = registerBlock("hanging_willow_leaves", p -> new HangingAccentBlock(p.noCollision().instabreak().sound(SoundType.GRASS)));
    public static final Block HANGING_WISTERIA_BLOSSOMS = registerBlock("hanging_wisteria_blossoms", p -> new HangingAccentBlock(p.noCollision().instabreak().sound(SoundType.GRASS)));
    public static final Block CLIMBING_WISTERIA_VINE = registerBlock("climbing_wisteria_vine", p -> new VineBlock(p.noCollision().randomTicks().instabreak().sound(SoundType.VINE)));
    public static final Block FALLEN_WISTERIA_PETALS = registerBlock("fallen_wisteria_petals", p -> new LeafLitterBlock(p.noCollision().strength(0.1F).sound(SoundType.GRASS)));
    public static final Block WILD_VIOLET = registerBlock("wild_violet", p -> new FlowerBlock(MobEffects.WATER_BREATHING, 5.0F, p.noCollision().instabreak().sound(SoundType.GRASS)));
    public static final Block MARSH_MARIGOLD = registerBlock("marsh_marigold", p -> new FlowerBlock(MobEffects.WATER_BREATHING, 5.0F, p.noCollision().instabreak().sound(SoundType.GRASS)));
    public static final Block WATER_SEDGE = registerBlock("water_sedge", p -> new FlexibleWetlandPlantBlock(3, p.noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));
    public static final Block MARSH_MARIGOLD_WATER = registerBlock("marsh_marigold_water", p -> new FlexibleWetlandPlantBlock(2, p.noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));
    public static final WaterMintBlock WATER_MINT = (WaterMintBlock) registerBlock("water_mint", p -> new WaterMintBlock(p.noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));


    // Elsewhere forest detailing; models/blockstates manually mapped in assets/.
    public static final Block CEDAR_ROOTS = registerBlock("cedar_roots", p -> new ForestRootsBlock(p.noOcclusion().strength(1.5F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block MOSSY_CEDAR_LOG = registerBlock("mossy_cedar_log", p -> new RotatedPillarBlock(p.strength(2F).sound(SoundType.WOOD).ignitedByLava()));
    public static final Block HANGING_FOREST_MOSS = registerBlock("hanging_forest_moss", p -> new HangingAccentBlock(p.noCollision().instabreak().sound(SoundType.VINE).pushReaction(PushReaction.POPPED)));
    public static final Block LIVERWORT = registerBlock("liverwort", p -> new LeafLitterBlock(p.noCollision().strength(0.1F).sound(SoundType.MOSS_CARPET).pushReaction(PushReaction.POPPED)));
    public static final Block CLUBMOSS = registerBlock("clubmoss", p -> new Block(p.mapColor(MapColor.PLANT).strength(0.1F).sound(SoundType.MOSS)));
    public static final Block CLUBMOSS_LOWLYING = registerBlock("clubmoss_lowlying",
            p -> new ClubmossLowlyingBlock(p.noCollision().instabreak()
                    .sound(SoundType.MOSS_CARPET).pushReaction(PushReaction.POPPED)));
    public static final Block BRACKET_FUNGUS = registerBlock("bracket_fungus", p -> new BracketFungusBlock(p.noCollision().noOcclusion().instabreak().sound(SoundType.FUNGUS).pushReaction(PushReaction.POPPED)));

    public static final Block FALLEN_AUTUMN_LEAVES = registerBlock("fallen_autumn_leaves", p -> new LeafLitterBlock(p.noCollision().strength(0.1F).sound(SoundType.GRASS)));
    // SILVER_GRASS already exists. Preserve its ID and display it as "Silvergrass".
    public static final Block ANEMONE_FLOWERS = registerBlock("anemone_flowers", p -> new FlowerBlock(MobEffects.WATER_BREATHING, 5.0F, p.noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));
    public static final Block BUSH = registerBlock("bush", p -> new FlowerBlock(MobEffects.WATER_BREATHING, 5.0F, p.noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));

    public static final Block FLOWERING_MIMOSA_LEAVES = registerBlock("flowering_mimosa_leaves", p -> new ExtendedForestLeavesBlock(0F, ParticleTypes.CHERRY_LEAVES, AmbientLeavesBlockSoundPlayer.noAmbientSound(), p.strength(0.2F).randomTicks().noOcclusion().ignitedByLava().sound(SoundType.GRASS)));
    public static final Block MIMOSA_BLOSSOMS = registerBlock("mimosa_blossoms", p -> new LeafBlossomBlock(7, 3, p.noCollision().noOcclusion().instabreak().sound(SoundType.AMETHYST_CLUSTER).pushReaction(PushReaction.POPPED)));
    public static final Block FALLEN_MIMOSA_PETALS = registerBlock("fallen_mimosa_petals", p -> new LeafLitterBlock(p.noCollision().strength(0.1F).sound(SoundType.GRASS)));
    public static final Block FEATHERGRASS = registerBlock("feathergrass", p -> new FlowerBlock(MobEffects.WATER_BREATHING, 5.0F, p.noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));
    public static final Block PINK_CLOVER = registerBlock("pink_clover", p -> new LeafLitterBlock(p.noCollision().strength(0.1F).sound(SoundType.GRASS)));

    public static ResourceKey<Block> getRK(Block block) {
        return BuiltInRegistries.BLOCK.getResourceKey(block).get();
    }

    /** Vanilla experience-drop behavior, including Silk Touch suppression. */
    private static Block registerOreBlock(String name, float hardness, SoundType sound, int minXp, int maxXp) {
        return registerBlock(name, p -> new DropExperienceBlock(UniformInt.of(minXp, maxXp),
                p.strength(hardness, 6.0F).requiresCorrectToolForDrops().sound(sound)));
    }

    private static Block registerMineralBlock(String name, float hardness, SoundType sound) {
        return registerBlock(name, p -> new Block(p.strength(hardness, 6.0F).requiresCorrectToolForDrops().sound(sound)));
    }

    private static Block registerBlock(String name, Function<BlockBehaviour.Properties, Block> function) {
        Block toRegister = function.apply(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name))));
        registerBlockItem(name, toRegister);
        return Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name), toRegister);
    }

    private static Block registerBlockWithoutBlockItem(String name, Function<BlockBehaviour.Properties, Block> function) {
        Block toRegister = function.apply(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name))));;
        return Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name), toRegister);
    }

    private static void registerBlockItem(String name, Block block) {
        Item.Properties properties = new Item.Properties().useBlockDescriptionPrefix()
                .setId(ResourceKey.create(Registries.ITEM,
                        Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name)));
        if (name.equals("cannel_block")) {
            properties.component(DataComponents.COOKING_FUEL, new CookingFuel(
                    ContextIntProviders.COOKING_TIME_COAL_BLOCK, ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER));
        }
        BlockItem item = switch (name) {
            case "duckweed", "lotus" -> new PlaceOnWaterBlockItem(block, properties);
            case "lotus_flower" -> new LotusFlowerItem(block, properties);
            default -> new BlockItem(block, properties);
        };
        Registry.register(BuiltInRegistries.ITEM,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name), item);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(output -> {
            if (!name.equals("fungal_mat")) output.accept(block);
        });

    }

    public static void registerModBlocks() {
        BugsBugsAndMoreBugs.LOGGER.info("Registering Mod Blocks for " + BugsBugsAndMoreBugs.MOD_ID);
        // 26.3 block transformers replace the removed Fabric tilling registry.
        BlockTransformerHelper.registerTilling(LOAM, TILLED_LOAM);
        BlockTransformerHelper.registerTilling(GRASSY_LOAM, TILLED_LOAM);
        BlockTransformerHelper.registerTilling(COARSE_LOAM, TILLED_LOAM);
        BlockTransformerHelper.registerTilling(ROOTED_LOAM, TILLED_LOAM);
    }
}
