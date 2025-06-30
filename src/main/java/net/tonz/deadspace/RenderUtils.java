package net.tonz.deadspace;


import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class RenderUtils {
    public static void drawTexturedQuad(MatrixStack matrices, VertexConsumerProvider consumers, Identifier texture, int light) {
        VertexConsumer consumer = consumers.getBuffer(RenderLayer.getEntityCutout(texture));
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float size = 0.5f;

        consumer.vertex(matrix, -size, -size, 0).texture(0, 1).light(light);
        consumer.vertex(matrix, -size,  size, 0).texture(0, 0).light(light);
        consumer.vertex(matrix,  size,  size, 0).texture(1, 0).light(light);
        consumer.vertex(matrix,  size, -size, 0).texture(1, 1).light(light);
    }
}