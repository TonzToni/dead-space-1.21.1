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

    }
}