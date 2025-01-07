package org.ivangeevo.piston_packing.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public class HardpointUtils
{

    /**
     * large attachment points that can support a full block width
     */
    public static boolean hasLargeCenterHardPointToFacing(WorldAccess blockAccess, BlockPos pos, Direction facing, boolean bIgnoreTransparency )
    {
        return blockAccess.getBlockState(pos).isSideSolidFullSquare(blockAccess, pos, facing);
    }

    public static boolean hasLargeCenterHardPointToFacing(WorldAccess blockAccess, BlockPos pos, Direction facing )
    {
        return hasLargeCenterHardPointToFacing( blockAccess, pos, facing, false );
    }
}
