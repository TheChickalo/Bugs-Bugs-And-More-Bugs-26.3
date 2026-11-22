package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.block.custom.VariableHeightPlantBlock;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeafLitterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

/**
 * A terrain identity for each of the fifteen *real* forest microbiomes.
 *
 * World-coordinate curves/landmarks have no chunk-local random origins; all
 * chunks paint only their own 16x16 area, so arcs and gullies do not end in
 * square edges at chunk borders. Nothing is painted into caves or non-soil
 * terrain and individual micro-biome boundaries are respected.
 */
public record ForestMicroTerrainFeature() implements Feature {
    public static final MapCodec<ForestMicroTerrainFeature> CODEC =
            MapCodec.unit(new ForestMicroTerrainFeature());

    @Override public MapCodec<ForestMicroTerrainFeature> codec() { return CODEC; }

    private record Bend(double cx, double cz, double radius, double cos, double sin) {}
    private static final int CELL = 44;

    private static int kind(Holder<Biome> biome) {
        if (biome.is(ModBiomes.VIOLET_CURTAINS)) return 0;
        if (biome.is(ModBiomes.TANGLED_ARBORS)) return 1;
        if (biome.is(ModBiomes.PETAL_GLADES)) return 2;
        if (biome.is(ModBiomes.OXBOW_BANKS)) return 3;
        if (biome.is(ModBiomes.SEDGE_POOLS)) return 4;
        if (biome.is(ModBiomes.WILLOW_HUMMOCKS)) return 5;
        if (biome.is(ModBiomes.ELDER_STANDS)) return 6;
        if (biome.is(ModBiomes.NURSE_LOG_HOLLOWS)) return 7;
        if (biome.is(ModBiomes.RAINWASHED_GULLIES)) return 8;
        if (biome.is(ModBiomes.AMBER_GROVES)) return 9;
        if (biome.is(ModBiomes.GOLDEN_MEADOWS)) return 10;
        if (biome.is(ModBiomes.BRAMBLE_TERRACES)) return 11;
        if (biome.is(ModBiomes.BLOSSOM_DRIFTS)) return 12;
        if (biome.is(ModBiomes.FEATHERGRASS_PRAIRIE)) return 13;
        if (biome.is(ModBiomes.SUNWASHED_RISES)) return 14;
        return -1;
    }

    private static long hash(int x,int z) {
        long n = ((long)x * 0x9E3779B97F4A7C15L) ^ ((long)z * 0xC2B2AE3D27D4EB4FL);
        n ^= n >>> 30;
        n *= 0xBF58476D1CE4E5B9L;
        n ^= n >>> 27;
        n *= 0x94D049BB133111EBL;
        return n ^ n >>> 31;
    }

    private static double patch(int x,int z) {
        return 0.50*Math.sin(x*0.11+Math.cos(z*0.041))
                +0.35*Math.cos(z*0.09-x*0.026)
                +0.15*Math.sin(x*0.19+z*0.06);
    }

    private static boolean soil(BlockState s) {
        return s.is(Blocks.GRASS_BLOCK) || s.is(Blocks.DIRT)
                || s.is(Blocks.COARSE_DIRT) || s.is(Blocks.PODZOL)
                || s.is(Blocks.MUD) || s.is(Blocks.CLAY)
                || s.is(Blocks.ROOTED_DIRT) || s.is(Blocks.MOSS_BLOCK)
                || s.is(ModBlocks.SILT) || s.is(ModBlocks.LOAM)
                || s.is(ModBlocks.GRASSY_LOAM) || s.is(ModBlocks.CLUBMOSS);
    }

    private static boolean put(WorldGenLevel level, BlockPos pos, Block block) {
        return level.setBlock(pos,block.defaultBlockState(),2);
    }

    private static boolean litter(WorldGenLevel level,BlockPos ground,Block plant,int layers) {
        BlockPos site=ground.above();
        if (!level.getBlockState(site).isAir()) return false;
        BlockState state=plant.defaultBlockState();
        if (plant instanceof LeafLitterBlock)
            state=state.setValue(BlockStateProperties.SEGMENT_AMOUNT,Math.max(1,Math.min(4,layers)));
        return state.canSurvive(level,site) && level.setBlock(site,state,2);
    }

    private static boolean grass(WorldGenLevel level,BlockPos ground,Block plant) {
        BlockPos site=ground.above();
        if(!level.getBlockState(site).isAir())return false;
        if(plant instanceof VariableHeightPlantBlock tall)
            return tall.placeAtHeight(level,site,1);
        BlockState state=plant.defaultBlockState();
        return state.canSurvive(level,site) && level.setBlock(site,state,2);
    }

    /** Carves a real one- to two-block-deep channel BELOW the old soil surface. */
    private static boolean waterCut(WorldGenLevel level,BlockPos top,int depth) {
        if(!soil(level.getBlockState(top)) || !level.getBlockState(top.above()).isAir())return false;
        BlockPos bed=top.below(depth+1);
        for(int n=0;n<=depth+1;n++) {
            BlockPos at=top.below(n);
            if(!soil(level.getBlockState(at)) && !level.getBlockState(at).is(Blocks.STONE))return false;
            if(!level.getFluidState(at).isEmpty())return false;
        }
        // The old top becomes air, water is below that surface, with mud/silt
        // below it. Never pour water above the surrounding ground.
        put(level,bed,ModBlocks.SILT);
        for(int n=1;n<=depth;n++)put(level,top.below(n),Blocks.WATER);
        return put(level,top,Blocks.AIR);
    }

    /** A horseshoe-shaped abandoned river loop, not a circular swamp puddle. */
    private static List<Bend> bends(int startX,int startZ) {
        List<Bend> result=new ArrayList<>();
        int cx=Math.floorDiv(startX,CELL),cz=Math.floorDiv(startZ,CELL);
        for(int i=-1;i<=1;i++)for(int j=-1;j<=1;j++) {
            int a=cx+i,b=cz+j;
            long seed=hash(a,b);
            if((seed&7)==0)continue;
            double centerX=a*CELL+22+((seed>>>8)&7)-3;
            double centerZ=b*CELL+22+((seed>>>12)&7)-3;
            double rotation=(seed&65535L)/65535.0*Math.PI*2;
            result.add(new Bend(centerX,centerZ,9+(seed>>>17&3),
                    Math.cos(rotation),Math.sin(rotation)));
        }
        return result;
    }

    private static double oxbowDistance(double x,double z,List<Bend> bends) {
        double best=Double.POSITIVE_INFINITY;
        // t=-2.77..2.77 leaves a narrow, dry neck between two disconnected ends.
        for(Bend b:bends) {
            if(Math.abs(x-b.cx)>22 || Math.abs(z-b.cz)>22)continue;
            for(int i=0;i<=32;i++) {
                double t=-2.77+5.54*i/32.0;
                double px=b.radius*Math.cos(t),pz=b.radius*Math.sin(t);
                double pxWorld=b.cx+px*b.cos-pz*b.sin;
                double pzWorld=b.cz+px*b.sin+pz*b.cos;
                double d=(x-pxWorld)*(x-pxWorld)+(z-pzWorld)*(z-pzWorld);
                if(d<best)best=d;
            }
        }
        return Math.sqrt(best);
    }

    private static double smoothDistance(int x,int z,int spacing,double amplitude) {
        int line=Math.floorDiv(z,spacing);
        double best=Double.POSITIVE_INFINITY;
        for(int k=-1;k<=1;k++) {
            int n=line+k;
            double center=n*spacing+spacing/2.0
                    +amplitude*Math.sin(x*0.061+n*1.67)
                    +2.1*Math.sin(x*0.127+n);
            best=Math.min(best,Math.abs(z-center));
        }
        return best;
    }

    private static boolean downedLog(WorldGenLevel level,BlockPos top,int x,int z) {
        int cellX=Math.floorDiv(x,19),cellZ=Math.floorDiv(z,19);
        long seed=hash(cellX,cellZ);
        int centerX=cellX*19+9,centerZ=cellZ*19+9;
        boolean east=(seed&1)==0;
        int dx=x-centerX,dz=z-centerZ;
        if(east ? (Math.abs(dz)>0 || Math.abs(dx)>3) :
                 (Math.abs(dx)>0 || Math.abs(dz)>3))return false;
        Block chosen=(seed&3)==0?ModBlocks.CEDAR_WOOD:ModBlocks.MOSSY_CEDAR_LOG;
        BlockState state=chosen.defaultBlockState().setValue(BlockStateProperties.AXIS,
                east?net.minecraft.core.Direction.Axis.X:net.minecraft.core.Direction.Axis.Z);
        if(!level.getBlockState(top.above()).isAir())return false;
        return level.setBlock(top.above(),state,2);
    }

    private static boolean erode(WorldGenLevel level,BlockPos top,int x,int z,double d) {
        // Only slope-cut channels: flat rainforest remains mossy and green.
        int dx=Math.abs(level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x+2,z)
                -level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x-2,z));
        int dz=Math.abs(level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z+2)
                -level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z-2));
        if(Math.max(dx,dz)<2)return false;
        if(d>3.8)return false;
        if(d>1.9) return put(level,top,(hash(x,z)&3)==0?Blocks.GRAVEL:Blocks.MUD);
        int depth=d<0.75?3:2;
        for(int n=0;n<=depth;n++){
            BlockPos at=top.below(n);
            if(!soil(level.getBlockState(at))&& !level.getBlockState(at).is(Blocks.STONE))return false;
            if(!level.getFluidState(at).isEmpty())return false;
        }
        put(level,top.below(depth),(hash(x,z)&1)==0?ModBlocks.RHYOLITE:Blocks.GRAVEL);
        for(int i=0;i<depth;i++)put(level,top.below(i),Blocks.AIR);
        return true;
    }

    @Override public boolean place(WorldGenLevel level, ChunkGenerator generator,
                                   RandomSource random, BlockPos origin) {
        int x0=origin.getX() & ~15,z0=origin.getZ() & ~15;
        List<Bend> curves=bends(x0,z0);
        int changes=0;
        for(int dx=0;dx<16;dx++)for(int dz=0;dz<16;dz++) {
            int x=x0+dx,z=z0+dz;
            int y=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z)-1;
            if(y<58||y>=level.getMaxY()-6)continue;
            BlockPos top=new BlockPos(x,y,z);
            int k=kind(level.getBiome(top));
            if(k<0)continue;
            BlockState state=level.getBlockState(top);
            if(!soil(state)||!level.getBlockState(top.above()).isAir()
                    ||!level.getFluidState(top).isEmpty())continue;
            long h=hash(x,z);
            double noise=patch(x,z);
            boolean changed=false;
            switch(k) {
                case 0 -> { // Violet Curtains: floor carpets of petals and violets.
                    if(noise> -0.10 && (h&3)!=0)
                        changed=litter(level,top,ModBlocks.FALLEN_WISTERIA_PETALS,2+(int)(h&3));
                    else if((h&15)==0)changed=grass(level,top,ModBlocks.WILD_VIOLET);
                }
                case 1 -> { // Tangled Arbors: exposed roots and horizontal vine-covered wood.
                    if(noise> -0.15)changed=put(level,top,Blocks.ROOTED_DIRT);
                    if((h&31)==0)changed|=downedLog(level,top,x,z);
                }
                case 2 -> { // Petal Glades: bright, dense low blooms and clear sight lines.
                    if(noise>0.12 && (h&1)==0)
                        changed=litter(level,top,ModBlocks.FALLEN_WISTERIA_PETALS,1+(int)(h&3));
                    else if((h&7)==0)changed=grass(level,top,ModBlocks.WILD_VIOLET);
                }
                case 3 -> { // Oxbow Banks: actual horseshoe-shaped, disconnected old channel.
                    double d=oxbowDistance(x+0.5,z+0.5,curves);
                    if(d<=2.1)changed=waterCut(level,top,d<0.9?2:1);
                    else if(d<4.2)changed=put(level,top,(h&3)==0?Blocks.MUD:ModBlocks.SILT);
                }
                case 4 -> { // Sedge Pools: many small wet hollows and sedge-rich margins.
                    double d=smoothDistance(x,z,26,4.0);
                    if(d<2.0 && noise> -0.30)changed=waterCut(level,top,1);
                    else if(d<4.0 && (h&3)==0)changed=put(level,top,Blocks.MUD);
                }
                case 5 -> { // Willow Hummocks: scattered low mossy islets above wet soil.
                    double d=smoothDistance(x,z,23,2.3);
                    if(d<1.55 && noise>0.0 && y<103)
                        changed=put(level,top.above(),Blocks.MOSS_BLOCK);
                    else if(noise>0.12 && (h&7)==0)
                        changed=put(level,top,Blocks.MOSS_BLOCK);
                }
                case 6 -> { // Elder Stands: uninterrupted carpets of moss and clubmoss.
                    if(noise> -0.30)changed=put(level,top,Blocks.MOSS_BLOCK);
                    else if((h&3)==0)changed=put(level,top,ModBlocks.CLUBMOSS);
                }
                case 7 -> { // Nurse-Log Hollows: visibly fallen, moss-encrusted cedar boles.
                    if(noise> -0.1)changed=put(level,top,(h&3)==0?Blocks.PODZOL:Blocks.MOSS_BLOCK);
                    if((h&3)==0)changed|=downedLog(level,top,x,z);
                }
                case 8 -> { // Rainwashed Gullies: true 2–3-deep erosion cuts on slopes.
                    double d=smoothDistance(x,z,39,6.0);
                    changed=erode(level,top,x,z,d);
                }
                case 9 -> { // Amber Groves: autumn leaf-litter carpets.
                    if(noise> -0.25 && (h&7)!=0)
                        changed=litter(level,top,ModBlocks.FALLEN_AUTUMN_LEAVES,2+(int)(h&3));
                    else if(noise>0.25)changed=put(level,top,Blocks.PODZOL);
                }
                case 10 -> { // Golden Meadows: carpets of tall silvergrass and flowers.
                    if(noise> -0.30 && (h&3)!=0)
                        changed=grass(level,top,(h&7)==0?ModBlocks.ANEMONE_FLOWERS:ModBlocks.SILVER_GRASS);
                    else if((h&15)==0)changed=put(level,top,Blocks.COARSE_DIRT);
                }
                case 11 -> { // Bramble Terraces: low stepped bands, bramble-rich edges.
                    double ridge=Math.sin(x*0.21+z*0.049);
                    if(ridge>0.74 && noise> -0.15)
                        changed=put(level,top.above(),Blocks.COARSE_DIRT);
                    else if(ridge>0.1 && noise>0.0)
                        changed=put(level,top,Blocks.COARSE_DIRT);
                    if((h&7)==0)changed|=grass(level,top,ModBlocks.BUSH);
                }
                case 12 -> { // Blossom Drifts: streaks of pink flower litter.
                    if(noise> -0.26 && (h&3)!=0)
                        changed=litter(level,top,ModBlocks.FALLEN_MIMOSA_PETALS,2+(int)(h&3));
                    else if((h&7)==0)changed=litter(level,top,ModBlocks.PINK_CLOVER,2);
                }
                case 13 -> { // Feathergrass Prairie: broad fields, fewer trees from sparse placer.
                    if(noise> -0.40 && (h&1)==0)
                        changed=grass(level,top,ModBlocks.FEATHERGRASS);
                    else if((h&7)==0)changed=litter(level,top,ModBlocks.PINK_CLOVER,1+(int)(h&3));
                }
                case 14 -> { // Sunwashed Rises: exposed low rocky crests, not columns.
                    double d=smoothDistance(x,z,31,3.0);
                    if(noise>0.05 && d<2.2){
                        changed=put(level,top,(h&3)==0?Blocks.COARSE_DIRT:ModBlocks.RHYOLITE);
                        if(d<0.8 && noise>0.30)
                            changed|=put(level,top.above(),ModBlocks.RHYOLITE);
                    } else if((h&7)==0)changed=grass(level,top,ModBlocks.FEATHERGRASS);
                }
                default -> {}
            }
            if(changed)changes++;
        }
        return changes>0;
    }
}
