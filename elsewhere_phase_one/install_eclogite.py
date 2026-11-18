#!/usr/bin/env python3
"""Adds the Eclogite family to Bugs, Bugs, and More Bugs 26.3.
Run from the project's root with: python install_eclogite.py
Creates .eclogite-backup copies before modifying an existing file.
"""
from pathlib import Path
import sys, json, shutil, argparse

parser = argparse.ArgumentParser(description="Install Eclogite and optionally prototype Elsewhere’s uneven underside.")
parser.add_argument("--underhang", action="store_true", help="Enable experimental lower-crust carving and aquifer exclusion.")
options = parser.parse_args()

ROOT = Path.cwd()
SRC = ROOT / 'src/main/java/net/chickalo/bugsbugsandmorebugs'
RES = ROOT / 'src/main/resources'
BUNDLE = Path(__file__).resolve().parent

variants = [
    ('ECLOGITE', 'eclogite', 'Eclogite'),
    ('COBBLED_ECLOGITE', 'cobbled_eclogite', 'Cobbled Eclogite'),
    ('POLISHED_ECLOGITE', 'polished_eclogite', 'Polished Eclogite'),
    ('ECLOGITE_BRICKS', 'eclogite_bricks', 'Eclogite Bricks'),
]
extra = [('CHISELED_ECLOGITE','chiseled_eclogite','Chiseled Eclogite'),('CRACKED_ECLOGITE_BRICKS','cracked_eclogite_bricks','Cracked Eclogite Bricks')]

def edit(rel, tag, old, new):
    p = SRC / rel
    if not p.is_file(): raise RuntimeError(f'Missing {p} - run in mod root')
    s = p.read_text(encoding='utf-8')
    if tag in s:
        print(f'SKIP {rel}: Eclogite additions already present')
        return
    if old not in s: raise RuntimeError(f'Anchor not found in {rel}: {old[:100]!r}; did not change file')
    backup=p.with_suffix(p.suffix+'.eclogite-backup')
    if not backup.exists(): shutil.copy2(p,backup)
    p.write_text(s.replace(old,new+old,1),encoding='utf-8')
    print('UPDATED', rel)

block_lines = ['    // Eclogite family (Elsewhere deepest stone)']
for sym,id_,_ in variants+extra:
    block_lines += [f'    public static final Block {sym} = registerBlock("{id_}",',
                    '            properties -> new Block(properties.strength(5.0f, 6.0f)',
                    '                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));']
for sym,id_,_ in variants:
    for suffix,cl in [('STAIRS','StairBlock'),('SLAB','SlabBlock'),('WALL','WallBlock')]:
        id2=f'{id_}_{suffix.lower()}'
        args=f'{sym}.defaultBlockState(), properties.strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.STONE)' if suffix=='STAIRS' else 'properties.strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.STONE)'
        block_lines += [f'    public static final Block {sym}_{suffix} = registerBlock("{id2}",',f'            properties -> new {cl}({args}));']
block_code='\n'.join(block_lines)+'\n\n'

model_code='\n'.join([f'        blockModelGenerators.family(ModBlocks.{sym})\n                .stairs(ModBlocks.{sym}_STAIRS)\n                .slab(ModBlocks.{sym}_SLAB)\n                .wall(ModBlocks.{sym}_WALL);' for sym,_,_ in variants] + [f'        blockModelGenerators.createTrivialCube(ModBlocks.{sym});' for sym,_,_ in extra])+'\n'
loot_code='''        add(ModBlocks.ECLOGITE,
                createSingleItemTableWithSilkTouch(ModBlocks.ECLOGITE, ModBlocks.COBBLED_ECLOGITE));
'''
for sym,_,_ in variants[1:]+extra:
    loot_code+=f'        dropSelf(ModBlocks.{sym});\n'
for sym,_,_ in variants:
    loot_code+=f'        dropSelf(ModBlocks.{sym}_STAIRS);\n        dropSelf(ModBlocks.{sym}_WALL);\n        add(ModBlocks.{sym}_SLAB, this::createSlabItemTable);\n'

recipe_code='''                // Eclogite family
                // 4 blocks in a square create the polished and brick variants.
                shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_ECLOGITE, 4)
                        .pattern("##").pattern("##").define('#', ModBlocks.COBBLED_ECLOGITE)
                        .unlockedBy(getHasName(ModBlocks.COBBLED_ECLOGITE), has(ModBlocks.COBBLED_ECLOGITE)).save(output);
                shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ECLOGITE_BRICKS, 4)
                        .pattern("##").pattern("##").define('#', ModBlocks.POLISHED_ECLOGITE)
                        .unlockedBy(getHasName(ModBlocks.POLISHED_ECLOGITE), has(ModBlocks.POLISHED_ECLOGITE)).save(output);
                shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_ECLOGITE)
                        .pattern("#").pattern("#").define('#', ModBlocks.ECLOGITE_BRICKS_SLAB)
                        .unlockedBy(getHasName(ModBlocks.ECLOGITE_BRICKS_SLAB), has(ModBlocks.ECLOGITE_BRICKS_SLAB)).save(output);
                SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.COBBLED_ECLOGITE), RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.BLOCKS,
                        ModBlocks.ECLOGITE, 0.1F, 200)
                        .unlockedBy(getHasName(ModBlocks.COBBLED_ECLOGITE), has(ModBlocks.COBBLED_ECLOGITE))
                        .save(output);
                SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.ECLOGITE_BRICKS), RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.BLOCKS,
                        ModBlocks.CRACKED_ECLOGITE_BRICKS, 0.1F, 200)
                        .unlockedBy(getHasName(ModBlocks.ECLOGITE_BRICKS), has(ModBlocks.ECLOGITE_BRICKS))
                        .save(output);
'''
for sym,_,_ in variants:
    recipe_code+=f'''                stairBuilder(ModBlocks.{sym}_STAIRS, Ingredient.of(ModBlocks.{sym}))
                        .unlockedBy(getHasName(ModBlocks.{sym}), has(ModBlocks.{sym}))
                        .group("eclogite").save(output);
                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.{sym}_SLAB, ModBlocks.{sym});
                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.{sym}_WALL, Ingredient.of(ModBlocks.{sym}))
                        .unlockedBy(getHasName(ModBlocks.{sym}), has(ModBlocks.{sym}))
                        .group("eclogite").save(output);
'''

all_syms=[s for s,_,_ in variants+extra]+[f'{s}_{suf}' for s,_,_ in variants for suf in ['STAIRS','SLAB','WALL']]
tags_code='        tag(BlockTags.MINEABLE_WITH_PICKAXE)\n'+''.join(f'                .add(ModBlocks.getRK(ModBlocks.{s}))\n' for s in all_syms).rstrip()+';\n'
tags_code+='''
        tag(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                .add(ModBlocks.getRK(ModBlocks.ECLOGITE));

        tag(BlockTags.WALLS)
'''+''.join(f'                .add(ModBlocks.getRK(ModBlocks.{s}_WALL))\n' for s,_,_ in variants).rstrip()+';\n'

# all anchors checked before first modification, to avoid partial patch
jobs=[
 ('block/ModBlocks.java','public static final Block ECLOGITE =','    // Limestone family',block_code),
 ('datagen/ModModelProvider.java','blockModelGenerators.family(ModBlocks.ECLOGITE)','        blockModelGenerators.createTrivialCube(ModBlocks.MOSSY_LIMESTONE);',model_code),
 ('datagen/ModBlockLootTableProvider.java','createSingleItemTableWithSilkTouch(ModBlocks.ECLOGITE','        add(\n                ModBlocks.LIMESTONE,',loot_code+'\n'),
 ('datagen/ModRecipeProvider.java','// Eclogite family','                // lwk gonna need a custom builder',recipe_code+'\n'),
 ('datagen/ModBlockTagsProvider.java','ModBlocks.ECLOGITE','        tag(BlockTags.STONE_ORE_REPLACEABLES)',tags_code+'\n'),
]
if not SRC.exists(): raise SystemExit('Run this script from the project root (containing src/main/java).')
if 'public static final Block SCHIST =' not in (SRC/'block/ModBlocks.java').read_text(encoding='utf-8'):
    raise SystemExit('Schist must be registered before installing Eclogite.')
for required in ['material_rule/elsewhere.json', 'noise_settings/elsewhere.json']:
    if not (RES/'data/bugsbugsandmorebugs/worldgen'/required).is_file():
        raise SystemExit('Missing Elsewhere resource: '+required)

for rel,tag,anchor,content in jobs:
    p=SRC/rel
    if not p.exists():raise SystemExit(f'Missing {p}')
    s=p.read_text(encoding='utf-8')
    if tag not in s and anchor not in s:raise SystemExit(f'Could not locate insertion point in {rel}; no changes made')

for rel,tag,anchor,content in jobs: edit(rel,tag,anchor,content)

p=SRC/'datagen/ModRecipeProvider.java'
s=p.read_text(encoding='utf-8')
if 'import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;' not in s:
    s=s.replace('import net.minecraft.data.recipes.RecipeProvider;', 'import net.minecraft.data.recipes.RecipeProvider;\nimport net.minecraft.data.recipes.SimpleCookingRecipeBuilder;')
    p.write_text(s,encoding='utf-8')
if 'import net.minecraft.world.item.crafting.CookingBookCategory;' not in s:
    s=s.replace('import net.minecraft.world.item.crafting.Ingredient;', 'import net.minecraft.world.item.crafting.Ingredient;\nimport net.minecraft.world.item.crafting.CookingBookCategory;')
    p.write_text(s,encoding='utf-8')

lang_path=RES/'assets/bugsbugsandmorebugs/lang/en_us.json'
lang=json.loads(lang_path.read_text(encoding='utf-8'))
for sym,id_,display in variants+extra:lang[f'block.bugsbugsandmorebugs.{id_}']=display
for sym,id_,display in variants:
    for suffix,disp in [('stairs','Stairs'),('slab','Slab'),('wall','Wall')]:
        lang[f'block.bugsbugsandmorebugs.{id_}_{suffix}']=f'{display.removesuffix("s") if display.endswith("Bricks") else display} {disp}'
backup=lang_path.with_suffix('.json.eclogite-backup')
if not backup.exists():shutil.copy2(lang_path,backup)
lang_path.write_text(json.dumps(lang,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
print('UPDATED language file')

# Preserve existing art; only report missing textures.
texture_dir=RES/'assets/bugsbugsandmorebugs/textures/block'
for _,id_,_ in variants+extra:
    if not (texture_dir/(id_+'.png')).is_file():
        print('ADD YOUR TEXTURE:', id_+'.png')

# Update material rules in the custom namespace only. Eclogite starts at -33;
# the future disaster horizon is reserved at -32 through -30 (still Schist).
rule=RES/'data/bugsbugsandmorebugs/worldgen/material_rule/elsewhere.json'
noise=RES/'data/bugsbugsandmorebugs/worldgen/noise_settings/elsewhere.json'
def save_json(path, data):
    backup=path.with_suffix(path.suffix+'.eclogite-backup')
    if not backup.exists(): shutil.copy2(path,backup)
    path.write_text(json.dumps(data,indent=2)+'\n',encoding='utf-8')

def state(name): return {'type':'minecraft:block','result_state':name}
def gradient_rule(name, at, above, material):
    return {'type':'minecraft:condition','if_true':{'type':'minecraft:vertical_gradient','false_at_and_above':{'absolute':above},'random_name':'bugsbugsandmorebugs:'+name,'true_at_and_below':{'absolute':at}},'then_run':state(material)}
if not rule.is_file() or not noise.is_file():
    raise SystemExit('Missing Elsewhere JSONs. Add them before generating the terrain.')
mat=json.loads(rule.read_text(encoding='utf-8'))
seq=mat['sequence']
# Upgrade the older temporary deepslate rule too, if Schist is registered.
block_source=(SRC/'block/ModBlocks.java').read_text(encoding='utf-8')
if 'public static final Block SCHIST =' not in block_source:
    raise SystemExit('Schist is not registered. Install the Schist family before Eclogite.')
for entry in seq:
    if isinstance(entry,dict) and isinstance(entry.get('then_run'),dict) and entry['then_run'].get('result_state')=='minecraft:deepslate':
        entry['then_run']['result_state']='bugsbugsandmorebugs:schist'
# Put the deep eclogite material before vanilla ore veins and surfaces, so those
# material rules cannot turn it into tuff or stone. Placed features remain separate.
if not any(isinstance(x,dict) and isinstance(x.get('then_run'),dict) and x['then_run'].get('result_state')=='bugsbugsandmorebugs:eclogite' for x in seq):
    seq.insert(0,gradient_rule('eclogite_top',-33,-32,'bugsbugsandmorebugs:eclogite'))
save_json(rule,mat)
print('UPDATED Elsewhere Eclogite material layer')

if options.underhang:
    settings=json.loads(noise.read_text(encoding='utf-8'))
    router=settings['noise_router']
    marker='bugsbugsandmorebugs:underhang_mask'
    def binary(t,l,r): return {'type':'minecraft:'+t,'left':l,'right':r}
    def grad(a,av,b,bv): return {'type':'minecraft:gradient','axis':'y','from_coordinate':a,'from_value':av,'to_coordinate':b,'to_value':bv}
    def band(lo,hi,inside,outside):
        return {'type':'minecraft:range_choice','input':grad(-64,-64.0,320,320.0),'min_inclusive':lo,'max_exclusive':hi,'when_in_range':inside,'when_out_of_range':outside}
    # Two horizontal noise scales give broad uneven valleys plus rougher contours.
    # Approximate zero crossing near Y=-59, shifted by noise. Preserve all density
    # above Y=-40; do not replace the existing final density or other router fields.
    mask=binary('add',grad(-64,-0.55,-48,1.05),binary('add',binary('mul',0.62,{'type':'minecraft:noise','noise':'minecraft:surface','xz_scale':0.55,'y_scale':0.0}),binary('mul',0.18,{'type':'minecraft:noise','noise':'minecraft:surface_secondary','xz_scale':1.25,'y_scale':0.0})))
    density_dir=RES/'data/bugsbugsandmorebugs/worldgen/density_function'
    density_dir.mkdir(parents=True,exist_ok=True)
    mask_path=density_dir/'underhang_mask.json'
    if not mask_path.exists(): mask_path.write_text(json.dumps(mask,indent=2)+'\n',encoding='utf-8')
    import copy
    old=router['final_density']
    if marker not in json.dumps(old):
        router['final_density']=band(-64.0,-40.0,binary('min',copy.deepcopy(old),marker),copy.deepcopy(old))
    # Positive exclusion suppresses aquifer cells. Vanilla deep-lava behavior may
    # still produce lava; this is a terrain prototype, not the finished Underhang.
    if 'aquifers' in settings:
        exclusion=settings['aquifers']['exclusion']
        boundary=grad(-46,1.0,-40,-1.0)
        if boundary not in [exclusion,exclusion.get('right') if isinstance(exclusion,dict) else None]:
            settings['aquifers']['exclusion']=binary('max',exclusion,boundary)
    save_json(noise,settings)
    print('UPDATED experimental Underhang density and lower aquifer exclusion')
print('Done! Next: .\\gradlew.bat runDatagen then .\\gradlew.bat runClient')
