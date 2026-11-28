package net.chickalo.bugsbugsandmorebugs.entity.cavebeetle;

import java.util.EnumSet;
import net.chickalo.bugsbugsandmorebugs.block.ModBlocks;
import net.chickalo.bugsbugsandmorebugs.worldgen.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class CaveBeetleEntity extends PathfinderMob {
    private static final EntityDataAccessor<Integer> POSE = SynchedEntityData.defineId(CaveBeetleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FULL = SynchedEntityData.defineId(CaveBeetleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final int THRESHOLD = 10, DIGEST_TICKS = 600, CHEW_TICKS = 24;
    private int foodPoints, digestTicks, chewTicks, digTicks, digCooldown = 3600, curiosityCooldown;
    private boolean investigating;
    private CaveBeetlePose lastVisualPose;
    public final AnimationState visualAnimation = new AnimationState();

    public CaveBeetleEntity(EntityType<? extends CaveBeetleEntity> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createLivingAttributes().add(Attributes.MAX_HEALTH, 8.0)
            .add(Attributes.MOVEMENT_SPEED, 0.20).add(Attributes.FOLLOW_RANGE, 12.0)
            .add(Attributes.TEMPT_RANGE, 8.0);
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(POSE, CaveBeetlePose.IDLE.ordinal());
        builder.define(FULL, false);
    }
    public CaveBeetlePose beetlePose() { return CaveBeetlePose.values()[entityData.get(POSE)]; }
    private void pose(CaveBeetlePose p) { if (beetlePose() != p) entityData.set(POSE, p.ordinal()); }
    private boolean busy() { return chewTicks > 0 || digTicks > 0; }

    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.5));
        goalSelector.addGoal(2, new Goal() {
            { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP)); }
            @Override public boolean canUse() { return busy(); }
            @Override public boolean canContinueToUse() { return busy(); }
            @Override public void start() { getNavigation().stop(); }
            @Override public void tick() { getNavigation().stop(); }
        });
        goalSelector.addGoal(3, new TemptGoal(this, 0.9, stack -> !entityData.get(FULL) && BeetleFood.points(stack) > 0, false));
        goalSelector.addGoal(4, new InvestigateGoal());
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 5.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public static boolean canSpawn(EntityType<CaveBeetleEntity> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        if (!level.getLevel().dimension().equals(ModDimensions.ELSEWHEREDIM_LEVEL_KEY)) return false;
        if (pos.getY() < -54 || pos.getY() >= 40) return false;
        if (level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()) - pos.getY() < 14) return false;
        BlockPos below = pos.below();
        BlockState floor = level.getBlockState(below);
        if (!floor.isFaceSturdy(level, below, Direction.UP) || !level.getFluidState(pos).isEmpty()) return false;
        if (!level.noCollision(type.getSpawnAABB(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5))) return false;
        // Lantern meadows are favored; stone caves, galleries and dry pool banks are secondary.
        if (floor.is(ModBlocks.GRASSY_LOAM) || floor.is(ModBlocks.CAVE_MOSS) || floor.is(ModBlocks.ROOTED_LOAM)) return true;
        if (floor.is(ModBlocks.RHYOLITE) || floor.is(ModBlocks.SCHIST) || floor.is(ModBlocks.LOAM)) return random.nextInt(2) == 0;
        return (floor.is(ModBlocks.LIMESTONE) || floor.is(ModBlocks.FLOWSTONE) || floor.is(ModBlocks.AMPHIBOLITE)
            || floor.is(ModBlocks.WEATHERED_LIMESTONE) || floor.is(ModBlocks.CHERT)
            || floor.is(ModBlocks.DACITE) || floor.is(ModBlocks.SERPENTINITE)) && random.nextInt(4) == 0;
    }

    @Override public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        int points = BeetleFood.points(held);
        if (points == 0) return super.mobInteract(player, hand);
        if (level() instanceof ServerLevel server) {
            if (digestTicks > 0 || chewTicks > 0 || isInWater() || !isAlive()) return InteractionResult.SUCCESS;
            if (!player.isCreative()) held.shrink(1);
            setPersistenceRequired();
            foodPoints += points;
            chewTicks = CHEW_TICKS;
            digTicks = 0;
            investigating = false;
            getNavigation().stop();
            pose(CaveBeetlePose.CHEW);
            server.sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY()+0.45, getZ(), 3, 0.25, 0.15, 0.25, 0.01);
            if (foodPoints >= THRESHOLD) {
                digestTicks = DIGEST_TICKS;
                entityData.set(FULL, true);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        boolean hit = super.hurtServer(level, source, amount);
        if (hit) { chewTicks = 0; digTicks = 0; investigating = false; curiosityCooldown = 160; pose(CaveBeetlePose.WALK); }
        return hit;
    }

    @Override public void tick() {
        super.tick();
        if (level().isClientSide()) {
            CaveBeetlePose p = beetlePose();
            if (p != lastVisualPose) { visualAnimation.start(tickCount); lastVisualPose = p; }
            return;
        }
        if (!(level() instanceof ServerLevel server) || !isAlive()) return;
        if (curiosityCooldown > 0) --curiosityCooldown;
        if (digCooldown > 0) --digCooldown;
        if (digestTicks > 0 && --digestTicks == 0) {
            foodPoints -= THRESHOLD;
            spawnAtLocation(server, new ItemStack(Items.BONE_MEAL));
            entityData.set(FULL, false);
        }
        if (isInWater() || !onGround()) { chewTicks = 0; digTicks = 0; investigating = false; }
        if (chewTicks > 0) {
            --chewTicks;
            pose(CaveBeetlePose.CHEW);
            return;
        }
        if (digTicks > 0) {
            BlockState floor = server.getBlockState(blockPosition().below());
            if (!floor.is(ModBlocks.RHYOLITE)) { digTicks = 0; }
            else {
                if (digTicks % 8 == 0) server.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, floor),
                    getX(), getY()+0.07, getZ(), 4, 0.3, 0.02, 0.3, 0.025);
                --digTicks;
                pose(CaveBeetlePose.DIG);
                return;
            }
        }
        if (investigating) { pose(CaveBeetlePose.INVESTIGATE); return; }
        boolean moving = getDeltaMovement().horizontalDistanceSqr() > 0.00001;
        if (!moving && getNavigation().isDone() && onGround() && !isInWater() && digCooldown == 0
                && server.getBlockState(blockPosition().below()).is(ModBlocks.RHYOLITE)) {
            // Cricket integration point: future bounded prey encounter after this non-destructive pose.
            digTicks = 48;
            digCooldown = 3600 + random.nextInt(3601);
            pose(CaveBeetlePose.DIG);
            return;
        }
        pose(moving ? CaveBeetlePose.WALK : CaveBeetlePose.IDLE);
    }

    private final class InvestigateGoal extends Goal {
        private Player target;
        private int started, arrived;
        InvestigateGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }
        @Override public boolean canUse() {
            if (busy() || curiosityCooldown > 0 || random.nextInt(60) != 0 || !(level() instanceof ServerLevel server)) return false;
            target = server.players().stream().filter(player -> !player.isSpectator() && player.isAlive() && distanceToSqr(player) <= 36)
                .min(java.util.Comparator.comparingDouble(player -> distanceToSqr(player))).orElse(null);
            return target != null;
        }
        @Override public void start() {
            started = tickCount; arrived = -1; curiosityCooldown = 200 + random.nextInt(201);
        }
        @Override public boolean canContinueToUse() {
            return target != null && target.isAlive() && !target.isSpectator() && distanceToSqr(target) <= 64
                && !busy() && tickCount - started < 240 && (arrived < 0 || tickCount - arrived < 120);
        }
        @Override public void tick() {
            getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (distanceToSqr(target) > 2.25 && arrived < 0) {
                if (tickCount % 10 < 2) getNavigation().moveTo(target, 0.75);
            } else {
                getNavigation().stop();
                if (arrived < 0) arrived = tickCount;
                investigating = true;
            }
        }
        @Override public void stop() { target = null; investigating = false; getNavigation().stop(); }
    }

    @Override protected void addAdditionalSaveData(ValueOutput out) {
        super.addAdditionalSaveData(out);
        out.putInt("FoodPoints", foodPoints);
        out.putInt("DigestTicks", digestTicks);
        out.putInt("DigCooldown", digCooldown);
    }
    @Override protected void readAdditionalSaveData(ValueInput in) {
        super.readAdditionalSaveData(in);
        foodPoints = Math.clamp(in.getIntOr("FoodPoints", 0), 0, 13);
        digestTicks = foodPoints >= THRESHOLD ? Math.clamp(in.getIntOr("DigestTicks", DIGEST_TICKS), 1, DIGEST_TICKS) : 0;
        digCooldown = Math.clamp(in.getIntOr("DigCooldown", 3600), 0, 7200);
        entityData.set(FULL, digestTicks > 0);
    }
}

