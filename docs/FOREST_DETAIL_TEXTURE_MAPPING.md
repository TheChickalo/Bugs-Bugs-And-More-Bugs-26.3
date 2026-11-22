# Cedar / Persimmon / Mimosa forest detail — texture map

These blocks and items are registered on the feature branch, **without inventing new final pixel art**.
Place each custom 16×16 PNG in the paths below, then test with `./gradlew runDatagen` and `./gradlew runClient`.
The JSON models and blockstates already reference these names.

## Cedar rainforest
| ID | Texture PNG(s) | Rendering / state |
| --- | --- | --- |
| `cedar_roots` | `textures/block/cedar_roots.png`, `cedar_roots_top.png` | Root framework modeled after existing Willow Roots; x/y/z axis states |
| `mossy_cedar_log` | `textures/block/mossy_cedar_log.png` | Rotating pillar; **uses existing** `cedar_log_top.png` for ends |
| `hanging_forest_moss` | `textures/block/hanging_forest_moss.png`, `hanging_forest_moss_tip.png` | Hanging 2-state cross models (`tip=false/true`) |
| `liverwort` | `textures/block/liverwort.png` | Low carpet model |
| `clubmoss` | `textures/block/clubmoss.png` | Low carpet model |
| `bracket_fungus` | `textures/block/bracket_fungus_top.png`, `bracket_fungus_side.png` | Four 3D cluster shapes, four wall-facing rotations; 1→4 with repeated placements |

## Persimmon woodland
| ID | Texture PNG(s) | Rendering / state |
| --- | --- | --- |
| `persimmon_fruit` | `textures/item/persimmon_fruit.png` | Edible item, may drop when breaking Persimmon Leaves, like apples |
| `fallen_autumn_leaves` | `textures/block/fallen_autumn_leaves.png` | Leaf litter with 1–4 scattered sections |
| `silver_grass` | **Existing** `textures/block/silver_grass_single.png`, `_lower.png`, `_upper.png` | Registered ID remains `silver_grass`; visible name is **Silvergrass** |
| `anemone_flowers` | `textures/block/anemone_flowers.png` | Cross block |
| `bush` | `textures/block/bush.png` | Cross block, named Bush |

## Mimosa savanna
| ID | Texture PNG(s) | Rendering / state |
| --- | --- | --- |
| `flowering_mimosa_leaves` | `textures/block/flowering_mimosa_leaves.png` | Same cube geometry as existing `mimosa_leaves`; leaf decay supported |
| `mimosa_blossoms` | `textures/block/mimosa_blossoms.png` | 3D crossed blossom, amethyst-cluster-style facing and waterlogging |
| `fallen_mimosa_petals` | `textures/block/fallen_mimosa_petals.png` | 1–4 section ground-cover litter |
| `feathergrass` | `textures/block/feathergrass.png` | Cross block |
| `pink_clover` | `textures/block/pink_clover.png` | Wildflower-like ground litter with 1–4 sections |
| `mimosa_seedpods` | `textures/item/mimosa_seedpods.png` | Item drop from Mimosa Leaves and Flowering Mimosa Leaves |

## Resources and gameplay

All JSON mappings live in `src/main/resources/assets/bugsbugsandmorebugs/{blockstates,models/block,models/item,items}`.
PNG files go in `src/main/resources/assets/bugsbugsandmorebugs/textures/{block,item}`.

Java registration: `ModBlocks.java`, `ModItems.java` and `ModFoods.java`.
Loot: `ModBlockLootTableProvider.java`. Tags: `ModBlockTagsProvider.java`.
Bracket fungus count and support logic: `block/custom/BracketFungusBlock.java`.
Leaf-mounted blossom support logic: `block/custom/LeafBlossomBlock.java`.

**Scope:** This is an asset/registration pass, not worldgen. No tree feature or biome placement has been added in this branch. Texture PNGs are deliberately not included; the game will display missing textures until they are supplied. Compile/datagen and in-game tests still need to be run locally.
