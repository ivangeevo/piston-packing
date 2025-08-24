package org.ivangeevo.piston_packing.util;

import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PistonUtils {

    public static boolean isPushingNormalCube(World world, BlockPos pos, PistonBlockEntity pistonBE) {
        return pistonBE.getPushedBlock().isOpaqueFullCube(world, pos);
    }

    public static boolean canContainPistonPackingToFacing(World world, BlockPos pos, Direction facing) {
        return HardpointUtils.hasLargeCenterHardPointToFacing(world, pos, facing);
    }

}
