# One-block flooded rice paddy test notes

This update intentionally changes rice planting and growing, not food recipes, crop drops, growth ages, or generation.

- Set tilled loam at Y=64; use a water bucket to create a source at Y=65; leave Y=66 as air.
- Use rice seeds on the flooded soil through its water block; rice should replace water visually with its submerged stem, without draining that source.
- Bonemeal or wait until it grows through ages 0..6; at ages 3 and 5, new parts should appear at Y=66 and Y=67.
- Try putting a second water source at Y=66 before planting: placement should be refused. Likewise for a one-deep source above mud, standard farmland, or dry tilled loam.
- Break its top part when mature: expect one loot roll and the water block back at Y=65.
- Watch for tilled loam staying tilled/hydrated beneath flooded roots, and for accidental water removal when breaking the base.

Run `./gradlew runClient` before merging. This change has not been play-tested in Minecraft.
