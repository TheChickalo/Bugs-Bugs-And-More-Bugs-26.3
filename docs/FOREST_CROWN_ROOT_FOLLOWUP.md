# Cedar / Willow / Persimmon / Mimosa tree-shape follow-up

This PR is a terrain/vegetation and tree-form update. The **Wisteria TreeFeature, Maple trunk/foliage placers, and Wisteria blossom decorator are untouched**.

## Ground cover
- `clubmoss` remains the original registered block ID but is now a full, solid, moss-like **cube**, rather than a carpet.
- New `clubmoss_lowlying` is a cross-shaped plant that survives only on top of `clubmoss`. The original `clubmoss_sprig` ID is retained for compatibility.
- Natural generation changes existing surface soil to `clubmoss`, then often places `clubmoss_lowlying` above. It does not build one-block-high piles of newly created soil.
- `liverwort` is a `LeafLitterBlock` like Fallen Autumn Leaves, with the `segment_amount=1..4` and horizontal `facing` states, individually patterned leaf-litter models, and 1–4 piece loot. Repeated placements expand the carpet patches.
- `clubmoss_lowlying` uses the new 16×16 texture path `textures/block/clubmoss_lowlying.png`. Existing `clubmoss.png` now needs to be a fully opaque block-face texture. `liverwort.png` continues to be a 16×16 image, now tiled as litter quadrants.
- Existing clubmoss carpets in saved chunks become full blocks under the new registry behavior.

## Tree crowns
- **Persimmon:** crooked fork elevations, unequal branch lengths, optional secondary twigs, small lobe-shaped clusters with imperfect edges.
- **Mimosa:** main trunk ends below the canopy; 2–3 uneven primary boughs fork into many short lateral branches, with flat, light leaf pads and gaps.
- **Willow:** uneven raised boughs and small secondary shoots, separated rounded leaf masses, independently descending hanging leaves.
- Secondary twigs now have a woody anchor to their parent's branch to prevent unsupported/diagonal-only connections.
- Leaf-distance repair is expanded to contain the wider species silhouettes, especially Mimosa and Willow.
- **Wisteria** was not restyled or reimplemented.

## Mangrove-style root generation
- Cedar and Willow use shared branching root-path logic inspired by mangrove root placement, **not the vanilla MangroveRootPlacer class**. Roots start against the trunk about 2–3 blocks above the ground, arch sideways in irregular paths, and descend onto soil; some roots fork near their anchored tips.
- Each path is validated before generation: if there is no acceptable landing soil or the route is blocked, it is skipped rather than producing orphan segments.
- Willow roots can extend into shallow water and are waterlogged, preserving existing water. Cedar roots are restricted to dry positions.
- The existing solid, low root geometry and bark textures remain. The 3-axis blockstates now cover both waterlogged states.

## Testing checklist — NOT YET RUN
1. `./gradlew runDatagen` and `./gradlew runClient`, and check first registry error if either fails.
2. Place `clubmoss`: verify a full collidable cube. Place `clubmoss_lowlying` on it and verify it breaks if the supporting block is removed.
3. Place `liverwort` repeatedly on the same block; verify one, two, three, and four coverage segments, rotations, and matching drop counts.
4. In newly generated Ancient Cedar Rainforest, inspect mossy bark, low root arches, liverwort, and clubmoss ground patches.
5. In newly generated Willow Backwaters, inspect branch asymmetry, leaf curtain density, root arches on slopes, and one-block-shallow water. Break submerged roots to verify water recovery.
6. In Persimmon Woodlands and Silkflower Savanna, compare tree silhouettes from multiple angles and inspect for floating leaf clusters after random ticks.
7. Grow Persimmon/Mimosa/Willow saplings manually, not just worldgen trees.
8. Verify Wisteria silhouettes and blossom strands have not changed.
9. Check `clubmoss_lowlying.png` and any other uncommitted forest PNGs are supplied; the PR contains model paths but **no new hand-painted texture assets**.

These checks are a QA plan, not claims of completed testing. The new generation affects ungenerated chunks only, not existing trees.
