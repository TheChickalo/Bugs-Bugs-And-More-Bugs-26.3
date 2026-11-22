#!/usr/bin/env python3
"""Static checks for the temporary biome-only Elsewhere alpha contract.

Retire or update the empty-spawn assertions when custom mob implementation begins.
This does not replace compiling, running a game client, or exploring new chunks.
"""
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
NS = "bugsbugsandmorebugs:"
DATA = ROOT / "src/main/generated/data"
WORLD = DATA / "bugsbugsandmorebugs/worldgen"
LOOT = DATA / "bugsbugsandmorebugs/loot_table/blocks"


def read(path: Path) -> dict:
    return json.loads(path.read_text(encoding="utf-8"))


# No biome-natural animals or monsters in the pre-custom-mobs alpha.
biomes = sorted((WORLD / "biome").glob("*.json"))
assert len(biomes) >= 43, f"Expected the full Elsewhere biome set, got {len(biomes)}"
for file in biomes:
    spawns = read(file)["attributes"]["minecraft:gameplay/natural_mob_spawns"]["argument"]["spawns_by_category"]
    assert not any(spawns.values()), f"Unintended biome mob spawns in {file.name}: {spawns}"

# Filter after the last random spatial shift, on each candidate position.
for name in ("maple_bush", "maple_short_grass", "maple_tall_grass", "maple_large_fern", "maple_red_shrub"):
    chain = read(WORLD / "placed_feature" / f"{name}_placed.json")["placement"]
    types = [modifier["type"] for modifier in chain]
    assert types.count("minecraft:biome") == 1, f"Biome filter count in {name}"
    assert types.index("minecraft:biome") > types.index("minecraft:offset"), f"Biome check too early: {name}"

# Breaking the potted plant returns both items; other absent drops are intentional.
pot = read(LOOT / "potted_glowshroom.json")
drops = {entry["name"] for pool in pot["pools"] for entry in pool["entries"]}
assert drops == {"minecraft:flower_pot", NS + "glowshroom"}, drops
assert not (LOOT / "infested_dirt.json").exists(), "Infested dirt intentionally drops nothing"
assert not (LOOT / "glowshroom_block.json").exists(), "Huge cap harvest reserved for a future hoe mechanic"

# Vanilla wood-family mining, behavior and fire registration must cover complete existing sets.
tagroot = DATA / "minecraft/tags/block"
axe = set(read(tagroot / "mineable/axe.json")["values"])
families = ("maple", "ginkgo", "hinoki")
components = ("log", "wood", "stripped_log", "stripped_wood", "planks", "stairs",
              "slab", "door", "trapdoor", "button", "pressure_plate", "fence", "fence_gate")
for family in families:
    expected = {
        NS + ("stripped_" + family + "_" + part[9:] if part.startswith("stripped_")
              else family + "_" + part)
        for part in components
    }
    assert expected <= axe, f"Missing axe mining tags for {family}: {sorted(expected - axe)}"
    assert NS + family + "_stairs" in read(tagroot / "wooden_stairs.json")["values"]
    assert NS + family + "_slab" in read(tagroot / "wooden_slabs.json")["values"]
    assert NS + family + "_pressure_plate" in read(tagroot / "wooden_pressure_plates.json")["values"]

assert NS + "hinoki_planks" in read(tagroot / "planks.json")["values"]

# These are retained as dormant candidates, not accidentally reattached.
for name in ("maple_lake_placed", "lake_pond_placed"):
    assert (WORLD / "placed_feature" / f"{name}.json").exists()
print(f"Biome-alpha checks passed: {len(biomes)} empty-spawn biomes, safe Maple offsets, pot loot, wood tags.")
