package org.ivangeevo.piston_packing.util;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.ivangeevo.piston_packing.recipe.PackingRecipeInput;
import org.ivangeevo.piston_packing.recipe.PistonPackingRecipe;

import java.util.*;

// TODO:
/** new and better class code? idk. try it after the recipe class is finished **/
public class PistonPackingUtil
{
    private static final PistonPackingUtil instance = new PistonPackingUtil();
    private PistonPackingUtil() {}
    public static PistonPackingUtil getInstance() {
        return instance;
    }

    final RecipeManager.MatchGetter<PackingRecipeInput, PistonPackingRecipe> matchGetter =
            RecipeManager.createCachedMatchGetter(PistonPackingRecipe.Type.INSTANCE);

    public void attemptToPackItems(World world, BlockPos pos, Direction direction) {
        if (isLocationSuitableForPacking(world, pos)) {
            // Define bounding box around the target position
            Box targetBox = new Box(pos).expand(0.5); // Adjust box size slightly
            List<ItemEntity> itemsWithinBox = world.getEntitiesByClass(ItemEntity.class, targetBox, itemEntity -> true);

            if (!itemsWithinBox.isEmpty()) {
                PistonPackingRecipe recipe = getValidRecipeFromItemList(itemsWithinBox, world);

                if (recipe != null) {
                    // Remove required items
                    removeItemsOfTypeFromList(recipe, itemsWithinBox);

                    Optional<BlockState> blockStateOptional = BlockStateUtil.getBlockStateFromIngredient(recipe.getBlockResult());

                    if (blockStateOptional.isPresent()) {
                        BlockState blockStateResult = blockStateOptional.get();
                        // Use blockStateResult
                        createPackedBlockOfTypeAtLocation(world, blockStateResult, pos);
                    }

                    // Create the packed block at the target position
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

    private static void removeItemsOfTypeFromList(PistonPackingRecipe recipe, List<ItemEntity> itemsWithinBox) {
        int countRequired = recipe.getIngredients().size();

        for (ItemEntity itemEntity : itemsWithinBox) {
            ItemStack stack = itemEntity.getStack();

            if (recipe.getIngredients().getFirst().test(stack)) {
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

    private  PistonPackingRecipe getValidRecipeFromItemList(List<ItemEntity> itemsWithinBox, World world) {
        DefaultedList<ItemStack> stack = createIngredientFromItems(itemsWithinBox);
        Optional<RecipeEntry<PistonPackingRecipe>> optionalRecipe = getRecipeFor(world, stack);

        return optionalRecipe.map(RecipeEntry::value).orElse(null);
    }

    private static DefaultedList<ItemStack> createIngredientFromItems(List<ItemEntity> itemsWithinBox) {
        return DefaultedList.copyOf(ItemStack.EMPTY, itemsWithinBox.stream().map(ItemEntity::getStack).toArray(ItemStack[]::new));

    }

    public Optional<RecipeEntry<PistonPackingRecipe>> getRecipeFor(World world, DefaultedList<ItemStack> ingredient) {
        if (ingredient.isEmpty()) {
            return Optional.empty();
        }
        // Now you have the matching items as ItemConvertible (Item[])
        return world.getRecipeManager().getFirstMatch(PistonPackingRecipe.Type.INSTANCE,
                new PackingRecipeInput(ingredient), world);
    }



}