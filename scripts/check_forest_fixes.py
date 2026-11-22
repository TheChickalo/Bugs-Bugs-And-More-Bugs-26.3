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
# The latest forest foliage family includes the flowering Mimosa variant, and
# Wisteria already uses extended leaf support in the merged baseline.
assert blocks.count("new ExtendedForestLeavesBlock(") >= 6
assert 'new ExtendedForestLeavesBlock' in blocks.split('public static final Block WISTERIA_LEAVES')[1].split(";")[0]
feature = read(src / "worldgen/feature/ForestHabitatFeature.java")
assert "ModBlocks.CEDAR_WOOD" in feature
assert "ModBlocks.CEDAR_LOG;" not in feature.split("private void rootButtresses(")[-1].split("private boolean scatterPlants(")[0]
assert "willowCrown(" in feature and "willowRootNetwork(" in feature
assert "persimmonCrown(" in feature and "mimosaCrown(" in feature
assert "kind == 0) return scatterPlants(" in feature
assert "ModBlocks.WILLOW_WOOD" in feature
# New canopy families must use uneven face-connected forks and secondary twigs.
assert "curvedBranch(" in feature and "foliageLobe(" in feature
assert "hangWillowTufts(" in feature
# Mangrove-inspired arches descend from trunk to real soil / shallow water.
assert "mangroveStyleRoots(" in feature and "findRootLanding(" in feature
assert "ForestRootsBlock.WATERLOGGED" in feature
assert "ModBlocks.CLUBMOSS_LOWLYING" in feature
assert "level.setBlock(soilPos,ModBlocks.CLUBMOSS.defaultBlockState()" in feature
assert 'CLUBMOSS = registerBlock("clubmoss", p -> new Block(' in blocks
assert 'LIVERWORT = registerBlock("liverwort", p -> new LeafLitterBlock(' in blocks
assert 'CLUBMOSS_LOWLYING = registerBlock(' in blocks
liverwort_state = json.loads(read(root / "resources/assets/bugsbugsandmorebugs/blockstates/liverwort.json"))
assert len(liverwort_state["variants"]) == 16, "Liverwort must have 4 patches x 4 facings"
clubmoss_model = json.loads(read(root / "resources/assets/bugsbugsandmorebugs/models/block/clubmoss.json"))
assert clubmoss_model["parent"] == "minecraft:block/cube_all"
for species in ("cedar", "willow"):
    states = json.loads(read(root / f"resources/assets/bugsbugsandmorebugs/blockstates/{species}_roots.json"))
    assert len(states["variants"]) == 6, "Each axis needs dry and waterlogged states"
native = json.loads(read(data / "bugsbugsandmorebugs/worldgen/feature/wisteria_tree.json"))
assert native["type"] == "minecraft:tree"
assert native["trunk_placer"]["type"] == "bugsbugsandmorebugs:maple_trunk_placer"
assert native["foliage_placer"]["type"] == "bugsbugsandmorebugs:maple_foliage_placer"
assert native["decorators"][0]["type"] == "bugsbugsandmorebugs:wisteria_blossoms"
habitat = json.loads(read(data / "bugsbugsandmorebugs/worldgen/feature/wisteria_habitat.json"))
assert habitat["kind"] == 0
grove = json.loads(read(data / "bugsbugsandmorebugs/worldgen/biome/wisteria_veilwood.json"))
assert "bugsbugsandmorebugs:wisteria_tree_placed" in grove["features"][9]
assert grove["features"][9].index("bugsbugsandmorebugs:wisteria_tree_placed") < grove["features"][9].index("bugsbugsandmorebugs:wisteria_habitat_placed")
root_model = json.loads(read(root / "resources/assets/bugsbugsandmorebugs/models/block/willow_roots.json"))
assert len(root_model["elements"]) >= 6, "Willow Roots are still a hollow-textured cube"
assert "noOcclusion()" in blocks.split('public static final Block WILLOW_ROOTS')[1].split(";")[0]

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
print("Forest ground cover, mangrove-style roots, leaf support and Willow climate checks passed")
