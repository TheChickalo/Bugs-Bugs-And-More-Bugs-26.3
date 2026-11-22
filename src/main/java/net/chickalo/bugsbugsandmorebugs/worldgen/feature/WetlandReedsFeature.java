package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.block.custom.ThreeBlockWetlandPlantBlock;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomeFamilies;
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
        boolean willow=ModBiomeFamilies.willow(level.getBiome(origin));
        // Reeds/cattails spread in clumps along the more extensive shallow banks.
        for(int attempt=0;attempt<(willow?14:5);attempt++){
            int x=origin.getX()+random.nextInt(11)-5;
            int z=origin.getZ()+random.nextInt(11)-5;
            int y=level.getHeight(Heightmap.Types.WORLD_SURFACE,x,z)-1;
            BlockPos base=new BlockPos(x,y,z);
            if (!ModBiomeFamilies.wetlandReeds(level.getBiome(base))) continue;
            Block target=willow && random.nextInt(5)==0?ModBlocks.WATER_SEDGE:
                    random.nextFloat()<0.44F?ModBlocks.CATTAILS:ModBlocks.REEDS;
            if(target instanceof ThreeBlockWetlandPlantBlock plant && plant.placeNaturally(level,base))placed++;
        }
        return placed>0;
    }
}
