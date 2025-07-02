package net.tonz.deadspace;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.tonz.deadspace.block.ModBlockEntities;
import net.tonz.deadspace.camera.CameraFramebufferManager;
import net.tonz.deadspace.camera.CameraStorage;
import net.tonz.deadspace.displayblock.DisplayBlockEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

@Environment(EnvType.CLIENT)
public class DeadSpaceClient implements ClientModInitializer {
    private boolean framebufferInitialized = false;

    @Override
    public void onInitializeClient() {
        // Register the block entity renderer (optional here, but good to keep)
        BlockEntityRendererRegistry.register(ModBlockEntities.DISPLAY_BLOCK_ENTITY, DisplayBlockEntityRenderer::new);

        // Delay framebuffer initialization until client window is ready
        WorldRenderEvents.END.register(context -> {
            if (!framebufferInitialized) {
                CameraFramebufferManager.init(256, 256);
                CameraFramebufferManager.renderRed();
                framebufferInitialized = true;
                System.out.println("Framebuffer initialized during render phase.");
            }
        });
        /* // WHEN USED DOESNT RENDER ARM
        WorldRenderEvents.END.register(context -> {
            CameraFramebufferManager.init(256, 256);
            CameraFramebufferManager.renderRed(); // re-clear every frame
        });
         */
    }

}