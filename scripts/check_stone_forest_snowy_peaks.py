#!/usr/bin/env python3
"""Protect the actual 26.3 Stone Forest silhouette and snowy mountain availability.

This inspects the live density JSON expressions and biome hypercube selection.
It cannot replace actual generated chunk inspection or measure seeded frequencies.
"""
import json
import math
import random
from pathlib import Path
from collections import Counter

root=Path(__file__).resolve().parents[1]
world=root/"src/main/resources/data/bugsbugsandmorebugs/worldgen"
dim=json.loads((root/"src/main/resources/data/bugsbugsandmorebugs/dimension/elsewheredim.json").read_text())
settings=json.loads((world/"noise_settings/elsewhere.json").read_text())
stoneMask=json.loads((world/"density_function/stone_forest_restoration_region.json").read_text())
roof=json.loads((world/"density_function/stone_forest_roof.json").read_text())
snowMask=json.loads((world/"density_function/snowy_peak_region.json").read_text())
snowDensity=json.loads((world/"density_function/snowy_peak_density.json").read_text())
router=settings["noise_router"]
ns="bugsbugsandmorebugs:"
r=router["final_density"]
assert r["when_in_range"]["type"]=="minecraft:max"
assert r["when_in_range"]["right"]==ns+"sky_island_density"
terrain=r["when_in_range"]["left"]
assert terrain["type"]=="minecraft:max"
assert terrain["right"]==ns+"snowy_peak_density"
stone=terrain["left"]
assert stone["type"]=="minecraft:lerp"
assert stone["alpha"]==ns+"stone_forest_restoration_region"
assert stone["second"]==ns+"stone_forest_roof"
assert stone["first"]["first"]=="minecraft:overworld_large_biomes/final_density"
assert stone["first"]["second"]==ns+"elsewhere_inland_legacy"
assert stoneMask["type"]=="minecraft:clamp"
assert "bugsbugsandmorebugs:karst_massif_region" in json.dumps(stoneMask)
assert "minecraft:overworld_large_biomes/continents" in json.dumps(stoneMask)
assert "minecraft:overworld_large_biomes/erosion" in json.dumps(stoneMask)
assert "minecraft:overworld/temperature" in json.dumps(snowMask)
assert "minecraft:overworld/vegetation" in json.dumps(snowMask)
assert "minecraft:overworld_large_biomes/continents" in json.dumps(snowMask)
assert "minecraft:overworld_large_biomes/erosion" in json.dumps(snowMask)

# The original limestone-pillar noise is unchanged. Verify its actual expression
# can create tall stone columns AND low, water-filled valleys at the same sea level.
def ev(n,values):
    if isinstance(n,(int,float)):return float(n)
    if isinstance(n,str):
        if n in values: return values[n]
        raise AssertionError("Unexpected external density ref "+n)
    kind=n["type"].removeprefix("minecraft:")
    if kind=="clamp":return max(n["min"],min(n["max"],ev(n["input"],values)))
    if kind=="noise":return values[n["noise"]]
    if kind=="abs":return abs(ev(n["input"],values))
    if kind=="add":return ev(n["left"],values)+ev(n["right"],values)
    if kind=="sub":return ev(n["left"],values)-ev(n["right"],values)
    if kind=="mul":return ev(n["left"],values)*ev(n["right"],values)
    if kind=="min":return min(ev(n["left"],values),ev(n["right"],values))
    if kind=="max":return max(ev(n["left"],values),ev(n["right"],values))
    if kind=="lerp":
        a=ev(n["alpha"],values)
        return ev(n["first"],values)*(1-a)+ev(n["second"],values)*a
    if kind=="gradient":
        assert n["axis"]=="y"
        y=values["y"]
        t=max(0,min(1,(y-n["from_coordinate"])/(n["to_coordinate"]-n["from_coordinate"])))
        return n["from_value"]+(n["to_value"]-n["from_value"])*t
    raise AssertionError("Cannot evaluate density expression "+kind)

base={
    "minecraft:overworld_large_biomes/continents":0.58,
    "minecraft:overworld_large_biomes/erosion":0.24,
    "minecraft:overworld/temperature":-0.38,
    "minecraft:overworld/vegetation":-0.36,
    "minecraft:surface":0.0,
    "minecraft:surface_secondary":0.0,
    "minecraft:badlands_surface":0.0,
    "minecraft:badlands_pillar":0.0,
    "minecraft:overworld/ridges":0.0,
}
mask_values={**base,"bugsbugsandmorebugs:karst_massif_region":1.0}
assert ev(stoneMask,mask_values)>0.98, "Historic Stone Forest center is still clipped"
for cont in (-0.55,-0.22,-0.05,0.1):
    assert ev(stoneMask,{**mask_values,"minecraft:overworld_large_biomes/continents":cont})==0, (
        "Stone Forest must not destroy Vanilla's seas, coast or regular near-inland rivers",cont
    )
# The original roof interpolates to the SHORT valley branch when the
# badlands pillar-threshold mask is high (noise near zero). Tall columns
# are the far-field cases where BOTH pillar noises sit outside that mask.
pillars={**base,"minecraft:badlands_surface":0.85,"minecraft:badlands_pillar":0.85,"y":150}
valley={**base}
assert ev(roof,pillars)>0.5,"Old pillars no longer rise far above sea level"
assert ev(roof,{**pillars,"y":202})<0,"Original Stone Forest column roof no longer terminates"
assert ev(roof,{**valley,"y":55})>0,"Stone Forest shallows lack a bottom"
assert ev(roof,{**valley,"y":63})<0,"Water filled Stone Forest valleys must be open at sea level"

cold={**base,
    "minecraft:overworld_large_biomes/continents":0.55,
    "minecraft:overworld_large_biomes/erosion":-0.55,
    "minecraft:overworld/temperature":-0.7,
    "minecraft:overworld/vegetation":-0.5,
    "bugsbugsandmorebugs:snowy_peak_region":1.0
}
assert ev(snowMask,cold)>0.98
assert ev(snowDensity,{**cold,"y":190})>0,"Snowy mountain mass must be solid above Y190"
assert ev(snowDensity,{**cold,"y":255})<0,"Snowy mountains must terminate"
for cont in (-0.7,-0.3,-0.12):
    assert ev(snowMask,{**cold,"minecraft:overworld_large_biomes/continents":cont})==0,(
        "Snowy peaks must never fill the ocean or coastline",cont
    )
assert ev(snowMask,{**cold,"minecraft:overworld/temperature":0.5})==0,(
    "Snowy mountain density must not raise warm volcanic peaks"
)

# Nearest-climate (squared distance) lookup, like MultiNoise climate matching.
rows=dim["generator"]["biome_source"]["biomes"]
axes=("temperature","humidity","continentalness","erosion","depth","weirdness","offset")
def nearest(point):
    best_name,best_distance=None,float("inf")
    for row in rows:
        score=0.0
        for key in axes:
            v=row["parameters"][key]
            lo,hi=(v if isinstance(v,list) else (v,v))
            delta=max(lo-point[key],0,point[key]-hi)
            score+=delta*delta
        if score<best_distance:
            best_name,best_distance=row["biome"],score
    return best_name
def target(temperature,humidity,continentalness,erosion,weirdness):
    return {"temperature":temperature,"humidity":humidity,"continentalness":continentalness,
            "erosion":erosion,"weirdness":weirdness,"depth":0,"offset":0}

snow_rows=[x for x in rows if x["biome"]==ns+"snowy_peak"]
stone_rows=[x for x in rows if x["biome"]==ns+"stone_forest"]
assert len(snow_rows)>=16,"Snowy Peak distribution still confined to one vanishingly rare climate corner"
assert len(stone_rows)>=10,"Original Stone Forest climate anchors missing"
assert nearest(target(-.7,-.7,.6,-.55,.2))==ns+"snowy_peak"
assert nearest(target(-.7,-.30,.6,-.55,.2))==ns+"snowy_peak",(
    "Cold snowy mountain ridges must include normal humidity"
)
assert nearest(target(-.7,-.7,.24,-.55,-.25))==ns+"snowy_peak",(
    "Snowy Peaks must occur in mid-inland highlands, not only extreme far inland"
)
for temp,humidity,erosion in ((-.36,-.28,.17),(-.42,-.34,.22),(-.32,-.25,.12)):
    assert nearest(target(temp,humidity,.57,erosion,.12))==ns+"stone_forest",(
        "Old limestone forest ecology no longer selects the authentic terrain",temp,humidity
    )
assert nearest(target(.6,-.6,.57,-.55,.2))!=ns+"snowy_peak"
assert nearest(target(.6,-.6,.57,-.55,.2))!=ns+"stone_forest"
assert nearest(target(-.38,-.28,-.5,.17,.1))==ns+"ocean"

# Independent samples of climate coverage, not percentages in actual generated worlds.
rng=random.Random(20261125)
snow_count=0
stone_count=0
for i in range(1500):
    t=rng.uniform(-.95,.85)
    h=rng.uniform(-.9,.9)
    c=rng.uniform(.16,.8)
    e=rng.uniform(-.8,.65)
    w=rng.uniform(-.8,.8)
    name=nearest(target(t,h,c,e,w))
    snow_count+=name==ns+"snowy_peak"
    stone_count+=name==ns+"stone_forest"
assert snow_count>=35,("Snowy Peak climate too rare across inland sample",snow_count)
assert stone_count>=12,("Old Stone Forest climate no longer receives biome labels",stone_count)
assert snow_count<450 and stone_count<400,("One special biome dominates",snow_count,stone_count)
print("Historic Stone Forest pillar/valley density, ocean safety, snowy peaks and biome distribution checked.")
print("Synthetic inland climate samples: Snowy Peaks",snow_count,"/1500; Stone Forest",stone_count,"/1500.")
