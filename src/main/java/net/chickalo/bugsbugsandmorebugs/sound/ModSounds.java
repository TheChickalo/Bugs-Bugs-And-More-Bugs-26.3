package net.chickalo.bugsbugsandmorebugs.sound;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {

    public static final Holder.Reference<SoundEvent> AMEISENHAUFEN = registerJukeboxSong("ameisenhaufen");

    private static Holder.Reference<SoundEvent> registerJukeboxSong(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name);
        return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void registerSound() {
        BugsBugsAndMoreBugs.LOGGER.info("Registering sounds for " + BugsBugsAndMoreBugs.MOD_ID);
    }
}
