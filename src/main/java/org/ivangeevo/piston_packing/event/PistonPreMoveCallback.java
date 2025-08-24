package org.ivangeevo.piston_packing.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public interface PistonPreMoveCallback {
    Event<PistonPreMoveCallback> EVENT = EventFactory.createArrayBacked(PistonPreMoveCallback.class,
        (listeners) -> (world, pos, blockState, isExtending) -> {
            for (PistonPreMoveCallback listener : listeners) {
                listener.onPistonPreMove(world, pos, blockState, isExtending);
            }
        }
    );

    void onPistonPreMove(ServerWorld world, BlockPos pistonPos,
                         BlockState movingState, boolean isExtending);
}
