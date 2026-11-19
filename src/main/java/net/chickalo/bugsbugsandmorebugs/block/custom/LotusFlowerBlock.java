package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Cross model flower resting on the separate flat Lotus pad. */
public class LotusFlowerBlock extends BushBlock {
    public LotusFlowerBlock(Properties properties){super(properties);}
    @Override protected boolean mayPlaceOn(BlockState state,BlockGetter level,BlockPos pos){
        return state.is(ModBlocks.LOTUS);
    }
}
