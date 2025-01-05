package org.ivangeevo.piston_packing;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.ivangeevo.piston_packing.datagen.PistonPackingRecipeProvider;
import org.tough_environment.datagen.*;

public class PistonPackingDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(PistonPackingRecipeProvider::new);
    }


}
