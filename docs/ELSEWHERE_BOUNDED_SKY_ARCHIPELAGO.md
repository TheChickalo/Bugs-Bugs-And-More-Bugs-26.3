# Elsewhere bounded, varied-height floating archipelago

The previous sky terrain used a continuous noise threshold and could create extremely large connected landmasses. This implementation uses a seeded spatial lattice instead.

- A custom Minecraft 26.3 DensityFunction node, bugsbugsandmorebugs:bounded_sky_islands, computes discrete islands independent of chunk generation order.
- Cell spacing: 224 blocks. Max radius: 72 blocks; hard horizontal diameter maximum: **144 blocks**. Centre jitter: at most 20 blocks, giving **at least 40 blocks** between neighbouring islands.
- 22% of cells are intentionally empty, with island radii 18-72 blocks and smaller islands more common.
- Each island's top is randomized approximately between Y180 and Y277. The underside is much thinner at the edges, drops deeply near the core, and adds three pointed drooping formations.
- Both the 2D biome mask and 3D solid terrain use the SAME named world-seeded island lattice.
- Island mass is allowed only over sufficiently deep ocean continentalness. No terrain, rice, river, climate or rainfall changes outside the archipelago.

## Verification
Build and datapack validation, custom density function codec and both worldgen scripts must pass. Then test new Elsewhere chunks in multiple seeds.

Expected: visibly separate islands, no single island wider than 144 blocks, clear gaps between neighbours, substantially different island altitudes and spiky tapering undersides. Check F3 biome names on the upper surfaces and that lower ocean / coastline generation is unchanged.

This PR is a draft until actual in-game testing. Java compilation and tests do not prove that arbitrary seeds look natural.
