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
# All three troubled trees now use vanilla 26.3 trunk AND foliage placers;
# the old Fancy Oak / Acacia configuration was still too spokelike.
assert "new CherryTrunkPlacer(" in sources
assert "new BendingTrunkPlacer(" in sources
assert "new CherryFoliagePlacer(" in sources
assert "new RandomSpreadFoliagePlacer(" in sources
assert 'context.register(WILLOW_TREE_KEY, new TreeFeature.Builder(' in sources
assert 'context.register(PERSIMMON_TREE_KEY, new TreeFeature.Builder(' in sources
assert 'context.register(MIMOSA_TREE_KEY, new TreeFeature.Builder(' in sources
# More durable than checking source text: inspect generated vanilla identifiers
# and ensure Willow has a taller trunk than the previous base-height 7.
def tree_config(species):
    return json.loads(read(data / f"bugsbugsandmorebugs/worldgen/feature/{species}_tree.json"))
willow_tree = tree_config("willow")
persimmon_tree = tree_config("persimmon")
mimosa_tree = tree_config("mimosa")
assert willow_tree["type"] == persimmon_tree["type"] == mimosa_tree["type"] == "minecraft:tree"
assert willow_tree["trunk_placer"]["type"] == "minecraft:cherry_trunk_placer"
assert willow_tree["foliage_placer"]["type"] == "minecraft:cherry_foliage_placer"
assert willow_tree["trunk_placer"]["base_height"] >= 13
assert persimmon_tree["trunk_placer"]["type"] == "minecraft:bending_trunk_placer"
assert persimmon_tree["foliage_placer"]["type"] == "minecraft:random_spread_foliage_placer"
assert mimosa_tree["trunk_placer"]["type"] == "minecraft:cherry_trunk_placer"
assert mimosa_tree["foliage_placer"]["type"] == "minecraft:cherry_foliage_placer"
assert mimosa_tree["foliage_placer"]["radius"] >= 4

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


# New cedar rainforest and mountain-range features must be present in the
# generated datapack, not only in the Java bootstraps.
for name in ("rainforest_creeks", "mountain_range"):
    configured = json.loads(read(data / f"bugsbugsandmorebugs/worldgen/feature/{name}.json"))
    placed = json.loads(read(data / f"bugsbugsandmorebugs/worldgen/placed_feature/{name}_placed.json"))
    assert configured["type"] == f"bugsbugsandmorebugs:{name}"
    assert placed["feature"] == f"bugsbugsandmorebugs:{name}"
cedar_biome = json.loads(read(data / "bugsbugsandmorebugs/worldgen/biome/ancient_cedar_rainforest.json"))
assert "bugsbugsandmorebugs:rainforest_creeks_placed" in cedar_biome["features"][1]
for biome_name in ("highland_karst", "hinoki_highlands", "volcanic_peak", "snowy_peak", "ashen_foothills"):
    mountain_biome = json.loads(read(data / f"bugsbugsandmorebugs/worldgen/biome/{biome_name}.json"))
    assert "bugsbugsandmorebugs:mountain_range_placed" in mountain_biome["features"][1]

# The three hanging vegetation types use the actual new 16x16 base, middle and
# tip PNG files, with explicit states instead of reusing one sprite everywhere.
assets = root / "resources/assets/bugsbugsandmorebugs"
for name in ("hanging_wisteria_blossoms", "hanging_willow_leaves", "hanging_forest_moss"):
    blockstate = json.loads(read(assets / f"blockstates/{name}.json"))
    assert len(blockstate["variants"]) == 4
    assert blockstate["variants"]["base=true,tip=false"]["model"].endswith("_base")
    assert blockstate["variants"]["base=false,tip=false"]["model"].endswith("_middle")
    assert blockstate["variants"]["base=false,tip=true"]["model"].endswith("_tip")
    for part in ("base", "middle", "tip"):
        assert (assets / f"textures/block/{name}_{part}.png").exists()
        assert (assets / f"models/block/{name}_{part}.json").exists()

habitat_java = read(src / "worldgen/feature/ForestHabitatFeature.java")
assert "microhabitat(level,p)" in habitat_java
assert "ModBiomeFamilies.forestMicroIndex(" in habitat_java
assert "placeNurseLog(" in habitat_java
assert "applyMicrohabitatSurface(" in habitat_java
assert "int colonies=random.nextInt(5)" in habitat_java
mountain_java = read(src / "worldgen/feature/MountainRangeFeature.java")
assert "if(depth>=4)return;" in mountain_java
assert "Math.pow(0.54,depth)" in mountain_java
assert "private static boolean room(" in mountain_java
rain_java = read(src / "entity/client/BugsBugsandMoreBugsClient.java")
server_java = read(src / "BugsBugsAndMoreBugs.java")
assert "server.setWeatherParameters(0,12000,true,false)" in server_java
assert "ELSEWHEREDIM_LEVEL_KEY" in server_java
assert "ParticleTypes.RAIN" not in rain_java, "Only actual server rain should remain"

# The climate-only Willow constraint targets rivers/coast and keeps river nodes.
dimension_biomes = climates
willow_params = [b["parameters"] for b in dimension_biomes
                 if b["biome"] == "bugsbugsandmorebugs:willow_backwaters"]
assert len(willow_params) == 30
assert all(-0.15 <= p["continentalness"] <= 0.34 for p in willow_params)
assert all(abs(p["weirdness"]) <= 0.06 for p in willow_params)
assert sum(b["biome"] == "bugsbugsandmorebugs:river" for b in dimension_biomes) >= 18


# All three named variants for each of the five forest families must be
# registered and in the actual dimension climate list, not only Java comments.
microfamilies={
    "wisteria_veilwood":["violet_curtains","tangled_arbors","petal_glades"],
    "willow_backwaters":["oxbow_banks","sedge_pools","willow_hummocks"],
    "ancient_cedar_rainforest":["elder_stands","nurse_log_hollows","rainwashed_gullies"],
    "persimmon_woodlands":["amber_groves","golden_meadows","bramble_terraces"],
    "silkflower_savanna":["blossom_drifts","feathergrass_prairie","sunwashed_rises"]
}
for parent,children in microfamilies.items():
    parent_features=json.loads(read(data / f"bugsbugsandmorebugs/worldgen/biome/{parent}.json"))["features"]
    for child in children:
        child_file=data / f"bugsbugsandmorebugs/worldgen/biome/{child}.json"
        assert child_file.exists(), f"Missing named /locate biome: {child}"
        child_features=json.loads(read(child_file))["features"]
        assert child_features == parent_features, f"Microbiome {child} lost tree or vegetation features"
        assert sum(b["biome"] == f"bugsbugsandmorebugs:{child}" for b in climates) >= 2
forest_families=read(src / "worldgen/biome/ModBiomeFamilies.java")
assert all(name.upper() in forest_families for children in microfamilies.values() for name in children)

# Smooth fixed bed depths, true shallow pools and bracket-fungus variations.
shallows_java=read(src / "worldgen/feature/WillowShallowsFeature.java")
assert "double wave=Math.sin(" in shallows_java
assert "Math.floorMod(x*19+z*31,3)" not in shallows_java
pool_feature=json.loads(read(data / "bugsbugsandmorebugs/worldgen/feature/willow_pools.json"))
assert pool_feature["type"] == "bugsbugsandmorebugs:willow_pools"
willow_biome=json.loads(read(data / "bugsbugsandmorebugs/worldgen/biome/willow_backwaters.json"))
assert "bugsbugsandmorebugs:willow_pools_placed" in willow_biome["features"][1]
fungi_state=json.loads(read(assets / "blockstates/bracket_fungus.json"))
assert len(fungi_state["variants"]) == 64, "4 shelf counts x 4 faces x 4 anchored variations"
fungi_java=read(src / "block/custom/BracketFungusBlock.java")
assert "IntegerProperty.create(\"variation\", 0, 3)" in fungi_java
cedar_biome=json.loads(read(data / "bugsbugsandmorebugs/worldgen/biome/ancient_cedar_rainforest.json"))
assert cedar_biome["attributes"]["minecraft:visual/sky_color"] == "#8c969a"
print("Fifteen registered locate-able micro-biomes, smooth willow pools and global rain verified")

print("Forest texture segments, 15 microhabitats, coastal Willow climates and ranges verified")

print("Vanilla tree placers, root models, layered Duckweed, shallow water and Willow climates verified")
