package net.tonz.deadspace;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.Vec3d;

public class CameraUpdateHandler {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            RenderTickCounter renderTickCounter = client.getRenderTickCounter();
            float tickDelta = renderTickCounter.getTickDelta(true);

            ModCamera camera = new ModCamera();

            camera.setFixedPosition(new Vec3d(client.player.getX() + 2, client.player.getY() + 1.5, client.player.getZ()));
            camera.setFixedRotation(0f, 0f);

            camera.update(client.world, client.player, false, false, tickDelta);

            CameraRenderer.render(camera, tickDelta);
        });
    }
}