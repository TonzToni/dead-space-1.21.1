package net.tonz.deadspace;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class DisplayBlockEntityRenderer {

    public void render(DisplayBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // Prepare the matrices to position the quad on your block face
        matrices.push();

        // Translate and rotate the matrix stack to the block face position/orientation
        matrices.translate(0.5, 0.5, 0.5);  // example: center of block
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));  // flip if needed

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getCutout());

        int textureId = CameraRenderer.getFramebufferTexture();

        RenderSystem.setShaderTexture(0, textureId);

        // Draw a simple quad with texture coordinates covering the quad
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        vertexConsumer.vertex(matrix4f, -0.5f, 0.5f, 0f).texture(0f, 0f);
        vertexConsumer.vertex(matrix4f, 0.5f, 0.5f, 0f).texture(1f, 0f);
        vertexConsumer.vertex(matrix4f, 0.5f, -0.5f, 0f).texture(1f, 1f);
        vertexConsumer.vertex(matrix4f, -0.5f, -0.5f, 0f).texture(0f, 1f);

        matrices.pop();
    }


}
