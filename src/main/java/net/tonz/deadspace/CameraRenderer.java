package net.tonz.deadspace;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.Camera;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class CameraRenderer {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private static SimpleFramebuffer framebuffer;

    private static CameraRenderer cameraRenderer;

    public CameraRenderer(int width, int height) {
        framebuffer = new SimpleFramebuffer(width, height, false, MinecraftClient.IS_SYSTEM_MAC);
    }

    public static void init(int width, int height) {
        cameraRenderer = new CameraRenderer(width, height);
    }

    public static SimpleFramebuffer getFramebuffer() {
        return cameraRenderer != null ? cameraRenderer.framebuffer : null;
    }

    public static void render(Camera camera, float tickDelta) {
        if (cameraRenderer != null) {
            cameraRenderer.renderToFramebuffer(camera, tickDelta);
        }
    }

    public void renderToFramebuffer(Camera camera, float tickDelta) {
        framebuffer.beginWrite(true);  // true = clear color & depth

        // Setup viewport to framebuffer size
        RenderSystem.viewport(0, 0, framebuffer.textureWidth, framebuffer.textureHeight);

        // Clear color & depth buffers
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, true);

        // Render the world from the alternate camera viewpoint
        RenderTickCounter counter = client.getRenderTickCounter();
        client.gameRenderer.renderWorld(counter);

        framebuffer.endWrite();

        // Reset viewport to window size (important!)
        int windowWidth = client.getWindow().getFramebufferWidth();
        int windowHeight = client.getWindow().getFramebufferHeight();
        RenderSystem.viewport(0, 0, windowWidth, windowHeight);
    }

    public static int getFramebufferTexture() {
        return framebuffer.getColorAttachment();
    }
}
