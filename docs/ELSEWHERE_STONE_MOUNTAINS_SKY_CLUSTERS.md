# Elsewhere mountains, Stone Forest restoration and clustered islands

## What broke in the vanilla-water refactor

The new main terrain blend strongly preferred vanilla Large-Biomes density in valleys, which suppressed the original Stone Forest limestone pillars and gorges. Additionally, the old Stone Forest terrain mask targeted colder, dry climates, while the new Stone Forest **biome atlas targets humid temperate highlands**. The original spire generator was still present but rarely enabled under its own biome.

The same mismatch affected volcanic mountain masks: they required cold temperature values despite the updated Volcanic Peak biome appearing in warmer, dry highland climate regions. The mountain-range *feature itself was never removed*, but the diminished underlying mountain relief made its subsidiary peaks harder to notice.

## Changes

- Preserve the previous Stone Forest terrain region and add a second ecology-aligned mask for temperate humid highlands; merge them together. Permit the original Stone Forest pillar/gorge terrain to override the generic vanilla river-valley protection **only inside a sufficiently inland Stone Forest zone**. Oceans and ordinary river valleys keep vanilla density.
- Align volcanic massif/mountain relief with the **warm, low-erosion volcanic biome** in the latest climate atlas rather than its old cold temperature gate.
- Increase root mountain occurrence (1/4 -> 1/3 macro seeds) and primary range-peak height (18–24 -> 24–33 blocks). Raise scan margin to prevent cross-chunk discontinuities; keep the recursive diminishing subsidiary-peak pattern.
- Change Snowy Peak mountain-range rock from **minecraft:stone to the Elsewhere's dacite**; paint exposed snow-peak stone faces as dacite.
- Reduce broad surface chert on mountain-adjacent limestone/highland biomes; retain small chert inclusions. Snow extends to Highland Karsts and Hinoki Highlands as well as Snowy/Volcanic Peaks and Ashen Foothills. Snowy Peak snowline is lowered; additional wind-swept packed ice and occasional blue ice appear at taller ridge tops.
- Floating islands now use deterministic **3×3-cell archipelago clusters**. A cluster starts with its center and then attempts neighbouring sites in a seeded clockwise or reversed sequence. Follow-up probability decreases strictly (96%, 88%, 75%, 60%, 42%, 28%, 14%, 7%). An early failure ends the chain, creating both small and larger groups. Six percent of macro-regions remain empty. The **144-block maximum diameter and 24-block minimum footprint separation remain unchanged**, and ocean gating still applies.

## Visual tests (must use newly generated Elsewhere chunks)

1. Check Stone Forests in distant humid-temperate highlands: tall, separated limestone columns, varying valley heights, vegetation and creeks, without rivers breaking everywhere else.
2. Compare Snowy Peaks, Highland Karsts, Hinoki Highlands and Volcanic Peaks: no vanilla stone ranges or extensive chert carpets, substantial high-altitude snow and blue/packed ice patches above the alpine snowline.
3. Find one primary mountain range and see whether successively smaller satellite peaks create a continuous *range* rather than unrelated tiny towers.
4. Fly over oceans near Y180–270: separate island groups of 2–5 landmasses should be common, with increasingly rare additional satellites. Some sky should remain entirely open; no giant connected sky continents.
5. Inspect at least two seeds and chunk borders; verify oceans and non-Stone-Forest rivers remain navigable. Existing chunks and snow layers will not regenerate.

The checks in CI validate shape constants, registrations, climate-mask structure, and data generation. **Only in-game inspection can verify natural-looking mountain and island silhouettes**.
