package net.chickalo.bugsbugsandmorebugs.worldgen.biome;

import net.chickalo.bugsbugsandmorebugs.worldgen.ModPlacedFeatures;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;

/**
 * Reusable helpers for adding the mod's vegetation to biome generation.
 *
 * Call these from any biome builder that should generate the corresponding
 * plants. The placed-feature definitions and their density remain centralized
 * in ModPlacedFeatures.
 */
public final class ModBiomeVegetation {
    private ModBiomeVegetation() {
    }

    /**
     * Adds both Haskap and Barberry berry-bush patches to a biome.
     *
     * Example:
     * ModBiomeVegetation.addBerryBushes(biomeBuilder);
     */
    public static void addBerryBushes(BiomeGenerationSettings.Builder biomeBuilder) {
        biomeBuilder.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.HASKAP_BUSH_PLACED_KEY
        );
        biomeBuilder.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.BARBERRY_BUSH_PLACED_KEY
        );
    }

    /**
     * Adds only Haskap patches to a biome.
     */
    public static void addHaskapBushes(BiomeGenerationSettings.Builder biomeBuilder) {
        biomeBuilder.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.HASKAP_BUSH_PLACED_KEY
        );
    }

    /**
     * Adds only Barberry patches to a biome.
     */
    public static void addBarberryBushes(BiomeGenerationSettings.Builder biomeBuilder) {
        biomeBuilder.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.BARBERRY_BUSH_PLACED_KEY
        );
    }
}
