#!/usr/bin/env python3
"""Adds the Schist family to Bugs, Bugs, and More Bugs 26.3.
Run from the project's root with: python install_schist.py
Creates .schist-backup copies before modifying an existing file.
"""
from pathlib import Path
import sys, json, shutil

ROOT = Path.cwd()
SRC = ROOT / 'src/main/java/net/chickalo/bugsbugsandmorebugs'
RES = ROOT / 'src/main/resources'
BUNDLE = Path(__file__).resolve().parent

variants = [
    ('SCHIST', 'schist', 'Schist'),
    ('COBBLED_SCHIST', 'cobbled_schist', 'Cobbled Schist'),
    ('POLISHED_SCHIST', 'polished_schist', 'Polished Schist'),
    ('SCHIST_BRICKS', 'schist_bricks', 'Schist Bricks'),
]
extra = [('CHISELED_SCHIST','chiseled_schist','Chiseled Schist'),('CRACKED_SCHIST_BRICKS','cracked_schist_bricks','Cracked Schist Bricks')]

def edit(rel, tag, old, new):
    p = SRC / rel
    if not p.is_file(): raise RuntimeError(f'Missing {p} - run in mod root')
    s = p.read_text(encoding='utf-8')
    if tag in s:
        print(f'SKIP {rel}: Schist additions already present')
        return
    if old not in s: raise RuntimeError(f'Anchor not found in {rel}: {old[:100]!r}; did not change file')
    backup=p.with_suffix(p.suffix+'.schist-backup')
    if not backup.exists(): shutil.copy2(p,backup)
    p.write_text(s.replace(old,new+old,1),encoding='utf-8')
    print('UPDATED', rel)

block_lines = ['    // Schist family (Elsewhere deep stone)']
for sym,id_,_ in variants+extra:
    block_lines += [f'    public static final Block {sym} = registerBlock("{id_}",',
                    '            properties -> new Block(properties.strength(3.5f)',
                    '                    .requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM)));']
for sym,id_,_ in variants:
    for suffix,cl in [('STAIRS','StairBlock'),('SLAB','SlabBlock'),('WALL','WallBlock')]:
        id2=f'{id_}_{suffix.lower()}'
        args=f'{sym}.defaultBlockState(), properties.strength(3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE)' if suffix=='STAIRS' else 'properties.strength(3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE)'
        block_lines += [f'    public static final Block {sym}_{suffix} = registerBlock("{id2}",',f'            properties -> new {cl}({args}));']
block_code='\n'.join(block_lines)+'\n\n'

model_code='\n'.join([f'        blockModelGenerators.family(ModBlocks.{sym})\n                .stairs(ModBlocks.{sym}_STAIRS)\n                .slab(ModBlocks.{sym}_SLAB)\n                .wall(ModBlocks.{sym}_WALL);' for sym,_,_ in variants] + [f'        blockModelGenerators.createTrivialCube(ModBlocks.{sym});' for sym,_,_ in extra])+'\n'
loot_code='''        add(ModBlocks.SCHIST,
                createSingleItemTableWithSilkTouch(ModBlocks.SCHIST, ModBlocks.COBBLED_SCHIST));
'''
for sym,_,_ in variants[1:]+extra:
    loot_code+=f'        dropSelf(ModBlocks.{sym});\n'
for sym,_,_ in variants:
    loot_code+=f'        dropSelf(ModBlocks.{sym}_STAIRS);\n        dropSelf(ModBlocks.{sym}_WALL);\n        add(ModBlocks.{sym}_SLAB, this::createSlabItemTable);\n'

recipe_code='''                // Schist family
                // 4 blocks in a square create the polished and brick variants.
                bricksBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_SCHIST, Ingredient.of(ModBlocks.SCHIST));
                bricksBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SCHIST_BRICKS, Ingredient.of(ModBlocks.POLISHED_SCHIST));
                chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_SCHIST, Ingredient.of(ModBlocks.SCHIST_BRICK_SLAB));
                SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.COBBLED_SCHIST), RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.SCHIST, 0.1F, 200)
                        .unlockedBy(getHasName(ModBlocks.COBBLED_SCHIST), has(ModBlocks.COBBLED_SCHIST))
                        .save(output);
                SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.SCHIST_BRICKS), RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.CRACKED_SCHIST_BRICKS, 0.1F, 200)
                        .unlockedBy(getHasName(ModBlocks.SCHIST_BRICKS), has(ModBlocks.SCHIST_BRICKS))
                        .save(output);
'''
for sym,_,_ in variants:
    recipe_code+=f'''                stairBuilder(ModBlocks.{sym}_STAIRS, Ingredient.of(ModBlocks.{sym}))
                        .unlockedBy(getHasName(ModBlocks.{sym}), has(ModBlocks.{sym}))
                        .group("schist").save(output);
                slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.{sym}_SLAB, ModBlocks.{sym});
                wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.{sym}_WALL, Ingredient.of(ModBlocks.{sym}))
                        .unlockedBy(getHasName(ModBlocks.{sym}), has(ModBlocks.{sym}))
                        .group("schist").save(output);
'''

all_syms=[s for s,_,_ in variants+extra]+[f'{s}_{suf}' for s,_,_ in variants for suf in ['STAIRS','SLAB','WALL']]
tags_code='        tag(BlockTags.MINEABLE_WITH_PICKAXE)\n'+''.join(f'                .add(ModBlocks.getRK(ModBlocks.{s}))\n' for s in all_syms).rstrip()+';\n'
tags_code+='''
        tag(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                .add(ModBlocks.getRK(ModBlocks.SCHIST));

        tag(BlockTags.WALLS)
'''+''.join(f'                .add(ModBlocks.getRK(ModBlocks.{s}_WALL))\n' for s,_,_ in variants).rstrip()+';\n'

# all anchors checked before first modification, to avoid partial patch
jobs=[
 ('block/ModBlocks.java','public static final Block SCHIST =','    // eventually the deepslate version of rhyolite will go here',block_code),
 ('datagen/ModModelProvider.java','blockModelGenerators.family(ModBlocks.SCHIST)','        blockModelGenerators.createTrivialCube(ModBlocks.MOSSY_LIMESTONE);',model_code),
 ('datagen/ModBlockLootTableProvider.java','createSingleItemTableWithSilkTouch(ModBlocks.SCHIST','        add(\n                ModBlocks.LIMESTONE,',loot_code+'\n'),
 ('datagen/ModRecipeProvider.java','// Schist family','                // lwk gonna need a custom builder',recipe_code+'\n'),
 ('datagen/ModBlockTagsProvider.java','ModBlocks.SCHIST','        tag(BlockTags.STONE_ORE_REPLACEABLES)',tags_code+'\n'),
]
if not SRC.exists(): raise SystemExit('Run this script from the project root (containing src/main/java).')
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

lang_path=RES/'assets/bugsbugsandmorebugs/lang/en_us.json'
lang=json.loads(lang_path.read_text(encoding='utf-8'))
for sym,id_,display in variants+extra:lang[f'block.bugsbugsandmorebugs.{id_}']=display
for sym,id_,display in variants:
    for suffix,disp in [('stairs','Stairs'),('slab','Slab'),('wall','Wall')]:
        lang[f'block.bugsbugsandmorebugs.{id_}_{suffix}']=f'{display} {disp}'
backup=lang_path.with_suffix('.json.schist-backup')
if not backup.exists():shutil.copy2(lang_path,backup)
lang_path.write_text(json.dumps(lang,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
print('UPDATED language file')

source=BUNDLE/'assets/bugsbugsandmorebugs/textures/block'
target=RES/'assets/bugsbugsandmorebugs/textures/block'
target.mkdir(parents=True,exist_ok=True)
for file in source.glob('*.png'):
    dest=target/file.name
    if not dest.exists():shutil.copy2(file,dest);print('PLACEHOLDER texture',dest.name)

# Optional material rule update: swap vanilla deepslate to schist for Elsewhere
rule=RES/'data/bugsbugsandmorebugs/worldgen/material_rule/elsewhere.json'
if rule.exists():
    original=rule.read_text(encoding='utf-8')
    new=original.replace('"result_state": "minecraft:deepslate"', '"result_state": "bugsbugsandmorebugs:schist"')
    if original!=new:
        bk=rule.with_suffix('.json.schist-backup')
        if not bk.exists():shutil.copy2(rule,bk)
        rule.write_text(new,encoding='utf-8')
        print('UPDATED Elsewhere deep stone material rule')
else:
    print('NOTE Elsewhere material rule not found; schist will exist but not generate underground yet.')
print('\nDone! Next: .\\gradlew.bat runDatagen  then  .\\gradlew.bat runClient')
