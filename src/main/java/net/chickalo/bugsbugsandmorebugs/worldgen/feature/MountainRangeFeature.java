package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.ArrayList;
import java.util.List;

/**
 * Cross-chunk deterministic mountain-range shoulders. A primary peak has a
 * chance to grow subsidiaries to either side; each recursion becomes smaller
 * and less likely. Checks free peak footprints before accepting side peaks.
 *
 * Unlike one-chunk "mountain towers", all chunks compute the same macro seeds
 * and only paint their own 16x16 region, producing continuous ridges.
 * Restricts painting to existing mountain biomes and steep, high terrain.
 */
public record MountainRangeFeature() implements Feature {
    public static final MapCodec<MountainRangeFeature> CODEC=MapCodec.unit(new MountainRangeFeature());
    private record Peak(int x, int z, int radius, int height, double direction) {}

    @Override public MapCodec<MountainRangeFeature> codec(){ return CODEC; }

    private static boolean mountain(Holder<Biome> b){
        return b.is(ModBiomes.VOLCANIC_PEAK)||b.is(ModBiomes.ASHEN_FOOTHILLS)
                ||b.is(ModBiomes.SNOWY_PEAK)||b.is(ModBiomes.HINOKI_HIGHLANDS)
                ||b.is(ModBiomes.HIGHLAND_KARST);
    }

    /** Space in the existing branch layout, so new peaks don't stack. */
    private static boolean room(List<Peak> peaks,int x,int z,int radius){
        for(Peak p:peaks){
            double d=Math.hypot(x-p.x,z-p.z);
            if(d<Math.max(7.0,Math.min(radius,p.radius)*0.95))return false;
        }
        return true;
    }

    private static void extend(List<Peak> peaks,Peak from,RandomSource rng,int depth){
        if(depth>=4)return;
        int nextRadius=Math.max(5,(int)Math.round(from.radius*0.69));
        int nextHeight=Math.max(3,(int)Math.round(from.height*0.69));
        if(nextRadius<6||nextHeight<4)return;
        double chance=0.70*Math.pow(0.54,depth);
        for(int side:new int[]{-1,1}){
            if(rng.nextDouble()>=chance)continue;
            double angle=from.direction+(side==1?0:Math.PI)
                    +(rng.nextDouble()-0.5)*0.60;
            double distance=(from.radius+nextRadius)*0.73;
            int x=from.x+(int)Math.round(Math.cos(angle)*distance);
            int z=from.z+(int)Math.round(Math.sin(angle)*distance);
            if(!room(peaks,x,z,nextRadius))continue;
            Peak next=new Peak(x,z,nextRadius,nextHeight,angle+(rng.nextDouble()-0.5)*0.25);
            peaks.add(next);
            extend(peaks,next,rng,depth+1);
        }
    }

    private static long hash(int x,int z){
        long seed=((long)x*0x9E3779B97F4A7C15L)^((long)z*0xC2B2AE3D27D4EB4FL);
        seed^=seed>>>33;
        seed*=0xFF51AFD7ED558CCDL;
        return seed^(seed>>>29);
    }

    @Override
    public boolean place(WorldGenLevel level,ChunkGenerator generator,
                         RandomSource random,BlockPos origin){
        int x0=origin.getX() & ~15, z0=origin.getZ() & ~15;
        List<Peak> peaks=new ArrayList<>();
        final int region=144, margin=152;
        int minCX=Math.floorDiv(x0-margin,region);
        int maxCX=Math.floorDiv(x0+15+margin,region);
        int minCZ=Math.floorDiv(z0-margin,region);
        int maxCZ=Math.floorDiv(z0+15+margin,region);
        for(int cx=minCX;cx<=maxCX;cx++)for(int cz=minCZ;cz<=maxCZ;cz++){
            RandomSource rng=RandomSource.create(hash(cx,cz));
            if(rng.nextInt(3)!=0)continue;
            int rootX=cx*region+region/2+rng.nextInt(49)-24;
            int rootZ=cz*region+region/2+rng.nextInt(49)-24;
            Peak root=new Peak(rootX,rootZ,30,24+rng.nextInt(10),
                    rng.nextDouble()*Math.PI*2);
            if(!room(peaks,rootX,rootZ,30))continue;
            peaks.add(root);
            extend(peaks,root,rng,0);
        }
        if(peaks.isEmpty())return false;

        int edits=0;
        for(int dx=0;dx<16;dx++)for(int dz=0;dz<16;dz++){
            int x=x0+dx,z=z0+dz;
            int lift=0;
            for(Peak p:peaks){
                double q=((long)(x-p.x)*(x-p.x)+(long)(z-p.z)*(z-p.z))
                        /((double)p.radius*p.radius);
                if(q>=1)continue;
                double shape=1-q;
                lift=Math.max(lift,(int)Math.round(p.height*shape*shape));
            }
            if(lift<2)continue;
            int topY=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z)-1;
            if(topY<110||topY>=level.getMaxY()-35)continue;
            BlockPos top=new BlockPos(x,topY,z);
            Holder<Biome> biome=level.getBiome(top);
            if(!mountain(biome))continue;
            BlockState ground=level.getBlockState(top);
            if(!ground.isSolid()||!level.getFluidState(top).isEmpty()
                    ||!level.getBlockState(top.above()).isAir())continue;
            Block stone=biome.is(ModBiomes.HIGHLAND_KARST)
                    ?ModBlocks.LIMESTONE
                    :biome.is(ModBiomes.SNOWY_PEAK)
                    ?ModBlocks.DACITE
                    :ModBlocks.RHYOLITE;
            int max=Math.min(lift,level.getMaxY()-topY-2);
            for(int h=1;h<=max;h++){
                BlockPos p=top.above(h);
                if(!level.getBlockState(p).isAir())break;
                level.setBlock(p,stone.defaultBlockState(),2);
                edits++;
            }
        }
        return edits>0;
    }
}
