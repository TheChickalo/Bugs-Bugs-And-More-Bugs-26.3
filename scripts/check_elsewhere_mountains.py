#!/usr/bin/env python3
"""Verify restored stone-forest columns, volcanic region and alpine caps.
Static shape checks supplement, but cannot replace, in-game visual QA.
"""
import json
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
WORLD=ROOT/"src/main/resources/data/bugsbugsandmorebugs/worldgen"
GEN=ROOT/"src/main/generated/data/bugsbugsandmorebugs/worldgen"
JAVA=ROOT/"src/main/java/net/chickalo/bugsbugsandmorebugs"
NS="bugsbugsandmorebugs:"

region=json.loads((WORLD/"density_function/stone_forest_region.json").read_text())
assert region["type"]=="minecraft:max"
assert region["left"]["left"]==NS+"karst_massif_region"
ecology=region["right"]
assert '"minecraft:overworld_large_biomes/continents"' in json.dumps(ecology)
assert '"minecraft:overworld/temperature"' in json.dumps(ecology)
assert '"minecraft:overworld/vegetation"' in json.dumps(ecology)
assert '"minecraft:overworld_large_biomes/erosion"' in json.dumps(ecology)

settings=json.loads((WORLD/"noise_settings/elsewhere.json").read_text())
union=settings["noise_router"]["final_density"]["when_in_range"]
assert union["right"]==NS+"sky_island_density"
land=union["left"]
assert land["first"]=="minecraft:overworld_large_biomes/final_density"
assert land["second"]==NS+"elsewhere_inland_legacy"
assert land["alpha"]["type"]=="minecraft:max"
boost=land["alpha"]["right"]
assert boost["left"]["left"]==NS+"stone_forest_region"
assert boost["right"]==0.98
assert '"minecraft:overworld_large_biomes/continents"' in json.dumps(boost)

legacy=json.loads((WORLD/"density_function/elsewhere_inland_legacy.json").read_text())
assert NS+"stone_forest_roof" in json.dumps(legacy)
assert NS+"stone_forest_region" in json.dumps(legacy)
assert NS+"volcanic_relief_density" in json.dumps(legacy)

# Warm volcanic region: previously registered warm volcanic biomes had a
# mutually exclusive *cold* terrain mask and could never reach their relief.
for name,old in (("volcanic_mountain_mask",-0.12),("volcanic_massif_region",-0.16)):
    mask=json.loads((WORLD/"density_function"/(name+".json")).read_text())
    thermal=(mask["right"]["left"] if name=="volcanic_mountain_mask"
             else mask["left"]["right"])
    assert thermal["input"]["left"]=={"type":"minecraft:sub",
            "left":"minecraft:overworld/temperature","right":(0.31 if name=="volcanic_mountain_mask" else 0.29)}
    assert thermal["input"]["right"]==4.5

source=(JAVA/"worldgen/feature/MountainRangeFeature.java").read_text()
assert "if(depth>=4)return;" in source
assert "Math.pow(0.54,depth)" in source
assert "if(rng.nextInt(3)!=0)continue;" in source
assert "final int region=144, margin=152;" in source
assert "Peak root=new Peak(rootX,rootZ,30,24+rng.nextInt(10)" in source
assert "?ModBlocks.DACITE" in source
assert "?Blocks.STONE" not in source, "Snowy mountain-range rock cannot be vanilla stone"

snow=(JAVA/"worldgen/feature/AlpineSnowFeature.java").read_text()
for signal in ("base=106;","base=174;","base=163;","base=179;","base=190;",
               "Blocks.PACKED_ICE","Blocks.BLUE_ICE",
               "ModBlocks.CHERT","ModBlocks.WEATHERED_LIMESTONE"):
    assert signal in snow,signal
for biome in ("snowy_peak","volcanic_peak","ashen_foothills","highland_karst","hinoki_highlands"):
    features=json.loads((GEN/"biome"/(biome+".json")).read_text())["features"]
    assert NS+"mountain_range_placed" in features[1],biome
    assert NS+"alpine_snow_placed" in features[10],biome

material=json.loads((WORLD/"material_rule/elsewhere.json").read_text())
for biome in ("highland_karst","highland_wildflower_meadow","limestone_scrubland"):
    row=next(r for r in material["sequence"] if r.get("if_true",{}).get("biome_is")==NS+biome)
    assert row["then_run"]["then_run"]["sequence"][0]["if_true"]["min_threshold"]>=0.9
stone=next(r for r in material["sequence"] if r.get("if_true",{}).get("biome_is")==NS+"stone_forest")
assert stone["then_run"]["then_run"]["sequence"][1]["then_run"]["if_true"]["min_threshold"]==0.85
snowy=next(r for r in material["sequence"] if r.get("if_true",{}).get("biome_is")==NS+"snowy_peak")
assert any(r.get("if_true",{}).get("type")=="minecraft:above_preliminary_surface"
           and r.get("then_run",{}).get("result_state")==NS+"dacite"
           for r in snowy["then_run"]["sequence"])
print("Stone Forest region, mountain-range chains, rock palette, snow and ice wiring passed")
