package net.chickalo.bugsbugsandmorebugs.worldgen.density;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class ModDensityFunctionTypes {
    private ModDensityFunctionTypes() {}

    public static void register() {
        Registry.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE,
                Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "bounded_sky_islands"),
                SkyArchipelagoDensityFunction.CODEC);
    }
}
