package net.chickalo.bugsbugsandmorebugs.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Give custom sweet berry bush subclasses vanilla land-pathfinding hazard behavior. */
@Mixin(WalkNodeEvaluator.class)
public abstract class BerryBushPathfindingMixin {
    @Inject(method = "getPathTypeFromState", at = @At("HEAD"), cancellable = true)
    private static void bugs$berryBushHazard(BlockGetter level, BlockPos pos, CallbackInfoReturnable<PathType> cir) {
        if (level.getBlockState(pos).getBlock() instanceof SweetBerryBushBlock) {
            cir.setReturnValue(PathType.DAMAGING);
        }
    }
}
