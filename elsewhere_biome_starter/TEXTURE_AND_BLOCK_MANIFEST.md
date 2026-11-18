# Elsewhere art and block manifest — checked against pushed repository

## Already present (PNG files committed)

- **Core stone:** `rhyolite`, `cobbled_rhyolite`, `polished_rhyolite`, `rhyolite_bricks`, `chiseled_rhyolite`, `cracked_rhyolite_bricks`, `mossy_rhyolite`; `schist`, `cobbled_schist`, `polished_schist`, `schist_bricks`, `chiseled_schist`, `cracked_schist_bricks`; `eclogite`, `cobbled_eclogite`, `polished_eclogite`, `eclogite_bricks`, `chiseled_eclogite`, `cracked_eclogite_bricks`.
- **Karst:** `limestone`, `rubbled_limestone`, `mossy_limestone`, `polished_limestone`, `weathered_limestone`, `limestone_bricks`, `limestone_pillar`, `limestone_pillar_top`, `rendzina`, `lithosol`.
- **Metamorphic/loose:** `amphibolite`, `polished_amphibolite`, `amphibolite_bricks`, `chiseled_amphibolite`, `chiseled_amphibolite_bricks`, `lapilli`.
- **Soils/disaster:** `loam`, `grassy_loam_top`, `grassy_loam_side_overlay`, `ashstone`, `shatterstone`.
- **Maple family:** `maple_log`, `maple_log_top`, `stripped_maple_log`, `stripped_maple_log_top`, `maple_planks`, `maple_leaves`, `maple_sapling`, `maple_door_top`, `maple_door_bottom`, `maple_trapdoor`.
- **Plants/other:** `glowshroom`, `glowshroom_block`, `silver_grass_*`, `bush_clover_*`, `dwarf_bamboo_*`, `bellflowers`, `haskap_berry_bush_stage*`, `barberry_bush_stage*`, `daikon_crop_stage*`.

All the `*_stairs`, `*_slab`, `*_wall`, `*_fence` models generally reuse their source block textures. **You do not need a separate PNG for every shape.**

## New art needed for authentic Ginkgo Forest

### Minimum viable tree set (8 block textures + 2 door textures)

- [ ] `ginkgo_log.png` — bark side
- [ ] `ginkgo_log_top.png` — end grain
- [ ] `stripped_ginkgo_log.png`
- [ ] `stripped_ginkgo_log_top.png`
- [ ] `ginkgo_planks.png`
- [ ] `ginkgo_leaves.png` — yellow/gold, not biome-tinted to green
- [ ] `ginkgo_sapling.png`
- [ ] `ginkgo_door_bottom.png`
- [ ] `ginkgo_door_top.png`
- [ ] `ginkgo_trapdoor.png`

### Block registrations / code still to create

- [ ] `GINKGO_LOG`, `GINKGO_WOOD`, `STRIPPED_GINKGO_LOG`, `STRIPPED_GINKGO_WOOD`, `GINKGO_PLANKS`, `GINKGO_LEAVES`, `GINKGO_SAPLING`, `POTTED_GINKGO_SAPLING`.
- [ ] `GINKGO_STAIRS`, `GINKGO_SLAB`, `GINKGO_DOOR`, `GINKGO_TRAPDOOR`, `GINKGO_FENCE`, `GINKGO_FENCE_GATE`, `GINKGO_BUTTON`, `GINKGO_PRESSURE_PLATE`.
- [ ] Ginkgo tree configured/placed feature, normal tree, occasional giant/old-growth 2x2 tree.
- [ ] Optional: `ginkgo_leaf_litter.png` + `GINKGO_LEAF_LITTER` block (1–4 layers); yellow leaf-fall particle; forest floor patches.

## New art needed for authentic Hinoki Highlands

- [ ] `hinoki_log.png`, `hinoki_log_top.png`
- [ ] `stripped_hinoki_log.png`, `stripped_hinoki_log_top.png`
- [ ] `hinoki_planks.png`
- [ ] `hinoki_leaves.png` — dark evergreen flat sprays
- [ ] `hinoki_sapling.png`
- [ ] `hinoki_door_bottom.png`, `hinoki_door_top.png`, `hinoki_trapdoor.png`

### Block registrations / code still to create

- [ ] `HINOKI_LOG`, `HINOKI_WOOD`, `STRIPPED_HINOKI_LOG`, `STRIPPED_HINOKI_WOOD`, `HINOKI_PLANKS`, `HINOKI_LEAVES`, `HINOKI_SAPLING`, `POTTED_HINOKI_SAPLING`.
- [ ] `HINOKI_STAIRS`, `HINOKI_SLAB`, `HINOKI_DOOR`, `HINOKI_TRAPDOOR`, `HINOKI_FENCE`, `HINOKI_FENCE_GATE`, `HINOKI_BUTTON`, `HINOKI_PRESSURE_PLATE`.
- [ ] Tall narrow-crown hinoki tree + rare old-growth 2x2 mega variant.
- [ ] Treeline-aware placement: limit Hinoki trees to mountain lower and middle slopes.

## Stone Forest

- **No mandatory new stone PNGs.** Reuse the already completed `limestone.png`, `weathered_limestone.png`, `rubbled_limestone.png`, and `mossy_limestone.png`.
- [x] Stone Forest spire feature prototype (new biome can use existing limestone blocks).
- [ ] Finalize spire tapering, wind/erosion effects, and density scaling toward the plateau.
- [ ] Strict Stone Forest–Highland Karst adjacency. Current multi-noise points are a prototype, not a guarantee.

## Volcanic Peak

- Current test summit is made from existing `rhyolite.png` (no new texture required for a first test).
- [ ] `scoria.png` + `SCORIA` (optional but desirable new dark porous volcanic rock).
- [ ] `pumice.png` + `PUMICE` (optional pale porous volcanic rock).
- [ ] `volcanic_ash.png` + `VOLCANIC_ASH` (optional summit sediment/ashfall).
- [ ] Optional `snowy_rhyolite` snow-overlay or use **vanilla snow/snow layers** (no new PNG required for vanilla snow).
- [ ] Cone/crater worldgen, rare mountain placement, distinct lower-to-upper rock strata, altitude-based snow cap, and treeless summit.
- [ ] Above-320 peak heights require an extended dimension and matching custom final density (experimental option included).

## Future resources already on the roadmap (not currently present)

- [ ] `dacite.png`, `DACITE` and building variants — upper volcanic stone.
- [ ] `chert.png`, `CHERT` — karst-associated sedimentary stone.
- [ ] `serpentinite.png`, `SERPENTINITE` — Schist-associated rocks.
- [ ] `jade.png`, `jade_ore_in_schist.png` — Jade item and host-rock ore; optional `jade_block.png`.
- [ ] `tektite.png`, `tektite_nodule.png` — impact glass and natural embedded deposit in Ashstone/Shatterstone.
- [ ] Optional ore textures for Elsewhere-specific metal-equivalent and ancient material.

## Other non-visual tasks

- [ ] Confirm Maple Forest elevation (Y≈65–70) and restore if needed.
- [ ] Make the Sundering Horizon more irregular without cave breaches.
- [ ] Audit vanilla geology and hostile/passive spawns across all biomes.
- [ ] Finish dimension portal, dimension cycle (Daybreak/Moonfall/Starless), biome atmosphere, and Underhang creature/vegetation features.
