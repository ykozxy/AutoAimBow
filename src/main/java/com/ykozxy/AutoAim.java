package com.ykozxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import static com.ykozxy.Constants.HOTKEY;

@Mod(modid = "autoaim", name = "Auto Aim", version = "1.0.0", useMetadata = true)
public class AutoAim {
  @Mod.EventHandler
  public void preLoad(FMLPreInitializationEvent event) {
    ClientRegistry.registerKeyBinding(HOTKEY);
  }

  @Mod.EventHandler
  public void init(FMLInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(new EventHandler());
  }
}
