package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomeFamilies;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Small, shallow bamboo wetland pools. Allows a one-block higher edge,
 * but validates the complete rim to keep the water contained.
 */
public record BambooPoolFeature() implements Feature {
    public static final MapCodec<BambooPoolFeature> CODEC=MapCodec.unit(new BambooPoolFeature());
    @Override public MapCodec<BambooPoolFeature> codec(){return CODEC;}

    @Override
    public boolean place(WorldGenLevel level,ChunkGenerator generator,
                         RandomSource random,BlockPos origin){
        int cx=origin.getX(),cz=origin.getZ();
        int y=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,cx,cz)-1;
        if(y<=level.getMinY()+2||y>=level.getMaxY()-4) return false;
        int radius=2+random.nextInt(2);

        // Reject any downward exit from the pond or an adjacent biome.
        // The old rule required every cell to have exactly equal height,
        // which virtually prevented marsh pools from ever generating.
        for(int dx=-radius-1;dx<=radius+1;dx++)for(int dz=-radius-1;dz<=radius+1;dz++){
            int d2=dx*dx+dz*dz;
            if(d2>(radius+1)*(radius+1))continue;
            BlockPos at=new BlockPos(cx+dx,y,cz+dz);
            if(!ModBiomeFamilies.bamboo(level.getBiome(at)))return false;
            int surface=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,cx+dx,cz+dz)-1;
            if(surface<y||surface>y+1)return false;
            BlockState b=level.getBlockState(new BlockPos(cx+dx,surface,cz+dz));
            if(!isNaturalBank(b)||!level.getFluidState(at.above()).isEmpty())return false;
        }

        int changed=0;
        for(int dx=-radius;dx<=radius;dx++)for(int dz=-radius;dz<=radius;dz++){
            double dist=Math.sqrt(dx*dx+dz*dz);
            if(dist>radius-0.20+random.nextFloat()*0.24)continue;
            int x=cx+dx,z=cz+dz;
            if(level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z)-1!=y)continue;
            BlockPos at=new BlockPos(x,y,z);
            if(!isNaturalBank(level.getBlockState(at)))continue;
            if(level.setBlock(at,Blocks.WATER.defaultBlockState(),2))changed++;
            if(random.nextFloat()<0.72F){
                BlockPos bed=at.below();
                if(isNaturalBank(level.getBlockState(bed)))
                    level.setBlock(bed,Blocks.MUD.defaultBlockState(),2);
            }
        }
        return changed>0;
    }

    private static boolean isNaturalBank(BlockState b){
        return b.is(Blocks.GRASS_BLOCK)||b.is(Blocks.DIRT)
                ||(b.is(Blocks.COARSE_DIRT) || b.is(ModBlocks.COARSE_LOAM))||(b.is(Blocks.ROOTED_DIRT) || b.is(ModBlocks.ROOTED_LOAM))
                ||b.is(Blocks.MUD)||b.is(Blocks.PODZOL)
                ||b.is(Blocks.GRAVEL)||b.is(ModBlocks.LOAM)
                ||b.is(ModBlocks.GRASSY_LOAM);
    }
}
