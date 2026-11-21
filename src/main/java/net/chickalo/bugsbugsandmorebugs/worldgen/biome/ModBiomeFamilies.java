package net.chickalo.bugsbugsandmorebugs.worldgen.biome;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

/**
 * Shared Elsewhere habitat tests, so custom worldgen features work for
 * derivative biomes instead of being silently limited to their original IDs.
 * Keep all new biome-family membership decisions in one place.
 */
public final class ModBiomeFamilies {
    private ModBiomeFamilies() {}

    public static boolean ginkgo(Holder<Biome> biome) {
        return biome.is(ModBiomes.GINKGO_FOREST)
                || biome.is(ModBiomes.OLD_GROWTH_GINKGO_FOREST)
                || biome.is(ModBiomes.SPARSE_GINKGO_WOODLAND)
                || biome.is(ModBiomes.MIXED_AUTUMN_FOREST);
    }

    public static boolean bamboo(Holder<Biome> biome) {
        return biome.is(ModBiomes.BAMBOO_LOWLANDS)
                || biome.is(ModBiomes.BAMBOO_MARSH);
    }

    public static boolean volcanic(Holder<Biome> biome) {
        return biome.is(ModBiomes.VOLCANIC_PEAK)
                || biome.is(ModBiomes.ASHEN_FOOTHILLS);
    }

    public static boolean limestone(Holder<Biome> biome) {
        return biome.is(ModBiomes.STONE_FOREST)
                || biome.is(ModBiomes.MISTY_KARST_FEN)
                || biome.is(ModBiomes.LIMESTONE_SCRUBLAND);
    }

    public static boolean wetland(Holder<Biome> biome) {
        return biome.is(ModBiomes.RIVER)
                || biome.is(ModBiomes.ESTUARY)
                || biome.is(ModBiomes.BAMBOO_MARSH)
                || biome.is(ModBiomes.MISTY_KARST_FEN);
    }

    public static boolean wetlandReeds(Holder<Biome> biome) {
        return wetland(biome) || biome.is(ModBiomes.BAMBOO_LOWLANDS)
                || biome.is(ModBiomes.STONE_FOREST);
    }

    public static boolean karstGround(Holder<Biome> biome) {
        return biome.is(ModBiomes.HIGHLAND_KARST)
                || biome.is(ModBiomes.STONE_FOREST)
                || biome.is(ModBiomes.HIGHLAND_WILDFLOWER_MEADOW)
                || biome.is(ModBiomes.LIMESTONE_SCRUBLAND)
                || biome.is(ModBiomes.MISTY_KARST_FEN);
    }

    public static boolean karstMeadow(Holder<Biome> biome) {
        return karstGround(biome);
    }

    public static boolean rockOutcrop(Holder<Biome> biome) {
        return biome.is(ModBiomes.HIGHLAND_KARST)
                || biome.is(ModBiomes.STONE_FOREST)
                || biome.is(ModBiomes.LIMESTONE_SCRUBLAND);
    }

    public static boolean karstWetBank(Holder<Biome> biome) {
        return biome.is(ModBiomes.STONE_FOREST)
                || biome.is(ModBiomes.MISTY_KARST_FEN);
    }

    public static boolean animalTrail(Holder<Biome> biome) {
        return biome.is(ModBiomes.HIGHLAND_KARST)
                || biome.is(ModBiomes.BAMBOO_LOWLANDS)
                || biome.is(ModBiomes.BAMBOO_MARSH)
                || biome.is(ModBiomes.LIMESTONE_SCRUBLAND);
    }
}
