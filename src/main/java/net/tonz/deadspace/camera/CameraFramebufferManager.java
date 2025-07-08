package net.tonz.deadspace.camera;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

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


    private static SimpleFramebuffer framebuffer;

    private static boolean initialized = false;
    public static boolean rendering = false;


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

        if (rendering) return;
        rendering = true;

        mc.getFramebuffer().endWrite();
        framebuffer.beginWrite(true);

        GL11.glViewport(0, 0, framebuffer.textureWidth, framebuffer.textureHeight);

        RenderSystem.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.clear(
                GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT,
                MinecraftClient.IS_SYSTEM_MAC);

        viewMatrix = setCustomViewMatrix(camera);

        float viewDistance = mc.options.getClampedViewDistance() * 16;
        projectionMatrix = setCustomProjectionMatrix(
                mc.options.getFov().getValue() * (float) (Math.PI / 180.0),
                (float)mc.getWindow().getWidth() / mc.getWindow().getHeight(),
                0.05f,
                viewDistance * 4.0F);


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

        // Composite/blit your custom framebuffers here
        blitToMainFramebuffer();

        // 2. Bind your custom framebuffer as a texture
        //GL30.glBindTexture(GL30.GL_TEXTURE_2D, cloudsFramebuffer.getColorAttachment());


        // 4. Unbind texture
        //GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);

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
        if (framebuffer == null) {
            // Initialize if needed
            MinecraftClient mc = MinecraftClient.getInstance();
            framebuffer = new SimpleFramebuffer(mc.getWindow().getWidth(), mc.getWindow().getHeight(), true, false);
        }
        prevFramebufferId = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        framebuffer.beginWrite(true);
    }

    public static void setMatrices(Matrix4f projection, Matrix4f view) {
        projectionMatrix = new Matrix4f(projection);
        viewMatrix = new Matrix4f(view);
        // Optionally: RenderSystem.setProjectionMatrix(projectionMatrix, VertexSorter.BY_DISTANCE);
    }

    public static void unbindCustomFramebuffer() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, prevFramebufferId);
    }

    public static Framebuffer getEntityOutlinesFramebuffer() {
        if (entityOutlinesFramebuffer == null) {
            entityOutlinesFramebuffer = new SimpleFramebuffer(MinecraftClient.getInstance().getWindow().getWidth(), MinecraftClient.getInstance().getWindow().getHeight(), true, false);
        }
        return entityOutlinesFramebuffer;
    }

    public static Framebuffer getTranslucentFramebuffer() {
        if (translucentFramebuffer == null) {
            translucentFramebuffer = new SimpleFramebuffer(MinecraftClient.getInstance().getWindow().getWidth(), MinecraftClient.getInstance().getWindow().getHeight(), true, false);
        }
        return translucentFramebuffer;
    }

    public static Framebuffer getEntityFramebuffer() {
        if (entityFramebuffer == null) {
            entityFramebuffer = new SimpleFramebuffer(MinecraftClient.getInstance().getWindow().getWidth(), MinecraftClient.getInstance().getWindow().getHeight(), true, false);
        }
        return entityFramebuffer;
    }

    public static Framebuffer getParticlesFramebuffer() {
        if (particlesFramebuffer == null) {
            particlesFramebuffer = new SimpleFramebuffer(MinecraftClient.getInstance().getWindow().getWidth(), MinecraftClient.getInstance().getWindow().getHeight(), true, false);
        }
        return particlesFramebuffer;
    }

    public static Framebuffer getWeatherFramebuffer() {
        if (weatherFramebuffer == null) {
            weatherFramebuffer = new SimpleFramebuffer(MinecraftClient.getInstance().getWindow().getWidth(), MinecraftClient.getInstance().getWindow().getHeight(), true, false);
        }
        return weatherFramebuffer;
    }

    public static Framebuffer getCloudsFramebuffer() {
        if (cloudsFramebuffer == null) {
            cloudsFramebuffer = new SimpleFramebuffer(MinecraftClient.getInstance().getWindow().getWidth(), MinecraftClient.getInstance().getWindow().getHeight(), true, false);
        }
        return cloudsFramebuffer;
    }

    private static void blitToMainFramebuffer() {
        // Example: draw the contents of your custom framebuffers to the main framebuffer
        // Use OpenGL or Minecraft's rendering utilities as needed
        // For each framebuffer:
        // framebuffer.draw(x, y, width, height, ...);

        if (entityOutlinesFramebuffer != null) {
            entityOutlinesFramebuffer.draw(framebuffer.textureWidth, framebuffer.textureHeight, true);
        }
        if (translucentFramebuffer != null) {
            translucentFramebuffer.draw(framebuffer.textureWidth, framebuffer.textureHeight, true);
        }
        if (entityFramebuffer != null) {
            entityFramebuffer.draw(framebuffer.textureWidth, framebuffer.textureHeight, true);
        }
        if (particlesFramebuffer != null) {
            particlesFramebuffer.draw(framebuffer.textureWidth, framebuffer.textureHeight, true);
        }
        if (weatherFramebuffer != null) {
            weatherFramebuffer.draw(framebuffer.textureWidth, framebuffer.textureHeight, true);
        }
        if (cloudsFramebuffer != null) {
            cloudsFramebuffer.draw(framebuffer.textureWidth, framebuffer.textureHeight, true);
        }
    }
}

