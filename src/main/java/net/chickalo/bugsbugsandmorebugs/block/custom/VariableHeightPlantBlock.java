
package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class VariableHeightPlantBlock extends BushBlock {

    public static final IntegerProperty SEGMENT =
            IntegerProperty.create("segment", 0, 3);

    public static final BooleanProperty TIP =
            BooleanProperty.create("tip");

    private final int maxHeight;

    public VariableHeightPlantBlock(
            int maxHeight,
            Properties properties
    ) {
        super(properties);

        if (maxHeight < 1 || maxHeight > 4) {
            throw new IllegalArgumentException(
                    "Plant height must be 1-4"
            );
        }

        this.maxHeight = maxHeight;

        registerDefaultState(
                stateDefinition.any()
                        .setValue(SEGMENT, 0)
                        .setValue(TIP, true)
        );
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(SEGMENT, TIP);
    }

    @Override
    protected boolean mayPlaceOn(
            BlockState ground,
            BlockGetter level,
            BlockPos pos
    ) {
        return net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(ground, Blocks.GRASS_BLOCK)
                || net.chickalo.bugsbugsandmorebugs.worldgen.ElsewhereGround.matches(ground, Blocks.DIRT)
                || ground.is(Blocks.COARSE_DIRT)
                || ground.is(Blocks.PODZOL)
                || ground.is(Blocks.ROOTED_DIRT)
                || ground.is(Blocks.MOSS_BLOCK)
                || ground.is(net.chickalo.bugsbugsandmorebugs.block.ModBlocks.RENDZINA)
                || ground.is(net.chickalo.bugsbugsandmorebugs.block.ModBlocks.LITHOSOL)
                || ground.is(net.chickalo.bugsbugsandmorebugs.block.ModBlocks.MOSSY_LIMESTONE);
    }

    @Override
    protected boolean canSurvive(
            BlockState state,
            LevelReader level,
            BlockPos pos
    ) {
        int segment = state.getValue(SEGMENT);

        if (segment == 0) {
            return super.canSurvive(state, level, pos);
        }

        BlockState below = level.getBlockState(pos.below());

        return below.is(this)
                && below.getValue(SEGMENT) == segment - 1;
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess ticks,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        if (direction == Direction.DOWN
                && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }

        if (direction == Direction.UP) {
            boolean isTip = !neighborState.is(this)
                    || neighborState.getValue(SEGMENT)
                    != state.getValue(SEGMENT) + 1;

            return state.setValue(TIP, isTip);
        }

        return super.updateShape(
                state, level, ticks, pos,
                direction, neighborPos, neighborState, random
        );
    }

    // Mine any section, harvest one root; other sections lose support.
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && state.getValue(SEGMENT) > 0) {
            BlockPos root = pos.below(state.getValue(SEGMENT));
            BlockState base = level.getBlockState(root);
            if (base.is(this) && base.getValue(SEGMENT) == 0) {
                if (!player.isCreative()) Block.dropResources(base, level, root);
                level.setBlock(root, Blocks.AIR.defaultBlockState(), 3);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    private BlockPos findTop(
            LevelReader level,
            BlockPos pos,
            BlockState state
    ) {
        BlockPos base = pos.below(state.getValue(SEGMENT));
        BlockPos top = base;

        for (int i = 1; i < maxHeight; i++) {
            BlockPos next = base.above(i);
            BlockState nextState = level.getBlockState(next);

            if (!nextState.is(this)
                    || nextState.getValue(SEGMENT) != i) {
                break;
            }

            top = next;
        }

        return top;
    }

    private boolean canGrowAbove(
            LevelReader level,
            BlockPos top,
            BlockState state
    ) {
        return state.getValue(SEGMENT) < maxHeight - 1
                && top.getY() + 1 < level.getMaxY()
                && level.getBlockState(top.above()).canBeReplaced()
                && level.getFluidState(top.above()).isEmpty();
    }

    private void growOne(
            LevelAccessor level,
            BlockPos top,
            BlockState state
    ) {
        if (!canGrowAbove(level, top, state)) {
            return;
        }

        int nextSegment = state.getValue(SEGMENT) + 1;

        level.setBlock(
                top,
                state.setValue(TIP, false),
                3
        );

        level.setBlock(
                top.above(),
                defaultBlockState()
                        .setValue(SEGMENT, nextSegment)
                        .setValue(TIP, true),
                3
        );
    }

    public boolean placeAtHeight(
            LevelAccessor level,
            BlockPos base,
            int height
    ) {
        if (height < 1 || height > maxHeight) {
            return false;
        }

        if (!defaultBlockState().canSurvive(level, base)) {
            return false;
        }

        for (int i = 0; i < height; i++) {
            BlockPos target = base.above(i);

            if (target.getY() >= level.getMaxY()
                    || !level.getBlockState(target).canBeReplaced()
                    || !level.getFluidState(target).isEmpty()) {
                return false;
            }
        }

        for (int i = 0; i < height; i++) {
            level.setBlock(
                    base.above(i),
                    defaultBlockState()
                            .setValue(SEGMENT, i)
                            .setValue(TIP, i == height - 1),
                    3
            );
        }

        return true;
    }

    @Override
    public boolean isValidBonemealTarget(
            LevelReader level,
            BlockPos pos,
            BlockState state,
            BonemealSource source
    ) {
        BlockPos top = findTop(level, pos, state);

        return canGrowAbove(
                level,
                top,
                level.getBlockState(top)
        );
    }

    @Override
    public boolean isBonemealSuccess(
            Level level,
            RandomSource random,
            BlockPos pos,
            BlockState state,
            BonemealSource source
    ) {
        return true;
    }

    @Override
    public void performBonemeal(
            ServerLevel level,
            RandomSource random,
            BlockPos pos,
            BlockState state,
            BonemealSource source
    ) {
        BlockPos top = findTop(level, pos, state);

        growOne(level, top, level.getBlockState(top));
    }

    @Override
    protected void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        if (state.getValue(TIP)
                && random.nextInt(25) == 0) {
            growOne(level, pos, state);
        }
    }
}
