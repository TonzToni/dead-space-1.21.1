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
    private static boolean initialized = false;

    // Initialize the framebuffer with given size
    public static void init(int width, int height) {
        if (initialized) return;

        framebuffer = new SimpleFramebuffer(width, height, false, MinecraftClient.IS_SYSTEM_MAC);
        //framebuffer.setClearColor(1.0F, 0.0F, 0.0F, 1.0F);

        // Optional: set nearest filter for debug
        int texId = framebuffer.getColorAttachment();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        System.out.println("Framebuffer created: " + width + "x" + height + ", texture ID: " + texId);

        initialized = true;
    }

    // Render a red texture to the framebuffer
    public static void renderRed() {
        if (framebuffer == null) return;

        // Start writing into the framebuffer, clearing previous contents
        framebuffer.beginWrite(true);

        // Set the OpenGL clear color to solid red
        GL11.glClearColor(0.75F, 0.2F, 0.2F, 1.0F);

        // Clear the framebuffer with the red color (color + depth buffer)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        // Stop writing — now framebuffer has a red texture stored
        framebuffer.endWrite();

        // Check for GL errors
        int error = GL11.glGetError();
        if (error != GL11.GL_NO_ERROR) {
            System.out.println("OpenGL Error during framebuffer clear: " + error);
        }
    }

    // Return the OpenGL texture ID of the framebuffer so it can be drawn on blocks
    public static int getTextureId() {
        return framebuffer != null ? framebuffer.getColorAttachment() : -1;
    }
}
