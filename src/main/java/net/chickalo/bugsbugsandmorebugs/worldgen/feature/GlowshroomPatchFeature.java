
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

        int placed = 0;

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {

                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;

                int y = level.getHeight(
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        x, z
                );

                BlockPos pos = new BlockPos(x, y, z);

                if (!level.isEmptyBlock(pos)) {
                    continue;
                }

                BlockState ground = level.getBlockState(pos.below());

                if (!ground.is(BlockTags.DIRT)
                        && !ground.is(Blocks.MOSS_BLOCK)) {
                    continue;
                }

                if (!mushroomState.canSurvive(level, pos)) {
                    System.out.println(
                            "Glowshroom rejected by canSurvive at " + pos
                    );
                    continue;
                }

                level.setBlock(pos, mushroomState, 2);
                placed++;
            }
        }

        System.out.println("Glowshrooms placed: " + placed);

        return placed > 0;
    }

}
