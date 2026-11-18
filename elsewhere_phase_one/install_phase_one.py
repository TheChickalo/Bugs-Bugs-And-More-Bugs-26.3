#!/usr/bin/env python3
"""Run from mod root. Adds soil, disaster horizon, Eclogite, and experimental underside."""
from pathlib import Path
import json, re, shutil, subprocess, sys, copy
ROOT=Path.cwd(); NS='bugsbugsandmorebugs'; PKG='net.chickalo.'+NS
SRC=ROOT/'src/main/java/net/chickalo'/NS; RES=ROOT/'src/main/resources'; GEN=ROOT/'src/main/generated'; DATA=RES/'data'/NS; AS=RES/'assets'/NS; B=Path(__file__).parent
if not (SRC/'block/ModBlocks.java').is_file(): sys.exit('Run from your mod root (contains gradlew.bat).')
if 'public static final Block SCHIST =' not in (SRC/'block/ModBlocks.java').read_text(): sys.exit('Install and compile Schist first; this installer assumes that family is present.')
backup=ROOT/'.elsewhere-phase-one-backup'
def write(p,s):
    if p.exists() and p.read_text(encoding='utf-8')==s:return
    if p.exists():
        q=backup/p.relative_to(ROOT);q.parent.mkdir(parents=True,exist_ok=True)
        if not q.exists():shutil.copy2(p,q)
    p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s,encoding='utf-8');print('UPDATED',p.relative_to(ROOT))
def read(p):return p.read_text(encoding='utf-8')
def js(p):return json.loads(read(p))
def save(p,x):write(p,json.dumps(x,indent=2,ensure_ascii=False)+'\n')
def insert(p,marker,anchor,code):
    s=read(p)
    if marker in s:return
    if anchor not in s:raise RuntimeError(f'Anchor missing: {p} {anchor!r}')
    write(p,s.replace(anchor,code+anchor,1))
# Eclogite installer is the same corrected family generator, without its old terrain prototype.
for rel in ['block/ModBlocks.java','datagen/ModModelProvider.java','datagen/ModBlockLootTableProvider.java','datagen/ModRecipeProvider.java','datagen/ModBlockTagsProvider.java','worldgen/biome/ModOverworldBiomes.java','worldgen/dimension/ModDimensions.java']:
    p=SRC/rel
    if not p.is_file():sys.exit(f'Missing {p}')
    q=backup/p.relative_to(ROOT);q.parent.mkdir(parents=True,exist_ok=True)
    if not q.exists():shutil.copy2(p,q)
for p in [DATA/'worldgen/material_rule/elsewhere.json',DATA/'worldgen/noise_settings/elsewhere.json',AS/'lang/en_us.json',RES/'fabric.mod.json']:
    if not p.is_file():sys.exit(f'Missing {p}')
    js(p)
    q=backup/p.relative_to(ROOT);q.parent.mkdir(parents=True,exist_ok=True)
    if not q.exists():shutil.copy2(p,q)
subprocess.check_call([sys.executable,str(B/'install_eclogite.py')])
blocks=SRC/'block/ModBlocks.java'
for sym,id,expr in [('LOAM','loam','new Block(properties.strength(0.5F).sound(SoundType.GRAVEL).instrument(NoteBlockInstrument.SNARE))'),('GRASSY_LOAM','grassy_loam',f'new {PKG}.block.custom.GrassyLoamBlock(properties.strength(0.6F).sound(SoundType.GRASS).randomTicks())'),('ASHSTONE','ashstone','new Block(properties.strength(2.0F, 4.0F).requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM))'),('SHATTERSTONE','shatterstone','new Block(properties.strength(4.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM))')]:
    insert(blocks,f'public static final Block {sym} =','    // Limestone family',f'    public static final Block {sym} = registerBlock("{id}", properties -> {expr});\n')
write(SRC/'block/custom/GrassyLoamBlock.java',f'''package {PKG}.block.custom;
import {PKG}.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

// Uses exposure, rather than effective daylight, so Starless will not kill the grass.
public class GrassyLoamBlock extends Block {{
    public GrassyLoamBlock(Properties properties) {{ super(properties); }}
    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {{
        BlockState above = level.getBlockState(pos.above());
        if (above.isSolidRender() || !level.getFluidState(pos.above()).isEmpty()) {{
            level.setBlock(pos, ModBlocks.LOAM.defaultBlockState(), 3);
            return;
        }}
        for (int i=0; i<4; i++) {{
            BlockPos target=pos.offset(random.nextInt(3)-1,random.nextInt(3)-1,random.nextInt(3)-1);
            if (level.getBlockState(target).is(ModBlocks.LOAM) && level.getBlockState(target.above()).isAir())
                level.setBlock(target, defaultBlockState(), 3);
        }}
    }}
}}
''')
model=SRC/'datagen/ModModelProvider.java'
insert(model,'createTrivialCube(ModBlocks.LOAM)','        blockModelGenerators.createTrivialCube(ModBlocks.MOSSY_LIMESTONE);',''.join(f'        blockModelGenerators.createTrivialCube(ModBlocks.{s});\n' for s in ['LOAM','ASHSTONE','SHATTERSTONE']))
# Grassy loam model is deliberately authored in resources, not generated as a plain cube.
loot=SRC/'datagen/ModBlockLootTableProvider.java'
insert(loot,'dropSelf(ModBlocks.LOAM)','        dropSelf(ModBlocks.COBBLED_RHYOLITE);','''        dropSelf(ModBlocks.LOAM);
        dropSelf(ModBlocks.ASHSTONE);
        dropSelf(ModBlocks.SHATTERSTONE);
        add(ModBlocks.GRASSY_LOAM, createSingleItemTableWithSilkTouch(ModBlocks.GRASSY_LOAM, ModBlocks.LOAM));
''')
tags=SRC/'datagen/ModBlockTagsProvider.java'
insert(tags,'ModBlocks.getRK(ModBlocks.GRASSY_LOAM)','        tag(BlockTags.STONE_ORE_REPLACEABLES)','''        tag(BlockTags.DIRT)
                .add(ModBlocks.getRK(ModBlocks.LOAM));
        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.getRK(ModBlocks.LOAM))
                .add(ModBlocks.getRK(ModBlocks.GRASSY_LOAM));
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.getRK(ModBlocks.ASHSTONE))
                .add(ModBlocks.getRK(ModBlocks.SHATTERSTONE));
''')
# 26.3 separates grass_blocks from dirt; vanilla vegetation uses substrate_overworld.
for tag,ids in [('grass_blocks',['grassy_loam']),('turns_into_farmland',['loam','grassy_loam'])]:
    p=RES/'data/minecraft/tags/block'/f'{tag}.json'
    d=js(p) if p.exists() else {'replace':False,'values':[]}
    for i in ids:
        v=NS+':'+i
        if v not in d['values']:d['values'].append(v)
    save(p,d)
lang=AS/'lang/en_us.json'; l=js(lang)
for id,name in [('loam','Loam'),('grassy_loam','Grassy Loam'),('ashstone','Ashstone'),('shatterstone','Shatterstone')]:l[f'block.{NS}.{id}']=name
save(lang,l)
# Optional side base texture; use loam for side and bottom if no separate side art exists.
tex=AS/'textures/block'
for f in ['loam','grassy_loam_top','grassy_loam_side_overlay','ashstone','shatterstone']:
    if not (tex/(f+'.png')).exists(): print('ADD YOUR TEXTURE:',f+'.png')
side=f'{NS}:block/'+('grassy_loam_side' if (tex/'grassy_loam_side.png').exists() else 'loam')
modelid=f'{NS}:block/grassy_loam'
save(AS/'models/block/grassy_loam.json',{'parent':'minecraft:block/grass_block','textures':{'particle':f'{NS}:block/loam','bottom':f'{NS}:block/loam','side':side,'top':f'{NS}:block/grassy_loam_top','overlay':f'{NS}:block/grassy_loam_side_overlay'}})
save(AS/'blockstates/grassy_loam.json',{'variants':{'':{'model':modelid}}})
save(AS/'items/grassy_loam.json',{'model':{'type':'minecraft:model','model':modelid,'tints':[{'type':'minecraft:constant','value':0x79A85C}]}})
# Separate client entrypoint prevents client classes from loading server-side.
client=SRC/'entity/client/ElsewhereSoilClient.java'
write(client,f'''package {PKG}.entity.client;
import {PKG}.block.ModBlocks;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.ARGB;
public class ElsewhereSoilClient implements ClientModInitializer {{
    @Override public void onInitializeClient() {{
        BlockColorRegistry.register(List.of(new BlockTintSource() {{
            @Override public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {{
                return ARGB.opaque(BiomeColors.getAverageGrassColor(level, pos));
            }}
            @Override public int color(BlockState state) {{ return ARGB.opaque(0x79A85C); }}
        }}), ModBlocks.GRASSY_LOAM);
    }}
}}
''')
# Client class may be in split source set. Follow existing client initializer location.
oldclient=SRC/'entity/client/BugsBugsandMoreBugsClient.java'
split=ROOT/'src/client/java/net/chickalo'/NS/'entity/client'
if not oldclient.exists() and split.exists():
    dest=split/client.name;write(dest,read(client));client.unlink()
mod=RES/'fabric.mod.json'; m=js(mod); ep=PKG+'.entity.client.ElsewhereSoilClient'
if ep not in m['entrypoints']['client']:m['entrypoints']['client'].append(ep);save(mod,m)
# Preserve all existing vanilla acceptance checks and add new soil equivalents.
# Only checks in custom features/plants; never globally replace vanilla constants.
for p in list((SRC/'worldgen').rglob('*.java'))+list((SRC/'block/custom').rglob('*.java')):
    if p.name=='GrassyLoamBlock.java':continue
    s=read(p); orig=s
    # Matches simple expressions including level.getBlockState(pos.below()).is(...).
    # Instead of parsing receivers, use a helper taking the original state and vanilla block.
    if 'ElsewhereGround.matches(' in s:continue
    pattern=r'([A-Za-z_][\w.]*(?:\([^;\n]*?\))?)\.is\(Blocks\.(GRASS_BLOCK|DIRT|STONE)\)'
    # More conservative: explicit state variables and getBlockState calls only.
    pattern=r'((?:\w+\.getBlockState\((?:[^()\n;]|\([^()\n;]*\))*\)|(?<![.\w])\w+))\.is\(Blocks\.(GRASS_BLOCK|DIRT|STONE)\)'
    s=re.sub(pattern,lambda m:f'{PKG}.worldgen.ElsewhereGround.matches({m[1]}, Blocks.{m[2]})',s)
    if s!=orig:write(p,s)
write(SRC/'worldgen/ElsewhereGround.java',f'''package {PKG}.worldgen;
import {PKG}.block.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
public final class ElsewhereGround {{
    private ElsewhereGround() {{}}
    public static boolean matches(BlockState state, Block vanilla) {{
        return state.is(vanilla)
            || (vanilla == Blocks.GRASS_BLOCK && state.is(ModBlocks.GRASSY_LOAM))
            || (vanilla == Blocks.DIRT && state.is(ModBlocks.LOAM))
            || (vanilla == Blocks.STONE && state.is(ModBlocks.RHYOLITE));
    }}
}}
''')
# Replace only the material selection of custom lake/shore features, not their checks.
for name in ['MapleLakeFeature.java','MapleWaterShoreFeature.java']:
    for p in (SRC/'worldgen').rglob(name):
        s=read(p);s=s.replace('Blocks.STONE.defaultBlockState()',f'{PKG}.block.ModBlocks.RHYOLITE.defaultBlockState()').replace('Blocks.ANDESITE.defaultBlockState()',f'{PKG}.block.ModBlocks.RHYOLITE.defaultBlockState()');write(p,s)
# Custom surface and horizon. No vanilla giant ore veins, which can overwrite strata.
def state(n):return {'type':'minecraft:block','result_state':n if ':' in n else NS+':'+n}
def cond(c,r):return {'type':'minecraft:condition','if_true':c,'then_run':r}
def seq(*a):return {'type':'minecraft:sequence','sequence':list(a)}
def y(n):return {'type':'minecraft:y_above','anchor':{'absolute':n},'add_stone_depth':False,'surface_depth_multiplier':0}
def below(n,r):return cond({'type':'minecraft:not','invert':y(n)},r)
def grad(a,av,b,bv):return {'type':'minecraft:gradient','axis':'y','from_coordinate':a,'from_value':av,'to_coordinate':b,'to_value':bv}
def binary(t,l,r):return {'type':'minecraft:'+t,'left':l,'right':r}
def noise(n,x):return {'type':'minecraft:noise','noise':'minecraft:'+n,'xz_scale':x,'y_scale':0.0}
def band(lo,hi,inside,outside):return {'type':'minecraft:range_choice','input':grad(-128,-128.0,320,320.0),'min_inclusive':lo,'max_exclusive':hi,'when_in_range':inside,'when_out_of_range':outside}
W=DATA/'worldgen'
# Fixed three-block marker; 2 ashstone-rich levels with some shatterstone, bottom shattered rock.
horizon=seq(cond({'type':'minecraft:noise_threshold','noise':'minecraft:surface_secondary','min_threshold':0.1,'max_threshold':1.0e6},state('shatterstone')),state('ashstone'))
surface=cond({'type':'minecraft:above_preliminary_surface'},seq(cond('minecraft:on_floor',seq(cond('minecraft:not_underwater',state('grassy_loam')),state('loam'))),cond('minecraft:under_floor',state('loam'))))
mat=seq(below(-32,state('eclogite')),below(-31,state('shatterstone')),below(-29,horizon),surface,cond({'type':'minecraft:vertical_gradient','false_at_and_above':{'absolute':8},'true_at_and_below':{'absolute':0},'random_name':NS+':elsewhere_deep_transition'},state('schist')),state('rhyolite'))
save(W/'material_rule/elsewhere.json',mat)
# Underhang: extend lower build limit, preserve max height 320, mask old final density.
# Enable by default; --no-underhang keeps the old limits and density configuration.
settingsp=W/'noise_settings/elsewhere.json';settings=js(settingsp)
if '--no-underhang' not in sys.argv:
    marker=NS+':phase_one_underhang_mask';router=settings['noise_router'];old=router['final_density']
    oldmarker=NS+':underhang_mask'
    if oldmarker in json.dumps(old) and isinstance(old,dict) and 'when_out_of_range' in old:old=old['when_out_of_range']
    if marker not in json.dumps(old):
        mask=binary('add',grad(-80,-1.2,-45,1.1),binary('add',binary('mul',0.7,noise('surface',0.5)),binary('mul',0.22,noise('surface_secondary',1.25))))
        save(W/'density_function/phase_one_underhang_mask.json',mask)
        router['final_density']=band(-128.0,-40.0,binary('min',copy.deepcopy(old),marker),copy.deepcopy(old))
    settings['noise']['min_y']=-128;settings['noise']['height']=448
    boundary=grad(-45,1.0,-39,-1.0)
    if boundary not in [settings['aquifers']['exclusion'],settings['aquifers']['exclusion'].get('right') if isinstance(settings['aquifers']['exclusion'],dict) else None]:settings['aquifers']['exclusion']=binary('max',settings['aquifers']['exclusion'],boundary)
    # Update source of truth for generated dimension type and any existing JSON.
    p=SRC/'worldgen/dimension/ModDimensions.java';s=read(p)
    s=re.sub(r'(?m)^([ \t]*)-64,(.*)$',r'\g<1>-128,\2',s,count=1)
    s=re.sub(r'(?m)^([ \t]*)384,(.*)$',r'\g<1>448,\2',s,count=2);write(p,s)
    for base in [RES,GEN]:
        p=base/'data'/NS/'dimension_type/elsewheredim_type.json'
        if p.exists():
            d=js(p);d.update(min_y=-128,height=448,logical_height=448);save(p,d)
save(settingsp,settings)
# Disable vanilla ore and underground variety generation in Elsewhere; keep caves and vegetation.
p=SRC/'worldgen/biome/ModOverworldBiomes.java'
s=read(p)
for method in ['addDefaultUndergroundVariety','addDefaultOres']:
    s=re.sub(r'(?m)^\s*BiomeDefaultFeatures\.'+method+r'\(biomeBuilder\);\s*\n','\n',s)
write(p,s)
print('Done. Run gradlew.bat runDatagen and runClient; test a NEW world. See README for limitations.')
