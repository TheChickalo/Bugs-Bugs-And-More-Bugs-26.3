package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.world.level.block.FarmlandBlock;

/**
 * Vanilla farmland hydration and trampling, with loam as the fallback soil.
 *
 * Minecraft 26.3's FarmlandBlock accepts the base block in its constructor,
 * so dry farmland, unsuitable cover and trampling revert directly to loam.
 */
public class TilledLoamBlock extends FarmlandBlock {
    public TilledLoamBlock(Properties properties) {
        super(ModBlocks.LOAM, properties);
    }
}
