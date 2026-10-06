package net.chickalo.bugsbugsandmorebugs.entity.client;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.chickalo.bugsbugsandmorebugs.entity.custom.AntWorkerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class AntWorkerRenderer extends MobRenderer<AntWorkerEntity, AntWorkerRenderState, AntWorkerModel> {


    public AntWorkerRenderer(EntityRendererProvider.Context context) {
        super(context, new AntWorkerModel(context.bakeLayer(ModModelLayerLocations.ANTWORKER)), 0.3f);
    }

    @Override
    public Identifier getTextureLocation(AntWorkerRenderState state) {
        return Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "textures/entity/antworker/antworker.png");
    }

    @Override
    public AntWorkerRenderState createRenderState() {
        return new AntWorkerRenderState();
    }

    @Override
    public void extractRenderState(AntWorkerEntity entity, AntWorkerRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);

        state.idleAnimationState.copyFrom(entity.idleAnimationState);
    }
}

