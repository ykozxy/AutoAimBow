package com.ykozxy;


import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.config.Config;
import org.lwjgl.input.Keyboard;

@SuppressWarnings("WeakerAccess")
@Config(modid = Constants.MODID, name = "Auto Aim Configuration")
public class Constants {
  @Config.Ignore
  static final String MODID = "autoaim";

  @Config.Ignore
  static final KeyBinding HOTKEY = new KeyBinding("Auto Aim Control", Keyboard.KEY_V,
          "Auto Aim");

  /**
   * Gravitational acceleration of the arrow
   * Unit: blocks / tick^2
   */
  @Config.Ignore
  static final double G = 0.05;

  /**
   * Initial velocity of the arrow
   * Unit: blocks / tick
   */
  @Config.Ignore
  static final double V = 2.65;

  @Config.Name("Enable Auto Aim")
  @Config.Comment("You can also press the hot key to control it")
  public static boolean modOn = true;

  @Config.Name("Crosshair move duration (tick)")
  @Config.Comment("The duration of the crosshair to move to the new target when aiming.\n" +
          "Kind remind: 1 second = 20 ticks")
  @Config.RangeInt(min = 1, max = 20)
  public static int rotateDuration = 7;

  @Config.Name("Detect Distance")
  @Config.Comment("The maximum distance of the mob to enable auto aim")
  @Config.RangeInt(min = 10, max = 256)
  public static int detectDistance = 64;

  @Config.Name("Aiming Hight Adjustment")
  @Config.Comment("The higher the value, the higher (relative to the center of the mob) " +
          "the position of the mob the auto aim will try to shoot")
  @Config.RangeInt(min = -5, max = 5)
  public static int heightAdjustment = 2;
}
