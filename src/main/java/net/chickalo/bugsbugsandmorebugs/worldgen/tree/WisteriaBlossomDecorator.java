package net.chickalo.bugsbugsandmorebugs.worldgen.tree;
import com.mojang.serialization.MapCodec;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.block.custom.HangingAccentBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

/** Decorates the vanilla tree pipeline, after the Maple-style trunk and leaves. */
public final class WisteriaBlossomDecorator extends TreeDecorator {
    public static final MapCodec<WisteriaBlossomDecorator> CODEC = MapCodec.unit(new WisteriaBlossomDecorator());
    @Override protected TreeDecoratorType<?> type() { return ModTreeDecoratorTypes.WISTERIA_BLOSSOMS; }
    @Override public void place(Context context) {
        var random = context.random();
        for (BlockPos leaf : context.leaves()) {
            if (random.nextFloat() >= 0.13F || !context.isAir(leaf.below())) continue;
            if (!context.checkBlock(leaf, state -> state.is(ModBlocks.WISTERIA_LEAVES))) continue;
            int max = 2 + random.nextInt(4);
            int placed = 0;
            for (int i=0; i<max; i++) {
                BlockPos site = leaf.below(i+1);
                if (!context.isAir(site)) break;
                context.setBlock(site, ModBlocks.HANGING_WISTERIA_BLOSSOMS.defaultBlockState()
                        .setValue(HangingAccentBlock.TIP,false));
                placed++;
            }
            if (placed>0) context.setBlock(leaf.below(placed),
                    ModBlocks.HANGING_WISTERIA_BLOSSOMS.defaultBlockState()
                            .setValue(HangingAccentBlock.TIP,true));
        }
        Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        for (BlockPos log : context.logs()) {
            if (random.nextInt(6)!=0) continue;
            Direction outward = directions[random.nextInt(4)];
            BlockPos site = log.relative(outward);
            if (!context.isAir(site)) continue;
            BlockState vine = ModBlocks.CLIMBING_WISTERIA_VINE.defaultBlockState();
            switch (outward.getOpposite()) {
                case NORTH -> vine = vine.setValue(VineBlock.NORTH,true);
                case SOUTH -> vine = vine.setValue(VineBlock.SOUTH,true);
                case EAST -> vine = vine.setValue(VineBlock.EAST,true);
                case WEST -> vine = vine.setValue(VineBlock.WEST,true);
                default -> { }
            }
            context.setBlock(site,vine);
        }
    }
}
