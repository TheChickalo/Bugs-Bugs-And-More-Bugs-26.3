package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/** Layered snow with a smoothly varying snowline, per 16x16 chunk. */
public record AlpineSnowFeature() implements Feature {
    public static final MapCodec<AlpineSnowFeature> CODEC=MapCodec.unit(new AlpineSnowFeature());
    @Override public MapCodec<AlpineSnowFeature> codec(){return CODEC;}

    @Override
    public boolean place(WorldGenLevel level,ChunkGenerator generator,
                         RandomSource random,BlockPos origin) {
        int firstX=Math.floorDiv(origin.getX(),16)*16;
        int firstZ=Math.floorDiv(origin.getZ(),16)*16;
        int changed=0;
        for(int x=firstX;x<firstX+16;x++)for(int z=firstZ;z<firstZ+16;z++) {
            int y=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z);
            if(y<90||y>=level.getMaxY()-3)continue;
            BlockPos ground=new BlockPos(x,y-1,z),top=ground.above();
            int base;
            if(level.getBiome(ground).is(ModBiomes.SNOWY_PEAK))base=117;
            else if(level.getBiome(ground).is(ModBiomes.VOLCANIC_PEAK))base=210;
            else if(level.getBiome(ground).is(ModBiomes.ASHEN_FOOTHILLS))base=186;
            else continue;

            // Macro and micro undulations make the snowline meander along
            // slopes rather than draw an unnaturally flat horizontal stripe.
            int line=base+(int)Math.round(
                    12*Math.sin(x*0.028+z*0.009)+
                    8*Math.cos(z*0.039-x*0.017)+
                    4*Math.sin(x*0.083+z*0.072));
            int depth=y-1-line;
            if(depth<0 || (depth<4 && random.nextFloat()<0.40F))continue;
            BlockState baseStone=level.getBlockState(ground);
            if(!topIsClean(level,top) || !snowSupport(baseStone))continue;

            // Snowy peaks build deeper caps; exposed volcanic shoulders have
            // patchier layers and a visible dacite/lapilli substrate.
            int layers=Math.min(8,Math.max(1,
                    1+depth/5+random.nextInt(3)));
            BlockState cover=Blocks.SNOW.defaultBlockState()
                    .setValue(SnowLayerBlock.LAYERS,layers);
            if(!cover.canSurvive(level,top))continue;
            if(depth>38 && random.nextInt(4)==0)cover=Blocks.SNOW_BLOCK.defaultBlockState();
            if(level.setBlock(top,cover,2))changed++;
        }
        return changed>0;
    }

    private static boolean topIsClean(WorldGenLevel level,BlockPos top) {
        return level.getBlockState(top).isAir()&&level.getFluidState(top).isEmpty();
    }

    private static boolean snowSupport(BlockState b) {
        return b.is(ModBlocks.DACITE)||b.is(ModBlocks.RHYOLITE)
                ||b.is(ModBlocks.LAPILLI)||b.is(Blocks.STONE)
                ||b.is(Blocks.SNOW_BLOCK)||b.is(Blocks.GRAVEL)
                ||b.is(Blocks.GRASS_BLOCK)||b.is(Blocks.DIRT)
                ||b.is(Blocks.BASALT)||b.is(ModBlocks.LIMESTONE);
    }
}
