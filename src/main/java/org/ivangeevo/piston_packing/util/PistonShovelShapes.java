package org.ivangeevo.piston_packing.util;

import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.math.Box;

import java.util.function.Function;

public class PistonShovelShapes {

    public static VoxelShape flipVertically(VoxelShape shape) {
        return transformShape(shape, box -> new Box(
                box.minX,
                16 - box.maxY,
                box.minZ,
                box.maxX,
                16 - box.minY,
                box.maxZ
        ));
    }

    public static VoxelShape rotateY90(VoxelShape shape) {
        return transformShape(shape, box -> new Box(
                16 - box.maxZ,
                box.minY,
                box.minX,
                16 - box.minZ,
                box.maxY,
                box.maxX
        ));
    }

    public static VoxelShape rotateY180(VoxelShape shape) {
        return transformShape(shape, box -> new Box(
                16 - box.maxX,
                box.minY,
                16 - box.maxZ,
                16 - box.minX,
                box.maxY, 16 - box.minZ
        ));
    }

    public static VoxelShape rotateY270(VoxelShape shape) {
        return transformShape(shape, box -> new Box(
                box.minZ,
                box.minY,
                16 - box.maxX,
                box.maxZ,
                box.maxY,
                16 - box.minX
        ));
    }

    private static VoxelShape transformShape(VoxelShape shape, Function<Box, Box> transformer) {
        VoxelShape result = VoxelShapes.empty();
        for (Box box : shape.getBoundingBoxes()) {
            result = VoxelShapes.union(result, VoxelShapes.cuboid(box.minX / 16.0, box.minY / 16.0, box.minZ / 16.0,
                    box.maxX / 16.0, box.maxY / 16.0, box.maxZ / 16.0));
        }
        return result;
    }
}
