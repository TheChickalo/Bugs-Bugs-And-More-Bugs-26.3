package net.chickalo.bugsbugsandmorebugs.mixin;

import net.minecraft.client.resources.SplashManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;

@Mixin(SplashManager.class)
public abstract class SplashManagerMixin {

    @Shadow
    private List<Component> splashes;

    @Inject(
            method = "apply(Ljava/util/List;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("TAIL")
    )
    private void bugsbugsandmorebugs$appendSplashes(
            List<Component> preparations,
            ResourceManager resourceManager,
            ProfilerFiller profiler,
            CallbackInfo ci
    ) {
        List<Component> merged = new ArrayList<>(this.splashes);

        String[] customSplashes = {
                "No two glowshrooms are alike!",
                "Maple you stay a while!",
                "Bugs, Bugs, and More Bugs!",
                "Totally normal mushrooms.",
                "日本語上手ですね!",
                "Keep your mods #niche. Gatekeep them!",
                "Uhm, is this still hip with the kids?",
                "Hardcore? I hardly know her!",
                "Erm, chat, am I mutted?",
                "478868574082066804",
                "Waxed Exposed Cut Copper Stairs!",
                "I could put anything I want here, not like you read these...",
                "Modder? I hardly know her!",
                "HEY EVERYONE! THIS USER PLAYS MODDED MINECRAFT!",
                "A.K.A. Journey to the Motherland.",
                "Yeah, I'm something of a coder myself, y'know.",
                "Erm, my mom said the mod was good, so...",
                "This is totally a vanilla splash text!",
                "No two glowshrooms are alike!",
                "Maple you stay a while!",
                "Bugs, Bugs, and More Bugs!",
                "Totally normal mushrooms.",
                "日本語上手ですね!",
                "Keep your mods #niche. Gatekeep them!",
                "Uhm, is this still hip with the kids?",
                "Hardcore? I hardly know her!",
                "Erm, chat, am I mutted?",
                "478868574082066804",
                "Waxed Exposed Cut Copper Stairs!",
                "I could put anything I want here, not like you read these...",
                "Modder? I hardly know her!",
                "HEY EVERYONE! THIS USER PLAYS MODDED MINECRAFT!",
                "A.K.A. Journey to the Motherland.",
                "Yeah, I'm something of a coder myself, y'know.",
                "Erm, my mom said the mod was good, so...",
                "This is totally a vanilla splash text!",
                "No two glowshrooms are alike!",
                "Maple you stay a while!",
                "Bugs, Bugs, and More Bugs!",
                "Totally normal mushrooms.",
                "日本語上手ですね!",
                "Keep your mods #niche. Gatekeep them!",
                "Uhm, is this still hip with the kids?",
                "Hardcore? I hardly know her!",
                "Erm, chat, am I mutted?",
                "478868574082066804",
                "Waxed Exposed Cut Copper Stairs!",
                "I could put anything I want here, not like you read these...",
                "Modder? I hardly know her!",
                "HEY EVERYONE! THIS USER PLAYS MODDED MINECRAFT!",
                "A.K.A. Journey to the Motherland.",
                "Yeah, I'm something of a coder myself, y'know.",
                "Erm, my mom said the mod was good, so...",
                "This is totally a vanilla splash text!",
                "Limestone, now made with real limes!",
                "Limestone, now made with real limes!",
                "Limestone, now made with real limes!",
                "Limestone, now made with real limes!",
                "Limestone, now made with real limes!",
                "Limestone, now made with real limes!",
                "Limestone? What's next, lemonstone?",
                "Limestone? What's next, lemonstone?",
                "Limestone? What's next, lemonstone?",
                "Limestone? What's next, lemonstone?",
                "Limestone? What's next, lemonstone?",
                "What, you don't find my jokes funny?",
                "What, you don't find my jokes funny?",
                "What, you don't find my jokes funny?",
                "What, you don't find my jokes funny?",
                "What, you don't find my jokes funny?",
                "Does your mother know you play these kinds of games?",
                "Does your mother know you play these kinds of games?",
                "Does your mother know you play these kinds of games?",
                "Does your mother know you play these kinds of games?",
                "Ehh, you get the schist.",
                "Ehh, you get the schist.",
                "Ehh, you get the schist.",
                "Ehh, you get the schist.",
                "Ehh, you get the schist."
        };

        for (String message : customSplashes) {
            for (int i = 1; i < 20; i++) {
                merged.add(
                        Component.literal(message)
                                .withStyle(ChatFormatting.YELLOW)
                );            }
        };

        this.splashes = List.copyOf(merged);
    }
}
