package net.chickalo.bugsbugsandmorebugs.tags;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> MAPLE_LOGS = createTag("maple_logs");

        private static final TagKey<Block> createTag(String name) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> MAPLE_LOGS = createTag("maple_logs");

        private static TagKey<Item> createTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name));
        }
    }
}
