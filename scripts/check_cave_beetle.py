import json, base64
from pathlib import Path
root=Path("src/main")
model=json.loads(Path("design/models/cave_beetle_4.bbmodel").read_text())
assert len(model["elements"]) == 21
assert {a["name"] for a in model["animations"]} == {"idle","walk","investigate","chew","dig"}
frames=sum(len(b.get("keyframes", [])) for a in model["animations"] for b in a["animators"].values())
assert frames == 1599
assets=root/"resources/assets/bugsbugsandmorebugs"
assert (assets/"textures/entity/cave_beetle/cave_beetle.png").read_bytes() == base64.b64decode(model["textures"][0]["source"].split(",")[1])
assert (assets/"textures/item/cave_beetle_spawn_egg.png").read_bytes()[:8] == b"\x89PNG\r\n\x1a\n"
java=root/"java/net/chickalo/bugsbugsandmorebugs"
entity=java/"entity/cavebeetle"
assert (entity/"client/CaveBeetleAnimations.java").read_text().count("new Keyframe(") == frames
assert "CaveBeetleContent.register();" in (java/"BugsBugsAndMoreBugs.java").read_text()
assert "CaveBeetleClient.register();" in (java/"entity/client/BugsBugsandMoreBugsClient.java").read_text()
for tag in ("small","normal","rich","dense"):
    json.loads((root/"resources/data/bugsbugsandmorebugs/tags/item"/("beetle_food_"+tag+".json")).read_text())
print("Cave beetle V4 source, keyframes, texture, registration and food tags verified")

