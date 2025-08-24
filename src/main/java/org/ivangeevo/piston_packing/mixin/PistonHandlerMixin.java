package org.ivangeevo.piston_packing.mixin;

import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.ivangeevo.piston_packing.util.PistonShovelHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonHandler.class)
public abstract class PistonHandlerMixin implements PistonShovelHandler {

    @Shadow @Final private boolean retracted;
    @Shadow @Final private BlockPos posTo;
    @Shadow @Final private Direction pistonDirection;
    @Shadow @Final private World world;

    @Inject(method = "calculatePush",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/PistonBlock;isMovable(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;ZLnet/minecraft/util/math/Direction;)Z"
            ), cancellable = true)
    private void onCalculatePush(CallbackInfoReturnable<Boolean> cir) {
        this.tryShovelBlock(world, posTo, pistonDirection, retracted, cir);
    }

}
