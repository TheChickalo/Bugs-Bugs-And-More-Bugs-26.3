package net.chickalo.bugsbugsandmorebugs.worldgen;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.worldgen.feature.BerryBushPatchFeature;
import net.chickalo.bugsbugsandmorebugs.worldgen.feature.MapleLakeFeature;
import net.chickalo.bugsbugsandmorebugs.worldgen.feature.ProtectedBambooFeature;
import net.chickalo.bugsbugsandmorebugs.worldgen.tree.MapleFoliagePlacer;
import net.chickalo.bugsbugsandmorebugs.worldgen.tree.MapleTrunkPlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;

public class ModFeatures {
    public static final ResourceKey<Feature> MAPLE_KEY = registerKey("maple");
    public static final ResourceKey<Feature> MAPLE_LAKE_KEY = registerKey("maple_lake");
    public static final ResourceKey<Feature> MAPLE_BAMBOO_KEY = registerKey("maple_bamboo");
    public static final ResourceKey<Feature> HASKAP_BUSH_PATCH_KEY = registerKey("haskap_bush_patch");
    public static final ResourceKey<Feature> BARBERRY_BUSH_PATCH_KEY = registerKey("barberry_bush_patch");

    public static void bootstrap(BootstrapContext<Feature> context) {
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
    }

    public static ResourceKey<Feature> registerKey(String name) {
        return ResourceKey.create(Registries.FEATURE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name));
    }
}
