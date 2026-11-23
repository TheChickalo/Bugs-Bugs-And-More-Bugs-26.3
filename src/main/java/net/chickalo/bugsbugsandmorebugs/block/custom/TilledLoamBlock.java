package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Vanilla hydration and trampling, with loam-specific restoration. */
public class TilledLoamBlock extends FarmBlock {
    public TilledLoamBlock(Properties properties) { super(properties); }

    private static void restoreLoam(Level level, BlockPos pos) {
        if (!level.isClientSide() && level.getBlockState(pos).is(Blocks.DIRT))
            level.setBlock(pos, ModBlocks.LOAM.defaultBlockState(), 3);
    }

    @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        restoreLoam(level, pos);
    }

    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        restoreLoam(level, pos);
    }

    @Override public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        super.fallOn(level, state, pos, entity, fallDistance);
        restoreLoam(level, pos);
    }
}
