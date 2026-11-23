#!/usr/bin/env python3
"""Static contract check for sky archipelago noise/biome wiring. Not an in-game test."""
import json
from pathlib import Path
root=Path(__file__).resolve().parents[1]
p=root/"src/main/resources/data/bugsbugsandmorebugs"
dim=json.loads((p/"dimension/elsewheredim.json").read_text())
settings=json.loads((p/"worldgen/noise_settings/elsewhere.json").read_text())
mask=json.loads((p/"worldgen/density_function/sky_island_mask.json").read_text())
density=json.loads((p/"worldgen/density_function/sky_island_density.json").read_text())
rules=json.loads((p/"worldgen/material_rule/elsewhere.json").read_text())
names={e["biome"] for e in dim["generator"]["biome_source"]["biomes"]}
sky={"bugsbugsandmorebugs:"+name for name in ("cloudcedar_forest","windcarved_crags","mistfall_gardens","vermilion_isles")}
assert sky <= names, "Missing sky multi-noise entries"
router=settings["noise_router"]
assert router["depth"]["type"]=="minecraft:lerp"
root=router["final_density"]
assert root["type"]=="minecraft:range_choice"
inner=root["when_in_range"]
assert inner["type"]=="minecraft:lerp"
assert inner["first"]["type"]=="minecraft:max"
assert inner["first"]["right"]=="bugsbugsandmorebugs:volcanic_relief_density"
assert inner["first"]["left"]["right"]=="bugsbugsandmorebugs:sky_island_density"
assert inner["second"]["left"]["left"]["right"]=="bugsbugsandmorebugs:sky_island_density"
assert '"minecraft:overworld_large_biomes/continents"' in json.dumps(mask), "Large ocean gate missing"
assert '"bugsbugsandmorebugs:sky_island_mask"' in json.dumps(density)
found={entry["if_true"].get("biome_is") for entry in rules["sequence"] if isinstance(entry.get("if_true"),dict)}
assert sky <= found, "Sky surface rules absent"
print("Static sky island biome + density wiring passed (gameplay requires testing)")
