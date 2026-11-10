package net.chickalo.bugsbugsandmorebugs.worldgen.biome;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

public class ModMaterialRules {
    private static final MaterialRule DIRT = makeStateRule(Blocks.DIRT);
    private static final MaterialRule GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final MaterialRule RHYOLITE = makeStateRule(ModBlocks.RHYOLITE);

    private static final MaterialCondition ON_FLOOR = MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR);
    private static final MaterialCondition UNDER_FLOOR = MaterialRules.stoneDepthCheck(0, true, CaveSurface.FLOOR);
    private static final MaterialCondition DEEP_UNDER_FLOOR = MaterialRules.stoneDepthCheck(0, true, 6, CaveSurface.FLOOR);
    private static final MaterialCondition UNDER_CEILING = MaterialRules.stoneDepthCheck(0, true, CaveSurface.CEILING);

    public static MaterialRule makeModOverworldRules(RegistryAccess registryAccess) {
        return MaterialRules.sequence(
                MaterialRules.ifTrue(MaterialRules.isBiome(registryAccess.lookupOrThrow(Registries.BIOME), ModBiomes.MAPLE_FOREST),
                        MaterialRules.sequence(MaterialRules.ifTrue(UNDER_FLOOR, GRASS_BLOCK), DIRT)),
                MaterialRules.ifTrue(ON_FLOOR, GRASS_BLOCK),
                MaterialRules.ifTrue(
                        MaterialRules.isBiome(registryAccess.lookupOrThrow(Registries.BIOME), ModBiomes.MAPLE_FOREST),
                        MaterialRules.sequence(
                                // Obsidian on the undersides of ceilings
                                MaterialRules.ifTrue(UNDER_CEILING, RHYOLITE),
                                // Obsidian on the undersides of floors (though less common in Nether caves)
                                MaterialRules.ifTrue(UNDER_FLOOR, RHYOLITE),
                                MaterialRules.ifTrue(DEEP_UNDER_FLOOR, RHYOLITE),
                                // Default to glowstone if not under a ceiling or floor
                                RHYOLITE))
        );
    }




    private static MaterialRule makeStateRule(Block block) {
        return MaterialRules.state(block.defaultBlockState());
    }
}
