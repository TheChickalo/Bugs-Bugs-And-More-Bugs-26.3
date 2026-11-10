package net.chickalo.bugsbugsandmorebugs.worldgen.biome.region;

import com.mojang.datafixers.util.Pair;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class NetherRegion extends Region {

    public NetherRegion(Identifier name, RegionType type, int weight) {
        super(name, RegionType.NETHER, weight);
    }


}
