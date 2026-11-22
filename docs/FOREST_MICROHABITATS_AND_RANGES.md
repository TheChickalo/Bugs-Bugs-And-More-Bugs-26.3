# Forest microhabitats, rainforest waterways and mountain chains

## Fifteen microhabitats

The five registered forest biome IDs retain natural transitions. `ForestHabitatFeature.microhabitat(x,z)` selects smoothly varying patches rather than adding fifteen hard-edged Minecraft biome IDs.

| Biome | Microhabitat 0 | Microhabitat 1 | Microhabitat 2 |
| --- | --- | --- | --- |
| Wisteria Veilwood | Violet Curtains: purple flowers and shade | Tangled Arbors: rooty ground and vegetation | Petal Glades: lighter ground, petals and violets |
| Willow Backwaters | Oxbow Banks: silt near water | Sedge Pools: mud, reeds, sedge, mint | Willow Hummocks: mossy ground and raised vegetation |
| Ancient Cedar Rainforest | Elder Stands: mossy bark and clubmoss | Nurse-Log Hollows: fallen logs and bracket fungi | Rainwashed Gullies: muddy ground and meandering creek sections |
| Persimmon Woodlands | Amber Groves: leaf litter and podzol | Golden Meadows: silvergrass and coarse soils | Bramble Terraces: bushes and coarse earth |
| Silkflower Savanna | Blossom Drifts: fallen pink petals | Feathergrass Prairie: airy grass patches | Sunwashed Rises: sparse bushes and coarse soils |

The noise patches are existing `ForestHabitatFeature` motifs now with additional visible soil, log and fungus choices. Worldgen vegetation is checked against the biome and soil; previously generated chunks do not retroactively change.

## Hanging vegetation

Existing 16×16 PNGs `hanging_wisteria_blossoms_{base,middle,tip}.png`, `hanging_willow_leaves_{base,middle,tip}.png`, and `hanging_forest_moss_{base,middle,tip}.png` are used by four blockstate combinations of `base` and `tip`. A single block uses tip, the top of a long strand uses base, the middle uses middle, the last uses tip.

## Rainforest creeks and rainfall

Cedar rainforest receives a shallow, globally continuous, sinuous channel feature at the LAKES decoration step. Existing forest soil is carved 1–2 blocks deep and replaced with stream water and silt, leaving natural terrain to shape drops/cascades. It only touches cedar-biome columns, respects height/slope restrictions and writes in its own chunk.

The client also draws localized falling water/rain particles while the camera is in Ancient Cedar Rainforest. This gives a **constant visual rainy atmosphere** without modifying the server's global rain/thunder state. Vanilla does not offer a full per-biome permanent rain weather flag.

## Willow climate restriction

The 30 Willow Backwaters multi-noise entries are moved to **river and estuary / coastal-transition climate anchors** and remain at least 26 entries for discoverability. Existing rivers and estuaries keep their own entries. The MultiNoiseBiomeSource does not directly inspect neighboring biome labels, so this is a climate-based placement constraint, not a literal guarantee that every Willow block borders a river or ocean. Follow-up world exploration should confirm the climate distribution.

## Recursive mountain ranges

Mountain-range shoulders are a bounded worldgen feature in Highland Karst, Hinoki Highlands, Volcanic Peak, Ashen Foothills and Snowy Peak. Nearby macro seeds produce small connected range clusters. Each accepted peak can produce up to two subsidiary peaks, with decreasing radii/heights and generation chance at each depth. Peak footprints check for geometric room. Calculations are deterministic across chunks and only fill each chunk locally. The feature applies smooth uplift on mountain terrain over Y 110; it **does not replace the dimension noise router**, and therefore cannot form completely new mountain-biome massifs in flat plains.

### QA

1. Run `./gradlew runDatagen` then `./gradlew runClient`; CI must pass.
2. In a new world, inspect all fifteen microhabitats and nurse log fungus variations.
3. Test every hanging plant at 1, 2 and 4 blocks, including breaking/rebuilding its support.
4. Explore cedar rainforest slopes: ensure channels remain narrow and don't flood lower forests, and observe client rain particles.
5. Inspect Willow distribution along ocean/river/estuary boundaries; check river frequency hasn't collapsed.
6. Fly across mountain borders: watch for mountain-block clipping, exposed rock patches and continuity between chunks.
