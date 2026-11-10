package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

/**
 * Registers the codecs that identify the custom world-generation feature types.
 */
public final class ModFeatureTypes {
    private ModFeatureTypes() {
    }

    public static void register() {
        Registry.register(
                BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "maple_lake"),
                MapleLakeFeature.CODEC
        );

        Registry.register(
                BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "protected_bamboo"),
                ProtectedBambooFeature.CODEC
        );

        Registry.register(
                BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "berry_bush_patch"),
                BerryBushPatchFeature.CODEC
        );
    }
}
