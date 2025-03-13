package org.ivangeevo.piston_packing.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.ivangeevo.piston_packing.recipe.PackingRecipe;
import org.ivangeevo.piston_packing.recipe.PackingRecipeInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow private World world;

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void dontPushOnSpecificBlocks(double x, double y, double z, CallbackInfo ci) {
        BlockPos pos = new BlockPos((int) x, (int) y, (int) z);
        Block block = world.getBlockState(pos).getBlock();

        // apply only to item entities and if the block that the item is inside is a piston head or moving piston
        // to minimize weird item entity pushed out behaviour
        if (this.isItemEntity() && this.isPistonHeadOrMoving(block)) {
            ci.cancel();
        }

    }

    @Unique
    private boolean isPistonHeadOrMoving(Block block) {
        return block == Blocks.PISTON_HEAD || block == Blocks.MOVING_PISTON;
    }

    @Unique
    private boolean isItemEntity() {
        return (Entity)(Object)this instanceof ItemEntity;
    }

}
