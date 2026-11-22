package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.state.BlockState;

/** An amethyst-style blossom that can attach to foliage, including underside faces. */
public class LeafBlossomBlock extends AmethystClusterBlock {
    public LeafBlossomBlock(int height, int offset, Properties properties) {
        super(height, offset, properties);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction face = state.getValue(FACING);
        BlockState support = level.getBlockState(pos.relative(face.getOpposite()));
        return support.is(BlockTags.LEAVES) || support.is(BlockTags.LOGS)
                || super.canSurvive(state, level, pos);
    }
}
