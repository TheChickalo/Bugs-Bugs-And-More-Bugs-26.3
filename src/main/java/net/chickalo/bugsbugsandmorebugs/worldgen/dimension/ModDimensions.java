
package net.chickalo.bugsbugsandmorebugs.worldgen.dimension;

import com.mojang.datafixers.util.Pair;
import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TimelineTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.attribute.*;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.List;
import java.util.Optional;

public class ModDimensions {

    public static final ResourceKey<LevelStem> ELSEWHEREDIM_KEY =
            ResourceKey.create(
                    Registries.LEVEL_STEM,
                    Identifier.fromNamespaceAndPath(
                            BugsBugsAndMoreBugs.MOD_ID,
                            "elsewheredim"
                    )
            );

    public static final ResourceKey<Level> ELSEWHEREDIM_LEVEL_KEY =
            ResourceKey.create(
                    Registries.DIMENSION,
                    Identifier.fromNamespaceAndPath(
                            BugsBugsAndMoreBugs.MOD_ID,
                            "elsewheredim"
                    )
            );

    public static final ResourceKey<DimensionType> ELSEWHERE_DIM_TYPE =
            ResourceKey.create(
                    Registries.DIMENSION_TYPE,
                    Identifier.fromNamespaceAndPath(
                            BugsBugsAndMoreBugs.MOD_ID,
                            "elsewheredim_type"
                    )
            );


    public static final ResourceKey<NoiseGeneratorSettings> ELSEWHERE_NOISE_SETTINGS_KEY =
            ResourceKey.create(
                    Registries.NOISE_SETTINGS,
                    Identifier.fromNamespaceAndPath(
                            BugsBugsAndMoreBugs.MOD_ID,
                            "elsewhere"
                    )
            );


    public static void bootstrapType(
            BootstrapContext<DimensionType> context
    ) {
        var timelines = context.lookup(Registries.TIMELINE);
        var clocks = context.lookup(Registries.WORLD_CLOCK);
        var blocks = context.lookup(Registries.BLOCK);

        context.register(
                ELSEWHERE_DIM_TYPE,
                new DimensionType(
                        false, // Fixed time
                        true,  // Skylight
                        false, // Ceiling
                        false, // Ender Dragon fight
                        1.0,   // Coordinate scale
                        -128,   // Minimum Y
                        448,   // Height
                        448,   // Logical height

                        blocks.getOrThrow(
                                BlockTags.INFINIBURN_OVERWORLD
                        ),

                        0.0f, // Ambient light

                        new DimensionType.MonsterSettings(
                                ConstantInt.of(0),
                                0
                        ),

                        DimensionType.Skybox.OVERWORLD,
                        CardinalLighting.Type.DEFAULT,

                        EnvironmentAttributeMap.builder()
                                .set(
                                        EnvironmentAttributes.FOG_COLOR,
                                        ARGB.vector3fFromRGB24(0xD5E5DD)
                                )
                                .set(
                                        EnvironmentAttributes.SKY_COLOR,
                                        ARGB.vector3fFromRGB24(0x83D6DE)
                                )
                                .set(
                                        EnvironmentAttributes.BACKGROUND_MUSIC,
                                        BackgroundMusic.OVERWORLD
                                )
                                .set(
                                        EnvironmentAttributes.AMBIENT_SOUNDS,
                                        AmbientSounds.LEGACY_CAVE_SETTINGS
                                )
                                .build(),

                        timelines.getOrThrow(
                                TimelineTags.IN_OVERWORLD
                        ),

                        Optional.of(
                                clocks.getOrThrow(WorldClocks.OVERWORLD)
                        )
                )
        );
    }

    public static void bootstrapStem(
            BootstrapContext<LevelStem> context
    ) {
        var biomes = context.lookup(Registries.BIOME);
        var dimensionTypes = context.lookup(
                Registries.DIMENSION_TYPE
        );
        var noiseSettings = context.lookup(
                Registries.NOISE_SETTINGS
        );

        var biomeSource = MultiNoiseBiomeSource.createFromList(
                new Climate.ParameterList<>(
                        List.of(
                                Pair.of(
                                        Climate.parameters(
                                                -0.3F, 0.2F, 0.25F,
                                                0.0F, 0.0F, 0.0F, 0.0F
                                        ),
                                        biomes.getOrThrow(
                                                ModBiomes.MAPLE_FOREST
                                        )
                                ),
                                Pair.of(
                                        Climate.parameters(
                                                -0.3F, -0.35F, 0.25F,
                                                0.3F, 0.0F, 0.0F, 0.0F
                                        ),
                                        biomes.getOrThrow(
                                                ModBiomes.HIGHLAND_KARST
                                        )
                                )
                        )
                )
        );

        var generator = new NoiseBasedChunkGenerator(
                biomeSource,
                noiseSettings.getOrThrow(
                        ELSEWHERE_NOISE_SETTINGS_KEY
                )
        );


        context.register(
                ELSEWHEREDIM_KEY,
                new LevelStem(
                        dimensionTypes.getOrThrow(
                                ELSEWHERE_DIM_TYPE
                        ),
                        generator
                )
        );
    }
}
