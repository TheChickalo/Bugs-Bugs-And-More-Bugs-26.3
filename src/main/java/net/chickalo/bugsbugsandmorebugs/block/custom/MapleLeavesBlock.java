package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import net.minecraft.world.level.block.sounds.AmbientLeavesBlockSoundPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;

public class MapleLeavesBlock extends UntintedParticleLeavesBlock {

    public static final int MAX_MAPLE_DISTANCE = 11;

    public static final IntegerProperty MAPLE_DISTANCE =
            IntegerProperty.create("maple_distance", 1, MAX_MAPLE_DISTANCE);

    public MapleLeavesBlock(
            float leafParticleChance,
            net.minecraft.core.particles.ParticleOptions leafParticle,
            AmbientLeavesBlockSoundPlayer ambientLeavesBlockSoundPlayer,
            BlockBehaviour.Properties properties
    ) {
        super(leafParticleChance, leafParticle, ambientLeavesBlockSoundPlayer, properties);

        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(MAPLE_DISTANCE, MAX_MAPLE_DISTANCE)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(MAPLE_DISTANCE);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(MAPLE_DISTANCE) == MAX_MAPLE_DISTANCE
                && !state.getValue(PERSISTENT);
    }

    @Override
    protected boolean decaying(BlockState state) {
        return state.getValue(MAPLE_DISTANCE) == MAX_MAPLE_DISTANCE
                && !state.getValue(PERSISTENT);
    }

    @Override
    protected void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        if (this.decaying(state)) {
            dropResources(state, level, pos);
            level.removeBlock(pos, false);
        }
    }

    @Override
    protected void tick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        level.setBlockAndUpdate(
                pos,
                updateMapleDistance(state, level, pos)
        );
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess ticks,
            BlockPos pos,
            Direction directionToNeighbour,
            BlockPos neighbourPos,
            BlockState neighbourState,
            RandomSource random
    ) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(
                    pos,
                    Fluids.WATER,
                    Fluids.WATER.getTickDelay(level)
            );
        }

        int distanceFromNeighbor = Math.min(
                MAX_MAPLE_DISTANCE,
                getDistanceAt(neighbourState) + 1
        );

        if (distanceFromNeighbor != 1
                || state.getValue(MAPLE_DISTANCE) != distanceFromNeighbor) {
            ticks.scheduleTick(pos, this, 1);
        }

        return state;
    }

    private static BlockState updateMapleDistance(
            BlockState state,
            LevelAccessor level,
            BlockPos pos
    ) {
        int newDistance = MAX_MAPLE_DISTANCE;

        BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();

        for (Direction direction : Direction.values()) {
            neighborPos.setWithOffset(pos, direction);

            newDistance = Math.min(
                    newDistance,
                    getDistanceAt(level.getBlockState(neighborPos)) + 1
            );

            if (newDistance == 1) {
                break;
            }
        }

        return state
                .setValue(MAPLE_DISTANCE, newDistance)
                .setValue(DISTANCE, Math.min(newDistance, DECAY_DISTANCE));
    }

    private static int getDistanceAt(BlockState state) {

        if (state.is(BlockTags.PREVENTS_NEARBY_LEAF_DECAY)) {
            return 0;
        }

        if (state.getBlock() instanceof MapleLeavesBlock) {
            return state.getValue(MAPLE_DISTANCE);
        }

        return LeavesBlock.getOptionalDistanceAt(state)
                .orElse(MAX_MAPLE_DISTANCE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);

        return updateMapleDistance(
                state,
                context.getLevel(),
                context.getClickedPos()
        );
    }
}