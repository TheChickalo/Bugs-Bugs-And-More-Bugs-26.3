package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Solid, low-root collision/selection shape rather than an invisible full cube.
 * Both cedar and willow use the same geometric behavior with different bark.
 */
public class ForestRootsBlock extends RotatedPillarBlock {
    private static final VoxelShape ROOT_BASE = Block.box(0,0,0,16,12,16);
    private static final VoxelShape ROOT_EAST_WEST = Block.box(0,0,2,16,12,15);
    private static final VoxelShape ROOT_NORTH_SOUTH = Block.box(2,0,0,15,12,16);

    public ForestRootsBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction.Axis axis=state.getValue(AXIS);
        return switch(axis) {
            case X -> ROOT_EAST_WEST;
            case Z -> ROOT_NORTH_SOUTH;
            case Y -> ROOT_BASE;
        };
    }
}
