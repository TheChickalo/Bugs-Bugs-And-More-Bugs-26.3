package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import java.util.*;

/**
 * Broad, contour-shaped rimstone formations. One feature owns a bounded
 * multi-chunk footprint (the origin chunk and its immediate neighbors).
 * No writes occur until the complete multi-tier plan is validated.
 */
final class LargeRimstoneTerraces {
    private static final int[][] SIDES={{1,0},{-1,0},{0,1},{0,-1}};
    private static final BlockState WATER=Blocks.WATER.defaultBlockState();
    private static final BlockState AIR=Blocks.AIR.defaultBlockState();
    private static boolean rock(BlockState s) {
        return s.is(ModBlocks.RHYOLITE)||s.is(ModBlocks.LIMESTONE)
                ||s.is(ModBlocks.WEATHERED_LIMESTONE)||s.is(ModBlocks.FLOWSTONE)
                ||s.is(ModBlocks.CHERT)||s.is(ModBlocks.DACITE)||s.is(ModBlocks.SERPENTINITE);
    }
    private static boolean solid(WorldGenLevel l,BlockPos p){
        return l.getFluidState(p).isEmpty()&&l.getBlockState(p).isSolid();
    }
    private static boolean clearable(WorldGenLevel l,BlockPos p){
        return l.getFluidState(p).isEmpty()&&(l.getBlockState(p).isAir()||rock(l.getBlockState(p)));
    }
    private static boolean buried(WorldGenLevel l,BlockPos p){
        return l.getHeight(Heightmap.Types.WORLD_SURFACE_WG,p.getX(),p.getZ())-p.getY()>=16;
    }
    private static BlockPos key(int x,int z,int rot,int u,int v){
        return switch(rot){
            case 1->new BlockPos(x-v,0,z+u);
            case 2->new BlockPos(x-u,0,z-v);
            case 3->new BlockPos(x+v,0,z-u);
            default->new BlockPos(x+u,0,z+v);
        };
    }
    private static int support(WorldGenLevel l,BlockPos p){
        for(int d=1;d<=11;d++){
            BlockPos b=p.below(d);
            if(b.getY()<2)return -1;
            if(solid(l,b))return d;
            if(!l.getBlockState(b).isAir()||!l.getFluidState(b).isEmpty())return -1;
        }
        return -1;
    }
    private static boolean canWater(WorldGenLevel l,BlockPos p){
        return p.getY()>=6&&p.getY()<=30&&buried(l,p)
                &&clearable(l,p)&&clearable(l,p.above())&&clearable(l,p.above(2))
                &&support(l,p)>0;
    }
    private static boolean canWall(WorldGenLevel l,BlockPos p){
        if(p.getY()<6||p.getY()>31||!buried(l,p)||!l.getFluidState(p).isEmpty())return false;
        return solid(l,p)||l.getBlockState(p).isAir()&&support(l,p)>0;
    }
    static boolean place(WorldGenLevel l,BlockPos origin,RandomSource rng){
        int chunkX=Math.floorDiv(origin.getX(),16),chunkZ=Math.floorDiv(origin.getZ(),16);
        // Space out large formations and prevent adjacent centers from fighting for the same cells.
        if(Math.floorMod(chunkX,3)!=0||Math.floorMod(chunkZ,3)!=0)return false;
        int x=chunkX*16+8,z=chunkZ*16+8;
        // A second placement attempt must not build another staircase on top of the first.
        for(int dx=-4;dx<=4;dx++)for(int dz=-4;dz<=4;dz++)
            for(int y=6;y<=32;y++)
                if(l.getBlockState(new BlockPos(x+dx,y,z+dz)).is(ModBlocks.RIMSTONE))return false;
        int first=rng.nextInt(4);
        double phase=rng.nextDouble()*Math.PI*2;
        for(int rotTry=0;rotTry<4;rotTry++){
            int rot=(first+rotTry)%4;
            for(int tiers=6;tiers>=4;tiers--){
                int radius=tiers==6?19:tiers==5?16:13;
                BlockPos sample=key(x,z,rot,-radius/2,0);
                for(int y=12;y>=-12;y--){
                    BlockPos at=sample.above(origin.getY()+y);
                    if(at.getY()<7||at.getY()>24||!buried(l,at)
                            ||!l.getBiome(at).equals(l.getBiome(origin)))continue;
                    if(!l.getBlockState(at).isAir()||!solid(l,at.below()))continue;
                    for(int shift=-2;shift<=0;shift++)
                        if(plan(l,x,z,rot,at.getY()+shift,tiers,radius,phase))return true;
                }
            }
        }
        return false;
    }
    private static boolean plan(WorldGenLevel l,int x,int z,int rot,int base,int tiers,int radius,double phase){
        int width=tiers==6?11:tiers==5?10:8;
        Map<BlockPos,Integer> shape=new HashMap<>(),wet=new HashMap<>();
        // A scalloped footprint and curved, continuously varying contour lines
        // replace the former chain of tiny round or rectangular pools.
        for(int u=-radius;u<=radius;u++)for(int v=-width;v<=width;v++){
            double centerline=1.8*Math.sin(u*0.15+phase);
            double w=v-centerline;
            double a=Math.atan2(w/width,(double)u/radius);
            double boundary=0.98+0.09*Math.sin(5*a+phase)+0.045*Math.cos(8*a-phase);
            if((double)u*u/(radius*radius)+w*w/(width*width)>boundary*boundary)continue;
            double contour=u+2.5*Math.sin(v*0.23+phase)+1.0*Math.sin(u*0.21+v*0.39);
            int tier=Math.clamp((int)Math.floor((contour+radius)*tiers/(2.0*radius)),0,tiers-1);
            shape.put(key(x,z,rot,u,v),base+tier);
        }
        for(var e:shape.entrySet()){
            BlockPos k=e.getKey();int h=e.getValue();
            boolean edge=false;
            for(int[] dir:SIDES){
                Integer nh=shape.get(k.offset(dir[0],0,dir[1]));
                // Retain a curved rim around the outside and on the lower side of every step.
                if(nh==null||nh>h){edge=true;break;}
            }
            if(!edge&&canWater(l,k.above(h)))wet.put(k,h);
        }
        // Clip around unsuitable terrain rather than canceling an entire formation for one boulder.
        // Remove any cell whose neighboring wall could not actually be supported.
        boolean changed;
        do{
            changed=false;List<BlockPos> remove=new ArrayList<>();
            for(var e:wet.entrySet()){
                BlockPos k=e.getKey();int h=e.getValue();
                for(int[] dir:SIDES){
                    BlockPos q=k.offset(dir[0],0,dir[1]);
                    Integer nh=wet.get(q);
                    if(nh!=null&&nh==h)continue;
                    if(nh!=null||!canWall(l,q.above(h))){remove.add(k);break;}
                }
            }
            for(BlockPos k:remove){wet.remove(k);changed=true;}
        }while(changed);
        Map<BlockPos,Integer> rims=new HashMap<>();
        for(var e:wet.entrySet())for(int[] dir:SIDES){
            BlockPos q=e.getKey().offset(dir[0],0,dir[1]);
            if(!wet.containsKey(q))rims.merge(q,e.getValue(),Math::max);
        }
        // Reject isolated puddles: all intended tiers must contain substantial water,
        // and each neighboring pair must share a retaining wall.
        int[] area=new int[tiers];boolean[] shared=new boolean[tiers-1];int open=0;
        for(var e:wet.entrySet()){
            int h=e.getValue();area[h-base]++;
            if(l.getBlockState(e.getKey().above(h+1)).isAir())open++;
        }
        if(wet.size()<tiers*15||open*100<wet.size()*55)return false;
        for(int n:area)if(n<15)return false;
        for(BlockPos k:rims.keySet()){
            int low=Integer.MAX_VALUE,high=Integer.MIN_VALUE;
            for(int[] dir:SIDES){
                Integer h=wet.get(k.offset(dir[0],0,dir[1]));
                if(h!=null){low=Math.min(low,h);high=Math.max(high,h);}
            }
            if(high==low+1&&low>=base&&high<base+tiers)shared[low-base]=true;
        }
        for(boolean b:shared)if(!b)return false;
        Map<BlockPos,BlockState> writes=new HashMap<>();
        BlockState deposit=ModBlocks.FLOWSTONE.defaultBlockState(),rim=ModBlocks.RIMSTONE.defaultBlockState();
        for(var e:rims.entrySet()){
            BlockPos at=e.getKey().above(e.getValue());
            if(!canWall(l,at))return false;
            if(solid(l,at)){
                if(rock(l.getBlockState(at)))writes.put(at,rim);
            }else{
                int d=support(l,at);if(d<1)return false;
                for(int k=1;k<d;k++)writes.put(at.below(k),deposit);
                writes.put(at,rim);
            }
            // The rim must be solid at all water levels adjacent to it, not just the upper one.
            for(int[] dir:SIDES){
                Integer h=wet.get(e.getKey().offset(dir[0],0,dir[1]));
                if(h==null)continue;
                for(int yy=h;yy<e.getValue();yy++){
                    BlockPos q=e.getKey().above(yy);
                    if(solid(l,q))continue;
                    if(!clearable(l,q))return false;
                    writes.put(q,deposit);
                }
            }
        }
        for(var e:wet.entrySet()){
            BlockPos at=e.getKey().above(e.getValue());
            int d=support(l,at);if(d<1)return false;
            for(int k=1;k<d;k++)writes.put(at.below(k),deposit);
            if(rock(l.getBlockState(at.below())))writes.put(at.below(),deposit);
            writes.put(at,WATER);
            writes.put(at.above(),AIR);writes.put(at.above(2),AIR);
        }
        // Final cardinal containment check at every individual source level.
        for(var e:wet.entrySet()){
            BlockPos at=e.getKey().above(e.getValue());
            BlockState bed=writes.getOrDefault(at.below(),l.getBlockState(at.below()));
            if(!bed.isSolid())return false;
            for(int[] dir:SIDES){
                BlockPos q=at.offset(dir[0],0,dir[1]);
                BlockState b=writes.getOrDefault(q,l.getBlockState(q));
                if(!b.is(Blocks.WATER)&&!b.isSolid())return false;
            }
        }
        for(var e:writes.entrySet())if(!e.getValue().is(Blocks.WATER))l.setBlock(e.getKey(),e.getValue(),2);
        for(var e:writes.entrySet())if(e.getValue().is(Blocks.WATER))l.setBlock(e.getKey(),e.getValue(),2);
        return true;
    }
}
