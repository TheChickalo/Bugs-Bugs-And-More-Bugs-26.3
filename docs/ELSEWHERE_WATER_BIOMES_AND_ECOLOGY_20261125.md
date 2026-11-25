# Elsewhere correction — waterways, oceans, Ginkgo Forests, sky spacing

## User-observed regressions (2026-11-25)
- River biomes appeared without water/rivers.
- Ginkgo Forest dominated nearby land.
- Individual floating islands now form, but were too widely separated.
- Ocean biomes owned high, dry stretches of land instead of continuous navigable ocean.
- Snails could no longer spawn, be summoned or use their spawn egg.

**Snails are fixed separately in PR #55**, because the missing initializer is unrelated to worldgen.

## Change 1 — River needs a real carved corridor
The dimension previously contained 18 River climate entries at ordinary land depth 0. MultiNoise nearest-target selection could choose them with no physical channel.

All River climate entries now target **depth 0.8**, with an activation pulse on the biome depth function ONLY where `river_corridor_region` (also used by terrain carving) is sufficiently positive. The pulse is vertically limited to approximately **Y43–75**, strongest around **Y58–63**, near the Elsewhere's Y60 sea level. On normal land, target depth remains 0 and the River targets are pushed far away in climate space. The Underhang remains depth 2 and sky islands remain depth -1.8.

This improves the correlation between biome label and actual carved water channel. Still inspect near river mouths, shallow floodplains and narrow upper river sources: the signal does not directly inspect individual water blocks.

## Change 2 — Ocean should contain water
The biome table previously contained four Ocean anchors at continentalness -0.16, which could classify raised coastline as Ocean. Those four entries are now reclassified as Grassy Plains (two warmer variants) and Limestone Scrubland (two cooler variants).

In deeper seas, the generator now **caps the ordinary stone terrain before adding the floating islands**. The cap suppresses emergent terrain for raw continentalness <=-0.25, forming a seabed below Y60; it gradually releases the ceiling toward the shore. The cap is applied only to the base Elsewhere surface density. Sky-island density is unioned **after** the cap, so islands continue hovering above deep oceans. Mainland relief and the lower Sundering/Underhang terrain are preserved.

This is a targeted physical ocean fix, not a comprehensive replacement of all coastline and estuary generation. A small amount of rocky shoreline at the transition is expected and should be a **land biome**.

## Change 3 — Ginkgo Forest should not dominate
Of the eight common Ginkgo Forest climate anchors, four are reassigned to Maple Forest (two) and Persimmon Woodlands (two). Ginkgo Forest retains its other four anchors, and Old-Growth, Sparse and Mixed Autumn Ginkgo variants remain registered.

The intended result is more distinct temperate forest patches, not zero Ginkgo trees.

## Change 4 — Sky archipelagos closer together
- Spatial cell size: **224 -> 192** blocks
- Center jitter: **20 -> 12** blocks
- Maximum radius: unchanged at 72 blocks (hard max diameter **144**)
- Guaranteed minimum gap between adjacent island footprints: **40 -> 24** blocks
- Cell occupancy: **78% -> 84%**

The biome, seed, height, and inverted rocky island geometry are unchanged. Since islands occupy smaller cells more frequently, ocean flyovers should feel more populated without reverting to continuous floating continents.

## QA in Minecraft 26.3
After checking out this branch, generate a **fresh Elsewhere world or new chunks**. Previously generated chunks will retain their terrain and biome data.

1. Fly along visible river channels and inspect F3 at Y55–65. The center should report River; nearby dry land and hills should not.
2. Cross a river with no visible water and compare `River corridor signal` / `River-biome activation` in F3+F6 Chunk Generation. Report X/Y/Z if a dry River biome survives.
3. Travel across deep Ocean. The water surface should remain at Y60 and the ordinary terrain should stay below it. Near shore, emergent grass or limestone cliffs should receive land biome labels rather than Ocean.
4. Fly through Ginkgo/Maple/Persimmon regions on at least two seeds. Ginkgo should no longer monopolize the same temperature/humidity niche.
5. Fly across island groups at Y180–270. Distinct islands should be closer together, separated by visible gaps. Measure maximum diameter <=144 blocks; inspect raised/lowered tops and drooping bottoms.
6. Test from a new world and verify there are no chunk borders, flat ocean floors, abrupt transition walls, slow generation, and no islands over mainland.
7. PR #55 separately: `/summon bugsbugsandmorebugs:snail`, `/give @s bugsbugsandmorebugs:snail_spawn_egg`, Creative Spawn Eggs tab and natural spawns in wet forest biomes.

## Static regression checks
The project CI checks hard island-cell separation, ocean cap structure, the 18 River depth anchors, the disappearance of coastal Ocean anchors, and reduced common Ginkgo Forest anchors. Data generation and Java compilation are also required.

A green CI build **does not** prove beautiful coastlines or correct river/water labels in every world seed. The user should inspect fresh-world chunks before merging this draft.
