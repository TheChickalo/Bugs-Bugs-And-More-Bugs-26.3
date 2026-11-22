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
# The three problem trees now use Mojang's actual tree pipeline, rather than
# hand-authored radial spokes inside ForestHabitatFeature.
sources=read(src / "worldgen/ModFeatures.java")
assert "new FancyTrunkPlacer(" in sources
assert "new ForkingTrunkPlacer(" in sources
assert "new AcaciaFoliagePlacer(" in sources
assert 'context.register(WILLOW_TREE_KEY, new TreeFeature.Builder(' in sources
assert 'context.register(PERSIMMON_TREE_KEY, new TreeFeature.Builder(' in sources
assert 'context.register(MIMOSA_TREE_KEY, new TreeFeature.Builder(' in sources
assert "kind != 2) return scatterPlants(" in feature
assert "mangroveStyleRoots(" in feature
assert "ModBlocks.CLUBMOSS_LOWLYING" in feature
assert "level.setBlock(soilPos,ModBlocks.CLUBMOSS.defaultBlockState()" in feature
assert 'CLUBMOSS = registerBlock("clubmoss", p -> new Block(' in blocks
assert 'LIVERWORT = registerBlock("liverwort", p -> new LeafLitterBlock(' in blocks
assert 'CLUBMOSS_LOWLYING = registerBlock(' in blocks
assert 'CLUBMOSS_SPRIG = registerBlock(' not in blocks, "Remove old Clubmoss Sprig block"
liverwort_state = json.loads(read(root / "resources/assets/bugsbugsandmorebugs/blockstates/liverwort.json"))
assert len(liverwort_state["variants"]) == 16
duckweed_state = json.loads(read(root / "resources/assets/bugsbugsandmorebugs/blockstates/duckweed.json"))
assert len(duckweed_state["variants"]) == 16, "Duckweed must use leaf-litter sections"
assert 'new FloatingLitterBlock(' in blocks
clubmoss_model = json.loads(read(root / "resources/assets/bugsbugsandmorebugs/models/block/clubmoss.json"))
assert clubmoss_model["parent"] == "minecraft:block/cube_all"
for species in ("cedar", "willow"):
    states = json.loads(read(root / f"resources/assets/bugsbugsandmorebugs/blockstates/{species}_roots.json"))
    assert len(states["variants"]) == 6
    model = json.loads(read(root / f"resources/assets/bugsbugsandmorebugs/models/block/{species}_roots.json"))
    assert model["parent"] == "minecraft:block/mangrove_roots", "Use native 26.3 mangrove-root geometry"

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
assert "noOcclusion()" in blocks.split('public static final Block WILLOW_ROOTS')[1].split(";")[0]
assert "WILLOW_SHALLOWS_PLACED_KEY" in read(src / "worldgen/biome/ModOverworldBiomes.java")
assert "WETLAND_REEDS_WILLOW_PLACED_KEY" in read(src / "worldgen/biome/ModOverworldBiomes.java")
assert "WILLOW_SHALLOWS_KEY" in sources

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

# Minecraft's FeatureSorter requires one global ordering for features shared
# across biomes at each decoration step. This catches cycles that compileJava
# and runDatagen cannot detect (they surface only during chunk generation).
# Example of the previous bug:
# Wisteria grass < mushrooms; Cedar mushrooms < Elsewhere flora;
# Mimosa Elsewhere flora < grass  -> a three-biome cycle.
from collections import defaultdict
biome_files = sorted((data / "bugsbugsandmorebugs/worldgen/biome").glob("*.json"))
assert biome_files, "Missing generated biome JSONs: runDatagen first"
feature_edges = defaultdict(set)
edge_biomes = {}
for biome_file in biome_files:
    biome = json.loads(read(biome_file))
    for step, sequence in enumerate(biome["features"]):
        for left, right in zip(sequence, sequence[1:]):
            u, v = (step, left), (step, right)
            feature_edges[u].add(v)
            edge_biomes.setdefault((u, v), set()).add(biome_file.stem)

visited, visiting = set(), {}
def verify_feature_order(node, path):
    if node in visiting:
        start = visiting[node]
        loop = path[start:] + [node]
        parts = []
        for u, v in zip(loop, loop[1:]):
            parts.append(f"{u[1]} -> {v[1]} ({', '.join(sorted(edge_biomes[(u, v)]))})")
        raise AssertionError("Feature order cycle at generation step "
                             + str(node[0]) + ": " + " | ".join(parts))
    if node in visited:
        return
    visiting[node] = len(path)
    path.append(node)
    for dest in sorted(feature_edges[node]):
        verify_feature_order(dest, path)
    path.pop()
    del visiting[node]
    visited.add(node)
for node in list(feature_edges):
    verify_feature_order(node, [])

print(f"Global feature ordering verified across {len(biome_files)} mod biomes")

print("Vanilla tree placers, root models, layered Duckweed, shallow water and Willow climates verified")
