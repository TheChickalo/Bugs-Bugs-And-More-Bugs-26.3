package net.chickalo.bugsbugsandmorebugs.entity.cavebeetle.client;
import net.chickalo.bugsbugsandmorebugs.entity.cavebeetle.CaveBeetleContent;
import net.chickalo.bugsbugsandmorebugs.entity.cavebeetle.CaveBeetleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
public final class CaveBeetleRenderer extends MobRenderer<CaveBeetleEntity, CaveBeetleRenderState, CaveBeetleModel> {
    private static final Identifier TEXTURE = CaveBeetleContent.id("textures/entity/cave_beetle/cave_beetle.png");
    public CaveBeetleRenderer(EntityRendererProvider.Context context) {
        super(context, new CaveBeetleModel(context.bakeLayer(CaveBeetleClient.LAYER)), 0.45F);
    }
    @Override public Identifier getTextureLocation(CaveBeetleRenderState state) { return TEXTURE; }
    @Override public CaveBeetleRenderState createRenderState() { return new CaveBeetleRenderState(); }
    @Override public void extractRenderState(CaveBeetleEntity entity, CaveBeetleRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.pose = entity.beetlePose();
        state.animation.copyFrom(entity.visualAnimation);
    }
}

