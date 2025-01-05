package org.ivangeevo.piston_packing.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

import java.util.List;

public record PackingRecipeInput(List<ItemStack> items) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {
        return items.get(slot);
    }

    @Override
    public int getSize() {
        return items.size();
    }

}
