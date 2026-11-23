# Elsewhere hydrology + floating archipelago correction

## Bugs resolved in this change
- Ocean and River macrobiome rectangles covered nearly all temperature and moisture combinations and outcompeted land biomes in unrelated areas. They are **removed**.
- The eighteen existing river climate anchors return to a strict ridges-zero (`weirdness: 0`) target, rather than the overly wide `[-0.065, 0.065]` ranges. Keep the existing terrain `river_corridor_region` density carve.
- Eighteen additional *deep-ocean-specific* climate anchors cover three temperature bands, three humidity bands and two erosion bands **only near continentalness -0.67**. This improves labels in actual open seas without the wildcard coastal takeover.
- The first sky-island density was hidden underneath inland `min(gradient)` terrain caps and a lowland height ceiling. The 26.3 terrain router now applies `max(existing Elsewhere terrain, sky_island_density)` as the final operation **after** the entire lowlands/river/stone-forest/volcano composition.
- Moderate mask values now create smaller satellite islands. The biome depth signal saturates more quickly with the landmass mask so that sky biomes are assigned to actual island tops, not only strong-mask centers.
- The underlying pre-1.7-inspired `overworld_large_biomes` continental scale is **not changed** by this fix.

## Mandatory tests (in game, not inferred from compilation)
1. Use a **new Elsewhere world**. Do not test in previously generated chunks; they retain old terrain and biome assignments.
2. Find broad Ocean in deep water using F3 and `/locate biome bugsbugsandmorebugs:ocean`. Fly inland and confirm Ocean does **not** label forest ground.
3. Follow a long river channel and compare F3 biome name to the actual water blocks. River biome labels should be limited to channel areas. Small isolated wetland pools retain their wetland biome labels.
4. From about Y200, run `/locate biome bugsbugsandmorebugs:cloudcedar_forest` (repeat other three sky biomes). Confirm **solid stone/loam islands exist** under the sky biomes, with empty air between islands and intact sea below.
5. Fly across chunk borders and multiple seeds. Check island frequency, coastlines, river mouths, and performance. Screenshot suspicious conditions.
6. If there are extensive sky biomes in empty air, or block islands labeled Ocean, the depth-to-island matching still needs calibration; do not treat this as release-ready.

## Static checks
`python3 scripts/check_sky_islands.py` now fails if sky density is added inside a capped terrain sub-branch rather than at the final terrain union. `python3 scripts/check_elsewhere_hydrology.py` rejects wildcard Ocean/River entries and preserves climate value bounds.

Passing datagen and build is insufficient to demonstrate correct procedural shapes; **actual in-game exploration is required**.
