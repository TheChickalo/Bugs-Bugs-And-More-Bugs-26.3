package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Small, contained volcanic seeps with lava only in fully enclosed rock cells.
 * Lapilli, basalt, and magma surround some vents. No lava river or loose pools.
 */
public record VolcanicVentFeature(boolean foothills) implements Feature {
    public static final MapCodec<VolcanicVentFeature> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.BOOL.fieldOf("foothills").forGetter(VolcanicVentFeature::foothills)
            ).apply(instance, VolcanicVentFeature::new));

    @Override public MapCodec<VolcanicVentFeature> codec() { return CODEC; }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator,
                         RandomSource random, BlockPos origin) {
        int x=origin.getX(), z=origin.getZ();
        int y=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z)-1;
        if(y<65 || y>level.getMaxY()-4) return false;
        BlockPos center=new BlockPos(x,y,z);
        if (!belongs(level, center) || !bareRock(level, center)) return false;

        int radius=foothills ? 2 + random.nextInt(2) : 2 + random.nextInt(3);
        int changed=0;
        for (int dx=-radius; dx<=radius; dx++) {
            for (int dz=-radius; dz<=radius; dz++) {
                double q=(dx*dx+dz*dz)/(double)(radius*radius);
                if(q>1.1 || (q>0.65 && random.nextFloat()<0.26F))continue;
                int bx=x+dx,bz=z+dz;
                int by=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,bx,bz)-1;
                if(Math.abs(by-y)>2)continue;
                BlockPos at=new BlockPos(bx,by,bz);
                if(!belongs(level,at) || !bareRock(level,at))continue;
                float roll=random.nextFloat();
                BlockState next=roll<0.13F
                        ? Blocks.MAGMA_BLOCK.defaultBlockState()
                        : roll<0.39F ? ModBlocks.LAPILLI.defaultBlockState()
                        : roll<0.56F ? Blocks.BASALT.defaultBlockState()
                        : roll<0.72F ? ModBlocks.DACITE.defaultBlockState()
                        : ModBlocks.RHYOLITE.defaultBlockState();
                if(!level.getBlockState(at).equals(next)
                        && level.setBlock(at,next,2))changed++;
            }
        }

        // Occasional single lava source in rock below the air surface. The
        // surrounding four cells must remain solid at lava's exact Y to prevent
        // exposed flowing sheets, especially on steep mountain slopes.
        if(random.nextFloat()<(foothills?0.22F:0.40F)) {
            BlockPos pool=new BlockPos(x,y,z);
            if (sealed(level,pool)) {
                for(Direction dir:Direction.Plane.HORIZONTAL) {
                    BlockPos edge=pool.relative(dir);
                    if (bareRock(level,edge)) {
                        level.setBlock(edge, random.nextBoolean()
                                ? Blocks.MAGMA_BLOCK.defaultBlockState()
                                : Blocks.BASALT.defaultBlockState(),2);
                    }
                }
                if (level.setBlock(pool,Blocks.LAVA.defaultBlockState(),2))changed++;
            }
        }
        return changed>0;
    }

    private boolean belongs(WorldGenLevel level,BlockPos at) {
        return level.getBiome(at).is(foothills
                ? ModBiomes.ASHEN_FOOTHILLS:ModBiomes.VOLCANIC_PEAK);
    }

    private static boolean bareRock(WorldGenLevel level,BlockPos at) {
        if(!level.getFluidState(at).isEmpty()
                || !level.getFluidState(at.above()).isEmpty()
                || !level.getBlockState(at.above()).isAir())return false;
        BlockState old=level.getBlockState(at);
        return old.is(ModBlocks.RHYOLITE)||old.is(ModBlocks.DACITE)
                ||old.is(ModBlocks.LAPILLI)||old.is(Blocks.BASALT)
                ||old.is(Blocks.MAGMA_BLOCK);
    }

    private static boolean sealed(WorldGenLevel level,BlockPos at) {
        if(!bareRock(level,at) || level.getBlockState(at.below()).isAir())return false;
        for(Direction dir:Direction.Plane.HORIZONTAL) {
            BlockPos neighbor=at.relative(dir);
            BlockState rock=level.getBlockState(neighbor);
            if(!level.getFluidState(neighbor).isEmpty()
                    || rock.isAir() || rock.canBeReplaced()
                    || !(rock.is(ModBlocks.RHYOLITE)||rock.is(ModBlocks.DACITE)
                      ||rock.is(ModBlocks.LAPILLI)||rock.is(Blocks.BASALT)
                      ||rock.is(Blocks.MAGMA_BLOCK)))return false;
        }
        return true;
    }
}
