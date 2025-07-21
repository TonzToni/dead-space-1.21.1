package net.tonz.deadspace.camera;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Shadow;

import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;

@Environment(EnvType.CLIENT)
public class CameraFramebufferManager {
    private static int prevFramebufferId;

    //private PostEffectProcessor entityOutlinePostProcessor;

    public static Framebuffer entityOutlinesFramebuffer;
    public static Framebuffer translucentFramebuffer;
    public static Framebuffer entityFramebuffer;
    public static Framebuffer particlesFramebuffer;
    public static Framebuffer weatherFramebuffer;
    public static Framebuffer cloudsFramebuffer;

    private static Framebuffer framebuffer;

    private static boolean initialized = false;
    public static boolean rendering = false;


    public static Matrix4f viewMatrix;
    public static Matrix4f projectionMatrix;

    // Initialize the framebuffer with given size
    public static void init(int width, int height) {

        if (framebuffer != null) {
            framebuffer.delete();
        }

        framebuffer = new SimpleFramebuffer(width, height, true, false);
        initialized = true;
    }

    public static void registerRenderHook() {
        WorldRenderEvents.END.register(context -> {
            if (!initialized) {
                init(MinecraftClient.getInstance().getWindow().getWidth(), MinecraftClient.getInstance().getWindow().getHeight());
            }

            //ModCamera camera = CameraFramebufferManager.getCamera();

            // this is the closest I can possibly get with minecraft render pipeline without altering it, in order to properly render things like water, entities, portals,
            // I would need to isolate the main render pass. currently without doing that, duplicates of these things are created outside my created framebuffer and instead placed into the main one with incorrect matrices.
            renderCustomCamera();

            // unused methods
            //renderRed(); // flawless easy works
        });
    }

    public static void renderCustomCamera() {
        MinecraftClient mc = MinecraftClient.getInstance();
        Camera camera = mc.gameRenderer.getCamera();
        WorldRenderer worldRenderer = mc.worldRenderer;

        if (rendering || mc.world == null) return;
        rendering = true;

        mc.getFramebuffer().endWrite();
        framebuffer.beginWrite(true);

        // Get the sky color as a Vec3d
        Vec3d skyColor = mc.world.getSkyColor(camera.getPos(), mc.getRenderTickCounter().getTickDelta(true));

        // Extract RGB components
        float skyRed = (float) skyColor.x;
        float skyGreen = (float) skyColor.y;
        float skyBlue = (float) skyColor.z;

        RenderSystem.clearColor(skyRed, skyGreen, skyBlue, 1.0f); // Opaque sky color
        RenderSystem.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, true);

        //GL30.glViewport(0, 0, framebuffer.textureWidth, framebuffer.textureHeight);

        viewMatrix = setCustomViewMatrix(camera);

        float viewDistance = (mc.options.getClampedViewDistance() * 16) * 4.0f;
        float fov = mc.options.getFov().getValue() * (float) (Math.PI / 180.0);
        projectionMatrix = setCustomProjectionMatrix(
                fov,
                getAspectRatio(),
                0.05f,
                viewDistance);


        RenderSystem.setProjectionMatrix(projectionMatrix, VertexSorter.BY_Z);

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

        rendering = false;
        initialized = false;
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

    public static void bindCustomFramebuffer() {
        prevFramebufferId = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
    }

    public static void setMatrices(Matrix4f projection, Matrix4f view) {
        projectionMatrix = new Matrix4f(projection);
        viewMatrix = new Matrix4f(view);
    }

    public static void unbindCustomFramebuffer() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, prevFramebufferId);
    }
}

