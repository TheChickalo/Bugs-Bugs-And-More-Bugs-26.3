package net.chickalo.bugsbugsandmorebugs.worldgen.biome;

import net.chickalo.bugsbugsandmorebugs.worldgen.ModPlacedFeatures;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;

/**
 * Reusable groups of mod vegetation that can be added to any biome.
 *
 * Call one or more of these methods while building a biome's generation
 * settings. The placed features are shared, so biomes do not need duplicate
 * configured-feature or placed-feature registrations.
 */
public final class ModBiomeVegetation {
    private ModBiomeVegetation() {
    }

    /** Adds naturally clustered Haskap bushes to a biome. */
    public static void addHaskapBushes(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.HASKAP_BUSH_PLACED_KEY
        );
    }

    /** Adds naturally clustered Barberry bushes to a biome. */
    public static void addBarberryBushes(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.BARBERRY_BUSH_PLACED_KEY
        );
    }

    /** Adds both berry-bush species to a biome. */
    public static void addBerryBushes(BiomeGenerationSettings.Builder builder) {
        addHaskapBushes(builder);
        addBarberryBushes(builder);
    }
}
