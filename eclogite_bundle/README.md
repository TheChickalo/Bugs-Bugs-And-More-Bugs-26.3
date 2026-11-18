# Eclogite family — Minecraft 26.3

Back up or commit your local project first. Extract eclogite_bundle into the project directory containing gradlew.bat. Run from that directory:

    python .\eclogite_bundle\install_eclogite.py
    .\gradlew.bat runDatagen
    .\gradlew.bat runClient

This adds 18 blocks and their models, loot, crafting and furnace recipes, mining/wall/ore replacement tags, English names, and the Eclogite material rule. Requires Schist already registered. Uses local source files, and makes .eclogite-backup files. No textures are overwritten or included. Supply these six 16x16 PNGs in src/main/resources/assets/bugsbugsandmorebugs/textures/block:

- eclogite.png
- cobbled_eclogite.png
- polished_eclogite.png
- eclogite_bricks.png
- chiseled_eclogite.png
- cracked_eclogite_bricks.png

## Stage 2: Experimental uneven underside

After the regular layer works, run:

    python .\eclogite_bundle\install_eclogite.py --underhang
    .\gradlew.bat runClient

The second invocation adds a custom density mask below Y=-40 and increases aquifer exclusion at low Y. This carves a variable underside within the current -64 world limit; deep lava may still appear. It is NOT a finished Underhang biome or guaranteed dry void. Test in a new disposable world. Restore noise_settings/elsewhere.json from its .eclogite-backup and remove the new underhang_mask.json to revert the terrain prototype (do not revert the material rule if keeping Eclogite). Use new chunks/worlds for each test. Existing generated chunks do not change.

Eclogite begins at Y=-33. Above it, Y=-32 through -30 is reserved for the future Sundering Horizon but remains Schist for now. No changes to Overworld resources. Existing placed ore and stone features may still add vanilla materials; the Eclogite material is evaluated before the vanilla material-rule veins. Adjust biome features separately later.

The Python installer was syntax-checked and run twice on representative local source stubs and existing noise-settings JSON, checking all eighteen symbols and idempotence. Java compilation and in-game world generation have not been tested here.
