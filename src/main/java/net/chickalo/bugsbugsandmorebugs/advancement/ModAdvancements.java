package net.chickalo.bugsbugsandmorebugs.advancement;

/**
 * ELSEWHERE ADVANCEMENT CATALOG (Minecraft 26.3)
 *
 * Our advancements are JSON data files, NOT registries that need manual Java bootstrap.
 * See: src/main/resources/data/bugsbugsandmorebugs/advancement/
 *
 * HOW TO ADD AN ADVANCEMENT:
 * 1. Create another JSON next to root.json and as_old_as_time.json.
 * 2. Set "parent": "bugsbugsandmorebugs:root" (or another advancement ID).
 * 3. Give it "display" (icon, title, description) and "criteria" (trigger + conditions).
 * 4. Add its title/description translations in assets/bugsbugsandmorebugs/lang/en_us.json.
 * 5. Test with /advancement grant @s only bugsbugsandmorebugs:your_advancement_id
 *
 * KEY EXAMPLES:
 * - minecraft:changed_dimension checks dimension transitions (root.json).
 * - minecraft:inventory_changed checks for obtained items (as_old_as_time.json).
 * - minecraft:location can recognize places using a location predicate.
 *
 * IMPORTANT: An inventory trigger means "obtained", not literally "mined".
 * To detect BREAKING either Ashstone or Shatterstone, a separate Fabric block-break
 * event and a custom criterion are needed. The starter chooses the dependable
 * inventory criterion so it works when digging normally.
 *
 * This class just collects IDs for future Java integrations; no initializer required.
 */
public final class ModAdvancements {
    private ModAdvancements() {}

    public static final String ROOT = "bugsbugsandmorebugs:root";
    public static final String AS_OLD_AS_TIME = "bugsbugsandmorebugs:as_old_as_time";

    // FUTURE:
    // public static final String THE_WORLD_ENDS_HERE = "bugsbugsandmorebugs:the_world_ends_here";
    // public static final String WRITTEN_IN_STONE = "bugsbugsandmorebugs:written_in_stone";
    // public static final String JADED = "bugsbugsandmorebugs:jaded";
}
