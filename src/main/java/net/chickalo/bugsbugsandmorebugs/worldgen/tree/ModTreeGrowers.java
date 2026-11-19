package net.chickalo.bugsbugsandmorebugs.worldgen.tree;

import net.chickalo.bugsbugsandmorebugs.BugsBugsAndMoreBugs;
import net.chickalo.bugsbugsandmorebugs.worldgen.ModFeatures;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class ModTreeGrowers {
    public static final TreeGrower MAPLE = new TreeGrower(BugsBugsAndMoreBugs.MOD_ID + ":maple",
            WeightedList.of(ModFeatures.MAPLE_KEY), WeightedList.of(), WeightedList.of(), null);
    public static final TreeGrower GINKGO = new TreeGrower(BugsBugsAndMoreBugs.MOD_ID + ":ginkgo",
            WeightedList.of(ModFeatures.GINKGO_TREE_KEY), WeightedList.of(), WeightedList.of(), null);

    public static final TreeGrower HINOKI = new TreeGrower(BugsBugsAndMoreBugs.MOD_ID + ":hinoki",
            WeightedList.of(ModFeatures.HINOKI_KEY), WeightedList.of(), WeightedList.of(), null);

}
