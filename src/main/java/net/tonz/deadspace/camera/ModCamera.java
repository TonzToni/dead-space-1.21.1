package net.tonz.deadspace.camera;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

public class ModCamera extends Camera {
    private Vec3d customPos;
    private float customPitch;
    private float customYaw;

    public ModCamera() {
        this.customPos = new Vec3d(0, 0, 0);
        this.customPitch = 0;
        this.customYaw = 0;
        this.setRotation(customYaw, customPitch);
    }

    @Override
    public void update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta) {
        this.setPos(customPos);
        this.setRotation(customYaw, customPitch);
    }

    public void setCustomPosition(double x, double y, double z) {
        this.customPos = new Vec3d(x, y, z);
        this.setPos(customPos);
    }

    public void setCustomRotation(float pitch, float yaw) {
        this.customPitch = pitch;
        this.customYaw = yaw;
        this.setRotation(yaw, pitch);
    }
}