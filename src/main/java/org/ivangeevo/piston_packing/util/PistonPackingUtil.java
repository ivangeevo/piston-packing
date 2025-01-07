package org.ivangeevo.piston_packing.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.ivangeevo.piston_packing.recipe.PackingRecipeInput;
import org.ivangeevo.piston_packing.recipe.PackingRecipe;

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

    final RecipeManager.MatchGetter<PackingRecipeInput, PackingRecipe> matchGetter =
            RecipeManager.createCachedMatchGetter(PackingRecipe.Type.INSTANCE);

    public void attemptToPackItems(World world, BlockPos pos) {
        if (world.isClient) {
            return;
        }

        if (!(world.getBlockEntity(pos) instanceof PistonBlockEntity pistonBE)) {
            return;
        }

        Direction movementDirection = pistonBE.getMovementDirection();
        boolean isExtending = pistonBE.isExtending();
        if (isExtending) {
            pos = pos.offset(movementDirection);
        } else {
            pos = pos.offset(movementDirection.getOpposite());
        }

        if (!isLocationSuitableForPacking(world, pos, pistonBE)) {
            return;
        }

        List<ItemEntity> itemsWithinBox = world.getEntitiesByClass(ItemEntity.class, new Box(pos), itemEntity -> true);

        if (!itemsWithinBox.isEmpty()) {
            PackingRecipe recipe = getValidRecipeFromItemList(itemsWithinBox, world);

            if (recipe != null) {
                removeItemsOfTypeFromList(recipe, itemsWithinBox);

                Optional<BlockState> blockStateOptional = BlockStateUtil.getBlockStateFromIngredient(recipe.getBlockResult());

                if (blockStateOptional.isPresent()) {
                    BlockState blockStateResult = blockStateOptional.get();
                    createPackedBlockOfTypeAtLocation(world, blockStateResult, pos);
                }

            }
        }
    }

    private static boolean isLocationSuitableForPacking(World world, BlockPos pos, PistonBlockEntity pistonBE) {
        if (world.isAir(pos)) {
            for (Direction direction : Direction.values()) {

                if (direction != pistonBE.getMovementDirection()) {

                    BlockPos tempPos = pos.offset(direction);

                    if (!isBlockSuitableForPackingToFacing(world, tempPos, direction.getOpposite())) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
    private static boolean isBlockSuitableForPackingToFacing(World world, BlockPos pos) {
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
     **/

    private static boolean isBlockSuitableForPackingToFacing(World world, BlockPos pos, Direction facing) {
        Block block = world.getBlockState(pos).getBlock();

        if ( block != null ) {
            return PistonUtils.canContainPistonPackingToFacing(world, pos, facing) ;
        }

        return false;
    }



    private static void createPackedBlockOfTypeAtLocation(World world, BlockState state, BlockPos pos) {
        if (world.isAir(pos)) {
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
            world.playSound(null, pos, state.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    private static void removeItemsOfTypeFromList(PackingRecipe recipe, List<ItemEntity> itemsWithinBox) {
        ItemStack inputStack = recipe.getIngredients().getFirst().getMatchingStacks()[0];
        int inputCount = recipe.getIngredients().size();

        for (ItemEntity tempItem : itemsWithinBox) {
            if (tempItem.isAlive()) {
                ItemStack tempStack = tempItem.getStack();

                if (tempStack == inputStack) {
                    int newCount;
                    if (tempStack.getCount() > inputCount) {
                        newCount = tempStack.getCount() - inputCount;
                        tempStack.setCount(newCount);
                        break;
                    } else {
                        inputCount = inputCount - tempStack.getCount();
                        tempStack.setCount(0);
                        tempItem.kill();

                        if (inputCount <= 0) {
                            break;
                        }
                    }


                }
            }
        }

    }

    /**
    private static void removeItemsOfTypeFromList(PackingRecipe recipe, List<ItemEntity> itemsWithinBox) {
        int inputCount = recipe.getIngredients().size();

        for (ItemEntity itemEntity : itemsWithinBox) {
            ItemStack stack = itemEntity.getStack();

            if (recipe.getIngredients().getFirst().test(stack)) {
                int stackCount = stack.getCount();

                if (inputCount <= stackCount) {
                    stack.decrement(inputCount);

                    if (stack.isEmpty()) {
                        itemEntity.discard(); // Remove the entity if stack is empty
                    }
                    break;
                } else {
                    inputCount -= stackCount;
                    itemEntity.discard();
                }
            }
        }
    }
     **/

    private PackingRecipe getValidRecipeFromItemList(List<ItemEntity> itemsWithinBox, World world) {
        DefaultedList<ItemStack> stack = createIngredientFromItems(itemsWithinBox);
        Optional<RecipeEntry<PackingRecipe>> optionalRecipe = getRecipeFor(world, stack);

        return optionalRecipe.map(RecipeEntry::value).orElse(null);
    }

    private static DefaultedList<ItemStack> createIngredientFromItems(List<ItemEntity> itemsWithinBox) {
        return DefaultedList.copyOf(ItemStack.EMPTY, itemsWithinBox.stream().map(ItemEntity::getStack).toArray(ItemStack[]::new));

    }

    public Optional<RecipeEntry<PackingRecipe>> getRecipeFor(World world, DefaultedList<ItemStack> ingredient) {
        if (ingredient.isEmpty()) {
            return Optional.empty();
        }
        // Now you have the matching items as ItemConvertible (Item[])
        return world.getRecipeManager().getFirstMatch(PackingRecipe.Type.INSTANCE,
                new PackingRecipeInput(ingredient), world);
    }



}