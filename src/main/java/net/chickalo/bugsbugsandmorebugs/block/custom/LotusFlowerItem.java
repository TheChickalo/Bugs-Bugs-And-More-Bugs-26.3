package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

/**
 * Right-click water to place a flowering lotus, or right-click an
 * existing lotus pad to turn it into the flowering variant.
 */
public class LotusFlowerItem extends PlaceOnWaterBlockItem {
    public LotusFlowerItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        if (level.getBlockState(pos).is(ModBlocks.LOTUS)) {
            if (!level.isClientSide()) {
                level.setBlock(pos, ModBlocks.LOTUS_FLOWER.defaultBlockState(), 3);
                var player = context.getPlayer();
                if (player == null || !player.isCreative()) {
                    context.getItemInHand().shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }
}
