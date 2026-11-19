package net.chickalo.bugsbugsandmorebugs.worldgen.biome;

import net.chickalo.bugsbugsandmorebugs.worldgen.ModPlacedFeatures;
import net.chickalo.bugsbugsandmorebugs.worldgen.feature.GlowshroomPatchFeature;
import net.minecraft.core.HolderGetter;
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
        spawnBuilder.addSpawn(EntityTypes.FOX, 20, UniformInt.of(2, 3));
        spawnBuilder.addSpawn(EntityTypes.RABBIT, 20, UniformInt.of(2, 3));

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(placedFeatureGetter, carverGetter);

// Lakes
        biomeBuilder.addFeature(
                GenerationStep.Decoration.LAKES,
                ModPlacedFeatures.MAPLE_LAKE_PLACED_KEY
        );


        BiomeDefaultFeatures.addDefaultMushrooms(biomeBuilder);

        biomeBuilder.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.MAPLE_WATER_SHORE_PLACED_KEY
        );
        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_RIVER);
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
                .mobSpawnSettings(spawnBuilder.build()).generationSettings(biomeBuilder.build())
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
                .generationSettings(new BiomeGenerationSettings.Builder(features, carvers).build())
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
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES,
                ModPlacedFeatures.AMPHIBOLITE_DEPOSIT_PLACED_KEY);



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
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);

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
                .generationSettings(biomeBuilder.build())

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
    public static Biome stoneForest(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        // Stone towers are created by the large-scale eroded density field,
        // not decorative spires glued onto a flat surface.
        generation.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.KARST_GROUND_PATCH_PLACED_KEY
        );
        generation.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.HIGHLAND_MEADOW_PATCH_PLACED_KEY
        );

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.62F).downfall(0.49F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x4B9A95).grassColorOverride(0x8AAE65).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(generation.build())
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(0x8ACFEF))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0xD5E5DD))
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
                .generationSettings(generation.build())
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
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.3F).downfall(0.65F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x506C72).grassColorOverride(0x6F966C).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(generation.build())
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(0x9AC4D0))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0x9CAFB7))
                .build();
    }

    public static Biome volcanicPeak(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        // Treeless by design. The material rule paints the exposed volcanic rock.
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(-0.4F).downfall(0.08F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x596E7F).grassColorOverride(0x70746D).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(generation.build())
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
                .generationSettings(generation.build())
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
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                AquaticPlacements.SEAGRASS_RIVER);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        return landscapeBiome(generation,
                0.56F, 0.50F, 0x558A81, 0x9BC2CC, 0xA7CBCE);
    }
    public static Biome river(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.WETLAND_REEDS_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                AquaticPlacements.SEAGRASS_RIVER);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        return landscapeBiome(generation,
                0.66F, 0.70F, 0x689F8C, 0xA1CDD8, 0xC8D7D0);
    }
    public static Biome lake(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.WETLAND_REEDS_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                AquaticPlacements.SEAGRASS_RIVER);
        return landscapeBiome(generation,
                0.61F, 0.76F, 0x598A79, 0xA0D1DA, 0xC4D4CF);
    }
    public static Biome estuary(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.WETLAND_REEDS_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                AquaticPlacements.SEAGRASS_RIVER);
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

    private static Biome landscapeBiome(BiomeGenerationSettings.Builder generation,
                                        float temperature, float downfall,
                                        int water, int sky, int fog) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(temperature).downfall(downfall)
                .specialEffects(new BiomeSpecialEffects.Builder().waterColor(water).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(generation.build())
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(sky))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(fog))
                .build();
    }

}
