package org.ivangeevo.piston_packing.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.ivangeevo.piston_packing.PistonPackingMod;
import org.ivangeevo.piston_packing.block.blocks.PistonShovelBlock;

public class ModBlocks
{

    public static final Block PISTON_SHOVEL = register("piston_shovel", new PistonShovelBlock(AbstractBlock.Settings.create().strength(5f).sounds(BlockSoundGroup.METAL)));

    private static Block register(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(PistonPackingMod.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(PistonPackingMod.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerToItemGroups() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(PISTON_SHOVEL);

        });
    }
}
