package net.chickalo.bugsbugsandmorebugs.worldgen.tree;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class ModTrunkPlacerTypes {
    public static final TrunkPlacerType<MapleTrunkPlacer> MAPLE_TRUNK_PLACER =
            Registry.register(BuiltInRegistries.TRUNK_PLACER_TYPE, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID,
                    "maple_trunk_placer"), new TrunkPlacerType<>(MapleTrunkPlacer.CODEC));

    public static void registerModTrunkPlacerTypes() {
        BugsBugsAndMoreBugs.LOGGER.info("Registering ModTrunkPlacerTypes for " + BugsBugsAndMoreBugs.MOD_ID);
    }
}