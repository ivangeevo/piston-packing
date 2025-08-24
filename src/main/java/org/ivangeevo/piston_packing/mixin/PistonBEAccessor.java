package org.ivangeevo.piston_packing.mixin;

import net.minecraft.block.entity.PistonBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PistonBlockEntity.class)
public interface PistonBEAccessor {

    @Accessor("lastProgress")
    float getLastProgress();
}
