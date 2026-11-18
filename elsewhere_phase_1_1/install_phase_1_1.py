#!/usr/bin/env python3
"""Elsewhere geology cleanup / Underhang beta. Run from the mod repository root.
Preserves existing assets; makes first-write backups. Requires Phase One installed.
"""
from pathlib import Path
import json, re, shutil, sys, copy
R=Path.cwd(); NS='bugsbugsandmorebugs'; PKG='net.chickalo.'+NS
SRC=R/'src/main/java/net/chickalo'/NS; RES=R/'src/main/resources'; GEN=R/'src/main/generated'
DATA=RES/'data'/NS; BACK=R/'.elsewhere-phase-1-1-backup'
if not (SRC/'block/ModBlocks.java').exists(): sys.exit('Run from the mod root (next to gradlew.bat).')
blocks=(SRC/'block/ModBlocks.java').read_text(encoding='utf8')
need=['COBBLED_RHYOLITE','RUBBLED_LIMESTONE','MOSSY_LIMESTONE','ASHSTONE','SHATTERSTONE','ECLOGITE','SCHIST','LOAM','GRASSY_LOAM']
missing=[v for v in need if re.search(r'\b'+v+r'\b',blocks) is None]
if missing: sys.exit('Missing blocks: '+', '.join(missing)+'\nInstall Phase One + Schist first; do not run this on an outdated GitHub snapshot.')
def write(p,s):
    if p.exists():
        before=p.read_text(encoding='utf8')
        if before==s:return
        dst=BACK/p.relative_to(R);dst.parent.mkdir(parents=True,exist_ok=True)
        if not dst.exists():shutil.copy2(p,dst)
    p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s,encoding='utf8');print('UPDATED',p.relative_to(R))
def js(p):return json.loads(p.read_text(encoding='utf8'))
def save(p,obj):write(p,json.dumps(obj,indent=2,ensure_ascii=False)+'\n')
def addline(s,needle,new):
    if new in s:return s
    if needle not in s:raise RuntimeError('Expected anchor not found: '+needle)
    return s.replace(needle,new+'\n'+needle,1)
# 1. Remove generation features that inject vanilla underground rock/spawners.
# Keep custom lakes and vegetation. Vanilla noise caves remain.
p=SRC/'worldgen/biome/ModOverworldBiomes.java'
s=p.read_text(encoding='utf8')
for method in ['addDefaultCrystalFormations','addDefaultMonsterRoom','addDefaultUndergroundVariety','addDefaultOres','addDefaultCarversAndLakes']:
    s=re.sub(r'(?m)^\s*BiomeDefaultFeatures\.'+method+r'\(biomeBuilder\);[^\n]*\n','\n',s)
if 'public static Biome underhang(' not in s:
    marker='    public static Biome highlandKarst('
    if marker not in s:raise RuntimeError('Missing highlandKarst method; please check biome file before installing')
    method='''    // Underhang is a void biome: no default caves, dungeons, geodes, ores, or vegetation.
    // Its dark atmosphere is visual only; we will add exclusive bug spawns and vines later.
    public static Biome underhang(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(false).temperature(0.45F).downfall(0.0F)
                .specialEffects(new BiomeSpecialEffects.Builder().waterColor(0x26373F).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(new BiomeGenerationSettings.Builder(features, carvers).build())
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(0x141925))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0x202A36))
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(ParticleTypes.ASH, 0.045F))
                .build();
    }

'''
    s=s.replace(marker,method+marker,1)
write(p,s)
# 2. Register biome and give its climate sample a deep-only climate point.
p=SRC/'worldgen/biome/ModBiomes.java';s=p.read_text(encoding='utf8')
s=addline(s,'    public static final ResourceKey<Biome> MAPLE_FOREST', '    public static final ResourceKey<Biome> UNDERHANG = registerBiomeKey("underhang");')
if 'register(context, UNDERHANG,' not in s:
    s=s.replace('        register(context, MAPLE_FOREST', '        register(context, UNDERHANG, ModOverworldBiomes.underhang(placedFeatures, carvers));\n        register(context, MAPLE_FOREST',1)
write(p,s)
p=DATA/'dimension/elsewheredim.json';d=js(p);bs=d['generator']['biome_source']['biomes']
if not any(x['biome']==NS+':underhang' for x in bs):
    for ex in list(bs):
        v=copy.deepcopy(ex);v['biome']=NS+':underhang';v['parameters']['depth']=2.0;bs.append(v)
    save(p,d)
# 3. Dimension height: consistent range -128..319, 448 high.
p=SRC/'worldgen/dimension/ModDimensions.java';s=p.read_text(encoding='utf8')
s=re.sub(r'(\n\s*)-64,(\s*// Minimum Y)',r'\g<1>-128,\g<2>',s,count=1)
s=re.sub(r'(\n\s*)384,(\s*// (?:Logical height|Height))',r'\g<1>448,\g<2>',s)
write(p,s)
for base in [RES,GEN]:
    p=base/'data'/NS/'dimension_type/elsewheredim_type.json'
    if p.exists():
        x=js(p);x.update(min_y=-128,height=448,logical_height=448);save(p,x)
# 4. Protect a warped geological boundary by forcing positive solid density at its band.
# No carvers above means vanilla cave noise cannot cross the barrier.
W=DATA/'worldgen';settingsp=W/'noise_settings/elsewhere.json';settings=js(settingsp)
def b(t,l,r):return {'type':'minecraft:'+t,'left':l,'right':r}
def grad(a,av,c,cv):return {'type':'minecraft:gradient','axis':'y','from_coordinate':a,'from_value':av,'to_coordinate':c,'to_value':cv}
def noise(name,xz=1.0):return {'type':'minecraft:noise','noise':'minecraft:'+name,'xz_scale':xz,'y_scale':0.0}
warp=b('add',grad(-128,-128.0,320,320.0),b('add',b('mul',5.0,noise('surface',0.38)),b('mul',2.0,noise('surface_secondary',0.85))))
# density protection deliberately wider than the material-boundary so caves can't open a tunnel
# through a small noise fluctuation; strong positive density produces a real solid shelf.
protect={'type':'minecraft:range_choice','input':warp,'min_inclusive':-40.0,'max_exclusive':-25.0,'when_in_range':4.0,'when_out_of_range':-1000000.0}
save(W/'density_function/sundering_barrier.json',protect)
# Recommended underlying underside density mask: strip rock away gradually below -55.
# The old Phase One mask can stay, but replace it with a stable named function.
under_mask=b('add',grad(-95,-1.55,-43,1.0),b('add',b('mul',0.65,noise('surface',0.45)),b('mul',0.22,noise('surface_secondary',1.1))))
save(W/'density_function/underhang_underside.json',under_mask)
# Look up older original final density if installer had already wrapped it.
old=settings['noise_router']['final_density']
def unwrap(v):
    if isinstance(v,dict) and v.get('type')=='minecraft:max' and v.get('right')==NS+':sundering_barrier':return unwrap(v['left'])
    if isinstance(v,dict) and v.get('type')=='minecraft:range_choice' and ('underhang' in json.dumps(v)):
        if 'when_out_of_range' in v:return unwrap(v['when_out_of_range'])
    return v
old=unwrap(old)
# Strong negative underside mask below Y~-55, untouched vanilla caves above.
masked={'type':'minecraft:range_choice','input':grad(-128,-128.0,320,320.0),'min_inclusive':-128.0,'max_exclusive':-40.0,
        'when_in_range':b('min',old,NS+':underhang_underside'),'when_out_of_range':old}
settings['noise_router']['final_density']=b('max',masked,NS+':sundering_barrier')
# Choose Underhang biome deep in the world. Other biomes remain at surface/upper caves.
settings['noise_router']['depth']={'type':'minecraft:range_choice','input':grad(-128,-128.0,320,320.0),
   'min_inclusive':-128.0,'max_exclusive':-55.0,'when_in_range':2.0,'when_out_of_range':'minecraft:overworld/depth'}
settings['noise'].update(min_y=-128,height=448)
# AIR as default fluid is important for a void dimension; custom lake features place water explicitly.
settings['default_fluid']='minecraft:air'
# Suppress special deep aquifer lava selection by making lava noise strongly negative.
# 26.3 retains deep aquifer fluid behavior: this is a beta and needs an in-game check.
settings['aquifers']['lava']=-1000000.0
save(settingsp,settings)
# 5. Material rule: uneven horizon. Anchor jitter is supplied by deterministic noise thresholds;
# dense barrier prevents caves passing between schist and eclogite.
def block(name):return {'type':'minecraft:block','result_state':NS+':'+name}
def cond(check,run):return {'type':'minecraft:condition','if_true':check,'then_run':run}
def seq(*args):return {'type':'minecraft:sequence','sequence':list(args)}
def below(y):return {'type':'minecraft:not','invert':{'type':'minecraft:y_above','anchor':{'absolute':y},'add_stone_depth':False,'surface_depth_multiplier':0}}
def chance(lo,hi):return {'type':'minecraft:noise_threshold','noise':'minecraft:surface_secondary','min_threshold':lo,'max_threshold':hi}
mat=seq(
  # Deep eclogite is the substrate under the fracture zone.
  cond(below(-39),block('eclogite')),
  cond(below(-36),seq(cond(chance(-1.0,-0.1),block('ashstone')),block('shatterstone'))),
  cond(below(-33),seq(cond(chance(0.15,1.0),block('shatterstone')),block('ashstone'))),
  cond(below(-30),seq(cond(chance(-1.0,-0.35),block('schist')),cond(chance(0.3,1.0),block('shatterstone')),block('ashstone'))),
  cond(below(-27),cond(chance(0.35,1.0),block('ashstone'))),
  # Preserve whatever Phase One installed for the soil / surface if present.
  cond({'type':'minecraft:above_preliminary_surface'},seq(
      cond('minecraft:on_floor',seq(cond('minecraft:not_underwater',block('grassy_loam')),block('loam'))),
      cond('minecraft:under_floor',block('loam')))),
  cond({'type':'minecraft:vertical_gradient','false_at_and_above':{'absolute':8},'true_at_and_below':{'absolute':0},'random_name':NS+':elsewhere_deep_transition'},block('schist')),
  block('rhyolite'))
save(W/'material_rule/elsewhere.json',mat)
# 6. Make custom features use local geology (do not replace vanilla namespace globally).
# This covers Java-authored custom worldgen. Vanilla structure processors are a future task.
for p in (SRC/'worldgen').rglob('*.java'):
    s=p.read_text(encoding='utf8');orig=s
    # Only direct block-state placements; avoid modifying predicates and checks.
    for src,dst in [('Blocks.COBBLESTONE.defaultBlockState()', 'ModBlocks.RUBBLED_LIMESTONE.defaultBlockState()' if 'Karst' in p.name or 'Limestone' in p.name else 'ModBlocks.COBBLED_RHYOLITE.defaultBlockState()'),
      ('Blocks.MOSSY_COBBLESTONE.defaultBlockState()','ModBlocks.MOSSY_LIMESTONE.defaultBlockState()')]:
        s=s.replace(src,dst)
    if s!=orig:
        if 'ModBlocks.' in s and 'import '+PKG+'.block.ModBlocks;' not in s:
            s=s.replace('\nimport ', '\nimport '+PKG+'.block.ModBlocks;\nimport ',1)
        write(p,s)
# 7. Advancement data + documentation-oriented Java catalog.
adv=DATA/'advancement'
def display(id,desc,icon,frame='task',bg=None):
    x={'icon':{'id':NS+':'+icon},'title':{'translate':'advancements.'+NS+'.'+id+'.title'},
       'description':{'translate':'advancements.'+NS+'.'+id+'.description'},'frame':frame}
    if bg:x['background']=bg
    return x
root={'display':display('root','Enter Elsewhere','rhyolite',bg='minecraft:gui/advancements/backgrounds/stone'),
    'criteria':{'enter_elsewhere':{'trigger':'minecraft:changed_dimension','conditions':{'to':NS+':elsewheredim'}}},
    'requirements':[['enter_elsewhere']]}
if not (adv/'root.json').exists(): save(adv/'root.json',root)
else: print('PRESERVED existing advancement:',adv/'root.json')
# Vanilla has no generic, stable 'block mined' trigger for arbitrary mod blocks.
# Inventory changed fires when the player acquires the actual material and is reliable in 26.3.
aot={'parent':NS+':root', 'display':display('as_old_as_time','Obtain a fragment of the Sundering Horizon','ashstone'),
     'criteria':{'found_sundering':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':[NS+':ashstone',NS+':shatterstone']}]}}},
     'requirements':[['found_sundering']]}
if not (adv/'as_old_as_time.json').exists(): save(adv/'as_old_as_time.json',aot)
else: print('PRESERVED existing advancement:',adv/'as_old_as_time.json')
p=SRC/'advancement/ModAdvancements.java'
if not p.exists():
    write(p,f'''package {PKG}.advancement;

/**
 * ELSEWHERE ADVANCEMENT CATALOG (Minecraft 26.3)
 *
 * Our advancements are JSON data files, NOT registries that need manual Java bootstrap.
 * See: src/main/resources/data/{NS}/advancement/
 *
 * HOW TO ADD AN ADVANCEMENT:
 * 1. Create another JSON next to root.json and as_old_as_time.json.
 * 2. Set "parent": "{NS}:root" (or another advancement ID).
 * 3. Give it "display" (icon, title, description) and "criteria" (trigger + conditions).
 * 4. Add its title/description translations in assets/{NS}/lang/en_us.json.
 * 5. Test with /advancement grant @s only {NS}:your_advancement_id
 *
 * KEY EXAMPLES:
 * - minecraft:changed_dimension checks dimension transitions (root.json).
 * - minecraft:inventory_changed checks for obtained items (as_old_as_time.json).
 * - minecraft:location can recognize places using a location predicate.
 *
 * IMPORTANT: An inventory trigger means "obtained", not literally "mined".
 * To detect BREAKING either Ashstone or Shatterstone, a separate Fabric block-break
 * event and a custom criterion are needed. The starter chooses the dependable
 * inventory criterion so it works when digging normally.
 *
 * This class just collects IDs for future Java integrations; no initializer required.
 */
public final class ModAdvancements {{
    private ModAdvancements() {{}}

    public static final String ROOT = "{NS}:root";
    public static final String AS_OLD_AS_TIME = "{NS}:as_old_as_time";

    // FUTURE:
    // public static final String THE_WORLD_ENDS_HERE = "{NS}:the_world_ends_here";
    // public static final String WRITTEN_IN_STONE = "{NS}:written_in_stone";
    // public static final String JADED = "{NS}:jaded";
}}
''')
langp=RES/'assets'/NS/'lang/en_us.json';l=js(langp)
for id,title,desc in [('root','Elsewhere','Enter the world beyond the rift.'),('as_old_as_time','As Old as Time','Obtain Ashstone or Shatterstone from the Sundering Horizon.')]:
    l['advancements.'+NS+'.'+id+'.title']=title;l['advancements.'+NS+'.'+id+'.description']=desc
save(langp,l)
print('\nDONE. First use .\\gradlew.bat runDatagen, then test in a NEW world.')
print('Caution: Underhang fluids and cave-boundary behavior are beta; see README.')
