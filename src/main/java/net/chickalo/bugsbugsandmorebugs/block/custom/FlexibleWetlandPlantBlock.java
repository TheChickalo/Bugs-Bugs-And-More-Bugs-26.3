package net.chickalo.bugsbugsandmorebugs.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;

/** Two/three-block tall wetland plant with one waterlogged root and dry upper sections. */
public class FlexibleWetlandPlantBlock extends ThreeBlockWetlandPlantBlock {
    public static final BooleanProperty SHORT = BooleanProperty.create("short");
    private final int maxHeight;

    public FlexibleWetlandPlantBlock(int maxHeight, Properties properties) {
        super(properties);
        if (maxHeight < 2 || maxHeight > 3) throw new IllegalArgumentException("height must be 2 or 3");
        this.maxHeight = maxHeight;
        registerDefaultState(defaultBlockState().setValue(SHORT, false));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHORT);
    }

    @Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        int height = maxHeight == 2 ? 2 : 2 + level.getRandom().nextInt(2);
        addUpper(level, pos, height);
    }

    private void addUpper(LevelAccessor level, BlockPos pos, int height) {
        for (int p = 1; p < height; p++) level.setBlock(pos.above(p),
                defaultBlockState().setValue(PART, p).setValue(WATERLOGGED, false)
                        .setValue(SHORT, height == 2 && p == 1), 3);
    }

    @Override public boolean placeNaturally(WorldGenLevel level, BlockPos pos) {
        int height = maxHeight == 2 ? 2 : 2 + level.getRandom().nextInt(2);
        if (pos.getY() < level.getMinY()+1 || pos.getY()+height >= level.getMaxY()) return false;
        if (!level.getFluidState(pos).is(Fluids.WATER) || !level.getFluidState(pos).isSource()) return false;
        if (!mayPlaceOn(level.getBlockState(pos.below()), level, pos.below())) return false;
        for (int i = 1; i < height; i++) if (!level.isEmptyBlock(pos.above(i))) return false;
        level.setBlock(pos, defaultBlockState().setValue(PART, 0).setValue(WATERLOGGED, true), 2);
        addUpper(level, pos, height);
        return true;
    }
}
