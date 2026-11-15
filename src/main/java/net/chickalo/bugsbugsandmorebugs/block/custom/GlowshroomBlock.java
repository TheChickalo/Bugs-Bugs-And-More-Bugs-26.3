
package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;

public class GlowshroomBlock extends MushroomBlock {

    public GlowshroomBlock(ResourceKey<Feature> feature,
                           BlockBehaviour.Properties properties) {
        super(feature, properties);
    }


    @Override
    public void animateTick(BlockState state, Level level,
                            BlockPos pos, RandomSource random) {

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;

        level.addParticle(
                ParticleTypes.END_ROD,
                x, y, z,
                0.0, 0.02, 0.0
        );
    }
}
