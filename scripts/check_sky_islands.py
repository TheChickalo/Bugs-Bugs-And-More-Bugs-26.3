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
assert inner["type"]=="minecraft:max", "Sky density must be unioned after every surface and height cap"
assert inner["right"]=="bugsbugsandmorebugs:sky_island_density"
base=inner["left"]
assert base["type"]=="minecraft:lerp" and base["alpha"]=="bugsbugsandmorebugs:bamboo_lowland_region"
assert base["first"]["type"]=="minecraft:max"
assert base["first"]["right"]=="bugsbugsandmorebugs:volcanic_relief_density"
assert base["second"]["left"]["right"]=="bugsbugsandmorebugs:volcanic_relief_density"
assert json.dumps(base).count('"bugsbugsandmorebugs:sky_island_density"')==0, (
    "Sky density still appears inside a clipped terrain branch"
)
assert density["left"]["left"]["right"]==-0.50
# With the reference micro-noise set to zero, moderate/high mask values
# must produce solid sky terrain without modifying ordinary sea-level ground.
def reference_density(mask_strength, y):
    horizontal=2.45*mask_strength-0.50
    lower=-1.15+(min(192,max(149,y))-149)*(1.2+1.15)/(192-149)
    upper=1.4-(min(286,max(218,y))-218)*(1.4+1.35)/(286-218)
    return min(horizontal, lower, upper)
assert reference_density(0.55, 200)>0, "Moderate sky mask no longer creates blocks"
assert reference_density(0.0, 200)<0, "Air between islands must remain empty"
assert reference_density(1.0, 320)<0, "Islands must terminate above the sky band"
assert '"minecraft:overworld_large_biomes/continents"' in json.dumps(mask), "Large ocean gate missing"
assert '"bugsbugsandmorebugs:sky_island_mask"' in json.dumps(density)
found={entry["if_true"].get("biome_is") for entry in rules["sequence"] if isinstance(entry.get("if_true"),dict)}
assert sky <= found, "Sky surface rules absent"
print("Static sky island biome + density wiring passed (gameplay requires testing)")
