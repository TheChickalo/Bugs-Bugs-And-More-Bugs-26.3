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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/** Fully submerged, single-block cave vegetation, with source-water preservation. */
public class SubterraneanWaterweedBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED=BlockStateProperties.WATERLOGGED;
    public SubterraneanWaterweedBlock(Properties p){
        super(p);
        registerDefaultState(stateDefinition.any().setValue(WATERLOGGED,true));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){
        builder.add(WATERLOGGED);
    }
    @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){
        BlockPos below=pos.below();
        return state.getValue(WATERLOGGED)&&level.getBlockState(below).isFaceSturdy(level,below,Direction.UP);
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext ctx){
        FluidState water=ctx.getLevel().getFluidState(ctx.getClickedPos());
        BlockState state=defaultBlockState();
        return water.is(Fluids.WATER)&&water.isSource()&&state.canSurvive(ctx.getLevel(),ctx.getClickedPos())?state:null;
    }
    @Override protected FluidState getFluidState(BlockState state){
        return state.getValue(WATERLOGGED)?Fluids.WATER.getSource(false):super.getFluidState(state);
    }
    @Override protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,
            BlockPos pos,Direction direction,BlockPos neighborPos,BlockState neighborState,RandomSource random){
        if(!state.canSurvive(level,pos))return state.getValue(WATERLOGGED)?Blocks.WATER.defaultBlockState():Blocks.AIR.defaultBlockState();
        if(state.getValue(WATERLOGGED))ticks.scheduleTick(pos,Fluids.WATER,Fluids.WATER.getTickDelay(level));
        return super.updateShape(state,level,ticks,pos,direction,neighborPos,neighborState,random);
    }
}
