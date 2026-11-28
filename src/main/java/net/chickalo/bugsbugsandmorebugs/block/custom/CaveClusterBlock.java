package net.chickalo.bugsbugsandmorebugs.block.custom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Small mushrooms can colonize exposed cave stone as well as soil. */
public class CaveClusterBlock extends BushBlock {
    public CaveClusterBlock(Properties p){super(p);}
    @Override protected boolean mayPlaceOn(BlockState ground, BlockGetter level, BlockPos pos) {
        return ground.isFaceSturdy(level,pos,Direction.UP);
    }
}
