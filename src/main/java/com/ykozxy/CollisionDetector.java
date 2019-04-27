package com.ykozxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static com.ykozxy.Constants.G;

class CollisionDetector {
  private static Minecraft mc = Minecraft.getMinecraft();
  private EntityLiving target;
  private Vec3d pos;
  private Vec3d velocity;
  private int tick = 0;

  CollisionDetector(EntityLiving target, Vec3d pos, Vec3d velocity) {
    this.target = target;
    this.pos = pos;
    this.velocity = velocity;
  }

  private RayTraceResult nextTick() {
    Vec3d prevPos = new Vec3d(pos.x, pos.y, pos.z);
    pos = pos.add(velocity);
    velocity = velocity.subtract(
            velocity.x * 0.01 * (velocity.x < 0 ? -1 : 1), G, velocity.z * 0.01 * (velocity.z < 0 ? -1 : 1)
    );

    return hitAnalyze(prevPos);
  }

  private RayTraceResult hitAnalyze(Vec3d prevPos) {
    RayTraceResult result = mc.world.rayTraceBlocks(
            prevPos, pos, false, true, false
    );
    Vec3d pos = new Vec3d(this.pos.x, this.pos.y, this.pos.z);
    if (result != null) pos = new Vec3d(result.hitVec.x, result.hitVec.y, result.hitVec.z);

    AxisAlignedBB bbArrow = new AxisAlignedBB(this.pos.x - .25, this.pos.y - .25, this.pos.z - .25,
            this.pos.x + .25, this.pos.y + .25, this.pos.z + .25);
    AxisAlignedBB bbDetect = bbArrow.expand(.5, .5, .5).expand(-.5, -.5, -.5);

    List<EntityLiving> entities = mc.world.getEntitiesWithinAABB(EntityLiving.class, bbDetect);
    double min = Integer.MAX_VALUE;
    for (EntityLiving e : entities) {
      AxisAlignedBB bbMob = e.getEntityBoundingBox().expand(.3, .3, .3).expand(-.3, -.3, -.3);
      RayTraceResult rayTraceResult = bbMob.calculateIntercept(prevPos, pos);
      if (rayTraceResult != null) {
        double dist = prevPos.distanceTo(rayTraceResult.hitVec);
        if (dist < min) {
          result = rayTraceResult;
          result.entityHit = e;
          min = dist;
        }
      }
    }

    return result;
  }

  boolean analyze() {
    for (; tick < 200; tick++) {
      RayTraceResult result = nextTick();
      if (result == null) continue;
      if (result.entityHit == target) return true;
      if (result.typeOfHit == RayTraceResult.Type.BLOCK) return false;
    }
    return false;
  }
}
