package net.chickalo.bugsbugsandmorebugs.worldgen;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.worldgen.tree.MapleFoliagePlacer;
import net.chickalo.bugsbugsandmorebugs.worldgen.tree.MapleTrunkPlacer;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.OffsetPlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

public class ModFeatures {
    // CF => Features with Configuration
    // Tree --> height, what trunks etc, etc...
    // How something looks like    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,

    public static final ResourceKey<Feature> MAPLE_KEY = registerKey("maple");

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
                BlockStateProvider.holderOf(Blocks.DIRT)).build());

    }

    public static ResourceKey<Feature> registerKey(String name) {
        return ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name));
    }

}
