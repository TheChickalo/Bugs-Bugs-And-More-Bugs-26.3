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
    private static final EntityDataAccessor<Boolean> CLIMBING = SynchedEntityData.defineId(SnailEntity.class, EntityDataSerializers.BOOLEAN);
    public boolean climbing() { return entityData.get(CLIMBING); }
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
        builder.define(VARIANT, 0); builder.define(MOSSY, false); builder.define(WAXED, false); builder.define(POSE, 0); builder.define(FRIENDLINESS, 0); builder.define(CLIMBING, false);
    }
    public int variant() { return entityData.get(VARIANT); }
    public boolean mossy() { return entityData.get(MOSSY); }
    public boolean waxed() { return entityData.get(WAXED); }
    public SnailPose snailPose() { return SnailPose.byId(entityData.get(POSE)); }
    private void pose(SnailPose next) {
        if (snailPose() != next) { entityData.set(POSE, next.ordinal()); poseTicks = 0; }
    }
    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new SeekAirGoal());
        goalSelector.addGoal(5, new UnderwaterForageGoal());
        goalSelector.addGoal(1, new Goal() {
            { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP)); }
            @Override public boolean canUse() { return snailPose().locksMovement() && !seekingAir && climbWall == null; }
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
        var box = type.getSpawnAABB(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5);
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)
                && level.getFluidState(pos).isEmpty() && level.getFluidState(pos.above()).isEmpty()
                && level.noCollision(box) && !hasBerryBush(level, box);
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
            int otherTrust = partner instanceof SnailEntity parent ? parent.friendliness() : 0;
            child.entityData.set(FRIENDLINESS, (friendliness()+otherTrust)/8);
        }
        return child;
    }
    @Override public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        boolean protectedBefore = snailPose().protectedInShell();
        boolean hit = super.hurtServer(level, source, protectedBefore ? amount * 0.05F : amount);
        if (hit && isAlive() && source.getEntity() != null) {
            if (source.getEntity() instanceof Player player && !player.isCreative()) {
                entityData.set(FRIENDLINESS, Math.max(0, friendliness()-4));
            }
            hideTicks = 120 + random.nextInt(381); peeked = false;
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
        // An axe removes wax without harming the snail.
        if (waxed() && stack.is(net.minecraft.tags.ItemTags.AXES)) {
            if (level() instanceof ServerLevel server) {
                entityData.set(WAXED, false); mossGrowth = 0;
                if (!player.isCreative()) stack.hurtAndBreak(1, player, hand);
                server.sendParticles(ParticleTypes.WAX_OFF,getX(),getY()+0.6,getZ(),10,0.4,0.3,0.4,0.02);
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
        // Escape environmental hazards even when a player or another mob is nearby.
        if (escapeEnvironment(server)) return;
        // Extend the hiding timer while danger is nearby. Never restart retract every tick.
        if (isInWater()) {
            ++wetTicks;
            if (wetTicks >= 120 || getAirSupply() < 160) seekingAir = true;
        } else { wetTicks=0; seekingAir=false; }
        if (seekingAir) {
            if (climbWall!=null) stopClimbing();
            hideTicks=0; peeked=false; pose(SnailPose.CRAWL); return;
        }
        if (updateClimbing(server)) return;
        if (nearbyThreat) {
            // Choose a new delay on the first scare; refresh only a short minimum while danger persists.
            if (!snailPose().protectedInShell()) hideTicks = 80 + random.nextInt(281);
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
                    if (!nearbyThreat && !peeked && hideTicks <= 45) { peeked = true; pose(SnailPose.PEEK); }
                    else if (hideTicks <= 0) pose(SnailPose.EMERGE);
                }
                case PEEK -> { if (poseTicks >= current.ticks) pose(SnailPose.SHELLED); }
                case EATING -> { if (poseTicks >= current.ticks) pose(SnailPose.HAPPY); }
                default -> { if (poseTicks >= current.ticks) pose(SnailPose.IDLE); }
            }
            return;
        }
        explore(server);
        if (snailPose().locksMovement()) return;
        boolean moving = getDeltaMovement().horizontalDistanceSqr() > 0.000004 && (onGround() || isInWater());
        if (moving) {
            pose(SnailPose.CRAWL); forageTicks++;
            if (!isInWater() && !isBaby() && slimeCooldown == 0 && forageTicks >= 30 && depositSlime(server)) {
                slimeCooldown = SLIME_INTERVAL; forageTicks = 0; pose(SnailPose.SLIME_DEPOSIT);
            }
        } else {
            if (temptation != null && temptation.isRunning()) pose(SnailPose.CURIOUS);
            else if (!isInWater() && !server.isRainingAt(blockPosition()) && tickCount % 200 == 0 && random.nextInt(4) == 0) pose(SnailPose.SLEEP);
            else pose(SnailPose.IDLE);
        }
    }
    // Snail survival patch: environmental escape takes priority over hiding.
    private static boolean hasBerryBush(net.minecraft.world.level.BlockGetter level, net.minecraft.world.phys.AABB box) {
        for (int x = (int)Math.floor(box.minX); x <= (int)Math.floor(box.maxX - 0.0001); x++)
            for (int y = (int)Math.floor(box.minY); y <= (int)Math.floor(box.maxY - 0.0001); y++)
                for (int z = (int)Math.floor(box.minZ); z <= (int)Math.floor(box.maxZ - 0.0001); z++) {
                    var state = level.getBlockState(new BlockPos(x, y, z));
                    if (state.getBlock() instanceof net.minecraft.world.level.block.SweetBerryBushBlock) return true;
                }
        return false;
    }

    private boolean escapeEnvironment(ServerLevel server) {
        boolean inWall = isInWall();
        boolean inBush = hasBerryBush(server, getBoundingBox());
        if (!inWall && !inBush) return false;
        hideTicks = 0;
        peeked = false;
        pose(SnailPose.CRAWL);
        getNavigation().stop();
        // Search only the immediate area; never move into an unloaded chunk,
        // a solid block, a bush, water or a position with no solid floor.
        BlockPos origin = blockPosition();
        BlockPos best = null;
        double bestDistance = Double.POSITIVE_INFINITY;
        for (int dy = 0; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) {
            BlockPos candidate = origin.offset(dx, dy, dz);
            if (!server.hasChunkAt(candidate)) continue;
            if (!server.getBlockState(candidate.below()).isFaceSturdy(server, candidate.below(), Direction.UP)) continue;
            if (!server.getFluidState(candidate).isEmpty() || !server.getFluidState(candidate.above()).isEmpty()) continue;
            if (getType().isBlockDangerous(server.getBlockState(candidate)) || getType().isBlockDangerous(server.getBlockState(candidate.below()))) continue;
            double x = candidate.getX()+0.5, y = candidate.getY(), z = candidate.getZ()+0.5;
            var box = getBoundingBox().move(x-getX(), y-getY(), z-getZ());
            if (!server.noCollision(this, box) || hasBerryBush(server, box)) continue;
            double d = (x-getX())*(x-getX()) + (y-getY())*(y-getY()) + (z-getZ())*(z-getZ());
            if (d < bestDistance) { bestDistance = d; best = candidate; }
        }
        if (best != null) {
            double x = best.getX()+0.5, y = best.getY(), z = best.getZ()+0.5;
            if (inWall) {
                // A mob already intersecting solid collision may not be able to path out.
                // Correct its position only to a checked nearby free location.
                setPos(x, y, z);
                setDeltaMovement(Vec3.ZERO);
            } else {
                // A berry bush is non-solid; crawl out without teleportation.
                getMoveControl().setWantedPosition(x, y, z, 1.5);
            }
        }
        return true;
    }

    // Snail V3: terrain, water and trust
    private int wetTicks, escapeRetry, climbCooldown, climbTicks, exploreCooldown;
    private boolean seekingAir;
    private Direction climbWall;
    private BlockPos climbTop;

    @Override protected net.minecraft.world.entity.ai.navigation.PathNavigation createNavigation(Level level) {
        return new net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation(this, level);
    }

    @Override public boolean canBeLeashed() { return true; }

    // No FloatGoal: naturally sink while exploring shallow water.
    @Override protected void travelInWater(Vec3 input, double gravity, boolean falling, double oldY) {
        super.travelInWater(input, gravity, falling, oldY);
        if (!seekingAir) {
            Vec3 v = getDeltaMovement();
            setDeltaMovement(v.x, Math.min(v.y, -0.025), v.z);
        }
    }

    private boolean dryFloor(ServerLevel server, BlockPos p) {
        if (!server.hasChunkAt(p) || !server.getFluidState(p).isEmpty()
                || !server.getFluidState(p.above()).isEmpty()
                || !server.getBlockState(p.below()).isFaceSturdy(server, p.below(), Direction.UP)) return false;
        var box = getBoundingBox().move(p.getX()+0.5-getX(), p.getY()-getY(), p.getZ()+0.5-getZ());
        return server.noCollision(this, box) && !hasBerryBush(server, box)
                && !getType().isBlockDangerous(server.getBlockState(p))
                && !getType().isBlockDangerous(server.getBlockState(p.below()));
    }

    private final class SeekAirGoal extends Goal {
        SeekAirGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP)); }
        @Override public boolean canUse() { return seekingAir && isInWater(); }
        @Override public boolean canContinueToUse() { return canUse(); }
        @Override public void start() { escapeRetry = 0; getNavigation().stop(); }
        @Override public void stop() { getNavigation().stop(); escapeRetry = 0; }
        @Override public boolean requiresUpdateEveryTick() { return true; }
        @Override public void tick() {
            if (!(level() instanceof ServerLevel server)) return;
            if (--escapeRetry <= 0) {
                escapeRetry = 30;
                BlockPos origin = blockPosition();
                java.util.List<BlockPos> choices = new java.util.ArrayList<>();
                for (int dx=-8; dx<=8; dx++) for (int dz=-8; dz<=8; dz++) {
                    if (dx*dx+dz*dz > 64) continue;
                    for (int dy=-1; dy<=5; dy++) {
                        BlockPos p = origin.offset(dx,dy,dz);
                        if (dryFloor(server,p)) choices.add(p);
                    }
                }
                choices.sort(java.util.Comparator.comparingDouble(p -> distanceToSqr(p.getX()+0.5,p.getY(),p.getZ()+0.5)));
                int tried=0;
                for (BlockPos p : choices) {
                    if (++tried > 6) break;
                    var path = getNavigation().createPath(p, 0);
                    if (path != null && path.canReach() && getNavigation().moveTo(path, 1.2)) break;
                }
            }
            // If no shore route is available, seek the surface for breath.
            // Normal exploration never uses this upward impulse.
            if (getAirSupply() < 100 || getNavigation().isDone()) {
                Vec3 v=getDeltaMovement();
                setDeltaMovement(v.x, Math.max(v.y, 0.11), v.z);
            }
        }
    }

    private final class UnderwaterForageGoal extends Goal {
        private int remaining;
        private net.minecraft.world.level.pathfinder.Path route;
        UnderwaterForageGoal() { setFlags(EnumSet.of(Flag.MOVE)); }
        @Override public boolean canUse() {
            if (!isInWater() || seekingAir || snailPose().locksMovement() || !(level() instanceof ServerLevel server)) return false;
            BlockPos origin=blockPosition();
            for(int i=0;i<8;i++) {
                BlockPos p=origin.offset(random.nextInt(9)-4,random.nextInt(3)-1,random.nextInt(9)-4);
                if (!server.hasChunkAt(p) || !server.getFluidState(p).is(net.minecraft.tags.FluidTags.WATER)
                        || !server.getBlockState(p.below()).isFaceSturdy(server,p.below(),Direction.UP)) continue;
                var box=getBoundingBox().move(p.getX()+0.5-getX(),p.getY()-getY(),p.getZ()+0.5-getZ());
                if (!server.noCollision(SnailEntity.this,box) || hasBerryBush(server,box)) continue;
                route=getNavigation().createPath(p,0);
                if(route!=null && route.canReach()) return true;
            }
            return false;
        }
        @Override public boolean canContinueToUse() { return remaining>0 && isInWater() && !seekingAir && !snailPose().locksMovement() && !getNavigation().isDone(); }
        @Override public void start() { remaining=80; getNavigation().moveTo(route,1.0); }
        @Override public void tick() { --remaining; }
        @Override public void stop() { getNavigation().stop(); route=null; }
        @Override public boolean requiresUpdateEveryTick() { return true; }
    }

    private boolean climbable(ServerLevel server, BlockPos p) {
        if (!server.hasChunkAt(p)) return false;
        var s=server.getBlockState(p);
        return s.canOcclude() && s.isCollisionShapeFullBlock(server,p)
                && !s.is(net.minecraft.tags.BlockTags.LEAVES)
                && !getType().isBlockDangerous(s);
    }

    private void stopClimbing() {
        climbWall=null; climbTop=null; climbTicks=0; climbCooldown=120+random.nextInt(160);
        entityData.set(CLIMBING,false);
    }

    private boolean updateClimbing(ServerLevel server) {
        if (climbCooldown>0) --climbCooldown;
        if (climbWall!=null) {
            if (++climbTicks>160 || isInWater() || nearbyThreat || snailPose().locksMovement()
                    || climbTop==null || !dryFloor(server,climbTop)) { stopClimbing(); return false; }
            if (getY()<climbTop.getY()-0.01) {
                int height=(int)Math.floor(getY()+0.01);
                BlockPos wall=new BlockPos(climbTop.getX(),height,climbTop.getZ());
                if (!climbable(server,wall)) { stopClimbing(); return false; }
            }
            if (getY()>=climbTop.getY()-0.01 && Math.abs(getX()-climbTop.getX()-0.5)<0.15
                    && Math.abs(getZ()-climbTop.getZ()-0.5)<0.15) { stopClimbing(); return false; }
            getNavigation().stop(); pose(SnailPose.CRAWL);
            setYRot(climbWall.toYRot()); setYBodyRot(climbWall.toYRot()); setYHeadRot(climbWall.toYRot());
            entityData.set(CLIMBING,getY()<climbTop.getY()-0.01);
            return true;
        }
        if (climbCooldown>0 || !onGround() || isInWater() || nearbyThreat || snailPose().locksMovement()
                || tickCount%10!=0 || random.nextInt(3)!=0) return false;
        // Local wall-climbing, not unrestricted 3-D pathfinding. Require an actual accessible top.
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos wall=blockPosition().relative(dir);
            if (!climbable(server,wall)) continue;
            double cx=wall.getX()+0.5, cz=wall.getZ()+0.5;
            double along=(cx-getX())*dir.getStepX()+(cz-getZ())*dir.getStepZ();
            double across=Math.abs((cx-getX())*dir.getStepZ()-(cz-getZ())*dir.getStepX());
            if (along>0.5+getBbWidth()/2+0.20 || across>0.25) continue;
            for (int h=1; h<=4; h++) {
                BlockPos top=wall.above(h);
                if (dryFloor(server,top)) {
                    // Check the whole ascent on the near side of the wall.
                    boolean clear=true;
                    for (int k=1;k<=h;k++) {
                        var box=getBoundingBox().move(0,k,0);
                        if (!server.noCollision(this,box) || hasBerryBush(server,box)) { clear=false; break; }
                    }
                    if (clear) { climbWall=dir; climbTop=top; climbTicks=0; getNavigation().stop();
                        pose(SnailPose.CRAWL); entityData.set(CLIMBING,true); return true; }
                    break;
                }
                if (!climbable(server,top)) break;
            }
        }
        return false;
    }

    @Override public void travel(Vec3 input) {
        if (!level().isClientSide() && climbWall!=null && climbTop!=null) {
            // Collision-aware movement; no teleporting or permanent NoGravity flag.
            double rise=climbTop.getY()-getY();
            if (rise>0.01) {
                move(MoverType.SELF,new Vec3(climbWall.getStepX()*0.025,Math.min(0.09,rise),climbWall.getStepZ()*0.025));
            } else {
                double dx=climbTop.getX()+0.5-getX(), dz=climbTop.getZ()+0.5-getZ();
                move(MoverType.SELF,new Vec3(Math.clamp(dx,-0.08,0.08),0,Math.clamp(dz,-0.08,0.08)));
            }
            resetFallDistance(); setDeltaMovement(Vec3.ZERO); return;
        }
        super.travel(input);
    }

    private boolean attractivePlant(ServerLevel server, BlockPos p) {
        var state=server.getBlockState(p);
        String name=net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        return name.equals("moss_block") || name.equals("moss_carpet") || name.contains("mushroom")
                || name.contains("fallen_") || name.contains("clubmoss") || name.contains("glowshroom")
                || name.equals("liverwort") || name.equals("pink_clover");
    }

    private void explore(ServerLevel server) {
        if (exploreCooldown>0) { --exploreCooldown; return; }
        if (!getNavigation().isDone() || isInWater() || snailPose().locksMovement() || nearbyThreat) return;
        boolean rain=server.isRainingAt(blockPosition());
        exploreCooldown=rain?40+random.nextInt(61):160+random.nextInt(181);
        BlockPos base=blockPosition();
        // Occasionally rest close to other snails, or investigate vegetation; no plant destruction.
        var company=server.getEntitiesOfClass(SnailEntity.class,getBoundingBox().inflate(5),s->s!=this && s.isAlive());
        if (!rain && !company.isEmpty() && random.nextInt(3)==0) {
            SnailEntity other=company.get(random.nextInt(company.size()));
            if (distanceToSqr(other)<4) { pose(SnailPose.SLEEP); return; }
            getNavigation().moveTo(other,0.85); return;
        }
        for (int i=0;i<14;i++) {
            BlockPos p=base.offset(random.nextInt(9)-4,random.nextInt(3)-1,random.nextInt(9)-4);
            if (!dryFloor(server,p)) continue;
            boolean plant=attractivePlant(server,p)||attractivePlant(server,p.below());
            for(Direction d:Direction.Plane.HORIZONTAL) plant |= attractivePlant(server,p.relative(d));
            if (!plant && !rain) continue;
            var path=getNavigation().createPath(p,0);
            if(path!=null && path.canReach() && getNavigation().moveTo(path,1.0)) return;
        }
    }

    private void updateThreats(ServerLevel server) {
        boolean wasThreat = nearbyThreat;
        // Any other living Mob is frightening, regardless of trust in players.
        nearbyThreat = !server.getEntitiesOfClass(Mob.class, getBoundingBox().inflate(3.5),
                m -> !(m instanceof SnailEntity) && m.isAlive() && distanceToSqr(m) <= 12.25).isEmpty();
        Map<UUID, Vec3> current = new HashMap<>();
        for (Player player : server.getEntitiesOfClass(Player.class, getBoundingBox().inflate(5), p -> !p.isSpectator() && !p.isCreative() && p.isAlive())) {
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
        if (wasThreat && !nearbyThreat && snailPose().protectedInShell()) {
            hideTicks = Math.max(hideTicks, 60 + random.nextInt(241)); peeked = false;
        }
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
        hideTicks = Math.clamp(in.getIntOr("HideTicks",0),0,500); if (hideTicks > 0) pose(SnailPose.SHELLED);
    }
}
