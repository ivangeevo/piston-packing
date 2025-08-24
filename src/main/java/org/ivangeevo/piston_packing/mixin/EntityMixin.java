package org.ivangeevo.piston_packing.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.ivangeevo.piston_packing.block.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow private World world;

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void dontPushOnSpecificBlocks(double x, double y, double z, CallbackInfo ci) {
        BlockPos pos = new BlockPos((int) x, (int) y, (int) z);
        Block block = world.getBlockState(pos).getBlock();

        // apply only to item entities and if the block that the item is inside is a piston head or moving piston
        // to minimize weird item entity pushed out behaviour
        if (this.isItemEntity() && this.isPistonShovelOrHeadOrMoving(block)) {
            ci.cancel();
        }

    }

    @Unique
    private boolean isPistonShovelOrHeadOrMoving(Block block) {
        return block == ModBlocks.PISTON_SHOVEL || block == Blocks.PISTON_HEAD || block == Blocks.MOVING_PISTON;
    }

    @Unique
    private boolean isItemEntity() {
        return (Entity)(Object)this instanceof ItemEntity;
    }

}
