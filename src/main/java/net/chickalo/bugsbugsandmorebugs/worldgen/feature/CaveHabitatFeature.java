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
import net.minecraft.world.level.material.Fluids;
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
                ||s.is(ModBlocks.WEATHERED_LIMESTONE)||s.is(ModBlocks.AMPHIBOLITE)||s.is(ModBlocks.FLOWSTONE)
                ||s.is(ModBlocks.CHERT)||s.is(ModBlocks.DACITE)||s.is(ModBlocks.SERPENTINITE);
    }
    private static boolean ground(BlockState s){
        return rock(s)||s.is(Blocks.MOSS_BLOCK)||s.is(ModBlocks.ROOTED_LOAM)
                ||s.is(ModBlocks.GRASSY_LOAM)||s.is(ModBlocks.LOAM)||s.is(ModBlocks.CAVE_MOSS);
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
        if(kind==6)return LargeRimstoneTerraces.place(l,origin,rng);
        if(kind==7)return galleryCrystals(l,origin,rng);
        if(kind==8)return waterweed(l,origin);
        if(kind==4 || kind==5)return livingCave(l,origin,rng,kind==4);
        boolean changed=false;
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
                    if(rng.nextInt(3)!=0)continue;
                    BlockState s=ModBlocks.GLOWSHROOM_CLUSTER.defaultBlockState();
                    if(s.canSurvive(l,at))changed|=l.setBlock(at,s,2);
                }
            }
            if(ceiling!=null&&rng.nextInt(3)==0)
                changed|=hang(l,ceiling,ModBlocks.GLOWWORM_THREADS,1+rng.nextInt(4));
        }
        return changed;
    }

    /** Light-bearing ceilings and sparse vegetation in the ordinary Schist caves;
     * dense grassy pockets in Lantern Meadows. Never replaces ores or existing fluids. */
    private static boolean livingCave(WorldGenLevel l,BlockPos origin,RandomSource rng,boolean meadow){
        int cx=origin.getX()&~15,cz=origin.getZ()&~15;
        boolean changed=false;
        for(int dy=0;dy<=15;dy++){
            BlockPos at=origin.below(dy);
            if(!livingValid(l,at,origin)||!air(l,at)||!ground(l.getBlockState(at.below())))continue;
            int radius=meadow?2:1;
            for(int dx=-radius;dx<=radius;dx++)for(int dz=-radius;dz<=radius;dz++){
                if(dx*dx+dz*dz>radius*radius||rng.nextInt(meadow?5:3)==0)continue;
                BlockPos column=at.offset(dx,0,dz);
                if(column.getX()<cx||column.getX()>cx+15||column.getZ()<cz||column.getZ()>cz+15)continue;
                for(int h=1;h>=-2;h--){
                    BlockPos q=column.above(h);
                    if(!livingValid(l,q,origin)||!air(l,q)||!ground(l.getBlockState(q.below())))continue;
                    BlockState under=l.getBlockState(q.below());
                    if(meadow&&rock(under)){
                        changed|=l.setBlock(q.below(),ModBlocks.GRASSY_LOAM.defaultBlockState(),2);
                    }else if(!meadow&&rock(under)&&rng.nextInt(3)==0){
                        changed|=l.setBlock(q.below(),ModBlocks.CAVE_MOSS.defaultBlockState(),2);
                    }
                    BlockState plant;
                    if(meadow){
                        int roll=rng.nextInt(12);
                        plant=(roll==0?ModBlocks.CAVE_LILY:roll<=3?ModBlocks.CAVE_FERN:
                                roll<=5?ModBlocks.HAKONE_GRASS:roll<=7?ModBlocks.FEATHERGRASS:Blocks.SHORT_GRASS).defaultBlockState();
                    }else{
                        if(rng.nextInt(5)!=0)break;
                        plant=(rng.nextInt(3)==0?ModBlocks.GLOWSHROOM_CLUSTER:ModBlocks.CAVE_FERN).defaultBlockState();
                    }
                    if(plant.canSurvive(l,q))changed|=l.setBlock(q,plant,2);
                    break;
                }
            }
            break;
        }
        // Ceiling lights are spaced rather than painting every ceiling with a luminous block.
        if(meadow||rng.nextInt(3)==0){
            for(int dy=-3;dy<=20;dy++){
                BlockPos at=origin.above(dy);
                if(!livingValid(l,at,origin)||!air(l,at)||!rock(l.getBlockState(at.above())))continue;
                // No full glowshroom blocks on ceilings; use the existing thin threads.
                changed|=meadow?lantern(l,at,rng):deepThreads(l,at,1+rng.nextInt(3));
                break;
            }
        }
        return changed;
    }
    /** One hanging bulb with a short connecting vine, never a ceiling full of luminous cubes. */
    private static boolean lantern(WorldGenLevel l,BlockPos at,RandomSource rng){
        int wanted=1+rng.nextInt(3),length=0;
        // Leave at least two clear blocks under the bulb for walking.
        while(length<wanted&&at.getY()-length>=-54&&air(l,at.below(length))
                &&air(l,at.below(length+1))&&air(l,at.below(length+2))
                &&l.getBiome(at.below(length)).equals(l.getBiome(at)))length++;
        if(length==0)return false;
        boolean changed=false;
        for(int i=0;i<length;i++){
            BlockPos p=at.below(i);
            BlockState s=(i==length-1?ModBlocks.LANTERN_BULB:ModBlocks.LANTERN_VINE).defaultBlockState();
            if(!s.canSurvive(l,p))break;
            changed|=l.setBlock(p,s,2);
        }
        return changed;
    }
    private static boolean galleryCrystals(WorldGenLevel l,BlockPos origin,RandomSource rng){
        for(int dy=0;dy<=14;dy++){
            BlockPos at=origin.below(dy);
            if(!livingValid(l,at,origin)||at.getY()>=0||!air(l,at)||!rock(l.getBlockState(at.below())))continue;
            BlockState s=ModBlocks.CRYSTAL_CLUSTER.defaultBlockState();
            return s.canSurvive(l,at)&&l.setBlock(at,s,2);
        }
        return false;
    }
    private static boolean waterweed(WorldGenLevel l,BlockPos origin){
        for(int dy=0;dy<=14;dy++){
            BlockPos at=origin.below(dy);
            if(!livingValid(l,at,origin)||at.getY()>=0||!l.getBlockState(at).is(Blocks.WATER)
                    ||!l.getFluidState(at).is(Fluids.WATER)||!l.getFluidState(at).isSource())continue;
            BlockState s=ModBlocks.SUBTERRANEAN_WATERWEED.defaultBlockState();
            if(s.canSurvive(l,at))return l.setBlock(at,s,2);
        }
        return false;
    }

    private static boolean livingValid(WorldGenLevel l,BlockPos p,BlockPos origin){
        return p.getY()>=-54&&p.getY()<40&&buried(l,p)&&l.getBiome(p).equals(l.getBiome(origin));
    }
    private static boolean deepThreads(WorldGenLevel l,BlockPos at,int wanted){
        int len=0;
        while(len<wanted&&at.getY()-len>=-54&&air(l,at.below(len))
                &&l.getBiome(at.below(len)).equals(l.getBiome(at)))len++;
        boolean changed=false;
        for(int i=0;i<len;i++){
            BlockPos q=at.below(i);
            BlockState s=ModBlocks.GLOWWORM_THREADS.defaultBlockState()
                    .setValue(CaveHangingBlock.BASE,i==0).setValue(CaveHangingBlock.TIP,i==len-1);
            if(!s.canSurvive(l,q))break;
            changed|=l.setBlock(q,s,2);
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

}
