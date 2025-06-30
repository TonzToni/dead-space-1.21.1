package net.tonz.deadspace;

import net.fabricmc.api.ModInitializer;
import net.tonz.deadspace.block.ModBlockEntities;
import net.tonz.deadspace.block.ModBlocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeadSpace implements ModInitializer {
	public static final String MOD_ID = "deadspace";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.register();
		ModBlockEntities.register();
	}
}