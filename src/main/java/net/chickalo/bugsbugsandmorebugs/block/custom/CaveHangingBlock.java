package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/** Ceiling-supported decorative chains, with explicit base/middle/tip states. */
public class CaveHangingBlock extends Block {
    public static final BooleanProperty BASE = BooleanProperty.create("base");
    public static final BooleanProperty TIP = BooleanProperty.create("tip");

    public CaveHangingBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(BASE,true).setValue(TIP,true));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        builder.add(BASE,TIP);
    }
    @Override protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos above=pos.above();
        BlockState support=level.getBlockState(above);
        return support.is(this) || support.isFaceSturdy(level,above,Direction.DOWN);
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos at=context.getClickedPos();
        var level=context.getLevel();
        BlockState state=defaultBlockState().setValue(BASE,!level.getBlockState(at.above()).is(this))
                .setValue(TIP,!level.getBlockState(at.below()).is(this));
        return state.canSurvive(level,at)?state:null;
    }
    @Override protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
            BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (direction==Direction.UP) {
            if(!state.canSurvive(level,pos))return Blocks.AIR.defaultBlockState();
            return state.setValue(BASE,!neighborState.is(this));
        }
        if(direction==Direction.DOWN)return state.setValue(TIP,!neighborState.is(this));
        return super.updateShape(state,level,ticks,pos,direction,neighborPos,neighborState,random);
    }
}
