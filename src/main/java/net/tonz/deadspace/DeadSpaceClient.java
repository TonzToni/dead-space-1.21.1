package net.tonz.deadspace;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.tonz.deadspace.block.ModBlockEntities;
import net.tonz.deadspace.camera.CameraFramebufferManager;
import net.tonz.deadspace.displayblock.DisplayBlockEntityRenderer;

@Environment(EnvType.CLIENT)
public class DeadSpaceClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CameraFramebufferManager.registerRenderHook();
        BlockEntityRendererRegistry.register(ModBlockEntities.DISPLAY_BLOCK_ENTITY, DisplayBlockEntityRenderer::new);

    }

}