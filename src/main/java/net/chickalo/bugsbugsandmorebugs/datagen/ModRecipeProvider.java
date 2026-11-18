package net.chickalo.bugsbugsandmorebugs.datagen;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.item.ModItems;
import net.chickalo.bugsbugsandmorebugs.tags.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.crafting.CookingBookCategory;

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
                stairBuilder(ModBlocks.AMPHIBOLITE_STAIRS, Ingredient.of(ModBlocks.AMPHIBOLITE))
                        .unlockedBy(getHasName(ModBlocks.AMPHIBOLITE), has(ModBlocks.AMPHIBOLITE))
                        .group("amphibolite")
                        .save(output);
                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.AMPHIBOLITE_SLAB, ModBlocks.AMPHIBOLITE);
                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.AMPHIBOLITE_WALL, Ingredient.of(ModBlocks.AMPHIBOLITE))
                        .unlockedBy(getHasName(ModBlocks.AMPHIBOLITE), has(ModBlocks.AMPHIBOLITE))
                        .group("amphibolite")
                        .save(output);
                stairBuilder(ModBlocks.POLISHED_AMPHIBOLITE_STAIRS, Ingredient.of(ModBlocks.POLISHED_AMPHIBOLITE))
                        .unlockedBy(getHasName(ModBlocks.POLISHED_AMPHIBOLITE), has(ModBlocks.POLISHED_AMPHIBOLITE))
                        .group("amphibolite")
                        .save(output);
                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_AMPHIBOLITE_SLAB, ModBlocks.POLISHED_AMPHIBOLITE);
                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_AMPHIBOLITE_WALL, Ingredient.of(ModBlocks.POLISHED_AMPHIBOLITE))
                        .unlockedBy(getHasName(ModBlocks.POLISHED_AMPHIBOLITE), has(ModBlocks.POLISHED_AMPHIBOLITE))
                        .group("amphibolite")
                        .save(output);
                stairBuilder(ModBlocks.AMPHIBOLITE_BRICK_STAIRS, Ingredient.of(ModBlocks.AMPHIBOLITE_BRICKS))
                        .unlockedBy(getHasName(ModBlocks.AMPHIBOLITE_BRICKS), has(ModBlocks.AMPHIBOLITE_BRICKS))
                        .group("amphibolite")
                        .save(output);
                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.AMPHIBOLITE_BRICK_SLAB, ModBlocks.AMPHIBOLITE_BRICKS);
                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.AMPHIBOLITE_BRICK_WALL, Ingredient.of(ModBlocks.AMPHIBOLITE_BRICKS))
                        .unlockedBy(getHasName(ModBlocks.AMPHIBOLITE_BRICKS), has(ModBlocks.AMPHIBOLITE_BRICKS))
                        .group("amphibolite")
                        .save(output);
                bricksBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_AMPHIBOLITE, Ingredient.of(ModBlocks.AMPHIBOLITE));
                bricksBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.AMPHIBOLITE_BRICKS, Ingredient.of(ModBlocks.POLISHED_AMPHIBOLITE));
                chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_AMPHIBOLITE, Ingredient.of(ModBlocks.AMPHIBOLITE_SLAB));
                chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_AMPHIBOLITE_BRICKS, Ingredient.of(ModBlocks.AMPHIBOLITE_BRICK_SLAB));
                shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.TEMP_PORTAL)
                        .pattern("RRR")
                        .pattern("RRR")
                        .pattern("RRR")
                        .define('R', Items.DIRT)
                        .unlockedBy(
                                getHasName(Items.DIRT),
                                has(Items.DIRT)
                        )
                        .save(output);


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

                doorBuilder(ModBlocks.MAPLE_DOOR, Ingredient.of(ModBlocks.MAPLE_PLANKS))
                        .unlockedBy(getHasName(ModBlocks.MAPLE_PLANKS), has(ModBlocks.MAPLE_PLANKS))
                        .group("maple")
                        .save(output);

                trapdoorBuilder(ModBlocks.MAPLE_TRAPDOOR, Ingredient.of(ModBlocks.MAPLE_PLANKS))
                        .unlockedBy(getHasName(ModBlocks.MAPLE_PLANKS), has(ModBlocks.MAPLE_PLANKS))
                        .group("maple")
                        .save(output);

                stairBuilder(ModBlocks.MAPLE_STAIRS, Ingredient.of(ModBlocks.MAPLE_PLANKS))
                        .unlockedBy(getHasName(ModBlocks.MAPLE_PLANKS), has(ModBlocks.MAPLE_PLANKS))
                        .group("maple")
                        .save(output);

                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MAPLE_SLAB, ModBlocks.MAPLE_PLANKS);

                buttonBuilder(ModBlocks.MAPLE_BUTTON, Ingredient.of(ModBlocks.MAPLE_PLANKS))
                        .unlockedBy(getHasName(ModBlocks.MAPLE_PLANKS), has(ModBlocks.MAPLE_PLANKS))
                        .group("maple")
                        .save(output);

                pressurePlate(ModBlocks.MAPLE_PRESSURE_PLATE, ModBlocks.MAPLE_PLANKS);

                fenceBuilder(ModBlocks.MAPLE_FENCE, Ingredient.of(ModBlocks.MAPLE_PLANKS))
                        .unlockedBy(getHasName(ModBlocks.MAPLE_PLANKS), has(ModBlocks.MAPLE_PLANKS))
                        .group("maple")
                        .save(output);

                fenceGateBuilder(ModBlocks.MAPLE_FENCE_GATE, Ingredient.of(ModBlocks.MAPLE_PLANKS))
                        .unlockedBy(getHasName(ModBlocks.MAPLE_PLANKS), has(ModBlocks.MAPLE_PLANKS))
                        .group("maple")
                        .save(output);

                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.RHYOLITE_WALL, Ingredient.of(ModBlocks.RHYOLITE))
                        .unlockedBy(getHasName(ModBlocks.RHYOLITE), has(ModBlocks.RHYOLITE))
                        .group("rhyolite")
                        .save(output);

                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_RHYOLITE_WALL, Ingredient.of(ModBlocks.POLISHED_RHYOLITE))
                        .unlockedBy(getHasName(ModBlocks.POLISHED_RHYOLITE), has(ModBlocks.POLISHED_RHYOLITE))
                        .group("rhyolite")
                        .save(output);

                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.RHYOLITE_BRICK_WALL, Ingredient.of(ModBlocks.RHYOLITE_BRICKS))
                        .unlockedBy(getHasName(ModBlocks.RHYOLITE_BRICKS), has(ModBlocks.RHYOLITE_BRICKS))
                        .group("rhyolite")
                        .save(output);

                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLED_RHYOLITE_WALL, Ingredient.of(ModBlocks.COBBLED_RHYOLITE))
                        .unlockedBy(getHasName(ModBlocks.COBBLED_RHYOLITE), has(ModBlocks.COBBLED_RHYOLITE))
                        .group("rhyolite")
                        .save(output);

                stairBuilder(ModBlocks.RHYOLITE_STAIRS, Ingredient.of(ModBlocks.RHYOLITE))
                        .unlockedBy(getHasName(ModBlocks.RHYOLITE), has(ModBlocks.RHYOLITE))
                        .group("rhyolite")
                        .save(output);


                stairBuilder(ModBlocks.POLISHED_RHYOLITE_STAIRS, Ingredient.of(ModBlocks.POLISHED_RHYOLITE))
                        .unlockedBy(getHasName(ModBlocks.POLISHED_RHYOLITE), has(ModBlocks.POLISHED_RHYOLITE))
                        .group("rhyolite")
                        .save(output);

                stairBuilder(ModBlocks.RHYOLITE_BRICK_STAIRS, Ingredient.of(ModBlocks.RHYOLITE_BRICKS))
                        .unlockedBy(getHasName(ModBlocks.RHYOLITE_BRICKS), has(ModBlocks.RHYOLITE_BRICKS))
                        .group("rhyolite")
                        .save(output);

                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.RHYOLITE_SLAB, ModBlocks.RHYOLITE);

                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.RHYOLITE_BRICK_SLAB, ModBlocks.RHYOLITE_BRICKS);

                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_RHYOLITE_SLAB, ModBlocks.POLISHED_RHYOLITE);

                stairBuilder(ModBlocks.COBBLED_RHYOLITE_STAIRS, Ingredient.of(ModBlocks.COBBLED_RHYOLITE))
                        .unlockedBy(getHasName(ModBlocks.COBBLED_RHYOLITE), has(ModBlocks.COBBLED_RHYOLITE))
                        .group("rhyolite")
                        .save(output);

                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLED_RHYOLITE_SLAB, ModBlocks.COBBLED_RHYOLITE);

                chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_RHYOLITE, Ingredient.of(ModBlocks.RHYOLITE));
                bricksBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.RHYOLITE_BRICKS, Ingredient.of(ModBlocks.RHYOLITE));
                bricksBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRACKED_RHYOLITE_BRICKS, Ingredient.of(ModBlocks.RHYOLITE));

                // Schist family
                // 4 blocks in a square create the polished and brick variants.
                bricksBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_SCHIST, Ingredient.of(ModBlocks.SCHIST));
                bricksBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SCHIST_BRICKS, Ingredient.of(ModBlocks.POLISHED_SCHIST));
                chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_SCHIST, Ingredient.of(ModBlocks.SCHIST_BRICKS_SLAB));

                SimpleCookingRecipeBuilder.smelting(
                                Ingredient.of(ModBlocks.COBBLED_SCHIST),
                                RecipeCategory.BUILDING_BLOCKS,
                                CookingBookCategory.BLOCKS,
                                ModBlocks.SCHIST,
                                0.1F,
                                200
                        )
                        .unlockedBy(
                                getHasName(ModBlocks.COBBLED_SCHIST),
                                has(ModBlocks.COBBLED_SCHIST)
                        )
                        .save(output);

                SimpleCookingRecipeBuilder.smelting(
                                Ingredient.of(ModBlocks.SCHIST_BRICKS),
                                RecipeCategory.BUILDING_BLOCKS,
                                CookingBookCategory.BLOCKS,
                                ModBlocks.CRACKED_SCHIST_BRICKS,
                                0.1F,
                                200
                        )
                        .unlockedBy(
                                getHasName(ModBlocks.SCHIST_BRICKS),
                                has(ModBlocks.SCHIST_BRICKS)
                        )
                        .save(output);
                stairBuilder(ModBlocks.SCHIST_STAIRS, Ingredient.of(ModBlocks.SCHIST))
                        .unlockedBy(getHasName(ModBlocks.SCHIST), has(ModBlocks.SCHIST))
                        .group("schist").save(output);
                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SCHIST_SLAB, ModBlocks.SCHIST);
                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SCHIST_WALL, Ingredient.of(ModBlocks.SCHIST))
                        .unlockedBy(getHasName(ModBlocks.SCHIST), has(ModBlocks.SCHIST))
                        .group("schist").save(output);
                stairBuilder(ModBlocks.COBBLED_SCHIST_STAIRS, Ingredient.of(ModBlocks.COBBLED_SCHIST))
                        .unlockedBy(getHasName(ModBlocks.COBBLED_SCHIST), has(ModBlocks.COBBLED_SCHIST))
                        .group("schist").save(output);
                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLED_SCHIST_SLAB, ModBlocks.COBBLED_SCHIST);
                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLED_SCHIST_WALL, Ingredient.of(ModBlocks.COBBLED_SCHIST))
                        .unlockedBy(getHasName(ModBlocks.COBBLED_SCHIST), has(ModBlocks.COBBLED_SCHIST))
                        .group("schist").save(output);
                stairBuilder(ModBlocks.POLISHED_SCHIST_STAIRS, Ingredient.of(ModBlocks.POLISHED_SCHIST))
                        .unlockedBy(getHasName(ModBlocks.POLISHED_SCHIST), has(ModBlocks.POLISHED_SCHIST))
                        .group("schist").save(output);
                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_SCHIST_SLAB, ModBlocks.POLISHED_SCHIST);
                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_SCHIST_WALL, Ingredient.of(ModBlocks.POLISHED_SCHIST))
                        .unlockedBy(getHasName(ModBlocks.POLISHED_SCHIST), has(ModBlocks.POLISHED_SCHIST))
                        .group("schist").save(output);
                stairBuilder(ModBlocks.SCHIST_BRICKS_STAIRS, Ingredient.of(ModBlocks.SCHIST_BRICKS))
                        .unlockedBy(getHasName(ModBlocks.SCHIST_BRICKS), has(ModBlocks.SCHIST_BRICKS))
                        .group("schist").save(output);
                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SCHIST_BRICKS_SLAB, ModBlocks.SCHIST_BRICKS);
                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SCHIST_BRICKS_WALL, Ingredient.of(ModBlocks.SCHIST_BRICKS))
                        .unlockedBy(getHasName(ModBlocks.SCHIST_BRICKS), has(ModBlocks.SCHIST_BRICKS))
                        .group("schist").save(output);

                // Eclogite family
                // 4 blocks in a square create the polished and brick variants.
                shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_ECLOGITE, 4)
                        .pattern("##").pattern("##").define('#', ModBlocks.COBBLED_ECLOGITE)
                        .unlockedBy(getHasName(ModBlocks.COBBLED_ECLOGITE), has(ModBlocks.COBBLED_ECLOGITE)).save(output);
                shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ECLOGITE_BRICKS, 4)
                        .pattern("##").pattern("##").define('#', ModBlocks.POLISHED_ECLOGITE)
                        .unlockedBy(getHasName(ModBlocks.POLISHED_ECLOGITE), has(ModBlocks.POLISHED_ECLOGITE)).save(output);
                shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_ECLOGITE)
                        .pattern("#").pattern("#").define('#', ModBlocks.ECLOGITE_BRICKS_SLAB)
                        .unlockedBy(getHasName(ModBlocks.ECLOGITE_BRICKS_SLAB), has(ModBlocks.ECLOGITE_BRICKS_SLAB)).save(output);
                SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.COBBLED_ECLOGITE), RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.BLOCKS,
                        ModBlocks.ECLOGITE, 0.1F, 200)
                        .unlockedBy(getHasName(ModBlocks.COBBLED_ECLOGITE), has(ModBlocks.COBBLED_ECLOGITE))
                        .save(output);
                SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.ECLOGITE_BRICKS), RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.BLOCKS,
                        ModBlocks.CRACKED_ECLOGITE_BRICKS, 0.1F, 200)
                        .unlockedBy(getHasName(ModBlocks.ECLOGITE_BRICKS), has(ModBlocks.ECLOGITE_BRICKS))
                        .save(output);
                stairBuilder(ModBlocks.ECLOGITE_STAIRS, Ingredient.of(ModBlocks.ECLOGITE))
                        .unlockedBy(getHasName(ModBlocks.ECLOGITE), has(ModBlocks.ECLOGITE))
                        .group("eclogite").save(output);
                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ECLOGITE_SLAB, ModBlocks.ECLOGITE);
                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ECLOGITE_WALL, Ingredient.of(ModBlocks.ECLOGITE))
                        .unlockedBy(getHasName(ModBlocks.ECLOGITE), has(ModBlocks.ECLOGITE))
                        .group("eclogite").save(output);
                stairBuilder(ModBlocks.COBBLED_ECLOGITE_STAIRS, Ingredient.of(ModBlocks.COBBLED_ECLOGITE))
                        .unlockedBy(getHasName(ModBlocks.COBBLED_ECLOGITE), has(ModBlocks.COBBLED_ECLOGITE))
                        .group("eclogite").save(output);
                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLED_ECLOGITE_SLAB, ModBlocks.COBBLED_ECLOGITE);
                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLED_ECLOGITE_WALL, Ingredient.of(ModBlocks.COBBLED_ECLOGITE))
                        .unlockedBy(getHasName(ModBlocks.COBBLED_ECLOGITE), has(ModBlocks.COBBLED_ECLOGITE))
                        .group("eclogite").save(output);
                stairBuilder(ModBlocks.POLISHED_ECLOGITE_STAIRS, Ingredient.of(ModBlocks.POLISHED_ECLOGITE))
                        .unlockedBy(getHasName(ModBlocks.POLISHED_ECLOGITE), has(ModBlocks.POLISHED_ECLOGITE))
                        .group("eclogite").save(output);
                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_ECLOGITE_SLAB, ModBlocks.POLISHED_ECLOGITE);
                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_ECLOGITE_WALL, Ingredient.of(ModBlocks.POLISHED_ECLOGITE))
                        .unlockedBy(getHasName(ModBlocks.POLISHED_ECLOGITE), has(ModBlocks.POLISHED_ECLOGITE))
                        .group("eclogite").save(output);
                stairBuilder(ModBlocks.ECLOGITE_BRICKS_STAIRS, Ingredient.of(ModBlocks.ECLOGITE_BRICKS))
                        .unlockedBy(getHasName(ModBlocks.ECLOGITE_BRICKS), has(ModBlocks.ECLOGITE_BRICKS))
                        .group("eclogite").save(output);
                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ECLOGITE_BRICKS_SLAB, ModBlocks.ECLOGITE_BRICKS);
                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ECLOGITE_BRICKS_WALL, Ingredient.of(ModBlocks.ECLOGITE_BRICKS))
                        .unlockedBy(getHasName(ModBlocks.ECLOGITE_BRICKS), has(ModBlocks.ECLOGITE_BRICKS))
                        .group("eclogite").save(output);

                // lwk gonna need a custom builder for weathered and other limestone types
                bricksBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICKS, Ingredient.of(ModBlocks.LIMESTONE));
                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_SLAB, ModBlocks.LIMESTONE);
                stairBuilder(ModBlocks.LIMESTONE_STAIRS, Ingredient.of(ModBlocks.LIMESTONE))
                        .unlockedBy(getHasName(ModBlocks.LIMESTONE), has(ModBlocks.LIMESTONE))
                        .group("limestone")
                        .save(output);
                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_WALL, Ingredient.of(ModBlocks.LIMESTONE))
                        .unlockedBy(getHasName(ModBlocks.LIMESTONE), has(ModBlocks.LIMESTONE))
                        .group("limestone")
                        .save(output);
                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE_SLAB, ModBlocks.POLISHED_LIMESTONE);
                stairBuilder(ModBlocks.POLISHED_LIMESTONE_STAIRS, Ingredient.of(ModBlocks.POLISHED_LIMESTONE))
                        .unlockedBy(getHasName(ModBlocks.POLISHED_LIMESTONE), has(ModBlocks.POLISHED_LIMESTONE))
                        .group("limestone")
                        .save(output);
                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE_WALL, Ingredient.of(ModBlocks.POLISHED_LIMESTONE))
                        .unlockedBy(getHasName(ModBlocks.POLISHED_LIMESTONE), has(ModBlocks.POLISHED_LIMESTONE))
                        .group("limestone")
                        .save(output);
                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICK_SLAB, ModBlocks.LIMESTONE_BRICKS);
                stairBuilder(ModBlocks.LIMESTONE_BRICK_STAIRS, Ingredient.of(ModBlocks.LIMESTONE_BRICKS))
                        .unlockedBy(getHasName(ModBlocks.LIMESTONE_BRICKS), has(ModBlocks.LIMESTONE_BRICKS))
                        .group("limestone")
                        .save(output);
                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICK_WALL, Ingredient.of(ModBlocks.LIMESTONE_BRICKS))
                        .unlockedBy(getHasName(ModBlocks.LIMESTONE_BRICKS), has(ModBlocks.LIMESTONE_BRICKS))
                        .group("limestone")
                        .save(output);
                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.RUBBLED_LIMESTONE_SLAB, ModBlocks.RUBBLED_LIMESTONE);
                stairBuilder(ModBlocks.RUBBLED_LIMESTONE_STAIRS, Ingredient.of(ModBlocks.RUBBLED_LIMESTONE))
                        .unlockedBy(getHasName(ModBlocks.RUBBLED_LIMESTONE), has(ModBlocks.RUBBLED_LIMESTONE))
                        .group("limestone")
                        .save(output);
                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.RUBBLED_LIMESTONE_WALL, Ingredient.of(ModBlocks.RUBBLED_LIMESTONE))
                        .unlockedBy(getHasName(ModBlocks.RUBBLED_LIMESTONE), has(ModBlocks.RUBBLED_LIMESTONE))
                        .group("limestone")
                        .save(output);
                pillarBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_PILLAR, Ingredient.of(ModBlocks.LIMESTONE));
                shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MOSSY_LIMESTONE)
                        .requires(ModBlocks.LIMESTONE)
                        .requires(Blocks.VINE)
                        .group("limestone")
                        .unlockedBy(getHasName(Blocks.VINE), has(Blocks.VINE))
                        .save(output, getItemName(ModBlocks.MOSSY_LIMESTONE) + "_from_vine");
                shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MOSSY_LIMESTONE)
                        .requires(ModBlocks.LIMESTONE)
                        .requires(Blocks.MOSS_BLOCK)
                        .group("limestone")
                        .unlockedBy(getHasName(Blocks.MOSS_BLOCK), has(Blocks.MOSS_BLOCK))
                        .save(output, getItemName(ModBlocks.MOSSY_LIMESTONE) + "_from_moss_block");

                bricksBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LITHOSOL, Ingredient.of(ModBlocks.RENDZINA));

            }
        };

    }

    @Override
    public String getName() {
        return "Bugs, Bugs, and More Bugs! recipes";
    }
}