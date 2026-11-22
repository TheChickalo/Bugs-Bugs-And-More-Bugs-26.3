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
    private int rainforestTick;

    @Override
    public void onInitializeClient() {
        ModelLayerRegistry.registerModelLayer(ModModelLayerLocations.ANTWORKER, AntWorkerModel::createBodyLayer);
        EntityRenderers.register(ModEntities.ANTWORKER, AntWorkerRenderer::new);

        // Year-round cedar rainforest rainfall is a client-side localized
        // visual effect. Vanilla weather itself is world-wide, so we do not
        // change rain/thunder or other biomes' server weather.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(client.level==null || client.player==null || client.isPaused())return;
            if((++rainforestTick & 1)!=0)return;
            var level=client.level;
            var player=client.player;
            if(!level.getBiome(player.blockPosition()).is(ModBiomes.ANCIENT_CEDAR_RAINFOREST))return;
            var random=ThreadLocalRandom.current();
            for(int i=0;i<20;i++){
                int x=player.getBlockX()+random.nextInt(-13,14);
                int z=player.getBlockZ()+random.nextInt(-13,14);
                int y=player.getBlockY()+random.nextInt(-2,13);
                BlockPos at=new BlockPos(x,y,z);
                if(!level.hasChunkAt(at)
                        || !level.getBiome(at).is(ModBiomes.ANCIENT_CEDAR_RAINFOREST)
                        || !level.getBlockState(at).isAir())continue;
                // Drops can appear below canopy; rain feels like humid forest
                // precipitation instead of always falling from a clear sky.
                level.addParticle(ParticleTypes.RAIN,
                        x+random.nextDouble(),y+random.nextDouble(),z+random.nextDouble(),
                        0,-0.25,0);
            }
        });

        // Low, local cloud wisps over Stone Forest valleys, beneath the normal global clouds.
        // Only rendered around the player, client-side: no persistent blocks,
        // worldgen lag, global cloud layer or particle accumulation.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level == null || client.player == null || client.isPaused()) return;
            if (++mistTick % 3 != 0) return;
            var level = client.level;
            var player = client.player;
            if (player.getY() > 160 || player.getY() < 47) return;

            var random = ThreadLocalRandom.current();
            for (int i = 0; i < 12; i++) {
                int x = player.getBlockX() + random.nextInt(-26, 27);
                int z = player.getBlockZ() + random.nextInt(-26, 27);
                BlockPos test = new BlockPos(x, 86, z);
                if (!level.hasChunkAt(test)) continue;
                int floor = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                if (floor < 54 || floor > 92
                        || !level.getBiome(new BlockPos(x, floor, z)).is(ModBiomes.STONE_FOREST))
                    continue;

                // Localized secondary mist band over valleys, not a second global cloud sheet.
                double y = (i % 3 == 0) ? floor + 0.7 + random.nextDouble() * 2.4
                        : 86.0 + random.nextDouble() * 4.0;
                if (y <= floor + 1 || !level.getBlockState(BlockPos.containing(x + 0.5, y, z + 0.5)).isAir())
                    continue;
                level.addParticle(ParticleTypes.CLOUD,
                        x + random.nextDouble(), y, z + random.nextDouble(),
                        (random.nextDouble() - 0.5) * 0.004, 0.0004,
                        (random.nextDouble() - 0.5) * 0.004);
            }
        });
    }
}
