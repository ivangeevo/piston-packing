package org.ivangeevo.piston_packing.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;
import org.ivangeevo.piston_packing.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class PistonPackingLootTableProvider extends FabricBlockLootTableProvider {

    public PistonPackingLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        this.addDrop(ModBlocks.PISTON_SHOVEL, ModBlocks.PISTON_SHOVEL);
    }
}
