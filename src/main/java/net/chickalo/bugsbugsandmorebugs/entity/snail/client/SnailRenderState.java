package net.chickalo.bugsbugsandmorebugs.entity.snail.client;

import net.chickalo.bugsbugsandmorebugs.entity.snail.SnailPose;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class SnailRenderState extends LivingEntityRenderState {
    public boolean climbing;
    public SnailPose pose = SnailPose.IDLE;
    public int variant;
    public boolean mossy, babySnail;
    public final AnimationState animation = new AnimationState();
}
