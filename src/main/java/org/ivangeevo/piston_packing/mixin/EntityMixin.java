package org.ivangeevo.piston_packing.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract World getWorld();

    @Shadow @Final protected Random random;

    @Shadow public abstract Vec3d getVelocity();

    @Shadow public abstract void setVelocity(double x, double y, double z);

    @Unique
    boolean isItemEntity = (Entity)(Object)this instanceof ItemEntity;

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void onPushOutOfBlocks(double x, double y, double z, CallbackInfo ci) {

        if (!isItemEntity) {
            return;
        }

        BlockPos blockPos = BlockPos.ofFloored(x, y, z);
        Vec3d offset = new Vec3d(x - blockPos.getX(), y - blockPos.getY(), z - blockPos.getZ());
        BlockPos.Mutable mutable = blockPos.mutableCopy();

        // Array of all cardinal and vertical directions
        Direction[] directions = Direction.values();
        Direction bestDirection = null;
        double smallestDistance = Double.MAX_VALUE;

        // Determine the best direction to push the entity out of the block
        for (Direction direction : directions) {
            mutable.set(blockPos, direction);

            // Check if the adjacent block in the given direction is not a full cube
            if (!this.getWorld().getBlockState(mutable).isFullCube(this.getWorld(), mutable)
                    && this.getSpecificBlocks(this.getWorld().getBlockState(mutable).getBlock())) {
                double distance = direction.getAxis().choose(offset.x, offset.y, offset.z);
                if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
                    distance = 1.0 - distance;
                }

                if (distance < smallestDistance) {
                    smallestDistance = distance;
                    bestDirection = direction;
                }
            }
        }

        // Apply motion in the determined direction
        if (bestDirection != null) {
            float velocityModifier = this.random.nextFloat() * 0.2F + 0.1F;
            Vec3d velocity = this.getVelocity().multiply(0.75);

            switch (bestDirection.getAxis()) {
                case X -> this.setVelocity(
                        bestDirection.getOffsetX() * velocityModifier,
                        velocity.y,
                        velocity.z
                );
                case Y -> this.setVelocity(
                        velocity.x,
                        bestDirection.getOffsetY() * velocityModifier,
                        velocity.z
                );
                case Z -> this.setVelocity(
                        velocity.x,
                        velocity.y,
                        bestDirection.getOffsetZ() * velocityModifier
                );
            }
        }

        ci.cancel();
    }

    @Unique
    private boolean getSpecificBlocks(Block block) {
        return block == Blocks.PISTON_HEAD || block == Blocks.MOVING_PISTON;
    }
}
