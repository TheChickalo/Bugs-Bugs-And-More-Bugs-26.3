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
                || state.is(ModBlocks.AMPHIBOLITE);
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
        if (kind == 0 && (x-cx<5 || x-cx>10 || z-cz<5 || z-cz>10)) return false;
        for (int dy=0; dy<=7; dy++) {
            BlockPos at=origin.offset(0,-dy,0);
            if (at.getY() < -18 || at.getY() >= 40 || !buried(level,at)) continue;
            if (!air(level,at) || !rock(level.getBlockState(at.below()))) continue;
            if (!level.getBiome(at).equals(level.getBiome(origin))) continue;
            if (kind == 0) return at.getY() >= 5 && terrace(level,at,random);
            if (kind == 1) {
                if (at.getY() < 5) return false;
                boolean changed=level.setBlock(at.below(),ModBlocks.ROOTED_LOAM.defaultBlockState(),2);
                for (int h=2;h<=8;h++) {
                    BlockPos ceiling=at.above(h);
                    if (ceiling.getY()>=40) break;
                    if (rock(level.getBlockState(ceiling)) && air(level,ceiling.below())) {
                        BlockState roots=Blocks.HANGING_ROOTS.defaultBlockState();
                        if (roots.canSurvive(level,ceiling.below())) changed |= level.setBlock(ceiling.below(),roots,2);
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
            if (kind == 3 && at.getY()<4 && level.getBlockState(at.below()).is(ModBlocks.SCHIST))
                return level.setBlock(at.below(),ModBlocks.AMPHIBOLITE.defaultBlockState(),2);
        }
        return false;
    }

    /** Two contained, shallow, nested rimstone pools. The entire footprint is
     * validated before writes; natural terrain and existing fluids are not removed.
     * Full limestone blocks are an intentionally coarse first-pass placeholder. */
    private static boolean terrace(WorldGenLevel level, BlockPos center, RandomSource random) {
        int rx=4, rz=3+random.nextInt(2);
        double phase=random.nextDouble()*Math.PI*2;
        java.util.Map<BlockPos,BlockState> writes=new java.util.HashMap<>();
        java.util.Set<BlockPos> water=new java.util.HashSet<>();
        BlockState stone=ModBlocks.LIMESTONE.defaultBlockState();
        BlockState rim=ModBlocks.WEATHERED_LIMESTONE.defaultBlockState();
        // Center pool has water at y; the surrounding pool is one block higher.
        // Dry rim cells are solid at both levels to contain the elevated pool.
        for (int dx=-5;dx<=5;dx++) for (int dz=-5;dz<=5;dz++) {
            double angle=Math.atan2(dz,dx);
            double q=Math.sqrt((double)dx*dx/(rx*rx)+(double)dz*dz/(rz*rz))
                    +0.06*Math.sin(3*angle+phase);
            if (q>1.13) continue;
            BlockPos at=center.offset(dx,0,dz);
            if (!buried(level,at) || !level.getBiome(at).equals(level.getBiome(center))) return false;
            BlockPos below=at.below();
            if (!rock(level.getBlockState(below))) {
                if (!air(level,below) || !rock(level.getBlockState(below.below()))) return false;
                writes.put(below,stone);
            }
            if (!air(level,at) || !air(level,at.above()) || !air(level,at.above(2))) return false;
            if(q<=0.32) { water.add(at); }
            else if(q<=0.60 || q>=0.85) { writes.put(at,stone); writes.put(at.above(),rim); }
            else { writes.put(at,stone); water.add(at.above()); }
        }
        // Reject any discretized diagonal/irregular edge that would leave water uncontained.
        if (water.size()<5) return false;
        for(BlockPos at:water) {
            if(!writes.containsKey(at.below()) && !rock(level.getBlockState(at.below()))) return false;
            for(int[] off:new int[][]{{1,0},{-1,0},{0,1},{0,-1}}) {
                BlockPos neighbor=at.offset(off[0],0,off[1]);
                if(!water.contains(neighbor) && !writes.containsKey(neighbor)
                        && !rock(level.getBlockState(neighbor))) return false;
            }
        }
        for(var entry:writes.entrySet()) level.setBlock(entry.getKey(),entry.getValue(),2);
        for(BlockPos at:water) level.setBlock(at,Blocks.WATER.defaultBlockState(),2);
        return true;
    }
}
