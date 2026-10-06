package net.chickalo.bugsbugsandmorebugs.entity.client;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;

public class ModModelLayerLocations {
    public static final ModelLayerLocation ANTWORKER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "ant_worker"), "main");
}
