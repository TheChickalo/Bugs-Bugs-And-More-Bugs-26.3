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

/** Low, irregular, taiga-style rock piles, never freestanding stone columns. */
public record HinokiBoulderFeature() implements Feature {
    public static final MapCodec<HinokiBoulderFeature> CODEC =
            MapCodec.unit(new HinokiBoulderFeature());

    @Override
    public MapCodec<HinokiBoulderFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator,
                         RandomSource random, BlockPos origin) {
        int x = origin.getX(), z = origin.getZ();
        int top = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        BlockPos center = new BlockPos(x, top - 1, z);
        if (!validGround(level, center)) return false;

        // A two-to-four block-wide mound that broadens at ground level.
        // The top is always smaller than the base, unlike the old stone towers.
        int rx = 2 + random.nextInt(2);
        int rz = 2 + random.nextInt(2);
        double phase = random.nextDouble() * Math.PI * 2.0;
        int changed = 0;
        for (int layer = 0; layer < 3; layer++) {
            int radX = layer == 0 ? rx : layer == 1 ? rx - 1 : 1;
            int radZ = layer == 0 ? rz : layer == 1 ? rz - 1 : 1;
            if (layer == 2 && random.nextFloat() < 0.76F) break;
            for (int dx = -radX; dx <= radX; dx++) {
                for (int dz = -radZ; dz <= radZ; dz++) {
                    double noise = 0.12 * Math.sin(dx * 2.6 + dz * 1.7 + phase);
                    double elliptical = (double)(dx * dx) / (radX * radX)
                            + (double)(dz * dz) / (radZ * radZ);
                    if (elliptical > 1.05 + noise
                            || (elliptical > 0.70 && random.nextFloat() < 0.27F)) continue;

                    int px = x + dx, pz = z + dz;
                    int surface = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, px, pz);
                    if (Math.abs(surface - top) > 1) continue;
                    BlockPos ground = new BlockPos(px, surface - 1, pz);
                    if (layer == 0 && !validGround(level, ground)) continue;
                    if (!level.getBiome(ground).is(ModBiomes.MOSSY_HINOKI_GROVE)) continue;

                    BlockPos at = layer == 0 ? ground : ground.above(layer);
                    BlockState old = level.getBlockState(at);
                    if (layer > 0 && !old.isAir()) continue;
                    if (!level.getFluidState(at).isEmpty()) continue;
                    BlockState stone = pickRock(random);
                    if (level.setBlock(at, stone, 2)) changed++;
                }
            }
        }
        return changed > 0;
    }

    private static BlockState pickRock(RandomSource random) {
        float n = random.nextFloat();
        if (n < 0.58F) return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        if (n < 0.73F) return Blocks.COBBLESTONE.defaultBlockState();
        if (n < 0.89F) return ModBlocks.RHYOLITE.defaultBlockState();
        return ModBlocks.MOSSY_RHYOLITE.defaultBlockState();
    }

    private static boolean validGround(WorldGenLevel level, BlockPos ground) {
        if (ground.getY() <= level.getMinY() + 2
                || ground.getY() >= level.getMaxY() - 4
                || !level.getBiome(ground).is(ModBiomes.MOSSY_HINOKI_GROVE)
                || !level.getFluidState(ground).isEmpty()
                || !level.getFluidState(ground.above()).isEmpty()
                || !level.getBlockState(ground.above()).isAir()
                || level.getBlockState(ground.below()).isAir()) return false;
        BlockState b = level.getBlockState(ground);
        return b.is(Blocks.GRASS_BLOCK) || b.is(Blocks.DIRT)
                || b.is(Blocks.COARSE_DIRT) || b.is(Blocks.PODZOL)
                || b.is(ModBlocks.GRASSY_LOAM) || b.is(ModBlocks.LOAM)
                || b.is(ModBlocks.RHYOLITE) || b.is(ModBlocks.DACITE);
    }
}
