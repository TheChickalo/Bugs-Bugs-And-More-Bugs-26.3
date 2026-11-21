package net.chickalo.bugsbugsandmorebugs.worldgen.biome;

import net.chickalo.bugsbugsandmorebugs.worldgen.ModPlacedFeatures;
import net.chickalo.bugsbugsandmorebugs.worldgen.feature.GlowshroomPatchFeature;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.placement.AquaticPlacements;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.attribute.*;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import java.util.Optional;

public class ModOverworldBiomes {
    public static Biome mapleForest(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<WorldCarver> carverGetter) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.commonSpawns(spawnBuilder, 30);

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(placedFeatureGetter, carverGetter);

// Large natural lakes come from density noise, rather than scattered chunk-sized ponds.


        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);

        biomeBuilder.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.MAPLE_WATER_SHORE_PLACED_KEY
        );
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.MAPLE_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.MAPLE_BAMBOO_PLACED_KEY);
        biomeBuilder.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.GLOWSHROOM_PATCH_PLACED_KEY
        );
        biomeBuilder.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.GLOWSHROOM_TREE_BASE_PLACED_KEY
        );
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.MAPLE_BUSH_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.MAPLE_SHORT_GRASS_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.MAPLE_TALL_GRASS_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.MAPLE_LARGE_FERN_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.MAPLE_RED_SHRUB_PLACED_KEY);
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.HUGE_GLOWSHROOM_PLACED_KEY);

        ModBiomeVegetation.addBerryBushes(biomeBuilder);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.7F).downfall(0.8F)
                .specialEffects(new BiomeSpecialEffects.Builder().waterColor(0x4B8F70).build())
                .mobSpawnSettings(spawnBuilder.build()).generationSettings(withLavaCleanup(biomeBuilder).build())
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(0x83D6DE))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0xC6D8C0))
                .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_FOREST))
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(ParticleTypes.CHERRY_LEAVES, 0.002F))
                .build();
    }


    // Underhang is a void biome: no default caves, dungeons, geodes, ores, or vegetation.
    // Its dark atmosphere is visual only; we will add exclusive bug spawns and vines later.
    public static Biome underhang(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(false).temperature(0.45F).downfall(0.0F)
                .specialEffects(new BiomeSpecialEffects.Builder().waterColor(0x26373F).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(withLavaOnly(new BiomeGenerationSettings.Builder(features, carvers)).build())
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(0x141925))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0x202A36))
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(ParticleTypes.ASH, 0.045F))
                .build();
    }

    public static Biome highlandKarst(
            HolderGetter<PlacedFeature> placedFeatureGetter,
            HolderGetter<WorldCarver> carverGetter) {

        MobSpawnSettings.Builder spawnBuilder =
                new MobSpawnSettings.Builder();

        BiomeDefaultFeatures.commonSpawns(spawnBuilder, 30);

        spawnBuilder.addSpawn(
                EntityTypes.RABBIT,
                12,
                UniformInt.of(2, 3)
        );

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(
                        placedFeatureGetter,
                        carverGetter
                );
        // Amphibolite is now a separate lower stratum; do not inject it into schist.



        biomeBuilder.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.KARST_GROUND_PATCH_PLACED_KEY
        );
        biomeBuilder.addFeature(
                GenerationStep.Decoration.LOCAL_MODIFICATIONS,
                ModPlacedFeatures.KARST_OUTCROP_PLACED_KEY
        );
        biomeBuilder.addFeature(
                GenerationStep.Decoration.LOCAL_MODIFICATIONS,
                ModPlacedFeatures.KARST_ANIMAL_TRAIL_PLACED_KEY
        );

        BiomeDefaultFeatures.addPlainGrass(biomeBuilder);
        BiomeDefaultFeatures.addDefaultFlowers(biomeBuilder);

        biomeBuilder.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.HIGHLAND_MEADOW_PATCH_PLACED_KEY
        );

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.65F)
                .downfall(0.45F)
                .specialEffects(
                        new BiomeSpecialEffects.Builder()
                                .waterColor(0x4B9A95)
                                .grassColorOverride(0x85C45F)
                                .foliageColorOverride(0x78B866)
                                .build()
                )
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(withLavaCleanup(biomeBuilder).build())

                .setAttribute(
                        EnvironmentAttributes.SKY_COLOR,
                        ARGB.vector3fFromRGB24(0x8ACFEF)
                )

                .setAttribute(
                        EnvironmentAttributes.FOG_COLOR,
                        ARGB.vector3fFromRGB24(0xD5E5DD)
                )
                .build();
    }


    // ================================================================
    // ELSEWHERE BIOME PROTOTYPES (26.3)
    // Stone Forest spires ARE real; ginkgo and hinoki temporarily share
    // Maple's tree feature until their own wood, leaves, and saplings exist.
    // No vanilla passive or hostile mobs are added in these four biomes.
    // ================================================================

    public static Biome stoneForest(
            HolderGetter<PlacedFeature> features,
            HolderGetter<WorldCarver> carvers) {

        var generation = new BiomeGenerationSettings.Builder(features, carvers);

        // Karst ground vegetation.
        generation.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.KARST_GROUND_PATCH_PLACED_KEY
        );

        generation.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.HIGHLAND_MEADOW_PATCH_PLACED_KEY
        );

        // Wetland reeds must precede Elsewhere flora.
        generation.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.WETLAND_REEDS_PLACED_KEY
        );

        generation.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY
        );

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.62F)
                .downfall(0.49F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x4B9A95)
                        .grassColorOverride(0x8AAE65)
                        .build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(withLavaCleanup(generation).build())
                .setAttribute(
                        EnvironmentAttributes.SKY_COLOR,
                        ARGB.vector3fFromRGB24(0x8ACFEF))
                .setAttribute(
                        EnvironmentAttributes.FOG_COLOR,
                        ARGB.vector3fFromRGB24(0xD5E5DD))
                .setAttribute(EnvironmentAttributes.CLOUD_HEIGHT, 84.0F)
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES,
                        AmbientParticle.of(ParticleTypes.CLOUD, 0.006F))
                .build();
    }


    public static Biome ginkgoForest(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        // Unique Ginkgo trees. Keep them ordered before any shared vegetation features.
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.GINKGO_TREE_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.GINKGO_OLD_GROWTH_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.GINKGO_SMALL_PLACED_KEY);
        // Far cheaper than the Maple-specific ~744 grass attempts/chunk.
        BiomeDefaultFeatures.addPlainGrass(generation);
        BiomeDefaultFeatures.addDefaultFlowers(generation);
        BiomeDefaultFeatures.addDefaultMushrooms(generation);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.67F).downfall(0.68F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x667F66).grassColorOverride(0xB5AF62).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(withLavaCleanup(generation).build())
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(0xA7D6D6))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0xDAD5BB))
                .build();
    }

    public static Biome hinokiHighlands(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        // Forest below the volcanic treeline: mixed ages of Hinoki.
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.HINOKI_SMALL_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.HINOKI_TREE_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.HINOKI_FERN_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.HINOKI_GRASS_PLACED_KEY);
        BiomeDefaultFeatures.addDefaultMushrooms(generation);
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.3F).downfall(0.65F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x506C72).grassColorOverride(0x6F966C).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(withLavaCleanup(generation).build())
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(0x9AC4D0))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0x9CAFB7))
                .build();
    }

    public static Biome volcanicPeak(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        // Treeless by design. Rare, compact magma outcrops punctuate bare rock.
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        generation.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS,
                ModPlacedFeatures.VOLCANIC_MAGMA_PATCH_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(-0.4F).downfall(0.08F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x596E7F).grassColorOverride(0x70746D).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(withLavaCleanup(generation).build())
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(0xA9C9D9))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0xB8C4CF))
                .build();
    }
    // New Elsewhere landscape biomes. Water presence comes from noise-settings
    // default_fluid/sea_level, not from a biome label alone.
    public static Biome snowyPeak(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        // Highest, open snowfields; separate from volcanic rocky caps.
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(-0.8F).downfall(0.28F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x6F9DB4).grassColorOverride(0xB8C6C5).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(withLavaCleanup(generation).build())
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(0x99B8D2))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0xC8D4DE))
                .build();
    }

    public static Biome grassyPlains(HolderGetter<PlacedFeature> features,
                                    HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        BiomeDefaultFeatures.addPlainGrass(generation);
        BiomeDefaultFeatures.addDefaultFlowers(generation);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        return landscapeBiome(generation, 0.72F, 0.44F, 0x78A85D, 0x84BED2, 0xCBDCD3);
    }

    public static Biome ocean(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        return landscapeBiome(generation,
                0.56F, 0.50F, 0x558A81, 0x9BC2CC, 0xA7CBCE);
    }
    public static Biome river(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.WETLAND_REEDS_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        return landscapeBiome(generation,
                0.66F, 0.70F, 0x689F8C, 0xA1CDD8, 0xC8D7D0);
    }
    public static Biome lake(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        // The large lake basin is cut into base terrain, not assembled from tiny ponds.
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.WETLAND_REEDS_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        return landscapeBiome(generation,
                0.61F, 0.76F, 0x598A79, 0xA0D1DA, 0xC4D4CF);
    }
    public static Biome estuary(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.WETLAND_REEDS_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        return landscapeBiome(generation,
                0.65F, 0.78F, 0x788F7B, 0x9BC9D2, 0xBCC7B5);
    }
    public static Biome bambooLowlands(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        generation.addFeature(GenerationStep.Decoration.LAKES,
                ModPlacedFeatures.BAMBOO_POOLS_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS,
                ModPlacedFeatures.KARST_ANIMAL_TRAIL_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.BAMBOO_GROVE_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.BAMBOO_FERNS_PLACED_KEY);
        BiomeDefaultFeatures.addPlainGrass(generation);
        BiomeDefaultFeatures.addDefaultFlowers(generation);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.WETLAND_REEDS_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        return landscapeBiome(generation, 0.73F, 0.85F,
                0x5B907C, 0x98CECE, 0xB4D7BD);
    }

    // Minor habitat variants. No new blocks or tree models are required.
    // Keep generation densities and climate/appearance distinct from each parent biome.
    public static Biome oldGrowthGinkgoForest(HolderGetter<PlacedFeature> features,
                                               HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        addVegetation(generation,
                ModPlacedFeatures.GINKGO_ANCIENT_STAND_PLACED_KEY,
                ModPlacedFeatures.GINKGO_MIXED_PLACED_KEY,
                ModPlacedFeatures.ELSEWHERE_FLORA_DENSE_PLACED_KEY);
        BiomeDefaultFeatures.addPlainGrass(generation);
        BiomeDefaultFeatures.addDefaultMushrooms(generation);
        return minorBiome(generation, 0.65F, 0.7F,
                0x667F66, 0xB5AF62, 0xD2B76E, 0xA7D6D6, 0xCDC5A3, 0);
    }

    public static Biome sparseGinkgoWoodland(HolderGetter<PlacedFeature> features,
                                              HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        addVegetation(generation,
                ModPlacedFeatures.GINKGO_SCATTERED_PLACED_KEY,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        BiomeDefaultFeatures.addPlainGrass(generation);
        BiomeDefaultFeatures.addDefaultFlowers(generation);
        return minorBiome(generation, 0.75F, 0.42F,
                0x739382, 0xB3BA71, 0xC5AB62, 0xA2D5D3, 0xD5D8B4, 0);
    }

    public static Biome mixedAutumnForest(HolderGetter<PlacedFeature> features,
                                          HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        addVegetation(generation,
                ModPlacedFeatures.MAPLE_SPARSE_PLACED_KEY,
                ModPlacedFeatures.GINKGO_MIXED_PLACED_KEY,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY,
                ModPlacedFeatures.HINOKI_FERN_PLACED_KEY);
        BiomeDefaultFeatures.addPlainGrass(generation);
        BiomeDefaultFeatures.addDefaultMushrooms(generation);
        ModBiomeVegetation.addBerryBushes(generation);
        return minorBiome(generation, 0.69F, 0.74F,
                0x568C79, 0x98B86B, 0xB5AC62, 0x92D5D9, 0xCDD6C1, 0);
    }

    public static Biome mossyHinokiGrove(HolderGetter<PlacedFeature> features,
                                         HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        addVegetation(generation,
                ModPlacedFeatures.HINOKI_TREE_PLACED_KEY,
                ModPlacedFeatures.HINOKI_SMALL_PLACED_KEY,
                ModPlacedFeatures.HINOKI_FERN_PLACED_KEY);
        BiomeDefaultFeatures.addDefaultMushrooms(generation);
        return minorBiome(generation, 0.32F, 0.88F,
                0x526E6B, 0x678D69, 0x61816B, 0xA1C3CB, 0x91ADB0, 116);
    }

    public static Biome bambooMarsh(HolderGetter<PlacedFeature> features,
                                    HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        generation.addFeature(GenerationStep.Decoration.LAKES,
                ModPlacedFeatures.BAMBOO_POOLS_MARSH_PLACED_KEY);
        addVegetation(generation,
                ModPlacedFeatures.BAMBOO_GROVE_PLACED_KEY,
                ModPlacedFeatures.BAMBOO_FERNS_PLACED_KEY,
                ModPlacedFeatures.WETLAND_REEDS_DENSE_PLACED_KEY,
                ModPlacedFeatures.ELSEWHERE_FLORA_DENSE_PLACED_KEY);
        return minorBiome(generation, 0.77F, 0.94F,
                0x4C8270, 0x609B69, 0x5B8A60, 0x9BCECF, 0xB8CFC1, 0);
    }

    public static Biome highlandWildflowerMeadow(HolderGetter<PlacedFeature> features,
                                                   HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        addVegetation(generation,
                ModPlacedFeatures.KARST_GROUND_PATCH_PLACED_KEY,
                ModPlacedFeatures.HIGHLAND_MEADOW_LUSH_PLACED_KEY);
        BiomeDefaultFeatures.addPlainGrass(generation);
        BiomeDefaultFeatures.addDefaultFlowers(generation);
        return minorBiome(generation, 0.63F, 0.52F,
                0x57A0A0, 0x96C86C, 0x7AA967, 0x8ACFEF, 0xD4E2D7, 0);
    }

    public static Biome limestoneScrubland(HolderGetter<PlacedFeature> features,
                                            HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        generation.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS,
                ModPlacedFeatures.KARST_OUTCROP_PLACED_KEY);
        addVegetation(generation,
                ModPlacedFeatures.KARST_GROUND_PATCH_PLACED_KEY,
                ModPlacedFeatures.HIGHLAND_MEADOW_PATCH_PLACED_KEY,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        BiomeDefaultFeatures.addPlainGrass(generation);
        return minorBiome(generation, 0.68F, 0.37F,
                0x709F9B, 0x9AAC72, 0x889D6F, 0x95CFDB, 0xD4DDD3, 0);
    }

    public static Biome glowshroomGrove(HolderGetter<PlacedFeature> features,
                                        HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        addVegetation(generation,
                ModPlacedFeatures.MAPLE_SPARSE_PLACED_KEY,
                ModPlacedFeatures.GLOWSHROOM_DENSE_PLACED_KEY,
                ModPlacedFeatures.GLOWSHROOM_TREE_BASE_PLACED_KEY,
                ModPlacedFeatures.HUGE_GLOWSHROOM_GROVE_PLACED_KEY,
                ModPlacedFeatures.HINOKI_FERN_PLACED_KEY);
        BiomeDefaultFeatures.addDefaultMushrooms(generation);
        return minorBiome(generation, 0.61F, 0.86F,
                0x4A8473, 0x76A172, 0x719B72, 0x8CC7C4, 0x93B5AD, 0);
    }

    public static Biome ashenFoothills(HolderGetter<PlacedFeature> features,
                                       HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        addVegetation(generation, ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        return minorBiome(generation, 0.05F, 0.24F,
                0x627B7F, 0x84857D, 0x81847F, 0xA6C4D0, 0xB9C1C4, 0);
    }

    public static Biome mistyKarstFen(HolderGetter<PlacedFeature> features,
                                      HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        addVegetation(generation,
                ModPlacedFeatures.KARST_GROUND_PATCH_PLACED_KEY,
                ModPlacedFeatures.HIGHLAND_MEADOW_PATCH_PLACED_KEY,
                ModPlacedFeatures.WETLAND_REEDS_DENSE_PLACED_KEY,
                ModPlacedFeatures.ELSEWHERE_FLORA_DENSE_PLACED_KEY);
        return minorBiome(generation, 0.65F, 0.91F,
                0x4F928C, 0x79A56E, 0x71A174, 0x98CCD6, 0xBACDC6, 84);
    }

    @SafeVarargs
    private static void addVegetation(BiomeGenerationSettings.Builder generation,
                                       ResourceKey<PlacedFeature>... features) {
        for (ResourceKey<PlacedFeature> feature : features) {
            generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, feature);
        }
    }

    private static Biome minorBiome(BiomeGenerationSettings.Builder generation,
                                    float temperature, float downfall,
                                    int water, int grass, int foliage, int sky, int fog,
                                    float cloudHeight) {
        var biome = new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(temperature).downfall(downfall)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(water).grassColorOverride(grass)
                        .foliageColorOverride(foliage).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(withLavaCleanup(generation).build())
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(sky))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(fog));
        if (cloudHeight > 0) {
            biome.setAttribute(EnvironmentAttributes.CLOUD_HEIGHT, cloudHeight)
                    .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES,
                            AmbientParticle.of(ParticleTypes.CLOUD, 0.004F));
        }
        return biome.build();
    }

    private static BiomeGenerationSettings.Builder withLavaCleanup(BiomeGenerationSettings.Builder generation) {
        // Small schist-hosted lenses can form underground beneath all surface biomes,
        // but should never be generated inside the exposed Underhang void.
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES,
                ModPlacedFeatures.AMPHIBOLITE_DEPOSIT_PLACED_KEY);
        return withLavaOnly(generation);
    }

    private static BiomeGenerationSettings.Builder withLavaOnly(BiomeGenerationSettings.Builder generation) {
        generation.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION,
                ModPlacedFeatures.LAVA_CLEANUP_PLACED_KEY);
        return generation;
    }

    private static Biome landscapeBiome(BiomeGenerationSettings.Builder generation,
                                        float temperature, float downfall,
                                        int water, int sky, int fog) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(temperature).downfall(downfall)
                .specialEffects(new BiomeSpecialEffects.Builder().waterColor(water).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(withLavaCleanup(generation).build())
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(sky))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(fog))
                .build();
    }

}
