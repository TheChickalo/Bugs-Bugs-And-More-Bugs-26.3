package net.chickalo.bugsbugsandmorebugs.entity.client;

import net.chickalo.bugsbugsandmorebugs.entity.ModEntities;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import java.util.concurrent.ThreadLocalRandom;

public class BugsBugsandMoreBugsClient implements ClientModInitializer {
    private int mistTick;

    @Override
    public void onInitializeClient() {
        ModelLayerRegistry.registerModelLayer(ModModelLayerLocations.ANTWORKER, AntWorkerModel::createBodyLayer);
        EntityRenderers.register(ModEntities.ANTWORKER, AntWorkerRenderer::new);

        // A thin layer of visible fog wisps on wet limestone valley floors.
        // Only rendered around the player, client-side: no persistent blocks,
        // worldgen lag, global cloud layer or particle accumulation.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level == null || client.player == null || client.isPaused()) return;
            if (++mistTick % 6 != 0) return;
            var level = client.level;
            var player = client.player;
            if (!level.getBiome(player.blockPosition()).is(ModBiomes.STONE_FOREST)
                    || player.getY() > 96 || player.getY() < 47) return;

            var random = ThreadLocalRandom.current();
            for (int i = 0; i < 2; i++) {
                int x = player.getBlockX() + random.nextInt(-18, 19);
                int z = player.getBlockZ() + random.nextInt(-18, 19);
                BlockPos test = new BlockPos(x, 70, z);
                if (!level.hasChunkAt(test)) continue;
                int floor = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                if (floor < 57 || floor > 84
                        || !level.getBiome(new BlockPos(x, floor, z)).is(ModBiomes.STONE_FOREST))
                    continue;

                double y = floor + 1.8 + random.nextDouble() * 3.2;
                if (!level.getBlockState(BlockPos.containing(x + 0.5, y, z + 0.5)).isAir())
                    continue;
                level.addParticle(ParticleTypes.CLOUD,
                        x + random.nextDouble(), y, z + random.nextDouble(),
                        (random.nextDouble() - 0.5) * 0.008, 0.001,
                        (random.nextDouble() - 0.5) * 0.008);
            }
        });
    }
}
