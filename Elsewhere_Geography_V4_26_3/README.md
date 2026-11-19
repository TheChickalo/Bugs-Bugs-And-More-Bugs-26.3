# Elsewhere Geography & Ecology V4 — Fabric 26.3

Patch based on the user's 2026-11-18 commit `068769ba0731eef6f30b51810d14536d424da760` of `TheChickalo/Bugs-Bugs-And-More-Bugs-26.3`.

## Before running

1. Commit or zip your project, and copy your world saves. **Only test worldgen in a disposable world**—this changes the worldgen configuration.
2. Extract this `Elsewhere_Geography_V4_26_3` folder alongside `gradlew.bat` in your project root. Do **not** put it under `src`.
3. Keep your existing texture PNGs. The installer **never modifies or re-encodes PNG files**. Verify these are present in `src/main/resources/assets/bugsbugsandmorebugs/textures/block/`:
   - `cattails_bottom.png`, `cattails_middle.png`, `cattails_top.png`
   - `reeds_bottom.png`, `reeds_middle.png`, `reeds_top.png`
   - `alpine_grass.png`, `hakone_grass.png`, `iris_flower.png`
   - `duckweed.png`, `lotus.png`, `lotus_flower.png`, `ginkgo_fallen_leaves.png`

## Recommended installation

From PowerShell in the mod root:

```powershell
py .\Elsewhere_Geography_V4_26_3\install.py --geography --ecology --dry-run
py .\Elsewhere_Geography_V4_26_3\install.py --geography --ecology
.\gradlew.bat runDatagen
.\gradlew.bat runClient
```

`--geography` changes data-driven terrain and biome climate points. `--ecology` adds Bamboo Lowlands biome registration, shallow pond and winding path features, 3-high water-edge plants, other flora, assets, and natural decoration. The **first** geography install must also include `--ecology` so Bamboo Lowlands has a registered biome ID. After that, either mode can be re-run on its own. It is idempotent: identical re-runs make zero changes.

### Worldgen changes

- **River:** Rebuilds the river-climate sample points with nearby land guard points at weirdness ±0.075, and redefines the channel mask from `abs(minecraft:overworld/ridges)`. The aim is thinner rivers whose biome follows their actual channels; it is not a guarantee of perfectly connected vanilla river graphs.
- **Lake:** Moves lake candidate climates inland and adds an inland/humid/noise-gated basin mask. Lakes may be less frequent and still need testing to ensure their biome label matches water consistently.
- **Stone Forest:** Shared Highland Karst uplift, then **subtraction of valleys** using an eroded roof with a target Y≈60 floor. Irregular residual columns and broader rock remnants come from 2D badlands noise, not added pillar objects.
- **Mountain:** Adds stronger volcanic massif density rising toward Y≈500+ in suitable cold, high-continentalness/low-erosion climates, leaving Underhang below Y=-16 unchanged.
- **Bamboo Lowlands:** Wet warm lowland climate points, 32 bamboo placement attempts/chunk, scattered ferns, existing continuous Karst-trail logic with coarse dirt/podzol palette, small guarded single-block-deep pools.
- **Material rule:** Bamboo lowland surface now mixes Grassy Loam, Podzol and Coarse Dirt; estuary Mud is limited to surface pockets, with Loam and Grassy Loam elsewhere.

### New blocks/features

- **Cattails** and **Reeds:** Three cross-texture sections: bottom waterlogged in one block of water, middle above the water, top two blocks above. Naturally sampled in shallow water in River, Lake, Estuary and Bamboo Lowlands. Requires solid soil beneath water; grows as exactly 3 segments.
- **Duckweed** and **Lotus:** Floating, lily-pad-shaped block models; **Lotus Flower** is a separate cross block that grows on the Lotus pad.
- **Alpine Grass**, **Hakone Grass**, and **Iris**: Cross-model flowers/plants with biome-specific natural decoration.
- **Ginkgo Leaf Litter**: Small carpet-style ground decoration, naturally placed in Ginkgo Forest.

These are first-pass worldgen placements. It is possible that the default block item for the aquatic pads will not target water surfaces appropriately when held by the player; their worldgen and `/setblock` behavior is the primary testing target.

## Test in a new world

1. **Launch/DataGen:** No missing registry key, duplicate tag or Java compiler errors.
2. **River:** Press F3; the `river` biome should generally occupy the narrow water channel and its banks, not a large flatland region. Check at least two different river systems.
3. **Lake:** Find a lake inland, not abutting the ocean.
4. **Stone Forest:** From Highland Karst, find an eroded transition with lower Y≈60–80 valley corridors and much taller residual rock formations. Look for chunk boundaries and hanging terrain artifacts.
5. **Volcanic Peak:** Locate a large massif; check heights against nearby Karst. Peaks should be taller, but there is no hard summit-only biome guarantee in V4.
6. **Underhang:** Confirm the void remains empty and dry below the crust, including under oceans.
7. **Bamboo Lowlands:** Winding narrow trails, Podzol/Coarse Dirt patches, ferns, Bamboo, rare 1-block-deep pools.
8. **Water-edge plants:** Check a one-deep water shelf; bottom of Reed/Cattail is submerged, with two segments above.
9. **Ocean performance:** Check FPS and TPS while stationary and while generating fresh ocean chunks. This patch does **not** comprehensively fix possible ocean generation lag.

To inspect generated river and terrain data, look at:
`src/main/resources/data/bugsbugsandmorebugs/worldgen/noise_settings/elsewhere.json`
`src/main/resources/data/bugsbugsandmorebugs/dimension/elsewheredim.json`

## Revert

The installer makes a **one-time original backup** in `.elsewhere_v4_backup` and a manifest of newly created files. Do not remove that folder before you are happy with the mod.

```powershell
py .\Elsewhere_Geography_V4_26_3\rollback.py --confirm
.\gradlew.bat runDatagen
```

Rollback overwrites modified files with their pre-V4 versions; do not use it after making unrelated edits unless you have saved them. The installer directory can be deleted after a successful test and commit; keep the backup until confirmed stable.

## Limits and honest validation status

- The Python installer passed `py_compile`, repeated-run tests, synthetic source-patching tests, and all emitted JSON files parsed correctly.
- **The Minecraft mod has not been compiled or launched in this environment.** Fabric/Minecraft 26.3 Java API and runtime worldgen details must be confirmed with your own `runDatagen` and `runClient`.
- This is a **prototype**, not a claim that Stone Forest, mountain skyline, or hydrology are already perfect. Please send the first failing build log or screenshots with F3 height if needed.
- Exact river channel + biome coincidence and guaranteed low/medium/high biome elevation bands may ultimately need a custom biome source or custom terrain generator class; basic multi-noise points cannot enforce those properties perfectly.
