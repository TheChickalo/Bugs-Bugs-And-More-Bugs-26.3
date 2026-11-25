#!/usr/bin/env python3
"""Regression tests for recovering legacy Stone Forest relief and Snowy Peak climate.

Mock density and climate selection checks do not prove real seeded chunk shapes.
"""
import json
from pathlib import Path
from collections import Counter
import random

ROOT=Path(__file__).resolve().parents[1]
NS="bugsbugsandmorebugs:"
DATA=ROOT/"src/main/resources/data/bugsbugsandmorebugs"
WORLD=DATA/"worldgen"
read=lambda rel: json.loads((WORLD/rel).read_text())
settings=read("noise_settings/elsewhere.json")
stone=read("density_function/stone_forest_roof.json")
mask=read("density_function/stone_forest_spires_region.json")
region=read("density_function/stone_forest_region.json")
dim=json.loads((DATA/"dimension/elsewheredim.json").read_text())
rows=dim["generator"]["biome_source"]["biomes"]

# No shortcut through a generic legacy mix: the precise original pillar and
# creek-floor density MUST take over the terrain directly in Stone Forest cores.
union=settings["noise_router"]["final_density"]["when_in_range"]
assert union["type"]=="minecraft:max" and union["right"]==NS+"sky_island_density"
base=union["left"]
assert base["type"]=="minecraft:lerp"
assert base["second"]==NS+"stone_forest_roof"
assert base["first"]["first"]=="minecraft:overworld_large_biomes/final_density"
assert base["first"]["second"]==NS+"elsewhere_inland_legacy"
assert base["alpha"]["type"]=="minecraft:clamp"
assert base["alpha"]["input"]["left"]==NS+"stone_forest_spires_region"
assert base["alpha"]["input"]["right"]>=1.25
assert region["type"]=="minecraft:max" and region["right"]==NS+"stone_forest_spires_region"

def evaluate(node, p, noises, y):
    if isinstance(node,(int,float)):
        return float(node)
    if isinstance(node,str):
        return p[node]
    typ=node["type"].removeprefix("minecraft:")
    ev=lambda key:evaluate(node[key],p,noises,y)
    if typ=="noise":
        return noises.get(node["noise"],0.0)
    if typ=="gradient":
        a,b=node["from_coordinate"],node["to_coordinate"]
        t=max(0.0,min(1.0,(y-a)/(b-a)))
        return node["from_value"]+(node["to_value"]-node["from_value"])*t
    if typ=="mul":
        return ev("left")*ev("right")
    if typ=="add":
        return ev("left")+ev("right")
    if typ=="sub":
        return ev("left")-ev("right")
    if typ=="min":
        return min(ev("left"),ev("right"))
    if typ=="max":
        return max(ev("left"),ev("right"))
    if typ=="abs":
        return abs(ev("input"))
    if typ=="clamp":
        return max(node["min"],min(node["max"],ev("input")))
    if typ=="lerp":
        a=ev("alpha")
        return (1-a)*ev("first")+a*ev("second")
    raise AssertionError("Unexpected density opcode "+typ)

land={
 "minecraft:overworld_large_biomes/continents":0.63,
 "minecraft:overworld_large_biomes/erosion":-0.47,
 "minecraft:overworld/temperature":-0.24,
 "minecraft:overworld/vegetation":0.34,
}
def region_strength(**patch):
    return evaluate(mask,{**land,**patch},{},145)
assert region_strength() > 0.96, "Suitable rugged, humid highlands must generate Stone Forest pillars"
assert region_strength(**{"minecraft:overworld_large_biomes/erosion":-0.55}) > 0.95
assert region_strength(**{"minecraft:overworld_large_biomes/continents":-0.30})==0, (
    "Stone Forest pillars must not disrupt the ocean"
)
assert region_strength(**{"minecraft:overworld/vegetation":-0.70})==0, (
    "Dry mountain biomes must not become Stone Forests"
)
assert region_strength(**{"minecraft:overworld/temperature":-0.99})==0, (
    "Extreme snowy climates must remain mountain terrain"
)
assert region_strength(**{"minecraft:overworld_large_biomes/erosion":0.8})==0, (
    "Flat high-erosion terrain must not be overwritten with pillars"
)

pillars={
 "minecraft:badlands_surface":0.0,
 "minecraft:badlands_pillar":0.6,
 "minecraft:surface_secondary":0.2,
 "minecraft:surface":0.0,
}
valleys={
 "minecraft:badlands_surface":0.8,
 "minecraft:badlands_pillar":0.8,
 "minecraft:surface_secondary":0.8,
 "minecraft:surface":0.8,
}
for y in (95,125,145):
    assert evaluate(stone,land,pillars,y)>0,(y,"Original rocky pillars missing")
    assert evaluate(stone,land,valleys,y)<0,(y,"Original gaps between pillars missing")
assert evaluate(stone,land,valleys,52)>0, "Gorge floors must still form under creek water"
assert evaluate(stone,land,pillars,210)<0, "Rock columns cannot become an endless ceiling"

snow=[r for r in rows if r["biome"]==NS+"snowy_peak"]
assert len(snow)>=5
assert all(r["parameters"]["temperature"]==[-1,-0.35] for r in snow)
assert all(r["parameters"]["humidity"]==[-1,-0.13] for r in snow)
assert all(r["parameters"]["continentalness"]==[0.24,1] for r in snow)
assert all(r["parameters"]["erosion"][0]<=-0.23 for r in snow)
# Keep Stone Forests in wet-temperate far-inland; Snowy Peaks need cold/dry conditions.
forest=[r for r in rows if r["biome"]==NS+"stone_forest"]
assert len(forest)>=18
assert all(r["parameters"]["continentalness"][0]>=0.3 for r in forest)

keys=("temperature","humidity","continentalness","erosion","depth","weirdness","offset")
prepared=[]
for r in rows:
    p=r["parameters"]
    prepared.append((r["biome"],[
        (p[k][0],p[k][1]) if isinstance(p[k],list) else (p[k],p[k]) for k in keys
    ]))
def locate(p):
    vals=[p[k] for k in keys]
    best,best_dist=None,float("inf")
    for name,axis in prepared:
        dist=0.0
        for value,(lo,hi) in zip(vals,axis):
            excess=lo-value if value<lo else value-hi if value>hi else 0
            dist+=excess*excess
        if dist<best_dist:
            best,best_dist=name,dist
    return best

rng=random.Random(20261125)
cold=Counter()
humid=Counter()
for _ in range(260):
    common={"continentalness":rng.uniform(.34,.85),
            "erosion":rng.uniform(-.85,-.25),"depth":0,
            "weirdness":rng.choice((-1,1))*rng.uniform(.12,.9),"offset":.028}
    cold[locate({**common,"temperature":rng.uniform(-.80,-.40),
                 "humidity":rng.uniform(-.85,-.22)})]+=1
    humid[locate({**common,"temperature":rng.uniform(-.36,.20),
                  "humidity":rng.uniform(.17,.82)})]+=1
assert cold[NS+"snowy_peak"]>=140, ("Snowy Peak still impractically rare in its intended climate",cold.most_common(5))
assert humid[NS+"stone_forest"]>=40,("Stone Forest missing in temperate humid highland climate",humid.most_common(5))
assert humid[NS+"snowy_peak"]==0,("Snowy Peak displaced the temperate Stone Forest",humid)
print("Stone Forest original pillar/gorge profile, biome-aligned masks, coast protection and snowy highlands passed.")
print("Cold rugged sample:",cold.most_common(5))
print("Humid rugged sample:",humid.most_common(5))
