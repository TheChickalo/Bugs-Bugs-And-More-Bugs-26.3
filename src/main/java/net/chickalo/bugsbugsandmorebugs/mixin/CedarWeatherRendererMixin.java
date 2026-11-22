package net.chickalo.bugsbugsandmorebugs.mixin;

import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomeFamilies;
import net.chickalo.bugsbugsandmorebugs.worldgen.dimension.ModDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Uses Minecraft's actual weather columns and textures with a local,
 * biome-specific intensity. Does NOT change global server weather or time.
 *
 * The normal renderer early-exits when getRainLevel() is zero, so its own
 * extracted intensity must be provided here. The second redirect filters the
 * individual columns, preventing visual rain in neighboring Elsewhere biomes.
 */
@Mixin(WeatherEffectRenderer.class)
public abstract class CedarWeatherRendererMixin {
    private static boolean bbb$isElsewhere(ClientLevel level) {
        return level.dimension().equals(ModDimensions.ELSEWHEREDIM_LEVEL_KEY);
    }

    @Redirect(method = "extractRenderState",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"))
    private float bbb$localRainIntensity(ClientLevel level, float partialTicks) {
        if (!bbb$isElsewhere(level)) return level.getRainLevel(partialTicks);
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return 0.0F;
        BlockPos cameraPos = client.player.blockPosition();
        return ModBiomeFamilies.cedar(level.getBiome(cameraPos)) ? 1.0F : 0.0F;
    }

    @Redirect(method = "extractRenderState",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    private Biome.Precipitation bbb$onlyCedarColumns(ClientLevel level, BlockPos pos) {
        if (bbb$isElsewhere(level) && !ModBiomeFamilies.cedar(level.getBiome(pos))) {
            return Biome.Precipitation.NONE;
        }
        return level.getPrecipitationAt(pos);
    }
}
