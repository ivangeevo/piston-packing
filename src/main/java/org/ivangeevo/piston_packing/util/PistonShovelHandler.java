package org.ivangeevo.piston_packing.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.ivangeevo.piston_packing.block.blocks.PistonShovelBlock;
import org.ivangeevo.piston_packing.tag.ModTags;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public interface PistonShovelHandler {

    default void tryShovelBlock(World world, BlockPos posTo, Direction pistonDirection, boolean retracted, CallbackInfoReturnable<Boolean> cir) {
        // The position to break with the shovel on push
        BlockPos shoveledPos = posTo.offset(pistonDirection);

        // Check if a piston shovel is facing properly first
        if (isShovelFacing(world.getBlockState(posTo), pistonDirection)) {
            BlockState targetState = world.getBlockState(shoveledPos);

            ItemStack fakeTool = new ItemStack(Items.NETHERITE_SHOVEL);

            // Add Silk Touch enchantment
            RegistryEntry<Enchantment> enchantment = world
                    .getRegistryManager()
                    .get(RegistryKeys.ENCHANTMENT)
                    .entryOf(Enchantments.SILK_TOUCH);

            fakeTool.addEnchantment(enchantment, 1);

            if (targetState.isIn(ModTags.Blocks.PISTON_SHOVEL_BREAKABLE)) {
                if (retracted) {
                    world.setBlockState(shoveledPos, Blocks.AIR.getDefaultState());
                    Block.dropStacks(targetState, world, shoveledPos, world.getBlockEntity(shoveledPos), null, fakeTool);
                    cir.setReturnValue(true);
                }
            }
        }
    }

    private boolean isShovelFacing(BlockState state, Direction pistonDirection) {
        return state.getBlock() instanceof PistonShovelBlock shovelBlock
                && shovelBlock.getFacing(state) == pistonDirection;
    }

}
