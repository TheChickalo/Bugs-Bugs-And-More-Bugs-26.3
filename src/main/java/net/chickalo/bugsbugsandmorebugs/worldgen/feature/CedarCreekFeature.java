package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.util.RandomSource;

/**
 * Shallow meandering Cedar Rainforest creeks, built from continuous global
 * coordinates so adjoining chunks agree on the stream corridor.
 *
 * Carves natural 1–2-deep creek beds into existing surface soil, with low,
 * blended silt/mossy-stone banks; skips underground voids and steep surfaces.
 * Water comes from normal source blocks, so downhill sections can flow using
 * vanilla water physics, without custom flowing-water blocks.
 */
public record CedarCreekFeature() implements Feature {
    public static final MapCodec<CedarCreekFeature> CODEC =
            MapCodec.unit(new CedarCreekFeature());

    @Override public MapCodec<CedarCreekFeature> codec() { return CODEC; }

    @Override public boolean place(WorldGenLevel level, ChunkGenerator generator,
                                   RandomSource random, BlockPos origin) {
        boolean changed = false;
        for (int localX = 0; localX < 16; localX++)
            for (int localZ = 0; localZ < 16; localZ++) {
                int x = origin.getX() + localX;
                int z = origin.getZ() + localZ;
                double wanderingZ = 11.0 * Math.sin(x * 0.022)
                        + 4.8 * Math.sin(x * 0.059 + 1.7);
                double a = (z - wanderingZ) / 108.0;
                double branchIndex = Math.rint(a);
                double deviation = (a - branchIndex) * 108.0;
                boolean broad = Math.floorMod((int) branchIndex, 3) == 0;
                double radius = (broad ? 3.6 : 2.2)
                        + 0.45 * Math.sin(x * 0.037 + branchIndex * 2.0);
                double abs = Math.abs(deviation);
                if (abs > radius + 1.8) continue;

                int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1;
                if (y < 64 || y > 185) continue;
                BlockPos ground = new BlockPos(x, y, z);
                if (!level.getBiome(ground).is(ModBiomes.ANCIENT_CEDAR_RAINFOREST))
                    continue;
                BlockState top = level.getBlockState(ground);
                if (!surfaceSoil(top) || !level.getFluidState(ground).isEmpty())
                    continue;
                if (!level.getBlockState(ground.above()).isAir()) continue;
                // Do not puncture thin rock bridges or large caverns.
                if (!level.getBlockState(ground.below(3)).isSolid()
                        || !level.getFluidState(ground.below(3)).isEmpty()) continue;

                // Banks are level with the forest floor: never a mound.
                if (abs >= radius) {
                    Block bank = random.nextInt(5) == 0 ? ModBlocks.MOSSY_RHYOLITE
                            : random.nextInt(3) == 0 ? Blocks.MOSS_BLOCK : ModBlocks.SILT;
                    changed |= level.setBlock(ground, bank.defaultBlockState(), 2);
                    continue;
                }
                int depth = abs < radius * 0.44 && random.nextInt(3) == 0 ? 2 : 1;
                BlockPos bed = ground.below(depth);
                if (!surfaceSoil(level.getBlockState(bed))
                        && !level.getBlockState(bed).is(ModBlocks.RHYOLITE)
                        && !level.getBlockState(bed).is(Blocks.STONE)) continue;
                changed |= level.setBlock(bed,
                        random.nextInt(4) == 0 ? Blocks.GRAVEL.defaultBlockState()
                                : ModBlocks.SILT.defaultBlockState(), 2);
                // Sources settle and connect horizontally, while falling steps
                // between unequal nearby land heights use vanilla water updates.
                for (int dy=0; dy<depth; dy++) {
                    BlockPos cell = ground.below(dy);
                    changed |= level.setBlock(cell, Blocks.WATER.defaultBlockState(), 2);
                }
            }
        return changed;
    }

    private static boolean surfaceSoil(BlockState state) {
        return state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.COARSE_DIRT) || state.is(Blocks.PODZOL)
                || state.is(Blocks.ROOTED_DIRT) || state.is(Blocks.MUD)
                || state.is(Blocks.MOSS_BLOCK) || state.is(Blocks.CLAY)
                || state.is(ModBlocks.LOAM) || state.is(ModBlocks.GRASSY_LOAM)
                || state.is(ModBlocks.CLUBMOSS) || state.is(ModBlocks.SILT);
    }
}
