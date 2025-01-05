package org.ivangeevo.piston_packing.datagen;

import btwr.btwr_sl.lib.util.utils.RecipeProviderUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.ivangeevo.piston_packing.PistonPackingMod;
import org.ivangeevo.piston_packing.recipe.PistonPackingRecipe;
import org.tough_environment.block.ModBlocks;
import org.tough_environment.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class PistonPackingRecipeProvider extends FabricRecipeProvider implements RecipeProviderUtils
{

    public PistonPackingRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected Identifier getRecipeIdentifier(Identifier identifier) {
        return identifier;
    }

    @Override
    public void generate(RecipeExporter exporter) {

        // testing recipe
        PistonPackingRecipe.JsonBuilder.create().result(Blocks.GRAVEL)
                .category(CraftingRecipeCategory.MISC)
                .ingredient(Items.FLINT,9)
                .criterion("has_flint", conditionsFromItem(Items.FLINT))
                .offerTo(exporter, Identifier.of(PistonPackingMod.MOD_ID, "gravel_from_piston_packing_flint"));

    }



}
