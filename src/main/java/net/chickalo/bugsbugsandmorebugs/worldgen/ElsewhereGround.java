package net.chickalo.bugsbugsandmorebugs.worldgen;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
public final class ElsewhereGround {
    private ElsewhereGround() {}
    public static boolean matches(BlockState state, Block vanilla) {
        return state.is(vanilla)
            || (vanilla == Blocks.GRASS_BLOCK && state.is(ModBlocks.GRASSY_LOAM))
            || (vanilla == Blocks.DIRT && state.is(ModBlocks.LOAM))
            || (vanilla == Blocks.STONE && state.is(ModBlocks.RHYOLITE));
    }
}
