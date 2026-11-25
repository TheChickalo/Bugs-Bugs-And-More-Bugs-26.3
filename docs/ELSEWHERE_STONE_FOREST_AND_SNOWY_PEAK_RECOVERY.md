# Restoring the original Stone Forest skyline and making Snowy Peaks locatable

## Diagnosis (after merged #58)
- Stone Forest biome targets use **rugged, low-erosion** climates (roughly -1 to -0.23 in the mountain bands).
- The previous "restoration" mask was multiplied by `clamp((erosion + 0.17) * 6)`. In the core low-erosion part of a Stone Forest, that term was **0**, so the original pillar relief was not active.
- Even where the mask was positive, the original `stone_forest_roof` was nested inside `elsewhere_inland_legacy` and blended with ordinary terrain. A positive mask alone cannot guarantee its pillars/creek valleys become the final surface.
- Snowy Peaks had just **five far-inland climate targets**, concentrated in temperature [-1,-0.45], humidity [-1,-0.45], continentalness [0.3,1]. That made them difficult to locate even though the mountain-range and snow features still existed.

## Actual fixes
1. Introduce `stone_forest_spires_region` explicitly aligned with humid temperate low-erosion, high continentalness. The old `stone_forest_region` remains as a union with the original mask, so other karst behavior stays available.
2. At strong Stone Forest cores **directly use the original `stone_forest_roof` density**, not just an indirect legacy blend. This function creates high limestone pillar tops with noise-carved gaps and low wet creek beds between them. Transitions into ordinary inland terrain are smooth. The core is zero at ocean, dry highlands and extreme snowy conditions; vanilla coastal/river density remains untouched elsewhere.
3. Expand the original Snowy Peak climate targets to `temperature [-1,-0.35]`, `humidity [-1,-0.13]`, `continentalness [0.24,1]`. This includes more cold mountain chains without selecting humid Stone Forest climate.
4. Keep existing alpine snow/ice, mountain-range chains, geology and sky island clustering from #58 unchanged.

## Manual game check
Test on newly generated Elsewhere chunks or a new world (old biome and terrain chunks will never re-create the landscape). In the Elsewhere:

- From the surface, `/locate biome bugsbugsandmorebugs:snowy_peak`, then fly to the reported X/Z and assess actual mountain relief and snow at Y100–220.
- Locate `bugsbugsandmorebugs:stone_forest` and fly over it from Y110–190. Expect **discrete vertical limestone columns, gaps down to wet low valleys**, vegetation and strong silhouettes, not an ordinary rolling plateau.
- Repeat with at least two seeds. Record the seed and XYZ if a Stone Forest biome still lacks pillars, and compare F3+F6 debug fields **Stone Forest restored core** and **Stone Forest pillar density**.
- Check rivers outside Stone Forests and deep oceans to ensure vanilla water generation remains undisturbed.

The CI regression checks evaluate representative mock noise values and climate selection; these are **not actual sampled Minecraft chunk terrain**. Keep the PR a draft pending visual verification.
