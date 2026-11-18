# Elsewhere Minecraft 26.3 — Lava and stone quick patch

## Install

1. Extract `elsewhere_stones_quickpatch` into the project root beside `gradlew.bat`.
2. Run `py .\elsewhere_stones_quickpatch\install_quickpatch.py`.
3. Run `.\gradlew.bat runDatagen` then `.\gradlew.bat runClient`.
4. Generate a NEW disposable world. Existing generated lava will not disappear.

The installer makes first-write backups in `.elsewhere-stones-quickpatch-backup`; it won't overwrite existing PNG textures.

## Lava

Minecraft 26.3 has a built-in lava level below min(-54, sea_level) even if `default_fluid` is air. Setting `sea_level = -256` (beneath Elsewhere's minimum -128), neutralizing the lava aquifer noise, and using `default_fluid = minecraft:air` suppresses that automatic lava layer. It also suppresses *natural water aquifers*, and some vanilla sea-level-dependent surface behaviors can change. Explicitly placed Maple Forest lakes remain water. This is a prototype workaround; selective aquifers may later require a custom fluid picker or code-level generator intervention.

## Stone and lake update

New registrations (if absent): `mossy_rhyolite` and `lapilli` (falling loose volcanic sediment), plus Amphibolite's main, polished, bricks, chiseled, chiseled bricks variants, plus stairs, slabs, walls for the 3 structural families. The five basic Amphibolite names are `amphibolite`, `polished_amphibolite`, `amphibolite_bricks`, `chiseled_amphibolite`, `chiseled_amphibolite_bricks`. Only missing registrations are added.

MapleLakeFeature's rock mix is 33% Amphibolite, 33% Cobbled Rhyolite, 15% Rhyolite, 11% Mossy Rhyolite, and 8% Lapilli. The patch updates MapleWaterShoreFeature's mossy boulders, gravel and stone bank decorations. Amphibolite generates in small-to-medium solid clusters within Schist and Eclogite (9 attempts per chunk between roughly Y=-55 and Y=-5, biome-filtered) and is **not** a falling gravel block: if you want Minecraft's falling-block behavior, use a separately named loose sediment such as Lapilli.

Placeholder textures are simple COPIES of existing art so models do not appear missing; replace all placeholders with your actual 16x16 textures. Existing files are never overwritten.

## Scope and caveats

This replaces explicit gravel placements in custom feature classes, NOT all vanilla gravel across Minecraft, does not remove existing lava, and does not change other vanilla dimensions. The installation assumes the same project conventions as your current `ModBlocks`, model, recipe, tags, loot, and custom lake classes. We have run syntax and file-edit checks but **have not compiled the complete Minecraft 26.3 mod**. Send the first compilation or registry error if needed.

**26.3 schema caution:** The patch deliberately retains the `aquifers` field in noise settings (its absence could cause registry decoding errors). It sets the aquifer lava density function to `0.0` instead. The low sea level is a workaround and may change surface water or shoreline behavior; confirm in a fresh test world.
