package net.tonz.deadspace.camera;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class CameraFramebufferManager {
    private static SimpleFramebuffer framebuffer;

    private static boolean initialized = false;
    private static boolean rendering = false;


    public static Matrix4f viewMatrix;
    public static Matrix4f projectionMatrix;

    public static int count;

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
        WorldRenderEvents.END.register(context -> {
            if (!initialized) {
                init(MinecraftClient.getInstance().getWindow().getWidth(), MinecraftClient.getInstance().getWindow().getHeight());
            }

            //ModCamera camera = CameraFramebufferManager.getCamera();
            renderCustomCamera();

            // unused methods
            //renderRed(); // flawless easy works
        });
    }

    public static void renderCustomCamera() {
        if (framebuffer == null || rendering) return;
        rendering = true;

        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            Camera camera = mc.gameRenderer.getCamera();
            if (!initialized || mc.world == null) return;

            WorldRenderer worldRenderer = mc.worldRenderer;
            if (worldRenderer == null) return;
            mc.getFramebuffer().endWrite();
            framebuffer.beginWrite(true);

            GL11.glViewport(0, 0, framebuffer.textureWidth, framebuffer.textureHeight);

            float tickDelta = mc.getRenderTickCounter().getTickDelta(true);
            Vec3d skyColorVec = mc.world.getSkyColor(camera.getPos(), tickDelta);
            RenderSystem.clearColor((float)skyColorVec.x, (float)skyColorVec.y, (float)skyColorVec.z, 1.0f);
            RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);

            viewMatrix = setCustomViewMatrix(camera);

            float viewDistance = mc.options.getClampedViewDistance() * 16;
            projectionMatrix = setCustomProjectionMatrix(mc.options.getFov().getValue() * (float) (Math.PI / 180.0), (float)mc.getWindow().getWidth() / mc.getWindow().getHeight(), 0.05f, viewDistance * 4.0F);


            //RenderSystem.setProjectionMatrix(projectionMatrix, VertexSorter.BY_DISTANCE);
            //worldRenderer.setupFrustum(camera.getPos(), customViewMatrix, projectionMatrix);

            worldRenderer.render(
                    mc.getRenderTickCounter(),
                    false,
                    camera,
                    mc.gameRenderer,
                    mc.gameRenderer.getLightmapTextureManager(),
                    viewMatrix,
                    projectionMatrix
            );

            framebuffer.endWrite();
            mc.getFramebuffer().beginWrite(false);

        } finally {
            rendering = false;
            initialized = false;
        }
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

    public static float getAspectRatio() {
        if (framebuffer == null) return 1.0f; // Default aspect ratio
        return (float) framebuffer.textureWidth / (float) framebuffer.textureHeight;
    }

    public static Matrix4f setCustomProjectionMatrix(float fov, float aspectRatio, float zNear, float zFar) {
        return new Matrix4f()
                .perspective(fov, aspectRatio, zNear, zFar);
    }

    public static Matrix4f setCustomViewMatrix(Camera camera) {
        Quaternionf quaternionf = camera.getRotation().conjugate(new Quaternionf());
        return new Matrix4f().rotation(quaternionf);
    }
}

