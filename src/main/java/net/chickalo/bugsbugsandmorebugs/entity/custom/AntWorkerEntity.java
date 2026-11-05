package net.chickalo.bugsbugsandmorebugs.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;

public class AntWorkerEntity extends PathfinderMob {
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;


    public AntWorkerEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // i want to say that for prio: 1 i wanted to make a system for when the ant is attacked to "revenge"

        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1f, true));

        // vvv eventually replace this with code for eating honey & such and bringing it back to nest structure
        this.goalSelector.addGoal(3, new MoveToBlockGoal(this, 1.2f, 10) {
            @Override
            protected boolean isValidTarget(LevelReader level, BlockPos pos) {
                return level.getBlockState(pos).is(Blocks.HONEY_BLOCK) || level.getBlockState(pos).is(Blocks.HONEYCOMB_BLOCK);            }
        });

        // cont. here, actually stuff i plan on keeping
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1f));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1f));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

    }

    // ANIMATIONS
    private void setupAnimationStates() {
        if(this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 20;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if(this.level().isClientSide()) {
            this.setupAnimationStates();
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 4D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 2D)
                .add(Attributes.FOLLOW_RANGE, 32D);
    }

}
