package net.chickalo.bugsbugsandmorebugs.entity.snail;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.phys.Vec3;
import net.chickalo.bugsbugsandmorebugs.item.ModItems;
import net.chickalo.bugsbugsandmorebugs.worldgen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.damagesource.DamageSource;

public class SnailEntity extends Animal {
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(SnailEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> MOSSY = SynchedEntityData.defineId(SnailEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> WAXED = SynchedEntityData.defineId(SnailEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> POSE = SynchedEntityData.defineId(SnailEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FRIENDLINESS = SynchedEntityData.defineId(SnailEntity.class, EntityDataSerializers.INT);
    public static final String[] SKINS = {"common", "golden", "chestnut", "banded", "ivory", "melanistic"};
    private static final int MOSS_TARGET = 60_000;
    private static final int SLIME_INTERVAL = 600;
    private int mossGrowth, slimeCooldown, poseTicks, hideTicks, forageTicks;
    private boolean peeked;
    private int feedCooldown, socialTicks, followCooldown;
    private String bondedPlayer = "";
    private boolean nearbyThreat;
    private final Map<UUID, Vec3> lastPlayerPositions = new HashMap<>();
    public int friendliness() { return entityData.get(FRIENDLINESS); }
    private void increaseFriendliness(int amount, Player player) {
        entityData.set(FRIENDLINESS, Math.clamp(friendliness() + amount, 0, 20));
        if (player != null) bondedPlayer = player.getUUID().toString();
    }
    private BlockPos lastDeposit;
    private TemptGoal temptation;
    public final AnimationState visualAnimation = new AnimationState();
    private SnailPose lastVisualPose;

    public SnailEntity(EntityType<? extends SnailEntity> type, Level level) { super(type, level); }
    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes().add(Attributes.MAX_HEALTH, 12).add(Attributes.MOVEMENT_SPEED, 0.08).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.TEMPT_RANGE, 10.0D);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0); builder.define(MOSSY, false); builder.define(WAXED, false); builder.define(POSE, 0); builder.define(FRIENDLINESS, 0);
    }
    public int variant() { return entityData.get(VARIANT); }
    public boolean mossy() { return entityData.get(MOSSY); }
    public boolean waxed() { return entityData.get(WAXED); }
    public SnailPose snailPose() { return SnailPose.byId(entityData.get(POSE)); }
    private void pose(SnailPose next) {
        if (snailPose() != next) { entityData.set(POSE, next.ordinal()); poseTicks = 0; }
    }
    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new Goal() {
            { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP)); }
            @Override public boolean canUse() { return snailPose().locksMovement(); }
            @Override public boolean canContinueToUse() { return canUse(); }
            @Override public void start() { getNavigation().stop(); }
            @Override public void tick() { getNavigation().stop(); }
        });
        goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        temptation = new TemptGoal(this, 1.15, this::isFood, false);
        goalSelector.addGoal(3, temptation);
        goalSelector.addGoal(4, new FollowParentGoal(this, 1.05));
        goalSelector.addGoal(5, new FriendlyFollowGoal());
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 5));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }
    public static boolean canSpawn(EntityType<SnailEntity> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)
                && level.getFluidState(pos).isEmpty() && level.getFluidState(pos.above()).isEmpty();
    }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData data) {
        if (reason != EntitySpawnReason.BREEDING) {
            int r = random.nextInt(100);
            entityData.set(VARIANT, r < 46 ? 0 : r < 66 ? 1 : r < 81 ? 2 : r < 93 ? 3 : 4);
        }
        return super.finalizeSpawn(level, difficulty, reason, data);
    }
    @Override public boolean isFood(ItemStack stack) { return stack.is(ModItems.DAIKON); }
    @Override public SnailEntity getBreedOffspring(ServerLevel level, AgeableMob partner) {
        SnailEntity child = SnailContent.SNAIL.create(level, EntitySpawnReason.BREEDING);
        if (child != null) {
            int other = partner instanceof SnailEntity s ? s.variant() : variant();
            int inherited = random.nextBoolean() ? variant() : other;
            child.entityData.set(VARIANT, random.nextInt(1200) == 0 ? 5 : inherited);
        }
        return child;
    }
    @Override public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        boolean protectedBefore = snailPose().protectedInShell();
        boolean hit = super.hurtServer(level, source, protectedBefore ? amount * 0.05F : amount);
        if (hit && isAlive() && source.getEntity() != null) {
            hideTicks = 160 + random.nextInt(141); peeked = false;
            if (!protectedBefore || snailPose() == SnailPose.EMERGE) pose(SnailPose.RETRACT);
            else if (snailPose() == SnailPose.PEEK) pose(SnailPose.SHELLED);
            getNavigation().stop();
        }
        return hit;
    }
    @Override
    public void knockback(double strength, double x, double z,
                          DamageSource source, float damage) {
        if (!snailPose().protectedInShell()) {
            super.knockback(strength, x, z, source, damage);
        }
    }

    @Override
    public void knockback(double strength, double x, double z,
                          DamageSource source, float damage,
                          boolean comesFromEffect) {
        if (!snailPose().protectedInShell()) {
            super.knockback(strength, x, z, source, damage, comesFromEffect);
        }
    }
    @Override public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!isBaby() && mossy() && stack.is(Items.SHEARS)) {
            if (level() instanceof ServerLevel server) {
                entityData.set(MOSSY, false); mossGrowth = 0;
                spawnAtLocation(server, new ItemStack(Items.MOSS_CARPET));
                if (!player.isCreative()) stack.hurtAndBreak(1, player, hand);
                server.sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY()+0.7, getZ(), 5, 0.3, 0.2, 0.3, 0.02);
                if (!snailPose().protectedInShell()) pose(SnailPose.SHAKE);
            }
            return InteractionResult.SUCCESS;
        }
        if (!isBaby() && !waxed() && stack.is(Items.HONEYCOMB)) {
            if (level() instanceof ServerLevel server) {
                entityData.set(WAXED, true); mossGrowth = 0;
                increaseFriendliness(5, player);
                if (!player.isCreative()) stack.shrink(1);
                server.sendParticles(ParticleTypes.WAX_ON, getX(), getY()+0.6, getZ(), 12, 0.4, 0.3, 0.4, 0.02);
                if (!snailPose().protectedInShell()) pose(SnailPose.HAPPY);
            }
            return InteractionResult.SUCCESS;
        }
        boolean food = isFood(stack);
        int before = stack.getCount();
        boolean wasInLove = isInLove();
        // Food is accepted even in the shell, so skittish snails can learn to trust.
        InteractionResult result = super.mobInteract(player, hand);
        if (food && result.consumesAction()) {
            if (!level().isClientSide() && (stack.getCount() < before || (!wasInLove && isInLove()) || (player.isCreative() && isBaby()))) {
                increaseFriendliness(2, player); feedCooldown = 100;
                if (!snailPose().protectedInShell()) pose(SnailPose.EATING);
            }
            return result;
        }
        // Trust snacks during breeding cooldown; no extra love mode or baby growth.
        if (food && friendliness() < 20 && feedCooldown == 0) {
            if (level() instanceof ServerLevel server) {
                if (!player.isCreative()) stack.shrink(1);
                increaseFriendliness(2, player); feedCooldown = 100;
                server.sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY()+0.7, getZ(), 4, 0.25, 0.2, 0.25, 0.02);
                if (!snailPose().protectedInShell()) pose(SnailPose.EATING);
            }
            return InteractionResult.SUCCESS;
        }
        return result;
    }
    @Override public void tick() {
        super.tick();
        if (level().isClientSide()) {
            SnailPose current = snailPose();
            if (current != lastVisualPose) { visualAnimation.start(tickCount); lastVisualPose = current; }
            return;
        }
        if (!(level() instanceof ServerLevel server) || !isAlive()) return;
        if (slimeCooldown > 0) --slimeCooldown;
        if (feedCooldown > 0) --feedCooldown;
        if (followCooldown > 0) --followCooldown;
        if (tickCount % 5 == 0) updateThreats(server);
        if (tickCount % 20 == 0 && friendliness() < 15) {
            boolean company = !server.getEntitiesOfClass(SnailEntity.class, getBoundingBox().inflate(5), s -> s != this && s.isAlive()).isEmpty();
            if (company) {
                socialTicks += 20;
                if (socialTicks >= 1200) { increaseFriendliness(1, null); socialTicks = 0; }
            }
        }
        if (!isBaby() && !waxed() && !mossy()) {
            var biome = server.getBiome(blockPosition());
            int increment = biome.is(ModBiomes.ANCIENT_CEDAR_RAINFOREST) || biome.is(ModBiomes.MOSSY_HINOKI_GROVE) ? 3 : 1;
            if (server.isRainingAt(blockPosition())) increment++;
            mossGrowth = Math.min(MOSS_TARGET, mossGrowth + increment);
            if (mossGrowth >= MOSS_TARGET) entityData.set(MOSSY, true);
        }
        // Extend the hiding timer while danger is nearby. Never restart retract every tick.
        if (nearbyThreat) {
            hideTicks = Math.max(hideTicks, 50); peeked = false;
            if (snailPose() == SnailPose.PEEK) pose(SnailPose.SHELLED);
            else if (snailPose() != SnailPose.RETRACT && snailPose() != SnailPose.SHELLED) pose(SnailPose.RETRACT);
            getNavigation().stop();
        }
        ++poseTicks;
        SnailPose current = snailPose();
        if (current.locksMovement()) {
            getNavigation().stop();
            if (hideTicks > 0) --hideTicks;
            switch (current) {
                case RETRACT -> { if (poseTicks >= current.ticks) pose(SnailPose.SHELLED); }
                case SHELLED -> {
                    if (!peeked && hideTicks <= 45) { peeked = true; pose(SnailPose.PEEK); }
                    else if (hideTicks <= 0) pose(SnailPose.EMERGE);
                }
                case PEEK -> { if (poseTicks >= current.ticks) pose(SnailPose.SHELLED); }
                case EATING -> { if (poseTicks >= current.ticks) pose(SnailPose.HAPPY); }
                default -> { if (poseTicks >= current.ticks) pose(SnailPose.IDLE); }
            }
            return;
        }
        boolean moving = getDeltaMovement().horizontalDistanceSqr() > 0.000004 && onGround();
        if (moving) {
            pose(SnailPose.CRAWL); forageTicks++;
            if (!isBaby() && slimeCooldown == 0 && forageTicks >= 30 && depositSlime(server)) {
                slimeCooldown = SLIME_INTERVAL; forageTicks = 0; pose(SnailPose.SLIME_DEPOSIT);
            }
        } else {
            if (temptation != null && temptation.isRunning()) pose(SnailPose.CURIOUS);
            else if (tickCount % 200 == 0 && random.nextInt(4) == 0) pose(SnailPose.SLEEP);
            else pose(SnailPose.IDLE);
        }
    }
    private void updateThreats(ServerLevel server) {
        // Any other living Mob is frightening, regardless of trust in players.
        nearbyThreat = !server.getEntitiesOfClass(Mob.class, getBoundingBox().inflate(3.5),
                m -> !(m instanceof SnailEntity) && m.isAlive() && distanceToSqr(m) <= 12.25).isEmpty();
        Map<UUID, Vec3> current = new HashMap<>();
        for (Player player : server.getEntitiesOfClass(Player.class, getBoundingBox().inflate(5), p -> !p.isSpectator() && p.isAlive())) {
            Vec3 pos = player.position();
            Vec3 old = lastPlayerPositions.get(player.getUUID());
            current.put(player.getUUID(), pos);
            if (distanceToSqr(player) > 12.25 || friendliness() >= 15) continue;
            // Sample actual horizontal displacement: server-side player velocity is not reliable for walking.
            double dx = old == null ? 0 : pos.x - old.x;
            double dz = old == null ? 0 : pos.z - old.z;
            boolean moving = dx*dx + dz*dz > 0.0025 || player.isSprinting();
            if (friendliness() < 5 ? moving : friendliness() < 10 ? moving && !player.isShiftKeyDown() : player.isSprinting()) nearbyThreat = true;
        }
        lastPlayerPositions.clear(); lastPlayerPositions.putAll(current);
    }
    private final class FriendlyFollowGoal extends Goal {
        private Player target;
        private int remaining;
        FriendlyFollowGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }
        @Override public boolean canUse() {
            if (friendliness() < 20 || bondedPlayer.isEmpty() || followCooldown > 0 || nearbyThreat || snailPose().locksMovement()
                    || !(level() instanceof ServerLevel server) || random.nextInt(60) != 0) return false;
            target = server.getEntitiesOfClass(Player.class, getBoundingBox().inflate(8), p -> !p.isSpectator() && p.isAlive()
                    && p.getUUID().toString().equals(bondedPlayer) && distanceToSqr(p) > 4 && distanceToSqr(p) <= 64)
                    .stream().findFirst().orElse(null);
            return target != null;
        }
        @Override public boolean canContinueToUse() {
            return target != null && target.isAlive() && !target.isSpectator() && remaining > 0 && !nearbyThreat
                    && !snailPose().locksMovement() && distanceToSqr(target) > 4 && distanceToSqr(target) <= 100;
        }
        @Override public void start() { remaining = 160; }
        @Override public void tick() {
            --remaining;
            if (target != null) {
                getLookControl().setLookAt(target, 20, 20);
                getNavigation().moveTo(target, 1.15);
            }
        }
        @Override public void stop() {
            target = null; getNavigation().stop(); followCooldown = 240 + random.nextInt(241);
        }
        @Override public boolean requiresUpdateEveryTick() { return true; }
    }
    private boolean depositSlime(ServerLevel level) {
        BlockPos pos = blockPosition();
        if (pos.equals(lastDeposit) || !level.getBlockState(pos.below()).is(BlockTags.SAND) || !level.getFluidState(pos).isEmpty()) return false;
        var existing = level.getBlockState(pos);
        if (existing.is(SnailContent.SLIME)) {
            int layers = existing.getValue(SnailSlimeBlock.LAYERS);
            if (layers >= 4) return false;
            if (!level.setBlock(pos, existing.setValue(SnailSlimeBlock.LAYERS, layers+1), 3)) return false;
        } else if (existing.isAir()) {
            if (!level.setBlock(pos, SnailContent.SLIME.defaultBlockState(), 3)) return false;
        } else return false;
        lastDeposit = pos.immutable();
        level.levelEvent(2001, pos, Block.getId(SnailContent.SLIME.defaultBlockState()));
        return true;
    }
    @Override protected void addAdditionalSaveData(ValueOutput out) {
        super.addAdditionalSaveData(out);
        out.putInt("Variant", variant()); out.putBoolean("Mossy", mossy()); out.putBoolean("Waxed", waxed());
        out.putInt("Friendliness", friendliness()); out.putString("BondedPlayer", bondedPlayer);
        out.putInt("SocialTicks", socialTicks);
        out.putInt("MossGrowth", mossGrowth); out.putInt("SlimeCooldown", slimeCooldown); out.putInt("HideTicks", hideTicks);
    }
    @Override protected void readAdditionalSaveData(ValueInput in) {
        super.readAdditionalSaveData(in);
        entityData.set(VARIANT, Math.clamp(in.getIntOr("Variant",0),0,5));
        entityData.set(MOSSY,in.getBooleanOr("Mossy",false)); entityData.set(WAXED,in.getBooleanOr("Waxed",false));
        entityData.set(FRIENDLINESS, Math.clamp(in.getIntOr("Friendliness",0),0,20));
        bondedPlayer = in.getStringOr("BondedPlayer", "");
        socialTicks = Math.clamp(in.getIntOr("SocialTicks",0),0,1199);
        mossGrowth = Math.clamp(in.getIntOr("MossGrowth",0),0,MOSS_TARGET); slimeCooldown = Math.clamp(in.getIntOr("SlimeCooldown",0),0,SLIME_INTERVAL);
        hideTicks = Math.clamp(in.getIntOr("HideTicks",0),0,300); if (hideTicks > 0) pose(SnailPose.SHELLED);
    }
}
