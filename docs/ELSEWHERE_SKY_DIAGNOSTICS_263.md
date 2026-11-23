# Sky island and shoreline diagnosis — 26.3

## Observed test failure (user screenshots, 2026-11-23)
At X≈275, Z≈−81 in the Elsewhere, F3 reported **Mistfall Gardens near Y108** but **Ocean near Y100**. Both screenshots show a sandy/rocky shoreline, not floating terrain. Note that both screenshots are BELOW the planned sky island range (roughly Y149–286): those images cannot confirm whether islands exist at Y~200.

## Proven low-altitude biome cause
The sky biome targets a climate `depth` range of [-2, -0.9], but the existing baseline Elsewhere depth (vanilla-style height-dependent depth plus offset) can also become negative around elevated land. Earlier versions used a `lerp` in which the normal low-altitude baseline was passed through unchanged, so a sky biome could win the closest multi-noise comparison even when the island mask was zero.

### Fix
Replace the loosely masked blend with an explicit climate-depth gate:
- `sky_island_density >= -0.01`: sky depth is -1.65, selecting the sky biomes near **actual island stone**. The slightly negative threshold permits surface-edge biome continuity.
- Otherwise: clamp the existing baseline depth to [0, 2], keeping ordinary inland/ocean surface biome depth near the normal 0 target and preserving Underhang depth 2 below the Sundering layer.
- Do NOT change the physical island mask, terrain final-density graph, ocean/river climate coverage, or surface material rules in this diagnostic PR. We need to measure the existing real-world terrain before tuning it AGAIN.

## Built-in 26.3 diagnostics
Noise settings now show these additional entries in the **Chunk Generation** debug group:
- **Sky mask**: 0 means no island is allowed in that X/Z column; positive values are island-generation candidates.
- **Sky solid density**: this should be **positive inside stone island blocks**, negative in open air. Compare directly to the actual visible terrain.
- **Elsewhere climate depth**: should be 0 at ordinary land and near -1.65 within a qualifying sky-island mass.
- **Unadjusted continents** and **Biome continents**: compare terrain's actual large-biome continentalness with the coastal-adjusted value sent to the multi-noise biome source. A large mismatch explains some Ocean labels over dry terrain.
- The existing **Vanilla base density** reading is deliberately relabeled to avoid confusing it with the modded final density.

In Minecraft 26.3, press **F3 + F6** to customize the debug screen. Enable the **Chunk Generation** group; if the new function labels are truncated, temporarily disable other debug groups.

## Reproduction and next diagnostic
1. Back up the world. Use a **fresh world**, or at least completely **new chunks**. Older chunks permanently retain the biome data they generated under previous builds.
2. Enter Elsewhere and visit **X275, Z-81** at Y100 and Y108. With the new branch, Mistfall Gardens should NOT appear there as a low-altitude biome. Observe and record the **Unadjusted continents**, **Biome continents**, and Y values. Ocean directly on a beach may be expected near a biome boundary, but widespread ocean labels on dry uplands indicate climate/terrain disagreement.
3. From the same X/Z, move to around **Y205** (Creative flight) and check **Sky mask** and **Sky solid density**. A positive density there predicts actual solid sky terrain; negative density means this is not an island column.
4. Search from Y205 with `/execute positioned ~ 205 ~ run locate biome bugsbugsandmorebugs:mistfall_gardens`. At the location it returns, inspect **Sky solid density** and actual blocks at Y180–260.
5. If `Sky solid density` is positive but no blocks exist in a fresh chunk, the terrain-generation pipeline is not evaluating the density as expected, and we must investigate chunk filler behavior instead of tuning noise. If the density is negative, there is simply no island at that location.
6. Please provide the exact world **seed**, **XYZ**, and new debug-function values if the mismatch persists.

This PR includes simulated function-tree tests for the low-altitude depth leakage. **No in-game chunk-generation test has been performed by this patch.**
