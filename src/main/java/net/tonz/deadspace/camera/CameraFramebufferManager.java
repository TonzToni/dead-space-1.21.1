package net.tonz.deadspace.camera;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class CameraFramebufferManager {

    // Our off-screen framebuffer
    private static SimpleFramebuffer framebuffer;

    // Initialize the framebuffer with given size
    public static void init(int width, int height) {
        // Create a framebuffer that does not use depth/stencil attachment (false)
        framebuffer = new SimpleFramebuffer(width, height, false, MinecraftClient.IS_SYSTEM_MAC);

        // Set the default clear color to red (R:1, G:0, B:0, A:1)
        framebuffer.setClearColor(1.0F, 0.0F, 0.0F, 1.0F);
    }

    // Render a red texture to the framebuffer
    public static void renderRed() {
        if (framebuffer == null) return;

        // Start writing into the framebuffer, clearing previous contents
        framebuffer.beginWrite(true);

        // Set the OpenGL clear color to solid red
        GL11.glClearColor(1.0F, 0.0F, 0.0F, 1.0F);

        // Clear the framebuffer with the red color (color + depth buffer)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        // Stop writing — now framebuffer has a red texture stored
        framebuffer.endWrite();
    }

    // Return the OpenGL texture ID of the framebuffer so it can be drawn on blocks
    public static int getTextureId() {
        return framebuffer != null ? framebuffer.getColorAttachment() : -1;
    }
}
