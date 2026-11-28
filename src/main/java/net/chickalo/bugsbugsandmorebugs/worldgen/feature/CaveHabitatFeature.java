package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.block.custom.CaveHangingBlock;
import net.chickalo.bugsbugsandmorebugs.block.custom.CaveSpeleothemBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import java.util.*;

/** Cave content above the Sundering Horizon; all terrain writes remain within
 * the generating chunk and preserve existing ores and unrelated features. */
public record CaveHabitatFeature(int kind) implements Feature {
    public static final MapCodec<CaveHabitatFeature> CODEC=RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.INT.fieldOf("kind").forGetter(CaveHabitatFeature::kind)).apply(instance,CaveHabitatFeature::new));
    @Override public MapCodec<CaveHabitatFeature> codec(){return CODEC;}
    private static final int[][] SIDES={{1,0},{-1,0},{0,1},{0,-1}};
    private static boolean rock(BlockState s){
        return s.is(ModBlocks.RHYOLITE)||s.is(ModBlocks.SCHIST)||s.is(ModBlocks.LIMESTONE)
                ||s.is(ModBlocks.WEATHERED_LIMESTONE)||s.is(ModBlocks.AMPHIBOLITE)||s.is(ModBlocks.FLOWSTONE);
    }
    private static boolean ground(BlockState s){
        return rock(s)||s.is(Blocks.MOSS_BLOCK)||s.is(ModBlocks.ROOTED_LOAM);
    }
    private static boolean buried(WorldGenLevel l,BlockPos p){
        return l.getHeight(Heightmap.Types.WORLD_SURFACE_WG,p.getX(),p.getZ())-p.getY()>=14;
    }
    private static boolean air(WorldGenLevel l,BlockPos p){
        return l.getBlockState(p).isAir()&&l.getFluidState(p).isEmpty();
    }
    private static boolean valid(WorldGenLevel l,BlockPos p,BlockPos origin){
        return p.getY()>=5&&p.getY()<40&&buried(l,p)&&l.getBiome(p).equals(l.getBiome(origin));
    }
    @Override public boolean place(WorldGenLevel l,ChunkGenerator g,RandomSource rng,BlockPos origin){
        boolean changed=false;
        if(kind==0)changed=terraces(l,origin,rng);
        if(kind==3){
            for(int dy=0;dy<=10;dy++){
                BlockPos at=origin.below(dy);
                if(at.getY()<-56||at.getY()>=0||!buried(l,at)||!l.getBiome(at).equals(l.getBiome(origin)))continue;
                if(air(l,at)&&l.getBlockState(at.below()).is(ModBlocks.SCHIST))
                    return l.setBlock(at.below(),ModBlocks.AMPHIBOLITE.defaultBlockState(),2);
            }
            return false;
        }
        BlockPos floor=null, ceiling=null;
        for(int dy=0;dy<=12;dy++){
            BlockPos at=origin.below(dy);
            if(valid(l,at,origin)&&air(l,at)&&ground(l.getBlockState(at.below()))){floor=at;break;}
        }
        for(int dy=-5;dy<=14;dy++){
            BlockPos at=origin.above(dy);
            if(valid(l,at,origin)&&air(l,at)&&rock(l.getBlockState(at.above()))){ceiling=at;break;}
        }
        if(kind==0){
            if(ceiling!=null){
                if(rng.nextInt(3)==0)changed|=hang(l,ceiling,ModBlocks.FLOWSTONE_CURTAIN,2+rng.nextInt(4));
                else changed|=speleothem(l,ceiling,false,1+rng.nextInt(4));
            }
            if(floor!=null&&rng.nextInt(3)==0)changed|=speleothem(l,floor,true,1+rng.nextInt(3));
        }else if(kind==1){
            if(ceiling!=null){
                changed|=hang(l,ceiling,ModBlocks.CAVE_ROOT_STRANDS,2+rng.nextInt(4));
                if(changed&&floor!=null&&rng.nextInt(8)==0&&rock(l.getBlockState(floor.below())))
                    changed|=l.setBlock(floor.below(),ModBlocks.ROOTED_LOAM.defaultBlockState(),2);
            }
        }else if(kind==2){
            if(floor!=null){
                int cx=origin.getX()&~15,cz=origin.getZ()&~15;
                for(int dx=-2;dx<=2;dx++)for(int dz=-2;dz<=2;dz++){
                    if(dx*dx+dz*dz>5||rng.nextInt(3)==0)continue;
                    BlockPos at=floor.offset(dx,0,dz);
                    if(at.getX()<cx||at.getX()>cx+15||at.getZ()<cz||at.getZ()>cz+15)continue;
                    if(!valid(l,at,origin)||!air(l,at)||!ground(l.getBlockState(at.below())))continue;
                    BlockState s=(rng.nextInt(4)==0?ModBlocks.GLOWSHROOM_CLUSTER:ModBlocks.FUNGAL_MAT).defaultBlockState();
                    if(s.canSurvive(l,at))changed|=l.setBlock(at,s,2);
                }
            }
            if(ceiling!=null&&rng.nextInt(3)==0)
                changed|=hang(l,ceiling,ModBlocks.GLOWWORM_THREADS,1+rng.nextInt(4));
        }
        return changed;
    }

    private static boolean hang(WorldGenLevel l,BlockPos at,Block block,int wanted){
        int length=0;
        while(length<wanted&&at.getY()-length>=5&&air(l,at.below(length)))length++;
        boolean placed=false;
        for(int i=0;i<length;i++){
            BlockPos p=at.below(i);
            BlockState s=block.defaultBlockState().setValue(CaveHangingBlock.BASE,i==0)
                    .setValue(CaveHangingBlock.TIP,i==length-1);
            if(!s.canSurvive(l,p))break;
            placed|=l.setBlock(p,s,2);
        }
        return placed;
    }
    private static boolean speleothem(WorldGenLevel l,BlockPos at,boolean up,int wanted){
        int length=0;
        while(length<wanted&&at.getY()+(up?length:-length)>=5
                &&at.getY()+(up?length:-length)<40&&air(l,at.above(up?length:-length)))length++;
        boolean placed=false;
        for(int i=0;i<length;i++){
            BlockPos p=at.above(up?i:-i);
            int seg=i==length-1?0:i==0?3:i==length-2?1:2;
            BlockState s=ModBlocks.LIMESTONE_SPELEOTHEM.defaultBlockState()
                    .setValue(CaveSpeleothemBlock.UP,up).setValue(CaveSpeleothemBlock.SEGMENT,seg);
            if(!s.canSurvive(l,p))break;
            placed|=l.setBlock(p,s,2);
        }
        return placed;
    }

    /** One connected rimstone staircase, never unrelated ponds. A shared
     * retaining wall separates every consecutive water level. */
    private static boolean terraces(WorldGenLevel l,BlockPos origin,RandomSource rng){
        int cx=origin.getX()&~15,cz=origin.getZ()&~15;
        int rotation=rng.nextInt(4);
        for(int tierCount=3;tierCount>=2;tierCount--){
            int len=tierCount*4;
            int u0=(15-len)/2;
            int v0=7+rng.nextInt(2);
            BlockPos sample=local(cx,cz,rotation,u0+2,v0,origin.getY());
            for(int dy=5;dy>=-10;dy--){
                BlockPos at=sample.above(dy);
                if(at.getY()<6||at.getY()>33||!buried(l,at)||!air(l,at)||!rock(l.getBlockState(at.below())))continue;
                if(!l.getBiome(at).equals(l.getBiome(origin)))continue;
                for(int lift=0;lift<=2;lift++){
                    if(staircase(l,origin,cx,cz,rotation,u0,v0,at.getY()+lift,tierCount,rng))return true;
                }
            }
        }
        return false;
    }
    private static BlockPos local(int cx,int cz,int rot,int u,int v,int y){
        return switch(rot){
            case 1->new BlockPos(cx+15-v,y,cz+u);
            case 2->new BlockPos(cx+15-u,y,cz+15-v);
            case 3->new BlockPos(cx+v,y,cz+15-u);
            default->new BlockPos(cx+u,y,cz+v);
        };
    }
    private static boolean staircase(WorldGenLevel l,BlockPos origin,int cx,int cz,int rot,
            int u0,int v0,int y,int count,RandomSource rng){
        Map<BlockPos,Integer> water=new HashMap<>();
        Map<BlockPos,Integer> wall=new HashMap<>();
        Map<BlockPos,BlockState> writes=new HashMap<>();
        BlockState foundation=ModBlocks.FLOWSTONE.defaultBlockState(),rim=ModBlocks.RIMSTONE.defaultBlockState();
        // Horizontal keys deliberately use Y=0. Adjacent tiers share a full row of rimstone.
        for(int i=0;i<count;i++){
            int height=y+i;
            int width=2+rng.nextInt(2);
            for(int du=1;du<=3;du++){
                int half=du==2?width:width-1;
                for(int dv=-half;dv<=half;dv++){
                    BlockPos key=local(cx,cz,rot,u0+4*i+du,v0+dv,0);
                    water.put(key,height);
                }
            }
        }
        for(var e:water.entrySet()){
            for(int[] s:SIDES){
                BlockPos q=e.getKey().offset(s[0],0,s[1]);
                if(!water.containsKey(q))wall.merge(q,e.getValue(),Math::max);
            }
        }
        // Every tier must share a rim with its neighbor, and the higher rim
        // is also solid down to the lower pool floor.
        for(int i=1;i<count;i++){
            BlockPos q=local(cx,cz,rot,u0+4*i,v0,0);
            if(!wall.containsKey(q)||wall.get(q)!=y+i)return false;
        }
        Map<BlockPos,Integer> all=new HashMap<>(water);all.putAll(wall);
        for(var e:all.entrySet()){
            BlockPos k=e.getKey(),at=k.above(e.getValue());
            boolean wet=water.containsKey(k);
            if(at.getX()<cx||at.getX()>cx+15||at.getZ()<cz||at.getZ()>cz+15
                    ||at.getY()>37||!buried(l,at))return false;
            if(!air(l,at.above()))return false;
            BlockState old=l.getBlockState(at);
            // Permit a shallow cut into plain rock, never ore/soil/features or existing liquid.
            if(!air(l,at)&&!rock(old))return false;
            boolean supported=false;
            for(int down=1;down<=8;down++){
                BlockPos b=at.below(down);
                if(b.getY()<5)return false;
                if(rock(l.getBlockState(b))){supported=true;break;}
                if(!air(l,b))return false;
                writes.put(b,foundation);
            }
            if(!supported)return false;
            writes.put(at,wet?Blocks.WATER.defaultBlockState():rim);
        }
        // Validate cardinal walls and floor against the completed plan before committing.
        for(var e:water.entrySet()){
            BlockPos at=e.getKey().above(e.getValue());
            BlockPos below=at.below();
            if(!writes.containsKey(below)&&!rock(l.getBlockState(below)))return false;
            for(int[] s:SIDES){
                BlockPos q=at.offset(s[0],0,s[1]);
                BlockState state=writes.getOrDefault(q,l.getBlockState(q));
                if(!state.is(Blocks.WATER)&&!state.is(ModBlocks.RIMSTONE)&&!rock(state))return false;
            }
        }
        for(var e:writes.entrySet())if(!e.getValue().is(Blocks.WATER))l.setBlock(e.getKey(),e.getValue(),2);
        for(var e:writes.entrySet())if(e.getValue().is(Blocks.WATER))l.setBlock(e.getKey(),e.getValue(),2);
        return true;
    }
}
