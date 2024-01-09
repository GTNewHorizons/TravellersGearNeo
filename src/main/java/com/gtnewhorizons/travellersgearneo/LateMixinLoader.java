package com.gtnewhorizons.travellersgearneo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import cpw.mods.fml.common.FMLCommonHandler;

@LateMixin
public class LateMixinLoader implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.travellersgearneo.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        List<String> mixins = new ArrayList<>();
        mixins.add("MixinSlotRestricted");
        if (FMLCommonHandler.instance().getSide().isClient()) {
            mixins.add("MixinClientProxy");
            mixins.add("MixinKeyHandler");
            mixins.add("MixinTGClientCommand");
        }
        return mixins;
    }

}
