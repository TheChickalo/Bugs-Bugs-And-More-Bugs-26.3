package net.chickalo.bugsbugsandmorebugs.entity.client;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.ARGB;
public class ElsewhereSoilClient implements ClientModInitializer {
    @Override public void onInitializeClient() {
        BlockColorRegistry.register(List.of(new BlockTintSource() {
            @Override public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                return ARGB.opaque(BiomeColors.getAverageGrassColor(level, pos));
            }
            @Override public int color(BlockState state) { return ARGB.opaque(0x79A85C); }
        }), ModBlocks.GRASSY_LOAM);
    }
}
