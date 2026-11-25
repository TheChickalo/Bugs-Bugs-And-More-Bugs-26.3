#!/usr/bin/env python3
"""Elsewhere bounded archipelago contracts. Does not replace in-game tests."""
import json
import re
from pathlib import Path
root=Path(__file__).resolve().parents[1]
base=root/"src/main/resources/data/bugsbugsandmorebugs"
world=base/"worldgen"
source=root/"src/main/java/net/chickalo/bugsbugsandmorebugs/worldgen/density/SkyArchipelagoDensityFunction.java"
register=root/"src/main/java/net/chickalo/bugsbugsandmorebugs/worldgen/density/ModDensityFunctionTypes.java"
initializer=root/"src/main/java/net/chickalo/bugsbugsandmorebugs/BugsBugsAndMoreBugs.java"
mask=json.loads((world/"density_function/sky_island_mask.json").read_text())
solid=json.loads((world/"density_function/sky_island_density.json").read_text())
settings=json.loads((world/"noise_settings/elsewhere.json").read_text())
dim=json.loads((base/"dimension/elsewheredim.json").read_text())
materials=json.loads((world/"material_rule/elsewhere.json").read_text())
java=source.read_text()
ns="bugsbugsandmorebugs:"

assert mask["type"]=="minecraft:clamp"
assert mask["input"]["type"]=="minecraft:min"
assert mask["input"]["left"]=={"type":ns+"bounded_sky_islands","mode":"mask"}
assert solid["type"]=="minecraft:min"
assert solid["left"]=={"type":ns+"bounded_sky_islands","mode":"solid"}
assert mask["input"]["right"]==solid["right"], "Ocean gate diverged between sky mask and blocks"
assert solid["right"]["left"]=={"type":"minecraft:sub","left":-0.32,"right":"minecraft:overworld_large_biomes/continents"}
assert '"bounded_sky_islands"' in register.read_text()
assert "ModDensityFunctionTypes.register();" in initializer.read_text()
assert "context.createRandom(SEED_ID)" in java
assert "Math.floorDiv(x, CELL_SIZE)" in java and "Math.floorDiv(z, CELL_SIZE)" in java
assert "DensitySampler.sampleVolumeNaive" in java

constants={}
for field in ("CELL_SIZE","CENTER_JITTER","MAX_RADIUS","MIN_RADIUS"):
    found=re.search(r"\b"+field+r"\s*=\s*(\d+)",java)
    assert found, "Missing explicit size setting: "+field
    constants[field]=int(found.group(1))
cell=constants["CELL_SIZE"]
rmax=constants["MAX_RADIUS"]
jitter=constants["CENTER_JITTER"]
rmin=constants["MIN_RADIUS"]
assert (cell,rmax,jitter,rmin)==(192,72,12,18)
assert 2*rmax==144, "Maximum island diameter changed"
assert cell-2*jitter-2*rmax>=20, "Neighbouring cells can touch / create giant sky continents"
assert '"mask"' in java and '"solid"' in java
assert "top = 187F + 83F" in java, "Independent island altitude variance removed"
assert "float thickness = 9F + 58F" in java, "Pointed underside taper removed"
assert "for (int spike = 0; spike < 3; spike++)" in java, "Drooping rock points removed"
assert "if (r >= 1F)" in java, "Hard radial cap removed"
assert "clusterContains(worldSeed, cxCell, czCell)" in java, "Sky archipelago group spawning missing"
assert "Math.floorDiv(cellX, CLUSTER_CELLS)" in java
assert "Math.floorMod(cellZ, CLUSTER_CELLS)" in java
assert "if (localX == 1 && localZ == 1) return true;" in java
assert "NEXT_ISLAND_CHANCE[step - 1]" in java
import re as _re
probabilities=_re.search(r"NEXT_ISLAND_CHANCE\s*=\s*\{(.*?)\}",java,_re.S)
assert probabilities, "No explicit declining island-cluster probability series"
chance=[float(x.strip().rstrip('Ff')) for x in probabilities.group(1).split(',') if x.strip()]
assert len(chance)==8
assert all(0<q<1 for q in chance)
assert all(chance[i]>chance[i+1] for i in range(7)), "Next island must become LESS likely as a group grows"
expected=1.0
survival=1.0
for q in chance:
    survival*=q
    expected+=survival
assert 2.8 < expected < 5.2, ("Need clusters of several islands, not infinite sky continents",expected)
assert 'if (mode.equals("solid") && (y < 95 || y > 300))' in java

router=settings["noise_router"]
rootdensity=router["final_density"]
assert rootdensity["type"]=="minecraft:range_choice"
union=rootdensity["when_in_range"]
assert union["type"]=="minecraft:max"
assert union["right"]==ns+"sky_island_density"
assert union["left"]["type"]=="minecraft:max", "Stone and snow special terrain must be unioned beneath sky"
assert union["left"]["right"]==ns+"snowy_peak_density"
assert union["left"]["left"]["type"]=="minecraft:lerp"
assert union["left"]["left"]["alpha"]==ns+"stone_forest_restoration_region"
assert union["left"]["left"]["second"]==ns+"stone_forest_roof"
assert union["left"]["left"]["first"]["type"]=="minecraft:lerp", "Vanilla water must remain the base"
assert union["left"]["left"]["first"]["first"]=="minecraft:overworld_large_biomes/final_density"
assert union["left"]["left"]["first"]["second"]==ns+"elsewhere_inland_legacy"
assert union["left"]["left"]["first"]["alpha"]["type"]=="minecraft:max"
assert json.dumps(union["left"]).count(ns+"sky_island_density")==0
depth=router["depth"]
assert depth["type"]=="minecraft:range_choice"
assert depth["input"]==ns+"sky_island_density"
assert depth["when_in_range"]==-1.8
assert depth["max_exclusive"]==4
assert depth["when_out_of_range"]["type"]=="minecraft:range_choice"
assert depth["when_out_of_range"]["when_in_range"]==2
assert depth["when_out_of_range"]["when_out_of_range"]==0
sky_ids={ns+n for n in ("cloudcedar_forest","windcarved_crags","mistfall_gardens","vermilion_isles")}
climates=dim["generator"]["biome_source"]["biomes"]
assert sky_ids<={b["biome"] for b in climates}
assert all(b["parameters"]["depth"]==[-2,-1.5] for b in climates if b["biome"] in sky_ids)
sky_biomes=(root/"src/main/java/net/chickalo/bugsbugsandmorebugs/worldgen/biome/ModSkyBiomes.java").read_text()
assert ".hasPrecipitation(false)" in sky_biomes and ".hasPrecipitation(true)" not in sky_biomes
surfaces={x.get("if_true",{}).get("biome_is") for x in materials["sequence"] if isinstance(x.get("if_true"),dict)}
assert sky_ids<=surfaces
ocean=[x for x in materials["sequence"] if x.get("if_true",{}).get("biome_is")==ns+"ocean"]
assert len(ocean)==1
assert ocean[0]["then_run"]["if_true"]["invert"]["anchor"]["absolute"]==68
for radius in (rmin,34,52,rmax):
    for d in (0,radius/3,radius*0.9,radius,radius*1.1):
        for ax in (0.73,0.85,1.0):
            normalized=d/(radius*ax)
            if d>=radius:
                assert normalized>=1, "Radial cap failed"
assert cell-2*jitter-2*rmax==24
print("Bounded sky islands: explicit <=144-block cap, >=24-block separation, altitude/droop checks OK")
