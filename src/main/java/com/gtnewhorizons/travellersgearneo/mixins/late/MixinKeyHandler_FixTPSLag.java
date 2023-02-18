package com.gtnewhorizons.travellersgearneo.mixins.late;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import travellersgear.TravellersGear;
import travellersgear.client.ClientProxy;
import travellersgear.client.KeyHandler;
import travellersgear.client.handlers.ActiveAbilityHandler;
import travellersgear.client.handlers.CustomizeableGuiHandler;
import travellersgear.common.network.MessageOpenGui;
import travellersgear.common.network.MessageSlotSync;

@Mixin(KeyHandler.class)
public class MixinKeyHandler_FixTPSLag {

    @Shadow(remap = false)
    public static boolean abilityLock;

    @Shadow(remap = false)
    public static float abilityRadial;

    @Shadow(remap = false)
    public static KeyBinding openInventory;

    @Shadow(remap = false)
    public static KeyBinding activeAbilitiesWheel;

    @Shadow(remap = false)
    public boolean[] keyDown;

    /**
     * @author Alexdoru
     * @reason suppress TPS lag from poorly made keybind handler, mainly the Keyboard.isCreated() calls
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void handleKeybinds(InputEvent.KeyInputEvent event) {
        if (openInventory.isPressed()) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (player == null) {
                return;
            }
            boolean[] hidden = new boolean[CustomizeableGuiHandler.moveableInvElements.size()];
            for (int bme = 0; bme < hidden.length; ++bme) {
                hidden[bme] = CustomizeableGuiHandler.moveableInvElements.get(bme).hideElement;
            }
            TravellersGear.packetHandler.sendToServer(new MessageSlotSync(player, hidden));
            TravellersGear.packetHandler.sendToServer(new MessageOpenGui(player, 0));
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void handleKeybindsOnTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().inGameHasFocus) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (player == null) {
                return;
            }
            if (activeAbilitiesWheel.getIsKeyPressed()
                && !this.keyDown[1]
                && ActiveAbilityHandler.instance.buildActiveAbilityList(player).length > 0) {
                if (abilityLock) {
                    abilityLock = false;
                } else {
                    if (abilityRadial < 1.0F) {
                        abilityRadial += ClientProxy.activeAbilityGuiSpeed;
                    }
                    if (abilityRadial > 1.0F) {
                        abilityRadial = 1.0F;
                    }
                    if (abilityRadial >= 1.0F) {
                        abilityLock = true;
                        this.keyDown[1] = true;
                    }
                }
            } else {
                if (this.keyDown[1]) {
                    this.keyDown[1] = false;
                }
                if (!abilityLock) {
                    if (abilityRadial > 0.0F) {
                        abilityRadial -= ClientProxy.activeAbilityGuiSpeed;
                    }
                    if (abilityRadial < 0.0F) {
                        abilityRadial = 0.0F;
                    }
                }
            }
        }
    }
}
