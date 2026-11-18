#!/usr/bin/env python3
"""Minecraft 26.3 Elsewhere: lava / new stone / lakebed quick patch.

Run from mod root, after Phase One and Phase 1.1. Uses first-write backups.
Does not overwrite texture art or change vanilla worldgen.
"""
from pathlib import Path
import json, re, shutil, sys
R=Path.cwd()
NS='bugsbugsandmorebugs'
SRC=R/'src/main/java/net/chickalo'/NS
RES=R/'src/main/resources'
BACK=R/'.elsewhere-stones-quickpatch-backup'
if not (R/'gradlew.bat').exists() or not (SRC/'block/ModBlocks.java').exists():
    sys.exit('Run this from your mod root, beside gradlew.bat.')

def write(p, content):
    if p.exists():
        existing=p.read_text(encoding='utf8')
        if existing==content:return
        target=BACK/p.relative_to(R);target.parent.mkdir(parents=True,exist_ok=True)
        if not target.exists():shutil.copy2(p,target)
    p.parent.mkdir(parents=True,exist_ok=True)
    p.write_text(content,encoding='utf8');print('UPDATED',p.relative_to(R))

def insert(s, anchor, addition):
    if addition in s:return s
    if anchor not in s:raise RuntimeError('Cannot find expected insertion anchor: '+anchor[:110])
    return s.replace(anchor,addition+'\n'+anchor,1)

def get(path):return path.read_text(encoding='utf8')

def save_json(path,obj):write(path,json.dumps(obj,indent=2,ensure_ascii=False)+'\n')

# Check prerequisites before modifying files.
blocks_path=SRC/'block/ModBlocks.java';blocks=get(blocks_path)
for name in ('RHYOLITE','LIMESTONE','SCHIST','ASHSTONE','SHATTERSTONE'):
    if not re.search(r'\b'+name+r'\b',blocks):
        sys.exit('Missing '+name+'; install earlier Elsewhere phases first.')

# 1. Deep lava fix (26.3).
# The vanilla global fluid picker uses LAVA below min(-54, sea_level).
# Thus default_fluid=air alone is NOT enough. Sea level below the entire
# dimension (-256 < min Y -128) ensures the hardcoded lava range is unreachable.
# Disabling aquifers further ensures no local fluid tables introduce fluids.
noise_path=RES/'data'/NS/'worldgen/noise_settings/elsewhere.json'
noise=json.loads(get(noise_path))
noise['sea_level']=-256
noise['default_fluid']='minecraft:air'
# Minecraft 26.3 expects the aquifers field. Keep its registry codec structure.
# The lava noise is neutralized, and the sea-level cutoff is below the world.
if 'aquifers' in noise:
    noise['aquifers']['lava'] = 0.0
save_json(noise_path,noise)
print('  Fluid fix: sea_level=-256, default_fluid=air, aquifer lava noise neutralized.')

# 2. New blocks: keep existing registrations (user may have added some already).
# Amphibolite family: amphibolite, polished, bricks, chiseled, chiseled bricks,
# stairs/slabs/walls for the three main construction materials.
# Plain amphibolite is NOT a falling block; use it as a decorative gravel replacement.
family=['amphibolite','polished_amphibolite','amphibolite_bricks','chiseled_amphibolite','chiseled_amphibolite_bricks']
family += [q+'_'+shape for q in ['amphibolite','polished_amphibolite','amphibolite_brick'] for shape in ['stairs','slab','wall']]
family.insert(0,'mossy_rhyolite')
family.append('lapilli')
base_var={'amphibolite_stairs':'AMPHIBOLITE','amphibolite_slab':'AMPHIBOLITE','amphibolite_wall':'AMPHIBOLITE',
          'polished_amphibolite_stairs':'POLISHED_AMPHIBOLITE','polished_amphibolite_slab':'POLISHED_AMPHIBOLITE','polished_amphibolite_wall':'POLISHED_AMPHIBOLITE',
          'amphibolite_brick_stairs':'AMPHIBOLITE_BRICKS','amphibolite_brick_slab':'AMPHIBOLITE_BRICKS','amphibolite_brick_wall':'AMPHIBOLITE_BRICKS'}
existing={name.upper() for name in family if re.search(r'\b'+name.upper()+r'\s*=\s*registerBlock\(',blocks)}
add=[]
for name in family:
    var=name.upper()
    if var in existing:continue
    if name.endswith('_stairs'):
        ctor='new StairBlock(ModBlocks.'+base_var[name]+'.defaultBlockState(), properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE))'
    elif name.endswith('_slab'):
        ctor='new SlabBlock(properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE))'
    elif name.endswith('_wall'):
        ctor='new WallBlock(properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE))'
    elif name=='lapilli':
        ctor='new FallingBlock(properties.strength(0.6f).sound(SoundType.GRAVEL))'
    else:
        ctor='new Block(properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM))'
    add.append('    public static final Block '+var+' = registerBlock("'+name+'",\n            properties -> '+ctor+');')
if add:
    # Place new registrations before maple, or at the beginning if no Maple comment.
    anchor='    // Maple family' if '    // Maple family' in blocks else '    public static ResourceKey<Block> getRK('
    blocks=insert(blocks,anchor,'    // Elsewhere: Mossy Rhyolite and Amphibolite family\n'+'\n'.join(add)+'\n')
    write(blocks_path,blocks)

# 3. Model DataGen: Minecraft 26.3 family() generates blockstates, models, item models.
p=SRC/'datagen/ModModelProvider.java';s=get(p)
model_specs=[('AMPHIBOLITE',['AMPHIBOLITE_STAIRS','AMPHIBOLITE_SLAB','AMPHIBOLITE_WALL']),
('POLISHED_AMPHIBOLITE',['POLISHED_AMPHIBOLITE_STAIRS','POLISHED_AMPHIBOLITE_SLAB','POLISHED_AMPHIBOLITE_WALL']),
('AMPHIBOLITE_BRICKS',['AMPHIBOLITE_BRICK_STAIRS','AMPHIBOLITE_BRICK_SLAB','AMPHIBOLITE_BRICK_WALL'])]
models=[]
for base,shapes in model_specs:
    if ('blockModelGenerators.family(ModBlocks.'+base+')') not in s:
        models.append('        blockModelGenerators.family(ModBlocks.'+base+')\n'+
              '                .stairs(ModBlocks.'+shapes[0]+')\n'+
              '                .slab(ModBlocks.'+shapes[1]+')\n'+
              '                .wall(ModBlocks.'+shapes[2]+');')
for var in ['MOSSY_RHYOLITE','CHISELED_AMPHIBOLITE','CHISELED_AMPHIBOLITE_BRICKS','LAPILLI']:
    stmt='blockModelGenerators.createTrivialCube(ModBlocks.'+var+');'
    if stmt not in s:models.append('        '+stmt)
if models:
    # Add immediately after method opening.
    anchor='    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {'
    if anchor not in s:raise RuntimeError('Cannot locate ModelProvider block method')
    s=s.replace(anchor,anchor+'\n'+'\n'.join(models),1)
    write(p,s)

# 4. Loot: self-drops except slabs use double-slab loot tables.
p=SRC/'datagen/ModBlockLootTableProvider.java';s=get(p)
loot=[]
for name in family:
    var=name.upper();ref='ModBlocks.'+var
    if re.search(r'(?:dropSelf\s*\(\s*|add\s*\(\s*)'+re.escape(ref)+r'\b',s):continue
    if name.endswith('_slab'):loot.append('        add('+ref+', this::createSlabItemTable);')
    else:loot.append('        dropSelf('+ref+');')
if loot:
    anchor='    public void generate() {'
    if anchor not in s:raise RuntimeError('Cannot locate LootProvider method')
    s=s.replace(anchor,anchor+'\n'+'\n'.join(loot),1)
    write(p,s)

# 5. BlockTags: pickaxe and walls, and optional stone-ore-replaceables for natural amphibolite.
p=SRC/'datagen/ModBlockTagsProvider.java';s=get(p)
start='    protected void addTags(HolderLookup.Provider registries) {'
if start not in s:raise RuntimeError('No BlockTags addTags method')
newtags=[]
for name in family:
    if name=='lapilli': continue  # shovel / falling sediment, not pickaxe
    var=name.upper();line='                .add(ModBlocks.getRK(ModBlocks.'+var+'))'
    # avoid duplicates by scanning current file
    if line not in s:newtags.append(line)
# a new tag invocation is independent of the existing block tags and avoids touching them.
addition='\n        tag(BlockTags.MINEABLE_WITH_PICKAXE)\n'+'\n'.join(newtags)+';\n' if newtags else ''
wall=['AMPHIBOLITE_WALL','POLISHED_AMPHIBOLITE_WALL','AMPHIBOLITE_BRICK_WALL']
wallnew=[v for v in wall if 'ModBlocks.getRK(ModBlocks.'+v+')' not in s]
if wallnew:addition+='\n        tag(BlockTags.WALLS)\n'+'\n'.join('                .add(ModBlocks.getRK(ModBlocks.'+v+'))' for v in wallnew)+';\n'
if 'ModBlocks.getRK(ModBlocks.LAPILLI)' not in s:
    addition+='\n        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.getRK(ModBlocks.LAPILLI));\n'
# Amphibolite naturally appears in terrain features, ore replaces it like base stone.
if 'ModBlocks.getRK(ModBlocks.AMPHIBOLITE))' not in s.split('tag(BlockTags.STONE_ORE_REPLACEABLES)')[-1].split(';')[0]:
    addition+='\n        tag(BlockTags.STONE_ORE_REPLACEABLES).add(ModBlocks.getRK(ModBlocks.AMPHIBOLITE));\n'
if addition:
    s=s.replace(start,start+'\n'+addition,1)
    write(p,s)

# 6. RecipeProvider: use existing 26.3 helper methods (no old smelting signature).
p=SRC/'datagen/ModRecipeProvider.java';s=get(p)
recipes=[]
for base,prefix in [('AMPHIBOLITE','AMPHIBOLITE'),('POLISHED_AMPHIBOLITE','POLISHED_AMPHIBOLITE'),('AMPHIBOLITE_BRICKS','AMPHIBOLITE_BRICK')]:
    for shape in ['STAIRS','SLAB','WALL']:
        target='ModBlocks.'+prefix+'_'+shape
        if re.search(r'\b(?:stairBuilder|wallBuilder|slab)\s*\([^;]*?'+re.escape(target),s,re.S):continue
        if shape=='SLAB':
            line='                slab(RecipeCategory.BUILDING_BLOCKS, '+target+', ModBlocks.'+base+');'
        else:
            method='stairBuilder' if shape=='STAIRS' else 'wallBuilder'
            starter=(method+'('+target+', Ingredient.of(ModBlocks.'+base+'))' if shape=='STAIRS' else method+'(RecipeCategory.BUILDING_BLOCKS, '+target+', Ingredient.of(ModBlocks.'+base+'))')
            line='                '+starter+'\n                        .unlockedBy(getHasName(ModBlocks.'+base+'), has(ModBlocks.'+base+'))\n                        .group("amphibolite")\n                        .save(output);'
        recipes.append(line)
for kind,target,source in [('polished','POLISHED_AMPHIBOLITE','AMPHIBOLITE'),('bricks','AMPHIBOLITE_BRICKS','POLISHED_AMPHIBOLITE'),('chiseled','CHISELED_AMPHIBOLITE','AMPHIBOLITE_SLAB'),('chiseled','CHISELED_AMPHIBOLITE_BRICKS','AMPHIBOLITE_BRICK_SLAB')]:
    if re.search(r'\b(?:bricksBuilder|chiseledBuilder)\s*\([^;]*?ModBlocks\.'+target,s,re.S):continue
    # User project exposes bricksBuilder and chiseledBuilder.
    recipes.append('                '+('bricksBuilder' if kind!='chiseled' else 'chiseledBuilder')+'(RecipeCategory.BUILDING_BLOCKS, ModBlocks.'+target+', Ingredient.of(ModBlocks.'+source+'));')
if recipes:
    anchor='            public void buildRecipes() {'
    if anchor not in s:raise RuntimeError('RecipeProvider buildRecipes anchor missing')
    s=s.replace(anchor,anchor+'\n'+'\n'.join(recipes),1);write(p,s)

# 7. English language translations.
p=RES/'assets'/NS/'lang/en_us.json';lang=json.loads(get(p))
for name in family:
    title=name.replace('_',' ').title()
    if name=='mossy_rhyolite':title='Mossy Rhyolite'
    lang.setdefault('block.'+NS+'.'+name,title)
save_json(p,lang)

# 8. Targeted custom lake/shore geometry blocks only.
lake=SRC/'worldgen/feature/MapleLakeFeature.java'
if lake.exists():
    s=get(lake)
    desired="""    private static BlockState randomLakebedBlock(RandomSource random) {
        // Elsewhere lakebed: amphibolite rubble, rhyolite and sparse mossy stone.
        int roll = random.nextInt(100);
        if (roll < 33) return ModBlocks.AMPHIBOLITE.defaultBlockState();
        if (roll < 66) return ModBlocks.COBBLED_RHYOLITE.defaultBlockState();
        if (roll < 81) return ModBlocks.RHYOLITE.defaultBlockState();
        if (roll < 92) return ModBlocks.MOSSY_RHYOLITE.defaultBlockState();
        return ModBlocks.LAPILLI.defaultBlockState();
    }"""
    s2,n=re.subn(r'    private static BlockState randomLakebedBlock\(RandomSource random\)\s*\{[\s\S]*?\n    \}',desired,s,count=1)
    if n!=1:raise RuntimeError('Cannot locate MapleLakeFeature.randomLakebedBlock')
    s=s2
    if 'import net.chickalo.'+NS+'.block.ModBlocks;' not in s:
        s=s.replace('\nimport ', '\nimport net.chickalo.'+NS+'.block.ModBlocks;\nimport ',1)
    write(lake,s)
shore=SRC/'worldgen/feature/MapleWaterShoreFeature.java'
if shore.exists():
    s=get(shore)
    def replace_method(s,method,new):
        pattern=r'    private static BlockState '+method+r'\(RandomSource random\)\s*\{[\s\S]*?\n    \}'
        s2,n=re.subn(pattern,new,s,count=1)
        if n!=1:raise RuntimeError('Cannot find shore '+method)
        return s2
    s=replace_method(s,'boulderMaterial',"""    private static BlockState boulderMaterial(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.62F) return ModBlocks.MOSSY_RHYOLITE.defaultBlockState();
        if (roll < 0.82F) return ModBlocks.COBBLED_RHYOLITE.defaultBlockState();
        if (roll < 0.94F) return ModBlocks.AMPHIBOLITE.defaultBlockState();
        return Blocks.MOSS_BLOCK.defaultBlockState();
    }""")
    s=replace_method(s,'randomBankBlock',"""    private static BlockState randomBankBlock(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.29F) return Blocks.MOSS_BLOCK.defaultBlockState();
        if (roll < 0.56F) return ModBlocks.MOSSY_RHYOLITE.defaultBlockState();
        if (roll < 0.75F) return ModBlocks.AMPHIBOLITE.defaultBlockState();
        if (roll < 0.86F) return ModBlocks.LAPILLI.defaultBlockState();
        if (roll < 0.94F) return Blocks.CLAY.defaultBlockState();
        if (roll < 0.97F) return Blocks.COARSE_DIRT.defaultBlockState();
        return ModBlocks.RHYOLITE.defaultBlockState();
    }""")
    # New stones must qualify as natural substrate for rock placement.
    marker='    private static boolean isNaturalGround(BlockState state) {'
    if marker in s and 'state.is(ModBlocks.MOSSY_RHYOLITE)' not in s:
        s=s.replace(marker,marker+'\n        if (state.is(ModBlocks.MOSSY_RHYOLITE) || state.is(ModBlocks.COBBLED_RHYOLITE)\n                || state.is(ModBlocks.RHYOLITE) || state.is(ModBlocks.AMPHIBOLITE)) return true;',1)
    write(shore,s)

# 9. Replace only explicit loose-gravel placements in the mod's custom worldgen.
# Don't rewrite vanilla block predicates or unrelated biomes.
for feature in (SRC/'worldgen/feature').glob('*.java'):
    if feature.name in ('MapleLakeFeature.java','MapleWaterShoreFeature.java'):
        continue
    source=get(feature)
    updated=source.replace('Blocks.GRAVEL.defaultBlockState()', 'ModBlocks.LAPILLI.defaultBlockState()')
    if updated!=source:
        if 'import net.chickalo.'+NS+'.block.ModBlocks;' not in updated:
            updated=updated.replace('\nimport ', '\nimport net.chickalo.'+NS+'.block.ModBlocks;\nimport ', 1)
        write(feature,updated)

# 10. Amphibolite underground deposits, following tuff-like dense stone pockets.
# Only schist/eclogite blocks are replaced; caves and Sundering Horizon stay intact.
feature_path=SRC/'worldgen/feature/AmphiboliteDepositFeature.java'
feature_java="""package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

/** Small to medium amphibolite bodies hosted by Elsewhere's metamorphic rock. */
public record AmphiboliteDepositFeature() implements Feature {
    public static final MapCodec<AmphiboliteDepositFeature> CODEC =
            MapCodec.unit(new AmphiboliteDepositFeature());

    @Override
    public MapCodec<AmphiboliteDepositFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator,
                         RandomSource random, BlockPos origin) {
        int rx = 2 + random.nextInt(4);
        int ry = 2 + random.nextInt(3);
        int rz = 2 + random.nextInt(4);
        boolean placed = false;
        for (int dx = -rx; dx <= rx; dx++) {
            for (int dy = -ry; dy <= ry; dy++) {
                for (int dz = -rz; dz <= rz; dz++) {
                    double distance = (double)dx*dx/(rx*rx)
                            + (double)dy*dy/(ry*ry)
                            + (double)dz*dz/(rz*rz);
                    if (distance > 1.05 - random.nextDouble()*0.15) continue;
                    BlockPos at = origin.offset(dx, dy, dz);
                    if (at.getY() < level.getMinY() || at.getY() >= level.getMaxY()) continue;
                    var state = level.getBlockState(at);
                    // Preserve the solid disaster layer and existing caves.
                    if (!state.is(ModBlocks.SCHIST) && !state.is(ModBlocks.ECLOGITE)) continue;
                    placed |= level.setBlock(at, ModBlocks.AMPHIBOLITE.defaultBlockState(), 2);
                }
            }
        }
        return placed;
    }
}
"""
if not feature_path.exists():write(feature_path, feature_java)

p=SRC/'worldgen/ModFeatures.java';s=get(p)
if 'AMPHIBOLITE_DEPOSIT_KEY' not in s:
    s=insert(s, '    public static void bootstrap(', '    public static final ResourceKey<Feature> AMPHIBOLITE_DEPOSIT_KEY = registerKey("amphibolite_deposit");\n')
    s=s.replace('    public static void bootstrap(BootstrapContext<Feature> context) {',
                '    public static void bootstrap(BootstrapContext<Feature> context) {\n        context.register(AMPHIBOLITE_DEPOSIT_KEY, new AmphiboliteDepositFeature());',1)
    write(p,s)
p=SRC/'worldgen/ModPlacedFeatures.java';s=get(p)
if 'AMPHIBOLITE_DEPOSIT_PLACED_KEY' not in s:
    s=insert(s,'    public static void bootstrap(',
             '    public static final ResourceKey<PlacedFeature> AMPHIBOLITE_DEPOSIT_PLACED_KEY = registerKey("amphibolite_deposit_placed");\n')
    anchor='    public static void bootstrap(BootstrapContext<PlacedFeature> context) {'
    code="""
        // Amphibolite: clusters in metamorphic country rock, like tuff deposits.
        register(context, AMPHIBOLITE_DEPOSIT_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.AMPHIBOLITE_DEPOSIT_KEY),
                List.of(
                        CountPlacement.of(9),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(
                                net.minecraft.world.level.levelgen.VerticalAnchor.absolute(-55),
                                net.minecraft.world.level.levelgen.VerticalAnchor.absolute(-5)),
                        BiomeFilter.biome()
                ));
"""
    # Must insert AFTER the configuredFeatures local variable, not before.
    marker='        var configuredFeatures = context.lookup(Registries.FEATURE);'
    if marker not in s: raise RuntimeError('Cannot locate ModPlacedFeatures configuredFeatures lookup')
    s=s.replace(marker,marker+'\n'+code,1)
    write(p,s)
p=SRC/'worldgen/biome/ModOverworldBiomes.java';s=get(p)
if 'AMPHIBOLITE_DEPOSIT_PLACED_KEY' not in s:
    # Add deposits to BOTH biomes without changing their other features.
    anchor='        BiomeDefaultFeatures.addDefaultCarversAndLakes(biomeBuilder);'
    # May have been removed by Phase 1.1; instead insert after each builder creation using common feature line.
    import re as _re
    pattern=r'(        BiomeGenerationSettings.Builder biomeBuilder\s*=\s*new BiomeGenerationSettings.Builder\([\s\S]*?\);)'
    matches=list(_re.finditer(pattern,s))
    if len(matches)!=2: raise RuntimeError('Cannot find both biome generation builder declarations for Amphibolite')
    stmt='\n        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES,\n                ModPlacedFeatures.AMPHIBOLITE_DEPOSIT_PLACED_KEY);'
    for m in reversed(matches):s=s[:m.end()]+stmt+s[m.end():]
    write(p,s)
print('  Added amphibolite metamorphic deposit generation in both Elsewhere surface biomes.')

# 11. Placeholder textures copied from matching existing stone art only if missing.
# Your original sprites and new art always win.
tex=RES/'assets'/NS/'textures/block';tex.mkdir(parents=True,exist_ok=True)
for name in family:
    target=tex/(name+'.png')
    if target.exists():continue
    candidates=['schist.png','rhyolite.png'] if name!='mossy_rhyolite' else ['rhyolite.png']
    if 'polished' in name:candidates=['polished_schist.png','schist.png','rhyolite.png']
    if 'bricks' in name:candidates=['schist_bricks.png','schist.png','rhyolite.png']
    for src in candidates:
        path=tex/src
        if path.exists():shutil.copy2(path,target);print('PLACEHOLDER',target.name,'from',path.name);break
    else: print('TEXTURE MISSING',target.name,'(add your art before testing client)')

print('DONE. Test in a NEW disposable world: .\\gradlew.bat runDatagen; .\\gradlew.bat runClient')
print('Important: disable aquifers and lowered sea_level also remove natural water tables.')
