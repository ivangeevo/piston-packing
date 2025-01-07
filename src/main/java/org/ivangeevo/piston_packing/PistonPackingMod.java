package org.ivangeevo.piston_packing;

import net.fabricmc.api.ModInitializer;
import org.ivangeevo.piston_packing.block.ModBlocks;
import org.ivangeevo.piston_packing.recipe.ModRecipes;
import org.ivangeevo.piston_packing.util.OGPistonPackingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PistonPackingMod implements ModInitializer {

    public static final String MOD_ID = "piston_packing";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    @Override
    public void onInitialize() {
        ModBlocks.registerToItemGroups();
        ModRecipes.registerRecipes();
        //OGPistonPackingUtil.registerPackables();
    }
}
