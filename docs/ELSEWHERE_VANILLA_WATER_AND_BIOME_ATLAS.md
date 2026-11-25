# Elsewhere worldgen: Minecraft Large-Biomes hydrology and habitat atlas

## What went wrong
Our old generator used a separate coastal continentalness warp, biome-specific river depth hacks, and a high ocean terrain ceiling while also blending huge independent karst / river / lowland terrain graphs. Physical water and the biome source could disagree. River labels could appear on dry land and Ocean labels on exposed cliffs.

## Reference (Minecraft Java 26.3)
- Reuse Mojang's **built-in** `minecraft:overworld_large_biomes/final_density`, `minecraft:overworld_large_biomes/continents`, and `minecraft:overworld_large_biomes/erosion` together.
- Vanilla-style geographic climate bands:
  - Ocean continentalness **[-1.2,-0.19]**
  - Coast **[-0.19,-0.11]**
  - Near inland **[-0.11,0.03]**
  - Mid inland **[0.03,0.30]**
  - Far inland **[0.30,1]**
- Narrow peaks/valleys climate stripes near **weirdness=0** for rivers, with narrower climate coverage than broad ad hoc rectangles. No extra `depth=0.8` pulse. If a vanilla valley is high/dry, it can remain a land biome.
- Sea level and aquifer surface use vanilla **Y63**, rather than a custom Y60 surface and a separate aquifer Y62 cap.
- These are shared geographic climate principles, **not copied proprietary Minecraft source code**. See https://minecraft.wiki/w/World_generation and https://minecraft.wiki/w/Noise_router.

## Keep the Elsewhere's distinct terrain
- The original extensive inland terrain graph is saved intact at `worldgen/density_function/elsewhere_inland_legacy.json`.
- Physical coastlines, ocean basins, and narrow river valleys always use Vanilla's Large-Biomes final density.
- Further inland, blend back to the previous special terrain (karst highlands, Stone Forests, Sundering relief and volcanic mountain masses), using a smooth mask based on **continentalness above 0.03** and **distance from river-valley weirdness 0**. At far-inland non-river points the legacy terrain remains available.
- Floating islands are added **after** that base blend and retain their individually bounded shapes, drops, altitude variety and rain-free sky biomes.
- The Underhang branch below the Sundering remains unchanged.
- Remove the prior speculative deep ocean cap and separate coast/river biome climate manipulations from the active generator.

## Biome rarity: geography, not entry count
Replace the unordered ordinary biome points with climate cells based on temperature, humidity, ocean-to-inland band, erosion and peaks/valleys. This supports many distinct patches at reasonable frequencies:
- Coast: wetlands, willows, reeds/bamboo marsh, shoreline plains and limestone.
- Near-inland: maples, ginkgos, persimmon, wisteria, cedar and wetlands.
- Mid-inland: balanced temperate forests, plains, bamboo, wisteria and rocky uplands.
- Far inland: karst/Stone Forests, volcanic or snowy peaks in rugged settings, woodland on gentler highland edges.
- Very low erosion enhances rocky/high mountain variants; higher erosion brings gentler meadows.
- Preserve named existing micro-biomes (rare patches), all four sky biomes, and the Underhang.

The checker samples deterministic synthetic climate points to prevent Ocean from winning well inland, River from winning far outside narrow valleys, and Ginkgo from dominating. **This is not a measured percentage of real generated terrain.**

## Essential manual QA
Test **fresh Elsewhere chunks/new world** on at least two seeds. Previously generated terrain and biome labels persist.

1. Follow rivers from headwaters toward sea. F3 biome must mostly match the actual shallow channels, not nearby dry banks. Small dry streambeds may still be possible in the naturally generated valley system.
2. Deep ocean should be broad open water with natural underwater seabeds. Coastlines should transition into shore, beach, wetlands or inland biomes without kilometre-long sand mountains marked Ocean.
3. Visit Ginkgo Forest / Maple / cedar / Willow / Bamboo Marsh / Stone Forest / Volcanic Peaks to confirm meaningful balance and no absurd biome-sized deserts or empty oceans.
4. Fly above deep ocean around Y180–270 to verify separate floating islands, bounded width, diverse heights and sky biome labels.
5. Inspect highland & Stone Forest formations **well inland**. If too much original custom relief disappears, alter only the inland blend mask, not the shared vanilla coast/rivers.
6. Check underwater lakes, actual river water placement, rare microbiomes, sky and Underhang biome selection, and no chunk-border discontinuities.

Do not merge based solely on a passing Gradle build. Procedural correctness must be inspected visually.
