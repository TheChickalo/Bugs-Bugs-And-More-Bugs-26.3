package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.chickalo.bugsbugsandmorebugs.block.custom.VariableHeightPlantBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/** Small ecological details. Bounded attempts: no expensive dense-forest loop. */
public record ElsewhereFloraFeature() implements Feature {
    public static final MapCodec<ElsewhereFloraFeature> CODEC=MapCodec.unit(new ElsewhereFloraFeature());
    @Override public MapCodec<ElsewhereFloraFeature> codec(){return CODEC;}
    @Override public boolean place(WorldGenLevel level,ChunkGenerator generator,RandomSource random,BlockPos origin){
        int changed=0;
        for(int k=0;k<9;k++){
            int x=origin.getX()+random.nextInt(13)-6;
            int z=origin.getZ()+random.nextInt(13)-6;
            int y=level.getHeight(Heightmap.Types.WORLD_SURFACE,x,z);
            BlockPos pos=new BlockPos(x,y,z);
            boolean ginkgo=level.getBiome(pos).is(ModBiomes.GINKGO_FOREST);
            boolean bamboo=level.getBiome(pos).is(ModBiomes.BAMBOO_LOWLANDS);
            boolean peak=level.getBiome(pos).is(ModBiomes.VOLCANIC_PEAK);
            boolean plains=level.getBiome(pos).is(ModBiomes.GRASSY_PLAINS);
            boolean limestone=level.getBiome(pos).is(ModBiomes.STONE_FOREST);
            boolean wet=level.getBiome(pos).is(ModBiomes.LAKE)||level.getBiome(pos).is(ModBiomes.ESTUARY)||level.getBiome(pos).is(ModBiomes.RIVER);
            if(!ginkgo&&!bamboo&&!peak&&!plains&&!wet&&!limestone)continue;
            if(peak && y>235)continue;
            if(!level.getFluidState(pos.below()).isEmpty() && level.getFluidState(pos.below()).is(Fluids.WATER)){
                if(level.isEmptyBlock(pos) && (wet||bamboo||limestone) && random.nextInt(limestone?12:5)==0){
                    Block pad=random.nextFloat()<0.40f ? ModBlocks.DUCKWEED
                            : random.nextFloat()<0.36f ? ModBlocks.LOTUS_FLOWER : ModBlocks.LOTUS;
                    var state=pad.defaultBlockState();
                    if(state.canSurvive(level,pos) && level.setBlock(pos,state,2)) changed++;
                }
                continue;
            }
            if(!level.isEmptyBlock(pos)||!level.getFluidState(pos).isEmpty())continue;
            // Short native bamboo grows only on damp, shallow Stone Forest banks.
            if(limestone && y<=84 && random.nextInt(3)==0 && nearWater(level,pos)) {
                VariableHeightPlantBlock bamboo=ModBlocks.DWARF_BAMBOO;
                if(bamboo.placeAtHeight(level,pos,1+random.nextInt(3)))changed++;
                continue;
            }
            Block plant;
            if(peak)plant=ModBlocks.ALPINE_GRASS;
            else if(ginkgo && random.nextInt(3)==0)plant=ModBlocks.GINKGO_FALLEN_LEAVES;
            else if(wet)plant=random.nextInt(4)==0?ModBlocks.HAKONE_GRASS:ModBlocks.IRIS_FLOWER;
            else if(limestone)plant=y<=84 && random.nextInt(3)==0 ? ModBlocks.IRIS_FLOWER
                    : random.nextInt(3)==0 ? ModBlocks.ALPINE_GRASS : ModBlocks.HAKONE_GRASS;
            else if(bamboo)plant=random.nextInt(5)==0?ModBlocks.IRIS_FLOWER:ModBlocks.HAKONE_GRASS;
            else plant=random.nextBoolean()?ModBlocks.HAKONE_GRASS:ModBlocks.ALPINE_GRASS;
            var state=plant.defaultBlockState();
            if(plant==ModBlocks.GINKGO_FALLEN_LEAVES)
                state=state.setValue(BlockStateProperties.SEGMENT_AMOUNT,1+random.nextInt(4));
            if(state.canSurvive(level,pos) && level.setBlock(pos,state,2))changed++;
        }
        return changed>0;
    }

    private static boolean nearWater(WorldGenLevel level, BlockPos pos) {
        for(int dx=-2;dx<=2;dx++) {
            for(int dz=-2;dz<=2;dz++) {
                if(Math.abs(dx)+Math.abs(dz)>3)continue;
                BlockPos neighbor=pos.offset(dx,0,dz);
                if(level.getBlockState(neighbor).is(Blocks.WATER)
                        || level.getBlockState(neighbor.below()).is(Blocks.WATER))return true;
            }
        }
        return false;
    }
}
