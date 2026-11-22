package net.chickalo.bugsbugsandmorebugs.worldgen.tree;
import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
public final class ModTreeDecoratorTypes {
    public static final TreeDecoratorType<WisteriaBlossomDecorator> WISTERIA_BLOSSOMS =
            Registry.register(BuiltInRegistries.TREE_DECORATOR_TYPE,
                    Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "wisteria_blossoms"),
                    new TreeDecoratorType<>(WisteriaBlossomDecorator.CODEC));
    public static void register() {
        BugsBugsAndMoreBugs.LOGGER.info("Registering Wisteria tree decorator");
    }
}
