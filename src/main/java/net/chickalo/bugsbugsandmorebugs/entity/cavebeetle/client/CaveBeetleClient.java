package net.chickalo.bugsbugsandmorebugs.entity.cavebeetle.client;
import net.chickalo.bugsbugsandmorebugs.entity.cavebeetle.CaveBeetleContent;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderers;
public final class CaveBeetleClient {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(CaveBeetleContent.id("cave_beetle"), "main");
    public static void register() {
        ModelLayerRegistry.registerModelLayer(LAYER, CaveBeetleModel::createBodyLayer);
        EntityRenderers.register(CaveBeetleContent.BEETLE, CaveBeetleRenderer::new);
    }
}

