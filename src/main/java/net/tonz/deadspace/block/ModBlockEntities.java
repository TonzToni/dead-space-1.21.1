package net.tonz.deadspace.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.tonz.deadspace.DeadSpace;
import net.tonz.deadspace.DisplayBlockEntity;

public class ModBlockEntities {
    public static BlockEntityType<DisplayBlockEntity> DISPLAY_BLOCK_ENTITY;

    public static void register() {
        DISPLAY_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(DeadSpace.MOD_ID, "display_block_entity"),
                FabricBlockEntityTypeBuilder.create(DisplayBlockEntity::new, ModBlocks.DISPLAY_BLOCK).build()
        );
    }
}