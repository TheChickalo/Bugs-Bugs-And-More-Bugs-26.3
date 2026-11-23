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

    /** Actual biome family registration, including all 15 /locate-able IDs. */
    public static boolean wisteria(Holder<Biome> biome) {
        return biome.is(ModBiomes.MISTFALL_GARDENS)
                || biome.is(ModBiomes.WISTERIA_VEILWOOD)
                || biome.is(ModBiomes.VIOLET_CURTAINS)
                || biome.is(ModBiomes.TANGLED_ARBORS)
                || biome.is(ModBiomes.PETAL_GLADES);
    }

    public static boolean willow(Holder<Biome> biome) {
        return biome.is(ModBiomes.WILLOW_BACKWATERS)
                || biome.is(ModBiomes.OXBOW_BANKS)
                || biome.is(ModBiomes.SEDGE_POOLS)
                || biome.is(ModBiomes.WILLOW_HUMMOCKS);
    }

    public static boolean cedar(Holder<Biome> biome) {
        return biome.is(ModBiomes.CLOUDCEDAR_FOREST)
                || biome.is(ModBiomes.ANCIENT_CEDAR_RAINFOREST)
                || biome.is(ModBiomes.ELDER_STANDS)
                || biome.is(ModBiomes.NURSE_LOG_HOLLOWS)
                || biome.is(ModBiomes.RAINWASHED_GULLIES);
    }

    public static boolean persimmon(Holder<Biome> biome) {
        return biome.is(ModBiomes.PERSIMMON_WOODLANDS)
                || biome.is(ModBiomes.AMBER_GROVES)
                || biome.is(ModBiomes.GOLDEN_MEADOWS)
                || biome.is(ModBiomes.BRAMBLE_TERRACES);
    }

    public static boolean mimosa(Holder<Biome> biome) {
        return biome.is(ModBiomes.SILKFLOWER_SAVANNA)
                || biome.is(ModBiomes.BLOSSOM_DRIFTS)
                || biome.is(ModBiomes.FEATHERGRASS_PRAIRIE)
                || biome.is(ModBiomes.SUNWASHED_RISES);
    }

    public static boolean forest(Holder<Biome> biome,int kind){
        return switch(kind){
            case 0 -> wisteria(biome);
            case 1 -> willow(biome);
            case 2 -> cedar(biome);
            case 3 -> persimmon(biome);
            default -> mimosa(biome);
        };
    }

    /** -1 = parent biome, otherwise the fixed motif for a named micro-biome. */
    public static int forestMicroIndex(Holder<Biome> biome,int kind) {
        return switch(kind) {
            case 0 -> {
                if (biome.is(ModBiomes.VIOLET_CURTAINS)) yield 0;
                if (biome.is(ModBiomes.TANGLED_ARBORS)) yield 1;
                if (biome.is(ModBiomes.PETAL_GLADES)) yield 2;
                yield -1;
            }
            case 1 -> {
                if (biome.is(ModBiomes.OXBOW_BANKS)) yield 0;
                if (biome.is(ModBiomes.SEDGE_POOLS)) yield 1;
                if (biome.is(ModBiomes.WILLOW_HUMMOCKS)) yield 2;
                yield -1;
            }
            case 2 -> {
                if (biome.is(ModBiomes.ELDER_STANDS)) yield 0;
                if (biome.is(ModBiomes.NURSE_LOG_HOLLOWS)) yield 1;
                if (biome.is(ModBiomes.RAINWASHED_GULLIES)) yield 2;
                yield -1;
            }
            case 3 -> {
                if (biome.is(ModBiomes.AMBER_GROVES)) yield 0;
                if (biome.is(ModBiomes.GOLDEN_MEADOWS)) yield 1;
                if (biome.is(ModBiomes.BRAMBLE_TERRACES)) yield 2;
                yield -1;
            }
            case 4 -> {
                if (biome.is(ModBiomes.BLOSSOM_DRIFTS)) yield 0;
                if (biome.is(ModBiomes.FEATHERGRASS_PRAIRIE)) yield 1;
                if (biome.is(ModBiomes.SUNWASHED_RISES)) yield 2;
                yield -1;
            }
            default -> -1;
        };
    }

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
        return willow(biome)
                || biome.is(ModBiomes.RIVER)
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
