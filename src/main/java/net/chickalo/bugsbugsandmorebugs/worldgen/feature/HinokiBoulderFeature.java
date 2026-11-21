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

/**
 * A small taiga-style boulder plus a few separate moss accents.
 * Does not blanket the terrain with mossy stone and never builds on water.
 */
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
        int x = origin.getX();
        int z = origin.getZ();
        int surface = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        if (!validGround(level, new BlockPos(x, surface - 1, z))) return false;

        boolean changed = false;
        int radius = random.nextFloat() < 0.22F ? 2 : 1;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > radius + (random.nextDouble() - 0.5) * 0.25) continue;
                if (dist > 0.8 && random.nextFloat() < 0.20F) continue;

                int bx = x + dx, bz = z + dz;
                int top = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, bx, bz);
                if (Math.abs(top - surface) > 2) continue;
                BlockPos ground = new BlockPos(bx, top - 1, bz);
                if (!validGround(level, ground)) continue;

                // Replace the surface block and, on the core, add one or two
                // rough upper blocks. All upper positions must still be empty.
                BlockState rock = random.nextFloat() < 0.11F
                        ? ModBlocks.MOSSY_RHYOLITE.defaultBlockState()
                        : random.nextFloat() < 0.38F
                        ? ModBlocks.SERPENTINITE.defaultBlockState()
                        : ModBlocks.RHYOLITE.defaultBlockState();
                if (level.setBlock(ground, rock, 2)) changed = true;

                int height = dist < 0.9 ? (radius == 2 ? 2 : 1) : 0;
                for (int dy = 1; dy <= height; dy++) {
                    BlockPos above = ground.above(dy);
                    if (!level.getBlockState(above).isAir()
                            || !level.getFluidState(above).isEmpty()) break;
                    level.setBlock(above, rock, 2);
                }
            }
        }

        // Sparse moss and short carpets are punctuation, not the ground palette.
        for (int i = 0; i < 6; i++) {
            int bx = x + random.nextInt(11) - 5;
            int bz = z + random.nextInt(11) - 5;
            int top = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, bx, bz);
            if (Math.abs(top - surface) > 2) continue;
            BlockPos soil = new BlockPos(bx, top - 1, bz);
            if (!validGround(level, soil)) continue;
            if (random.nextFloat() < 0.23F) {
                // Only common soil is converted; do not repaint boulders or geology.
                BlockState existing = level.getBlockState(soil);
                if (existing.is(Blocks.GRASS_BLOCK) || existing.is(Blocks.DIRT)
                        || existing.is(Blocks.PODZOL)) {
                    changed |= level.setBlock(soil, Blocks.MOSS_BLOCK.defaultBlockState(), 2);
                }
            } else if (random.nextFloat() < 0.33F) {
                changed |= level.setBlock(soil.above(), Blocks.MOSS_CARPET.defaultBlockState(), 2);
            }
        }
        return changed;
    }

    private static boolean validGround(WorldGenLevel level, BlockPos ground) {
        if (ground.getY() <= level.getMinY() + 2
                || ground.getY() >= level.getMaxY() - 3
                || !level.getBiome(ground).is(ModBiomes.MOSSY_HINOKI_GROVE)
                || !level.getFluidState(ground).isEmpty()
                || !level.getFluidState(ground.above()).isEmpty()
                || !level.getBlockState(ground.above()).isAir()) return false;

        BlockState state = level.getBlockState(ground);
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT) || state.is(Blocks.PODZOL)
                || state.is(ModBlocks.GRASSY_LOAM) || state.is(ModBlocks.LOAM)
                || state.is(ModBlocks.RHYOLITE) || state.is(ModBlocks.DACITE)
                || state.is(ModBlocks.SERPENTINITE);
    }
}
