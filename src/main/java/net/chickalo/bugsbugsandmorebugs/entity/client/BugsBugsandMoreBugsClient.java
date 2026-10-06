package net.chickalo.bugsbugsandmorebugs.entity.client;

import net.chickalo.bugsbugsandmorebugs.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class BugsBugsandMoreBugsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLayerRegistry.registerModelLayer(ModModelLayerLocations.ANTWORKER, AntWorkerModel::createBodyLayer);
        EntityRenderers.register(ModEntities.ANTWORKER, AntWorkerRenderer::new);
    }
}
