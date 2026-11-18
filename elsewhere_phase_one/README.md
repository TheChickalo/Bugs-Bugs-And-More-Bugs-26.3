# Elsewhere phase one + loam (Minecraft 26.3)

This merges with your local source, assumes Schist is already installed, and includes the corrected Eclogite family installer. Commit or copy your project first. Run from the project root (where gradlew.bat is). It makes backups in .elsewhere-phase-one-backup and the Eclogite installer also makes its own .eclogite-backup copies. Do not commit backups, and remove/restore newly created files separately if rolling back.

```powershell
py .\elsewhere_phase_one\install_phase_one.py
.\gradlew.bat runDatagen
.\gradlew.bat runClient
```

For a FIRST installation without the experimental lower terrain, use `py .\elsewhere_phase_one\install_phase_one.py --no-underhang` instead. This flag doesn't undo already installed Underhang modifications. To enable later, rerun without the flag. Use a disposable NEW world. Do not change the lower build limit of a valuable existing save. The upper build limit remains 320; min Y becomes -128 and height becomes 448. The Underhang is only a density/aquifer prototype, not a finished biome; deeper lava, caves and noise interpolation may need tuning. No bedrock layer is added. The vanilla final density is masked below -40; old terrain density above that remains unchanged.

## Textures (all existing PNGs preserved)
Put your own 16x16 art in `src/main/resources/assets/bugsbugsandmorebugs/textures/block/`:
- loam.png
- grassy_loam_top.png (opaque grayscale)
- grassy_loam_side_overlay.png (grayscale with transparent pixels outside the grass)
- grassy_loam_side.png (optional un-tinted base; if absent, loam.png is reused)
- ashstone.png
- shatterstone.png
- eclogite.png, cobbled_eclogite.png, polished_eclogite.png, eclogite_bricks.png, chiseled_eclogite.png, cracked_eclogite_bricks.png

If the names differ, rename existing texture files to match. No PNGs are generated or overwritten. If you add the optional side texture later, rerun the installer to select it.

## Changes
- Schist stays installed. Eclogite family registered with crafting/smelting, loot, tags, models and English names. Eclogite below Y=-32, Schist above the disaster horizon, mixed Ashstone/Shatterstone at Y=-31 and -30, with Shatterstone at -32. The upper boundary is fixed for this first pass; the underside is irregular.
- New Loam and Grassy Loam, grass drops Loam unless Silk Touch, spreads to exposed Loam and reverts when covered by a solid block or water. It does not depend on effective daylight (future Starless). Grayscale top and overlay are tinted with the biome grass color by a separate client initializer. Both can be tilled into vanilla farmland via the 26.3 tag. No custom farmland/path, snow-covered state or bonemeal vegetation logic yet.
- Custom surface rule applies Loam and Grassy Loam instead of the vanilla surface rule; no vanilla giant copper/iron veins. Vanilla default underground variety and ore placed features removed from the two current Elsewhere biomes. Future secondary deposits must be added later. Vanilla crystal formations and other features can still place vanilla materials.
- Existing ground checks accept new ground types; Maple Lake and Maple Water Shore use rhyolite instead of vanilla stone/andesite in direct material selections. Other features/configured feature block predicates may still contain vanilla references: test trees, grass/flowers, bamboo, glowshrooms and karst outcrops. Existing cultivated Farmland and other vanilla soil types are preserved.
- New generated resources should be regenerated with DataGen. If your DataGen complains about manually authored Grassy Loam models, send the exact error; do not replace them with an untinted cube.
- Existing shared minecraft:overworld density overrides are NOT removed by this installer. Keep that cleanup for the isolated terrain pass.

## Roadmap
1. Test blocks, textures and layer transitions; inspect the underside in spectator mode; tune density and fluids. Verify flora and karst still generate.
2. Add Dacite, Chert, Serpentinite, Lapilli and Amphibolite; replace remaining vanilla lake/sediment blocks; finish soil compatibility.
3. Implement independent terrain elevations, Stone Forest, Limestone Caves, Underhang biome and Hinoki Forest.
4. Bug-only natural spawns, Daybreak/Moonfall/Starless, portal and dimension transition background.
5. Mineral progression: Jade (diamond-equivalent candidate), Tektite (Sundering Horizon/portal or artifact material), iron-equivalent, rare deep material, oddball ores. Tektite and Jade are TODOs only, not registered by this package.
6. Bug ecology, settlements, ruins, lore, artifacts, minibosses, main boss and release polish.

Python syntax and installer file operations tested with representative fixture files; Java compilation, Minecraft registry loading and visual/world-generation behavior require local testing.
