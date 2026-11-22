package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Taller clubmoss shoots can only grow atop the low clubmoss ground cover. */
public class ClubmossSprigBlock extends BushBlock {
    public ClubmossSprigBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState ground, BlockGetter level, BlockPos pos) {
        return ground.is(ModBlocks.CLUBMOSS);
    }
}
