# Schist Family Add-on — Bugs, Bugs, and More Bugs (Minecraft 26.3)

## What's included

18 block definitions with BlockItems and creative-tab registration:
- Schist (Silk Touch => Schist, otherwise Cobbled Schist)
- Cobbled Schist
- Polished Schist
- Schist Bricks
- Chiseled Schist
- Cracked Schist Bricks
- Stairs, slabs, walls for each of the first four blocks (12 more)

Also adds: generated model/blockstate hooks, loot tables (double slabs drop 2), crafting recipes (including smelt Schist Bricks -> Cracked Schist Bricks), pickaxe and wall tags, `DEEPSLATE_ORE_REPLACEABLES` for Schist, en_us names, and six temporary 16x16 textures.

The installer also replaces the `minecraft:deepslate` surface-material entry in your **Elsewhere-only** `worldgen/material_rule/elsewhere.json` with `bugsbugsandmorebugs:schist`, if found. Vanilla Overworld is not changed.

## Installation

1. Extract the archive.
2. Copy the **entire extracted `schist_bundle` folder** into the folder containing your project's `gradlew.bat` and `src` directory, or run with its full path from the project root.
3. Open PowerShell in the project root and run:

   `python .\schist_bundle\install_schist.py`

   (If needed: `py .\schist_bundle\install_schist.py`.)

4. Run `\.\gradlew.bat runDatagen` then `\.\gradlew.bat runClient`.
5. **New chunks** will generate Schist in the current Elsewhere deepslate band (approximately below Y=0, blended to Y=8).

## Safety and limitations

- The installer checks anchor text before changing files. Every original Java file it edits gets a `.schist-backup` copy the first time. It will not overwrite existing texture PNGs.
- It is meant for the GitHub file layout and code current on 2026-11-17. If you have changed these classes locally, verify the proposed insertions before running.
- `ModRecipeProvider` uses the 26.3 `SimpleCookingRecipeBuilder.smelting` builder. If your mappings reject this overload, send the exact compilation error.
- Textures are **rough placeholders**; replace each of the six PNGs with your chosen 16x16 art later. All structural variant textures are generated from these bases.
- Currently only Schist has worldgen; Cobbled Schist and its decorative variants are obtained by mining/crafting.
- Schist is tagged as a deepslate ore-replacement target. Vanilla ore features may still need testing when the surrounding base stone isn't vanilla deepslate.
- Does not yet add stonecutting recipes, extra schist ore textures, or custom sound families. No independent registry/worldgen type is required.
- No automatic compilation was possible in this environment; test `runDatagen` on your machine.
