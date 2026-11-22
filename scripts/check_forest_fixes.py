#!/usr/bin/env python3
"""Regression assertions for the five new Elsewhere tree habitats."""
import json
from pathlib import Path

root = Path("src/main")
data = root / "generated/data"
src = root / "java/net/chickalo/bugsbugsandmorebugs"
read = lambda p: p.read_text(encoding="utf-8")

def tag(name):
    return json.loads(read(data / f"minecraft/tags/block/{name}.json"))["values"]

logs = set(tag("logs"))
support = set(tag("prevents_nearby_leaf_decay"))
leaves = set(tag("leaves"))
for species in ("wisteria", "willow", "cedar", "persimmon", "mimosa"):
    for piece in (f"{species}_log", f"{species}_wood", f"stripped_{species}_log", f"stripped_{species}_wood"):
        block = "bugsbugsandmorebugs:" + piece
        assert block in logs, f"{block} missing minecraft:logs"
        assert block in support, f"{block} missing leaf-decay support"
    assert "bugsbugsandmorebugs:" + species + "_leaves" in leaves

forest = read(src / "block/custom/ExtendedForestLeavesBlock.java")
blocks = read(src / "block/ModBlocks.java")
assert "MAX_FOREST_DISTANCE = 16" in forest
assert blocks.count("new ExtendedForestLeavesBlock(") == 5
feature = read(src / "worldgen/feature/ForestHabitatFeature.java")
assert "ModBlocks.CEDAR_WOOD" in feature
assert "ModBlocks.CEDAR_LOG;" not in feature.split("private void rootButtresses(")[-1].split("private boolean scatterPlants(")[0]
assert "int[] radii = {2, 3, 4, 3, 1}" in feature

boulders = read(src / "worldgen/feature/HinokiBoulderFeature.java")
picker = boulders.split("private static BlockState pickRock")[1].split("private static boolean validGround")[0]
assert "COBBLED_RHYOLITE" in picker and "MOSSY_RHYOLITE" in picker
assert "Blocks.COBBLESTONE" not in picker and "Blocks.MOSSY_COBBLESTONE" not in picker

dimension = json.loads(read(root / "resources/data/bugsbugsandmorebugs/dimension/elsewheredim.json"))
climates = dimension["generator"]["biome_source"]["biomes"]
willow = [p["parameters"] for p in climates if p["biome"] == "bugsbugsandmorebugs:willow_backwaters"]
assert len(willow) >= 26, "Willow climates too rare again"
assert any(0.40 <= p["humidity"] <= 0.60 for p in willow)
assert any(p["continentalness"] < 0.2 for p in willow)
assert any(p["erosion"] >= 0.45 for p in willow)
assert sum(x["biome"] == "bugsbugsandmorebugs:river" for x in climates) >= 18, "Protect river distribution"
assert sum(x["biome"] == "bugsbugsandmorebugs:estuary" for x in climates) == 2
print("New forest decay, rock palette, wisteria silhouette and Willow climate checks passed")
