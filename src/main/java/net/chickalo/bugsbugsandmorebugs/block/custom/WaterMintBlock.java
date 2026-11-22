package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

/**
 * Two sections: waterlogged submerged roots, and one exposed flowering tip.
 * Water is retained as a fluid state; breaking the upper part restores the source.
 */
public class WaterMintBlock extends FlexibleWetlandPlantBlock {
    public WaterMintBlock(Properties properties) {
        super(2, properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos base = context.getClickedPos();
        var level = context.getLevel();
        if (!level.getFluidState(base).is(Fluids.WATER) || !level.getFluidState(base).isSource()) return null;
        if (!mayPlaceOn(level.getBlockState(base.below()), level, base.below())) return null;
        if (!level.isEmptyBlock(base.above())) return null;
        return defaultBlockState().setValue(PART, 0).setValue(WATERLOGGED, true)
                .setValue(SHORT, false);
    }
}
