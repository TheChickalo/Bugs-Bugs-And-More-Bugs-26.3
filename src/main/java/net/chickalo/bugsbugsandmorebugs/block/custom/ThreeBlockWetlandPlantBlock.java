package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/** Three cross-model segments: waterlogged lower third, two dry upper thirds. */
public class ThreeBlockWetlandPlantBlock extends BushBlock implements SimpleWaterloggedBlock {
    public static final IntegerProperty PART=IntegerProperty.create("part",0,2);
    public static final BooleanProperty WATERLOGGED=BlockStateProperties.WATERLOGGED;
    public ThreeBlockWetlandPlantBlock(Properties properties){
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(PART,0).setValue(WATERLOGGED,false));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){
        builder.add(PART,WATERLOGGED);
    }
    @Override protected boolean mayPlaceOn(BlockState ground,BlockGetter level,BlockPos pos){
        return ground.is(Blocks.MUD)||ground.is(Blocks.CLAY)||ground.is(Blocks.DIRT)
            ||ground.is(Blocks.GRASS_BLOCK)||ground.is(Blocks.COARSE_DIRT)
            ||ground.is(Blocks.PODZOL)||ground.is(ModBlocks.LOAM)||ground.is(ModBlocks.GRASSY_LOAM)||ground.is(ModBlocks.SILT);
    }
    @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){
        int part=state.getValue(PART);
        if(part==0)return state.getValue(WATERLOGGED)
            && mayPlaceOn(level.getBlockState(pos.below()),level,pos.below());
        BlockState below=level.getBlockState(pos.below());
        return below.is(this) && below.getValue(PART)==part-1;
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext ctx){
        BlockPos pos=ctx.getClickedPos();
        if(!ctx.getLevel().getFluidState(pos).is(Fluids.WATER)
            ||!ctx.getLevel().getFluidState(pos).isSource())return null;
        if(!ctx.getLevel().isEmptyBlock(pos.above())||!ctx.getLevel().isEmptyBlock(pos.above(2)))return null;
        return defaultBlockState().setValue(PART,0).setValue(WATERLOGGED,true);
    }
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){
        for(int part=1;part<=2;part++)level.setBlock(pos.above(part),
            defaultBlockState().setValue(PART,part).setValue(WATERLOGGED,false),3);
    }
    // Upper sections do not own loot; harvest the submerged root once.
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && state.getValue(PART) > 0) {
            BlockPos root = pos.below(state.getValue(PART));
            BlockState base = level.getBlockState(root);
            if (base.is(this) && base.getValue(PART) == 0) {
                if (!player.isCreative()) Block.dropResources(base, level, root);
                level.setBlock(root,
                        base.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
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
    public boolean placeNaturally(WorldGenLevel level,BlockPos pos){
        if(pos.getY()<level.getMinY()+1||pos.getY()>level.getMaxY()-3)return false;
        if(!level.getFluidState(pos).is(Fluids.WATER)||!level.getFluidState(pos).isSource())return false;
        if(!mayPlaceOn(level.getBlockState(pos.below()),level,pos.below()))return false;
        if(!level.isEmptyBlock(pos.above())||!level.isEmptyBlock(pos.above(2)))return false;
        level.setBlock(pos,defaultBlockState().setValue(PART,0).setValue(WATERLOGGED,true),2);
        for(int p=1;p<=2;p++)level.setBlock(pos.above(p),defaultBlockState().setValue(PART,p).setValue(WATERLOGGED,false),2);
        return true;
    }
}
