package net.chickalo.bugsbugsandmorebugs.worldgen.biome.region;

import com.mojang.datafixers.util.Pair;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ParameterUtils;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;

import java.util.function.Consumer;

public class OverworldRegion extends Region {
    public OverworldRegion(Identifier name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();

        // Temperature presets are ordered from coldest to warmest numerically.
        // FROZEN -> COOL creates a real interval; COOL -> FROZEN collapses to
        // the shared boundary at -0.45 and makes the biome extremely restricted.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.span(
                        ParameterUtils.Temperature.FROZEN,
                        ParameterUtils.Temperature.COOL))
                .humidity(ParameterUtils.Humidity.span(
                        ParameterUtils.Humidity.NORMAL,
                        ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.INLAND)
                .erosion(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_2)
                .depth(ParameterUtils.Depth.SURFACE, ParameterUtils.Depth.FLOOR)
                .weirdness(ParameterUtils.Weirdness.FULL_RANGE)
                .offset(0f)
                .build()
                .forEach(parameterPoint -> builder.add(parameterPoint, ModBiomes.MAPLE_FOREST));

        builder.build().forEach(mapper);
    }
}
