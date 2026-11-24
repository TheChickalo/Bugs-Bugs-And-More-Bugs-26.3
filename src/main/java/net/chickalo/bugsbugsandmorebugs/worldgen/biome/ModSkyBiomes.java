package net.chickalo.bugsbugsandmorebugs.worldgen.biome;

import net.chickalo.bugsbugsandmorebugs.worldgen.ModPlacedFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.util.ARGB;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * First-pass sky biomes. Small, safe vegetation palettes reuse existing
 * trees and flora without importing river carving, swamp pools or volcanoes.
 * All natural mob spawn lists remain empty for the biome-only pre-alpha.
 */
public final class ModSkyBiomes {
    private ModSkyBiomes() {}

    public static Biome cloudcedarForest(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.HINOKI_FERN_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.CEDAR_HABITAT_PLACED_KEY);
        BiomeDefaultFeatures.addDefaultMushrooms(generation);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        return sky(generation, 0.34F, 0.90F, 0x517D65, 0x47715D, 0xA7BEC4);
    }

    public static Biome windcarvedCrags(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        BiomeDefaultFeatures.addPlainGrass(generation);
        BiomeDefaultFeatures.addDefaultFlowers(generation);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        return sky(generation, 0.45F, 0.30F, 0x9FB683, 0x829E75, 0xB5C9D7);
    }

    public static Biome mistfallGardens(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.WISTERIA_TREE_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.WISTERIA_HABITAT_PLACED_KEY);
        BiomeDefaultFeatures.addPlainGrass(generation);
        BiomeDefaultFeatures.addDefaultMushrooms(generation);
        return sky(generation, 0.68F, 0.85F, 0x8DB18A, 0x81A582, 0xBCCBDD);
    }

    public static Biome vermilionIsles(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.MAPLE_SPARSE_PLACED_KEY);
        BiomeDefaultFeatures.addPlainGrass(generation);
        BiomeDefaultFeatures.addDefaultFlowers(generation);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.MAPLE_BUSH_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        return sky(generation, 0.62F, 0.62F, 0xAD956D, 0xB8794E, 0xC9B7A9);
    }

    private static Biome sky(BiomeGenerationSettings.Builder generation, float temperature,
                             float downfall, int grass, int foliage, int fog) {
        // Preserve the baseline Elsewhere geological placements and top-layer cleanup.
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.AMPHIBOLITE_DEPOSIT_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, ModPlacedFeatures.LAVA_CLEANUP_PLACED_KEY);
        return new Biome.BiomeBuilder()
                .hasPrecipitation(false).temperature(temperature).downfall(0.0F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x558F99)
                        .grassColorOverride(grass)
                        .foliageColorOverride(foliage).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(generation.build())
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(0x93CFE0))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(fog))
                .build();
    }
}
