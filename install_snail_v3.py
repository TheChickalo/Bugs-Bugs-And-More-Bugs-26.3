#!/usr/bin/env python3
"""Incremental snail behavior update. Run in project root: py install_snail_v3.py ."""
from pathlib import Path
import datetime, shutil, sys

ROOT=Path(sys.argv[1] if len(sys.argv)>1 else '.').resolve()
BASE=ROOT/'src/main/java/net/chickalo/bugsbugsandmorebugs/entity/snail'
MARK='// Snail V3: terrain, water and trust'

def swap(s,a,b):
    if s.count(a)!=1: raise ValueError('Could not match exactly once: '+a[:110])
    return s.replace(a,b,1)

HELPERS='''    // Snail V3: terrain, water and trust
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

'''

def patch(s):
    if MARK in s: return s
    if 'private boolean escapeEnvironment(' not in s or 'private static boolean hasBerryBush(' not in s:
        raise ValueError('Install the previous survival patch before V3; it is required for safe movement.')
    s=swap(s,'    public static final String[] SKINS', '    private static final EntityDataAccessor<Boolean> CLIMBING = SynchedEntityData.defineId(SnailEntity.class, EntityDataSerializers.BOOLEAN);\n    public boolean climbing() { return entityData.get(CLIMBING); }\n    public static final String[] SKINS')
    s=swap(s,'builder.define(FRIENDLINESS, 0);','builder.define(FRIENDLINESS, 0); builder.define(CLIMBING, false);')
    s=swap(s,'goalSelector.addGoal(0, new FloatGoal(this));','goalSelector.addGoal(0, new SeekAirGoal());\n        goalSelector.addGoal(5, new UnderwaterForageGoal());')
    s=swap(s,'return snailPose().locksMovement();','return snailPose().locksMovement() && !seekingAir && climbWall == null;')
    s=swap(s,'p -> !p.isSpectator() && p.isAlive()))','p -> !p.isSpectator() && !p.isCreative() && p.isAlive()))')
    s=swap(s,'hideTicks = 160 + random.nextInt(141); peeked = false;', '''if (source.getEntity() instanceof Player player && !player.isCreative()) {
                entityData.set(FRIENDLINESS, Math.max(0, friendliness()-4));
            }
            hideTicks = 120 + random.nextInt(381); peeked = false;''')
    s=swap(s,'            child.entityData.set(VARIANT, random.nextInt(1200) == 0 ? 5 : inherited);','''            child.entityData.set(VARIANT, random.nextInt(1200) == 0 ? 5 : inherited);
            int otherTrust = partner instanceof SnailEntity parent ? parent.friendliness() : 0;
            child.entityData.set(FRIENDLINESS, (friendliness()+otherTrust)/8);''')
    s=swap(s,'        boolean food = isFood(stack);','''        // An axe removes wax without harming the snail.
        if (waxed() && stack.is(net.minecraft.tags.ItemTags.AXES)) {
            if (level() instanceof ServerLevel server) {
                entityData.set(WAXED, false); mossGrowth = 0;
                if (!player.isCreative()) stack.hurtAndBreak(1, player, hand);
                server.sendParticles(ParticleTypes.WAX_OFF,getX(),getY()+0.6,getZ(),10,0.4,0.3,0.4,0.02);
            }
            return InteractionResult.SUCCESS;
        }
        boolean food = isFood(stack);''')
    s=swap(s,'        if (nearbyThreat) {\n            hideTicks = Math.max(hideTicks, 50); peeked = false;', '''        if (isInWater()) {
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
            hideTicks = Math.max(hideTicks, 50); peeked = false;''')
    s=swap(s,'if (!peeked && hideTicks <= 45) { peeked = true; pose(SnailPose.PEEK); }','if (!nearbyThreat && !peeked && hideTicks <= 45) { peeked = true; pose(SnailPose.PEEK); }')
    s=swap(s,'boolean moving = getDeltaMovement().horizontalDistanceSqr() > 0.000004 && onGround();','boolean moving = getDeltaMovement().horizontalDistanceSqr() > 0.000004 && (onGround() || isInWater());')
    s=swap(s,'if (!isBaby() && slimeCooldown == 0 && forageTicks >= 30 && depositSlime(server))','if (!isInWater() && !isBaby() && slimeCooldown == 0 && forageTicks >= 30 && depositSlime(server))')
    s=swap(s,'else if (tickCount % 200 == 0 && random.nextInt(4) == 0) pose(SnailPose.SLEEP);','else if (!isInWater() && !server.isRainingAt(blockPosition()) && tickCount % 200 == 0 && random.nextInt(4) == 0) pose(SnailPose.SLEEP);')
    s=swap(s,'    private void updateThreats(ServerLevel server) {','    '+HELPERS.lstrip()+'    private void updateThreats(ServerLevel server) {')
    s=swap(s,'        // Any other living Mob is frightening, regardless of trust in players.', '        boolean wasThreat = nearbyThreat;\n        // Any other living Mob is frightening, regardless of trust in players.')
    s=swap(s,'        lastPlayerPositions.clear(); lastPlayerPositions.putAll(current);', '        lastPlayerPositions.clear(); lastPlayerPositions.putAll(current);\n        if (wasThreat && !nearbyThreat && snailPose().protectedInShell()) {\n            hideTicks = Math.max(hideTicks, 60 + random.nextInt(241)); peeked = false;\n        }')
    s=swap(s,'hideTicks = Math.clamp(in.getIntOr("HideTicks",0),0,300);','hideTicks = Math.clamp(in.getIntOr("HideTicks",0),0,500);')
    s=swap(s,'            return;\n        }\n        boolean moving =','            return;\n        }\n        explore(server);\n        if (snailPose().locksMovement()) return;\n        boolean moving =')
    return s

def main():
    paths=[BASE/'SnailEntity.java',BASE/'client/SnailRenderState.java',BASE/'client/SnailRenderer.java',BASE/'client/SnailModel.java']
    old={p:p.read_text(encoding='utf-8') for p in paths}
    e,rs,r,m=paths
    new=dict(old);new[e]=patch(old[e])
    if 'public boolean climbing;' not in old[rs]:
        new[rs]=swap(old[rs],'extends LivingEntityRenderState {','extends LivingEntityRenderState {\n    public boolean climbing;')
    if 'state.climbing = entity.climbing();' not in old[r]:
        new[r]=swap(old[r],'state.animation.copyFrom(entity.visualAnimation);','state.climbing = entity.climbing();\n        state.animation.copyFrom(entity.visualAnimation);')
    if 'if (state.climbing)' not in old[m]:
        new[m]=swap(old[m],'        if (state.babySnail) {','''        if (state.climbing) {
            ModelPart climbingRoot = root().getChild("root");
            climbingRoot.xRot -= (float)(Math.PI/2);
            climbingRoot.z -= state.babySnail ? 3.0F : 6.0F;
        }
        if (state.babySnail) {''')
    changes={p:s for p,s in new.items() if s!=old[p]}
    if not changes: print('V3 already installed.');return
    backup=ROOT/('.snail_v3_backup_'+datetime.datetime.now().strftime('%Y%m%d_%H%M%S_%f'))
    for p in changes:
        dst=backup/p.relative_to(ROOT);dst.parent.mkdir(parents=True,exist_ok=True);shutil.copy2(p,dst)
    for p,s in changes.items():p.write_text(s,encoding='utf-8');print('Updated',p.relative_to(ROOT))
    print('Backup:',backup,'\nRun .\\gradlew.bat build then .\\gradlew.bat runClient')

if __name__=='__main__':
    try: main()
    except (OSError,ValueError) as exc: sys.exit('Patch stopped: '+str(exc))
