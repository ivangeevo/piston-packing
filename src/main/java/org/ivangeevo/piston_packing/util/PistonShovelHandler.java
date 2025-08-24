package org.ivangeevo.piston_packing.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.ivangeevo.piston_packing.block.blocks.PistonShovelBlock;
import org.ivangeevo.piston_packing.tag.ModTags;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

import static net.minecraft.block.Block.*;

public interface PistonShovelHandler {

    default void tryShovelBlock(World world, BlockPos posTo, Direction pistonDirection, boolean retracted, CallbackInfoReturnable<Boolean> cir) {
        // The block state of the piston shovel
        BlockState shovelState = world.getBlockState(posTo);

        // The position to break with the shovel on push
        BlockPos shoveledPos = posTo.offset(pistonDirection);

        // Check if a piston shovel is facing properly first
        if (isShovelFacing(world.getBlockState(posTo), pistonDirection)
                && shovelState.getBlock() instanceof PistonShovelBlock pistonShovelBlock)
        {
            BlockState targetState = world.getBlockState(shoveledPos);
            Direction shovelFacing = pistonShovelBlock.getVerticalDirection(shovelState);

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
                    dropShoveledStacks(targetState, world, shoveledPos, world.getBlockEntity(shoveledPos), null, fakeTool, shovelFacing);
                    cir.setReturnValue(true);
                }
            }
        }
    }

    default boolean isShovelFacing(BlockState state, Direction pistonDirection) {
        if (!(state.getBlock() instanceof PistonShovelBlock shovelBlock)) return false;
        Direction verticalDirection = pistonDirection.getAxis().isVertical() ? pistonDirection : Direction.UP;
        return (shovelBlock.getFacing(state) == pistonDirection || shovelBlock.getVerticalDirection(state) == verticalDirection);
    }

    static void dropShoveledStacks(BlockState state, World world, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack tool, Direction facing) {
        if (world instanceof ServerWorld) {
            getDroppedStacks(state, (ServerWorld)world, pos, blockEntity, entity, tool).forEach((stack) -> dropShovelStack(world, pos, stack, facing));
            state.onStacksDropped((ServerWorld)world, pos, tool, true);
        }

    }

    static void dropShovelStack(World world, BlockPos pos, ItemStack stack, Direction facing) {
        double d = (double) EntityType.ITEM.getHeight() / (double)2.0F;
        double e = (double)pos.getX() + (double)0.5F + MathHelper.nextDouble(world.random, -0.25F, 0.25F);
        double f = (double)pos.getY() + (double)0.5F + MathHelper.nextDouble(world.random, -0.25F, 0.25F) - d;
        double g = (double)pos.getZ() + (double)0.5F + MathHelper.nextDouble(world.random, -0.25F, 0.25F);
        dropStack(world, () -> new ItemEntity(world, e, f, g, stack), stack, facing);
    }

    static void dropStack(World world, Supplier<ItemEntity> itemEntitySupplier, ItemStack stack, Direction facing) {
        if (!world.isClient && !stack.isEmpty() && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            ItemEntity itemEntity = itemEntitySupplier.get();

            // Apply velocity in eject direction (scaled down for realism)
            Vec3d velocity = Vec3d.of(facing.getVector()).multiply(0.25);
            itemEntity.setVelocity(velocity);

            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
        }
    }


}
