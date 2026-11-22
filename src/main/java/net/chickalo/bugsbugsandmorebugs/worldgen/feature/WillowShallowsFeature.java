package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.Fluids;

/**
 * Restrict open water columns inside Willow Backwaters to 1–3 blocks deep.
 * Raise the natural silt/mud bed instead of changing sea level, adding air or
 * breaking the water surface. Only affects generated chunks in the biome.
 */
public record WillowShallowsFeature() implements Feature {
    public static final MapCodec<WillowShallowsFeature> CODEC=
            MapCodec.unit(new WillowShallowsFeature());
    @Override public MapCodec<WillowShallowsFeature> codec(){ return CODEC; }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator,
                         RandomSource random, BlockPos origin) {
        boolean changed=false;
        for(int dx=0;dx<16;dx++)for(int dz=0;dz<16;dz++) {
            int x=origin.getX()+dx,z=origin.getZ()+dz;
            int top=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z)-1;
            BlockPos surface=new BlockPos(x,top,z);
            if(!level.getBiome(surface).is(ModBiomes.WILLOW_BACKWATERS))continue;
            if(!level.getBlockState(surface).is(Blocks.WATER)
                    || !level.getFluidState(surface).isSource())continue;
            int depth=0;
            while(depth<40 && level.getBlockState(surface.below(depth)).is(Blocks.WATER)
                    && level.getFluidState(surface.below(depth)).isSource())
                depth++;
            if(depth<=3 || depth>=40)continue;
            BlockPos floor=surface.below(depth);
            if(!level.getBlockState(floor).isSolidRender()
                    && !level.getBlockState(floor).is(Blocks.MUD)
                    && !level.getBlockState(floor).is(ModBlocks.SILT))continue;
            // Keep all visible water in the 1–3 block range. Do not fill caves.
            int target=1+Math.floorMod(x*19+z*31,3);
            for(int d=depth-1;d>=target;d--){
                BlockPos cell=surface.below(d);
                if(!level.getBlockState(cell).is(Blocks.WATER))break;
                if(level.setBlock(cell,
                        random.nextInt(5)==0?Blocks.MUD.defaultBlockState()
                                :ModBlocks.SILT.defaultBlockState(),2))changed=true;
            }
        }
        return changed;
    }
}
