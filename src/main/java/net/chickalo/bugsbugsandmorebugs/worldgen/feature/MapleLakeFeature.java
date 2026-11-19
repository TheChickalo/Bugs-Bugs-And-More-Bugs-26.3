package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Small sealed woodland ponds, wholly inside the origin chunk. This avoids
 * partial cross-chunk structures and the 32x32 all-or-nothing validation that
 * made the previous lake feature almost always return false.
 */
public record MapleLakeFeature() implements Feature {
    public static final MapCodec<MapleLakeFeature> CODEC = MapCodec.unit(new MapleLakeFeature());
    private static final BlockState WATER = Blocks.WATER.defaultBlockState();
    private static final int SIZE = 16;

    @Override public MapCodec<MapleLakeFeature> codec() { return CODEC; }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        int chunkX = Math.floorDiv(origin.getX(), 16) * 16;
        int chunkZ = Math.floorDiv(origin.getZ(), 16) * 16;
        int centerX = chunkX + 7 + random.nextInt(3);
        int centerZ = chunkZ + 7 + random.nextInt(3);
        double rx = 3.0 + random.nextDouble() * 2.0;
        double rz = 3.0 + random.nextDouble() * 2.0;
        double phase = random.nextDouble() * Math.PI * 2;
        boolean[][] inside = new boolean[SIZE][SIZE];
        int[][] top = new int[SIZE][SIZE];
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        int count = 0;
        for (int dx = 1; dx < SIZE - 1; dx++) {
            for (int dz = 1; dz < SIZE - 1; dz++) {
                int x = chunkX + dx, z = chunkZ + dz;
                double xx = (x - centerX) / rx;
                double zz = (z - centerZ) / rz;
                double angle = Math.atan2(zz, xx);
                double outline = 1.0 + 0.07 * Math.sin(angle * 3 + phase);
                if (xx * xx + zz * zz > outline * outline) continue;
                int ground = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z) - 1;
                BlockPos gp = new BlockPos(x, ground, z);
                BlockState soil = level.getBlockState(gp);
                if (!level.getFluidState(gp).isEmpty() || !level.getFluidState(gp.above()).isEmpty()
                        || (!soil.is(Blocks.GRASS_BLOCK) && !soil.is(Blocks.DIRT)
                        && !soil.is(ModBlocks.GRASSY_LOAM) && !soil.is(ModBlocks.LOAM)
                        && !soil.is(Blocks.PODZOL) && !soil.is(Blocks.COARSE_DIRT)
                        && !soil.is(Blocks.MUD) && !soil.is(Blocks.CLAY)
                        && !soil.is(ModBlocks.MOSSY_RHYOLITE) && !soil.is(ModBlocks.RHYOLITE))) return false;
                top[dx][dz] = ground;
                inside[dx][dz] = true;
                min = Math.min(min, ground);
                max = Math.max(max, ground);
                count++;
            }
        }
        if (count < 20 || max - min > 8 || min < level.getMinY() + 6
                || min >= level.getMaxY() - 6 || min < 60) return false;
        final int waterY = min;
        // A pond whose rim is lower than its waterline would spill immediately.
        for (int dx = 1; dx < SIZE-1; dx++) for (int dz = 1; dz < SIZE-1; dz++) {
            if (!inside[dx][dz]) continue;
            for (int[] off : new int[][]{{1,0},{-1,0},{0,1},{0,-1}}) {
                int nx=dx+off[0], nz=dz+off[1];
                if (nx < 0 || nx >= SIZE || nz < 0 || nz >= SIZE) return false;
                if (!inside[nx][nz] && level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG,
                        chunkX+nx,chunkZ+nz)-1 < waterY) return false;
            }
        }
        for (int dx = 1; dx < SIZE-1; dx++) for (int dz = 1; dz < SIZE-1; dz++) {
            if (!inside[dx][dz]) continue;
            int x=chunkX+dx,z=chunkZ+dz;
            double d=Math.sqrt(Math.pow((x-centerX)/rx,2)+Math.pow((z-centerZ)/rz,2));
            int depth=1+(int)Math.floor(Math.max(0.0,1.0-d)*2.5);
            int floor=waterY-depth;
            // Retain natural, irregular soil walls. Only carve the actual pond footprint.
            for(int y=floor+1;y<=top[dx][dz]+1;y++)
                level.setBlock(new BlockPos(x,y,z), y<=waterY?WATER:Blocks.AIR.defaultBlockState(),2);
            BlockState bed = random.nextInt(5)==0 ? Blocks.CLAY.defaultBlockState()
                    : random.nextInt(4)==0 ? Blocks.GRAVEL.defaultBlockState()
                    : Blocks.MUD.defaultBlockState();
            for(int y=floor-2;y<=floor;y++) level.setBlock(new BlockPos(x,y,z),bed,2);
            if(depth>=2 && random.nextInt(5)==0)
                level.setBlock(new BlockPos(x,floor+1,z),Blocks.SEAGRASS.defaultBlockState(),2);
        }
        return true;
    }
}
