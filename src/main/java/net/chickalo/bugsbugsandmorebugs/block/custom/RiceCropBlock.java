package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * Flooded paddy rice:
 *
 *   air (at planting time)
 *   one water source, occupied by a waterlogged rice root
 *   tilled loam
 *
 * Seedlings are one block tall, grow to two blocks at age 3, then three
 * blocks at age 5. Only the submerged root ticks and awards loot.
 */
public class RiceCropBlock extends BushBlock implements SimpleWaterloggedBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 6);
    public static final IntegerProperty PART = IntegerProperty.create("part", 0, 2);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final int MAX_AGE = 6;

    public RiceCropBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(PART, 0)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, PART, WATERLOGGED);
    }

    @Override
    protected boolean mayPlaceOn(BlockState soil, BlockGetter level, BlockPos pos) {
        return soil.is(ModBlocks.TILLED_LOAM);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos root = context.getClickedPos();
        FluidState fluid = context.getLevel().getFluidState(root);

        // No dry planting or two-block-deep ponds: tilled loam must sit
        // directly under a water source with an open air block above.
        if (!context.getLevel().getBlockState(root.below()).is(ModBlocks.TILLED_LOAM)
                || !fluid.is(Fluids.WATER)
                || !fluid.isSource()
                || !context.getLevel().isEmptyBlock(root.above())) {
            return null;
        }

        return defaultBlockState().setValue(WATERLOGGED, true);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        int part = state.getValue(PART);
        if (part == 0) {
            return state.getValue(WATERLOGGED)
                    && mayPlaceOn(level.getBlockState(pos.below()), level, pos.below());
        }

        BlockState below = level.getBlockState(pos.below());
        return !state.getValue(WATERLOGGED)
                && below.is(this)
                && below.getValue(PART) == part - 1;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
                                     BlockPos pos, Direction direction, BlockPos neighborPos,
                                     BlockState neighborState, RandomSource random) {
        // Keep the one-block-deep water source intact when the soil is removed.
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        if (!state.canSurvive(level, pos)) {
            return state.getValue(WATERLOGGED)
                    ? Blocks.WATER.defaultBlockState()
                    : Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }

    private boolean hasFloodedRoot(LevelReader level, BlockPos root, BlockState base) {
        return base.is(this)
                && base.getValue(PART) == 0
                && base.getValue(WATERLOGGED)
                && base.getFluidState().is(Fluids.WATER)
                && level.getBlockState(root.below()).is(ModBlocks.TILLED_LOAM)
                // All parts above the submerged root must stay out of water.
                && level.getFluidState(root.above()).isEmpty();
    }

    private boolean canAdvance(LevelReader level, BlockPos root, BlockState base) {
        if (!hasFloodedRoot(level, root, base) || base.getValue(AGE) >= MAX_AGE) return false;
        int next = base.getValue(AGE) + 1;
        if (next == 3 && !level.isEmptyBlock(root.above())) return false;
        return next != 5 || level.isEmptyBlock(root.above(2));
    }

    private void advance(LevelAccessor level, BlockPos root, BlockState base) {
        if (!canAdvance(level, root, base)) return;
        int age = base.getValue(AGE) + 1;

        // Preserve WATERLOGGED=true on the root when advancing its age.
        level.setBlock(root, base.setValue(AGE, age), 3);
        if (age >= 3) {
            level.setBlock(root.above(),
                    defaultBlockState().setValue(PART, 1).setValue(AGE, age), 3);
        }
        if (age >= 5) {
            level.setBlock(root.above(2),
                    defaultBlockState().setValue(PART, 2).setValue(AGE, age), 3);
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(PART) != 0
                || level.getRawBrightness(pos, 0) < 9
                || random.nextInt(5) != 0) return;
        advance(level, pos, state);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state,
                                         BonemealSource source) {
        BlockPos root = pos.below(state.getValue(PART));
        return canAdvance(level, root, level.getBlockState(root));
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos,
                                     BlockState state, BonemealSource source) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos,
                                BlockState state, BonemealSource source) {
        BlockPos root = pos.below(state.getValue(PART));
        for (int i = 0, steps = 1 + random.nextInt(2); i < steps; i++) {
            BlockState base = level.getBlockState(root);
            if (!canAdvance(level, root, base)) break;
            advance(level, root, base);
        }
    }

    // Breaking an upper stalk awards the root's loot once, and restores
    // the water source instead of leaving a hole in the flooded terrace.
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && state.getValue(PART) > 0) {
            BlockPos root = pos.below(state.getValue(PART));
            BlockState base = level.getBlockState(root);
            if (base.is(this) && base.getValue(PART) == 0) {
                if (!player.isCreative()) {
                    Block.dropResources(base, level, root, level.getBlockEntity(root),
                            player, player.getMainHandItem());
                }
                level.setBlock(root, Blocks.WATER.defaultBlockState(), 3);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
