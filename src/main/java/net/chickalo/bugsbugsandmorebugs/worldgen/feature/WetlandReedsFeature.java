package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.block.custom.ThreeBlockWetlandPlantBlock;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/** Cattails and reeds require precisely one submerged block and two dry blocks. */
public record WetlandReedsFeature() implements Feature {
    public static final MapCodec<WetlandReedsFeature> CODEC=MapCodec.unit(new WetlandReedsFeature());
    @Override public MapCodec<WetlandReedsFeature> codec(){ return CODEC; }
    @Override public boolean place(WorldGenLevel level,ChunkGenerator generator,RandomSource random,BlockPos origin){
        int placed=0;
        for(int attempt=0;attempt<5;attempt++){
            int x=origin.getX()+random.nextInt(11)-5;
            int z=origin.getZ()+random.nextInt(11)-5;
            int y=level.getHeight(Heightmap.Types.WORLD_SURFACE,x,z)-1;
            BlockPos base=new BlockPos(x,y,z);
            if(!level.getBiome(base).is(ModBiomes.RIVER) && !level.getBiome(base).is(ModBiomes.LAKE)
                && !level.getBiome(base).is(ModBiomes.ESTUARY)
                && !level.getBiome(base).is(ModBiomes.BAMBOO_LOWLANDS)) continue;
            Block target=random.nextFloat()<0.44F?ModBlocks.CATTAILS:ModBlocks.REEDS;
            if(target instanceof ThreeBlockWetlandPlantBlock plant && plant.placeNaturally(level,base))placed++;
        }
        return placed>0;
    }
}
