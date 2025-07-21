package net.tonz.deadspace.mixin;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.*;
import net.tonz.deadspace.camera.CameraFramebufferManager;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    private Framebuffer originalEntityOutlinesFramebuffer;
    private Framebuffer originalTranslucentFramebuffer;
    private Framebuffer originalEntityFramebuffer;
    private Framebuffer originalParticlesFramebuffer;
    private Framebuffer originalWeatherFramebuffer;
    private Framebuffer originalCloudsFramebuffer;

    @Shadow @Nullable private Framebuffer entityOutlinesFramebuffer;
    @Shadow @Nullable private Framebuffer translucentFramebuffer;
    @Shadow @Nullable private Framebuffer entityFramebuffer;
    @Shadow @Nullable private Framebuffer particlesFramebuffer;
    @Shadow @Nullable private Framebuffer weatherFramebuffer;
    @Shadow @Nullable private Framebuffer cloudsFramebuffer;

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderHead(
            RenderTickCounter tickCounter,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightmapTextureManager lightmapTextureManager,
            Matrix4f projectionMatrix,
            Matrix4f viewMatrix,
            CallbackInfo ci
    ) {
        if (CameraFramebufferManager.rendering) {

            // Save originals
            originalEntityOutlinesFramebuffer = entityOutlinesFramebuffer;
            originalTranslucentFramebuffer = translucentFramebuffer;
            originalEntityFramebuffer = entityFramebuffer;
            originalParticlesFramebuffer = particlesFramebuffer;
            originalWeatherFramebuffer = weatherFramebuffer;
            originalCloudsFramebuffer = cloudsFramebuffer;

            // Swap in your custom framebuffers
            entityOutlinesFramebuffer = CameraFramebufferManager.entityOutlinesFramebuffer;
            translucentFramebuffer = CameraFramebufferManager.translucentFramebuffer;
            entityFramebuffer = CameraFramebufferManager.entityFramebuffer;
            particlesFramebuffer = CameraFramebufferManager.particlesFramebuffer;
            weatherFramebuffer = CameraFramebufferManager.weatherFramebuffer;
            cloudsFramebuffer = CameraFramebufferManager.cloudsFramebuffer;

            CameraFramebufferManager.bindCustomFramebuffer();

        }
        //CameraFramebufferManager.setMatrices(projectionMatrix, viewMatrix);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void onRenderReturn(
            RenderTickCounter tickCounter,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightmapTextureManager lightmapTextureManager,
            Matrix4f projectionMatrix,
            Matrix4f viewMatrix,
            CallbackInfo ci
    ) {
        if (CameraFramebufferManager.rendering) {
            CameraFramebufferManager.unbindCustomFramebuffer();

            // Swap out your custom framebuffers
            CameraFramebufferManager.entityOutlinesFramebuffer = entityOutlinesFramebuffer;
            CameraFramebufferManager.translucentFramebuffer = translucentFramebuffer;
            CameraFramebufferManager.entityFramebuffer = entityFramebuffer;
            CameraFramebufferManager.particlesFramebuffer = particlesFramebuffer;
            CameraFramebufferManager.weatherFramebuffer = weatherFramebuffer;
            CameraFramebufferManager.cloudsFramebuffer = cloudsFramebuffer;

            // Restore originals
            entityOutlinesFramebuffer = originalEntityOutlinesFramebuffer;
            translucentFramebuffer = originalTranslucentFramebuffer;
            entityFramebuffer = originalEntityFramebuffer;
            particlesFramebuffer = originalParticlesFramebuffer;
            weatherFramebuffer = originalWeatherFramebuffer;
            cloudsFramebuffer = originalCloudsFramebuffer;
        }
    }
}