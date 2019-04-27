package com.ykozxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;

import static com.ykozxy.Constants.*;
import static java.lang.Math.*;

class AimCalculation {
  static RotationInfo rotation = new RotationInfo();
  private static Minecraft mc = Minecraft.getMinecraft();

  private static double getPitch(double x, double y) {
    return -toDegrees(2 * atan(
            (2 * pow(V, 2) * x - sqrt(4 * pow(V, 4) * (pow(x, 2) + pow(y, 2)) - pow(G, 2) * pow(x, 4))) /
                    (G * pow(x, 2) - 2 * pow(V, 2) * y)
    ));
  }

  private static double getDist(EntityLiving e1, EntityPlayerSP e2) {
    return sqrt(pow(e1.posX - e2.posX, 2) + pow(e1.posY - e2.posY, 2) + pow(e1.posZ - e2.posZ, 2));
  }

  private static void aimNearest() {
//    TODO: motion detect
    Item item = mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem();
    if (item != Items.BOW) return;

//    Get All Entities in the range
    AxisAlignedBB bb = new AxisAlignedBB(
            mc.player.posX - detectDistance, mc.player.posY - detectDistance, mc.player.posZ - detectDistance,
            mc.player.posX + detectDistance, mc.player.posY + detectDistance, mc.player.posZ + detectDistance
    );
    List<EntityLiving> entities = mc.world.getEntitiesWithinAABB(EntityLiving.class, bb);
    if (entities.isEmpty()) return;
    entities.sort(Comparator.comparingDouble(o -> getDist(o, mc.player)));

//    TODO: set aim mode
    for (EntityLiving entity : entities) {
      Vec3d d = new Vec3d(entity.posX, entity.posY + entity.height * (heightAdjustment / 10.), entity.posZ)
              .subtract(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
      double yaw;
      double atan_ = atan(abs(d.x / d.z));
      if (d.z > 0) yaw = toDegrees(atan_);
      else yaw = 180 - toDegrees(atan_);
      yaw *= (d.x <= 0) ? 1 : -1;
      double pitch = getPitch(sqrt(d.x * d.x + d.z * d.z), d.y);

      CollisionDetector detector = new CollisionDetector(
              entity,
              new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ),
              new Vec3d(V * cos(toRadians(-pitch)) * cos(toRadians(yaw + 90)),
                      V * sin(toRadians(-pitch)),
                      V * cos(toRadians(-pitch)) * sin(toRadians(yaw + 90))
              ));
//      System.out.println("Detect " + entity.getName());
      if (detector.analyze()) {
        lookAt(yaw, pitch, rotateDuration);
        return;
      }
    }
  }

  private static void lookAt(double yaw, double pitch, int duration) {
    if (duration <= 1) {
      mc.player.rotationYaw = (float) yaw;
      mc.player.rotationPitch = (float) pitch;
    } else {
      rotation = new RotationInfo(mc.player.rotationYaw, yaw, mc.player.rotationPitch, pitch, duration);
    }
  }

  static class RotationInfo {
    double fromYaw, toYaw, deltaYaw;
    double fromPitch, toPitch, deltaPitch;
    int tickLeft;
    boolean complete = false;

    RotationInfo(double fromYaw, double toYaw, double fromPitch, double toPitch, int duration) {
      this.fromYaw = fromYaw;
      this.toYaw = toYaw;
      this.deltaYaw = (toYaw - fromYaw) / duration;
      this.fromPitch = fromPitch;
      this.toPitch = toPitch;
      this.deltaPitch = (toPitch - fromPitch) / duration;
      this.tickLeft = duration;
    }

    RotationInfo() {
      this.fromYaw = 0;
      this.toYaw = 0;
      this.deltaYaw = 0;
      this.fromPitch = 0;
      this.toPitch = 0;
      this.deltaPitch = 0;
      this.tickLeft = 0;
      complete = true;
    }

    void rotate() {
      mc.player.rotationYaw += deltaYaw;
      mc.player.rotationPitch += deltaPitch;
      complete = (--tickLeft == 0);
    }
  }

  static class AimLoop implements Runnable {
    static boolean needAim = false;

    @Override
    public void run() {
      while (needAim) {
        try {
          aimNearest();
        } catch (ConcurrentModificationException ignored) {
        }
      }
    }
  }
}
