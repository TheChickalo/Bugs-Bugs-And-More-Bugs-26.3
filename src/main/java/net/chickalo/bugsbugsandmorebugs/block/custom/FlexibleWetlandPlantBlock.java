package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;

/** Two/three-block wetland plant with a waterlogged root and dry upper sections. */
public class FlexibleWetlandPlantBlock extends ThreeBlockWetlandPlantBlock {
    public static final BooleanProperty SHORT = BooleanProperty.create("short");
    private final int maxHeight;

    public FlexibleWetlandPlantBlock(int maxHeight, Properties properties) {
        super(properties);
        if (maxHeight < 2 || maxHeight > 3) {
            throw new IllegalArgumentException("height must be 2 or 3");
        }
        this.maxHeight = maxHeight;
        registerDefaultState(defaultBlockState().setValue(SHORT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHORT);
    }

    // Pick the height BEFORE placement, and store the choice in the placed root state.
    // If a three-block plant is selected but the ceiling is low, use its two-block form.
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos root = context.getClickedPos();
        var level = context.getLevel();
        if (!level.getFluidState(root).is(Fluids.WATER) || !level.getFluidState(root).isSource()) {
            return null;
        }
        if (!mayPlaceOn(level.getBlockState(root.below()), level, root.below())) {
            return null;
        }

        int height = chooseHeight(level, root, level.getRandom());
        if (height == 0) return null;
        return defaultBlockState()
                .setValue(PART, 0)
                .setValue(WATERLOGGED, true)
                .setValue(SHORT, height == 2);
    }

    private boolean hasClearance(LevelReader level, BlockPos root, int height) {
        if (root.getY() < level.getMinY() + 1 || root.getY() + height - 1 >= level.getMaxY()) {
            return false;
        }
        for (int part = 1; part < height; part++) {
            if (!level.isEmptyBlock(root.above(part))) return false;
        }
        return true;
    }

    private int chooseHeight(LevelReader level, BlockPos root, RandomSource random) {
        int preferred = maxHeight == 2 ? 2 : 2 + random.nextInt(2);
        if (hasClearance(level, root, preferred)) return preferred;
        if (preferred == 3 && hasClearance(level, root, 2)) return 2;
        return 0;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        // Never re-roll here: a second roll could place a third section inside a ceiling.
        int height = maxHeight == 2 || state.getValue(SHORT) ? 2 : 3;
        addUpper(level, pos, height);
    }

    private void addUpper(LevelAccessor level, BlockPos pos, int height) {
        for (int part = 1; part < height; part++) {
            level.setBlock(pos.above(part),
                    defaultBlockState().setValue(PART, part).setValue(WATERLOGGED, false)
                            .setValue(SHORT, height == 2 && part == 1), 3);
        }
    }

    @Override
    public boolean placeNaturally(WorldGenLevel level, BlockPos pos) {
        if (!level.getFluidState(pos).is(Fluids.WATER) || !level.getFluidState(pos).isSource()) return false;
        if (!mayPlaceOn(level.getBlockState(pos.below()), level, pos.below())) return false;

        int height = chooseHeight(level, pos, level.getRandom());
        if (height == 0) return false;
        level.setBlock(pos, defaultBlockState().setValue(PART, 0).setValue(WATERLOGGED, true)
                .setValue(SHORT, height == 2), 2);
        addUpper(level, pos, height);
        return true;
    }
}
