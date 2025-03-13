package org.ivangeevo.piston_packing.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import org.ivangeevo.piston_packing.block.ModBlocks;

public class PistonPackingModClient implements ClientModInitializer
{

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PISTON_SHOVEL, RenderLayer.getCutout());
    }
}
