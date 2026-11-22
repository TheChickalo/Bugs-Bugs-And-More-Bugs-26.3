# Vanilla 26.3 branch & foliage overhaul

After repeated in-game complaints about unnatural branches and leaf blobs, this pass uses **only Mojang's built-in tree trunk and foliage placers** for the three problem species. The previous custom crown helpers in `ForestHabitatFeature` are unreachable for kinds 1, 3 and 4; those kinds now scatter plants only, while the actual tree `TreeFeature` is responsible for branches and leaves.

| Tree | 26.3 native trunk placer | 26.3 native foliage placer | Intent |
| --- | --- | --- | --- |
| Persimmon | `minecraft:bending_trunk_placer` (Azalea) | `minecraft:random_spread_foliage_placer` (Azalea) | Compact orchard tree, asymmetry, continuous uneven leaves, no radial spoked branches |
| Mimosa | `minecraft:cherry_trunk_placer` | `minecraft:cherry_foliage_placer` | Spreading, naturally forked limbs and much fuller flat-ish leaf pads; existing flowering blossom decorator preserved |
| Willow | `minecraft:cherry_trunk_placer` | `minecraft:cherry_foliage_placer` | Taller trees: trunk 13–20 blocks before additional crown/branches, broader multi-branched canopy, with existing hanging willow leaf/root decorator |

The configurations are based on vanilla 26.3 `data/minecraft/worldgen/feature/azalea_tree.json` and `cherry.json`, with species-specific height, width and branching changes. Wisteria is unchanged.

**Testing:** run `runDatagen` and `runClient`. On a new world or new chunks, inspect several examples of each species. Test sapling growth separately. This patch changes future generation only, not old placed trees. The automated forest regression test verifies actual generated JSON and the tall willow base height.

**Important:** the exact silhouettes need in-game review; no screenshot-based visual tests are available in CI.
