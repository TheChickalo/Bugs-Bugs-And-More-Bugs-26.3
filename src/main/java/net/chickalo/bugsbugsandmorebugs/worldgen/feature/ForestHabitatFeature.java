package net.chickalo.bugsbugsandmorebugs.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.block.custom.FlexibleWetlandPlantBlock;
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
        if (saplingPlaced || random.nextFloat() < (kind == 2 ? 0.73F : 0.65F))
            return growTree(level, origin, random, motif, saplingPlaced);
        return scatterPlants(level, origin, random, motif);
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
        if (old.canBeReplaced() && level.getFluidState(pos).isEmpty())
            level.setBlock(pos, leaves().defaultBlockState(), 2);
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
        for (int y = 0; y < height; y++) for (int x = 0; x < width; x++)
            for (int z = 0; z < width; z++) wood(level, base.offset(x,y,z), Direction.Axis.Y);

        BlockPos top = base.above(height-1).offset(width/2, 0, width/2);
        if (kind == 0) {
            // Use the same two/three ascending branch silhouette and layered
            // {2,3,4,3,1} foliage profile as the existing Maple placers.
            wisteriaMapleCrown(level, base, height, motif, rng);
            vineClimbers(level, base, height, rng);
        } else if (kind == 2) {
            cedarCrown(level, top, height, rng, motif);
            rootButtresses(level, base, rng, false);
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
        return true;
    }

    /**
     * Maple-style wisteria. Like MapleTrunkPlacer it grows two opposing, rising
     * branches (occasionally a third); each receives the same five canopy
     * layers/radii used by MapleFoliagePlacer. Blossom curtains remain unique.
     */
    private void wisteriaMapleCrown(WorldGenLevel level, BlockPos base, int height,
                                   int motif, RandomSource random) {
        Direction first = SIDES[random.nextInt(SIDES.length)];
        int branchCount = random.nextInt(3) == 0 ? 3 : 2;
        for (int branch = 0; branch < branchCount; branch++) {
            Direction direction = branch == 0 ? first
                    : branch == 1 ? first.getOpposite() : SIDES[random.nextInt(SIDES.length)];
            Direction across = (direction == Direction.NORTH || direction == Direction.SOUTH)
                    ? Direction.EAST : Direction.NORTH;
            int startY = Math.max(4, height - 4 - (branch % 2));
            int endY = Math.max(startY + 1, height - 2 + random.nextInt(3));
            int length = 2 + random.nextInt(3) + (motif == 0 ? 1 : 0);
            int lateral = random.nextInt(3) - 1;

            BlockPos previous = base.above(startY);
            for (int step = 1; step <= length; step++) {
                int y = startY + (endY - startY) * step / length;
                int side = lateral * step / length;
                BlockPos next = base.relative(direction, step).relative(across, side).above(y);
                // Fill intermediate vertical steps so limbs are truly connected.
                for (int climb = previous.getY() + 1; climb <= next.getY(); climb++)
                    wood(level, new BlockPos(previous.getX(), climb, previous.getZ()), Direction.Axis.Y);
                wood(level, next, direction.getAxis());
                previous = next;
            }

            BlockPos attachment = previous.above();
            mapleFoliage(level, attachment, random);
            // Curtains originate beneath individual Maple-shaped canopies.
            hangCurtains(level, attachment.above(2), 4, random);
        }

        if (branchCount == 2 && motif == 0 && random.nextBoolean()) {
            BlockPos apex = base.above(height);
            mapleFoliage(level, apex, random);
            hangCurtains(level, apex.above(2), 4, random);
        }
    }

    /** Matches the five crown radii and irregular trimmed corners of MapleFoliagePlacer. */
    private void mapleFoliage(WorldGenLevel level, BlockPos attachment, RandomSource random) {
        int[] radii = {2, 3, 4, 3, 1};
        for (int layer = 0; layer < radii.length; layer++) {
            int radius = radii[layer];
            for (int x = -radius; x <= radius; x++) for (int z = -radius; z <= radius; z++) {
                if (Math.abs(x) == radius && Math.abs(z) == radius) continue;
                if ((Math.abs(x) == radius || Math.abs(z) == radius)
                        && random.nextFloat() < 0.10F) continue;
                leaf(level, attachment.offset(x, layer, z));
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
        int attempts=7+(motif==0?7:3);
        for(int i=0;i<attempts;i++) {
            int x=origin.getX()+rng.nextInt(13)-6;
            int z=origin.getZ()+rng.nextInt(13)-6;
            int y=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z);
            BlockPos p=new BlockPos(x,y,z);
            if (!correctBiome(level,p) || y < level.getMinY()+1 || y>=level.getMaxY()-3) continue;
            // No artificial lakes or riverbanks: silt only replaces existing soil.
            if (kind==1 && rng.nextInt(6)==0 && nearbyWater(level,p,3) && soil(level,p.below())) {
                BlockState old=level.getBlockState(p.below());
                if (!old.is(ModBlocks.SILT) && !old.is(Blocks.MUD)) {
                    changed|=level.setBlock(p.below(),ModBlocks.SILT.defaultBlockState(),2);
                }
            }
            if (kind == 1 && level.getFluidState(p.below()).is(Fluids.WATER) && rng.nextBoolean()) {
                BlockPos water=p.below();
                Block target=rng.nextInt(3)==0?ModBlocks.MARSH_MARIGOLD_WATER:ModBlocks.WATER_SEDGE;
                if (target instanceof FlexibleWetlandPlantBlock wet)
                    changed|=wet.placeNaturally(level,water);
                continue;
            }
            if (!level.getBlockState(p).isAir() || !soil(level,p.below())) continue;
            Block plant;
            switch(kind) {
                case 0 -> plant=motif==2 && rng.nextInt(3)==0?ModBlocks.FALLEN_WISTERIA_PETALS:
                        rng.nextBoolean()?ModBlocks.WILD_VIOLET:Blocks.FERN;
                case 1 -> plant=motif==1?rng.nextBoolean()?ModBlocks.WATER_MINT:ModBlocks.MARSH_MARIGOLD:
                        rng.nextInt(3)==0?Blocks.FERN:ModBlocks.WATER_MINT;
                case 2 -> plant=motif==1?rng.nextBoolean()?ModBlocks.GLOWSHROOM:Blocks.MOSS_CARPET:
                        rng.nextBoolean()?Blocks.FERN:Blocks.MOSS_CARPET;
                case 3 -> plant=motif==0 && rng.nextInt(3)==0?ModBlocks.GINKGO_FALLEN_LEAVES:
                        rng.nextBoolean()?ModBlocks.SILVER_GRASS:ModBlocks.BUSH_CLOVER;
                default -> plant=motif==1?rng.nextBoolean()?Blocks.SHORT_GRASS:ModBlocks.SILVER_GRASS:
                        rng.nextBoolean()?ModBlocks.BELLFLOWERS:Blocks.SHORT_GRASS;
            }
            if (plant == ModBlocks.WATER_MINT) {
                changed|=ModBlocks.WATER_MINT.placeAtHeight(level,p,2);
            } else if (plant == ModBlocks.SILVER_GRASS || plant == ModBlocks.BUSH_CLOVER) {
                changed|=((VariableHeightPlantBlock)plant).placeAtHeight(level,p,1+rng.nextInt(2));
            } else {
                BlockState state=plant.defaultBlockState();
                if (plant instanceof LeafLitterBlock)
                    state=state.setValue(BlockStateProperties.SEGMENT_AMOUNT,1+rng.nextInt(4));
                if (state.canSurvive(level,p))changed|=level.setBlock(p,state,2);
            }
        }
        return changed;
    }
}
