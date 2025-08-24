package org.ivangeevo.piston_packing;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.ivangeevo.piston_packing.block.ModBlocks;
import org.ivangeevo.piston_packing.block.blocks.PistonShovelBlock;
import org.ivangeevo.piston_packing.event.PistonPreMoveCallback;
import org.ivangeevo.piston_packing.recipe.ModRecipes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.state.property.Properties.FACING;

public class PistonPackingMod implements ModInitializer {

    public static final String MOD_ID = "piston_packing";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    @Override
    public void onInitialize() {
        ModBlocks.registerToItemGroups();
        ModRecipes.registerRecipes();

    }


}
