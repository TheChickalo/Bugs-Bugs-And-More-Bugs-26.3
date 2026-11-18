package net.chickalo.bugsbugsandmorebugs.block.custom;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

// Uses exposure, rather than effective daylight, so Starless will not kill the grass.
public class GrassyLoamBlock extends Block {
    public GrassyLoamBlock(Properties properties) { super(properties); }
    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState above = level.getBlockState(pos.above());
        if (above.isSolidRender() || !level.getFluidState(pos.above()).isEmpty()) {
            level.setBlock(pos, ModBlocks.LOAM.defaultBlockState(), 3);
            return;
        }
        for (int i=0; i<4; i++) {
            BlockPos target=pos.offset(random.nextInt(3)-1,random.nextInt(3)-1,random.nextInt(3)-1);
            if (level.getBlockState(target).is(ModBlocks.LOAM) && level.getBlockState(target.above()).isAir())
                level.setBlock(target, defaultBlockState(), 3);
        }
    }
}
