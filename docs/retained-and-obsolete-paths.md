# Retained and obsolete paths — do not delete without review

This is a preservation inventory, not a removal list. Some old paths remain useful prototypes for later expansions.

| Path / identifier | Current status | Keep because | Revisit when |
| --- | --- | --- | --- |
| `ModPlacedFeatures.MAPLE_LAKE_PLACED_KEY` / generated `maple_lake_placed.json` | Registered, not attached to active biome generation | Candidate pond generator | The per-cell biome-boundary, slope, and edge sealing logic passes terrain tests |
| `ModPlacedFeatures.LAKE_POND_PLACED_KEY` / generated `lake_pond_placed.json` | Registered, not attached to active biome generation | Alternative pond density and styling | The same containment tests pass; no return of mud/gravel mound lakes |
| `MapleLakeFeature.java` | Code exists, placement dormant | Future small lakes or constructed ponds | Lake feature can run without cross-biome leaks or unpredictable berms |
| English legacy limestone localization keys | Potentially unused; retained | May support future item/block names, legacy save compatibility | Dedicated translation/registry cleanup prior to beta |
| `glowshroom_block` absent loot table | Deliberate placeholder | Future hoe-based harvest, not a normal self-drop | Hoe action, enchantment behavior and exact drop rate designed |
| `infested_dirt` absent loot table | Intentionally empty | Breaks without resource reward by design | Only if gameplay concept changes |
| Worker ant incomplete AI and spawn rules | Prototype only | Placeholder while custom model/animations are pending | Natural life cycle, goals, spawn ecology and visual assets exist |
| Cedar client-only local rain | Deliberately visual for now | Preserves biome-local weather without toggling global world weather | Server-side precipitation mechanics are designed |
| Open rice PR #26 | Separate review branch, not on master | Rice farming can be evaluated independently | The PR is tested, merged and compatible with biome-only scope |

Before deleting any retained path, search references in biome bootstrap, generated JSON, recipes, assets and migrated world data. Rename/rework only with explicit migration considerations; a dormant asset is not automatically dead code.
