package org.ivangeevo.piston_packing.util;

import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PistonUtils {

    public static boolean canContainPistonPackingToFacing(World world, BlockPos pos, Direction facing)
    {
        return HardpointUtils.hasLargeCenterHardPointToFacing(world, pos, facing);
    }

    /**
     * Returns the metadata that will be placed
     */
    public int onPreBlockPlacedByPiston(World world, BlockPos pos, int iMetadata, int iDirectionMoved)
    {
        return iMetadata;
    }

    public boolean canBlockBePulledByPiston(World world, BlockPos pos, int iToFacing) {
        PistonBehavior behavior = world.getBlockState(pos).getPistonBehavior();

        // Blocks destroyed on push or immovable blocks cannot be pulled
        if (behavior == PistonBehavior.DESTROY || behavior == PistonBehavior.BLOCK) {
            return false;
        }

        return canBlockBePushedByPiston(world, pos, iToFacing);
    }


    public boolean canBlockBePushedByPiston(World world, BlockPos pos, int iToFacing) {
        PistonBehavior behavior = world.getBlockState(pos).getPistonBehavior();

        // Blocks that are BLOCK or IGNORE cannot be pushed
        return behavior != PistonBehavior.BLOCK && behavior != PistonBehavior.IGNORE;
    }

    /**
     * returns the direction the shoveled block will go in if this block is moving towards iToFacing.  
     * return -1 if it's no shoveling is taking place.
     */
    public Direction getPistonShovelEjectDirection(World world, BlockPos pos, Direction toFacing)
    {
        return Direction.byId(-1);
    }

    /**
    public Box getAsPistonMovingBoundingBox(World world, BlockPos pos)
    {
        return getCollisionBoundingBoxFromPool( world, i, j, k );
    }

    public static int adjustMetadataForPistonMove(int iMetadata)
    {
        return iMetadata;
    }

    public static boolean canContainPistonPackingToFacing(World world, BlockPos pos, Direction facing)
    {
        return hasLargeCenterHardPointToFacing( world, pos, facing, true );
    }

    public static void onBrokenByPistonPush(World world, BlockPos pos, int iMetadata)
    {
        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(),
                new ItemStack(world.getBlockState(pos).getBlock())
        );
    }
     **/
}
