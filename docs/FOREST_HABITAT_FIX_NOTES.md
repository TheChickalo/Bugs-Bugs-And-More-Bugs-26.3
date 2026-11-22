# Forest Habitat Fixes — November 22, 2026

This branch fixes the five newly implemented forest biome families without changing their registrations or overwriting existing 16×16 artwork.

## Fixes
- **Persimmon:** replaces hard four-direction branch spokes and bulbous spheres with varied face-connected diagonal limbs and irregular rounded foliage lobes.
- **Wisteria:** uses the already-existing extended leaf-distance system, avoiding ordinary vanilla 7-block leaf decay on wide maple-style crowns.
- **Willow:** replaces dense dome foliage with more widely spaced radial branch fronds, air pockets, and hanging leaf curtains.
- **Water Mint:** switches from a land-based `VariableHeightPlantBlock` to a two-section aquatic `WaterMintBlock`, using a waterlogged lower root and a dry upper part. The loot rule now targets `PART=0`. Its existing lower and upper texture files are reused, with updated blockstate keys.
- **Mimosa:** grows more organic, asymmetrical, face-connected branches, small flat leaf pads, flowering leaf variants, and occasional hanging blossom clusters.
- **Cedar:** generates surface root networks, some mossy cedar bark, bracket fungus on trunks, hanging moss, and more diverse understory.
- **Roots:** replaces tall skeletal cage models for Willow and Cedar with broad, connected woody root models and low collision/selection shapes. The root models now use opaque existing log bark and top textures so the transparent Willow Roots sprite cannot recreate a lattice.
- **Biome vegetation:** the `ForestHabitatFeature` now scatters understory even after a successful tree. The scanner descends below tree canopies to ground or shallow water and uses the three existing microbiome motifs to pick region-specific vegetation.
- **Clubmoss:** adds `clubmoss_sprig`, an upright cross-model block that can only survive on top of `clubmoss` low-lying carpet. Some clubmoss patches generate these naturally.

## Asset requirements
The default GitHub branch currently lacks custom PNGs for most of the prior block-registration pass. These were **not fabricated** in this code patch. Before testing the new flora in a clean checkout, supply the corresponding 16×16 textures, for example:

- `textures/block/mossy_cedar_log.png`
- `textures/block/hanging_forest_moss.png` and `hanging_forest_moss_tip.png`
- `textures/block/liverwort.png`, `clubmoss.png`, **`clubmoss_sprig.png`**
- `textures/block/bracket_fungus_top.png` and `bracket_fungus_side.png`
- `textures/block/fallen_autumn_leaves.png`, `anemone_flowers.png`, `bush.png`
- `textures/block/flowering_mimosa_leaves.png`, `mimosa_blossoms.png`, `fallen_mimosa_petals.png`, `feathergrass.png`, `pink_clover.png`
- `textures/item/persimmon_fruit.png`, `mimosa_seedpods.png`

Root blocks are intentionally mapped to **existing opaque bark textures** for this fix; custom root textures can be revisited later.

See [the full texture filename mapping](FOREST_DETAIL_TEXTURE_MAPPING.md) for prior assets.

## Testing (not yet run)

1. Run `./gradlew runDatagen` to catch registry/model/loot mismatches. If it fails, inspect the first registry-loading cause.
2. Run `./gradlew runClient`. Test **new chunks**, not only old forest areas, because worldgen changes do not automatically regenerate explored chunks.
3. Visit `wisteria_veilwood`, `willow_backwaters`, `ancient_cedar_rainforest`, `persimmon_woodlands`, and `silkflower_savanna`.
4. Wait several Minecraft days near a generated Wisteria tree to check whether leaves decay.
5. Place Water Mint in one-block-deep still water on mud/silt, break top then bottom, and check that it drops once without deleting the water.
6. Check Cedar and Willow Roots from every axis, including their collision/selection shapes.
7. Look for liverwort, clubmoss carpets and sprigs, silvergrass, anemones, Bush, fallen leaves/petals, feathergrass, and pink clover beneath natural trees.
8. Check Cedar logs for shelf fungi and hanging moss, and Mimosa branches for flowering leaves/blossoms.
9. Break Persimmon and Mimosa Leaves repeatedly to verify fruit and seedpod loot.

These changes have been committed to a feature branch. **Gradle compilation, datagen, and in-game verification are still outstanding.**
