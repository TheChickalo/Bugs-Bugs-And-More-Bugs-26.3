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

/** One-block-deep isolated pools on genuinely flat Bamboo Lowlands ground. */
public record BambooPoolFeature() implements Feature {
    public static final MapCodec<BambooPoolFeature> CODEC = MapCodec.unit(new BambooPoolFeature());
    @Override public MapCodec<BambooPoolFeature> codec() { return CODEC; }
    @Override public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        int cx = origin.getX(), cz = origin.getZ();
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,cx,cz)-1;
        if (y <= level.getMinY()+2 || y >= level.getMaxY()-4) return false;
        int radius = 2 + random.nextInt(2);
        // Validate the whole basin before placing any water; prevents giant floods.
        for (int dx=-radius-1;dx<=radius+1;dx++) for (int dz=-radius-1;dz<=radius+1;dz++) {
            if (dx*dx+dz*dz>(radius+1)*(radius+1)) continue;
            BlockPos p = new BlockPos(cx+dx,y,cz+dz);
            if (!ModBiomeFamilies.bamboo(level.getBiome(p))) return false;
            int gy=level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,cx+dx,cz+dz)-1;
            if (gy!=y) return false;
            BlockState state=level.getBlockState(p);
            if (!(state.is(ModBlocks.GRASSY_LOAM)||state.is(ModBlocks.LOAM)||state.is(Blocks.PODZOL)||state.is(Blocks.COARSE_DIRT))) return false;
            if (!level.getFluidState(p.above()).isEmpty()) return false;
        }
        boolean changed=false;
        for (int dx=-radius;dx<=radius;dx++) for (int dz=-radius;dz<=radius;dz++) {
            int rr=dx*dx+dz*dz;
            BlockPos p=new BlockPos(cx+dx,y,cz+dz);
            if (rr<radius*radius && rr<=4+random.nextInt(Math.max(1,radius*radius))) {
                level.setBlock(p,Blocks.WATER.defaultBlockState(),2);
                if(random.nextFloat()<0.55f) level.setBlock(p.below(),Blocks.MUD.defaultBlockState(),2);
                changed=true;
            } else if (rr<=radius*radius && random.nextFloat()<0.34f) {
                level.setBlock(p,random.nextBoolean()?Blocks.PODZOL.defaultBlockState():Blocks.COARSE_DIRT.defaultBlockState(),2);
            }
        }
        return changed;
    }
}
