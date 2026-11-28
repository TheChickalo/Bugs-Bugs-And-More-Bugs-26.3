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
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** Up/down limestone deposits; segment 0 tip, 1 frustum, 2 middle,
 * 3 base, 4 merged tip. Decorative rather than vanilla falling dripstone. */
public class CaveSpeleothemBlock extends Block {
    public static final BooleanProperty UP=BooleanProperty.create("up");
    public static final IntegerProperty SEGMENT=IntegerProperty.create("segment",0,4);
    public CaveSpeleothemBlock(Properties p){
        super(p);
        registerDefaultState(stateDefinition.any().setValue(UP,true).setValue(SEGMENT,0));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){
        builder.add(UP,SEGMENT);
    }
    private boolean same(BlockState s,boolean up){return s.is(this)&&s.getValue(UP)==up;}
    @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){
        boolean up=state.getValue(UP);
        Direction dir=up?Direction.DOWN:Direction.UP;
        BlockPos support=pos.relative(dir);
        BlockState s=level.getBlockState(support);
        return same(s,up)||s.isFaceSturdy(level,support,dir.getOpposite());
    }
    private BlockState shapeFor(BlockState state,LevelReader level,BlockPos pos){
        boolean up=state.getValue(UP);
        Direction tip=up?Direction.UP:Direction.DOWN;
        BlockState ahead=level.getBlockState(pos.relative(tip));
        BlockState back=level.getBlockState(pos.relative(tip.getOpposite()));
        int segment;
        if(ahead.is(this)&&ahead.getValue(UP)!=up)segment=4;
        else if(!same(ahead,up))segment=0;
        else if(!same(back,up))segment=3;
        else if(!same(level.getBlockState(pos.relative(tip,2)),up))segment=1;
        else segment=2;
        return state.setValue(SEGMENT,segment);
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){
        boolean up=context.getClickedFace()!=Direction.DOWN;
        BlockState state=defaultBlockState().setValue(UP,up);
        return state.canSurvive(context.getLevel(),context.getClickedPos())
                ?shapeFor(state,context.getLevel(),context.getClickedPos()):null;
    }
    @Override protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,
            BlockPos pos,Direction direction,BlockPos neighborPos,BlockState neighborState,RandomSource random){
        if(direction.getAxis()==Direction.Axis.Y){
            if(!state.canSurvive(level,pos))return Blocks.AIR.defaultBlockState();
            return shapeFor(state,level,pos);
        }
        return super.updateShape(state,level,ticks,pos,direction,neighborPos,neighborState,random);
    }
}
