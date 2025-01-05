package org.ivangeevo.piston_packing.util;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.tough_environment.block.ModBlocks;
import org.tough_environment.item.ModItems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OGPistonPackingUtil
{

    private static final Map<ItemConvertible, PackingRecipe> PACKING_RECIPES = new HashMap<>();

    public static void registerPackables() {
        initItemsPacking();
        initLooseBlocksPacking();
    }

    private static void initItemsPacking() {

        PACKING_RECIPES.put(ModItems.PILE_DIRT, new PackingRecipe(ModBlocks.DIRT_LOOSE, 8));
        PACKING_RECIPES.put(ModItems.PILE_GRAVEL, new PackingRecipe(Blocks.GRAVEL, 8));
        PACKING_RECIPES.put(ModItems.PILE_SAND, new PackingRecipe(Blocks.SAND, 8));
        PACKING_RECIPES.put(ModItems.PILE_RED_SAND, new PackingRecipe(Blocks.RED_SAND, 8));
        PACKING_RECIPES.put(ModItems.PILE_CLAY, new PackingRecipe(ModBlocks.CLAY_BLOCK, 18));

        PACKING_RECIPES.put(ModItems.SMALL_STONE, new PackingRecipe(ModBlocks.COBBLESTONE_LOOSE, 8));
        //PACKING_RECIPES.put(ModItems.SMALL_STONE_1, new PackingRecipe(ModBlocks.MANTLESTONE_LOOSE, 8));
        PACKING_RECIPES.put(ModItems.SMALL_STONE_2, new PackingRecipe(ModBlocks.COBBLED_DEEPSLATE_LOOSE, 8));

        PACKING_RECIPES.put(ModItems.STONE_BRICK, new PackingRecipe(ModBlocks.STONE_BRICKS_LOOSE, 4));
        //PACKING_RECIPES.put(ModItems.STONE_BRICK_1, new PackingRecipe(ModBlocks.MANTLESTONE_BRICKS_LOOSE, 4));
        PACKING_RECIPES.put(ModItems.STONE_BRICK_2, new PackingRecipe(ModBlocks.DEEPSLATE_BRICKS_LOOSE, 4));

        PACKING_RECIPES.put(Items.CLAY_BALL, new PackingRecipe(ModBlocks.CLAY_BLOCK, 9));


        //PACKING_RECIPES.put(Items.BONE, new PackingRecipe(ModBlocks.BONE_BLOCK, 9));
        //PACKING_RECIPES.put(Items.ROTTEN_FLESH, new PackingRecipe(ModBlocks.ROTTEN_FLESH_BLOCK, 9));
        PACKING_RECIPES.put(Items.RAW_GOLD, new PackingRecipe(Blocks.RAW_GOLD_BLOCK, 9));
        PACKING_RECIPES.put(Items.RAW_IRON, new PackingRecipe(Blocks.RAW_IRON_BLOCK, 9));
        PACKING_RECIPES.put(Items.RAW_COPPER, new PackingRecipe(Blocks.RAW_COPPER_BLOCK, 9));
        //PACKING_RECIPES.put(BTWR_Items.CREEPER_OYSTERS, new PackingRecipe(BTWR_Blocks.CREEPER_OYSTERS_BLOCK, 16));
        //PACKING_RECIPES.put(Items.SPIDER_EYE, new PackingRecipe(BTWR_Blocks.SPIDER_EYES_BLOCK, 16));
        //PACKING_RECIPES.put(Items.ENDER_PEARL, new PackingRecipe(BTWR_Blocks.ENDER_BLOCK, 9));

        PACKING_RECIPES.put(Items.BRICK, new PackingRecipe(ModBlocks.BRICKS_LOOSE, 8));
        PACKING_RECIPES.put(Items.NETHER_BRICK, new PackingRecipe(ModBlocks.NETHER_BRICKS_LOOSE, 8));
        //PACKING_RECIPES.put(BwtItems.dungItem, new PackingRecipe(BTWR_Blocks.DUNG_BLOCK, 8));
        //PACKING_RECIPES.put(Items.FLINT, new PackingRecipe(BTWR_Blocks.DUNG_BLOCK, 8));
        //PACKING_RECIPES.put(BwtItems.soapItem, new PackingRecipe(BTWR_Blocks.SOAP_BLOCK, 8));

    }


    private static void initLooseBlocksPacking() {
        //PACKING_RECIPES.put(ModBlocks.DIRT_LOOSE.asItem(), new PackingRecipe(ModBlocks.DIRT_PACKED, 2));
        PACKING_RECIPES.put(Blocks.SAND.asItem(), new PackingRecipe(Blocks.SANDSTONE, 2));
    }

    public static void attemptToPackItems(World world, BlockPos pos, Direction direction) {
        if (isLocationSuitableForPacking(world, pos)) {
            // Define bounding box around the target position
            Box targetBox = new Box(pos).expand(0.5); // Adjust box size slightly
            List<ItemEntity> itemsWithinBox = world.getEntitiesByClass(ItemEntity.class, targetBox, itemEntity -> true);

            if (!itemsWithinBox.isEmpty()) {
                PackingRecipe recipe = getValidRecipeFromItemList(itemsWithinBox);

                if (recipe != null) {
                    // Remove required items
                    removeItemsOfTypeFromList(recipe, itemsWithinBox);

                    // Create the packed block at the target position
                    createPackedBlockOfTypeAtLocation(world, recipe.block.getDefaultState(), pos);
                }
            }
        }
    }

    private static boolean isLocationSuitableForPacking(World world, BlockPos pos) {
        if (world.isAir(pos)) {
            for (Direction direction : Direction.values()) {
                BlockPos offsetPos = pos.offset(direction);

                if (!isBlockSuitableForPackingToFacing(world, offsetPos, direction.getOpposite())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    // TODO: this could probably get a better check on what block is the facing side of the packing
    private static boolean isBlockSuitableForPackingToFacing(World world, BlockPos pos, Direction direction) {
        Block block = world.getBlockState(pos).getBlock();

        // Check if the block is a moving piston (special case)
        if (block == Blocks.MOVING_PISTON) {
            return true; // Treat moving pistons as solid for packing
        }

        // Check if the block is solid or can block movement (including glass, etc.)
        if (block != Blocks.AIR && block.getDefaultState().isSolidBlock(world, pos)) {
            return true;
        }

        // Special case: treat transparent blocks like glass as suitable for packing
        return block.getDefaultState().isIn(ConventionalBlockTags.GLASS_BLOCKS);

    }

    private static void createPackedBlockOfTypeAtLocation(World world, BlockState state, BlockPos pos) {
        if (world.isAir(pos)) { // Ensure only air blocks are replaced
            world.setBlockState(pos, state, Block.NOTIFY_ALL); // Place the block with proper flags
            world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    private static void removeItemsOfTypeFromList(PackingRecipe recipe, List<ItemEntity> itemsWithinBox) {
        int countRequired = recipe.count;

        for (ItemEntity itemEntity : itemsWithinBox) {
            ItemStack stack = itemEntity.getStack();

            if (stack.getItem() == recipe.input) {
                int stackCount = stack.getCount();

                if (countRequired <= stackCount) {
                    stack.decrement(countRequired);

                    if (stack.isEmpty()) {
                        itemEntity.discard(); // Remove the entity if stack is empty
                    }
                    break;
                } else {
                    countRequired -= stackCount;
                    itemEntity.discard();
                }
            }
        }
    }

    private static PackingRecipe getValidRecipeFromItemList(List<ItemEntity> itemsWithinBox) {
        for (Map.Entry<ItemConvertible, PackingRecipe> entry : PACKING_RECIPES.entrySet()) {
            Item item = (Item) entry.getKey();
            PackingRecipe recipe = entry.getValue();

            int count = 0;
            for (ItemEntity itemEntity : itemsWithinBox) {
                if (itemEntity.getStack().getItem() == item) {
                    count += itemEntity.getStack().getCount();
                    if (count >= recipe.count) {
                        return recipe;
                    }
                }
            }
        }
        return null;
    }

    public static class PackingRecipe {
        final Block block;
        final int count;
        final Item input;

        PackingRecipe(Block block, int count) {
            this.block = block;
            this.count = count;
            this.input = block.asItem(); // Default input is the block's item representation
        }

        public BlockState getOutput() {
            return block.getDefaultState();
        }
    }
}