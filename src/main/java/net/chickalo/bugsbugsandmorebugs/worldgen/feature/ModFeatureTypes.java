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
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "hinoki_boulder"),
                HinokiBoulderFeature.CODEC
        );

        Registry.register(
                BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "volcanic_magma_patch"),
                VolcanicMagmaPatchFeature.CODEC
        );

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

        Registry.register(
                BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(
                        BugsBugsAndMoreBugs.MOD_ID,
                        "glowshroom_patch"
                ),
                GlowshroomPatchFeature.CODEC
        );


        Registry.register(
                BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(
                        BugsBugsAndMoreBugs.MOD_ID,
                        "glowshroom_tree_base"
                ),
                GlowshroomTreeBaseFeature.CODEC
        );


        Registry.register(
                BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(
                        BugsBugsAndMoreBugs.MOD_ID,
                        "huge_glowshroom"
                ),
                HugeGlowshroomFeature.CODEC
        );

        Registry.register(
                BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "maple_water_shore"),
                MapleWaterShoreFeature.CODEC
        );

        Registry.register(
                BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "maple_riverbed_mud"),
                MapleRiverbedMudFeature.CODEC
        );

        Registry.register(
                BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "highland_meadow_patch"),
                HighlandMeadowPatchFeature.CODEC
        );

        Registry.register(
                BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "karst_outcrop"),
                KarstOutcropFeature.CODEC
        );

        Registry.register(
                BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "karst_ground_patch"),
                KarstGroundPatchFeature.CODEC
        );

        Registry.register(
                BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "karst_animal_trail"),
                KarstAnimalTrailFeature.CODEC
        );

        Registry.register(
                BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(
                        BugsBugsAndMoreBugs.MOD_ID,
                        "amphibolite_deposit"),
                AmphiboliteDepositFeature.CODEC
        );


        Registry.register(BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "bamboo_pool"),
                BambooPoolFeature.CODEC);
        Registry.register(BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "wetland_reeds"),
                WetlandReedsFeature.CODEC);
        Registry.register(BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "elsewhere_flora"),
                ElsewhereFloraFeature.CODEC);
        Registry.register(BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "lava_cleanup"),
                ElsewhereLavaCleanupFeature.CODEC);
    }
}
