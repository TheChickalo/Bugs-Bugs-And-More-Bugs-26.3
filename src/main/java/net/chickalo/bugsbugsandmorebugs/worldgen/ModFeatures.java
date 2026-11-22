
package net.chickalo.bugsbugsandmorebugs.worldgen;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.worldgen.feature.*;
import net.chickalo.bugsbugsandmorebugs.worldgen.tree.MapleFoliagePlacer;
import net.chickalo.bugsbugsandmorebugs.worldgen.tree.MapleTrunkPlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.CherryFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.RandomSpreadFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.CherryTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.BendingTrunkPlacer;


import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;

public class ModFeatures {
    // Three Ginkgo sizes with vanilla foliage placers for more natural silhouettes.
    public static final ResourceKey<Feature> GINKGO_SMALL_KEY = registerKey("ginkgo_small");
    public static final ResourceKey<Feature> GINKGO_TREE_KEY = registerKey("ginkgo_tree");
    // Rare old-growth tree: taller bole and a wider, flatter crown.
    public static final ResourceKey<Feature> GINKGO_OLD_GROWTH_KEY = registerKey("ginkgo_old_growth");


    public static final ResourceKey<Feature> BAMBOO_POOLS_KEY = registerKey("bamboo_pools");
    public static final ResourceKey<Feature> BAMBOO_MARSH_WETLAND_KEY = registerKey("bamboo_marsh_wetland");
    public static final ResourceKey<Feature> WETLAND_REEDS_KEY = registerKey("wetland_reeds");
    public static final ResourceKey<Feature> WILLOW_SHALLOWS_KEY = registerKey("willow_shallows");
    public static final ResourceKey<Feature> RAINFOREST_CREEKS_KEY = registerKey("rainforest_creeks");
    public static final ResourceKey<Feature> MOUNTAIN_RANGE_KEY = registerKey("mountain_range");
    public static final ResourceKey<Feature> ELSEWHERE_FLORA_KEY = registerKey("elsewhere_flora");
    public static final ResourceKey<Feature> LAVA_CLEANUP_KEY = registerKey("lava_cleanup");
    public static final ResourceKey<Feature> HINOKI_KEY = registerKey("hinoki");
    public static final ResourceKey<Feature> HINOKI_SMALL_KEY = registerKey("hinoki_small");
    public static final ResourceKey<Feature> MAPLE_KEY = registerKey("maple");
    public static final ResourceKey<Feature> MAPLE_LAKE_KEY = registerKey("maple_lake");
    public static final ResourceKey<Feature> MAPLE_BAMBOO_KEY = registerKey("maple_bamboo");
    public static final ResourceKey<Feature> HASKAP_BUSH_PATCH_KEY = registerKey("haskap_bush_patch");
    public static final ResourceKey<Feature> BARBERRY_BUSH_PATCH_KEY = registerKey("barberry_bush_patch");
    public static final ResourceKey<Feature> HUGE_GLOWSHROOM_KEY = registerKey("huge_glowshroom");
    public static final ResourceKey<Feature> GLOWSHROOM_PATCH_KEY = registerKey("glowshroom_patch");
    public static final ResourceKey<Feature> GLOWSHROOM_TREE_BASE_KEY = registerKey("glowshroom_tree_base");
    public static final ResourceKey<Feature> MAPLE_WATER_SHORE_KEY = registerKey("maple_water_shore");
    public static final ResourceKey<Feature> MAPLE_RIVERBED_MUD_KEY = registerKey("maple_riverbed_mud");
    public static final ResourceKey<Feature> HIGHLAND_MEADOW_PATCH_KEY = registerKey("highland_meadow_patch");
    public static final ResourceKey<Feature> KARST_OUTCROP_KEY = registerKey("karst_outcrop");
    public static final ResourceKey<Feature> KARST_GROUND_PATCH_KEY = registerKey("karst_ground_patch");
    public static final ResourceKey<Feature> KARST_ANIMAL_TRAIL_KEY = registerKey("karst_animal_trail");


    public static final ResourceKey<Feature> HINOKI_BOULDER_KEY = registerKey("hinoki_boulder");
    public static final ResourceKey<Feature> HINOKI_MOSS_PATCH_KEY = registerKey("hinoki_moss_patch");
    public static final ResourceKey<Feature> VOLCANIC_VENT_PEAK_KEY = registerKey("volcanic_vent_peak");
    public static final ResourceKey<Feature> VOLCANIC_VENT_FOOTHILLS_KEY = registerKey("volcanic_vent_foothills");
    public static final ResourceKey<Feature> ALPINE_SNOW_KEY = registerKey("alpine_snow");
    public static final ResourceKey<Feature> VOLCANIC_MAGMA_PATCH_KEY = registerKey("volcanic_magma_patch");

    public static final ResourceKey<Feature> STONE_FOREST_SPIRES_KEY = registerKey("stone_forest_spires");
    public static final ResourceKey<Feature> AMPHIBOLITE_DEPOSIT_KEY = registerKey("amphibolite_deposit");

    public static final ResourceKey<Feature> WISTERIA_TREE_KEY = registerKey("wisteria_tree");
    public static final ResourceKey<Feature> WISTERIA_HABITAT_KEY = registerKey("wisteria_habitat");
    public static final ResourceKey<Feature> WILLOW_TREE_KEY = registerKey("willow_tree");
    public static final ResourceKey<Feature> CEDAR_TREE_KEY = registerKey("cedar_tree");
    public static final ResourceKey<Feature> PERSIMMON_TREE_KEY = registerKey("persimmon_tree");
    public static final ResourceKey<Feature> MIMOSA_TREE_KEY = registerKey("mimosa_tree");
    public static final ResourceKey<Feature> WILLOW_HABITAT_KEY = registerKey("willow_habitat");
    public static final ResourceKey<Feature> PERSIMMON_HABITAT_KEY = registerKey("persimmon_habitat");
    public static final ResourceKey<Feature> MIMOSA_HABITAT_KEY = registerKey("mimosa_habitat");

    public static void bootstrap(BootstrapContext<Feature> context) {
        // Standard TreeFeature runs the existing Maple trunk/foliage placers.
        context.register(WISTERIA_TREE_KEY, new TreeFeature.Builder(
                BlockStateProvider.of(ModBlocks.WISTERIA_LOG),
                new MapleTrunkPlacer(7, 5, 4,
                        UniformInt.of(2, 3), UniformInt.of(2, 4),
                        UniformInt.of(-4, -2), UniformInt.of(-2, 1)),
                BlockStateProvider.of(ModBlocks.WISTERIA_LEAVES),
                new MapleFoliagePlacer(ConstantInt.of(1), ConstantInt.of(1), 3),
                new TwoLayersFeatureSize(1, 0, 2),
                BlockStateProvider.holderOf(Blocks.DIRT)
        ).decorators(java.util.List.of(new net.chickalo.bugsbugsandmorebugs.worldgen.tree.WisteriaBlossomDecorator())).build());
        context.register(WISTERIA_HABITAT_KEY, new ForestHabitatFeature(0));
        // Grounded in vanilla 26.3 tree configs: Azalea, Cherry, and their
        // vanilla placers. These are TreeFeatures; no custom branch drawing.
        //
        // Willow: a TALL Cherry-style branched trunk carrying wide cherry-style
        // foliage, with our existing hanging-leaf/root decorator.
        // The base 13 + up to 4 + up to 3 logs is intentionally taller than the
        // old Fancy Oak's base 7 + 5 + 2, while the canopy stays full and loose.
        context.register(WILLOW_TREE_KEY, new TreeFeature.Builder(
                BlockStateProvider.of(ModBlocks.WILLOW_LOG),
                new CherryTrunkPlacer(
                        13, 4, 3,
                        UniformInt.of(2, 3), UniformInt.of(3, 5),
                        UniformInt.of(-5, -4), UniformInt.of(-2, 0)),
                BlockStateProvider.of(ModBlocks.WILLOW_LEAVES),
                new CherryFoliagePlacer(
                        ConstantInt.of(4), ConstantInt.of(0), ConstantInt.of(5),
                        0.12F, 0.15F, 0.24F, 0.35F),
                new TwoLayersFeatureSize(1, 0, 2),
                BlockStateProvider.holderOf(Blocks.DIRT)
        ).decorators(java.util.List.of(
                new net.chickalo.bugsbugsandmorebugs.worldgen.tree.WillowDecorator()
        )).build());
        context.register(WILLOW_HABITAT_KEY, new ForestHabitatFeature(1));
        context.register(CEDAR_TREE_KEY, new ForestHabitatFeature(2));

        // Persimmon: natural small orchard tree. Vanilla Azalea's subtly
        // bending trunk and its irregularly scattered foliage remove the
        // exposed, symmetrical branch spokes and artificial spherical lobes.
        context.register(PERSIMMON_TREE_KEY, new TreeFeature.Builder(
                BlockStateProvider.of(ModBlocks.PERSIMMON_LOG),
                new BendingTrunkPlacer(6, 2, 2, 3, UniformInt.of(1, 2)),
                BlockStateProvider.of(ModBlocks.PERSIMMON_LEAVES),
                new RandomSpreadFoliagePlacer(
                        ConstantInt.of(3), ConstantInt.of(0),
                        ConstantInt.of(3), 85),
                new TwoLayersFeatureSize(1, 0, 2),
                BlockStateProvider.holderOf(Blocks.DIRT)
        ).build());
        context.register(PERSIMMON_HABITAT_KEY, new ForestHabitatFeature(3));

        // Mimosa: vanilla Cherry branch placement instead of Acacia's hard,
        // angular two-arm fork. Cherry foliage in broader, thicker layers
        // forms a nearly continuous umbrella, retaining some edge gaps.
        context.register(MIMOSA_TREE_KEY, new TreeFeature.Builder(
                BlockStateProvider.of(ModBlocks.MIMOSA_LOG),
                new CherryTrunkPlacer(
                        7, 2, 1,
                        UniformInt.of(2, 3), UniformInt.of(3, 5),
                        UniformInt.of(-4, -3), UniformInt.of(-1, 0)),
                BlockStateProvider.of(ModBlocks.MIMOSA_LEAVES),
                new CherryFoliagePlacer(
                        ConstantInt.of(5), ConstantInt.of(0),
                        ConstantInt.of(4), 0.10F, 0.12F, 0.04F, 0.06F),
                new TwoLayersFeatureSize(1, 0, 2),
                BlockStateProvider.holderOf(Blocks.DIRT)
        ).decorators(java.util.List.of(
                new net.chickalo.bugsbugsandmorebugs.worldgen.tree.MimosaDecorator()
        )).build());
        context.register(MIMOSA_HABITAT_KEY, new ForestHabitatFeature(4));
        context.register(ELSEWHERE_FLORA_KEY, new ElsewhereFloraFeature());
        context.register(WILLOW_SHALLOWS_KEY, new WillowShallowsFeature());
        context.register(RAINFOREST_CREEKS_KEY, new RainforestCreeksFeature());
        context.register(MOUNTAIN_RANGE_KEY, new MountainRangeFeature());
        context.register(VOLCANIC_MAGMA_PATCH_KEY, new VolcanicMagmaPatchFeature());
        context.register(HINOKI_BOULDER_KEY, new HinokiBoulderFeature());
        context.register(HINOKI_MOSS_PATCH_KEY, new HinokiMossPatchFeature());
        context.register(VOLCANIC_VENT_PEAK_KEY, new VolcanicVentFeature(false));
        context.register(VOLCANIC_VENT_FOOTHILLS_KEY, new VolcanicVentFeature(true));
        context.register(ALPINE_SNOW_KEY, new AlpineSnowFeature());
        context.register(LAVA_CLEANUP_KEY, new ElsewhereLavaCleanupFeature());
        context.register(BAMBOO_POOLS_KEY, new BambooPoolFeature());
        context.register(BAMBOO_MARSH_WETLAND_KEY, new BambooMarshWetlandFeature());
        context.register(WETLAND_REEDS_KEY, new WetlandReedsFeature());
        context.register(STONE_FOREST_SPIRES_KEY, new KarstOutcropFeature(10, 28, 3, 90));
        context.register(AMPHIBOLITE_DEPOSIT_KEY, new AmphiboliteDepositFeature());
        // Small Ginkgo adds an understory height tier without inventing new textures.
        context.register(GINKGO_SMALL_KEY, new TreeFeature.Builder(
                BlockStateProvider.of(ModBlocks.GINKGO_LOG),
                new net.chickalo.bugsbugsandmorebugs.worldgen.tree.GinkgoTrunkPlacer(5, 2, 2, false),
                BlockStateProvider.of(ModBlocks.GINKGO_LEAVES),
                new net.chickalo.bugsbugsandmorebugs.worldgen.tree.GinkgoFoliagePlacer(ConstantInt.of(1), ConstantInt.of(0), 5),
                new TwoLayersFeatureSize(1, 0, 2),
                BlockStateProvider.holderOf(ModBlocks.LOAM)
        ).build());
        context.register(GINKGO_TREE_KEY, new TreeFeature.Builder(
                BlockStateProvider.of(ModBlocks.GINKGO_LOG),
                new net.chickalo.bugsbugsandmorebugs.worldgen.tree.GinkgoTrunkPlacer(12, 3, 3, false),
                BlockStateProvider.of(ModBlocks.GINKGO_LEAVES),
                new net.chickalo.bugsbugsandmorebugs.worldgen.tree.GinkgoFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 5),
                new TwoLayersFeatureSize(1, 0, 2),
                BlockStateProvider.holderOf(ModBlocks.LOAM)
        ).build());
        context.register(GINKGO_OLD_GROWTH_KEY, new TreeFeature.Builder(
                BlockStateProvider.of(ModBlocks.GINKGO_LOG),
                new net.chickalo.bugsbugsandmorebugs.worldgen.tree.GinkgoTrunkPlacer(18, 3, 4, true),
                BlockStateProvider.of(ModBlocks.GINKGO_LEAVES),
                new net.chickalo.bugsbugsandmorebugs.worldgen.tree.GinkgoFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), 7),
                new TwoLayersFeatureSize(1, 0, 2),
                BlockStateProvider.holderOf(ModBlocks.LOAM)
        ).build());
        // Tall, wide-based Hinoki cypress with tiered conical foliage. The trunk placer
        // adds subtle wooden buttress roots and scattered podzol underneath.
        context.register(HINOKI_KEY, new TreeFeature.Builder(
                BlockStateProvider.of(ModBlocks.HINOKI_LOG),
                new net.chickalo.bugsbugsandmorebugs.worldgen.tree.HinokiTrunkPlacer(16, 5, 6),
                BlockStateProvider.of(ModBlocks.HINOKI_LEAVES),
                new net.chickalo.bugsbugsandmorebugs.worldgen.tree.HinokiFoliagePlacer(
                        ConstantInt.of(5), ConstantInt.of(0), 18),
                new TwoLayersFeatureSize(1, 0, 2),
                BlockStateProvider.holderOf(Blocks.DIRT)
        ).build());

        // Smaller, single-trunk Hinoki for edge habitats and understory.
        context.register(HINOKI_SMALL_KEY, new TreeFeature.Builder(
                BlockStateProvider.of(ModBlocks.HINOKI_LOG),
                new StraightTrunkPlacer(9, 3, 3),
                BlockStateProvider.of(ModBlocks.HINOKI_LEAVES),
                new net.chickalo.bugsbugsandmorebugs.worldgen.tree.HinokiFoliagePlacer(
                        ConstantInt.of(3), ConstantInt.of(0), 10),
                new TwoLayersFeatureSize(1, 0, 2),
                BlockStateProvider.holderOf(Blocks.DIRT)
        ).build());

        context.register(MAPLE_KEY, new TreeFeature.Builder(
                BlockStateProvider.of(ModBlocks.MAPLE_LOG),
                new MapleTrunkPlacer(
                        7, 5, 4,
                        UniformInt.of(2, 3),
                        UniformInt.of(2, 4),
                        UniformInt.of(-4, -2),
                        UniformInt.of(-2, 1)
                ),
                BlockStateProvider.of(ModBlocks.MAPLE_LEAVES),
                new MapleFoliagePlacer(ConstantInt.of(1), ConstantInt.of(1), 3),
                new TwoLayersFeatureSize(1, 0, 2),
                BlockStateProvider.holderOf(Blocks.DIRT)
        ).build());

        context.register(MAPLE_LAKE_KEY, new MapleLakeFeature());
        context.register(MAPLE_BAMBOO_KEY, new ProtectedBambooFeature(0.2F));
        context.register(HASKAP_BUSH_PATCH_KEY, new BerryBushPatchFeature(ModBlocks.HASKAP_BERRY_BUSH));
        context.register(BARBERRY_BUSH_PATCH_KEY, new BerryBushPatchFeature(ModBlocks.BARBERRY_BUSH));
        context.register(GLOWSHROOM_PATCH_KEY, new GlowshroomPatchFeature(ModBlocks.GLOWSHROOM));
        context.register(GLOWSHROOM_TREE_BASE_KEY, new GlowshroomTreeBaseFeature(ModBlocks.GLOWSHROOM, ModBlocks.MAPLE_LOG));
        context.register(HUGE_GLOWSHROOM_KEY, new HugeGlowshroomFeature(ModBlocks.GLOWSHROOM_BLOCK, Blocks.MUSHROOM_STEM));
        context.register(MAPLE_WATER_SHORE_KEY, new MapleWaterShoreFeature());
        context.register(MAPLE_RIVERBED_MUD_KEY, new MapleRiverbedMudFeature());
        context.register(HIGHLAND_MEADOW_PATCH_KEY, new HighlandMeadowPatchFeature());
        context.register(KARST_OUTCROP_KEY, new KarstOutcropFeature(1, 7, 2, 65));
        context.register(KARST_GROUND_PATCH_KEY, new KarstGroundPatchFeature());
        context.register(KARST_ANIMAL_TRAIL_KEY, new KarstAnimalTrailFeature());

    }

    public static ResourceKey<Feature> registerKey(String name) {
        return ResourceKey.create(Registries.FEATURE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name));
    }
}
