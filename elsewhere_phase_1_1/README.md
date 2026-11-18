# Elsewhere Phase 1.1: geology cleanup, Underhang beta, advancements

**Target:** Minecraft 26.3 / Fabric. Installer was written against the user's Minecraft 26.3 code conventions and Phase One installer. It has **not** been compiled or run in Minecraft.

## Prerequisites

First install **Schist** and **Elsewhere Phase One + Loam**. This upgrade requires the Java constants `COBBLED_RHYOLITE`, `RUBBLED_LIMESTONE`, `MOSSY_LIMESTONE`, `SCHIST`, `ECLOGITE`, `ASHSTONE`, `SHATTERSTONE`, and Phase One soil classes. Make a Git commit or copy of the project before installation.

Extract `elsewhere_phase_1_1` into the project root (beside `gradlew.bat`), then in PowerShell run:

```powershell
py .\elsewhere_phase_1_1\install_phase_1_1.py
.\gradlew.bat runDatagen
.\gradlew.bat runClient
```

The installer creates backups under `.elsewhere-phase-1-1-backup/`. It edits **source/resource files only**, leaves existing block texture PNGs alone, and does not touch your saves. Test using a **new disposable world**: dimension height and noise settings have changed.

## What changes

- Removes vanilla cave **carvers** from both Elsewhere biomes to keep the disaster boundary intact. Vanilla **noise caves remain**.
- Removes vanilla monster rooms, geodes, default ore placements, and default underground rock variations from those two biomes.
- Replaces direct Java placements of `Blocks.COBBLESTONE` with `COBBLED_RHYOLITE` (or `RUBBLED_LIMESTONE` in Karst/Limestone feature files), and direct `Blocks.MOSSY_COBBLESTONE` placements with `MOSSY_LIMESTONE` in `src/main/java/.../worldgen/`.
- Adds a custom `underhang` biome with ash particles, no precipitation, dark fog, and no natural mob entries or added features. The biome selector uses deep climate depth and is **approximate**: its biome distribution may also encompass very deep caves. It does not yet follow the underside surface exactly.
- Changes Elsewhere's `default_fluid` to `minecraft:air` and suppresses aquifer lava noise. Custom lakes still explicitly place water. **Deep lava suppression is a beta**: the generator may still have other hardcoded lava behavior and should be checked in-game. If lava persists, a dimension-specific aquifer override will be necessary.
- Adds density-function `sundering_barrier` to force solid terrain across the layer. The resulting Ashstone/Shatterstone material has an uneven, noise-controlled upper margin, with a deeper eclogite layer.
- Adds datapack advancements `root` (enter Elsewhere) and `as_old_as_time` (obtain Ashstone or Shatterstone), localized in `en_us.json`. Adds a **heavily commented** Java `ModAdvancements.java` index for adding future advancements. No advancement bootstrap code is required for these JSONs.

## Limitations worth knowing

1. **“As Old as Time” is triggered by acquiring a Sundering block, not by the block-break event itself.** This is intentionally implemented using the reliable vanilla `minecraft:inventory_changed` trigger. Detecting breaking specifically needs a separate Fabric break event/custom criterion.
2. Some **vanilla structures** can still introduce cobblestone in Elsewhere if their biome tags allow generation. This installer only handles the named default biome features and direct Java placements; it doesn't globally replace structure palettes.
3. The underhang biome uses vertical climate selection, not a surface-shape-aware biome selector. Its ash particles may appear in deep caves too.
4. The floor fluid fix, density threshold, and biome distribution must be tested in a fresh world. They are **not verified in Minecraft 26.3**.
5. Removing default ore placements removes vanilla ores. You will need custom ore distributions when Jade/Tektite and progression materials are added.
6. The boundary is geological worldgen, not an unbreakable block barrier. Players mine through it as intended; caves should not pass through the dense shelf, but structures/commands can still modify it.

## Testing checklist

- `runDatagen` finishes successfully (note registry/codec errors if any).
- `runClient` and a fresh world load.
- Use `/execute in bugsbugsandmorebugs:elsewheredim run tp @s 0 -80 0` in spectator mode to inspect the Underhang.
- Check for lava below Y≈−60, then explore lower caves to see whether they connect through the horizon.
- `/locate biome bugsbugsandmorebugs:underhang` may help locate the biome, but can show points in deep caves.
- Test the achievement by entering Elsewhere and mining Ashstone or Shatterstone.
- Check `ModAdvancements.java` comments for future additions.

## Planned later

- More precise Underhang-only biome placement and custom fog density.
- Special Underhang bugs, hanging plants, and structural ruins.
- Tektite and Jade ore generation, advancements `Written in Stone` and `Jaded`.
- Custom secondary rocks such as Dacite, Chert, Serpentinite, Lapilli.
