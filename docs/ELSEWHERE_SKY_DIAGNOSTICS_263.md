# Sky island and shoreline diagnosis — Minecraft 26.3

## User-observed failure
At X≈275, Z≈−81 in the Elsewhere, F3 reported Mistfall Gardens at **Y108** and Ocean at **Y100** on almost identical sandy/rocky land. Those heights are below the intended floating-island altitude (roughly Y149–286). They demonstrate an erroneous biome label, **not** the absence of terrain at Y200.

## Causes identified
1. Normal Elsewhere height/offset depth can become negative on elevated land, and the old sky-biome depth function let that baseline value compete with sky biome targets.
2. Even at **normal depth 0**, the sky biome targets' huge temperature/humidity/erosion/weirdness ranges could beat all regular biome targets. In deterministic synthetic tests, the old sky targets yielded 586 sky matches among 5,000 ordinary-depth samples.

## Corrected depth gating and target priority
- **Physical island density** now controls sky biome depth. `sky_island_density >= -0.01` selects climate depth **-1.8**, close to real sky-island stone; otherwise the previous land/underhang baseline is clamped to [0,2].
- All four sky biome climate targets use depth range **[-2.0,-1.5]** instead of [-2.0,-0.9]. This keeps their broad ecology selection over actual islands but makes them too distant to beat normal biomes at depth 0.
- Multi-noise nearest-target regression tests cover several ocean/coast/inland continentalness values with temperature/humidity/erosion/weirdness varied, as well as selection of each of the four actual sky biomes at depth -1.8.
- The physical terrain mask, island size, and ocean/rivers are **unchanged** pending live diagnostics; this PR must not be presented as proof that solid islands now generate.

## Built-in 26.3 runtime diagnostics
The Elsewhere noise settings expose five new debug metrics through **Chunk Generation** in Minecraft's F3 overlay (configure with **F3 + F6**):
- **Sky mask**: a 2D island-candidate signal; zero indicates a forbidden location (typically mainland).
- **Sky solid density**: should be positive inside a generated island and negative in empty air at the sampled Y.
- **Elsewhere climate depth**: 0 on normal land and near -1.8 in qualifying sky-island stone.
- **Unadjusted continents**: large-biome continental noise used by physical terrain.
- **Biome continents**: coast-adjusted value used for biome selection, which may differ near sea level.
The previous "Final density" debug label actually showed **vanilla base density**, not modded final density, so it is renamed to avoid misleading readings.

## In-game test procedure (required before merge)
1. **Back up the save and create a fresh test world**. Old biome sections and terrain chunks persist across code updates.
2. In the Elsewhere visit X275/Z-81 at Y100 and Y108; the sky biome label must be absent on beach land in freshly generated chunks.
3. Move to Y205 and inspect **Sky mask** and **Sky solid density**. If the density is negative, the column isn't an island. If positive in a fresh generated chunk, rock should be present.
4. From Y205 run `/locate biome bugsbugsandmorebugs:mistfall_gardens` (or the other sky biomes). At the returned X/Z, compare Y180–250 density readings with actual blocks, and see whether the island really floats over ocean.
5. Compare the **Unadjusted continents** and **Biome continents** values at a shoreline where Ocean labels dry land. That will distinguish a coast-climate mapping error from terrain that physically rises above its intended ocean basin.
6. Compare multiple seeds. Report the **seed, XYZ and debug values** for any continued mismatch.

The tests simulate climate selection and expression-tree outputs; they do NOT instantiate actual seeded Minecraft chunks. Do not merge this diagnostic PR based on CI alone.
