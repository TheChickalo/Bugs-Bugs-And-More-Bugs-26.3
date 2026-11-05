    package net.chickalo.bugsbugsandmorebugs.worldgen.tree;

    import com.mojang.serialization.MapCodec;
    import com.mojang.serialization.codecs.RecordCodecBuilder;
    import net.minecraft.core.BlockPos;
    import net.minecraft.util.RandomSource;
    import net.minecraft.world.level.WorldGenLevel;
    import net.minecraft.world.level.block.state.BlockState;
    import net.minecraft.world.level.levelgen.feature.TreeFeature;
    import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
    import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
    import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;


    import java.util.List;
    import java.util.function.BiConsumer;

    public class MapleTrunkPlacer extends TrunkPlacer {
        public static final MapCodec<MapleTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
                trunkPlacerParts(instance).apply(instance, MapleTrunkPlacer::new));

        public MapleTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
            super(baseHeight, heightRandA, heightRandB);
        }

        @Override
        protected TrunkPlacerType<?> type() {
            return ModTrunkPlacerTypes.MAPLE_TRUNK_PLACER;
        }

        @Override
        public List<FoliagePlacer.FoliageAttachment> placeTrunk(WorldGenLevel level, BiConsumer<BlockPos, BlockState> trunkSetter, RandomSource random, int treeHeight, BlockPos origin, TreeFeature config) {

            placeBelowTrunkBlock(level, trunkSetter, random, origin.below(), config);
            int height = treeHeight + random.nextInt(heightRandA, heightRandB + 3) + random.nextInt(heightRandA - 1, heightRandB + 2);

            for(int i = 0; i < baseHeight; i++) {
                placeLog(level, trunkSetter, random, origin.above(i), config);
            }

            BlockPos currentPos = origin.above(baseHeight - 1);


            return List.of(new FoliagePlacer.FoliageAttachment(currentPos, 0, false));
        }
    }
