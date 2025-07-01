package net.tonz.deadspace;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class DeadSpaceClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CameraRenderer.init(256, 256);

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.world != null && client.player != null) {
                int textureId = CameraRenderer.getFramebufferTexture();

                if (textureId > 0) {
                    // Set the OpenGL texture to the framebuffer's color attachment
                    RenderSystem.setShaderTexture(0, textureId);

                    // Draw the texture at screen coords (x=10, y=10) at 128x128 resolution
                    drawContext.drawTexture(
                            Identifier.of("minecraft", "white"), // Texture is bound via GL, so this can be dummy
                            10, 10, // X, Y
                            0f, 0f, // U, V (not used here)
                            128, 128, // Width, Height
                            128, 128 // Texture width & height
                    );
                }
            }
        });
    }
}
