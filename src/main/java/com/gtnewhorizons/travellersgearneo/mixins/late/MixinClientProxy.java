package com.gtnewhorizons.travellersgearneo.mixins.late;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import travellersgear.client.ClientProxy;
import travellersgear.client.handlers.CustomizeableGuiHandler;
import travellersgear.common.CommonProxy;

import com.gtnewhorizons.travellersgearneo.hooks.ClientProxyHook;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mixin(value = ClientProxy.class)
public class MixinClientProxy extends CommonProxy {

    @Shadow(remap = false)
    public static int[] equipmentButtonPos;
    @Shadow(remap = false)
    public static float activeAbilityGuiSpeed;
    @Shadow(remap = false)
    public static float titleOffset;

    /**
     * @author Alexdoru
     * @reason add stuff
     */
    @Overwrite(remap = false)
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ClientProxyHook.config = new Configuration(event.getSuggestedConfigurationFile());
        ClientProxyHook.config.load();
        equipmentButtonPos = ClientProxyHook.config.get(
                "Options",
                "Button Position",
                new int[] { 27, 9 },
                "The position of the Equipment Button in the Inventory").getIntList();
        activeAbilityGuiSpeed = ClientProxyHook.config.getFloat(
                "Radial Speed",
                "Options",
                .15f,
                .05f,
                1f,
                "The speed at which the radial for active abilities opens. Default is 15% per tick, minimum is 5%, maximum is 100%");
        titleOffset = (float) ClientProxyHook.config.get(
                "Options",
                "Title Offset",
                0d,
                "Configures the vertical offset of the title above the players head. 0 is default, set to 1 to render above the players name, the other offsets will use that scale.")
                .getDouble();
        Property prop = ClientProxyHook.config.get(
                "Local",
                "Key Bindings",
                new String[] { "one", "two", "three" },
                "Hotkey binding for active abilities");
        if (prop.getStringList().length < 3) {
            prop.setToDefault();
        }
        ClientProxyHook.keyBindingsValues = prop.getStringList();
        ClientProxyHook.config.save();
        CustomizeableGuiHandler.instance.preInit(event);
    }

}
