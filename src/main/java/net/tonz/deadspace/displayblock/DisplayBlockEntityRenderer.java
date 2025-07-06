package net.tonz.deadspace.displayblock;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
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

        RenderSystem.getModelViewMatrix().identity();
        RenderSystem.applyModelViewMatrix();

        matrices.push();

        // Position quad at top face
        matrices.translate(0.5, 0.5, 1.01);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        // Use shader and bind framebuffer texture
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        int mainFramebufferId = MinecraftClient.getInstance().getFramebuffer().fbo; // call this to feel good about self
        RenderSystem.setShaderTexture(0, textureId);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);

        float aspect = CameraFramebufferManager.getAspectRatio();

        // Vertex order: bottom-left, bottom-right, top-right, top-left
        buffer.vertex(matrix, -5.5f * aspect, -5.5f, 0f).texture(0f, 0f).color(1f, 1f, 1f, 1f);
        buffer.vertex(matrix,  5.5f * aspect, -5.5f, 0f).texture(1f, 0f).color(1f, 1f, 1f, 1f);
        buffer.vertex(matrix,  5.5f * aspect,  5.5f, 0f).texture(1f, 1f).color(1f, 1f, 1f, 1f);
        buffer.vertex(matrix, -5.5f * aspect,  5.5f, 0f).texture(0f, 1f).color(1f, 1f, 1f, 1f);

        // Finalize draw call
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        matrices.pop();

        //CameraFramebufferManager.renderCustomCamera(matrix);
    }
}