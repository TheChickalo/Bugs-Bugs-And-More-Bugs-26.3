package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomeFamilies;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Occasional small, irregular magma exposures on bare Volcanic Peak rock.
 * This replaces only the top rock blocks; it does not carve the terrain,
 * introduce lava, or overwrite snow, water, vegetation, or structures.
 */
public record VolcanicMagmaPatchFeature() implements Feature {
    public static final MapCodec<VolcanicMagmaPatchFeature> CODEC =
            MapCodec.unit(new VolcanicMagmaPatchFeature());

    @Override
    public MapCodec<VolcanicMagmaPatchFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator,
                         RandomSource random, BlockPos origin) {
        int centerY = surfaceY(level, origin.getX(), origin.getZ());
        if (centerY <= 60 || centerY >= level.getMaxY() - 1) return false;

        BlockPos center = new BlockPos(origin.getX(), centerY, origin.getZ());
        if (!ModBiomeFamilies.volcanic(level.getBiome(center))
                || !isExposedVolcanicRock(level, center)) return false;

        int radius = 1 + random.nextInt(3); // one compact 3-7 block-wide cluster
        int changed = 0;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double distance = Math.sqrt(dx * dx + dz * dz);
                if (distance > radius + 0.2 * (random.nextDouble() - 0.5)) continue;
                if (random.nextFloat() > (distance < 1 ? 1.0F : 0.79F)) continue;

                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;
                int y = surfaceY(level, x, z);
                if (Math.abs(y - centerY) > 2 || y <= 60
                        || y >= level.getMaxY() - 1) continue;

                BlockPos ground = new BlockPos(x, y, z);
                if (!ModBiomeFamilies.volcanic(level.getBiome(ground))
                        || !isExposedVolcanicRock(level, ground)) continue;
                if (level.setBlock(ground, Blocks.MAGMA_BLOCK.defaultBlockState(), 2)) changed++;
            }
        }
        return changed > 0;
    }

    private static int surfaceY(WorldGenLevel level, int x, int z) {
        return level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1;
    }

    private static boolean isExposedVolcanicRock(WorldGenLevel level, BlockPos ground) {
        if (!level.getFluidState(ground).isEmpty()
                || !level.getFluidState(ground.above()).isEmpty()
                || !level.getBlockState(ground.above()).isAir()) return false;

        BlockState state = level.getBlockState(ground);
        return state.is(ModBlocks.RHYOLITE)
                || state.is(ModBlocks.DACITE)
                || state.is(ModBlocks.LAPILLI);
    }
}
