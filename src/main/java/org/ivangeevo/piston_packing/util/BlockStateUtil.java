package org.ivangeevo.piston_packing.util;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class BlockStateUtil {
        public static BlockState getBlockStateFromItemStack(ItemStack stack) {
            if (stack.getItem() instanceof BlockItem blockItem) {
                return blockItem.getBlock().getDefaultState();
            }
            return null;
        }
    
        public static Optional<BlockState> getBlockStateFromIngredient(Ingredient ingredient) {
            ItemStack[] matchingStacks = ingredient.getMatchingStacks();
    
            // Iterate through the stacks to find the first that represents a block
            return Stream.of(matchingStacks)
                    .map(stack -> {
                        if (stack.getItem() instanceof BlockItem blockItem) {
                            return blockItem.getBlock().getDefaultState();
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .findFirst();
        }
    }