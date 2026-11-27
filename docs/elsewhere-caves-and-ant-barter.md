# Elsewhere cave biomes — first pass

This branch builds on PR #63. It adds seven data-pack cave biomes above the Sundering Horizon, using existing blocks. The biome selection is an initial absolute-height plus vanilla climate/depth approximation, and the terrain cave noise is still the vanilla-derived restoration in #63. The surface, sky and Underhang retain their existing depth channels. Existing ore injection is unchanged. The limestone terrace feature is intentionally a small, coarse prototype; actual basin frequency, water containment and appearance require in-game testing. New biome/feature definitions are hand-authored in main resources, not generated. Run datagen and a clean build to confirm there are no duplicate resources.

## Next blocks (not implemented)
- Flowstone: natural cream layered deposit; optional weathered variant.
- Rimstone: pale deposit block, slab and thin water-retaining rim (waterlogging and placement states to design).
- Limestone speleothem: tapered stalactite/stalagmite, multi-segment and connectable; optionally flowstone columns.
- Cave silt/sediment: distinguish dry fine material from existing wet silt only if needed.
- Cave root strands and hanging moss variants; pale cave fungal mats and glowshroom clusters (existing glowshroom used now).
- Later: ancient eclogite masonry, carved glyphs, fractured eclogite and ancient artifacts. Eclogite generation is explicitly outside this PR.

## Ant bartering design (not implemented)
Persistent independently sampled personality traits: friendliness, business acumen, generosity, secretiveness, curiosity, loyalty, patience, sentimentality. Separate player-to-ant trust and player-to-colony reputation. Offer budget = sum(quantity × base value × colony demand × personal preference) × clamp((1 + .25 trust + .15 reputation + .2 generosity)/(1 + .25 business acumen), .65, 1.6). Values normalized; demand and preference multipliers bounded. Low business acumen also increases deterministic valuation uncertainty. Secretiveness gates which offers are revealed rather than directly changing prices. Inventory-backed, stable counteroffers; commit atomically, update stock and demand, and persist per-player relationship. No trade reroll by reopening the interface.

## Validation checklist
- Generate new chunks; check all seven biome IDs with /locate biome and inspect F3 at depth.
- Verify river, estuary and ocean surfaces keep sediment, but caves beneath do not receive sand or grassy loam.
- Check fresh chunks in Stone Forest, mountains, rivers and oceans; verify no change to the eclogite stratum or void.
- Inspect the terrace prototype with water updates enabled, especially chunk boundaries; compare with reference rimstone pools.
- Verify natural mob behavior, ore generation and feature ordering remain consistent.
- Run clean build, datagen and registry load before merge.
