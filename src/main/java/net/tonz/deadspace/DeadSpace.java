package net.tonz.deadspace;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class DeadSpace implements ModInitializer {
	public static final String MOD_ID = "deadspace";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Block DISPLAY_BLOCK = new DisplayBlock();
	public static BlockEntityType<DisplayBlockEntity> DISPLAY_BLOCK_ENTITY;

	@Override
	public void onInitialize() {
		Registry.register(Registries.BLOCK, id("display_block"), DISPLAY_BLOCK);
		DISPLAY_BLOCK_ENTITY = Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				id("display_block_entity"),
				FabricBlockEntityTypeBuilder.create(DisplayBlockEntity::new, DISPLAY_BLOCK).build()
		);

		// Register the block item so it shows in creative inventory
		Registry.register(Registries.ITEM, id("display_block"), DISPLAY_BLOCK_ENTITY);
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}