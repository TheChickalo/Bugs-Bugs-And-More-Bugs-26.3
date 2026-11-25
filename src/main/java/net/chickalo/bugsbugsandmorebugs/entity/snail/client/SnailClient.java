package net.chickalo.bugsbugsandmorebugs.entity.snail.client;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.chickalo.bugsbugsandmorebugs.entity.snail.SnailContent;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.Identifier;

public final class SnailClient {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID,"snail"),"main");
    public static void register() {
        ModelLayerRegistry.registerModelLayer(LAYER, SnailModel::createBodyLayer);
        EntityRenderers.register(SnailContent.SNAIL, SnailRenderer::new);
    }
}
