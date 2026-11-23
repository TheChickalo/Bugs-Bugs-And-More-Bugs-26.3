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

assert router["continents"] == "minecraft:overworld_large_biomes/continents", "Biome continentalness not scaled"
assert router["erosion"] == "minecraft:overworld_large_biomes/erosion", "Terrain and biome erosion disagree"
assert router["temperature"] == "minecraft:overworld/temperature", "Preserve local forest temperature scale"
assert router["vegetation"] == "minecraft:overworld/vegetation", "Preserve local forest moisture mosaics"
assert router["ridges"] == "minecraft:overworld/ridges", "River and climate ridge signals must match"
assert router["depth"]["first"]["when_out_of_range"] == NS+"elsewhere_large_depth", "Wrong inland depth"
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
assert len(ocean)>=10, "Missing broad open-ocean climate coverage"
assert any(p["continentalness"]==[-1.25,-0.26] and p["weirdness"]==[-1,1] for p in ocean), "Unrepresented deep ocean"
assert len(river)>=19
assert any(p["continentalness"]==[-0.10,0.52] and p["weirdness"]==[-0.073,0.073] for p in river)
assert all(any(p["depth"]==[-2.1,-0.9] for p in entries) for entries in sky.values())

# No sky biome at ordinary surface depth; ensure all four actual biome names
# appear with ocean-facing continentalness and share the vertical density band.
for name, entries in sky.items():
    assert entries and all(p["continentalness"]==[-1.25,-0.28] for p in entries), name
mask = json.loads((WORLD / "density_function/sky_island_mask.json").read_text())
assert '"minecraft:overworld_large_biomes/continents"' in json.dumps(mask)
density = json.loads((WORLD / "density_function/sky_island_density.json").read_text())
assert density["left"]["left"]["right"] == -0.65

print("Elsewhere ocean, river and sky climate static checks passed.")
