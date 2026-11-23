package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;

/**
 * Irrigated paddy rice. Seedlings begin one block tall, reach two blocks at
 * age 3 and grow their final grain-bearing third at age 5. Only the root ticks
 * and produces loot, so breaking a stalk never duplicates a harvest.
 */
public class RiceCropBlock extends BushBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 6);
    public static final IntegerProperty PART = IntegerProperty.create("part", 0, 2);
    public static final int MAX_AGE = 6;

    public RiceCropBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AGE, 0).setValue(PART, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, PART);
    }

    @Override
    protected boolean mayPlaceOn(BlockState soil, BlockGetter level, BlockPos pos) {
        return soil.is(Blocks.MUD) || soil.is(Blocks.FARMLAND)
                || soil.is(ModBlocks.LOAM) || soil.is(ModBlocks.GRASSY_LOAM)
                || soil.is(ModBlocks.TILLED_LOAM);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        int part = state.getValue(PART);
        if (part == 0) return super.canSurvive(state, level, pos);
        BlockState below = level.getBlockState(pos.below());
        return below.is(this) && below.getValue(PART) == part - 1;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
                                     BlockPos pos, Direction direction, BlockPos neighborPos,
                                     BlockState neighborState, RandomSource random) {
        if (!state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }

    // One source irrigates a 7x7 paddy; rice does not advance if the paddy dries.
    private boolean hasWaterNearby(LevelReader level, BlockPos root) {
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                for (int dy = -1; dy <= 0; dy++) {
                    if (level.getFluidState(root.offset(dx, dy, dz)).is(Fluids.WATER)) return true;
                }
            }
        }
        return false;
    }

    private boolean canAdvance(LevelReader level, BlockPos root, BlockState base) {
        if (!base.is(this) || base.getValue(PART) != 0 || base.getValue(AGE) >= MAX_AGE) return false;
        int next = base.getValue(AGE) + 1;
        if (next == 3 && !level.isEmptyBlock(root.above())) return false;
        return next != 5 || level.isEmptyBlock(root.above(2));
    }

    private void advance(LevelAccessor level, BlockPos root, BlockState base) {
        if (!canAdvance(level, root, base)) return;
        int age = base.getValue(AGE) + 1;
        level.setBlock(root, base.setValue(AGE, age), 3);
        if (age >= 3) level.setBlock(root.above(),
                defaultBlockState().setValue(PART, 1).setValue(AGE, age), 3);
        if (age >= 5) level.setBlock(root.above(2),
                defaultBlockState().setValue(PART, 2).setValue(AGE, age), 3);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(PART) != 0 || level.getRawBrightness(pos, 0) < 9
                || random.nextInt(5) != 0 || !hasWaterNearby(level, pos)) return;
        advance(level, pos, state);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state,
                                         BonemealSource source) {
        BlockPos root = pos.below(state.getValue(PART));
        return hasWaterNearby(level, root) && canAdvance(level, root, level.getBlockState(root));
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
        if (!hasWaterNearby(level, root)) return;
        for (int i = 0, steps = 1 + random.nextInt(2); i < steps; i++) {
            BlockState base = level.getBlockState(root);
            if (!canAdvance(level, root, base)) break;
            advance(level, root, base);
        }
    }

    // Harvesting any upper section breaks the plant and awards the root's loot once.
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && state.getValue(PART) > 0) {
            BlockPos root = pos.below(state.getValue(PART));
            BlockState base = level.getBlockState(root);
            if (base.is(this) && base.getValue(PART) == 0) {
                if (!player.isCreative()) Block.dropResources(base, level, root);
                level.removeBlock(root, false);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
