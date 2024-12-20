package com.gtnewhorizons.travellersgearneo.mixins.late;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizons.travellersgearneo.hooks.ClientProxyHook;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import travellersgear.TravellersGear;
import travellersgear.client.ClientProxy;
import travellersgear.client.KeyHandler;
import travellersgear.client.handlers.ActiveAbilityHandler;
import travellersgear.client.handlers.CustomizeableGuiHandler;
import travellersgear.common.network.MessageActiveAbility;
import travellersgear.common.network.MessageOpenGui;
import travellersgear.common.network.MessageSlotSync;
import travellersgear.common.network.old.PacketActiveAbility;

@Mixin(value = KeyHandler.class)
public class MixinKeyHandler {

    @Shadow(remap = false)
    public static boolean abilityLock;

    @Shadow(remap = false)
    public static float abilityRadial;

    @Shadow(remap = false)
    public static KeyBinding openInventory;

    @Shadow(remap = false)
    public static KeyBinding activeAbilitiesWheel;

    @Unique
    private static final KeyBinding travellersGearNeo$activeAbility1 = new KeyBinding(
            "TG.keybind.activeAbility1",
            Keyboard.KEY_NONE,
            TravellersGear.MODNAME);
    @Unique
    private static final KeyBinding travellersGearNeo$activeAbility2 = new KeyBinding(
            "TG.keybind.activeAbility2",
            Keyboard.KEY_NONE,
            TravellersGear.MODNAME);
    @Unique
    private static final KeyBinding travellersGearNeo$activeAbility3 = new KeyBinding(
            "TG.keybind.activeAbility3",
            Keyboard.KEY_NONE,
            TravellersGear.MODNAME);

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void travellersgearneo$registerKeys(CallbackInfo ci) {
        ClientRegistry.registerKeyBinding(travellersGearNeo$activeAbility1);
        ClientRegistry.registerKeyBinding(travellersGearNeo$activeAbility2);
        ClientRegistry.registerKeyBinding(travellersGearNeo$activeAbility3);
    }

    /**
     * @author Alexdoru
     * @reason suppress TPS lag from poorly made keybind handler, mainly the Keyboard.isCreated() calls
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {}

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void handleKeybinds(InputEvent.KeyInputEvent event) {
        handleInput();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void handleMousebinds(InputEvent.MouseInputEvent event) {
        handleInput();
    }

    private void handleInput() {
        if (openInventory.isPressed()) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (player == null) return;

            boolean[] hidden = new boolean[CustomizeableGuiHandler.moveableInvElements.size()];
            for (int bme = 0; bme < hidden.length; ++bme) {
                hidden[bme] = CustomizeableGuiHandler.moveableInvElements.get(bme).hideElement;
            }
            TravellersGear.packetHandler.sendToServer(new MessageSlotSync(player, hidden));
            TravellersGear.packetHandler.sendToServer(new MessageOpenGui(player, 0));
        } else if (travellersGearNeo$activeAbility1.isPressed()) {
            checkAbilityKey(0);
        } else if (travellersGearNeo$activeAbility2.isPressed()) {
            checkAbilityKey(1);
        } else if (travellersGearNeo$activeAbility3.isPressed()) {
            checkAbilityKey(2);
        }
    }

    private void checkAbilityKey(int i) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;

        final Object[][] gear = ActiveAbilityHandler.instance.buildActiveAbilityList(player);
        for (Object[] ability : gear) {
            ItemStack stack = (ItemStack) ability[0];
            if (stack != null && ClientProxyHook.keyBindingsValues[i]
                    .equals(Item.itemRegistry.getNameForObject(stack.getItem()))) {
                TravellersGear.packetHandler.sendToServer(new MessageActiveAbility(player, (Integer) ability[1]));
                PacketActiveAbility.performAbility(player, (Integer) ability[1]);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void handleKeybindsOnTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (Minecraft.getMinecraft().inGameHasFocus) {
            if (!abilityLock) {
                if (activeAbilitiesWheel.getIsKeyPressed()) {
                    openAbilityWheel();
                } else {
                    closeAbilityWheel();
                }
            }
        }
    }

    private void openAbilityWheel() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null || ActiveAbilityHandler.instance.buildActiveAbilityList(player).length == 0) return;

        if (abilityRadial < 1.0F) {
            abilityRadial += ClientProxy.activeAbilityGuiSpeed;
        }
        if (abilityRadial > 1.0F) {
            abilityRadial = 1.0F;
        }
        if (abilityRadial >= 1.0F) {
            abilityLock = true;
        }
    }

    private void closeAbilityWheel() {
        if (abilityRadial > 0.0F) {
            abilityRadial -= ClientProxy.activeAbilityGuiSpeed;
        }
        if (abilityRadial < 0.0F) {
            abilityRadial = 0.0F;
        }
    }
}
