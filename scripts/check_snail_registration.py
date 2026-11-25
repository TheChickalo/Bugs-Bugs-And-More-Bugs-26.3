#!/usr/bin/env python3
"""Prevent snail entity + spawn egg from silently disappearing again."""
from pathlib import Path
root=Path(__file__).resolve().parents[1]
java=root/"src/main/java/net/chickalo/bugsbugsandmorebugs"
assets=root/"src/main/resources/assets/bugsbugsandmorebugs"
init=(java/"BugsBugsAndMoreBugs.java").read_text()
snail=(java/"entity/snail/SnailContent.java").read_text()
client=(java/"entity/client/BugsBugsandMoreBugsClient.java").read_text()
assert "import net.chickalo.bugsbugsandmorebugs.entity.snail.SnailContent;" in init
assert "SnailContent.register();" in init, "Snail class exists but never registered"
assert 'id("snail")' in snail and 'id("snail_spawn_egg")' in snail
assert "FabricDefaultAttributeRegistry.register(SNAIL" in snail
assert "BiomeModifications.addSpawn" in snail
assert "CreativeModeTabs.SPAWN_EGGS" in snail
assert "SnailClient.register();" in client
assert (assets/"items/snail_spawn_egg.json").exists()
assert (assets/"textures/item/snail_spawn_egg.png").exists()
assert (assets/"textures/entity/snail/snail_common.png").exists()
print("Snail entity, egg, natural spawn hook and client renderer are wired")
