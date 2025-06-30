package net.tonz.deadspace;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.joml.Quaternionf;

public class DisplayBlockEntityRenderer implements BlockEntityRenderer<DisplayBlockEntity> {
    private static final Identifier PLACEHOLDER_TEXTURE = Identifier.of("minecraft", "textures/block/stone.png");

    public DisplayBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(DisplayBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        // Translate to center of block
        matrices.translate(0.5, 0.5, 0.5);

        // Rotate to face front (optional)
        matrices.multiply(new Quaternionf().identity());

        // Scale down the rendered quad
        matrices.scale(1.0f, 1.0f, 1.0f);

        RenderUtils.drawTexturedQuad(matrices, vertexConsumers, PLACEHOLDER_TEXTURE, light);

        matrices.pop();
    }
}