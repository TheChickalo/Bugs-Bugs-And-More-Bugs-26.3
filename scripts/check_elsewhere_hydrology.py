#!/usr/bin/env python3
"""Verify shared vanilla Large Biomes geography and balanced Elsewhere biomes.

The real world-seeded layout and biome percentages still require in-game sampling.
"""
import json
import random
from collections import Counter
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
NS="bugsbugsandmorebugs:"
DATA=ROOT/"src/main/resources/data/bugsbugsandmorebugs"
WORLD=DATA/"worldgen"
dim=json.loads((DATA/"dimension/elsewheredim.json").read_text())
settings=json.loads((WORLD/"noise_settings/elsewhere.json").read_text())
legacy=json.loads((WORLD/"density_function/elsewhere_inland_legacy.json").read_text())
rows=dim["generator"]["biome_source"]["biomes"]
router=settings["noise_router"]

# Same vanilla Large-Biomes continentalness in BOTH biome selection and
# physical land/water; no separate coast warp and no invented river depth pulse.
continents="minecraft:overworld_large_biomes/continents"
vanilla="minecraft:overworld_large_biomes/final_density"
assert settings["sea_level"]==63
assert router["continents"]==continents
assert router["erosion"]=="minecraft:overworld_large_biomes/erosion"
assert router["ridges"]=="minecraft:overworld/ridges"
assert settings["aquifers"]["surface_level"]=="minecraft:overworld_large_biomes/preliminary_surface_level"
assert router["temperature"]=="minecraft:overworld/temperature"
assert router["vegetation"]=="minecraft:overworld/vegetation"

# The ocean and rivers inherit the actual vanilla terrain density;
# only away from both coast and PV valleys is custom inland relief blended in.
root=router["final_density"]
assert root["type"]=="minecraft:range_choice"
sky_union=root["when_in_range"]
assert sky_union["type"]=="minecraft:max"
assert sky_union["right"]==NS+"sky_island_density"
land=sky_union["left"]
assert land["type"]=="minecraft:lerp"
assert land["first"]==vanilla
assert land["second"]==NS+"elsewhere_inland_legacy"
blend=land["alpha"]
assert blend["type"]=="minecraft:mul"
coast=blend["left"]
valley=blend["right"]
assert coast["type"]=="minecraft:clamp" and valley["type"]=="minecraft:clamp"
assert coast["input"]["left"]=={"type":"minecraft:sub","left":continents,"right":0.03}
assert valley["input"]["left"]["left"]=={"type":"minecraft:abs","input":"minecraft:overworld/ridges"}
assert valley["input"]["left"]["right"]==0.085
assert legacy["type"]=="minecraft:lerp", "Retain custom karst/stone forest/volcanic relief"
assert legacy["alpha"]==NS+"bamboo_lowland_region"
assert NS+"stone_forest_region" in json.dumps(legacy)
assert NS+"volcanic_relief_density" in json.dumps(legacy)

def amount(c,weird):
    inland=max(0,min(1,(c-0.03)*5))
    valley=max(0,min(1,(abs(weird)-0.085)*8))
    return inland*valley
assert amount(-0.5,0.8)==0, "Oceans must remain vanilla density"
assert amount(-0.15,0.8)==0, "Shores must remain vanilla density"
assert amount(0.6,0)==0, "River valley must remain vanilla density"
assert amount(0.6,0.4)>0.99, "Far-inland custom geology must remain available"

depth=router["depth"]
assert depth["type"]=="minecraft:range_choice"
assert depth["input"]==NS+"sky_island_density"
assert depth["when_in_range"]==-1.8 and depth["max_exclusive"]>=3
normal=depth["when_out_of_range"]
assert normal["type"]=="minecraft:range_choice"
assert normal["when_in_range"]==2
assert normal["when_out_of_range"]==0
assert normal["max_exclusive"]==-55

# Vanilla continental boundaries. Biome chance is the share of actual
# climate volume, NOT the number of registry entries.
ocean=[x for x in rows if x["biome"]==NS+"ocean"]
river=[x for x in rows if x["biome"]==NS+"river"]
assert len(ocean)==4
assert all(x["parameters"]["continentalness"]==[-1.2,-0.19] for x in ocean)
assert all(x["parameters"]["weirdness"]==[-1,1] for x in ocean)
assert 50<=len(river)<=130
assert all(x["parameters"]["weirdness"]==[-0.045,0.045] for x in river)
assert all(x["parameters"]["depth"]==0 for x in river)
assert all(x["parameters"]["continentalness"][0]>=-0.11 for x in river)
assert any(x["biome"]==NS+"ginkgo_forest" for x in rows)
assert any(x["biome"]==NS+"maple_forest" for x in rows)
assert any(x["biome"]==NS+"ancient_cedar_rainforest" for x in rows)
assert len(rows)>=500
for region in ((-0.19,-0.11),(-0.11,0.03),(0.03,0.3),(0.3,1.0)):
    assert any(x["biome"]!=NS+"river" and x["parameters"]["continentalness"]==list(region) for x in rows),region
for i,row in enumerate(rows):
    p=row["parameters"]
    for axis in ("temperature","humidity","continentalness","erosion","weirdness","depth"):
        vals=p[axis] if isinstance(p[axis],list) else [p[axis]]
        assert all(-2<=v<=2 for v in vals),(i,axis,vals)
        if len(vals)==2:
            assert vals[0]<=vals[1],(i,axis,vals)
    assert 0<=p["offset"]<=1

# Synthetic nearest-hypercube climate selection. This tests the true
# selection algorithm (but not world noise correlations or exact percentages).
keys=("temperature","humidity","continentalness","erosion","depth","weirdness","offset")
prepared=[]
for row in rows:
    dims=[]
    for key in keys:
        v=row["parameters"][key]
        dims.append((v[0],v[1]) if isinstance(v,list) else (v,v))
    prepared.append((row["biome"],dims))
def nearest(p):
    best_name,best_score=None,float("inf")
    values=[p[k] for k in keys]
    for name,dim in prepared:
        score=0
        for value,(lo,hi) in zip(values,dim):
            delta=lo-value if value<lo else value-hi if value>hi else 0
            score+=delta*delta
        if score<best_score:
            best_name,best_score=name,score
    return best_name
rng=random.Random(20261125)
deep=Counter()
main=Counter()
valleys=Counter()
dry=Counter()
for _ in range(450):
    climate={
      "temperature":rng.uniform(-1,1),"humidity":rng.uniform(-1,1),
      "continentalness":rng.uniform(-1,-0.22),
      "erosion":rng.uniform(-1,1),"depth":0,
      "weirdness":rng.uniform(-1,1),"offset":0}
    deep[nearest(climate)]+=1
    climate["continentalness"]=rng.uniform(-0.11,1)
    main[nearest(climate)]+=1
    climate["continentalness"]=rng.uniform(0.03,0.29)
    climate["erosion"]=rng.uniform(-0.1,0.35)
    climate["weirdness"]=rng.uniform(-0.04,0.04)
    valleys[nearest(climate)]+=1
    climate["weirdness"]=rng.choice((-1,1))*rng.uniform(0.15,0.9)
    dry[nearest(climate)]+=1
assert deep[NS+"ocean"]>=440,("Deep ocean was assigned land",deep.most_common(3))
assert main[NS+"ocean"]==0,("Dry inland mislabelled Ocean",main[NS+"ocean"])
assert valleys[NS+"river"]>=390,("Rivers absent from carved valley climates",valleys.most_common(3))
assert dry[NS+"river"]<=15,("Rivers appearing well outside valleys",dry[NS+"river"])
assert main[NS+"ginkgo_forest"]<55,("Ginkgo is dominating climate volume",main[NS+"ginkgo_forest"])
main_ecology=[(k,v) for k,v in main.items() if v>=3 and k not in (NS+"river",NS+"ocean")]
assert len(main_ecology)>=12,("Too little biome variety",main_ecology)
sky=("cloudcedar_forest","windcarved_crags","mistfall_gardens","vermilion_isles")
for name in sky:
    assert any(x["biome"]==NS+name and x["parameters"]["depth"]==[-2,-1.5] for x in rows),name
assert sum(x["biome"]==NS+"underhang" for x in rows)>0

print("Vanilla Large Biomes ocean/river coupling, custom inland terrain and climate coverage checks passed.")
print("Synthetic inland biome mix:",main.most_common(12))
print("Ocean  %s/450, river valley %s/450, dry valleys %s/450"%
      (deep[NS+"ocean"],valleys[NS+"river"],dry[NS+"river"]))
