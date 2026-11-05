package net.chickalo.bugsbugsandmorebugs.worldgen.tree;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class ModFoliagePlacerTypes {
    public static final FoliagePlacerType<MapleFoliagePlacer> MAPLE_FOLIAGE_PLACER_FOLIAGE_PLACER_TYPE =
            Registry.register(BuiltInRegistries.FOLIAGE_PLACER_TYPE, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "maple_foliage_placer"),
                    new FoliagePlacerType<>(MapleFoliagePlacer.CODEC));

    public static void registerModFoliagePlacerTypes() {
        BugsBugsAndMoreBugs.LOGGER.info("Registering ModFoliagePlacerTypes for " + BugsBugsAndMoreBugs.MOD_ID);
    }
}
