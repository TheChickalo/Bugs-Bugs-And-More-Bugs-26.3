#!/usr/bin/env python3
"""Static tests of the Elsewhere large-ocean/river/sky biome alignment.
The real gameplay contract still requires new-chunk sampling in Minecraft 26.3.
"""
from __future__ import annotations
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
NS = "bugsbugsandmorebugs:"
DATA = ROOT / "src/main/resources/data/bugsbugsandmorebugs"
WORLD = DATA / "worldgen"
dimension = json.loads((DATA / "dimension/elsewheredim.json").read_text())
settings = json.loads((WORLD / "noise_settings/elsewhere.json").read_text())
biomes = dimension["generator"]["biome_source"]["biomes"]
router = settings["noise_router"]

climateRouter=router["continents"]
assert climateRouter["type"]=="minecraft:lerp", "River biome selection must use its actual terrain corridor"
assert climateRouter["first"]==NS+"elsewhere_biome_continents"
assert climateRouter["second"]==0.28
assert climateRouter["alpha"]["type"]=="minecraft:mul"
assert climateRouter["alpha"]["left"]==NS+"river_corridor_region"
river_fade=climateRouter["alpha"]["right"]
assert river_fade["type"]=="minecraft:gradient" and river_fade["axis"]=="y"
assert river_fade["from_coordinate"]==62 and river_fade["to_coordinate"]==83
climate = json.loads((WORLD / "density_function/elsewhere_biome_continents.json").read_text())
assert climate["type"]=="minecraft:lerp" and climate["first"]=="minecraft:overworld_large_biomes/continents"
assert climate["alpha"]["type"]=="minecraft:clamp" and climate["second"]["type"]=="minecraft:clamp"
assert climate["second"]["min"] >= -1.0
assert climate["second"]["max"] <= -0.3
assert climate["second"]["min"]==-0.92
assert climate["alpha"]["input"]["left"]["left"]==-0.20
assert router["erosion"] == "minecraft:overworld_large_biomes/erosion", "Terrain and biome erosion disagree"
assert router["temperature"] == "minecraft:overworld/temperature", "Preserve local forest temperature scale"
assert router["vegetation"] == "minecraft:overworld/vegetation", "Preserve local forest moisture mosaics"
assert router["ridges"] == "minecraft:overworld/ridges", "River and climate ridge signals must match"
assert router["depth"]["type"]=="minecraft:range_choice"
assert router["depth"]["input"] == NS+"sky_island_density", "Climate-depth signal must be gated by physical island density"
under=router["depth"]["when_out_of_range"]
assert under["type"]=="minecraft:clamp" and under["min"]==0 and under["max"]==2
assert under["input"]["when_out_of_range"] == NS+"elsewhere_large_depth", "Wrong inland depth"
assert settings["aquifers"]["surface_level"]["left"] == "minecraft:overworld_large_biomes/preliminary_surface_level"

largeDepth = json.loads((WORLD / "density_function/elsewhere_large_depth.json").read_text())
assert '"minecraft:overworld_large_biomes/offset"' in json.dumps(largeDepth)
assert NS+"karst_elevation_mask" in json.dumps(largeDepth), "Karst elevation must survive"

# Geological masks that read continentalness/erosion must match the
# biome sampler's large-biome fields rather than a different vanilla scale.
for file in (WORLD / "density_function").glob("*.json"):
    content = file.read_text()
    assert '"minecraft:overworld/continents"' not in content, file
    assert '"minecraft:overworld/erosion"' not in content, file
settings_data = json.dumps(settings)
assert '"minecraft:overworld/continents"' not in settings_data
assert '"minecraft:overworld/erosion"' not in settings_data
assert '"minecraft:overworld/final_density"' not in settings_data
assert '"minecraft:overworld_large_biomes/final_density"' in settings_data

ocean = [p["parameters"] for p in biomes if p["biome"]==NS+"ocean"]
river = [p["parameters"] for p in biomes if p["biome"]==NS+"river"]
skyNames = ("cloudcedar_forest", "windcarved_crags", "mistfall_gardens", "vermilion_isles")
sky = {name: [p["parameters"] for p in biomes if p["biome"]==NS+name] for name in skyNames}
assert len(ocean)>=25, "Missing targeted deep-ocean climate coverage"
assert all(not (isinstance(p["temperature"],list) and p["temperature"]==[-1,1]) for p in ocean), (
    "Ocean wildcard has returned: can swallow terrestrial biomes"
)
assert all(not (isinstance(p["temperature"],list) and p["temperature"]==[-1,1]) for p in river), (
    "River wildcard has returned: can swallow terrestrial biomes"
)
deep = [p for p in ocean if p["continentalness"]==-0.67]
assert len(deep)==18, "Nine temperature/moisture climate cells need two erosion variants"
assert all(p["weirdness"]==0 for p in river), "River corridors should track ridges=0, not wide areas"
assert len(river)>=18
assert all(any(p["depth"]==[-2,-1.5] for p in entries) for entries in sky.values())

# Minecraft 26.3 Climate.Parameter.CODEC rejects numbers outside [-2, 2].
# A bad biome entry can report the misleading "No key preset" error.
for index, row in enumerate(biomes):
    parameters = row["parameters"]
    for field in ("temperature", "humidity", "continentalness", "erosion", "depth", "weirdness"):
        raw = parameters[field]
        values = raw if isinstance(raw, list) else [raw]
        assert len(values) in (1, 2), (index, row["biome"], field, raw)
        assert all(isinstance(v, (int, float)) and not isinstance(v, bool) for v in values), (index, row["biome"], field, raw)
        assert all(-2.0 <= v <= 2.0 for v in values), (
            f"Invalid 26.3 climate parameter at biome #{index} {row['biome']}: {field}={raw}. "
            "Allowed range is [-2.0, 2.0]."
        )
        if len(values) == 2:
            assert values[0] <= values[1], (index, row["biome"], field, raw)
    offset = parameters["offset"]
    assert isinstance(offset, (int, float)) and not isinstance(offset, bool) and 0.0 <= offset <= 1.0, (
        index, row["biome"], "offset", offset
    )

# No sky biome at ordinary surface depth; ensure all four actual biome names
# appear with ocean-facing continentalness and share the vertical density band.
for name, entries in sky.items():
    assert entries and all(p["continentalness"]==[-1.25,-0.28] for p in entries), name
mask = json.loads((WORLD / "density_function/sky_island_mask.json").read_text())
assert '"minecraft:overworld_large_biomes/continents"' in json.dumps(mask)
density = json.loads((WORLD / "density_function/sky_island_density.json").read_text())
assert density["type"]=="minecraft:min"
assert density["left"] == {"type":NS+"bounded_sky_islands","mode":"solid"}
assert density["right"]["left"]["left"] == -0.32


# Synthetic tests of MultiNoise nearest-climate matching. Only the biome
# continentalness signal changes; check deep/shallow seas versus inland ground.
def ocean_climate_continentalness(c):
    alpha=max(0,min(1,(-0.20-c)*11))
    warped=max(-0.92,min(-0.30,c*1.28-0.09))
    return c*(1-alpha)+warped*alpha
def climate_distance(given,selected):
    squared=0
    for key in ("temperature","humidity","continentalness","erosion","depth","weirdness","offset"):
        b=given[key]
        lo,hi=(b if isinstance(b,list) else (b,b))
        val=selected[key]
        delta=lo-val if val<lo else val-hi if val>hi else 0
        squared+=delta*delta
    return squared
for c,expected_ocean in [(-0.65,True),(-0.38,True),(-0.17,True),(0.30,False)]:
    point={"temperature":0.10,"humidity":0.18,"continentalness":ocean_climate_continentalness(c),
           "erosion":0.12,"depth":0,"weirdness":0.23,"offset":0}
    chosen=min(biomes,key=lambda row:climate_distance(row["parameters"],point))["biome"]
    assert (chosen==NS+"ocean")==expected_ocean, (c,chosen)


# A separate failure mode: the four sky biome points cover nearly every
# humidity/temperature/erosion/weirdness combination, so they used to win
# the nearest-climate search on ordinary dry land at depth 0. The target depth
# must be sufficiently far from normal surface depth that a sky biome CAN'T
# beat the ordinary biome candidates merely through their broad other ranges.
from math import sin, floor
def pseudo(i,k):
    v=sin(i*12.9898+k*78.233)*43758.5453
    return v-floor(v)
sky_ids={NS+name for name in skyNames}
for cont in (-.9,-.7,-.5,-.35,-.25,-.15,-.08,.08,.3,.6):
    for i in range(100):
        point={
            "temperature":-1+2*pseudo(i,1),
            "humidity":-1+2*pseudo(i,2),
            "continentalness":cont,
            "erosion":-1+2*pseudo(i,3),
            "depth":0,
            "weirdness":-1+2*pseudo(i,4),
            "offset":0,
        }
        selected=min(biomes,key=lambda row:climate_distance(row["parameters"],point))
        assert selected["biome"] not in sky_ids, (
            "Sky biome wins at non-island surface depth 0", cont, i,selected["biome"]
        )
for temp,humid,expected in ((-.4,.4,"cloudcedar_forest"),(-.4,-.4,"windcarved_crags"),(.4,.4,"mistfall_gardens"),(.4,-.4,"vermilion_isles")):
    point={"temperature":temp,"humidity":humid,"continentalness":-.65,
           "erosion":0,"depth":-1.8,"weirdness":0.28,"offset":0}
    actual=min(biomes,key=lambda row:climate_distance(row["parameters"],point))["biome"]
    assert actual==NS+expected, ("Sky biome not selectable inside actual island",expected,actual)



# River climate must use the SAME region controlling the carved river, not a
# global climate rectangle. This checks a typical river climate with a fully
# active river corridor at sea level.
rivertest={"temperature":.25,"humidity":.42,"continentalness":0.28,
           "erosion":.14,"depth":0,"weirdness":0,"offset":0}
river_selected=min(biomes,key=lambda row:climate_distance(row["parameters"],rivertest))["biome"]
assert river_selected==NS+"river", ("River carve still wins Ocean or land biome",river_selected)

# Ocean climate should not be artificially forced deep into the maritime
# anchors for near-shore land noise; farther offshore is still ocean-facing.
assert ocean_climate_continentalness(-.10)==-.10
assert ocean_climate_continentalness(-.17)==-.17
assert -.40 < ocean_climate_continentalness(-.25) < -.25
assert ocean_climate_continentalness(-.60) < -.65
print("Elsewhere ocean, river and sky climate static checks passed.")
