package org.ivangeevo.piston_packing.recipe;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.tough_environment.ToughEnvironmentMod;

public class ModRecipes
{

    public static void registerRecipes() {

        // Piston Packing
        Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(ToughEnvironmentMod.MOD_ID, PistonPackingRecipe.Serializer.ID),
                PistonPackingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, Identifier.of(ToughEnvironmentMod.MOD_ID, PistonPackingRecipe.Type.ID),
                PistonPackingRecipe.Type.INSTANCE);

    }
}
