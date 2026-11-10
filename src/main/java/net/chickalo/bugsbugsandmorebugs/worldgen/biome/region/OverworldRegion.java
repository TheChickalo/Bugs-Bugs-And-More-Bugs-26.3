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

        // Give Maple Forest a broader climate footprint. The old narrow,
        // dry-only humidity band made its transitions look abrupt beside
        // neighboring cold biomes and restricted where it could appear.
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.span(
                        ParameterUtils.Temperature.COOL,
                        ParameterUtils.Temperature.FROZEN))
                .humidity(ParameterUtils.Humidity.span(
                        ParameterUtils.Humidity.ARID,
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
