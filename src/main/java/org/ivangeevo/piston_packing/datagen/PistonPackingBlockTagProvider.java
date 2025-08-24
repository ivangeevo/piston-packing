package org.ivangeevo.piston_packing.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import org.ivangeevo.piston_packing.tag.ModTags;

import java.util.concurrent.CompletableFuture;

public class PistonPackingBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public PistonPackingBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

        getOrCreateTagBuilder(ModTags.Blocks.PISTON_SHOVEL_BREAKABLE)
                .add(Blocks.GRASS_BLOCK)
                .add(Blocks.MYCELIUM)
                .add(Blocks.SAND)
                .add(Blocks.CLAY)
                .add(Blocks.DIRT)
                .add(Blocks.GRAVEL)
                .add(Blocks.SOUL_SAND);

    }
}
