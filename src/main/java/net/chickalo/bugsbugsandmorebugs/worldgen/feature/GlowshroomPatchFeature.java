
package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

public record GlowshroomPatchFeature(Block mushroom)
        implements Feature {

    public static final MapCodec<GlowshroomPatchFeature> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BuiltInRegistries.BLOCK.byNameCodec()
                            .fieldOf("mushroom")
                            .forGetter(GlowshroomPatchFeature::mushroom)
            ).apply(instance, GlowshroomPatchFeature::new));

    @Override
    public MapCodec<GlowshroomPatchFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level,
                         ChunkGenerator generator,
                         RandomSource random,
                         BlockPos origin) {

        BlockState mushroomState = mushroom.defaultBlockState();


        int surfaceY = level.getHeight(
                Heightmap.Types.WORLD_SURFACE,
                origin.getX(),
                origin.getZ()
        );

        BlockPos surfacePos = new BlockPos(
                origin.getX(), surfaceY, origin.getZ()
        );

        BlockPos groundPos = surfacePos.below();



        double radius = 2.5 + random.nextDouble() * 2.0;
        int attempts = 24 + random.nextInt(9);
        int placed = 0;


        for (int i = 0; i < attempts; i++) {

            // Position around the circumference.
            double angle = 2 * Math.PI * i / attempts;
            angle += (random.nextDouble() - 0.5) * 0.25;

            double distance = radius
                    + (random.nextDouble() - 0.5) * 0.8;

            int x = origin.getX()
                    + (int) Math.round(Math.cos(angle) * distance);

            int z = origin.getZ()
                    + (int) Math.round(Math.sin(angle) * distance);

            // Occasional gaps in the ring.
            if (random.nextFloat() < 0.15F) {
                continue;
            }

            BlockPos pos = findGround(level, x, z);

            if (pos == null) {
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



    private static BlockPos findGround(
            WorldGenLevel level, int x, int z) {

        int topY = level.getHeight(
                Heightmap.Types.WORLD_SURFACE,
                x, z
        );

        for (int y = topY; y >= level.getMinY() + 1; y--) {

            BlockPos groundPos = new BlockPos(x, y, z);
            BlockState ground = level.getBlockState(groundPos);

            // Ignore tree canopy, trunks, and bamboo while
            // searching for the actual terrain surface.
            if (ground.is(BlockTags.LEAVES)
                    || ground.is(BlockTags.LOGS)
                    || ground.is(Blocks.BAMBOO)
                    || ground.is(Blocks.BAMBOO_SAPLING)) {
                continue;
            }


            // Ignore empty air and small vegetation.
            if (ground.isAir() || ground.canBeReplaced()) {
                continue;
            }

            // We've reached the first solid terrain or obstacle.
            // Never search below it into a cave.


            boolean suitableSoil =
                    ground.is(Blocks.GRASS_BLOCK)
                            || ground.is(Blocks.DIRT)
                            || ground.is(Blocks.COARSE_DIRT)
                            || ground.is(Blocks.PODZOL)
                            || ground.is(Blocks.ROOTED_DIRT)
                            || ground.is(Blocks.MYCELIUM)
                            || ground.is(Blocks.MOSS_BLOCK);

            if (!suitableSoil) {
                return null;
            }

            BlockPos mushroomPos = groundPos.above();

            // Only place in replaceable, non-waterlogged space.
            if (!level.getBlockState(mushroomPos).canBeReplaced()
                    || !level.getFluidState(mushroomPos).isEmpty()) {
                return null;
            }

            return mushroomPos;
        }



        return null;
    }

}
