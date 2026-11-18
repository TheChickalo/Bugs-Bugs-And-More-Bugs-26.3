#!/usr/bin/env python3
"""Elsewhere biome prototype installer for Minecraft/Fabric 26.3.

Inspects and edits the user's CURRENT source files. Creates backups, refuses missing
anchors, never overwrites artwork. Optional experimental mountain generation.
"""
import argparse
import json
from pathlib import Path
import shutil

P = argparse.ArgumentParser()
P.add_argument('--mountain-prototype', action='store_true', help='Experimental Y=640 mountain density prototype; use only in a fresh test world')
P.add_argument('--restore-backups', action='store_true', help='Restore files stored in .elsewhere_biomes_backup')
a = P.parse_args()
root = Path.cwd()
package = Path('src/main/java/net/chickalo/bugsbugsandmorebugs')
ns = Path('src/main/resources/data/bugsbugsandmorebugs')
java = package / 'worldgen'
backup = root / '.elsewhere_biomes_backup'
work = {}


def get(path):
    path = root / path
    if path not in work:
        if not path.exists():
            raise RuntimeError(f'Required source file not found: {path.relative_to(root)}')
        work[path] = path.read_text(encoding='utf-8')
    return work[path]


def settext(path, value):
    work[root/path] = value


def replace_once(path, old, new, check=None):
    s = get(path)
    if check and check in s:
        return
    if old not in s:
        raise RuntimeError(f'Cannot safely patch {path}: expected anchor not found:\n{old[:200]}')
    settext(path, s.replace(old, new, 1))


def restore():
    if not backup.exists():
        raise RuntimeError('No backup folder to restore')
    for file in backup.rglob('*'):
        if file.is_file():
            target = root / file.relative_to(backup)
            target.parent.mkdir(parents=True,exist_ok=True)
            shutil.copy2(file,target)
    print('Restored original files from',backup)

if a.restore_backups:
    restore(); raise SystemExit

# Fix the actual bug found in the new pushed AmphiboliteDepositFeature.java.
f = java/'feature/AmphiboliteDepositFeature.java'
replace_once(f,
    'if (!state.is(ModBlocks.SCHIST) && !state.is(ModBlocks.ECLOGITE)) continue;',
    'if (!state.is(ModBlocks.SCHIST)) continue; // Never replace Eclogite or the Sundering Horizon.',
    'if (!state.is(ModBlocks.SCHIST)) continue; // Never replace')

# Key registration and bootstrap. Do not change an existing key on repeated installs.
f = java/'biome/ModBiomes.java'
replace_once(f,
    '    public static void registerBiomes() {',
    '''    // New Elsewhere biome families. See README for climate-adjacency limitations.
    public static final ResourceKey<Biome> STONE_FOREST = registerBiomeKey("stone_forest");
    public static final ResourceKey<Biome> GINKGO_FOREST = registerBiomeKey("ginkgo_forest");
    public static final ResourceKey<Biome> HINOKI_HIGHLANDS = registerBiomeKey("hinoki_highlands");
    public static final ResourceKey<Biome> VOLCANIC_PEAK = registerBiomeKey("volcanic_peak");

    public static void registerBiomes() {''',
    'public static final ResourceKey<Biome> STONE_FOREST')
replace_once(f,
    '        register(context, UNDERHANG, ModOverworldBiomes.underhang(placedFeatures, carvers));',
    '''        register(context, STONE_FOREST, ModOverworldBiomes.stoneForest(placedFeatures, carvers));
        register(context, GINKGO_FOREST, ModOverworldBiomes.ginkgoForest(placedFeatures, carvers));
        register(context, HINOKI_HIGHLANDS, ModOverworldBiomes.hinokiHighlands(placedFeatures, carvers));
        register(context, VOLCANIC_PEAK, ModOverworldBiomes.volcanicPeak(placedFeatures, carvers));
        register(context, UNDERHANG, ModOverworldBiomes.underhang(placedFeatures, carvers));''',
    'ModOverworldBiomes.stoneForest(')

# Four compile-friendly biome builders based on exact already-used 26.3 APIs.
f = java/'biome/ModOverworldBiomes.java'
s = get(f)
if 'public static Biome stoneForest(' not in s:
    last = s.rfind('}')
    assert last>0
    methods = '''
    // ================================================================
    // ELSEWHERE BIOME PROTOTYPES (26.3)
    // Stone Forest spires ARE real; ginkgo and hinoki temporarily share
    // Maple's tree feature until their own wood, leaves, and saplings exist.
    // No vanilla passive or hostile mobs are added in these four biomes.
    // ================================================================
    public static Biome stoneForest(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        generation.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS,
                ModPlacedFeatures.STONE_FOREST_SPIRES_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.HIGHLAND_MEADOW_PATCH_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.KARST_GROUND_PATCH_PLACED_KEY);
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.62F).downfall(0.49F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x4B9A95).grassColorOverride(0x8AAE65).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(generation.build())
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(0x8ACFEF))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0xD5E5DD))
                .build();
    }

    public static Biome ginkgoForest(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        // TODO: Replace with GINKGO_TREE_PLACED_KEY when ginkgo texture and foliage models are ready.
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.MAPLE_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.MAPLE_SHORT_GRASS_PLACED_KEY);
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.67F).downfall(0.68F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x667F66).grassColorOverride(0xB5AF62).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(generation.build())
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(0xA7D6D6))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0xDAD5BB))
                .build();
    }

    public static Biome hinokiHighlands(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        // TODO: Replace the Maple stand-in with tall Hinoki conifers.
        // Keep tree features out of volcanicPeak() to establish the treeline.
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.MAPLE_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.MAPLE_SHORT_GRASS_PLACED_KEY);
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.3F).downfall(0.65F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x506C72).grassColorOverride(0x6F966C).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(generation.build())
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(0x9AC4D0))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0x9CAFB7))
                .build();
    }

    public static Biome volcanicPeak(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        // Treeless by design. The material rule paints the exposed volcanic rock.
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(-0.4F).downfall(0.08F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x596E7F).grassColorOverride(0x70746D).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(new BiomeGenerationSettings.Builder(features, carvers).build())
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(0xA9C9D9))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0xB8C4CF))
                .build();
    }
'''
    settext(f,s[:last]+methods+s[last:])

# Spires: configure one more copy of the known-good feature, but much taller.
f = java/'ModFeatures.java'
replace_once(f,
    '    public static final ResourceKey<Feature> AMPHIBOLITE_DEPOSIT_KEY',
    '    public static final ResourceKey<Feature> STONE_FOREST_SPIRES_KEY = registerKey("stone_forest_spires");\n    public static final ResourceKey<Feature> AMPHIBOLITE_DEPOSIT_KEY',
    'public static final ResourceKey<Feature> STONE_FOREST_SPIRES_KEY')
replace_once(f,
    '        context.register(AMPHIBOLITE_DEPOSIT_KEY, new AmphiboliteDepositFeature());',
    '        context.register(STONE_FOREST_SPIRES_KEY, new KarstOutcropFeature(10, 28, 3, 90));\n        context.register(AMPHIBOLITE_DEPOSIT_KEY, new AmphiboliteDepositFeature());',
    'context.register(STONE_FOREST_SPIRES_KEY,')

f = java/'ModPlacedFeatures.java'
replace_once(f,
    '    public static final ResourceKey<PlacedFeature> AMPHIBOLITE_DEPOSIT_PLACED_KEY',
    '    public static final ResourceKey<PlacedFeature> STONE_FOREST_SPIRES_PLACED_KEY = registerKey("stone_forest_spires_placed");\n    public static final ResourceKey<PlacedFeature> AMPHIBOLITE_DEPOSIT_PLACED_KEY',
    'public static final ResourceKey<PlacedFeature> STONE_FOREST_SPIRES_PLACED_KEY')
replace_once(f,
    '        register(context, AMPHIBOLITE_DEPOSIT_PLACED_KEY,',
    '''        // Tall Stone Forest pinnacles; intentionally biome-limited.
        register(context, STONE_FOREST_SPIRES_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.STONE_FOREST_SPIRES_KEY),
                List.of(
                        CountPlacement.of(5),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));

        register(context, AMPHIBOLITE_DEPOSIT_PLACED_KEY,''',
    'register(context, STONE_FOREST_SPIRES_PLACED_KEY,')

# Current Karst features hard-gate HIGHLAND_KARST. Their checks must recognize
# STONE_FOREST or no pinnacles / meadow grass would ever appear.
for p,old,new in [
    (java/'feature/KarstOutcropFeature.java',
     '|| !level.getBiome(ground).is(ModBiomes.HIGHLAND_KARST)) {',
     '|| (!level.getBiome(ground).is(ModBiomes.HIGHLAND_KARST)\n                        && !level.getBiome(ground).is(ModBiomes.STONE_FOREST))) {'),
    (java/'feature/KarstGroundPatchFeature.java',
     'if (!level.getBiome(ground).is(ModBiomes.HIGHLAND_KARST)) continue;',
     'if (!level.getBiome(ground).is(ModBiomes.HIGHLAND_KARST)\n                        && !level.getBiome(ground).is(ModBiomes.STONE_FOREST)) continue;'),
    (java/'feature/HighlandMeadowPatchFeature.java',
     'if (!level.getBiome(pos).is(ModBiomes.HIGHLAND_KARST)) continue;',
     'if (!level.getBiome(pos).is(ModBiomes.HIGHLAND_KARST)\n                    && !level.getBiome(pos).is(ModBiomes.STONE_FOREST)) continue;'),
]:
    replace_once(p,old,new,'ModBiomes.STONE_FOREST')

# Multi-noise biome list; new climate points are deliberately biased toward
# Karst and cool, continental mountainous regions. This is NOT strict adjacency.
f = ns/'dimension/elsewheredim.json'
d = json.loads(get(f))
biomes = d['generator']['biome_source']['biomes']
name='bugsbugsandmorebugs:'
new = [
    ('stone_forest', -.3, -.23, .25, .14, 0.,0.,0.),
    ('ginkgo_forest', .40, .40, .25, .0,0.,0.,0.),
    ('hinoki_highlands', -.65, -.10, .55, -.20,0.,0.,0.),
    ('volcanic_peak', -.85, -.18, .65, -.38,0.,0.,0.)
]
for key,t,h,c,e,dep,w,o in new:
    if not any(b['biome']==name+key for b in biomes):
        biomes.append({'biome':name+key,'parameters':dict(temperature=t,humidity=h,continentalness=c,erosion=e,depth=dep,weirdness=w,offset=o)})
settext(f,json.dumps(d,indent=2)+'\n')

# Bare volcanic summit material: place ahead of the generic topsoil condition,
# but after deep Sundering/Eclogite materials. No imported stock snow features.
f = ns/'worldgen/material_rule/elsewhere.json'
d = json.loads(get(f))
seq=d['sequence']
if not any('volcanic_peak' in json.dumps(v) for v in seq):
    volcanic_rule={
        'type':'minecraft:condition',
        'if_true':{'type':'minecraft:biome','biome_is':name+'volcanic_peak'},
        'then_run':{
            'type':'minecraft:condition',
            'if_true':{'type':'minecraft:above_preliminary_surface'},
            'then_run':{'type':'minecraft:block','result_state':name+'rhyolite'}}}
    pos=next((i for i,v in enumerate(seq) if isinstance(v,dict) and 'above_preliminary_surface' in json.dumps(v)),len(seq))
    seq.insert(pos,volcanic_rule)
    settext(f,json.dumps(d,indent=2)+'\n')

# Vanilla timber is only temporary, so do not invent Ginkgo/Hinoki registrations.
# Translations are harmless until their full biome blocks arrive.
f = Path('src/main/resources/assets/bugsbugsandmorebugs/lang/en_us.json')
d = json.loads(get(f))
for k,v in {'stone_forest':'Stone Forest','ginkgo_forest':'Ginkgo Forest','hinoki_highlands':'Hinoki Highlands','volcanic_peak':'Volcanic Peak'}.items():
    d.setdefault('biome.bugsbugsandmorebugs.'+k,v)
settext(f,json.dumps(d,indent=2,ensure_ascii=False)+'\n')

# Optionally EXPERIMENTAL mountain shape: expand dimension max from Y=320 to 640
# and use an additional, biome-climate-correlated density above the existing
# final-density function. Keeps Underhang and Sundering untouched under Y=-25.
if a.mountain_prototype:
    f=java/'dimension/ModDimensions.java'
    s=get(f)
    if '768,   // Height' not in s and '768,   // Logical height' not in s:
        s=s.replace('-128,   // Minimum Y\n                        448,   // Height\n                        448,   // Logical height',
                    '-128,   // Minimum Y\n                        768,   // Height\n                        768,   // Logical height')
        if '768,   // Height' not in s:
            raise RuntimeError('Could not find the expected 448-high DimensionType parameters')
        settext(f,s)
    f=ns/'worldgen/noise_settings/elsewhere.json'
    d=json.loads(get(f))
    if d['noise']['min_y']!=-128:
        raise RuntimeError('Expected min_y=-128; refusing to change unknown dimension shape')
    if d['noise']['height'] not in (448,768):
        raise RuntimeError('Unexpected noise height; refusing terrain overwrite')
    d['noise']['height']=768
    old=d['noise_router']['final_density']
    if 'volcanic_mountain_density' not in json.dumps(old):
        d['noise_router']['final_density']={'type':'minecraft:max','left':old,'right':name+'volcanic_mountain_density'}
    settext(f,json.dumps(d,indent=2)+'\n')
    # Whole new density field references the vanilla 2D climate functions and
    # intentionally makes no changes to negative-Y terrain.
    f=ns/'worldgen/density_function/volcanic_mountain_density.json'
    if not (root/f).exists():
        altitude={'type':'minecraft:gradient','axis':'y','from_coordinate':90,'from_value':0.0,'to_coordinate':600,'to_value':1.0}
        temp={'type':'minecraft:clamp','input':{'type':'minecraft:mul','left':{'type':'minecraft:sub','left':-0.22,'right':'minecraft:overworld/temperature'},'right':1.30},'min':0.0,'max':1.0}
        cont={'type':'minecraft:clamp','input':{'type':'minecraft:mul','left':{'type':'minecraft:sub','left':'minecraft:overworld/continents','right':0.16},'right':3.0},'min':0.0,'max':1.0}
        mountain={'type':'minecraft:sub','left':{'type':'minecraft:sub','left':{'type':'minecraft:mul','left':temp,'right':cont},'right':0.12},'right':altitude}
        result={'type':'minecraft:range_choice','input':{'type':'minecraft:gradient','axis':'y','from_coordinate':-128,'from_value':-128.0,'to_coordinate':640,'to_value':640.0},'min_inclusive':-128.0,'max_exclusive':-25.0,'when_in_range':-1000000.0,'when_out_of_range':mountain}
        settext(f,json.dumps(result,indent=2)+'\n')

# Apply only after every source anchor was located and all JSON parsed.
modified=[]
for path,value in work.items():
    old=path.read_text(encoding='utf-8') if path.exists() else None
    if old==value:
        continue
    rel=path.relative_to(root)
    original=backup/rel
    if path.exists() and not original.exists():
        original.parent.mkdir(parents=True,exist_ok=True)
        shutil.copy2(path,original)
    path.parent.mkdir(parents=True,exist_ok=True)
    path.write_text(value,encoding='utf-8')
    modified.append(str(rel))
print('Updated files:',len(modified))
for p in modified: print('  ',p)
print('Backups:', backup)
if a.mountain_prototype:
    print('EXPERIMENTAL mountain density enabled. Test in a disposable new world!')
else:
    print('Mountain height and density unchanged (use --mountain-prototype for opt-in test).')
print('NEXT: .\\gradlew.bat runDatagen, then .\\gradlew.bat runClient')
