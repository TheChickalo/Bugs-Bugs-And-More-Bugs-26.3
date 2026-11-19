package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Buried, tuff-like amphibolite lenses that can form ONLY in schist.
 * No surface strata, no volcanic rock replacement, and no large exposed outcrops.
 */
public record AmphiboliteDepositFeature() implements Feature {
    public static final MapCodec<AmphiboliteDepositFeature> CODEC =
            MapCodec.unit(new AmphiboliteDepositFeature());

    @Override
    public MapCodec<AmphiboliteDepositFeature> codec() { return CODEC; }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator,
                         RandomSource random, BlockPos origin) {
        // The surrounding solid schist, not a biome name, determines eligibility.
        if (origin.getY() > -18 || origin.getY() < -60
                || !level.getBlockState(origin).is(ModBlocks.SCHIST)) return false;

        // Even on cliffs, underhangs and unusual mountains, keep lenses deep
        // enough that they cannot become a surface block palette.
        int surface = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,
                origin.getX(), origin.getZ());
        if (surface - origin.getY() < 18) return false;

        int rx = 2 + random.nextInt(3);
        int ry = 1 + random.nextInt(3);
        int rz = 2 + random.nextInt(3);
        boolean placed = false;

        for (int dx = -rx; dx <= rx; dx++) {
            for (int dz = -rz; dz <= rz; dz++) {
                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;
                int localSurface = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                for (int dy = -ry; dy <= ry; dy++) {
                    int y = origin.getY() + dy;
                    if (y < level.getMinY() || y > -16 || localSurface - y < 16) continue;

                    // Rounded, slightly irregular pocket rather than a perfect cuboid.
                    double distance = (double) dx * dx / (rx * rx)
                            + (double) dy * dy / (ry * ry)
                            + (double) dz * dz / (rz * rz);
                    if (distance > 1.02 - random.nextDouble() * 0.18) continue;

                    BlockPos at = new BlockPos(x, y, z);
                    BlockState countryRock = level.getBlockState(at);
                    if (!countryRock.is(ModBlocks.SCHIST)) continue;
                    placed |= level.setBlock(at, ModBlocks.AMPHIBOLITE.defaultBlockState(), 2);
                }
            }
        }
        return placed;
    }
}
