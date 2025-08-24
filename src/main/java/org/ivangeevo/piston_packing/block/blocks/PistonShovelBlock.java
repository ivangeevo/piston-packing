package org.ivangeevo.piston_packing.block.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class PistonShovelBlock extends Block {

    public static final DirectionProperty FACING = Properties.FACING;
    public static final DirectionProperty VERTICAL_DIRECTION = Properties.VERTICAL_DIRECTION;


    public PistonShovelBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, VERTICAL_DIRECTION);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction side = ctx.getSide();
        Vec3d hitPos = ctx.getHitPos();
        double hitY = hitPos.y - ctx.getBlockPos().getY();

        // Defaults
        Direction facing = ctx.getHorizontalPlayerFacing().getOpposite(); // Block faces player
        Direction vertical = Direction.UP; // Default vertical orientation

        if (side == Direction.DOWN || hitY > 0.5F) {
            vertical = Direction.DOWN;
        }

        return this.getDefaultState().with(VERTICAL_DIRECTION, vertical).with(FACING, facing);
    }

    public Direction getFacing(BlockState state) {
        return state.get(FACING);
    }
    

}
