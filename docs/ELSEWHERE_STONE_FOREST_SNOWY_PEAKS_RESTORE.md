# Elsewhere: Restore Original Stone Forests & Reliable Snowy Peaks

## Diagnoses
- PR #57 replaced most inland/river/shore density with built-in Large Biomes. Later restoration re-enabled some legacy terrain but forced it through several additional blends; pillar geometry was greatly weakened.
- PR #58 added a Stone Forest climate mask that targeted comparatively wet/cold, often strongly negative-erosion ranges, *whereas the original limestone spires were built for cool, comparatively dry, positive-erosion karst climates*. More masks did not fix that disconnect.
- The new climate atlas restricted `snowy_peak` to **five** entries, essentially the single coldest/driest far-inland corner. Even if /locate could eventually find it, mountains were rare and typically underwhelming.

## Specific corrections
1. Preserve the complete Minecraft Large Biomes ocean/river terrain as the foundation. Ocean and coast use the exact same raw continentalness as the biome atlas and terrain; no return to the old climate warping.
2. Restore **the unchanged original `stone_forest_roof` function** directly into final-density terrain: use a `lerp` from the vanilla/legacy blend to that exact historic pillar-and-shallow-valley density under an inland geology mask. Its old noise-driven spires can extend toward Y180–200, while valleys can reach the sea level Y63 and fill naturally with water.
3. Restore **all ten historic Stone Forest climate anchors from before the vanilla hydrology refactor** and add three closely aligned climate bands. Stop assigning the name Stone Forest to nineteen unrelated cold/wet ridgelines; instead those become highland and mossy hinoki forests. This restores the same terrain-biome relationship as the old Stone Forest, plus a few broad but still inland patches.
4. Expand Snowy Peak selection from **five to seventeen** climate entries. Snowy ridges now also occur in cold, less-dry far inland terrain and low-erosion mid-inland highlands. Snowy terrain uses its own gated cold mountain density field instead of relying only on decorations added above ordinary vanilla slopes. It can rise to approximately Y190–240 depending on noise and nearby ranges.
5. Both Stone Forest and Snowy terrain density functions are strictly gated on continentalness **above the coastline**. Both are added **below** the sky-island union. The 144-block floating islands, rivers, oceans, and custom Underhang dimension remain unchanged.
6. Existing snow features, highland ice outcrops, and recursive mountain-range features remain registered. These changes fix the *underlying terrain* so those features have substantial mountains to decorate.

## Validation / known limits
Run `python3 scripts/check_stone_forest_snowy_peaks.py`. It tests the original Stone Forest roof at high/low vertical samples (including underwater valleys), mask isolation from coasts/oceans, cold mountain density, and direct nearest-climate biome selection. CI runs this plus the other terrain regression checks and Java 25 datagen/compilation.

**Mandatory visual testing** in a fresh Elsewhere world, preferably two different seeds:
- `/locate biome bugsbugsandmorebugs:stone_forest`: inspect limestone columns, narrow valleys, wetlands, shallow water and varied elevations; compare against the original Stone Forest.
- `/locate biome bugsbugsandmorebugs:snowy_peak`: check both incidence and actual tall, snow/ice-covered mountains, not just F3 naming.
- Confirm clear ocean water, natural rivers, no inland-to-water cliff discontinuity, plus unmodified floating island clusters.
- If one specific seed is wrong, report coordinates and F3 noise metrics: **Stone Forest restoration region**, **Snowy peak region**, and **Snowy mountain density** are exposed by the debug screen.
- Previous chunks retain old blocks and biome data; use fresh chunks or a new test world.

Static tests cannot establish the actual silhouette or locate frequency in seeded Minecraft worlds. Keep PR draft until inspected.
