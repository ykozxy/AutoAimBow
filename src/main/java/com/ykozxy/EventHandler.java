package com.ykozxy;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.ykozxy.AimCalculation.rotation;
import static com.ykozxy.Constants.*;

@SideOnly(Side.CLIENT)
public class EventHandler {
  private Minecraft mc = Minecraft.getMinecraft();

  @SubscribeEvent
  public void setPitch(InputEvent.KeyInputEvent e) {
    if (HOTKEY.isPressed()) {
      modOn = !modOn;
      mc.player.sendMessage(new TextComponentString("Auto Aim is " + (modOn ? "on" : "off")));
    }
  }

  @SubscribeEvent
  public void onArrowNock(ArrowNockEvent e) {
    if (!modOn || mc.player == null || e.getEntity() != mc.player) return;

    AimCalculation.AimLoop.needAim = true;
    new Thread(new AimCalculation.AimLoop(), "Aim Loop").start();
  }

  @SubscribeEvent
  public void onArrowLoose(ArrowLooseEvent e) {
    if (mc.player == null || e.getEntity() != mc.player) return;
    AimCalculation.AimLoop.needAim = false;
  }

  @SubscribeEvent
  public void onTick(TickEvent e) {
    if (!rotation.complete)
      rotation.rotate();
  }

  @SubscribeEvent
  public void onConfigChange(ConfigChangedEvent e) {
    if (e.getModID().equals(MODID)) {
      ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }
  }
}
