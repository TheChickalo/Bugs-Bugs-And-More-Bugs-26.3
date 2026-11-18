# Elsewhere biome starter — Minecraft/Fabric 26.3

**Repository inspected:** `TheChickalo/Bugs-Bugs-And-More-Bugs-26.3`, commit `28bef7b2647a89ef4fd39cc5335196264fead018` (November 18, 2026).

## What this installer ACTUALLY does

- Fixes `AmphiboliteDepositFeature`: deposits only replace **Schist**, not Eclogite.
- Registers and bootstraps four biomes: **Stone Forest**, **Ginkgo Forest**, **Hinoki Highlands**, **Volcanic Peak**.
- Adds a tall Stone Forest spire feature reusing the already-implemented `KarstOutcropFeature` class, with `minHeight=10`, `maxHeight=28`. Updates the existing Karst feature biome gates to allow Stone Forest.
- Adds climate-point prototypes to the existing Elsewhere dimension definition, which is manually maintained in `src/main/resources/data/bugsbugsandmorebugs/dimension/elsewheredim.json`.
- Gives the peak biome exposed rhyolite rather than Loam on the surface.
- Adds biome names to English translations.
- **Temporary trees:** Ginkgo Forest and Hinoki Highlands reuse the existing Maple tree placed feature so you can test biome routing. They do NOT look like ginkgo or hinoki yet. Volcanic Peak has no trees.
- Does not change the working Underhang or Sundering density functions by default.

**Stone Forest adjacency:** placing a Stone Forest climate sample near Highland Karst tends to make a transition, but vanilla multi-noise **cannot guarantee** every Stone Forest patch actually touches a Highland Karst. Absolute adjacency will need a dedicated spatial biome selection solution in a later iteration.

## Install

Extract this directory into the repository root (the folder containing `gradlew.bat`).

In PowerShell:

```powershell
py .\elsewhere_biome_starter\install_biomes.py
.\gradlew.bat runDatagen
.\gradlew.bat runClient
```

### OPTIONAL experimental high mountains

To test raising the build ceiling from Y=320 to **Y=640** and an experimental high-mountain density function:

```powershell
py .\elsewhere_biome_starter\install_biomes.py --mountain-prototype
.\gradlew.bat runDatagen
.\gradlew.bat runClient
```

The mountain prototype is based on the existing climate/density functions. It is **not** a finished Mount Fuji-shaped cone, crater, or altitude-constrained treeline. It might produce large massifs, unexpected biome transitions, or performance issues. Do **not** apply this option to a valued existing save: use a separate disposable world and preserve backups. The high mountain biome selection is climate-based, not strictly height-dependent yet.

We deliberately did not permanently activate the experimental mountain settings by default because your current Underhang is working and you wanted it preserved.

## Additional art needed

See `TEXTURE_AND_BLOCK_MANIFEST.md`. We have not created or overwritten any texture PNGs or tree blocks.

## Rollback

Backups are kept in `.elsewhere_biomes_backup/` under the same relative directory paths. To restore:

```powershell
py .\elsewhere_biome_starter\install_biomes.py --restore-backups
```

Caveats: Rollback restores each file's state **from before its first modification** by this installer, so it also discards later edits to those files. Copy your project elsewhere first if you have made new changes since installation.

## Limitations and checks

The installer checks for expected file anchors and should stop without writing anything if those are missing. Source in your local checkout might differ from this pushed commit. The Python installer has had syntax and synthetic-file tests, but the Java build was **not compiled in Minecraft 26.3** here.

Test `/locate biome bugsbugsandmorebugs:stone_forest`, `ginkgo_forest`, `hinoki_highlands`, and `volcanic_peak` in a **fresh** Elsewhere world. Inspect Stone Forest spire heights and growth. Look for errors from DataGen first.
