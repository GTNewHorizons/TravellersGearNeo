package com.gtnewhorizons.travellersgearneo.mixins.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import travellersgear.common.inventory.SlotRestricted;

@Mixin(SlotRestricted.class)
public class MixinSlotRestricted {

    @Shadow(remap = false)
    public SlotRestricted.SlotType type;

    @Shadow(remap = false)
    public int slotLimit;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void travellersgearneo$inject(CallbackInfo ci) {
        if (this.type.equals(SlotRestricted.SlotType.TINKERS_HEART_R)
                || this.type.equals(SlotRestricted.SlotType.TINKERS_HEART_G)
                || this.type.equals(SlotRestricted.SlotType.TINKERS_HEART_Y))
            this.slotLimit = 10;
    }
}
