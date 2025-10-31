package org.ivangeevo.piston_packing.util;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.Arrays;
import java.util.Optional;

public class BlockStateUtil {

    public static BlockState getBlockStateFromItemStack(ItemStack stack) {
        return stack != null ? ((BlockItem) stack.getItem()).getBlock().getDefaultState() : null;
    }

    /**
     * Converts a recipe-defined Ingredient into a BlockState, if any matching stack is a BlockItem.
     * If the ingredient represents non-block items, returns empty.
     */
    public static Optional<BlockState> getBlockStateFromIngredient(Ingredient ingredient) {
        return Arrays.stream(ingredient.getMatchingStacks())
                .filter(stack -> stack.getItem() instanceof BlockItem)
                .map(stack -> ((BlockItem) stack.getItem()).getBlock().getDefaultState())
                .findFirst();
    }
}