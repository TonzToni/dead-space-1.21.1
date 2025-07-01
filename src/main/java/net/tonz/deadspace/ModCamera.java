package net.tonz.deadspace;

import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;

public class ModCamera extends Camera {

    private Vec3d fixedPos;
    private float fixedPitch;
    private float fixedYaw;

    public ModCamera() {
        super();
    }

    public void setFixedPosition(Vec3d pos) {
        this.fixedPos = pos;
    }

    public void setFixedRotation(float pitch, float yaw) {
        this.fixedPitch = pitch;
        this.fixedYaw = yaw;
    }

    @Override
    public Vec3d getPos() {
        return fixedPos != null ? fixedPos : super.getPos();
    }

    @Override
    public float getPitch() {
        return fixedPitch;
    }

    @Override
    public float getYaw() {
        return fixedYaw;
    }
}