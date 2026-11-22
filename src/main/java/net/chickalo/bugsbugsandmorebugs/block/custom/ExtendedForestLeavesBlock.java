package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import net.minecraft.world.level.block.sounds.AmbientLeavesBlockSoundPlayer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;

/**
 * Long-range natural leaf decay for the five new forest trees.
 *
 * Their broad branches are genuine logs in the Minecraft log/leaf-support tags;
 * vanilla's distance=7 is simply too short for these crowns. Unlike setting
 * persistent=true, this still lets detached foliage decay after chopping.
 */
public class ExtendedForestLeavesBlock extends UntintedParticleLeavesBlock {
    private static final int MAX_FOREST_DISTANCE = 16;
    public static final IntegerProperty FOREST_DISTANCE =
            IntegerProperty.create("forest_distance", 1, MAX_FOREST_DISTANCE);

    public ExtendedForestLeavesBlock(float chance, ParticleOptions particle,
                                     AmbientLeavesBlockSoundPlayer soundPlayer,
                                     BlockBehaviour.Properties properties) {
        super(chance, particle, soundPlayer, properties);
        registerDefaultState(defaultBlockState().setValue(FOREST_DISTANCE, MAX_FOREST_DISTANCE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FOREST_DISTANCE);
    }

    @Override
    protected boolean decaying(BlockState state) {
        return !state.getValue(PERSISTENT) && state.getValue(FOREST_DISTANCE) == MAX_FOREST_DISTANCE;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return decaying(state);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // First repair any stale distance after chunk load or worldgen, then
        // let vanilla handle a genuinely orphaned leaf normally.
        BlockState updated = updateForestDistance(state, level, pos);
        if (!updated.equals(state)) {
            level.setBlockAndUpdate(pos, updated);
            return;
        }
        super.randomTick(state, level, pos, random);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState updated = updateForestDistance(state, level, pos);
        if (!updated.equals(state)) level.setBlockAndUpdate(pos, updated);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
                                     BlockPos pos, Direction direction, BlockPos neighborPos,
                                     BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED))
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        ticks.scheduleTick(pos, this, 1);
        return state;
    }

    @Override
    protected void onPlace(BlockState state, net.minecraft.world.level.Level level, BlockPos pos,
                           BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!oldState.is(this)) level.scheduleTick(pos, this, 1);
    }

    private static BlockState updateForestDistance(BlockState state, LevelAccessor level, BlockPos pos) {
        int distance = MAX_FOREST_DISTANCE;
        BlockPos.MutableBlockPos neighbor = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.values()) {
            neighbor.setWithOffset(pos, direction);
            distance = Math.min(distance, distanceAt(level.getBlockState(neighbor)) + 1);
            if (distance == 1) break;
        }
        // Vanilla still receives its expected 1..7 distance state.
        return state.setValue(FOREST_DISTANCE, distance)
                .setValue(DISTANCE, Math.min(distance, DECAY_DISTANCE));
    }

    private static int distanceAt(BlockState state) {
        if (state.is(BlockTags.PREVENTS_NEARBY_LEAF_DECAY) || state.is(BlockTags.LOGS))
            return 0;
        if (state.getBlock() instanceof ExtendedForestLeavesBlock)
            return state.getValue(FOREST_DISTANCE);
        return LeavesBlock.getOptionalDistanceAt(state).orElse(MAX_FOREST_DISTANCE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return updateForestDistance(super.getStateForPlacement(context),
                context.getLevel(), context.getClickedPos());
    }
}
