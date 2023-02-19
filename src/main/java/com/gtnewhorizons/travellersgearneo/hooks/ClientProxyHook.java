package com.gtnewhorizons.travellersgearneo.hooks;

import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxyHook {

    public static Configuration config;
    public static String[] keyBindingsValues;

    public static void bindKey(int num, String item) {
        if (num >= 0 && num < 3) {
            keyBindingsValues[num] = item;
            config.save();
        }
    }

}
