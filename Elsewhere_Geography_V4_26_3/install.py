#!/usr/bin/env python3
"""Elsewhere geography/ecology V4. Applies minimal, independently selectable patches.
Run from mod root: py Elsewhere_Geography_V4_26_3/install.py --geography --ecology
"""
from __future__ import annotations
import argparse, copy, json, re, shutil, sys
from pathlib import Path

NS='bugsbugsandmorebugs'
R=Path('src/main/resources')
J=Path('src/main/java/net/chickalo/bugsbugsandmorebugs')
D=R/'data'/NS/'worldgen'
BASE=Path.cwd()
BACK=BASE/'.elsewhere_v4_backup'
changes={}


def require(path):
    p=BASE/path
    if not p.exists(): raise RuntimeError(f'Required file missing: {p}')
    return p

def text_read(path):
    path=Path(path)
    return changes[path] if path in changes else require(path).read_text(encoding='utf-8')
def write(path,content):
    path=Path(path)
    if isinstance(content,(dict,list)): content=json.dumps(content,ensure_ascii=False,indent=2)+'\n'
    if not content.endswith('\n'):content+='\n'
    old=(BASE/path).read_text(encoding='utf-8') if (BASE/path).is_file() else None
    if old!=content:changes[path]=content

def load(path):return json.loads(text_read(path))
def at(s,label):
    if label not in s: raise RuntimeError('Could not locate patch anchor: '+label)
    return s

def replace_once(s,old,new,description):
    if old==new:return s
    if new in s:return s
    n=s.count(old)
    if n!=1:raise RuntimeError(f'{description}: expected 1 old anchor, found {n}')
    return s.replace(old,new,1)

def B(name):return NS+':'+name

def C(x):return {'type':'minecraft:clamp','input':x,'min':0.0,'max':1.0}
def A(x,y):return {'type':'minecraft:add','left':x,'right':y}
def S(x,y):return {'type':'minecraft:sub','left':x,'right':y}
def M(x,y):return {'type':'minecraft:mul','left':x,'right':y}
def mn(x,y):return {'type':'minecraft:min','left':x,'right':y}
def mx(x,y):return {'type':'minecraft:max','left':x,'right':y}
def G(lo,lv,hi,hv):return {'type':'minecraft:gradient','axis':'y','from_coordinate':lo,'from_value':lv,'to_coordinate':hi,'to_value':hv}
def L(alpha,first,second):return {'type':'minecraft:lerp','alpha':alpha,'first':first,'second':second}
def N(name,xz):return {'type':'minecraft:noise','noise':'minecraft:'+name,'xz_scale':xz,'y_scale':0.0}
def ref(name):return B(name)
def cr(x,t):return C(M(x,t))
def biome_rule(biome, sub):return {'type':'minecraft:condition','if_true':{'type':'minecraft:biome','biome_is':B(biome)},'then_run':sub}
def condition(cond,rule):return {'type':'minecraft:condition','if_true':cond,'then_run':rule}
def floor(rule):return condition('minecraft:on_floor',rule)
def block(n):return {'type':'minecraft:block','result_state':n if ':' in n else B(n)}
def noise_rule(noise,lo,hi,name):return condition({'type':'minecraft:noise_threshold','noise':'minecraft:'+noise,'min_threshold':lo,'max_threshold':hi},block(name))


def geography():
    dim=R/'data'/NS/'dimension/elsewheredim.json'
    obj=load(dim)
    entries=obj['generator']['biome_source']['biomes']
    old_count=len(entries)
    surface=[e for e in entries if e['biome']!=B('underhang')]
    # Already-installed climate schema? Keep its explicit guard points instead
    # of adding them a second time on a repeat install.
    already_v4=any(e['biome']==B('bamboo_lowlands') for e in surface)
    if not already_v4:
        surface=[e for e in surface if e['biome'] not in (B('river'),B('lake'))]
    new=list(surface)
    # 26.3 multi_noise entries are nearest-neighbor; guard points bound the
    # river strip on either side of weirdness=0.
    def climate(b,t,h,c,e,w,depth=0.0):
        return {'biome':B(b),'parameters':{'temperature':t,'humidity':h,'continentalness':c,'erosion':e,'depth':depth,'weirdness':w,'offset':0.0}}
    # River is a slim band around ridge=0. Other climate axes match *exactly*
    # for guard points on both sides.
    if not already_v4:
      for t,h,c,e,bank in [
        (-0.38,0.22,0.26,0.30,'maple_forest'),
        (0.26,0.42,0.26,0.30,'ginkgo_forest'),
        (0.45,-0.25,0.26,0.30,'grassy_plains'),
        (-0.20,-0.27,0.51,0.25,'highland_karst')]:
          for w in (-0.16,-0.075):new.append(climate(bank,t,h,c,e,w))
          new.append(climate('river',t,h,c,e,0.0))
          for w in (0.075,0.16):new.append(climate(bank,t,h,c,e,w))
    # Lakes are inland by climate and by a separate density mask.
    if not already_v4:
        new += [climate('lake',0.36,0.60,0.40,0.41,-0.35),
                climate('lake',-0.24,0.57,0.41,0.45,0.32)]
    # Bamboo forest sits in wet lowlands beside Ginkgo / Plains, not on the peak.
    if not already_v4:
        new += [climate('bamboo_lowlands',0.55,0.65,0.27,0.15,-0.22),
                climate('bamboo_lowlands',0.30,0.69,0.31,0.32,0.20),
                climate('bamboo_lowlands',0.52,0.52,0.34,0.29,0.42)]
    under=[]
    for e in new:
        x=copy.deepcopy(e);x['biome']=B('underhang');x['parameters']['depth']=2.0;under.append(x)
    obj['generator']['biome_source']['biomes']=new+under
    write(dim,obj)

    # River noise has to match the climate river's *same* ridges axis.
    criver=M(C(M(S(0.085,{'type':'minecraft:abs','input':'minecraft:overworld/ridges'}),20.0)),
             M(C(M(S('minecraft:overworld/continents',-0.12),5.0)),
               C(M(S(0.50,'minecraft:overworld/continents'),4.0))))
    write(D/'density_function/river_corridor_region.json',criver)

    # Lake mask is explicitly inland + wet, and rare pockets from high noise values.
    lake=M(C(M(S('minecraft:overworld/continents',0.18),4.5)),
           M(C(M(S('minecraft:overworld/vegetation',0.20),4.0)),
             C(M(S(N('surface_secondary',0.065),0.32),3.3))))
    write(D/'density_function/lake_basin_region.json',lake)

    # Real *carved* Stone Forest, not surface feature poles. Incisions carve from
    # a shared plateau to a water level around Y=60; residual noise leaves
    # broad rock remnants and rare towers in the former solid shelf.
    broad=C(M(S({'type':'minecraft:abs','input':N('badlands_surface',0.11)},0.23),3.4))
    sharp=C(M(S({'type':'minecraft:abs','input':N('badlands_pillar',0.28)},0.14),5.8))
    roof=L(mx(sharp,M(broad,0.62)),
           G(52,1.9,76,-2.0),
           A(G(68,4.4,310,-0.60),M(N('surface_secondary',0.15),0.42)))
    write(D/'density_function/stone_forest_roof.json',roof)
    # Gradual macro region. No abrupt per-chunk changes.
    stone=M(ref('karst_massif_region'),C(M(A('minecraft:overworld/erosion',0.30),4.2)))
    write(D/'density_function/stone_forest_region.json',stone)

    # Volcanic massif tuned to ensure a sharp height advantage over karst.
    volc=S(M(ref('volcanic_massif_region'),
             A(G(98,12.8,632,-0.6),M(N('surface',0.55),0.38))),
           M(S(1.0,ref('volcanic_massif_region')),3.3))
    write(D/'density_function/volcanic_massif_density.json',volc)
    # Physical default 65-ish ground on inland lower terrain (never under ocean).
    landmask=C(M(A('minecraft:overworld/continents',0.07),6.5))
    lowland=S(M(landmask,G(51,2.3,91,-2.1)),M(S(1.0,landmask),4.5))
    # Preserve original underside and solid Sundering below Y=-16; the new
    # upper-world terrain starts well below the surface to avoid floating sheets.
    vanilla=mx({'type':'minecraft:range_choice','input':G(-128,-128,640,640),
        'min_inclusive':-128,'max_exclusive':-40,
        'when_in_range':mn('minecraft:overworld/final_density',ref('underhang_underside')),
        'when_out_of_range':'minecraft:overworld/final_density'},ref('sundering_barrier'))
    baseline=mx(mx(mx(vanilla,lowland),ref('karst_plateau_density')),ref('volcanic_massif_density'))
    stonecut=L(ref('stone_forest_region'),baseline,mn(baseline,ref('stone_forest_roof')))
    rivercut=L(ref('river_corridor_region'),stonecut,mn(stonecut,G(48,2.2,58,-1.6)))
    lakecut=L(ref('lake_basin_region'),rivercut,mn(rivercut,G(49,2.4,57,-1.8)))
    final={'type':'minecraft:range_choice','input':G(-128,-128,640,640),'min_inclusive':-16.0,'max_exclusive':640.0,'when_in_range':lakecut,'when_out_of_range':vanilla}
    settings=D/'noise_settings/elsewhere.json'
    noise=load(settings)
    noise['noise_router']['final_density']=final
    # The underhang's aquifer exclusion and dimension type remain exactly as-is.
    write(settings,noise)

    # Material palette: wet bamboo floor and ancient karst exposed limestone.
    mat=D/'material_rule/elsewhere.json'
    j=load(mat); seq=j['sequence'];
    seq[:]=[e for e in seq if not (isinstance(e,dict) and isinstance(e.get('if_true'),dict) and e['if_true'].get('biome_is')==B('bamboo_lowlands'))]
    bamboo_surf=biome_rule('bamboo_lowlands',floor({'type':'minecraft:sequence','sequence':[
        noise_rule('surface_secondary',0.42,1.0,'minecraft:podzol'),
        noise_rule('surface_secondary',-1.0,-0.49,'minecraft:coarse_dirt'),
        block('grassy_loam')]}))
    # Insert after underground material ranges and before common topsoil.
    target=next((i for i,v in enumerate(seq) if isinstance(v.get('if_true'),dict) and v['if_true'].get('biome_is')==B('stone_forest')),len(seq)-2)
    seq.insert(target,bamboo_surf)
    # Avoid painting all estuary underground into mud; keep only surface palette.
    est=next((i for i,v in enumerate(seq) if isinstance(v.get('if_true'),dict) and v['if_true'].get('biome_is')==B('estuary')),None)
    if est is not None:
        seq[est]=biome_rule('estuary',floor({'type':'minecraft:sequence','sequence':[
            noise_rule('surface',-1.0,-0.33,'minecraft:mud'),
            noise_rule('surface',-0.33,0.15,'loam'),
            noise_rule('surface',0.15,1.0,'grassy_loam'),block('minecraft:clay')]}))
    write(mat,j)
    return old_count,len(new)+len(under)


def patch_java_biomes():
    p=J/'worldgen/biome/ModBiomes.java'
    s=text_read(p)
    s=replace_once(s,'    public static final ResourceKey<Biome> GRASSY_PLAINS',
                   '    public static final ResourceKey<Biome> BAMBOO_LOWLANDS = registerBiomeKey("bamboo_lowlands");\n    public static final ResourceKey<Biome> GRASSY_PLAINS','biome key')
    s=replace_once(s,'        register(context, GRASSY_PLAINS,',
                   '        register(context, BAMBOO_LOWLANDS, ModOverworldBiomes.bambooLowlands(placedFeatures, carvers));\n        register(context, GRASSY_PLAINS,','biome bootstrap')
    write(p,s)
    p=J/'worldgen/biome/ModOverworldBiomes.java'
    s=text_read(p)
    method='''    public static Biome bambooLowlands(HolderGetter<PlacedFeature> features, HolderGetter<WorldCarver> carvers) {
        var generation = new BiomeGenerationSettings.Builder(features, carvers);
        generation.addFeature(GenerationStep.Decoration.LAKES,
                ModPlacedFeatures.BAMBOO_POOLS_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS,
                ModPlacedFeatures.KARST_ANIMAL_TRAIL_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.BAMBOO_GROVE_PLACED_KEY);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.BAMBOO_FERNS_PLACED_KEY);
        BiomeDefaultFeatures.addPlainGrass(generation);
        BiomeDefaultFeatures.addDefaultFlowers(generation);
        return landscapeBiome(generation, 0.73F, 0.85F,
                0x5B907C, 0x98CECE, 0xB4D7BD);
    }

'''
    if 'public static Biome bambooLowlands(' not in s:
        s=at(s,'    private static Biome landscapeBiome(').replace('    private static Biome landscapeBiome(',method+'    private static Biome landscapeBiome(',1)
    # Add reeds near estuary/river/lake and bamboo. Keep feature order compatible.
    def add_to(methodname):
        nonlocal s
        a=s.find('    public static Biome '+methodname+'(')
        if a<0:raise RuntimeError(f'Biome method missing: {methodname}')
        end=s.find('    public static Biome ',a+10)
        if end<0:end=s.find('    private static Biome landscapeBiome(',a+10)
        piece=s[a:end]
        if 'WETLAND_REEDS_PLACED_KEY' in piece:return
        if 'return landscapeBiome(new BiomeGenerationSettings.Builder(features, carvers),' in piece:
            piece=piece.replace('return landscapeBiome(new BiomeGenerationSettings.Builder(features, carvers),','''var generation = new BiomeGenerationSettings.Builder(features, carvers);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.WETLAND_REEDS_PLACED_KEY);
        return landscapeBiome(generation,''',1)
        elif 'return landscapeBiome(generation,' in piece:
            piece=piece.replace('return landscapeBiome(generation,','''generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.WETLAND_REEDS_PLACED_KEY);
        return landscapeBiome(generation,''',1)
        else:raise RuntimeError('No wetland anchor in '+methodname)
        s=s[:a]+piece+s[end:]
    for m in ('river','lake','estuary','bambooLowlands'):add_to(m)
    write(p,s)


def ecology_java():
    patch_java_biomes()
    p=J/'worldgen/feature/KarstAnimalTrailFeature.java';s=text_read(p)
    old='if (!level.getBiome(ground).is(ModBiomes.HIGHLAND_KARST)) continue;'
    new='''if (!level.getBiome(ground).is(ModBiomes.HIGHLAND_KARST)
                        && !level.getBiome(ground).is(ModBiomes.BAMBOO_LOWLANDS)) continue;'''
    s=replace_once(s,old,new,'bamboo trail biome check')
    s=replace_once(s,'BlockState replacement = trailMaterial(x, z);',
        'BlockState replacement = trailMaterial(x, z, level.getBiome(ground).is(ModBiomes.BAMBOO_LOWLANDS));', 'trail material call')
    s=replace_once(s,'private static BlockState trailMaterial(int x, int z) {',
         '''private static BlockState trailMaterial(int x, int z, boolean bamboo) {
        if (bamboo) {
            double p = hash01(x, z, 601);
            if (p < 0.63) return Blocks.COARSE_DIRT.defaultBlockState();
            if (p < 0.85) return Blocks.PODZOL.defaultBlockState();
            return Blocks.ROOTED_DIRT.defaultBlockState();
        }''','trail material declaration')
    write(p,s)

    # Add custom shallow puddles to bamboo biome without extra textures.
    write(J/'worldgen/feature/BambooPoolFeature.java',BAMBOO_POOL_JAVA)
    write(J/'worldgen/feature/WetlandReedsFeature.java',WETLAND_FEATURE_JAVA)
    write(J/'block/custom/ThreeBlockWetlandPlantBlock.java',WETLAND_BLOCK_JAVA)

    p=J/'worldgen/feature/ModFeatureTypes.java';s=text_read(p)
    tag='        Registry.register(\n                BuiltInRegistries.FEATURE_TYPE,\n                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "bamboo_pool"),'
    if '"bamboo_pool"' not in s:
        add='''        Registry.register(BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "bamboo_pool"),
                BambooPoolFeature.CODEC);
        Registry.register(BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "wetland_reeds"),
                WetlandReedsFeature.CODEC);
'''
        idx=s.rfind('    }');s=s[:idx]+add+s[idx:]
    write(p,s)

    p=J/'worldgen/ModFeatures.java';s=text_read(p)
    if 'BAMBOO_POOLS_KEY' not in s:
        anchor='    public static final ResourceKey<Feature> MAPLE_KEY ='
        s=at(s,anchor).replace(anchor,'''
    public static final ResourceKey<Feature> BAMBOO_POOLS_KEY = registerKey("bamboo_pools");
    public static final ResourceKey<Feature> WETLAND_REEDS_KEY = registerKey("wetland_reeds");
''' + anchor,1)
        anchor='    public static void bootstrap(BootstrapContext<Feature> context) {'
        s=at(s,anchor).replace(anchor,anchor+'''
        context.register(BAMBOO_POOLS_KEY, new BambooPoolFeature());
        context.register(WETLAND_REEDS_KEY, new WetlandReedsFeature());''',1)
    write(p,s)

    p=J/'worldgen/ModPlacedFeatures.java';s=text_read(p)
    if 'BAMBOO_GROVE_PLACED_KEY' not in s:
        anchor='    public static final ResourceKey<PlacedFeature> MAPLE_PLACED_KEY'
        s=at(s,anchor).replace(anchor,'''
    public static final ResourceKey<PlacedFeature> BAMBOO_GROVE_PLACED_KEY = registerKey("bamboo_grove_placed");
    public static final ResourceKey<PlacedFeature> BAMBOO_FERNS_PLACED_KEY = registerKey("bamboo_ferns_placed");
    public static final ResourceKey<PlacedFeature> BAMBOO_POOLS_PLACED_KEY = registerKey("bamboo_pools_placed");
    public static final ResourceKey<PlacedFeature> WETLAND_REEDS_PLACED_KEY = registerKey("wetland_reeds_placed");
''' + anchor,1)
        anchor='        // Amphibolite: clusters in metamorphic country rock, like tuff deposits.'
        s=at(s,anchor).replace(anchor,'''
        register(context, BAMBOO_GROVE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.MAPLE_BAMBOO_KEY),
                List.of(CountPlacement.of(32), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        register(context, BAMBOO_FERNS_PLACED_KEY,
                configuredFeatures.getOrThrow(VegetationFeatures.LARGE_FERN),
                List.of(CountPlacement.of(10), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        register(context, BAMBOO_POOLS_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.BAMBOO_POOLS_KEY),
                List.of(RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        register(context, WETLAND_REEDS_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.WETLAND_REEDS_KEY),
                List.of(CountPlacement.of(3), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

''' +anchor,1)
    write(p,s)

    p=J/'block/ModBlocks.java';s=text_read(p)
    if 'public static final Block CATTAILS' not in s:
        anchor='    // Crops & Bushes'
        add='''    // Exactly three blocks high: one waterlogged base, two emergent segments.
    public static final Block CATTAILS = registerBlock("cattails",
            p -> new ThreeBlockWetlandPlantBlock(p.noCollision().instabreak()
                    .sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));
    public static final Block REEDS = registerBlock("reeds",
            p -> new ThreeBlockWetlandPlantBlock(p.noCollision().instabreak()
                    .sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));

'''
        s=at(s,anchor).replace(anchor,add+anchor,1)
    write(p,s)

    p=J/'datagen/ModBlockLootTableProvider.java';s=text_read(p)
    if 'dropSelf(ModBlocks.CATTAILS);' not in s:
        anchor='    public void generate() {'
        s=at(s,anchor).replace(anchor,anchor+'''
        dropSelf(ModBlocks.CATTAILS);
        dropSelf(ModBlocks.REEDS);''',1)
    write(p,s)
    p=R/'assets'/NS/'lang/en_us.json';j=load(p)
    j['block.'+NS+'.cattails']='Cattails'
    j['block.'+NS+'.reeds']='Reeds'
    j['biome.'+NS+'.bamboo_lowlands']='Bamboo Lowlands'
    write(p,j)

    # No generated model conflicts: plant JSONs are hand-authored resources.
    assets=R/'assets'/NS
    for name in ('cattails','reeds'):
        variants={}
        for i,part in enumerate(('bottom','middle','top')):
            write(assets/f'models/block/{name}_{part}.json',
                  {'parent':'minecraft:block/cross',
                   'textures':{'cross':B(f'block/{name}_{part}')}})
            for w in ('false','true'):
                variants[f'part={i},waterlogged={w}']={'model':B(f'block/{name}_{part}')}
        write(assets/f'blockstates/{name}.json',{'variants':variants})
        write(assets/f'models/item/{name}.json',{'parent':'minecraft:item/generated','textures':{'layer0':B(f'block/{name}_top')}})
        write(assets/f'items/{name}.json',{'model':{'type':'minecraft:model','model':B(f'item/{name}')}})

BAMBOO_POOL_JAVA='''package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/** One-block-deep isolated pools on genuinely flat Bamboo Lowlands ground. */
public record BambooPoolFeature() implements Feature {
    public static final MapCodec<BambooPoolFeature> CODEC = MapCodec.unit(new BambooPoolFeature());
    @Override public MapCodec<BambooPoolFeature> codec() { return CODEC; }
    @Override public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        int cx = origin.getX(), cz = origin.getZ();
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,cx,cz)-1;
        if (y <= level.getMinY()+2 || y >= level.getMaxY()-4) return false;
        int radius = 2 + random.nextInt(2);
        // Validate the whole basin before placing any water; prevents giant floods.
        for (int dx=-radius-1;dx<=radius+1;dx++) for (int dz=-radius-1;dz<=radius+1;dz++) {
            if (dx*dx+dz*dz>(radius+1)*(radius+1)) continue;
            BlockPos p = new BlockPos(cx+dx,y,cz+dz);
            if (!level.getBiome(p).is(ModBiomes.BAMBOO_LOWLANDS)) return false;
            int gy=level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,cx+dx,cz+dz)-1;
            if (gy!=y) return false;
            BlockState state=level.getBlockState(p);
            if (!(state.is(ModBlocks.GRASSY_LOAM)||state.is(ModBlocks.LOAM)||state.is(Blocks.PODZOL)||state.is(Blocks.COARSE_DIRT))) return false;
            if (!level.getFluidState(p.above()).isEmpty()) return false;
        }
        boolean changed=false;
        for (int dx=-radius;dx<=radius;dx++) for (int dz=-radius;dz<=radius;dz++) {
            int rr=dx*dx+dz*dz;
            BlockPos p=new BlockPos(cx+dx,y,cz+dz);
            if (rr<radius*radius && rr<=4+random.nextInt(Math.max(1,radius*radius))) {
                level.setBlock(p,Blocks.WATER.defaultBlockState(),2);
                if(random.nextFloat()<0.55f) level.setBlock(p.below(),Blocks.MUD.defaultBlockState(),2);
                changed=true;
            } else if (rr<=radius*radius && random.nextFloat()<0.34f) {
                level.setBlock(p,random.nextBoolean()?Blocks.PODZOL.defaultBlockState():Blocks.COARSE_DIRT.defaultBlockState(),2);
            }
        }
        return changed;
    }
}
'''

WETLAND_FEATURE_JAVA='''package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.block.custom.ThreeBlockWetlandPlantBlock;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/** Cattails and reeds require precisely one submerged block and two dry blocks. */
public record WetlandReedsFeature() implements Feature {
    public static final MapCodec<WetlandReedsFeature> CODEC=MapCodec.unit(new WetlandReedsFeature());
    @Override public MapCodec<WetlandReedsFeature> codec(){ return CODEC; }
    @Override public boolean place(WorldGenLevel level,ChunkGenerator generator,RandomSource random,BlockPos origin){
        int placed=0;
        for(int attempt=0;attempt<12;attempt++){
            int x=origin.getX()+random.nextInt(11)-5;
            int z=origin.getZ()+random.nextInt(11)-5;
            int y=level.getHeight(Heightmap.Types.WORLD_SURFACE,x,z)-1;
            BlockPos base=new BlockPos(x,y,z);
            if(!level.getBiome(base).is(ModBiomes.RIVER) && !level.getBiome(base).is(ModBiomes.LAKE)
                && !level.getBiome(base).is(ModBiomes.ESTUARY)
                && !level.getBiome(base).is(ModBiomes.BAMBOO_LOWLANDS)) continue;
            Block target=random.nextFloat()<0.44F?ModBlocks.CATTAILS:ModBlocks.REEDS;
            if(target instanceof ThreeBlockWetlandPlantBlock plant && plant.placeNaturally(level,base))placed++;
        }
        return placed>0;
    }
}
'''

WETLAND_BLOCK_JAVA='''package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/** Three cross-model segments: waterlogged lower third, two dry upper thirds. */
public class ThreeBlockWetlandPlantBlock extends BushBlock implements SimpleWaterloggedBlock {
    public static final IntegerProperty PART=IntegerProperty.create("part",0,2);
    public static final BooleanProperty WATERLOGGED=BlockStateProperties.WATERLOGGED;
    public ThreeBlockWetlandPlantBlock(Properties properties){
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(PART,0).setValue(WATERLOGGED,false));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){
        builder.add(PART,WATERLOGGED);
    }
    @Override protected boolean mayPlaceOn(BlockState ground,BlockGetter level,BlockPos pos){
        return ground.is(Blocks.MUD)||ground.is(Blocks.CLAY)||ground.is(Blocks.DIRT)
            ||ground.is(Blocks.GRASS_BLOCK)||ground.is(Blocks.COARSE_DIRT)
            ||ground.is(Blocks.PODZOL)||ground.is(ModBlocks.LOAM)||ground.is(ModBlocks.GRASSY_LOAM);
    }
    @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){
        int part=state.getValue(PART);
        if(part==0)return state.getValue(WATERLOGGED)
            && mayPlaceOn(level.getBlockState(pos.below()),level,pos.below());
        BlockState below=level.getBlockState(pos.below());
        return below.is(this) && below.getValue(PART)==part-1;
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext ctx){
        BlockPos pos=ctx.getClickedPos();
        if(!ctx.getLevel().getFluidState(pos).is(Fluids.WATER)
            ||!ctx.getLevel().getFluidState(pos).isSource())return null;
        if(!ctx.getLevel().isEmptyBlock(pos.above())||!ctx.getLevel().isEmptyBlock(pos.above(2)))return null;
        return defaultBlockState().setValue(PART,0).setValue(WATERLOGGED,true);
    }
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){
        for(int part=1;part<=2;part++)level.setBlock(pos.above(part),
            defaultBlockState().setValue(PART,part).setValue(WATERLOGGED,false),3);
    }
    @Override protected FluidState getFluidState(BlockState state){
        return state.getValue(WATERLOGGED)?Fluids.WATER.getSource(false):super.getFluidState(state);
    }
    @Override protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,
             BlockPos pos,Direction direction,BlockPos neighborPos,BlockState neighborState,RandomSource random){
        if(!state.canSurvive(level,pos))return state.getValue(WATERLOGGED)?Blocks.WATER.defaultBlockState():Blocks.AIR.defaultBlockState();
        if(state.getValue(WATERLOGGED))ticks.scheduleTick(pos,Fluids.WATER,Fluids.WATER.getTickDelay(level));
        return super.updateShape(state,level,ticks,pos,direction,neighborPos,neighborState,random);
    }
    public boolean placeNaturally(WorldGenLevel level,BlockPos pos){
        if(pos.getY()<level.getMinY()+1||pos.getY()>level.getMaxY()-3)return false;
        if(!level.getFluidState(pos).is(Fluids.WATER)||!level.getFluidState(pos).isSource())return false;
        if(!mayPlaceOn(level.getBlockState(pos.below()),level,pos.below()))return false;
        if(!level.isEmptyBlock(pos.above())||!level.isEmptyBlock(pos.above(2)))return false;
        level.setBlock(pos,defaultBlockState().setValue(PART,0).setValue(WATERLOGGED,true),2);
        for(int p=1;p<=2;p++)level.setBlock(pos.above(p),defaultBlockState().setValue(PART,p).setValue(WATERLOGGED,false),2);
        return true;
    }
}
'''

def additional_flora():
    """Register existing 16px art for small plants, floating pads, and ginkgo leaf litter."""
    p=J/'block/ModBlocks.java';s=text_read(p)
    if 'public static final Block ALPINE_GRASS' not in s:
        anchor='    // Crops & Bushes'
        add='''    // Small vegetation from the already supplied 16x16 texture atlas.
    public static final Block ALPINE_GRASS = registerBlock("alpine_grass",
            p -> new FlowerBlock(MobEffects.WATER_BREATHING, 5.0F,
                    p.noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));
    public static final Block HAKONE_GRASS = registerBlock("hakone_grass",
            p -> new FlowerBlock(MobEffects.WATER_BREATHING, 5.0F,
                    p.noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));
    public static final Block IRIS_FLOWER = registerBlock("iris_flower",
            p -> new FlowerBlock(MobEffects.WATER_BREATHING, 5.0F,
                    p.noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));
    public static final Block GINKGO_FALLEN_LEAVES = registerBlock("ginkgo_fallen_leaves",
            p -> new CarpetBlock(p.noCollision().strength(0.1f).sound(SoundType.GRASS)));
    public static final Block DUCKWEED = registerBlock("duckweed",
            p -> new WaterlilyBlock(p.noCollision().instabreak().sound(SoundType.GRASS)));
    public static final Block LOTUS = registerBlock("lotus",
            p -> new WaterlilyBlock(p.noCollision().instabreak().sound(SoundType.GRASS)));
    public static final Block LOTUS_FLOWER = registerBlock("lotus_flower",
            p -> new LotusFlowerBlock(p.noCollision().instabreak().sound(SoundType.GRASS)));

'''
        s=at(s,anchor).replace(anchor,add+anchor,1)
    write(p,s)
    write(J/'block/custom/LotusFlowerBlock.java',LOTUS_FLOWER_JAVA)
    write(J/'worldgen/feature/ElsewhereFloraFeature.java',FLORA_JAVA)

    p=J/'worldgen/feature/ModFeatureTypes.java';s=text_read(p)
    if '"elsewhere_flora"' not in s:
        idx=s.rfind('    }');s=s[:idx]+'''        Registry.register(BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "elsewhere_flora"),
                ElsewhereFloraFeature.CODEC);
''' + s[idx:]
    write(p,s)

    p=J/'worldgen/ModFeatures.java';s=text_read(p)
    if 'ELSEWHERE_FLORA_KEY' not in s:
        anchor='    public static final ResourceKey<Feature> MAPLE_KEY ='
        s=at(s,anchor).replace(anchor,
           '    public static final ResourceKey<Feature> ELSEWHERE_FLORA_KEY = registerKey("elsewhere_flora");\n'+anchor,1)
        anchor='    public static void bootstrap(BootstrapContext<Feature> context) {'
        s=at(s,anchor).replace(anchor,anchor+'\n        context.register(ELSEWHERE_FLORA_KEY, new ElsewhereFloraFeature());',1)
    write(p,s)

    p=J/'worldgen/ModPlacedFeatures.java';s=text_read(p)
    if 'ELSEWHERE_FLORA_PLACED_KEY' not in s:
        anchor='    public static final ResourceKey<PlacedFeature> MAPLE_PLACED_KEY'
        s=at(s,anchor).replace(anchor,
            '    public static final ResourceKey<PlacedFeature> ELSEWHERE_FLORA_PLACED_KEY = registerKey("elsewhere_flora_placed");\n'+anchor,1)
        anchor='        // Amphibolite: clusters in metamorphic country rock, like tuff deposits.'
        s=at(s,anchor).replace(anchor,'''
        register(context, ELSEWHERE_FLORA_PLACED_KEY,
                configuredFeatures.getOrThrow(ModFeatures.ELSEWHERE_FLORA_KEY),
                List.of(CountPlacement.of(3), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

'''+anchor,1)
    write(p,s)

    p=J/'worldgen/biome/ModOverworldBiomes.java';s=text_read(p)
    # All common feature orderings: vegetation after own tree, bamboo, grass, reeds.
    for methodname in ('ginkgoForest','bambooLowlands','grassyPlains','volcanicPeak','estuary','lake'):
        start=s.find('    public static Biome '+methodname+'(')
        if start<0:raise RuntimeError('Missing method for flora: '+methodname)
        end=s.find('    public static Biome ',start+10)
        if end<0:end=s.find('    private static Biome landscapeBiome(',start+10)
        piece=s[start:end]
        if 'ELSEWHERE_FLORA_PLACED_KEY' in piece:continue
        if methodname=='volcanicPeak':
            piece=piece.replace('''return new Biome.BiomeBuilder()''','''var generation = new BiomeGenerationSettings.Builder(features, carvers);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        return new Biome.BiomeBuilder()''',1)
            piece=piece.replace('new BiomeGenerationSettings.Builder(features, carvers).build()', 'generation.build()')
        else:
            expr='return new Biome.BiomeBuilder()' if methodname=='ginkgoForest' else 'return landscapeBiome(generation,'
            assert expr in piece,(methodname,expr)
            piece=piece.replace(expr,'''generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                ModPlacedFeatures.ELSEWHERE_FLORA_PLACED_KEY);
        '''+expr,1)
        s=s[:start]+piece+s[end:]
    write(p,s)

    p=J/'datagen/ModBlockLootTableProvider.java';s=text_read(p)
    if 'dropSelf(ModBlocks.ALPINE_GRASS)' not in s:
        anchor='    public void generate() {'
        s=at(s,anchor).replace(anchor,anchor+'''
        dropSelf(ModBlocks.ALPINE_GRASS);
        dropSelf(ModBlocks.HAKONE_GRASS);
        dropSelf(ModBlocks.IRIS_FLOWER);
        dropSelf(ModBlocks.GINKGO_FALLEN_LEAVES);
        dropSelf(ModBlocks.DUCKWEED);
        dropSelf(ModBlocks.LOTUS);
        dropSelf(ModBlocks.LOTUS_FLOWER);''',1)
    write(p,s)

    p=J/'datagen/ModModelProvider.java';s=text_read(p)
    if 'ModBlocks.ALPINE_GRASS' not in s:
        anchor='        blockModelGenerators.createCrossBlock(ModBlocks.BELLFLOWERS, BlockModelGenerators.PlantType.NOT_TINTED);'
        s=at(s,anchor).replace(anchor,anchor+'''
        blockModelGenerators.createCrossBlock(ModBlocks.ALPINE_GRASS, BlockModelGenerators.PlantType.NOT_TINTED);
        blockModelGenerators.createCrossBlock(ModBlocks.HAKONE_GRASS, BlockModelGenerators.PlantType.NOT_TINTED);
        blockModelGenerators.createCrossBlock(ModBlocks.IRIS_FLOWER, BlockModelGenerators.PlantType.NOT_TINTED);
        blockModelGenerators.createCrossBlock(ModBlocks.LOTUS_FLOWER, BlockModelGenerators.PlantType.NOT_TINTED);''',1)
    write(p,s)

    p=J/'datagen/ModBlockTagsProvider.java';s=text_read(p)
    if '.add(ModBlocks.getRK(ModBlocks.GRASSY_LOAM));' not in s[:s.find('tag(BlockTags.MINEABLE_WITH_SHOVEL)')]:
        anchor='tag(BlockTags.DIRT)\n                .add(ModBlocks.getRK(ModBlocks.LOAM));'
        s=at(s,anchor).replace(anchor,'tag(BlockTags.DIRT)\n                .add(ModBlocks.getRK(ModBlocks.LOAM))\n                .add(ModBlocks.getRK(ModBlocks.GRASSY_LOAM));',1)
    write(p,s)

    p=R/'assets'/NS/'lang/en_us.json';j=load(p)
    for id,name in [('alpine_grass','Alpine Grass'),('hakone_grass','Hakone Grass'),
                   ('iris_flower','Iris'),('ginkgo_fallen_leaves','Ginkgo Leaf Litter'),
                   ('duckweed','Duckweed'),('lotus','Lotus'),('lotus_flower','Flowering Lotus')]:
        j['block.'+NS+'.'+id]=name
    write(p,j)

    # Lily-pad footprint models for Duckweed/Lotus; separate cross model flower
    # generated by ModModelProvider, physically rests on the floating pad.
    assets=R/'assets'/NS
    for id in ('duckweed','lotus'):
        write(assets/f'blockstates/{id}.json',{'variants':{'':{'model':B('block/'+id)}}})
        write(assets/f'models/block/{id}.json',{'parent':'minecraft:block/lily_pad',
              'textures':{'particle':B('block/'+id),'texture':B('block/'+id)}})
        write(assets/f'models/item/{id}.json',{'parent':'minecraft:item/generated',
              'textures':{'layer0':B('block/'+id)}})
        write(assets/f'items/{id}.json',{'model':{'type':'minecraft:model','model':B('item/'+id)}})
    write(assets/'blockstates/ginkgo_fallen_leaves.json',
          {'variants':{'':{'model':B('block/ginkgo_fallen_leaves')}}})
    write(assets/'models/block/ginkgo_fallen_leaves.json',
          {'parent':'minecraft:block/carpet','textures':{'wool':B('block/ginkgo_fallen_leaves')}})
    write(assets/'models/item/ginkgo_fallen_leaves.json',
          {'parent':B('block/ginkgo_fallen_leaves')})
    write(assets/'items/ginkgo_fallen_leaves.json',
          {'model':{'type':'minecraft:model','model':B('item/ginkgo_fallen_leaves')}})

LOTUS_FLOWER_JAVA='''package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Cross model flower resting on the separate flat Lotus pad. */
public class LotusFlowerBlock extends BushBlock {
    public LotusFlowerBlock(Properties properties){super(properties);}
    @Override protected boolean mayPlaceOn(BlockState state,BlockGetter level,BlockPos pos){
        return state.is(ModBlocks.LOTUS);
    }
}
'''

FLORA_JAVA='''package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.Fluids;

/** Small ecological details. Bounded attempts: no expensive dense-forest loop. */
public record ElsewhereFloraFeature() implements Feature {
    public static final MapCodec<ElsewhereFloraFeature> CODEC=MapCodec.unit(new ElsewhereFloraFeature());
    @Override public MapCodec<ElsewhereFloraFeature> codec(){return CODEC;}
    @Override public boolean place(WorldGenLevel level,ChunkGenerator generator,RandomSource random,BlockPos origin){
        int changed=0;
        for(int k=0;k<22;k++){
            int x=origin.getX()+random.nextInt(13)-6;
            int z=origin.getZ()+random.nextInt(13)-6;
            int y=level.getHeight(Heightmap.Types.WORLD_SURFACE,x,z);
            BlockPos pos=new BlockPos(x,y,z);
            boolean ginkgo=level.getBiome(pos).is(ModBiomes.GINKGO_FOREST);
            boolean bamboo=level.getBiome(pos).is(ModBiomes.BAMBOO_LOWLANDS);
            boolean peak=level.getBiome(pos).is(ModBiomes.VOLCANIC_PEAK);
            boolean plains=level.getBiome(pos).is(ModBiomes.GRASSY_PLAINS);
            boolean wet=level.getBiome(pos).is(ModBiomes.LAKE)||level.getBiome(pos).is(ModBiomes.ESTUARY);
            if(!ginkgo&&!bamboo&&!peak&&!plains&&!wet)continue;
            if(peak && y>235)continue;
            if(!level.getFluidState(pos.below()).isEmpty() && level.getFluidState(pos.below()).is(Fluids.WATER)){
                if(level.isEmptyBlock(pos) && (wet||bamboo)){
                    Block pad=random.nextFloat()<0.54f?ModBlocks.DUCKWEED:ModBlocks.LOTUS;
                    var state=pad.defaultBlockState();
                    if(state.canSurvive(level,pos)){
                        level.setBlock(pos,state,2);changed++;
                        if(pad==ModBlocks.LOTUS && random.nextFloat()<0.25f && level.isEmptyBlock(pos.above())){
                            var flower=ModBlocks.LOTUS_FLOWER.defaultBlockState();
                            if(flower.canSurvive(level,pos.above()))level.setBlock(pos.above(),flower,2);
                        }
                    }
                }
                continue;
            }
            if(!level.isEmptyBlock(pos)||!level.getFluidState(pos).isEmpty())continue;
            Block plant;
            if(peak)plant=ModBlocks.ALPINE_GRASS;
            else if(ginkgo && random.nextInt(4)==0)plant=ModBlocks.GINKGO_FALLEN_LEAVES;
            else if(wet)plant=ModBlocks.IRIS_FLOWER;
            else if(bamboo)plant=random.nextInt(5)==0?ModBlocks.IRIS_FLOWER:ModBlocks.HAKONE_GRASS;
            else plant=random.nextBoolean()?ModBlocks.HAKONE_GRASS:ModBlocks.ALPINE_GRASS;
            var state=plant.defaultBlockState();
            if(state.canSurvive(level,pos) && level.setBlock(pos,state,2))changed++;
        }
        return changed>0;
    }
}
'''

def main():
    ap=argparse.ArgumentParser(description='Elsewhere 26.3 V4 installer')
    ap.add_argument('--geography',action='store_true',help='new continental terrain, narrow river climate, stone forests and volcano heights')
    ap.add_argument('--ecology',action='store_true',help='Bamboo Lowlands biome, ponds, reeds and cattails blocks/models')
    ap.add_argument('--dry-run',action='store_true',help='report proposed edits without writing')
    args=ap.parse_args()
    if not (args.geography or args.ecology):ap.error('Choose --geography, --ecology, or both')
    if args.geography and not args.ecology and 'BAMBOO_LOWLANDS' not in text_read(J/'worldgen/biome/ModBiomes.java'):
        ap.error('Initial geography install requires --ecology too, so Bamboo Lowlands is registered')
    require(R/'data'/NS/'dimension/elsewheredim.json')
    try:
        if args.geography:
            counts=geography();print('Climate entries:',counts[0],'->',counts[1])
        if args.ecology:
            ecology_java()
            additional_flora()
    except Exception as e:
        print('PATCH ABORTED, no files changed:',e,file=sys.stderr)
        return 2
    if args.dry_run:
        for p in sorted(changes):print('WOULD WRITE',p)
        return 0
    manifest=BACK/'manifest.json'
    created=set(json.loads(manifest.read_text(encoding='utf-8'))['created']) if manifest.exists() else set()
    for p,content in changes.items():
        target=BASE/p
        if target.exists():
            back=BACK/p
            if not back.exists():back.parent.mkdir(parents=True,exist_ok=True);shutil.copy2(target,back)
        else:
            created.add(p.as_posix())
        target.parent.mkdir(parents=True,exist_ok=True)
        target.write_text(content,encoding='utf-8')
    if changes:
        BACK.mkdir(parents=True,exist_ok=True)
        manifest.write_text(json.dumps({'created':sorted(created)},indent=2)+'\n',encoding='utf-8')
    print(f'Written {len(changes)} files; backups stored at {BACK}')
    print('Next: .\\gradlew.bat runDatagen && .\\gradlew.bat runClient')
    return 0

if __name__=='__main__':sys.exit(main())
