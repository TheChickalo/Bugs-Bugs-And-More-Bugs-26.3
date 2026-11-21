#!/usr/bin/env python3
"""Reject Minecraft feature-order cycles across the generated biome set.

The FeatureSorter combines relative ordering constraints from every biome.
Distinct biomes can individually look valid but still form a global cycle.
"""
import json
from collections import defaultdict
from pathlib import Path

BIOMES = Path("src/main/generated/data/bugsbugsandmorebugs/worldgen/biome")
edges = defaultdict(set)
sources = defaultdict(set)
nodes = set()

for path in sorted(BIOMES.glob("*.json")):
    biome = json.loads(path.read_text())
    for step, features in enumerate(biome["features"]):
        for feature in features:
            nodes.add((step, feature))
        for left, right in zip(features, features[1:]):
            edge = ((step, left), (step, right))
            edges[edge[0]].add(edge[1])
            sources[edge].add(path.stem)

visited, active, stack = set(), set(), []

def visit(node):
    if node in active:
        i = stack.index(node)
        cycle = stack[i:] + [node]
        print("Feature-order cycle (Minecraft FeatureSorter):")
        for left, right in zip(cycle, cycle[1:]):
            print(f"  {left[1]} -> {right[1]} (step {left[0]})")
            print(f"    from biomes: {', '.join(sorted(sources[(left, right)]))}")
        raise SystemExit(1)
    if node in visited:
        return
    active.add(node)
    stack.append(node)
    for nxt in sorted(edges[node]):
        visit(nxt)
    stack.pop()
    active.remove(node)
    visited.add(node)

for start in sorted(nodes):
    visit(start)

print(f"Biome feature order valid: {len(nodes)} nodes in {len(list(BIOMES.glob('*.json')))} biomes")
