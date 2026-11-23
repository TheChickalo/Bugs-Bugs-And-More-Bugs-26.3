
package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

public record GlowshroomTreeBaseFeature(
        Block mushroom,
        Block mapleLog
) implements Feature {

    public static final MapCodec<GlowshroomTreeBaseFeature> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BuiltInRegistries.BLOCK.byNameCodec()
                            .fieldOf("mushroom")
                            .forGetter(GlowshroomTreeBaseFeature::mushroom),
                    BuiltInRegistries.BLOCK.byNameCodec()
                            .fieldOf("maple_log")
                            .forGetter(GlowshroomTreeBaseFeature::mapleLog)
            ).apply(instance, GlowshroomTreeBaseFeature::new));

    @Override
    public MapCodec<GlowshroomTreeBaseFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level,
                         ChunkGenerator generator,
                         RandomSource random,
                         BlockPos origin) {

        BlockState mushroomState = mushroom.defaultBlockState();

        // Find the surface near this generation attempt.
        BlockPos ground = findGround(
                level, origin.getX(), origin.getZ()
        );

        if (ground == null) {
            return false;
        }

        // Look for a maple trunk within two blocks.
        BlockPos trunk = null;

        search:
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {

                BlockPos nearby = ground.offset(dx, 0, dz);

                // Allow for slopes and uneven terrain.
                for (int dy = -2; dy <= 3; dy++) {
                    BlockPos check = nearby.above(dy);

                    if (level.getBlockState(check).is(mapleLog)) {
                        trunk = check;
                        break search;
                    }
                }
            }
        }

        if (trunk == null) {
            return false;
        }

        // Try to place between one and four mushrooms.
        int target = 1 + random.nextInt(4);
        int placed = 0;

        for (int attempt = 0;
             attempt < 16 && placed < target;
             attempt++) {

            int dx = random.nextInt(5) - 2;
            int dz = random.nextInt(5) - 2;

            // Keep mushrooms close to the trunk.
            int distanceSquared = dx * dx + dz * dz;

            if (distanceSquared == 0 || distanceSquared > 5) {
                continue;
            }

            int x = trunk.getX() + dx;
            int z = trunk.getZ() + dz;

            BlockPos pos = findGround(level, x, z);

            if (pos == null) {
                continue;
            }

            // Avoid existing mushrooms and other solid blocks.
            BlockState current = level.getBlockState(pos);

            if (current.is(mushroom) || !current.canBeReplaced()) {
                continue;
            }

            if (!mushroomState.canSurvive(level, pos)) {
                continue;
            }

            if (level.setBlock(pos, mushroomState, 2)) {
                placed++;
            }
        }

        return placed > 0;
    }

    private BlockPos findGround(
            WorldGenLevel level, int x, int z) {

        int topY = level.getHeight(
                Heightmap.Types.WORLD_SURFACE, x, z
        );

        for (int y = topY;
             y > level.getMinY();
             y--) {

            BlockPos groundPos = new BlockPos(x, y, z);
            BlockState ground = level.getBlockState(groundPos);

            // Pass through trees, air, and small vegetation.
            if (ground.is(BlockTags.LEAVES)
                    || ground.is(BlockTags.LOGS)
                    || ground.is(mapleLog)
                    || ground.is(Blocks.BAMBOO)
                    || ground.is(Blocks.BAMBOO_SAPLING)
                    || ground.isAir()
                    || ground.canBeReplaced()) {
                continue;
            }

            // The first solid terrain must be suitable soil.
            boolean suitable =
                    net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(ground, Blocks.GRASS_BLOCK)
                            || net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(ground, Blocks.DIRT)
                            || (ground.is(Blocks.COARSE_DIRT) || ground.is(ModBlocks.COARSE_LOAM))
                            || ground.is(Blocks.PODZOL)
                            || (ground.is(Blocks.ROOTED_DIRT) || ground.is(ModBlocks.ROOTED_LOAM))
                            || ground.is(Blocks.MYCELIUM)
                            || ground.is(Blocks.MOSS_BLOCK);

            if (!suitable) {
                return null;
            }

            BlockPos pos = groundPos.above();

            if (!level.getBlockState(pos).canBeReplaced()
                    || !level.getFluidState(pos).isEmpty()) {
                return null;
            }

            return pos;
        }

        return null;
    }
}
