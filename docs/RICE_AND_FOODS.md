# Rice and paddy foods

Rice seeds are currently **Creative-only** (no wild placement or natural world generation).

Rice is a **flooded paddy crop**, not a normal dry-field crop.

**Underwater hoeing:** use a hoe on loam, grassy loam, coarse loam, or rooted loam while there is exactly one source-water block above the soil, with air above the water. This produces hydrated tilled loam. Normal dry-field hoeing still works as before. To plant it, stack these blocks vertically:

1. **Air**, for the leaves to emerge into.
2. **Exactly one water source block**, where you use rice seeds. The root occupies this block and remains waterlogged instead of removing the water.
3. **Tilled loam**, directly underneath the water.

The water must be a source, not flowing water. Rice **cannot be planted on ordinary farmland, mud, grassy loam, dry tilled loam, or under two blocks of water**. As the crop matures, the second and third crop sections replace air above the submerged base. Both normal growth and bonemeal require the flooded root over tilled loam.

### Manual checks
- Seed planting succeeds in one-deep source water on tilled loam, with air above.
- Placement fails in dry air, flowing water, two-deep water, on normal farmland/mud, or with a non-air block immediately above water.
- Natural ticks and bonemeal grow a plant to heights 1, 2, then 3; the root remains waterlogged.
- Breaking a fully grown upper stalk grants one harvest and restores water at the root.
- Removing tilled loam beneath the crop breaks it and restores water.
- No natural rice generation is registered.

- Age 0–2: one block tall; age 3–4: two blocks; age 5–6: three blocks.
- Breaking any part gives a single harvest; maturity gives 2–4 raw rice plus 1–3 seeds; immature crops drop one seed.
- 1 raw rice -> 1 cooked rice (furnace 200 ticks, smoker 100, campfire 600).
- 2 cooked rice + 1 dried kelp -> 2 onigiri.
- 2 cooked rice + 1 carrot + 1 egg -> 1 fried rice.
- 2 cooked rice + 1 sugar -> 2 mochi.

Food nutrition points: cooked rice 3, onigiri 4, fried rice 8, mochi 3. All crop and food item textures are exactly 16x16 PNGs in `assets/bugsbugsandmorebugs/textures`.

Recipes and recipe-book advancements are hand-authored in `src/main/resources/data`; crop loot/table/tag generation remains part of existing Fabric datagen. No rice spawning feature or biome placement is introduced.
