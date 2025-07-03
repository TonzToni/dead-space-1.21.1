package net.tonz.deadspace.camera;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class CameraFramebufferManager {

    // Our off-screen framebuffer
    private static SimpleFramebuffer framebuffer;

    private static boolean initialized = false;

    // Initialize the framebuffer with given size
    public static void init(int width, int height) {

        if (framebuffer != null) {
            framebuffer.delete();
        }

        framebuffer = new SimpleFramebuffer(width, height, true, false);
        framebuffer.setClearColor(0f, 0f, 0f, 1f);
        initialized = true;
    }

    public static void registerRenderHook() {
        ClientTickEvents.END_CLIENT_TICK.register(context -> {
            if (!initialized) {
                init(1024, 1024);
            }
            RenderSystem.recordRenderCall(() -> {
                renderPlayerCameraView(); // safe on render thread
            });

        });
    }

    public static Matrix4f getViewMatrix(Camera camera) {
        // Create identity matrix
        Matrix4f viewMatrix = new Matrix4f().identity();

        // Apply rotation (must be conjugated because Minecraft uses right-handed system)
        Quaternionf rotation = new Quaternionf(camera.getRotation()).conjugate();
        viewMatrix.rotate(rotation);

        // Apply translation (negative because we are moving the world opposite to camera)
        //Vec3d pos = camera.getPos();
        //viewMatrix.translate((float) -pos.x, (float) -pos.y, (float) -pos.z);

        return viewMatrix;
    }

    public static void renderPlayerCameraView() {
        if (framebuffer == null) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        Camera camera = mc.gameRenderer.getCamera();
        WorldRenderer worldRenderer = mc.worldRenderer;
        float tickDelta = mc.getRenderTickCounter().getTickDelta(true);

        if (!initialized || mc.world == null || mc.player == null || mc.gameRenderer == null) return;

        try {
            camera = mc.gameRenderer.getCamera();
        } catch (Exception e) {
            return;
        }

        framebuffer.beginWrite(true);

        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, true);

        float fov = mc.options.getFov().getValue();
        //Matrix4f projectionMatrix = mc.gameRenderer.getBasicProjectionMatrix(fov);
        Matrix4f viewMatrix = RenderSystem.getModelViewMatrix();
        //System.out.println("viewMatrix Output: " + viewMatrix);

        Matrix4f projectionMatrix = mc.gameRenderer.getBasicProjectionMatrix(mc.options.getFov().getValue().floatValue());
        //Matrix4f viewMatrix = getViewMatrix(camera);

        RenderTickCounter tickCounter = mc.getRenderTickCounter();

        worldRenderer.render(
                tickCounter,
                false,
                camera,
                mc.gameRenderer,
                mc.gameRenderer.getLightmapTextureManager(),
                projectionMatrix,
                viewMatrix
        );

        framebuffer.endWrite();
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

    public static Framebuffer getFramebuffer() {
        return framebuffer;
    }
}
