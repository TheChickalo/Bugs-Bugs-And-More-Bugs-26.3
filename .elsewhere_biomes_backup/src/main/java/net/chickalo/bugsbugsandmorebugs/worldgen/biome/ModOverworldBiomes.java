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
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES,
                ModPlacedFeatures.AMPHIBOLITE_DEPOSIT_PLACED_KEY);


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

}
