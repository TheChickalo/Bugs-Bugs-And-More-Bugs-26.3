# Elsewhere: ocean-biome mismatch and missing floating islands (26.3)

## User-observed failures
- Vast **water-covered expanses** carrying land biome labels.
- Sky biomes appearing over actual land without associated floating terrain.
- No reliably visible solid floating islands even where sky biomes could be located.
- The earlier fix changed the physical island density placement, but left the mask extremely restrictive and biome-depth regions too generous.

## Fix
- Preserve **large-biomes continental terrain** and the normal inland biomes, but apply a **climate-only coastal adjustment** to the biome router's continentalness signal. Over open seas, the shifted negative continentalness makes Ocean anchors win nearest-biome selection. The transformation transitions smoothly near -0.1 and is unchanged above -0.1; it does **not** increase water terrain or turn land into water.
- Keep the coastal adjustment clamped to avoid pushing deep ocean climate values far beyond the Ocean anchors.
- Replace the restrictive triple-product sky mask with `ocean_gate × (large-scale noise + smaller irregular noise)`. It can create actual islands for ordinary positive noise values instead of requiring three separate noise gates to align simultaneously.
- Isolate sky generation to **actual deeper ocean continentalness** (zero for c≥-0.21), so the sky islands cannot spontaneously generate on dry continental ground.
- The sky biome depth now uses a positive **island-core threshold of 0.34** in the SAME mask as the solid terrain. Mask values outside that threshold produce no sky-biome depth override. The band fades out above Y~278 and begins near Y~160, preventing sky biome labels floating arbitrarily high over mainland.
- The existing `max(original_elsewhere_terrain, sky_island_density)` final terrain union is retained. All four sky biomes remain registered.

## In-game validation is still necessary
1. **Back up your existing world.** Generate a new Elsewhere world, since existing chunks retain terrain and biome data.
2. Over deep ocean: inspect the F3 biome label near Y60 (should be `bugsbugsandmorebugs:ocean`) and note coast/river transitions.
3. Near Y185–240: try `/locate biome bugsbugsandmorebugs:cloudcedar_forest`, repeat for Mistfall Gardens, Windcarved Crags, Vermilion Isles. There should be **stone/loam blocks** under sky-biome cores. Compare exact F3 biome at surface.
4. Fly horizontally 1000+ blocks above mid-ocean and several hundred blocks above mainland. Floating islands should be restricted to open ocean; clear sky above land should not be called a sky biome.
5. Repeat on different seeds. Check cave/sea behavior, ocean coverage, chunk seams, tree growth, and rendering performance.
6. If an island is absent at a reported biome, report the **seed**, **XYZ of F3 biome**, and a screenshot. This lets us trace that exact location rather than blindly tuning more thresholds.

## What automated checks do and do not prove
CI simulates the actual mask/density JSON with representative noise inputs, verifies dry inland coordinates stay unmodified, verifies deep ocean masks can create positive density and verifies some open-air gaps remain. It also samples synthetic biome targets. **Those are structural and mock-noise tests**, not actual Minecraft-generated chunks or verification for every world seed.

A further in-game validation is required before merge.
