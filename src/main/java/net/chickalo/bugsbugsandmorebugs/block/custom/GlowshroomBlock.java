
package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.block.BonemealSource;

public class GlowshroomBlock extends MushroomBlock {

    public GlowshroomBlock(ResourceKey<Feature> feature,
                           BlockBehaviour.Properties properties) {
        super(feature, properties);
    }

    @Override
    protected boolean canSurvive(BlockState state,
                                 LevelReader level,
                                 BlockPos pos) {

        BlockState ground = level.getBlockState(pos.below());

        return ground.is(Blocks.GRASS_BLOCK)
                || ground.is(Blocks.DIRT)
                || ground.is(Blocks.COARSE_DIRT)
                || ground.is(Blocks.PODZOL)
                || ground.is(Blocks.ROOTED_DIRT)
                || ground.is(Blocks.MYCELIUM)
                || ground.is(Blocks.MOSS_BLOCK);
    }


    @Override
    public boolean isValidBonemealTarget(
            LevelReader level,
            BlockPos pos,
            BlockState state,
            BonemealSource source) {
        return level.isInsideBuildHeight(pos.above(16));
    }


    @Override
    public void animateTick(BlockState state, Level level,
                            BlockPos pos, RandomSource random) {
        if (random.nextInt(5) != 0) {
            return;
        }
        double x = pos.getX() + 0.5
                + (random.nextDouble() - 0.5) * 1.2;
        double y = pos.getY() + 0.3
                + random.nextDouble() * 0.8;
        double z = pos.getZ() + 0.5
                + (random.nextDouble() - 0.5) * 1.2;
        level.addParticle(
                ParticleTypes.FIREFLY,
                x, y, z,
                0.0, 0.0, 0.0
        );
    }

}
