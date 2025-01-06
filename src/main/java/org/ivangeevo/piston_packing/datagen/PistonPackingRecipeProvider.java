package org.ivangeevo.piston_packing.datagen;

import btwr.btwr_sl.lib.util.utils.RecipeProviderUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import org.ivangeevo.piston_packing.recipe.PackingRecipe;
import org.tough_environment.block.ModBlocks;
import org.tough_environment.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class PistonPackingRecipeProvider extends FabricRecipeProvider implements RecipeProviderUtils
{

    public PistonPackingRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {

        // Items Packing Recipes
        offerPacking(Blocks.RAW_GOLD_BLOCK, Items.RAW_GOLD, 9, exporter);
        offerPacking(Blocks.RAW_IRON_BLOCK, Items.RAW_IRON, 9, exporter);
        offerPacking(Blocks.RAW_COPPER_BLOCK, Items.RAW_COPPER, 9, exporter);

        // Loose Blocks Packing Recipes
        offerPacking(Blocks.SANDSTONE, Items.SAND, 2, exporter);

        // Uncomment and add more as necessary:
        offerPacking(ModBlocks.DIRT_LOOSE, ModItems.PILE_DIRT, 8, exporter);
        offerPacking(ModBlocks.DIRT_PACKED, ModBlocks.DIRT_LOOSE.asItem(), 2, exporter);

        offerPacking(Blocks.GRAVEL, ModItems.PILE_GRAVEL, 8, exporter);
        offerPacking(Blocks.SAND, ModItems.PILE_SAND, 8, exporter);
        offerPacking(Blocks.RED_SAND, ModItems.PILE_RED_SAND, 8, exporter);
        offerPacking(ModBlocks.CLAY_BLOCK, ModItems.PILE_CLAY, 18, exporter);

        offerPacking(ModBlocks.COBBLESTONE_LOOSE, ModItems.SMALL_STONE, 8, exporter);
        //offerPacking(ModBlocks.MANTLESTONE_LOOSE, ModItems.SMALL_STONE_1, 8, exporter);
        offerPacking(ModBlocks.COBBLED_DEEPSLATE_LOOSE, ModItems.SMALL_STONE_2, 8, exporter);

        offerPacking(ModBlocks.STONE_BRICKS_LOOSE, ModItems.STONE_BRICK, 4, exporter);
        //offerPacking(ModBlocks.MANTLESTONE_BRICKS_LOOSE, ModItems.STONE_BRICK_1, 4, exporter);
        offerPacking(ModBlocks.DEEPSLATE_BRICKS_LOOSE, ModItems.STONE_BRICK_2, 4, exporter);

        offerPacking(ModBlocks.CLAY_BLOCK, Items.CLAY_BALL, 9, exporter);

        //offerPacking(ModBlocks.BONE_BLOCK, Items.BONE, 9, exporter);
        //offerPacking(ModBlocks.ROTTEN_FLESH_BLOCK, Items.ROTTEN_FLESH, 9, exporter);

        offerPacking(ModBlocks.BRICKS_LOOSE, Items.BRICK, 8, exporter);
        offerPacking(ModBlocks.NETHER_BRICKS_LOOSE, Items.NETHER_BRICK, 8, exporter);
        //offerPacking(BTWR_Blocks.DUNG_BLOCK, BwtItems.dungItem, 8, exporter);
        //offerPacking(BTWR_Blocks.FLINT_BLOCK, Items.FLINT, 8, exporter);
        //offerPacking(BTWR_Blocks.SOAP_BLOCK, BwtItems.soapItem, 8, exporter);
    }

    private void offerPacking(Block result, Item ingredient, int count, RecipeExporter exporter) {
        PackingRecipe.JsonBuilder.create().result(result)
                .category(CraftingRecipeCategory.MISC)
                .ingredient(ingredient, count)
                .criterion(hasItem(ingredient), conditionsFromItem(ingredient))
                .offerTo(exporter);
    }


}
