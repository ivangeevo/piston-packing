package org.ivangeevo.piston_packing.event;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface PistonBreakCallback {
    void onPistonBreak(World world, BlockPos pos, BlockState state);
}
