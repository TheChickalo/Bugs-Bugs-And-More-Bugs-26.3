# Elsewhere 26.3 — inverted islands, dry sky biomes and coastal repairs

## Scope
User reports that all the tall sand/clay mountains, plus nearby river channels, were reported as Ocean in F3. Floating islands now generate, but their undersides are flat and island precipitation becomes snow. The user wants the islands to resemble **pointed inverted mountains** rather than disks.

## Changes
- Preserve the **existing sky island mask, archipelago positions, upper altitude cap and terrain union**.
- Replace the constant bottom-Y cutoff with a mask-shaped underside:
  - center of an island: bottom rock can descend toward ~Y165-175
  - narrow island margins: bottom rock stays higher (often around ~Y210-225)
  - use independent noise variation so undersides are irregular, not perfect geometric cones
  - island roofs still terminate near Y250, leaving broad ocean water below
- Set `.hasPrecipitation(false).downfall(0.0F)` for all four sky biomes. Normal snow/rain should not occur there. **Existing placed snow blocks in old chunks will not disappear automatically.**
- Constrain the **Ocean** sand/clay/gravel/lapilli surface rule to **Y<=68**, near the Elsewhere's sea level Y60. Above that height the Ocean biome does not automatically coat peaks in seabed blocks. New world chunks only.
- Soften the **coastal-biome continentalness warp**, which previously turned raw continentalness -0.17 into about -0.60 and therefore mislabeled near-shore land as Ocean. Actual continental terrain remains unchanged.
- When the same `river_corridor_region` that carves a river is strong and Y is near sea level, bias biome-facing continentalness toward 0.28 (the established River anchor range). This should improve River labels without introducing broad river rectangles.
- The large-ocean terrain scale, other surface geology, inland biomes, rice, mobs and cave systems are untouched.

## Verification
1. **New test world/new chunks:** screenshot the formerly huge sand/clay cliff and confirm it is no longer painted as an endless Ocean seafloor. Check F3 biome at shoreline and high mountains separately.
2. Locate an actual floating island above the sea, descend below it, and confirm the stone underside slopes lower toward the island's center. There should still be gaps between islands.
3. Change global weather to rain: in the sky biomes there should be neither rain nor snow. Existing snow layers from older chunks are not a reliable test.
4. At a true river channel around sea level, F3 should report River more frequently instead of Ocean. Check water area, surrounding banks, and river mouths.
5. Repeat from several seeds. If Ocean remains on very tall exposed land, compare `Unadjusted continents`, `Biome continents` and the F3 height at the cliff; **no static-only correction can guarantee an exact terrain/biome match**.

The CI checks JSON and compiler/biome-data registration. Minecraft still needs in-game visual checks.
