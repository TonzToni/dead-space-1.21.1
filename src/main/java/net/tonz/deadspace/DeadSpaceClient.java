package net.tonz.deadspace;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.tonz.deadspace.camera.CameraStorage;

@Environment(EnvType.CLIENT)
public class DeadSpaceClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Print position every 20 ticks (~1 second)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) return;

            tickCounter++;
            if (tickCounter >= 20) {
                tickCounter = 0;

                if (CameraStorage.camera != null) {
                    System.out.println("Camera at: " + CameraStorage.camera.getPos());
                }
            }
        });
    }
    private int tickCounter = 0;
}
