package net.chickalo.bugsbugsandmorebugs.registries;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.material.Fluids;

/**
 * Vanilla hoe tilling requires air above the soil. Rice paddies instead
 * require one water source directly above tilled loam, then air above that.
 * This narrowly adds the underwater case without changing ordinary tilling.
 */
public final class ModPaddyTilling {
    private ModPaddyTilling() {}

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (player.isSpectator()) return InteractionResult.PASS;
            if (!(player.getItemInHand(hand).getItem() instanceof HoeItem)) {
                return InteractionResult.PASS;
            }

            BlockPos pos = hit.getBlockPos();
            if (hit.getDirection() != Direction.UP) return InteractionResult.PASS;

            var ground = level.getBlockState(pos);
            if (!ground.is(ModBlocks.LOAM)
                    && !ground.is(ModBlocks.GRASSY_LOAM)
                    && !ground.is(ModBlocks.COARSE_LOAM)
                    && !ground.is(ModBlocks.ROOTED_LOAM)) {
                return InteractionResult.PASS;
            }

            // Water must be exactly one source block deep, with air above it.
            var water = level.getFluidState(pos.above());
            if (!water.is(Fluids.WATER)
                    || !water.isSource()
                    || !level.isEmptyBlock(pos.above(2))) {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide()) {
                level.setBlock(pos,
                        ModBlocks.TILLED_LOAM.defaultBlockState()
                                .setValue(FarmlandBlock.MOISTURE, 7), 3);
                level.playSound(null, pos, SoundEvents.HOE_TILL,
                        SoundSource.BLOCKS, 1.0F, 1.0F);
                player.getItemInHand(hand).hurtAndBreak(1, player, hand);
            }
            // Client success sends the right-click packet; server changes soil.
            return InteractionResult.SUCCESS;
        });
    }
}
