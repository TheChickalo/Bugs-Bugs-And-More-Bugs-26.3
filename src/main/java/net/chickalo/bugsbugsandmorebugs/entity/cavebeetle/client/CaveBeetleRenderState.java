package net.chickalo.bugsbugsandmorebugs.entity.cavebeetle.client;
import net.chickalo.bugsbugsandmorebugs.entity.cavebeetle.CaveBeetlePose;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;
public final class CaveBeetleRenderState extends LivingEntityRenderState {
    public CaveBeetlePose pose = CaveBeetlePose.IDLE;
    public final AnimationState idleState = new AnimationState();
    public final AnimationState investigateState = new AnimationState();
    public final AnimationState chewState = new AnimationState();
    public final AnimationState digState = new AnimationState();
}

