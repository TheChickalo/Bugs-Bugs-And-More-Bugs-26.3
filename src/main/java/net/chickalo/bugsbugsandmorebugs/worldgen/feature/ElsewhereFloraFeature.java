package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SegmentableBlock;
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
        for(int k=0;k<34;k++){
            int x=origin.getX()+random.nextInt(13)-6;
            int z=origin.getZ()+random.nextInt(13)-6;
            int y=level.getHeight(Heightmap.Types.WORLD_SURFACE,x,z);
            BlockPos pos=new BlockPos(x,y,z);
            boolean ginkgo=level.getBiome(pos).is(ModBiomes.GINKGO_FOREST);
            boolean stone=level.getBiome(pos).is(ModBiomes.STONE_FOREST);
            boolean hinoki=level.getBiome(pos).is(ModBiomes.HINOKI_HIGHLANDS);
            boolean ocean=level.getBiome(pos).is(ModBiomes.OCEAN);
            boolean bamboo=level.getBiome(pos).is(ModBiomes.BAMBOO_LOWLANDS);
            boolean peak=level.getBiome(pos).is(ModBiomes.VOLCANIC_PEAK);
            boolean plains=level.getBiome(pos).is(ModBiomes.GRASSY_PLAINS);
            boolean wet=level.getBiome(pos).is(ModBiomes.LAKE)||level.getBiome(pos).is(ModBiomes.ESTUARY)||level.getBiome(pos).is(ModBiomes.RIVER);
            if(!ginkgo&&!bamboo&&!peak&&!plains&&!wet&&!stone&&!hinoki&&!ocean)continue;
            // Use the actual ocean floor, not the water surface, for seagrass.
            if((ocean||wet) && random.nextInt(5)==0){
                int floorY=level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG,x,z);
                BlockPos submerged=new BlockPos(x,floorY,z);
                if(submerged.getY()<y-1 && level.getFluidState(submerged).is(Fluids.WATER)
                        && level.getBlockState(submerged).is(Blocks.WATER)){
                    var seaGrass=Blocks.SEAGRASS.defaultBlockState();
                    if(seaGrass.canSurvive(level,submerged) && level.setBlock(submerged,seaGrass,2))changed++;
                }
            }
            if(ocean || (peak && y>235))continue;
            if(!level.getFluidState(pos.below()).isEmpty() && level.getFluidState(pos.below()).is(Fluids.WATER)){
                if(level.isEmptyBlock(pos) && (wet||bamboo)){
                    // One water-surface block: the flowering lotus model includes the pad
                    // and blossom. Never place a second flower a full block above it.
                    Block pad=random.nextFloat()<0.50f?ModBlocks.DUCKWEED:
                              random.nextFloat()<0.32f?ModBlocks.LOTUS_FLOWER:ModBlocks.LOTUS;
                    var state=pad.defaultBlockState();
                    if(state.canSurvive(level,pos)){
                        level.setBlock(pos,state,2);changed++;
                    }
                }
                continue;
            }
            if(!level.isEmptyBlock(pos)||!level.getFluidState(pos).isEmpty())continue;
            Block plant;
            if(peak)plant=ModBlocks.ALPINE_GRASS;
            else if(ginkgo)plant=random.nextInt(3)!=0?ModBlocks.GINKGO_FALLEN_LEAVES:ModBlocks.HAKONE_GRASS;
            else if(wet)plant=random.nextInt(3)==0?ModBlocks.HAKONE_GRASS:ModBlocks.IRIS_FLOWER;
            else if(stone)plant=random.nextInt(3)==0?ModBlocks.ALPINE_GRASS:ModBlocks.HAKONE_GRASS;
            else if(hinoki)plant=random.nextBoolean()?Blocks.FERN:ModBlocks.HAKONE_GRASS;
            else if(bamboo)plant=random.nextInt(5)==0?ModBlocks.IRIS_FLOWER:ModBlocks.HAKONE_GRASS;
            else plant=random.nextBoolean()?ModBlocks.HAKONE_GRASS:ModBlocks.ALPINE_GRASS;
            var state=plant.defaultBlockState();
            if(plant==ModBlocks.GINKGO_FALLEN_LEAVES)
                state=state.setValue(SegmentableBlock.AMOUNT,1+random.nextInt(4));
            if(state.canSurvive(level,pos) && level.setBlock(pos,state,2))changed++;
        }
        return changed>0;
    }
}
