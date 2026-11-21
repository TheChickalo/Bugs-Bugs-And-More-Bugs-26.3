package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/** Scattered moss patches independent of boulders, so the forest floor stays green. */
public record HinokiMossPatchFeature() implements Feature {
    public static final MapCodec<HinokiMossPatchFeature> CODEC =
            MapCodec.unit(new HinokiMossPatchFeature());

    @Override public MapCodec<HinokiMossPatchFeature> codec() { return CODEC; }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator,
                         RandomSource random, BlockPos origin) {
        int changed = 0;
        int size = 1 + random.nextInt(3);
        for (int i = 0; i < 17; i++) {
            int x = origin.getX() + random.nextInt(9) - 4;
            int z = origin.getZ() + random.nextInt(9) - 4;
            if (Math.abs(x-origin.getX())+Math.abs(z-origin.getZ()) > 6 + size) continue;
            int top = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
            BlockPos ground = new BlockPos(x, top-1, z);
            if (!level.getBiome(ground).is(ModBiomes.MOSSY_HINOKI_GROVE)
                    || !level.getFluidState(ground).isEmpty()
                    || !level.getBlockState(ground.above()).isAir()) continue;
            BlockState soil = level.getBlockState(ground);
            if (soil.is(Blocks.GRASS_BLOCK) || soil.is(Blocks.DIRT)
                    || soil.is(Blocks.PODZOL) || soil.is(ModBlocks.LOAM)
                    || soil.is(ModBlocks.GRASSY_LOAM)) {
                if (random.nextFloat() < 0.18F) {
                    if (level.setBlock(ground, Blocks.MOSS_BLOCK.defaultBlockState(), 2)) changed++;
                } else if (random.nextFloat() < 0.53F) {
                    BlockState carpet = Blocks.MOSS_CARPET.defaultBlockState();
                    if (carpet.canSurvive(level, ground.above())
                            && level.setBlock(ground.above(), carpet, 2)) changed++;
                }
            }
        }
        return changed > 0;
    }
}
