package com.gtnewhorizons.travellersgearneo;

import cpw.mods.fml.common.Mod;

@Mod(
        modid = "travellersgearneo",
        version = "1.0",
        name = "TravellersGearNeo",
        acceptedMinecraftVersions = "[1.7.10]",
        dependencies = "required-after:Baubles;before:WitchingGadgets;before:TravellersGear;after:BiblioCraft;after:gregtech")
// the dependency here is to trick FML to load TravellersGear after Bibliocraft and after gregtech
public class TravellersGearNeo {
}
