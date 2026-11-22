package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.block.custom.VariableHeightPlantBlock;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomeFamilies;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeafLitterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.Fluids;

/**
 * Ecologically bounded Elsewhere vegetation shared among new and existing biomes.
 * Vegetation is placed on actual soil beneath the canopy, not on tree leaves.
 */
public record ElsewhereFloraFeature() implements Feature {
    public static final MapCodec<ElsewhereFloraFeature> CODEC=
            MapCodec.unit(new ElsewhereFloraFeature());
    @Override public MapCodec<ElsewhereFloraFeature> codec(){return CODEC;}

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator,
                         RandomSource random, BlockPos origin) {
        int changed=0;
        for(int attempt=0;attempt<16;attempt++){
            int x=origin.getX()+random.nextInt(13)-6;
            int z=origin.getZ()+random.nextInt(13)-6;
            int surfaceY=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z);
            BlockPos pos=new BlockPos(x,surfaceY,z);

            // Follow tree leaves down to ground or the top of the water surface.
            for(int step=0;step<48&&pos.getY()>level.getMinY()+1;step++){
                if(level.getBlockState(pos).isAir()
                        && (naturalSoil(level.getBlockState(pos.below()))
                            || level.getFluidState(pos.below()).is(Fluids.WATER)))break;
                pos=pos.below();
            }
            int y=pos.getY();
            if(y<level.getMinY()+2||y>=level.getMaxY()-3)continue;
            var biome=level.getBiome(pos);
            boolean ginkgo=ModBiomeFamilies.ginkgo(biome);
            boolean bamboo=ModBiomeFamilies.bamboo(biome);
            boolean peak=ModBiomeFamilies.volcanic(biome);
            boolean plains=biome.is(ModBiomes.GRASSY_PLAINS);
            boolean limestone=ModBiomeFamilies.limestone(biome);
            boolean wet=ModBiomeFamilies.wetland(biome);
            boolean cedar=ModBiomeFamilies.cedar(biome);
            boolean hinoki=biome.is(ModBiomes.HINOKI_HIGHLANDS)
                    || biome.is(ModBiomes.MOSSY_HINOKI_GROVE);
            boolean persimmon=ModBiomeFamilies.persimmon(biome);
            boolean mimosa=ModBiomeFamilies.mimosa(biome);
            boolean maple=biome.is(ModBiomes.MAPLE_FOREST);
            boolean meadow=biome.is(ModBiomes.HIGHLAND_WILDFLOWER_MEADOW);
            boolean glow=biome.is(ModBiomes.GLOWSHROOM_GROVE);
            // No changes to Wisteria appearance or its native tree/decorator.
            if(!ginkgo&&!bamboo&&!peak&&!plains&&!wet&&!limestone
                    &&!cedar&&!hinoki&&!persimmon&&!mimosa&&!maple&&!meadow&&!glow)continue;
            if(peak&&y>235)continue;

            // Floating plants are a layer above water; never replace source water.
            if(level.getFluidState(pos.below()).is(Fluids.WATER)
                    && level.getFluidState(pos.below()).isSource()){
                if(!level.getBlockState(pos).isAir()
                        || !(wet||bamboo||limestone||maple)
                        || random.nextInt(wet?3:7)!=0)continue;
                Block pad=random.nextFloat()<0.65F?ModBlocks.DUCKWEED
                        :random.nextFloat()<0.33F?ModBlocks.LOTUS_FLOWER:ModBlocks.LOTUS;
                BlockState state=pad.defaultBlockState();
                if(pad==ModBlocks.DUCKWEED)
                    state=state.setValue(BlockStateProperties.SEGMENT_AMOUNT,1+random.nextInt(4));
                if(state.canSurvive(level,pos) && level.setBlock(pos,state,2))changed++;
                continue;
            }
            if(!level.getBlockState(pos).isAir()
                    || !level.getFluidState(pos).isEmpty()
                    || !naturalSoil(level.getBlockState(pos.below())))continue;
            if(limestone && y<=84 && random.nextInt(4)==0 && nearWater(level,pos)){
                if(ModBlocks.DWARF_BAMBOO.placeAtHeight(level,pos,1+random.nextInt(3)))changed++;
                continue;
            }
            int roll=random.nextInt(12);
            Block plant;
            if(peak)plant=ModBlocks.ALPINE_GRASS;
            else if(cedar||hinoki||glow)
                plant=roll<3?ModBlocks.CLUBMOSS
                        :roll<6?ModBlocks.LIVERWORT
                        :roll<8?Blocks.MOSS_CARPET
                        :roll<10?Blocks.FERN:ModBlocks.ANEMONE_FLOWERS;
            else if(persimmon||ginkgo||maple)
                plant=roll<4?(ginkgo?ModBlocks.GINKGO_FALLEN_LEAVES:ModBlocks.FALLEN_AUTUMN_LEAVES)
                        :roll<6?ModBlocks.ANEMONE_FLOWERS
                        :roll<8?ModBlocks.SILVER_GRASS
                        :roll==8?ModBlocks.BUSH:Blocks.FERN;
            else if(mimosa)
                plant=roll<5?ModBlocks.FEATHERGRASS
                        :roll<8?ModBlocks.PINK_CLOVER
                        :roll<10?ModBlocks.FALLEN_MIMOSA_PETALS:ModBlocks.BUSH;
            else if(wet)
                plant=roll<4?ModBlocks.MARSH_MARIGOLD
                        :roll<7?ModBlocks.IRIS_FLOWER
                        :roll<9?Blocks.FERN:ModBlocks.HAKONE_GRASS;
            else if(meadow||plains)
                plant=roll<3?ModBlocks.PINK_CLOVER
                        :roll<5?ModBlocks.ANEMONE_FLOWERS
                        :roll<7?ModBlocks.FEATHERGRASS:ModBlocks.HAKONE_GRASS;
            else if(limestone)
                plant=roll<4?ModBlocks.ANEMONE_FLOWERS
                        :roll<6?ModBlocks.FEATHERGRASS
                        :roll<8?ModBlocks.IRIS_FLOWER:ModBlocks.HAKONE_GRASS;
            else if(bamboo)
                plant=roll<4?ModBlocks.HAKONE_GRASS
                        :roll<6?ModBlocks.ANEMONE_FLOWERS
                        :roll<8?ModBlocks.IRIS_FLOWER:Blocks.FERN;
            else plant=ModBlocks.HAKONE_GRASS;

            if(plant==ModBlocks.CLUBMOSS){
                BlockPos below=pos.below();
                if(!level.getBlockState(below).is(ModBlocks.CLUBMOSS)
                        && level.setBlock(below,ModBlocks.CLUBMOSS.defaultBlockState(),2))changed++;
                if(level.getBlockState(below).is(ModBlocks.CLUBMOSS)
                        && random.nextInt(4)!=0 && level.getBlockState(pos).isAir()){
                    BlockState top=ModBlocks.CLUBMOSS_LOWLYING.defaultBlockState();
                    if(top.canSurvive(level,pos)&&level.setBlock(pos,top,2))changed++;
                }
                continue;
            }
            if(plant==ModBlocks.SILVER_GRASS){
                if(ModBlocks.SILVER_GRASS.placeAtHeight(level,pos,1+random.nextInt(2)))changed++;
                continue;
            }
            BlockState state=plant.defaultBlockState();
            if(plant instanceof LeafLitterBlock)
                state=state.setValue(BlockStateProperties.SEGMENT_AMOUNT,1+random.nextInt(4));
            if(state.canSurvive(level,pos)&&level.setBlock(pos,state,2))changed++;
        }
        return changed>0;
    }

    private static boolean naturalSoil(BlockState state){
        return state.is(Blocks.GRASS_BLOCK)||state.is(Blocks.DIRT)
                ||state.is(Blocks.MUD)||state.is(Blocks.CLAY)
                ||state.is(Blocks.COARSE_DIRT)||state.is(Blocks.PODZOL)
                ||state.is(Blocks.ROOTED_DIRT)||state.is(Blocks.MOSS_BLOCK)
                ||state.is(ModBlocks.SILT)||state.is(ModBlocks.LOAM)
                ||state.is(ModBlocks.GRASSY_LOAM)||state.is(ModBlocks.CLUBMOSS);
    }

    private static boolean nearWater(WorldGenLevel level,BlockPos pos){
        for(int x=-2;x<=2;x++)for(int z=-2;z<=2;z++){
            BlockPos p=pos.offset(x,-1,z);
            if(level.getFluidState(p).is(Fluids.WATER)
                    ||level.getFluidState(p.above()).is(Fluids.WATER))return true;
        }
        return false;
    }
}
