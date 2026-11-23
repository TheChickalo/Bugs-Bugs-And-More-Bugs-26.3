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
assert router["continents"]=="bugsbugsandmorebugs:elsewhere_biome_continents"
depth_router=router["depth"]
assert depth_router["type"]=="minecraft:range_choice", "Biome selection must follow actual solid sky density"
assert depth_router["input"]=="bugsbugsandmorebugs:sky_island_density"
assert depth_router["min_inclusive"]<=0 and depth_router["min_inclusive"]>=-0.05
assert depth_router["when_in_range"]==-1.8
assert depth_router["when_out_of_range"]["type"]=="minecraft:clamp"
assert depth_router["when_out_of_range"]["min"]==0, "Normal elevated land must never imitate sky depth"
assert depth_router["when_out_of_range"]["max"]==2, "Underhang remains available below the Sundering"
labels={row["label"] for row in settings["debug_functions"]}
for wanted in ("Sky mask","Sky solid density","Elsewhere climate depth","Unadjusted continents","Biome continents"):
    assert wanted in labels, f"Missing runtime density diagnostic {wanted}"
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

# Evaluate the actual JSON expression trees with controlled stand-in noise
# samples. This catches impossible island masks and accidental sky-biome leakage;
# it is NOT a replacement for sampling Minecraft's seeded noises in a game.
def evaluate(node, c, surface, secondary, y):
    if isinstance(node, (int,float)):
        return float(node)
    if isinstance(node, str):
        if node == "minecraft:overworld_large_biomes/continents":
            return c
        if node == "bugsbugsandmorebugs:sky_island_mask":
            return evaluate(mask, c, surface, secondary, y)
        if node == "bugsbugsandmorebugs:sky_island_density":
            return evaluate(density, c, surface, secondary, y)
        if node == "bugsbugsandmorebugs:elsewhere_large_depth":
            # Synthetic elevated-land worst case: the original baseline could
            # be strongly negative even outside the actual island mask.
            return -1.25
        raise AssertionError(f"Unexpected reference in sky test: {node}")
    typ = node["type"].removeprefix("minecraft:")
    def ev(k):
        return evaluate(node[k], c, surface, secondary, y)
    if typ=="noise":
        return {"minecraft:surface":surface,"minecraft:surface_secondary":secondary}[node["noise"]]
    if typ=="add":
        return ev("left")+ev("right")
    if typ=="sub":
        return ev("left")-ev("right")
    if typ=="mul":
        return ev("left")*ev("right")
    if typ=="min":
        return min(ev("left"),ev("right"))
    if typ=="max":
        return max(ev("left"),ev("right"))
    if typ=="abs":
        return abs(ev("input"))
    if typ=="clamp":
        return max(node["min"],min(node["max"],ev("input")))
    if typ=="gradient":
        assert node["axis"]=="y"
        start,end=node["from_coordinate"],node["to_coordinate"]
        t=max(0,min(1,(y-start)/(end-start)))
        return node["from_value"]+(node["to_value"]-node["from_value"])*t
    if typ=="range_choice":
        return ev("when_in_range") if node["min_inclusive"]<=ev("input")<node["max_exclusive"] else ev("when_out_of_range")
    if typ=="lerp":
        a=ev("alpha")
        return ev("first")*(1-a)+ev("second")*a
    raise AssertionError(f"Unexpected density type in sky check: {typ}")

sky_at_altitude=router["depth"]
assert sky_at_altitude["type"]=="minecraft:range_choice"
assert sky_at_altitude["input"]=="bugsbugsandmorebugs:sky_island_density"
for c,s1,s2,y,solid,sky_biome in (
    (-0.40,0.15,0.08,200,True,True),
    (-0.30,0.15,0.08,200,True,True),
    (-0.16,0.15,0.08,200,False,False),
    ( 0.30,0.15,0.08,200,False,False),
    (-0.40,0.00,0.00,200,False,False),
    (-0.40,0.15,0.08,125,False,False),
    (-0.40,0.15,0.08,310,False,False),
):
    land=evaluate(density,c,s1,s2,y)>0
    signal=evaluate(sky_at_altitude,c,s1,s2,y)<-0.8
    assert land==solid, (c,s1,s2,y,"terrain",land,solid)
    assert signal==sky_biome, (c,s1,s2,y,"biome",signal,sky_biome)

# In a hypothetical deep-ocean transect, the 2D noises should create some
# island patches and leave some open water; previous multiplicative mask
# effectively gave zero islands over most values.
from math import sin,cos,pi
transect=[
    evaluate(density,-0.45,0.19*sin(t*2*pi/256),0.15*cos(t*2*pi/97),200)>0
    for t in range(256)
]
assert 15 < sum(transect) < 200, f"Island mask is barren or a solid sky carpet: {sum(transect)}/256"

# Regression for the supplied screenshots: at X~275/Z~-81, Y100 and
# Y108 both reported different biomes, one of them a sky biome despite solid
# beach terrain. At these heights there must never be negative sky-depth.
for y in (60, 100, 108, 125, 140, 300, 360):
    actual=evaluate(sky_at_altitude, -0.4, .4, .4, y)
    assert actual>=0, (y, actual, "sky biome depth leaking outside the island layer")
# Even if vanilla mountain depth is -1.25, the non-island climate must
# remain >=0; underhang depth=2 remains possible at y=-80.
assert evaluate(sky_at_altitude, .45, .15, .08, 100)==0
assert evaluate(sky_at_altitude, .45, .15, .08, -80)==2


print("Static sky island biome + density wiring and mock-noise terrain tests passed (gameplay still unverified)")
