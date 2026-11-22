package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.block.custom.FlexibleWetlandPlantBlock;
import net.chickalo.bugsbugsandmorebugs.block.custom.BracketFungusBlock;
import net.chickalo.bugsbugsandmorebugs.block.custom.ExtendedForestLeavesBlock;
import net.chickalo.bugsbugsandmorebugs.block.custom.HangingAccentBlock;
import net.chickalo.bugsbugsandmorebugs.block.custom.VariableHeightPlantBlock;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeafLitterBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.Fluids;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

/**
 * Five species, three locally varying habitat motifs per species.
 * Uses the existing Elsewhere terrain; NEVER carves new water or raises banks.
 * kind: 0=wisteria, 1=willow, 2=cedar, 3=persimmon, 4=mimosa.
 */
public record ForestHabitatFeature(int kind) implements Feature {
    public static final MapCodec<ForestHabitatFeature> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.INT.fieldOf("kind").forGetter(ForestHabitatFeature::kind)
            ).apply(instance, ForestHabitatFeature::new));

    private static final Direction[] SIDES = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    @Override public MapCodec<ForestHabitatFeature> codec() { return CODEC; }

    private Block log() {
        return switch (kind) {
            case 0 -> ModBlocks.WISTERIA_LOG;
            case 1 -> ModBlocks.WILLOW_LOG;
            case 2 -> ModBlocks.CEDAR_LOG;
            case 3 -> ModBlocks.PERSIMMON_LOG;
            default -> ModBlocks.MIMOSA_LOG;
        };
    }
    private Block leaves() {
        return switch (kind) {
            case 0 -> ModBlocks.WISTERIA_LEAVES;
            case 1 -> ModBlocks.WILLOW_LEAVES;
            case 2 -> ModBlocks.CEDAR_LEAVES;
            case 3 -> ModBlocks.PERSIMMON_LEAVES;
            default -> ModBlocks.MIMOSA_LEAVES;
        };
    }
    private Block sapling() {
        return switch (kind) {
            case 0 -> ModBlocks.WISTERIA_SAPLING;
            case 1 -> ModBlocks.WILLOW_SAPLING;
            case 2 -> ModBlocks.CEDAR_SAPLING;
            case 3 -> ModBlocks.PERSIMMON_SAPLING;
            default -> ModBlocks.MIMOSA_SAPLING;
        };
    }

    private boolean correctBiome(WorldGenLevel level, BlockPos pos) {
        return switch (kind) {
            case 0 -> level.getBiome(pos).is(ModBiomes.WISTERIA_VEILWOOD);
            case 1 -> level.getBiome(pos).is(ModBiomes.WILLOW_BACKWATERS);
            case 2 -> level.getBiome(pos).is(ModBiomes.ANCIENT_CEDAR_RAINFOREST);
            case 3 -> level.getBiome(pos).is(ModBiomes.PERSIMMON_WOODLANDS);
            default -> level.getBiome(pos).is(ModBiomes.SILKFLOWER_SAVANNA);
        };
    }

    @Override public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        if (origin.getY() < level.getMinY()+2 || origin.getY() > level.getMaxY()-44) return false;
        boolean saplingPlaced = level.getBlockState(origin).is(sapling());
        if (!saplingPlaced && !correctBiome(level, origin)) return false;
        int motif = microhabitat(origin.getX(), origin.getZ());
        // Sapling growth must always attempt a tree, not undergrowth.
        if (kind == 0) return scatterPlants(level, origin, random, motif);
        if (saplingPlaced) return growTree(level, origin, random, motif, true);
        boolean treePlaced = false;
        if (random.nextFloat() < (kind == 2 ? 0.70F : 0.63F))
            treePlaced = growTree(level, origin, random, motif, false);
        // Understory must run EVEN when tree generation succeeds.
        boolean floraPlaced = scatterPlants(level, origin, random, motif);
        return treePlaced || floraPlaced;
    }

    /** Continuous ~20-60 block patches, not artificial tiny biome polygons. */
    private int microhabitat(int x, int z) {
        double v = Math.sin(x * 0.043 + kind * 1.8) * Math.cos(z * 0.036 - kind * 0.91)
                + 0.36 * Math.sin(z * 0.057 + x * 0.017 + kind);
        return v > 0.29 ? 0 : v < -0.27 ? 1 : 2;
    }

    private static boolean soil(WorldGenLevel level, BlockPos at) {
        BlockState b = level.getBlockState(at);
        return b.is(Blocks.GRASS_BLOCK) || b.is(Blocks.DIRT) || b.is(Blocks.COARSE_DIRT)
                || b.is(Blocks.PODZOL) || b.is(Blocks.ROOTED_DIRT) || b.is(Blocks.MOSS_BLOCK)
                || b.is(Blocks.MUD) || b.is(Blocks.CLAY)
                || b.is(ModBlocks.LOAM) || b.is(ModBlocks.GRASSY_LOAM) || b.is(ModBlocks.SILT)
                || b.is(ModBlocks.RENDZINA) || b.is(ModBlocks.LITHOSOL);
    }
    private static boolean nearbyWater(WorldGenLevel level, BlockPos pos, int reach) {
        for (int x = -reach; x <= reach; x++) for (int z = -reach; z <= reach; z++) {
            if (x*x+z*z > reach*reach) continue;
            BlockPos at = pos.offset(x, -1, z);
            if (level.getFluidState(at).is(Fluids.WATER) || level.getFluidState(at.above()).is(Fluids.WATER))
                return true;
        }
        return false;
    }
    private boolean free(WorldGenLevel level, BlockPos pos) {
        BlockState b = level.getBlockState(pos);
        return b.canBeReplaced() || b.is(sapling()) || b.is(BlockTags.LEAVES);
    }
    private boolean wood(WorldGenLevel level, BlockPos pos, Direction.Axis axis) {
        if (!free(level, pos) || !level.getFluidState(pos).isEmpty()) return false;
        return level.setBlock(pos, log().defaultBlockState().setValue(BlockStateProperties.AXIS, axis), 2);
    }
    private void leaf(WorldGenLevel level, BlockPos pos) {
        if (pos.getY() >= level.getMaxY()-1 || pos.getY() < level.getMinY()+1) return;
        BlockState old = level.getBlockState(pos);
        if (old.canBeReplaced() && level.getFluidState(pos).isEmpty()) {
            Block foliage = leaves();
            // Patchy flowering leaves rather than an entire solid pink crown.
            if (kind == 4 && Math.floorMod(pos.getX()*31 + pos.getY()*17 + pos.getZ()*13, 7) < 2)
                foliage = ModBlocks.FLOWERING_MIMOSA_LEAVES;
            level.setBlock(pos, foliage.defaultBlockState(), 2);
        }
    }

    private boolean growTree(WorldGenLevel level, BlockPos base, RandomSource rng, int motif, boolean saplingPlaced) {
        if (!soil(level, base.below())) return false;
        if (kind == 1 && !saplingPlaced && !nearbyWater(level, base, 6) && rng.nextInt(3) == 0) return false;
        int height = switch (kind) {
            case 0 -> 8 + rng.nextInt(motif == 1 ? 7 : 5);
            case 1 -> 9 + rng.nextInt(6);
            case 2 -> 17 + rng.nextInt(9) + (motif == 0 ? 4 : 0);
            case 3 -> 6 + rng.nextInt(5);
            default -> 7 + rng.nextInt(5);
        };
        int width = kind == 2 && motif == 0 && rng.nextInt(6) == 0 ? 2 : 1;
        // Validate full trunk footprint BEFORE mutation; avoid buried logs and terrain pillars.
        for (int x = 0; x < width; x++) for (int z = 0; z < width; z++) {
            BlockPos foot = base.offset(x, 0, z);
            if (!soil(level, foot.below())) return false;
            for (int y = 0; y <= height; y++) {
                if (!free(level, foot.above(y)) || !level.getFluidState(foot.above(y)).isEmpty()) return false;
            }
        }
        // A silktree forks below the canopy; an exposed full-height pole is unnatural.
        int trunkHeight = kind == 4 ? height - 2 : height;
        for (int y = 0; y < trunkHeight; y++) for (int x = 0; x < width; x++)
            for (int z = 0; z < width; z++) wood(level, base.offset(x,y,z), Direction.Axis.Y);

        BlockPos top = base.above(height-1).offset(width/2, 0, width/2);
        if (kind == 2) {
            cedarCrown(level, top, height, rng, motif);
            cedarRootNetwork(level, base, rng);
            decorateCedar(level, base, height, rng);
        } else if (kind == 1) {
            willowCrown(level, top, rng);
            willowRootNetwork(level, base, rng);
        } else if (kind == 3) {
            persimmonCrown(level, top, rng, motif);
        } else if (kind == 4) {
            mimosaCrown(level, top, rng);
            decorateMimosa(level, top, rng);
        } else {
            int radius = switch (kind) {
                case 0 -> motif == 0 ? 6 : 4 + rng.nextInt(2);
                case 1 -> 5 + rng.nextInt(2);
                case 3 -> 3 + rng.nextInt(2);
                default -> 5 + rng.nextInt(2);
            };
            int yThickness = kind == 4 ? 2 : kind == 0 ? 3 : 3;
            // Visible horizontal branches support the crown and prevent leaf decay.
            for (Direction d : SIDES) {
                int length = radius-1-rng.nextInt(2);
                for (int t=1;t<=length;t++) {
                    BlockPos limb = top.below(kind==4?1:2).relative(d,t);
                    wood(level, limb, d.getAxis());
                }
            }
            roundCrown(level, top, radius, yThickness, rng);
            if (kind == 0 || kind == 1) hangCurtains(level, top, radius, rng);
            if (kind == 0) vineClimbers(level, base, height, rng);
            if (kind == 1) rootButtresses(level, base, rng, true);
        }
        // Our custom Feature does not invoke vanilla TreeFeature's post-generation
        // leaf-distance fixup. Do it explicitly to prevent newly created crowns
        // from starting at the maximum distance and decaying on their first tick.
        settleLeafDistances(level, base, height);
        return true;
    }

    /**
     * TreeFeature-style breadth-first leaf support propagation. Wood/log blocks
     * are distance-zero seeds; all leaves reachable within 15 steps receive the
     * proper extended and vanilla distance states before chunk generation ends.
     */
    private void settleLeafDistances(WorldGenLevel level, BlockPos base, int treeHeight) {
        int radius = switch (kind) {
            case 0 -> 14; // Opposing Maple-style wisteria crowns and trailing flowers
            case 1 -> 9;
            case 2 -> 7;
            default -> 8;
        };
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        Map<BlockPos, Integer> distances = new HashMap<>();
        int minY = Math.max(level.getMinY(), base.getY() - 2);
        int maxY = Math.min(level.getMaxY() - 1, base.getY() + treeHeight + 8);

        // Every log and bark-covered root in the generated crown is a source.
        // Seeds are bounded so adjacent tree foliage is not modified unnecessarily.
        for (int x = base.getX() - radius; x <= base.getX() + radius; x++)
            for (int z = base.getZ() - radius; z <= base.getZ() + radius; z++)
                for (int y = minY; y <= maxY; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (state.is(log())
                            || (kind == 2 && (state.is(ModBlocks.CEDAR_WOOD)
                                    || state.is(ModBlocks.MOSSY_CEDAR_LOG)
                                    || state.is(ModBlocks.CEDAR_ROOTS)))
                            || (kind == 1 && (state.is(ModBlocks.WILLOW_ROOTS)
                                    || state.is(ModBlocks.WILLOW_WOOD)))) {
                        queue.addLast(pos);
                        distances.put(pos, 0);
                    }
                }

        while (!queue.isEmpty()) {
            BlockPos current = queue.removeFirst();
            int nextDistance = distances.get(current) + 1;
            if (nextDistance >= 16) continue;
            for (Direction direction : Direction.values()) {
                BlockPos next = current.relative(direction);
                if (next.getY() < minY || next.getY() > maxY
                        || Math.abs(next.getX() - base.getX()) > radius
                        || Math.abs(next.getZ() - base.getZ()) > radius)
                    continue;

                BlockState state = level.getBlockState(next);
                if (!(state.is(leaves()) || (kind == 4 && state.is(ModBlocks.FLOWERING_MIMOSA_LEAVES)))
                        || nextDistance >= distances.getOrDefault(next, 16))
                    continue;
                distances.put(next, nextDistance);
                level.setBlock(next, state.setValue(ExtendedForestLeavesBlock.FOREST_DISTANCE, nextDistance)
                        .setValue(BlockStateProperties.DISTANCE, Math.min(nextDistance, 7)), 2);
                queue.addLast(next);
            }
        }
    }

    /**
     * Draw a curving branch using face-connected log segments.
     * The lateral sweep and independently selected fork heights stop crowns
     * becoming identical X-shaped spokes. Every log touches its predecessor.
     */
    private BlockPos curvedBranch(WorldGenLevel level, BlockPos start, double azimuth,
                                  int reach, int rise, double sweep) {
        BlockPos previous = start;
        for (int step = 1; step <= reach; step++) {
            double t = step / (double) reach;
            double bearing = azimuth + sweep * t * t;
            int x = (int) Math.round(Math.cos(bearing) * step);
            int z = (int) Math.round(Math.sin(bearing) * step);
            int y = (int) Math.round(rise * (2.0 * t - t * t));
            BlockPos destination = start.offset(x, y, z);
            BlockPos cursor = previous;
            int guard = 0;
            // Alternating X and Z removes the artificial straight cardinal beams.
            while (!cursor.equals(destination) && guard++ < 20) {
                int dx = destination.getX() - cursor.getX();
                int dz = destination.getZ() - cursor.getZ();
                int dy = destination.getY() - cursor.getY();
                Direction.Axis axis;
                if (dx != 0 && (dz == 0 || (guard & 1) == 0)) {
                    cursor = cursor.offset(Integer.signum(dx), 0, 0);
                    axis = Direction.Axis.X;
                } else if (dz != 0) {
                    cursor = cursor.offset(0, 0, Integer.signum(dz));
                    axis = Direction.Axis.Z;
                } else if (dy != 0) {
                    cursor = cursor.above();
                    axis = Direction.Axis.Y;
                } else {
                    break;
                }
                wood(level, cursor, axis);
            }
            previous = destination;
        }
        return previous;
    }

    /** Rounded leaf clouds with scalloped edges, used only for Willow/Persimmon/Mimosa. */
    private void foliageLobe(WorldGenLevel level, BlockPos center, RandomSource random,
                              int rx, int ry, int rz, float edgeGap) {
        for (int y = -ry; y <= ry; y++)
            for (int x = -rx; x <= rx; x++)
                for (int z = -rz; z <= rz; z++) {
                    float normalized = x*x/(float)(rx*rx)
                            + y*y/(float)(ry*ry)
                            + z*z/(float)(rz*rz);
                    if (normalized > 1.07F) continue;
                    if (normalized > 0.65F && random.nextFloat() < edgeGap) continue;
                    leaf(level, center.offset(x, y, z));
                }
    }

    /** Add independent, naturally varying weeping strands beneath willow leaf tips. */
    private void hangWillowTufts(WorldGenLevel level, BlockPos around, RandomSource random,
                                 int attempts) {
        for (int i=0;i<attempts;i++) {
            int x=random.nextInt(5)-2,z=random.nextInt(5)-2;
            BlockPos lowest=null;
            for (int y=2;y>=-3;y--) {
                BlockPos p=around.offset(x,y,z);
                if (level.getBlockState(p).is(ModBlocks.WILLOW_LEAVES)) lowest=p;
            }
            if (lowest==null || !level.getBlockState(lowest.below()).isAir()) continue;
            int maximum=2+random.nextInt(4),placed=0;
            for (int d=1;d<=maximum;d++) {
                BlockPos p=lowest.below(d);
                if (!level.getBlockState(p).isAir()) break;
                level.setBlock(p,ModBlocks.HANGING_WILLOW_LEAVES.defaultBlockState()
                        .setValue(HangingAccentBlock.TIP,false),2);
                placed++;
            }
            if (placed>0) level.setBlock(lowest.below(placed),
                    ModBlocks.HANGING_WILLOW_LEAVES.defaultBlockState()
                            .setValue(HangingAccentBlock.TIP,true),2);
        }
    }

    /**
     * Willow: a few curved ascending main limbs, many slender side boughs,
     * loosely overlapping rounded foliage and independently drooping skirts.
     */
    private void willowCrown(WorldGenLevel level, BlockPos top, RandomSource random) {
        int branches=3+random.nextInt(3);
        double starting=random.nextDouble()*Math.PI*2.0;
        for (int b=0;b<branches;b++) {
            double angle=starting+b*2.0*Math.PI/branches
                    +(random.nextDouble()-0.5)*0.65;
            BlockPos fork=top.below(3+random.nextInt(2));
            int length=4+random.nextInt(3);
            BlockPos tip=curvedBranch(level,fork,angle,length,2+random.nextInt(2),
                    (random.nextDouble()-0.5)*0.60);
            foliageLobe(level,tip,random,3,2,3,0.29F);
            hangWillowTufts(level,tip,random,3);
            // Side shoots descend from different points, not a symmetrical star.
            for (int twig=0;twig<2;twig++) {
                double divergence=(twig==0?1:-1)*(0.38+random.nextDouble()*0.38);
                BlockPos branchStart=tip.below(random.nextInt(2));
                BlockPos twigTip=curvedBranch(level,branchStart,angle+divergence,
                        2+random.nextInt(2),random.nextInt(2),
                        (random.nextDouble()-0.5)*0.24);
                foliageLobe(level,twigTip,random,2,2,2,0.27F);
                hangWillowTufts(level,twigTip,random,2);
            }
        }
        foliageLobe(level,top.below(1),random,3,2,3,0.21F);
    }

    /** Mangrove-like root-arches are shared by Willow and Cedar. */
    private void willowRootNetwork(WorldGenLevel level, BlockPos base, RandomSource random) {
        mangroveStyleRoots(level,base,random,true);
    }

    /**
     * Persimmon: multiple offset crotches and lobe-shaped foliage rather than
     * a symmetrical sphere atop four identical straight branches.
     */
    private void persimmonCrown(WorldGenLevel level, BlockPos top,
                                RandomSource random, int motif) {
        int limbs=3+random.nextInt(3);
        double orientation=random.nextDouble()*Math.PI*2.0;
        for (int i=0;i<limbs;i++) {
            double angle=orientation+i*2.0*Math.PI/limbs
                    +(random.nextDouble()-0.5)*0.65;
            BlockPos fork=top.below(2+random.nextInt(3));
            BlockPos end=curvedBranch(level,fork,angle,2+random.nextInt(3),
                    1+random.nextInt(3),(random.nextDouble()-0.5)*0.45);
            int radius=motif==0 && random.nextBoolean()?3:2;
            foliageLobe(level,end,random,radius,2,radius,0.20F);
            if (random.nextFloat()<0.75F) {
                double divergence=(random.nextBoolean()?1:-1)
                        *(0.42+random.nextDouble()*0.48);
                BlockPos twig=curvedBranch(level,end.below(1),angle+divergence,
                        1+random.nextInt(2),random.nextInt(2),
                        (random.nextDouble()-0.5)*0.25);
                foliageLobe(level,twig,random,2,1,2,0.24F);
            }
        }
        foliageLobe(level,top.below(1),random,2,2,2,0.18F);
    }

    /**
     * Mimosa: a short central trunk splits into crooked horizontal boughs.
     * Each bough forks again and supports two or three small, FLAT leaf pads,
     * leaving open air through the crown rather than forming a solid saucer.
     */
    private void mimosaCrown(WorldGenLevel level, BlockPos top, RandomSource random) {
        int major=2+random.nextInt(2);
        double starting=random.nextDouble()*Math.PI*2.0;
        for (int arm=0;arm<major;arm++) {
            double angle=starting+arm*2.0*Math.PI/major
                    +(random.nextDouble()-0.5)*0.65;
            BlockPos crotch=top.below(3+random.nextInt(2));
            BlockPos majorTip=curvedBranch(level,crotch,angle,3+random.nextInt(3),
                    2,(random.nextDouble()-0.5)*0.48);
            for (int twig=0;twig<3;twig++) {
                double forkAngle=angle+(twig-1)*(0.45+random.nextDouble()*0.24)
                        +(random.nextDouble()-0.5)*0.22;
                int length=1+random.nextInt(3);
                BlockPos twigBase=majorTip.below(random.nextInt(2));
                BlockPos tip=curvedBranch(level,twigBase,forkAngle,length,
                        random.nextInt(2),(random.nextDouble()-0.5)*0.24);
                int radius=2+random.nextInt(2);
                foliageLobe(level,tip,random,radius,1,radius,0.30F);
            }
            foliageLobe(level,majorTip,random,2,1,2,0.32F);
        }
        foliageLobe(level,top.below(2),random,1,1,2,0.20F);
    }

    /**
     * Cedar roots branch along the ground: bulky bark-covered sections near
     * the trunk, with smaller but still solid root segments toward their tips.
     */
    private void cedarRootNetwork(WorldGenLevel level, BlockPos base, RandomSource random) {
        int arms=5+random.nextInt(3);
        for(int arm=0;arm<arms;arm++){
            double angle=2.0*Math.PI*arm/arms+(random.nextDouble()-0.5)*0.28;
            int length=2+random.nextInt(3);
            for(int step=1;step<=length;step++){
                int dx=Math.round((float)(Math.cos(angle)*step));
                int dz=Math.round((float)(Math.sin(angle)*step));
                BlockPos p=base.offset(dx,0,dz);
                if(!soil(level,p.below()) || !free(level,p)
                        || !level.getFluidState(p).isEmpty())break;
                Block root=step==1?ModBlocks.CEDAR_WOOD:ModBlocks.CEDAR_ROOTS;
                Direction.Axis axis=Math.abs(Math.cos(angle))>=Math.abs(Math.sin(angle))
                        ?Direction.Axis.X:Direction.Axis.Z;
                level.setBlock(p,root.defaultBlockState().setValue(BlockStateProperties.AXIS,axis),2);
            }
        }
    }

    private void decorateCedar(WorldGenLevel level, BlockPos base, int height, RandomSource random) {
        // Isolated older moss-covered log sections near ground level.
        for(int y=1;y<Math.min(height-2,8);y++){
            BlockPos trunk=base.above(y);
            if(level.getBlockState(trunk).is(ModBlocks.CEDAR_LOG) && random.nextInt(3)==0)
                level.setBlock(trunk,ModBlocks.MOSSY_CEDAR_LOG.defaultBlockState(),2);
        }
        // Bracket fungus shelves cling to accessible sides of lower trunks.
        for(int i=0;i<10;i++){
            BlockPos bark=base.above(1+random.nextInt(Math.max(1,Math.min(height-3,11))));
            Direction face=SIDES[random.nextInt(4)];
            BlockPos p=bark.relative(face);
            if(!level.getBlockState(p).isAir()
                    || !level.getBlockState(bark).is(BlockTags.LOGS))continue;
            BlockState fungus=ModBlocks.BRACKET_FUNGUS.defaultBlockState()
                    .setValue(BracketFungusBlock.FACING,face)
                    .setValue(BracketFungusBlock.COUNT,1+random.nextInt(3));
            if(fungus.canSurvive(level,p))level.setBlock(p,fungus,2);
        }

        // Hang narrow moss strands below cedar foliage.
        BlockPos crown=base.above(height-1);
        for(int i=0;i<40;i++){
            int x=random.nextInt(13)-6,z=random.nextInt(13)-6;
            BlockPos lowest=null;
            for(int dy=2;dy>=-17;dy--){
                BlockPos p=crown.offset(x,dy,z);
                if(level.getBlockState(p).is(ModBlocks.CEDAR_LEAVES))lowest=p;
            }
            if(lowest==null || !level.getBlockState(lowest.below()).isAir())continue;
            int length=1+random.nextInt(4),count=0;
            for(int j=1;j<=length;j++){
                BlockPos p=lowest.below(j);
                if(!level.getBlockState(p).isAir())break;
                level.setBlock(p,ModBlocks.HANGING_FOREST_MOSS.defaultBlockState()
                        .setValue(HangingAccentBlock.TIP,false),2);
                count++;
            }
            if(count>0)level.setBlock(lowest.below(count),
                    ModBlocks.HANGING_FOREST_MOSS.defaultBlockState()
                            .setValue(HangingAccentBlock.TIP,true),2);
        }
    }

    private void decorateMimosa(WorldGenLevel level, BlockPos top, RandomSource random) {
        // Blossoms behave like tiny amethyst-style buds on the bottom of leaves.
        int placed=0;
        for(int attempt=0;attempt<60 && placed<10;attempt++){
            BlockPos p=top.offset(random.nextInt(15)-7,
                    random.nextInt(6)-4,random.nextInt(15)-7);
            if(!level.getBlockState(p.above()).is(ModBlocks.FLOWERING_MIMOSA_LEAVES)
                    || !level.getBlockState(p).isAir())continue;
            BlockState state=ModBlocks.MIMOSA_BLOSSOMS.defaultBlockState()
                    .setValue(BlockStateProperties.FACING,Direction.DOWN);
            if(state.canSurvive(level,p)){
                level.setBlock(p,state,2);
                placed++;
            }
        }
    }

    private void cedarCrown(WorldGenLevel level, BlockPos top, int height, RandomSource rng, int motif) {
        int depth = Math.min(17, height-3);
        for (int d=0;d<=depth;d++) {
            int radius = Math.min(5, 1 + d/3);
            if (d%4==1) radius = Math.max(1,radius-1);
            if (motif == 0 && d > 9) radius++;
            for (int x=-radius;x<=radius;x++) for(int z=-radius;z<=radius;z++) {
                double ellipse=(x*x+z*z)/(double)(radius*radius);
                if (ellipse <= 1.02 && rng.nextFloat() > (ellipse>0.8?0.22F:0.06F))
                    leaf(level, top.offset(x,-d,z));
            }
        }
        leaf(level, top.above());
    }

    private void roundCrown(WorldGenLevel level, BlockPos top, int radius, int thick, RandomSource rng) {
        for (int dy=-thick;dy<=1;dy++) for (int x=-radius;x<=radius;x++) for(int z=-radius;z<=radius;z++) {
            double d=(x*x+z*z)/(double)(radius*radius)
                    + (dy*dy)/(double)(thick*thick);
            float holes = kind == 4 ? 0.24F : 0.08F;
            if (d <= 1.10 && rng.nextFloat() > holes && (x*x+z*z < 2 || rng.nextFloat() > 0.10F))
                leaf(level, top.offset(x,dy,z));
        }
    }

    private void hangCurtains(WorldGenLevel level, BlockPos top, int radius, RandomSource rng) {
        Block hanging = kind == 0 ? ModBlocks.HANGING_WISTERIA_BLOSSOMS : ModBlocks.HANGING_WILLOW_LEAVES;
        for(int x=-radius;x<=radius;x++) for(int z=-radius;z<=radius;z++) {
            int sq=x*x+z*z;
            if (sq < (radius-2)*(radius-2) || sq > radius*radius || rng.nextInt(kind==0?4:3)!=0) continue;
            // Find the actual canopy underside. Beginning inside leaves suppresses hanging strands.
            BlockPos lowestLeaf = null;
            for (int dy = 1; dy >= -6; dy--) {
                BlockPos check = top.offset(x, dy, z);
                if (level.getBlockState(check).is(leaves())) lowestLeaf = check;
            }
            if (lowestLeaf == null) continue;
            BlockPos start = lowestLeaf.below();
            if (!level.getBlockState(start).isAir()) continue;
            int length = 1+rng.nextInt(kind == 0 ? 5 : 6),placed=0;
            for (int dy=0;dy<length;dy++) {
                BlockPos q=start.below(dy);
                if (!level.getBlockState(q).isAir()) break;
                level.setBlock(q,hanging.defaultBlockState().setValue(HangingAccentBlock.TIP,false),2);
                placed++;
            }
            if (placed>0)level.setBlock(start.below(placed-1),
                    hanging.defaultBlockState().setValue(HangingAccentBlock.TIP,true),2);
        }
    }

    private void vineClimbers(WorldGenLevel level, BlockPos trunk, int height, RandomSource rng) {
        for (int y=1;y<height-2;y++) if (rng.nextFloat()<0.55F) {
            Direction outward=SIDES[rng.nextInt(SIDES.length)];
            BlockPos vineAt=trunk.above(y).relative(outward);
            if (!level.getBlockState(vineAt).isAir()) continue;
            Direction support=outward.getOpposite();
            BlockState vine=ModBlocks.CLIMBING_WISTERIA_VINE.defaultBlockState();
            if(support==Direction.NORTH)vine=vine.setValue(VineBlock.NORTH,true);
            if(support==Direction.SOUTH)vine=vine.setValue(VineBlock.SOUTH,true);
            if(support==Direction.EAST)vine=vine.setValue(VineBlock.EAST,true);
            if(support==Direction.WEST)vine=vine.setValue(VineBlock.WEST,true);
            level.setBlock(vineAt,vine,2);
        }
    }

    private void rootButtresses(WorldGenLevel level, BlockPos base, RandomSource rng, boolean willow) {
        for(Direction d:SIDES) if(rng.nextBoolean()) {
            BlockPos site=base.relative(d);
            if (soil(level,site.below()) && level.getBlockState(site).canBeReplaced() &&
                    level.getFluidState(site).isEmpty()) {
                // Buttresses should have bark on every face, not exposed end grain.
                Block root=willow?ModBlocks.WILLOW_ROOTS:ModBlocks.CEDAR_WOOD;
                level.setBlock(site,root.defaultBlockState().setValue(BlockStateProperties.AXIS,d.getAxis()),2);
            }
        }
    }

    private boolean scatterPlants(WorldGenLevel level, BlockPos origin, RandomSource rng, int motif) {
        boolean changed=false;
        int attempts=16+(motif==0?8:4);
        for(int i=0;i<attempts;i++) {
            int x=origin.getX()+rng.nextInt(17)-8,z=origin.getZ()+rng.nextInt(17)-8;
            // WORLD_SURFACE sees tree leaves. Descend through the crown and find
            // the real ground or shallow-water surface beneath it.
            int surfaceY=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z);
            BlockPos p=new BlockPos(x,surfaceY,z);
            for(int depth=0;depth<56 && p.getY()>level.getMinY()+1;depth++) {
                boolean empty=level.getBlockState(p).isAir();
                boolean shore=kind==1 && level.getFluidState(p.below()).is(Fluids.WATER)
                        && level.getFluidState(p.below()).isSource();
                if(empty && (soil(level,p.below()) || shore))break;
                p=p.below();
            }
            int y=p.getY();
            if(!correctBiome(level,p) || y<level.getMinY()+1 || y>=level.getMaxY()-3)continue;
            if(kind==1 && rng.nextInt(10)==0 && nearbyWater(level,p,3)
                    && soil(level,p.below()) && level.getFluidState(p.below()).isEmpty()) {
                if(!level.getBlockState(p.below()).is(ModBlocks.SILT))
                    changed|=level.setBlock(p.below(),ModBlocks.SILT.defaultBlockState(),2);
            }
            // Preserve real water when placing aquatic plants.
            BlockPos water=p.below();
            if(kind==1 && level.getFluidState(water).is(Fluids.WATER)
                    && level.getFluidState(water).isSource()) {
                Block target=switch(rng.nextInt(5)) {
                    case 0,1 -> ModBlocks.WATER_MINT;
                    case 2,3 -> ModBlocks.WATER_SEDGE;
                    default -> ModBlocks.MARSH_MARIGOLD_WATER;
                };
                if(target instanceof FlexibleWetlandPlantBlock wet)
                    changed|=wet.placeNaturally(level,water);
                continue;
            }
            if(!level.getBlockState(p).isAir() || !soil(level,p.below()))continue;
            int roll=rng.nextInt(8);
            Block plant=switch(kind) {
                case 0 -> motif==2 && roll<3 ? ModBlocks.FALLEN_WISTERIA_PETALS
                        : roll<5 ? ModBlocks.WILD_VIOLET : Blocks.FERN;
                case 1 -> roll<3?ModBlocks.MARSH_MARIGOLD
                        :roll<6?Blocks.FERN:Blocks.SHORT_GRASS;
                case 2 -> switch(motif) {
                    case 0 -> roll<3?ModBlocks.CLUBMOSS:roll<5?ModBlocks.LIVERWORT
                            :roll==5?ModBlocks.GLOWSHROOM:Blocks.FERN;
                    case 1 -> roll<3?ModBlocks.LIVERWORT:roll<5?ModBlocks.CLUBMOSS
                            :roll==5?ModBlocks.GLOWSHROOM:Blocks.MOSS_CARPET;
                    default -> roll<3?ModBlocks.CLUBMOSS:roll<5?ModBlocks.LIVERWORT
                            :roll==5?Blocks.MOSS_CARPET:Blocks.FERN;
                };
                case 3 -> switch(motif) {
                    case 0 -> roll<4?ModBlocks.FALLEN_AUTUMN_LEAVES:roll<6
                            ?ModBlocks.ANEMONE_FLOWERS:ModBlocks.SILVER_GRASS;
                    case 1 -> roll<4?ModBlocks.SILVER_GRASS:roll<6
                            ?ModBlocks.ANEMONE_FLOWERS:ModBlocks.FALLEN_AUTUMN_LEAVES;
                    default -> roll<3?ModBlocks.BUSH:roll<6
                            ?ModBlocks.FALLEN_AUTUMN_LEAVES:ModBlocks.ANEMONE_FLOWERS;
                };
                default -> switch(motif) {
                    case 0 -> roll<4?ModBlocks.FALLEN_MIMOSA_PETALS:roll<6
                            ?ModBlocks.PINK_CLOVER:ModBlocks.FEATHERGRASS;
                    case 1 -> roll<4?ModBlocks.FEATHERGRASS:roll<6
                            ?ModBlocks.PINK_CLOVER:Blocks.SHORT_GRASS;
                    default -> roll<3?ModBlocks.FEATHERGRASS:roll<5
                            ?ModBlocks.BUSH:roll<7?ModBlocks.PINK_CLOVER:ModBlocks.FALLEN_MIMOSA_PETALS;
                };
            };
            if(plant==ModBlocks.SILVER_GRASS) {
                changed|=ModBlocks.SILVER_GRASS.placeAtHeight(level,p,1+rng.nextInt(2));
                continue;
            }
            BlockState state=plant.defaultBlockState();
            if(plant instanceof LeafLitterBlock)
                state=state.setValue(BlockStateProperties.SEGMENT_AMOUNT,1+rng.nextInt(4));
            if(!state.canSurvive(level,p))continue;
            boolean placed=level.setBlock(p,state,2);
            changed|=placed;
            if(placed && plant==ModBlocks.CLUBMOSS && rng.nextInt(3)==0
                    && level.getBlockState(p.above()).isAir()) {
                BlockState sprig=ModBlocks.CLUBMOSS_SPRIG.defaultBlockState();
                if(sprig.canSurvive(level,p.above()))
                    changed|=level.setBlock(p.above(),sprig,2);
            }
        }
        return changed;
    }
}
