package net.chickalo.bugsbugsandmorebugs.worldgen.biome;

import net.chickalo.bugsbugsandmorebugs.worldgen.ModPlacedFeatures;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;

/**
 * Reusable groups of mod vegetation that can be added to any biome.
 *
 * To give another biome the same naturally clustered Haskap and Barberry
 * bushes, call addBerryBushes(builder) while constructing that biome's
 * BiomeGenerationSettings.
 */
public final class ModBiomeVegetation {
    private ModBiomeVegetation() {
    }

    /**
     * Adds both berry-bush species as part of the biome's vegetal decoration.
     * The configured and placed features are shared, so each biome can use
     * the same generation behavior without registering duplicate features.
     */
    public static void addBerryBushes(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.HASKAP_BUSH_PLACED_KEY
        );
        builder.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.BARBERRY_BUSH_PLACED_KEY
        );
    }
}
