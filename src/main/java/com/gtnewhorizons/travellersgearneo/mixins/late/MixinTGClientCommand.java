package com.gtnewhorizons.travellersgearneo.mixins.late;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizons.travellersgearneo.hooks.ClientProxyHook;

import travellersgear.api.IActiveAbility;
import travellersgear.common.util.TGClientCommand;

@Mixin(TGClientCommand.class)
public class MixinTGClientCommand {

    @Inject(method = "processCommand", at = @At("TAIL"))
    private void travellersgearneo$addCommand(ICommandSender sender, String[] args, CallbackInfo ci) {
        if (sender instanceof EntityPlayer && args.length >= 1
                && args[0].equalsIgnoreCase("bind")
                && ((EntityPlayer) sender).worldObj.isRemote) {
            EntityPlayer player = (EntityPlayer) sender;
            if (args.length == 1) {
                player.addChatMessage(
                        new ChatComponentText(EnumChatFormatting.RED + "Usage: travellersgear bind {1,2,3}"));
                return;
            }
            int key = MathHelper.parseIntWithDefault(args[1], 0);
            if (key < 1 || key > 3) {
                player.addChatMessage(
                        new ChatComponentText(EnumChatFormatting.RED + "Usage: travellersgear bind {1,2,3}"));
                return;
            }
            ItemStack stack = player.getHeldItem();
            if (stack == null) {
                player.addChatMessage(
                        new ChatComponentText(
                                EnumChatFormatting.RED + "You need to hold in your hand item you want to bind!"));
                return;
            }
            if (stack.getItem() instanceof IActiveAbility) {
                String itemName = Item.itemRegistry.getNameForObject(stack.getItem());
                player.addChatMessage(
                        new ChatComponentText(
                                EnumChatFormatting.GREEN + "Successfully binded " + itemName + " to key " + key));
                ClientProxyHook.bindKey(key - 1, itemName);
            } else {
                player.addChatMessage(
                        new ChatComponentText(
                                EnumChatFormatting.RED + "The item you are holding doesn't have any special ability"));
            }
        }
    }

    @ModifyArg(
            method = "addTabCompletionOptions",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Arrays;asList([Ljava/lang/Object;)Ljava/util/List;",
                    remap = false))
    private Object[] travellersgearneo$addTabcompleteOption(Object[] original) {
        return new String[] { "gui", "toolDisplay", "bind" };
    }

}
