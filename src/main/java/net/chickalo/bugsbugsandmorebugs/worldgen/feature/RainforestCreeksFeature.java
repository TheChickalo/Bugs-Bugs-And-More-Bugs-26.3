package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
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

/**
 * Shallow natural-looking ribbons, following the same world-coordinate curve
 * across chunk edges. The creek bed is placed below the previous grass surface,
 * so exposed source blocks cannot flood a flat rainforest.
 *
 * Drops in the existing terrain produce natural flowing steps, not new oceans
 * or aboveground stacks of water. Does not modify blocks outside its own chunk.
 */
public record RainforestCreeksFeature() implements Feature {
    public static final MapCodec<RainforestCreeksFeature> CODEC =
            MapCodec.unit(new RainforestCreeksFeature());
    @Override public MapCodec<RainforestCreeksFeature> codec() { return CODEC; }

    private static double centerLine(int x, int z) {
        double warp = 11.0 * Math.sin(x * 0.032)
                + 5.0 * Math.sin(x * 0.011 + 1.43);
        int track = Math.round((float)((z - warp) / 116.0));
        return track * 116.0 + warp;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator,
                         RandomSource random, BlockPos origin) {
        int startX=origin.getX() & ~15, startZ=origin.getZ() & ~15;
        int changed=0;
        for(int dx=0;dx<16;dx++)for(int dz=0;dz<16;dz++){
            int x=startX+dx,z=startZ+dz;
            double distance=Math.abs(z-centerLine(x,z));
            double width=1.45+0.55*Math.sin(x*0.045+z*0.03);
            if(distance>width+1.5)continue;
            int top=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z)-1;
            if(top<72||top>194)continue;
            BlockPos surface=new BlockPos(x,top,z);
            if(!ModBiomeFamilies.cedar(level.getBiome(surface)))continue;
            BlockState old=level.getBlockState(surface);
            boolean soil=old.is(Blocks.GRASS_BLOCK)||old.is(Blocks.DIRT)
                    ||old.is(Blocks.MUD)||old.is(Blocks.PODZOL)
                    ||old.is(ModBlocks.LOAM)||old.is(ModBlocks.GRASSY_LOAM)
                    ||old.is(ModBlocks.SILT);
            if(!soil||!level.getFluidState(surface).isEmpty()
                    ||!level.getBlockState(surface.above()).isAir())continue;
            // Avoid a flood spill over steep drops or into neighboring caves.
            if(Math.abs(level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z+1)
                    -level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z-1))>4
                || Math.abs(level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x+1,z)
                    -level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x-1,z))>4)continue;

            if(distance>width){
                if(random.nextInt(4)!=0)continue;
                level.setBlock(surface,random.nextInt(3)==0
                        ?Blocks.MOSS_BLOCK.defaultBlockState()
                        :Blocks.MUD.defaultBlockState(),2);
                changed++;
                continue;
            }
            // The old top block becomes air; the water surface is one block
            // lower. A solid, rain-washed silt bed keeps depth near 1–2 blocks.
            int depth=(distance<0.8 && random.nextInt(3)==0)?2:1;
            BlockPos waterTop=surface.below();
            if(!level.getBlockState(waterTop).isSolid()
                    ||!level.getFluidState(waterTop).isEmpty())continue;
            BlockPos bed=waterTop.below(depth);
            if(!level.getBlockState(bed).isSolid()
                    ||!level.getFluidState(bed).isEmpty())continue;
            level.setBlock(bed,ModBlocks.SILT.defaultBlockState(),2);
            for(int d=0;d<depth;d++)level.setBlock(waterTop.below(d),
                    Blocks.WATER.defaultBlockState(),2);
            level.setBlock(surface,Blocks.AIR.defaultBlockState(),2);
            changed++;
        }
        return changed>0;
    }
}
