package net.chickalo.bugsbugsandmorebugs.datagen;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.chickalo.bugsbugsandmorebugs.sound.ModSounds;
import net.fabricmc.fabric.api.client.datagen.v1.builder.SoundTypeBuilder;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModSoundsProvider extends FabricSoundsProvider {
    public ModSoundsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registryLookup, SoundExporter exporter) {
        exporter.add(ModSounds.AMEISENHAUFEN, SoundTypeBuilder.of(ModSounds.AMEISENHAUFEN.value())
                .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(
                        Identifier.fromNamespaceAndPath(BugsBugsAndMoreBugs.MOD_ID, "ameisenhaufen")
                ).stream(true))
        );



        }

    @Override
    public String getName() {
        return "";
    }
}
