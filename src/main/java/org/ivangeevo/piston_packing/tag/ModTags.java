package org.ivangeevo.piston_packing.tag;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.ivangeevo.piston_packing.PistonPackingMod;

public class ModTags {

    public static class Blocks {
        public static final TagKey<Block> PISTON_SHOVEL_BREAKABLE = register("piston_shovel_breakable");

        private static TagKey<Block> register(String id) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(PistonPackingMod.MOD_ID, id));
        }
    }

    public static class Items {

        private static TagKey<Item> register(String id) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(PistonPackingMod.MOD_ID, id));
        }
    }
}
