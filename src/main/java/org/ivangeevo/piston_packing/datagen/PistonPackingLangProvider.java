package org.ivangeevo.piston_packing.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import org.ivangeevo.piston_packing.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class PistonPackingLangProvider extends FabricLanguageProvider {


    public PistonPackingLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder tb) {
        tb.add(ModBlocks.PISTON_SHOVEL, "Piston Shovel");
    }
}
