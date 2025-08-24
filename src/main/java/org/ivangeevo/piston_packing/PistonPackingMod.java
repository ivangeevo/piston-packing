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
        //OGPistonPackingUtil.registerPackables();


        // Handle event when a piston is pushing a piston shovel
        PistonPreMoveCallback.EVENT.register((world, pistonPos, movingState, isExtending) -> {
            System.out.println("[DEBUG] Piston at " + pistonPos + " is about to " +
                    (isExtending ? "extend" : "retract") + " and move: " + movingState.getBlock());
            BlockState pistonState = world.getBlockState(pistonPos);

            if (world.isClient) return;


            Direction facing = pistonState.get(FACING);
            BlockPos frontPos = pistonPos.offset(facing, 1); // 1 block in front of piston base

            BlockState frontBlock = world.getBlockState(frontPos);

            // Check if it's a piston shovel block facing the same direction
            if (!(frontBlock.getBlock() instanceof PistonShovelBlock)) return;
            if (frontBlock.get(PistonShovelBlock.FACING) != facing) return;

            // Target is the block 2 blocks in front of the piston base (in front of the shovel tip)
            BlockPos targetPos = pistonPos.offset(facing, 2);
            BlockState targetBlock = world.getBlockState(targetPos);

            if (targetBlock.isAir()) return;

            // Drop loot for the shoveled block
            Block.dropStacks(targetBlock, world, targetPos);

            world.setBlockState(targetPos, Blocks.AIR.getDefaultState());

        });
    }

    public static Direction normalizeToHorizontal(Direction dir) {
        return dir.getAxis().isHorizontal() ? dir : Direction.NORTH;
    }

}
