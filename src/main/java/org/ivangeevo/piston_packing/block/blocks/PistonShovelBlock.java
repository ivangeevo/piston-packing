package org.ivangeevo.piston_packing.block.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class PistonShovelBlock extends Block {

    // Define properties
    public static final DirectionProperty FACING = Properties.FACING;
    public static final IntProperty VERTICAL_ORIENTATION = IntProperty.of("vertical_orientation", 0, 1);

    public PistonShovelBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(VERTICAL_ORIENTATION, 0)); // Default state
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, VERTICAL_ORIENTATION);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        // Logic for setting FACING and VERTICAL_ORIENTATION properties
        BlockState blockState = this.getDefaultState();
        Direction facing = ctx.getPlayerLookDirection();
        int verticalOrientation = ctx.getHitPos().getY() > 0.5F ? 0 : 1;
        blockState = blockState.with(FACING, facing).with(VERTICAL_ORIENTATION, verticalOrientation);
        return blockState;
    }


}
