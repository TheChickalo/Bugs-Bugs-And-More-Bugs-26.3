package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

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
