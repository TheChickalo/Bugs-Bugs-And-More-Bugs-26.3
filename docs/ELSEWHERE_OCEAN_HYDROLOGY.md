# Elsewhere ocean-scale and water-biome alignment (26.3)

## Design target

The Elsewhere's major seas should have a **large-biomes / pre-1.7-esque scale**: coherent, explorable ocean expanses, not tiny scattered water patches. Woodland, wetland and microhabitat temperatures and vegetation should retain their local detail.

### Terrain and biomes must share an ocean signal

The biome climate map and physical terrain previously used different assumptions: only scattered ocean climate anchors existed; worldgen could carve waterways in non-river biome regions. Sky archipelagos also required a very selective deep-ocean mask.

- Use vanilla `minecraft:overworld_large_biomes/continents` for **Elsewhere terrain and biome continentalness**, and the matching `erosion`, `final_density`, `preliminary_surface_level`, and `chunk_surface_level` variants where appropriate.
- Keep standard `temperature`, `vegetation`, and `ridges` signals to avoid multiplying all biome patches and to preserve fine-grained inland rivers.
- The dedicated `elsewhere_large_depth` function retains the custom karst elevation adjustment, applied to the large-biomes offset.
- Add broad ocean climate coverage at `continentalness <= -0.26`, rather than only nine isolated ocean climates.
- Widen river biomes to match the existing ridges-zero river corridor. Their hydrology also needs favorable erosion and continentalness; **wetland ponds and small freshwater pools should not be mislabeled as oceans or rivers**.
- Give the four sky island biomes broad climate ranges in a high-altitude depth band, not four tiny exact climate points.
- Relax sky island density and cluster availability while keeping the deep-ocean continentalness gate.

This affects **new world generation** and the biome map for new chunks. Old chunks keep their previous generated terrain and biome sections; use a new world to inspect distribution cleanly. **Do not upgrade an important save before making a backup.**

## How to test in the Elsewhere

1. Build Java 25 / Minecraft 26.3: `./gradlew runDatagen build`. Ensure CI is green.
2. **New test world:** use `/locate biome bugsbugsandmorebugs:ocean` and fly across the region. Verify the water surface reports Elsewhere Ocean (F3); sample varied temperatures and erosion settings.
3. Follow wide waterways inland. Use F3 to confirm the central channels have `bugsbugsandmorebugs:river`, while small pools in marshes remain their appropriate wetland biome.
4. Test the sky biomes from their altitude:
   `/execute positioned ~ 220 ~ run locate biome bugsbugsandmorebugs:cloudcedar_forest`
   Repeat for windcarved_crags, mistfall_gardens and vermilion_isles.
5. Fly directly below and above a new island at about Y150–286. Ocean biomes must remain below, the floating island canopy must be its sky biome, and no giant stone or water columns should bridge the gap.
6. Compare multiple seeds and fly across chunk boundaries to check abrupt sea cliffs, river mouths, fragmented coastlines and excessive sky-island density.
7. Profile chunk generation and inspect biome borders. A passing compiler **does not** establish that ocean size or river shape looks natural.

### Gameplay target (still unverified)

There should be long navigable open seas, with islands big enough to find naturally but not carpeting the sky. River biome selection should match actual long river corridors rather than intermittent patches in the wrong forest/ocean biome.

This is a first-pass ocean-scale **draft**. Terrain heights, shoreline shape and /locate discoverability must be validated in the game before release.
