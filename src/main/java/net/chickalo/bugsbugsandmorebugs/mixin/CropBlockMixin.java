package net.chickalo.bugsbugsandmorebugs.mixin;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Make vanilla crop plants recognize custom farmland. */
@Mixin(CropBlock.class)
public abstract class CropBlockMixin {
    @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
    private void bugs$allowTilledLoam(BlockState soil, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (soil.is(ModBlocks.TILLED_LOAM)) cir.setReturnValue(true);
    }
}
