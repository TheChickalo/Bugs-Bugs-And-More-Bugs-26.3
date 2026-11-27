package net.chickalo.bugsbugsandmorebugs.entity.snail.client;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.chickalo.bugsbugsandmorebugs.entity.snail.SnailEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class SnailRenderer extends MobRenderer<SnailEntity, SnailRenderState, SnailModel> {
    private static final Identifier[][] TEXTURES = new Identifier[6][2];
    static {
        for (int i=0;i<6;i++) for (int moss=0;moss<2;moss++) {
            TEXTURES[i][moss] = Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "textures/entity/snail/snail_"+SnailEntity.SKINS[i]+(moss==1?"_mossy":"")+".png");
        }
    }
    public SnailRenderer(EntityRendererProvider.Context context) {
        super(context, new SnailModel(context.bakeLayer(SnailClient.LAYER)), 0.55F);
    }
    @Override public Identifier getTextureLocation(SnailRenderState state) { return TEXTURES[Math.clamp(state.variant,0,5)][state.mossy?1:0]; }
    @Override public SnailRenderState createRenderState() { return new SnailRenderState(); }
    @Override public void extractRenderState(SnailEntity entity, SnailRenderState state, float partialTicks) {
        super.extractRenderState(entity,state,partialTicks);
        state.babySnail = entity.isBaby(); state.pose = entity.snailPose(); state.variant = entity.variant(); state.mossy = entity.mossy();
        state.animation.copyFrom(entity.visualAnimation);
    }
}
