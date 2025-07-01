package net.tonz.deadspace.displayblock;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.tonz.deadspace.camera.CameraFramebufferManager;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class DisplayBlockEntityRenderer implements BlockEntityRenderer<DisplayBlockEntity> {

    // Constructor with Context param as required by Fabric API
    public DisplayBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        // You can initialize things here if needed
    }

    @Override
    public void render(DisplayBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {

        int textureId = CameraFramebufferManager.getTextureId();
        if (textureId <= 0) return;

        matrices.push();

        // Move the quad to the top face center of the block
        matrices.translate(0.5, 1.01, 0.5);

        // Rotate quad to lay flat on the top face (X-axis -90 degrees)
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));

        // Get the transformation matrix for vertices
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        // Bind the framebuffer texture for rendering
        RenderSystem.setShaderTexture(0, textureId);

        // Get vertex consumer for cutout render layer (supports transparency)
        //VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getCutout());
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getSolid());

        // Define the normal vector facing up (outward from quad)
        float normalX = 0f;
        float normalY = 0f;
        float normalZ = 1f;

        // Define full white color with full opacity
        int r = 255, g = 255, b = 255, a = 255;

        // Use full brightness packed light value
        int packedLight = 0xF000F0;

        // Build the quad (4 vertices) with position, texture UV, color, light, and normal
        vertexConsumer.vertex(matrix, -0.5f, -0.5f, 0f)
                .color(r, g, b, a)
                .texture(0f, 0f)
                .light(packedLight)
                .normal(normalX, normalY, normalZ);

        vertexConsumer.vertex(matrix,  0.5f, -0.5f, 0f)
                .color(r, g, b, a)
                .texture(1f, 0f)
                .light(packedLight)
                .normal(normalX, normalY, normalZ);

        vertexConsumer.vertex(matrix,  0.5f,  0.5f, 0f)
                .color(r, g, b, a)
                .texture(1f, 1f)
                .light(packedLight)
                .normal(normalX, normalY, normalZ);

        vertexConsumer.vertex(matrix, -0.5f,  0.5f, 0f)
                .color(r, g, b, a)
                .texture(0f, 1f)
                .light(packedLight)
                .normal(normalX, normalY, normalZ);

        matrices.pop();
    }
}