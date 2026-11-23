# Elsewhere floating archipelagos — prototype (26.3)

## Story
An asteroid brought a strange gravity-affecting mineral into the Elsewhere. Mineralized rock eventually separated from the surface, rising over deep ocean. After the catastrophe, surviving civilizations attempted to reclaim the islands, building scattered outposts before abandoning them. **Aetherite** is a working lore name, not a registered ore or harvestable block in this PR.

## Implementation
- `sky_island_mask`: ocean continentalness gate + low-frequency cluster mask + two intersecting irregular surface-noise masks. All inputs are seeded noise and evaluated continuously across chunks.
- `sky_island_density`: irregular stone mass, with lower and upper Y clamps (roughly 150–286).
- `noise_router.final_density`: max(existing Elsewhere terrain, sky density); does not replace seabeds, rivers or cave strata.
- `noise_router.depth`: y-aware sky biome climate signal follows the same island mask, returning to baseline outside the sky band. The actual ocean biomes should remain below.
- Four registered sky biomes: Cloudcedar Forest, Windcarved Crags, Mistfall Gardens, Vermilion Isles.
- Existing tree and plant features reused, **without** cutting rainforest creeks/swamp pools into these first-pass sky islands.
- Sky-specific material rules create loam/grass caps with rhyolite exposed in Windcarved Crags.
- No registered mineral block, flying-island structures, custom mobs, or additional ore recipes yet.

## In-game release gate (NOT verified)
1. Run `./gradlew runDatagen build` with Java 25 and Minecraft 26.3; do not merge if registries fail.
2. Create a **fresh test world**, enter Elsewhere and use `/locate biome bugsbugsandmorebugs:cloudcedar_forest` (also test the three other new biome IDs). The locate command may select a point in the air; inspect Y 150–286.
3. Fly over the sky biomes and check: isolated islands instead of a continuous sheet; stone underbellies; grass/trees only on actual blocks; no water towers or lava.
4. Dive directly below the islands: underlying Elsewhere ocean and seabed should remain intact, with no surface biome converted into a sky biome.
5. Generate across chunk edges, restart the world, regenerate a different seed, and check /locate and multiple island silhouettes. Verify vegetation does not float off island rims.
6. Inspect nearby mainland mountains at sky height; sky masks should not change them. Repeat checks around river mouths and ocean coastlines.
7. Run all existing CI scripts, especially biome feature-order DAG and biome-alpha checks.

**Caution:** Noise amplitudes, density sign and sky-climate thresholds are first-pass values; the branch is a draft pending in-game calibration. Avoid promoting as alpha until terrain and registry tests are green.

## Rice dependency / rollout
Rice is handled separately in existing PR #39. Keep the two PRs independent for easier diagnosis, then merge both only after their respective compilation and fresh-world testing. No wild rice generation yet.
