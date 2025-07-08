package net.tonz.deadspace.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.tonz.deadspace.DeadSpace;
import net.tonz.deadspace.displayblock.DisplayBlock;

public class ModBlocks {
    public static final Block DISPLAY_BLOCK = new DisplayBlock(FabricBlockSettings.create().strength(4.0f));

    public static void register() {
        Registry.register(Registries.BLOCK, Identifier.of(DeadSpace.MOD_ID, "display_block"), DISPLAY_BLOCK);

        Registry.register(Registries.ITEM, Identifier.of(DeadSpace.MOD_ID, "display_block"),
                new BlockItem(DISPLAY_BLOCK, new Item.Settings()));
    }
}