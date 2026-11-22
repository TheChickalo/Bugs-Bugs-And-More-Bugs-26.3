package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Four cluster sizes and four horizontal orientations, on a single supporting wall face. */
public class BracketFungusBlock extends HorizontalDirectionalBlock {
    public static final IntegerProperty COUNT = IntegerProperty.create("count", 1, 4);

    public BracketFungusBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(COUNT, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, COUNT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState existing = context.getLevel().getBlockState(context.getClickedPos());
        if (existing.is(this) && existing.getValue(COUNT) < 4) {
            return existing.setValue(COUNT, existing.getValue(COUNT) + 1);
        }
        Direction clicked = context.getClickedFace();
        if (clicked.getAxis().isHorizontal()) {
            BlockState candidate = defaultBlockState().setValue(FACING, clicked);
            if (candidate.canSurvive(context.getLevel(), context.getClickedPos())) return candidate;
        }
        for (Direction face : context.getNearestLookingDirections()) {
            if (!face.getAxis().isHorizontal()) continue;
            BlockState candidate = defaultBlockState().setValue(FACING, face);
            if (candidate.canSurvive(context.getLevel(), context.getClickedPos())) return candidate;
        }
        return null;
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return (context.getItemInHand().is(asItem()) && state.getValue(COUNT) < 4)
                || super.canBeReplaced(state, context);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction face = state.getValue(FACING);
        BlockPos anchor = pos.relative(face.getOpposite());
        return level.getBlockState(anchor).isFaceSturdy(level, anchor, face);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
            BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos))
            return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int count = state.getValue(COUNT);
        return switch (state.getValue(FACING)) {
            case NORTH -> Block.box(1, 3, count == 1 ? 9 : 5, 15, 14, 16);
            case SOUTH -> Block.box(1, 3, 0, 15, 14, count == 1 ? 7 : 11);
            case WEST -> Block.box(count == 1 ? 9 : 5, 3, 1, 16, 14, 15);
            case EAST -> Block.box(0, 3, 1, count == 1 ? 7 : 11, 14, 15);
            default -> Block.box(3, 3, 3, 13, 13, 13);
        };
    }
}
