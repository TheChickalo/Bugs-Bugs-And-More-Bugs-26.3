#!/usr/bin/env python3
"""Patch the V2 snail survival code; run from the mod's root with py script.py ."""
from pathlib import Path
import json, sys, re, shutil, datetime

root = Path(sys.argv[1] if len(sys.argv)>1 else '.').resolve()
base = root / 'src/main/java/net/chickalo/bugsbugsandmorebugs'
entity = base / 'entity/snail/SnailEntity.java'
content = base / 'entity/snail/SnailContent.java'
config = root / 'src/main/resources/bugsbugsandmorebugs.mixins.json'
mixin = base / 'mixin/BerryBushPathfindingMixin.java'

def replace_once(s, old, new):
    if s.count(old) != 1:
        raise ValueError('Expected exactly one matching code section: '+old[:90])
    return s.replace(old,new,1)

HELPER = '''    // Snail survival patch: environmental escape takes priority over hiding.
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

'''
MIXIN = '''package net.chickalo.bugsbugsandmorebugs.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Give custom sweet berry bush subclasses vanilla land-pathfinding hazard behavior. */
@Mixin(WalkNodeEvaluator.class)
public abstract class BerryBushPathfindingMixin {
    @Inject(method = "getPathTypeFromState", at = @At("HEAD"), cancellable = true)
    private static void bugs$berryBushHazard(BlockGetter level, BlockPos pos, CallbackInfoReturnable<PathType> cir) {
        if (level.getBlockState(pos).getBlock() instanceof SweetBerryBushBlock) {
            cir.setReturnValue(PathType.DAMAGING);
        }
    }
}
'''

def main():
    if not all(p.is_file() for p in (entity,content,config)):
        raise ValueError('Run from the installed V2 mod project root (missing snail Java files or mixin config).')
    old = entity.read_text(encoding='utf-8'); s=old
    c_old=content.read_text(encoding='utf-8'); c=c_old
    cfg=json.loads(config.read_text(encoding='utf-8'))
    if 'private boolean escapeEnvironment(' not in s:
        s=replace_once(s, 'return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)\n                && level.getFluidState(pos).isEmpty() && level.getFluidState(pos.above()).isEmpty();', '''var box = type.getSpawnAABB(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5);
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)
                && level.getFluidState(pos).isEmpty() && level.getFluidState(pos.above()).isEmpty()
                && level.noCollision(box) && !hasBerryBush(level, box);''')
        s=replace_once(s, '        // Extend the hiding timer while danger is nearby. Never restart retract every tick.', '''        // Escape environmental hazards even when a player or another mob is nearby.
        if (escapeEnvironment(server)) return;
        // Extend the hiding timer while danger is nearby. Never restart retract every tick.''')
        s=replace_once(s, '    private void updateThreats(ServerLevel server) {', HELPER+'    private void updateThreats(ServerLevel server) {')
    if '.sized(1.35F, 1.0F)' in c:
        c=replace_once(c,'.sized(1.35F, 1.0F)', '.sized(0.9F, 1.0F)')
    elif '.sized(0.9F, 1.0F)' not in c:
        raise ValueError('Unexpected snail hitbox size; not changing your custom registration.')
    if 'BerryBushPathfindingMixin' not in cfg['mixins']:
        cfg['mixins'].append('BerryBushPathfindingMixin')
    if mixin.exists() and mixin.read_text(encoding='utf-8') != MIXIN:
        raise ValueError('A different BerryBushPathfindingMixin already exists; refusing to overwrite it.')
    changes={entity:s,content:c,config:json.dumps(cfg,indent=2)+'\n',mixin:MIXIN}
    changes={p:v for p,v in changes.items() if not p.exists() or p.read_text(encoding='utf-8')!=v}
    if not changes:
        print('Snail survival patch already installed.'); return
    backup=root/('.snail_survival_backup_'+datetime.datetime.now().strftime('%Y%m%d_%H%M%S_%f'))
    for p in changes:
        if p.exists():
            dst=backup/p.relative_to(root); dst.parent.mkdir(parents=True,exist_ok=True); shutil.copy2(p,dst)
    for p,v in changes.items():
        p.parent.mkdir(parents=True,exist_ok=True); p.write_text(v,encoding='utf-8')
        print('Updated',p.relative_to(root))
    print('Backups:',backup)
    print('Now run .\\gradlew.bat build, then .\\gradlew.bat runClient')

if __name__=='__main__':
    try: main()
    except (OSError,ValueError,KeyError) as e:
        sys.exit('Patch stopped: '+str(e))
