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
import net.minecraft.world.level.material.Fluids;
import java.util.ArrayList;
import java.util.List;

/** Small, one-block-deep still-water forest pools, with no raised berms or cut-off chunk edges. */
public record WillowPoolsFeature() implements Feature {
    public static final MapCodec<WillowPoolsFeature> CODEC=MapCodec.unit(new WillowPoolsFeature());
    @Override public MapCodec<WillowPoolsFeature> codec(){ return CODEC; }

    private static boolean soil(BlockState s) {
        return s.is(Blocks.GRASS_BLOCK)||s.is(Blocks.DIRT)||s.is(Blocks.MUD)
                ||s.is(Blocks.CLAY)||s.is(ModBlocks.SILT)
                ||s.is(ModBlocks.GRASSY_LOAM)||s.is(ModBlocks.LOAM);
    }
    @Override public boolean place(WorldGenLevel level,ChunkGenerator generator,
                                   RandomSource random,BlockPos origin) {
        int baseX=origin.getX() & ~15,baseZ=origin.getZ() & ~15;
        int changed=0;
        // One or two small depressions in a chunk, and some chunks left dry.
        int tries=random.nextInt(3);
        for(int attempt=0;attempt<tries;attempt++){
            int cx=baseX+4+random.nextInt(8), cz=baseZ+4+random.nextInt(8);
            int y=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,cx,cz)-1;
            if(y<58||y>108)continue;
            int radius=2+random.nextInt(2);
            List<BlockPos> cells=new ArrayList<>();
            boolean suitable=true;
            for(int dx=-radius;dx<=radius && suitable;dx++)
                for(int dz=-radius;dz<=radius;dz++){
                    double q=(dx*dx+dz*dz)/(double)(radius*radius);
                    if(q>1.05)continue;
                    int x=cx+dx,z=cz+dz;
                    // Compact ponds fit entirely within the chunk; dry edge
                    // remains at the prior grass level and prevents overflow.
                    if(x<=baseX||x>=baseX+15||z<=baseZ||z>=baseZ+15){
                        suitable=false;break;
                    }
                    int top=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z)-1;
                    BlockPos at=new BlockPos(x,top,z);
                    if(top!=y || !ModBiomeFamilies.willow(level.getBiome(at))
                            || !soil(level.getBlockState(at))
                            || !soil(level.getBlockState(at.below()))
                            || !level.getFluidState(at).isEmpty()
                            || !level.getBlockState(at.above()).isAir()){
                        suitable=false;break;
                    }
                    cells.add(at);
                }
            if(!suitable||cells.size()<10)continue;
            for(BlockPos at:cells){
                BlockPos water=at.below();
                level.setBlock(water,Blocks.WATER.defaultBlockState(),2);
                level.setBlock(at,Blocks.AIR.defaultBlockState(),2);
                level.setBlock(at.below(2),random.nextInt(5)==0
                        ?Blocks.MUD.defaultBlockState():ModBlocks.SILT.defaultBlockState(),2);
                changed++;
            }
        }
        return changed>0;
    }
}
