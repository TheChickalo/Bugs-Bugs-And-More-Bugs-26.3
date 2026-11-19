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

/** Hinoki-only extended leaf decay. Maple/Ginkgo distance behavior is unchanged. */
public class HinokiLeavesBlock extends UntintedParticleLeavesBlock {
    public static final int MAX_HINOKI_DISTANCE = 15;
    public static final IntegerProperty HINOKI_DISTANCE =
            IntegerProperty.create("hinoki_distance", 1, MAX_HINOKI_DISTANCE);

    public HinokiLeavesBlock(float leafParticleChance, ParticleOptions leafParticle,
                             AmbientLeavesBlockSoundPlayer ambientLeavesBlockSoundPlayer,
                             BlockBehaviour.Properties properties) {
        super(leafParticleChance, leafParticle, ambientLeavesBlockSoundPlayer, properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(HINOKI_DISTANCE, MAX_HINOKI_DISTANCE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HINOKI_DISTANCE);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(HINOKI_DISTANCE) == MAX_HINOKI_DISTANCE
                && !state.getValue(PERSISTENT);
    }

    @Override
    protected boolean decaying(BlockState state) {
        return !state.getValue(PERSISTENT)
                && state.getValue(HINOKI_DISTANCE) == MAX_HINOKI_DISTANCE;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState updated = updateHinokiDistance(state, level, pos);
        if (updated != state) {
            level.setBlockAndUpdate(pos, updated);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
            BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos,
            BlockState neighbourState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        // Queue a full recalculation when neighbors are placed or removed.
        ticks.scheduleTick(pos, this, 1);
        return state;
    }

    @Override
    protected void onPlace(BlockState state, net.minecraft.world.level.Level level, BlockPos pos,
                           BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!oldState.is(this)) {
            level.scheduleTick(pos, this, 1);
        }
    }

    private static BlockState updateHinokiDistance(BlockState state, LevelAccessor level, BlockPos pos) {
        int distance = MAX_HINOKI_DISTANCE;
        BlockPos.MutableBlockPos neighbor = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.values()) {
            neighbor.setWithOffset(pos, direction);
            distance = Math.min(distance, distanceAt(level.getBlockState(neighbor)) + 1);
            if (distance == 1) break;
        }
        // Keep vanilla's distance state synchronized (1..7) so nearby vanilla
        // leaves can still use Hinoki as a normal support neighbor.
        return state.setValue(HINOKI_DISTANCE, distance)
                .setValue(DISTANCE, Math.min(distance, DECAY_DISTANCE));
    }

    private static int distanceAt(BlockState state) {
        if (state.is(BlockTags.PREVENTS_NEARBY_LEAF_DECAY)) return 0;
        if (state.getBlock() instanceof HinokiLeavesBlock) {
            return state.getValue(HINOKI_DISTANCE);
        }
        return LeavesBlock.getOptionalDistanceAt(state).orElse(MAX_HINOKI_DISTANCE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return updateHinokiDistance(super.getStateForPlacement(context),
                context.getLevel(), context.getClickedPos());
    }
}
