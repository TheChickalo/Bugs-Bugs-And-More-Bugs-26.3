package net.chickalo.bugsbugsandmorebugs.worldgen.tree;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.block.custom.HangingAccentBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

/** Adds flowering leaf islands while the actual tree shape remains vanilla. */
public final class MimosaDecorator extends TreeDecorator {
    public static final MapCodec<MimosaDecorator> CODEC=MapCodec.unit(new MimosaDecorator());
    @Override protected TreeDecoratorType<?> type() { return ModTreeDecoratorTypes.MIMOSA_DECORATOR; }
    @Override public void place(Context context) {
        var random=context.random();
        for(BlockPos pos:context.leaves()){
            if(random.nextInt(5)!=0)continue;
            // The extended-distance leaves repair their support via scheduled
            // ticks; do not mark flowered leaves persistent forever.
            if(!context.checkBlock(pos,s->s.is(ModBlocks.MIMOSA_LEAVES)))continue;
            context.setBlock(pos,ModBlocks.FLOWERING_MIMOSA_LEAVES.defaultBlockState());
            if(random.nextInt(3)==0 && context.isAir(pos.below()))
                context.setBlock(pos.below(),ModBlocks.MIMOSA_BLOSSOMS.defaultBlockState()
                        .setValue(BlockStateProperties.FACING,Direction.DOWN));
        }
    }
}
