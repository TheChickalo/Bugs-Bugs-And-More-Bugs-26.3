
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
import net.chickalo.bugsbugsandmorebugs.worldgen.tree.GinkgoFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;

public class ModFeatures {
    // Standard, taller-than-maple Ginkgo with an upward-broadening golden crown.
    public static final ResourceKey<Feature> GINKGO_SMALL_KEY = registerKey("ginkgo_small");
    public static final ResourceKey<Feature> GINKGO_TREE_KEY = registerKey("ginkgo_tree");
    // Rare old-growth tree: taller bole and a wider, flatter crown.
    public static final ResourceKey<Feature> GINKGO_OLD_GROWTH_KEY = registerKey("ginkgo_old_growth");


    public static final ResourceKey<Feature> BAMBOO_POOLS_KEY = registerKey("bamboo_pools");
    public static final ResourceKey<Feature> WETLAND_REEDS_KEY = registerKey("wetland_reeds");
    public static final ResourceKey<Feature> ELSEWHERE_FLORA_KEY = registerKey("elsewhere_flora");
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


    public static final ResourceKey<Feature> STONE_FOREST_SPIRES_KEY = registerKey("stone_forest_spires");
    public static final ResourceKey<Feature> AMPHIBOLITE_DEPOSIT_KEY = registerKey("amphibolite_deposit");

    public static void bootstrap(BootstrapContext<Feature> context) {
        context.register(ELSEWHERE_FLORA_KEY, new ElsewhereFloraFeature());
        context.register(BAMBOO_POOLS_KEY, new BambooPoolFeature());
        context.register(WETLAND_REEDS_KEY, new WetlandReedsFeature());
        context.register(STONE_FOREST_SPIRES_KEY, new KarstOutcropFeature(10, 28, 3, 90));
        context.register(AMPHIBOLITE_DEPOSIT_KEY, new AmphiboliteDepositFeature());
        // Small Ginkgo adds an understory height tier without inventing new textures.
        context.register(GINKGO_SMALL_KEY, new TreeFeature.Builder(
                BlockStateProvider.of(ModBlocks.GINKGO_LOG),
                new StraightTrunkPlacer(5, 2, 2),
                BlockStateProvider.of(ModBlocks.GINKGO_LEAVES),
                new GinkgoFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 5),
                new TwoLayersFeatureSize(1, 0, 2),
                BlockStateProvider.holderOf(ModBlocks.LOAM)
        ).build());
        context.register(GINKGO_TREE_KEY, new TreeFeature.Builder(
                BlockStateProvider.of(ModBlocks.GINKGO_LOG),
                new StraightTrunkPlacer(15, 4, 3),
                BlockStateProvider.of(ModBlocks.GINKGO_LEAVES),
                new GinkgoFoliagePlacer(ConstantInt.of(4), ConstantInt.of(0), 7),
                new TwoLayersFeatureSize(1, 0, 2),
                BlockStateProvider.holderOf(ModBlocks.LOAM)
        ).build());
        context.register(GINKGO_OLD_GROWTH_KEY, new TreeFeature.Builder(
                BlockStateProvider.of(ModBlocks.GINKGO_LOG),
                new StraightTrunkPlacer(23, 5, 4),
                BlockStateProvider.of(ModBlocks.GINKGO_LEAVES),
                new GinkgoFoliagePlacer(ConstantInt.of(5), ConstantInt.of(0), 9),
                new TwoLayersFeatureSize(1, 0, 2),
                BlockStateProvider.holderOf(ModBlocks.LOAM)
        ).build());
        // Short, single-trunk Hinoki adds a natural second canopy height.
        context.register(HINOKI_SMALL_KEY, new TreeFeature.Builder(
                BlockStateProvider.of(ModBlocks.HINOKI_LOG),
                new StraightTrunkPlacer(13, 2, 2),
                BlockStateProvider.of(ModBlocks.HINOKI_LEAVES),
                new net.chickalo.bugsbugsandmorebugs.worldgen.tree.HinokiFoliagePlacer(
                        ConstantInt.of(3), ConstantInt.of(0), 10),
                new TwoLayersFeatureSize(1, 0, 2),
                BlockStateProvider.holderOf(Blocks.DIRT)
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
