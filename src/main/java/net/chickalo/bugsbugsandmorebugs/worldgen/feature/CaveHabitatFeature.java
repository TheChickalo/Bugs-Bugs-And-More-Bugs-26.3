package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

/** Decorations for naturally generated upper caves. Never excavates ore, opens the
 * Sundering Horizon, or writes outside the originating chunk. */
public record CaveHabitatFeature(int kind) implements Feature {
    public static final MapCodec<CaveHabitatFeature> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.INT.fieldOf("kind").forGetter(CaveHabitatFeature::kind))
                    .apply(instance, CaveHabitatFeature::new));
    @Override public MapCodec<CaveHabitatFeature> codec() { return CODEC; }

    private static boolean rock(BlockState state) {
        return state.is(ModBlocks.RHYOLITE) || state.is(ModBlocks.SCHIST)
                || state.is(ModBlocks.LIMESTONE) || state.is(ModBlocks.WEATHERED_LIMESTONE)
                || state.is(ModBlocks.AMPHIBOLITE) || state.is(ModBlocks.FLOWSTONE);
    }
    private static boolean buried(WorldGenLevel level, BlockPos pos) {
        return level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, pos.getX(), pos.getZ()) - pos.getY() >= 14;
    }
    private static boolean air(WorldGenLevel level, BlockPos pos) {
        return level.getBlockState(pos).isAir() && level.getFluidState(pos).isEmpty();
    }

    @Override public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        int cx = origin.getX() & ~15, cz = origin.getZ() & ~15;
        int x = origin.getX(), z = origin.getZ();
        if (kind == 0) return terraces(level, origin, random);
        for (int dy=0; dy<=7; dy++) {
            BlockPos at=origin.offset(0,-dy,0);
            if (at.getY() < -56 || at.getY() >= 40 || !buried(level,at)) continue;
            if (!air(level,at) || !rock(level.getBlockState(at.below()))) continue;
            if (!level.getBiome(at).equals(level.getBiome(origin))) continue;
            if (kind == 1) {
                if (at.getY() < 5) return false;
                boolean changed=false;
                for (int h=2;h<=12;h++) {
                    BlockPos ceiling=at.above(h);
                    if (ceiling.getY()>=40) break;
                    if (rock(level.getBlockState(ceiling)) && air(level,ceiling.below())) {
                        BlockState roots=Blocks.HANGING_ROOTS.defaultBlockState();
                        if (roots.canSurvive(level,ceiling.below())) {
                            changed=level.setBlock(ceiling.below(),roots,2);
                            // Soil is a small accent associated with a real root, not a blanket.
                            if (changed && random.nextInt(5)==0)
                                changed |= level.setBlock(at.below(),ModBlocks.ROOTED_LOAM.defaultBlockState(),2);
                        }
                        break;
                    }
                }
                return changed;
            }
            if (kind == 2) {
                if (at.getY()<5) return false;
                boolean changed=level.setBlock(at.below(),Blocks.MOSS_BLOCK.defaultBlockState(),2);
                BlockState mushroom=ModBlocks.GLOWSHROOM.defaultBlockState();
                if (mushroom.canSurvive(level,at)) changed |= level.setBlock(at,mushroom,2);
                return changed;
            }
            if (kind == 3 && at.getY()<0 && level.getBlockState(at.below()).is(ModBlocks.SCHIST))
                return level.setBlock(at.below(),ModBlocks.AMPHIBOLITE.defaultBlockState(),2);
        }
        return false;
    }

    /** Try a staggered group of shallow basins on existing cave relief.
     * Each basin is independently validated, so one obstructed pool does not
     * cancel the entire formation. Work remains inside the origin chunk. */
    private static boolean terraces(WorldGenLevel level, BlockPos origin, RandomSource random) {
        int cx=origin.getX() & ~15, cz=origin.getZ() & ~15;
        int angle=random.nextInt(4);
        int[][] centers={{4,4},{11,4},{11,11},{4,11}};
        int baseY=origin.getY();
        boolean changed=false;
        for(int i=0;i<4;i++) {
            int[] c=centers[(i+angle)%4];
            int x=cx+c[0], z=cz+c[1];
            int radius=2+random.nextInt(2);
            BlockPos floor=null;
            for(int dy=4;dy>=-10;dy--) {
                BlockPos at=new BlockPos(x,baseY+dy,z);
                if(at.getY()<5 || at.getY()>34 || !buried(level,at)) continue;
                if(!level.getBiome(at).equals(level.getBiome(origin))) continue;
                if(air(level,at) && air(level,at.above()) && rock(level.getBlockState(at.below()))) {
                    floor=at; break;
                }
            }
            if(floor==null) continue;
            // Small raised shelves can turn even a flat cave into stepped pools.
            int lift=Math.min(i,2);
            for(int h=lift;h>=0;h--) {
                if(basin(level,floor.above(h),radius,random)) { changed=true; break; }
            }
        }
        return changed;
    }

    private static boolean basin(WorldGenLevel level, BlockPos center, int radius, RandomSource random) {
        java.util.Set<BlockPos> wet=new java.util.HashSet<>();
        java.util.Set<BlockPos> rim=new java.util.HashSet<>();
        java.util.Map<BlockPos,BlockState> writes=new java.util.HashMap<>();
        BlockState deposit=ModBlocks.FLOWSTONE.defaultBlockState();
        BlockState rimState=ModBlocks.RIMSTONE.defaultBlockState();
        int[][] sides={{1,0},{-1,0},{0,1},{0,-1}};
        double phase=random.nextDouble()*Math.PI*2;
        // Interior has an irregular rounded outline. Build every cardinal
        // boundary cell, guaranteeing containment at the water level.
        for(int dx=-radius;dx<=radius;dx++) for(int dz=-radius;dz<=radius;dz++) {
            double a=Math.atan2(dz,dx);
            double limit=radius-0.45+0.22*Math.sin(3*a+phase);
            if(dx*dx+dz*dz<=limit*limit) wet.add(center.offset(dx,0,dz));
        }
        if(wet.size()<5) return false;
        for(BlockPos w:wet) for(int[] s:sides) {
            BlockPos n=w.offset(s[0],0,s[1]);
            if(!wet.contains(n)) rim.add(n);
        }
        java.util.Set<BlockPos> all=new java.util.HashSet<>(wet);
        all.addAll(rim);
        for(BlockPos at:all) {
            if(at.getY()>38 || !buried(level,at)
                    || !level.getBiome(at).equals(level.getBiome(center))
                    || (at.getX()>>4)!=(center.getX()>>4)
                    || (at.getZ()>>4)!=(center.getZ()>>4)) return false;
            if(!air(level,at) || !air(level,at.above())) return false;
            // Fill only air and only until supported by original stone.
            // Never remove ore or overwrite existing features or fluids.
            boolean supported=false;
            for(int d=1;d<=4;d++) {
                BlockPos below=at.below(d);
                if(rock(level.getBlockState(below))) { supported=true; break; }
                if(!air(level,below)) return false;
                writes.put(below,deposit);
            }
            if(!supported) return false;
            if(rim.contains(at)) writes.put(at,rimState);
        }
        for(var e:writes.entrySet()) level.setBlock(e.getKey(),e.getValue(),2);
        for(BlockPos at:wet) level.setBlock(at,Blocks.WATER.defaultBlockState(),2);
        return true;
    }
}
