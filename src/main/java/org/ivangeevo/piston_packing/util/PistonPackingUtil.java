package org.ivangeevo.piston_packing.util;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
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


    public void attemptToPackItems(World world, PistonBlockEntity pistonBE) {
        if (!world.isClient && pistonBE.isExtending()
                && (pistonBE.getPushedBlock().getBlock() == Blocks.PISTON_HEAD
                || PistonUtils.isPushingNormalCube(world, pistonBE.getPos(), pistonBE)
                || pistonBE.getPushedBlock().getBlock().getDefaultState().isIn(ConventionalBlockTags.GLASS_BLOCKS))
        ) {
            BlockPos pos = pistonBE.getPos().offset(pistonBE.getFacing());

            if (isLocationSuitableForPacking(world, pos, pistonBE.getMovementDirection().getOpposite())) {
                Box targetBox = new Box(pos);
                List<ItemEntity> itemsWithinBox = world.getEntitiesByClass(ItemEntity.class, targetBox, itemEntity -> true);

                if (!itemsWithinBox.isEmpty()) {
                    PackingRecipe recipe = getValidRecipeFromItemList(itemsWithinBox, world);

                    if (recipe != null) {
                        for (Ingredient ingredient : recipe.getIngredients()) {
                            for (ItemStack stack : ingredient.getMatchingStacks()) {
                                removeItemsOfTypeFromList(stack, stack.getCount(), itemsWithinBox);
                            }
                        }

                        Optional<BlockState> blockStateOptional = BlockStateUtil.getBlockStateFromIngredient(recipe.getBlockResult());

                        if (blockStateOptional.isPresent()) {
                            BlockState blockStateResult = blockStateOptional.get();
                            createPackedBlockOfTypeAtLocation(world, blockStateResult, pos);
                        }

                    }
                }
            }
        }
    }

    private static boolean isLocationSuitableForPacking(World world, BlockPos pos, Direction pistonDirection) {
        if (world.isAir(pos)) {
            for (Direction direction : Direction.values()) {

                if (direction != pistonDirection) {

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

    private static boolean isBlockSuitableForPackingToFacing(World world, BlockPos pos, Direction facing) {
        Block block = world.getBlockState(pos).getBlock();

        if ( block != null ) {
            return PistonUtils.canContainPistonPackingToFacing(world, pos, facing) ;
        }

        return false;
    }

    private static void createPackedBlockOfTypeAtLocation(World world, BlockState state, BlockPos pos) {
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
        world.playSound(null, pos, state.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    private static void removeItemsOfTypeFromList(ItemStack stack, int iCount, List<ItemEntity> itemsWithinBox) {
        int inputCount = iCount;

        for (ItemEntity itemEntity : itemsWithinBox) {

            if (stack != null) {
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