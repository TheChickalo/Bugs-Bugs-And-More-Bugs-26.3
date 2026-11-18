package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.block.custom.VariableHeightPlantBlock;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Highland Karst meadow patches: silver grass dominates, bush clover and
 * bellflowers form loosely curved flower ribbons, and dwarf bamboo makes
 * infrequent, locally dense thickets. Also scatters a few vanilla flowers.
 *
 * Requires SILVER_GRASS, BUSH_CLOVER and DWARF_BAMBOO to use the
 * VariableHeightPlantBlock class with placeAtHeight(...).
 */
public record HighlandMeadowPatchFeature() implements Feature {
    public static final MapCodec<HighlandMeadowPatchFeature> CODEC =
            MapCodec.unit(new HighlandMeadowPatchFeature());

    @Override
    public MapCodec<HighlandMeadowPatchFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(
            WorldGenLevel level,
            ChunkGenerator generator,
            RandomSource random,
            BlockPos origin
    ) {
        // 0: silver grass (52%), 1: bush clover (24%),
        // 2: dwarf bamboo (9%), 3: mixed flowers (15%).
        int choice = random.nextInt(100);
        int patch = choice < 52 ? 0 : choice < 76 ? 1 : choice < 85 ? 2 : 3;

        int radiusX = 5 + random.nextInt(5); // 5-9 blocks
        int radiusZ = 3 + random.nextInt(5); // 3-7 blocks
        double angle = random.nextDouble() * Math.PI * 2.0;
        double ca = Math.cos(angle);
        double sa = Math.sin(angle);
        double phase = random.nextDouble() * Math.PI * 2.0;
        int attempts = 110 + random.nextInt(55);
        int successes = 0;

        for (int i = 0; i < attempts; i++) {
            int dx = random.nextInt(2 * radiusX + 1) - radiusX;
            int dz = random.nextInt(2 * radiusX + 1) - radiusX;

            // Rotate the local coordinates so patches have varied directions.
            double u = dx * ca + dz * sa;
            double v = -dx * sa + dz * ca;
            double norm2 = (u * u) / (radiusX * radiusX)
                    + (v * v) / (radiusZ * radiusZ);
            if (norm2 >= 1.0) continue;

            // Flower-heavy patches form soft, winding ribbons instead of blobs.
            if ((patch == 1 || patch == 3)
                    && Math.abs(v - Math.sin(u * 0.43 + phase) * 1.45) > 2.3) {
                continue;
            }

            // The center is lush and the edge fades gradually into open grass.
            if (random.nextFloat() > 0.73 - 0.34 * Math.sqrt(norm2)) {
                continue;
            }

            int x = origin.getX() + dx;
            int z = origin.getZ() + dz;
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos pos = new BlockPos(x, y, z);

            if (y <= level.getMinY() || y >= level.getMaxY() - 4) continue;
            if (!level.getBiome(pos).is(ModBiomes.HIGHLAND_KARST)
                    && !level.getBiome(pos).is(ModBiomes.STONE_FOREST)) continue;
            if (!net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(level.getBlockState(pos.below()), Blocks.GRASS_BLOCK)) continue;

            Block target = choosePlant(patch, random);
            if (target == null) continue;
            if (placePlant(level, pos, target, random)) successes++;
        }
        return successes > 0;
    }

    private static Block choosePlant(int patch, RandomSource random) {
        int roll = random.nextInt(100);
        return switch (patch) {
            case 0 -> roll < 75 ? ModBlocks.SILVER_GRASS
                    : roll < 83 ? ModBlocks.BELLFLOWERS
                    : roll < 90 ? Blocks.DANDELION
                    : roll < 97 ? Blocks.OXEYE_DAISY : null;
            case 1 -> roll < 69 ? ModBlocks.BUSH_CLOVER
                    : roll < 80 ? ModBlocks.SILVER_GRASS
                    : roll < 91 ? ModBlocks.BELLFLOWERS
                    : roll < 96 ? Blocks.AZURE_BLUET : Blocks.OXEYE_DAISY;
            case 2 -> roll < 85 ? ModBlocks.DWARF_BAMBOO
                    : roll < 95 ? ModBlocks.SILVER_GRASS : ModBlocks.BUSH_CLOVER;
            default -> roll < 49 ? ModBlocks.BELLFLOWERS
                    : roll < 61 ? ModBlocks.BUSH_CLOVER
                    : roll < 75 ? ModBlocks.SILVER_GRASS
                    : roll < 86 ? Blocks.OXEYE_DAISY
                    : roll < 95 ? Blocks.AZURE_BLUET : Blocks.DANDELION;
        };
    }

    private static boolean placePlant(
            WorldGenLevel level,
            BlockPos pos,
            Block block,
            RandomSource random
    ) {
        if (block instanceof VariableHeightPlantBlock plant) {
            int height = pickHeight(plant, random);
            for (int section = 0; section < height; section++) {
                if (!canReplace(level.getBlockState(pos.above(section)))) {
                    return false;
                }
            }
            // Also validates ground/survival before placing the whole column.
            return plant.placeAtHeight(level, pos, height);
        }

        if (!canReplace(level.getBlockState(pos))) return false;
        BlockState state = block.defaultBlockState();
        return state.canSurvive(level, pos) && level.setBlock(pos, state, 2);
    }

    private static int pickHeight(
            VariableHeightPlantBlock plant,
            RandomSource random
    ) {
        int max = plant.getMaxHeight();
        if (max == 2) return random.nextFloat() < 0.40F ? 1 : 2;
        if (max == 4) {
            int n = random.nextInt(100);
            return n < 15 ? 1 : n < 53 ? 2 : n < 87 ? 3 : 4;
        }
        return 1 + random.nextInt(max);
    }

    // We replace ordinary single-height vanilla groundcover, not existing
    // custom plants, full-height crops, tall grass pairs or world structures.
    private static boolean canReplace(BlockState state) {
        return state.isAir()
                || state.is(Blocks.SHORT_GRASS)
                || state.is(Blocks.FERN);
    }
}
