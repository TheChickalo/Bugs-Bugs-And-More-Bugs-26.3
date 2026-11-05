
package net.chickalo.bugsbugsandmorebugs.worldgen.tree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class MapleTrunkPlacer extends TrunkPlacer {

    private static final Codec<UniformInt> BRANCH_START_CODEC =
            UniformInt.MAP_CODEC.codec().validate(u ->
                    u.maxInclusive() - u.minInclusive() < 1
                            ? DataResult.error(() ->
                            "Need at least 2 possible branch start heights")
                            : DataResult.success(u)
            );

    public static final MapCodec<MapleTrunkPlacer> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    trunkPlacerParts(instance)
                            .and(instance.group(
                                    IntProviders.codec(1, 5)
                                            .fieldOf("branch_count")
                                            .forGetter(t -> t.branchCount),

                                    IntProviders.codec(2, 16)
                                            .fieldOf("branch_horizontal_length")
                                            .forGetter(t -> t.branchHorizontalLength),

                                    IntProviders.validateCodec(
                                                    -16, 0, BRANCH_START_CODEC)
                                            .fieldOf("branch_start_offset_from_top")
                                            .forGetter(t -> t.branchStartOffsetFromTop),

                                    IntProviders.codec(-16, 16)
                                            .fieldOf("branch_end_offset_from_top")
                                            .forGetter(t -> t.branchEndOffsetFromTop)
                            ))
                            .apply(instance, MapleTrunkPlacer::new)
            );

    private final IntProvider branchCount;
    private final IntProvider branchHorizontalLength;
    private final UniformInt branchStartOffsetFromTop;
    private final UniformInt secondBranchStartOffsetFromTop;
    private final IntProvider branchEndOffsetFromTop;

    public MapleTrunkPlacer(
            int baseHeight,
            int heightRandA,
            int heightRandB,
            IntProvider branchCount,
            IntProvider branchHorizontalLength,
            UniformInt branchStartOffsetFromTop,
            IntProvider branchEndOffsetFromTop
    ) {
        super(baseHeight, heightRandA, heightRandB);

        this.branchCount = branchCount;
        this.branchHorizontalLength = branchHorizontalLength;
        this.branchStartOffsetFromTop = branchStartOffsetFromTop;

        this.secondBranchStartOffsetFromTop = UniformInt.of(
                branchStartOffsetFromTop.minInclusive(),
                branchStartOffsetFromTop.maxInclusive() - 1
        );

        this.branchEndOffsetFromTop = branchEndOffsetFromTop;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTrunkPlacerTypes.MAPLE_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(
            WorldGenLevel level,
            BiConsumer<BlockPos, BlockState> trunkSetter,
            RandomSource random,
            int treeHeight,
            BlockPos origin,
            TreeFeature tree
    ) {
        placeBelowTrunkBlock(level, trunkSetter, random, origin.below(), tree);

        int firstBranchOffsetFromOrigin = Math.max(
                0,
                treeHeight - 1 + branchStartOffsetFromTop.sample(random)
        );

        int secondBranchOffsetFromOrigin = Math.max(
                0,
                treeHeight - 1 + secondBranchStartOffsetFromTop.sample(random)
        );

        if (secondBranchOffsetFromOrigin >= firstBranchOffsetFromOrigin) {
            secondBranchOffsetFromOrigin++;
        }

        int branchCount = this.branchCount.sample(random);

        boolean hasMiddleBranch = branchCount == 3;
        boolean hasBothSideBranches = branchCount >= 2;

        int trunkHeight;

        if (hasMiddleBranch) {
            trunkHeight = treeHeight;
        } else if (hasBothSideBranches) {
            trunkHeight = Math.max(
                    firstBranchOffsetFromOrigin,
                    secondBranchOffsetFromOrigin
            ) + 1;
        } else {
            trunkHeight = firstBranchOffsetFromOrigin + 1;
        }

        // Main vertical trunk.
        for (int y = 0; y < trunkHeight; y++) {
            placeLog(level, trunkSetter, random, origin.above(y), tree);
        }

        List<FoliagePlacer.FoliageAttachment> attachments =
                new ArrayList<>();

        if (hasMiddleBranch) {
            attachments.add(
                    new FoliagePlacer.FoliageAttachment(
                            origin.above(trunkHeight), 0, false
                    )
            );
        }

        BlockPos.MutableBlockPos logPos = new BlockPos.MutableBlockPos();

        Direction treeDirection =
                Direction.Plane.HORIZONTAL.getRandomDirection(random);

        // This rotates branch logs along their horizontal direction.
        Function<BlockState, BlockState> sidewaysStateModifier =
                state -> state.trySetValue(
                        RotatedPillarBlock.AXIS,
                        treeDirection.getAxis()
                );

        attachments.add(generateBranch(
                level,
                trunkSetter,
                random,
                treeHeight,
                origin,
                tree,
                sidewaysStateModifier,
                treeDirection,
                firstBranchOffsetFromOrigin,
                firstBranchOffsetFromOrigin < trunkHeight - 1,
                logPos
        ));

        if (hasBothSideBranches) {
            attachments.add(generateBranch(
                    level,
                    trunkSetter,
                    random,
                    treeHeight,
                    origin,
                    tree,
                    sidewaysStateModifier,
                    treeDirection.getOpposite(),
                    secondBranchOffsetFromOrigin,
                    secondBranchOffsetFromOrigin < trunkHeight - 1,
                    logPos
            ));
        }

        return attachments;
    }

    private FoliagePlacer.FoliageAttachment generateBranch(
            WorldGenLevel level,
            BiConsumer<BlockPos, BlockState> trunkSetter,
            RandomSource random,
            int treeHeight,
            BlockPos origin,
            TreeFeature tree,
            Function<BlockState, BlockState> sidewaysStateModifier,
            Direction branchDirection,
            int offsetFromOrigin,
            boolean middleContinuesUpwards,
            BlockPos.MutableBlockPos logPos
    ) {
        logPos.set(origin).move(Direction.UP, offsetFromOrigin);

        int branchEndPosOffsetFromOrigin =
                treeHeight - 1 + branchEndOffsetFromTop.sample(random);

        boolean extendBranchAwayFromTrunk =
                middleContinuesUpwards
                        || branchEndPosOffsetFromOrigin < offsetFromOrigin;

        int distanceToTrunk =
                branchHorizontalLength.sample(random)
                        + (extendBranchAwayFromTrunk ? 1 : 0);

        BlockPos branchEndPos = origin
                .relative(branchDirection, distanceToTrunk)
                .above(branchEndPosOffsetFromOrigin);

        int stepsHorizontally = extendBranchAwayFromTrunk ? 2 : 1;

        for (int i = 0; i < stepsHorizontally; i++) {
            placeLog(
                    level,
                    trunkSetter,
                    random,
                    logPos.move(branchDirection),
                    tree,
                    sidewaysStateModifier
            );
        }

        Direction verticalDirection =
                branchEndPos.getY() > logPos.getY()
                        ? Direction.UP
                        : Direction.DOWN;

        while (true) {
            int distance = logPos.distManhattan(branchEndPos);

            if (distance == 0) {
                return new FoliagePlacer.FoliageAttachment(
                        branchEndPos.above(), 0, false
                );
            }

            float chanceToGrowVertically =
                    (float) Math.abs(branchEndPos.getY() - logPos.getY())
                            / distance;

            boolean growVertically =
                    random.nextFloat() < chanceToGrowVertically;

            logPos.move(
                    growVertically ? verticalDirection : branchDirection
            );

            placeLog(
                    level,
                    trunkSetter,
                    random,
                    logPos,
                    tree,
                    growVertically
                            ? Function.identity()
                            : sidewaysStateModifier
            );
        }
    }
}
