package net.chickalo.bugsbugsandmorebugs.datagen;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.item.ModItems;
import net.chickalo.bugsbugsandmorebugs.tags.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(
            HolderLookup.Provider registries,
            BootstrapContext<Recipe<?>> recipes,
            BootstrapContext<Advancement> advancements
    ) {
        return new RecipeProvider(recipes, advancements) {
            @Override
            public void buildRecipes() {
                shaped(RecipeCategory.COMBAT, ModItems.REINFORCED_SHIELD)
                        .pattern("RRR")
                        .pattern("RSR")
                        .pattern(" R ")
                        .define('R', ModItems.EXOSKELETON)
                        .define('S', Items.SHIELD)
                        .unlockedBy(
                                getHasName(ModItems.EXOSKELETON),
                                has(ModItems.EXOSKELETON)
                        )
                        .group("combat")
                        .save(output);

                woodFromLogs(ModBlocks.MAPLE_WOOD, ModBlocks.MAPLE_LOG);
                woodFromLogs(ModBlocks.STRIPPED_MAPLE_WOOD, ModBlocks.STRIPPED_MAPLE_LOG);
                planksFromLogs(ModBlocks.MAPLE_PLANKS, ModTags.Items.MAPLE_LOGS, 4);

            }
        };

    }

    @Override
    public String getName() {
        return "Bugs, Bugs, and More Bugs! recipes";
    }
}