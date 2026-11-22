# Elsewhere — limited biome-only playtest (pre-alpha)

This is a **world-generation and atmosphere preview**, not a survival-content release or beta. Testers should judge terrain, biome appearance, flora, weather visuals, and exploration. The custom mob models/animations, progression, structures, and advanced item mechanics are deliberately incomplete.

## Gameplay boundaries agreed for this preview

- **Natural mob spawns:** no biome-natural vanilla mobs or custom bug mobs anywhere in Elsewhere. Maple Forest and Highland Karst's prior `commonSpawns` / rabbits exceptions have been removed. Vanilla global or structure-driven spawning (e.g. certain special spawns or spawner blocks) can still occur and must be verified in game; an empty biome spawn list is not a universal no-entities guarantee.
- **Ant:** prototype only; no natural spawning or completed AI expected.
- **Infested dirt:** deliberately drops nothing.
- **Huge glowshroom cap (`glowshroom_block`):** deliberately lacks a drop until the future hoe-harvesting behavior. Ordinary glowshrooms and potted glowshrooms behave differently; the latter now returns its flower pot **and** mushroom.
- **Tall plants/wetlands:** preserve held tool when harvesting from any segment, and select wetland height before clearance checks.
- **Rainforest weather:** Cedar's local rain is presently primarily client-side weather rendering. Test rain visuals across biome borders; don't interpret it as complete server-side rain simulation.
- **Legacy lake features:** `maple_lake_placed` and `lake_pond_placed` are still registered but intentionally unused. They are preserved as possible future generators, **not** reactivated until per-cell biome boundaries and terrain sealing can be validated.
- **Translations:** incomplete Spanish and future French/Japanese localization are intentionally deferred until before beta; English is the preview language.
- **Rice PR #26:** separate, open and outside this branch; it should be merged and tested independently if wanted in the preview.

## Focused tester itinerary

1. **Fresh world, new chunks:** record the Minecraft/mod version and seed; do not judge old generated terrain or update a valuable save without backups.
2. **Explore the main surface families:** Maple, Ginkgo, Hinoki, Stone Forest, Highland Karst, Cedar Rainforest, Willow Backwaters, Bamboo Marsh/Lowlands, Wisteria, Persimmon, Mimosa, volcanic and alpine regions. Record coordinates and screenshots.
3. **Look for terrain failures:** arbitrary flat Y plateaus, square or raised lakes, sudden water towers or aquifer holes, over-carved gullies, ugly chunk seams, inaccessible/bare volcanic peaks, and misplaced strata.
4. **Evaluate microbiomes:** each should be recognizably different in substrate, relief, vegetation and water—not just a color or spawn-rate change. Report rare microbiomes that cannot be found with the normal exploration/locate tools.
5. **Try plants by hand:** plant water mint and marsh marigold with one or two air blocks above water; try all segments of silver grass with shears; inspect roots/leaves, potted glowshroom drops, and leaf decay.
6. **Cross borders:** verify the Maple-specific grass/shrubs stay within the intended biome; compare local Cedar rain with clear weather just outside.
7. **Performance:** travel rapidly through newly generated areas while watching spikes, stutters and crashes. Note whether they correlate with forest density, wetlands or mountain features. Repeat in multiplayer if possible.
8. **Feedback:** send seed, XYZ, biome ID, screenshot(s), what should have happened, what happened, and whether the problem reproduces in a fresh world.

## Release gate

Do **not** distribute a public/beta-labelled build until the GitHub Actions `runDatagen` and `build` checks pass and at least one person has launched the generated mod successfully. A private pre-alpha for a handful of informed testers is reasonable even if aesthetics, spawn balance and custom gameplay are unfinished. Treat registry loading errors, broken terrain or water generation, crashing, and potentially save-corrupting changes as **blockers**; cosmetic density and translation gaps can be documented as known limitations.

Broader vegetation and climate-parameter rebalancing should follow profiling and tester feedback rather than changing dozens of densities blindly. Keep the current terrain baseline for meaningful before/after comparisons.
