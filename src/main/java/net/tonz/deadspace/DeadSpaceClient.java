package net.tonz.deadspace;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public class DeadSpaceClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(DeadSpace.DISPLAY_BLOCK_ENTITY, DisplayBlockEntityRenderer::new);
    }
}
