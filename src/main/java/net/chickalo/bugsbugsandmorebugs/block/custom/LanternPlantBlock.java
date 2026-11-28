package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Non-growing ceiling plant. Vines support further vines or a terminal bulb. */
public class LanternPlantBlock extends Block {
    private final boolean vine;
    public LanternPlantBlock(boolean vine, Properties properties) {
        super(properties);
        this.vine=vine;
    }
    @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos) {
        BlockPos above=pos.above();
        BlockState support=level.getBlockState(above);
        return support.is(ModBlocks.LANTERN_VINE)||support.isFaceSturdy(level,above,Direction.DOWN);
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state=defaultBlockState();
        return state.canSurvive(context.getLevel(),context.getClickedPos())?state:null;
    }
    @Override protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,
            BlockPos pos,Direction direction,BlockPos neighborPos,BlockState neighborState,RandomSource random) {
        if(direction==Direction.UP&&!state.canSurvive(level,pos))return Blocks.AIR.defaultBlockState();
        return super.updateShape(state,level,ticks,pos,direction,neighborPos,neighborState,random);
    }
}
