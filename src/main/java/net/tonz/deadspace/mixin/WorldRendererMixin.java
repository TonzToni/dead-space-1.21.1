// src/main/java/net/tonz/deadspace/mixin/WorldRendererMixin.java
package net.tonz.deadspace.mixin;

import net.minecraft.client.render.*;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    private void printMatrices(
            RenderTickCounter tickCounter,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightmapTextureManager lightmapTextureManager,
            Matrix4f matrix4f,
            Matrix4f matrix4f2,
            CallbackInfo ci
    ) {
        //System.out.println("Main Camera Projection Matrix:\n" + matrix4f);
        //System.out.println("Main Camera View Matrix:\n" + matrix4f2);
    }
}