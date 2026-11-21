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

        // A second, LOCAL cloud-bank effect rather than lowering the vanilla
        // cloud sheet for the whole camera. Keep the dimension's Y=184 layer.
        // Particles appear only over low Stone Forest terrain, including when
        // the player is standing just outside the biome boundary.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level == null || client.player == null || client.isPaused()) return;
            if (++mistTick % 3 != 0) return;

            var level = client.level;
            var player = client.player;
            if (player.getY() < 45 || player.getY() > 130) return;

            var random = ThreadLocalRandom.current();
            for (int i = 0; i < 10; i++) {
                int x = player.getBlockX() + random.nextInt(-21, 22);
                int z = player.getBlockZ() + random.nextInt(-21, 22);
                BlockPos probe = new BlockPos(x, player.getBlockY(), z);
                if (!level.hasChunkAt(probe)) continue;

                int floor = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                if (floor < 52 || floor > 93
                        || !level.getBiome(new BlockPos(x, floor, z)).is(ModBiomes.STONE_FOREST)) {
                    continue;
                }

                // Four in five wisps float near the creek bed; an occasional
                // higher wisp creates a soft second bank above the valley.
                double y = random.nextInt(5) == 0
                        ? floor + 4.0 + random.nextDouble() * 5.0
                        : floor + 0.6 + random.nextDouble() * 2.8;
                double px = x + random.nextDouble();
                double pz = z + random.nextDouble();
                if (!level.getBlockState(BlockPos.containing(px, y, pz)).isAir()) continue;

                level.addParticle(ParticleTypes.CLOUD, px, y, pz,
                        (random.nextDouble() - 0.5) * 0.004, 0.0004,
                        (random.nextDouble() - 0.5) * 0.004);
            }
        });
    }
}
