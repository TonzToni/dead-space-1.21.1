package net.tonz.deadspace.camera;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

public class ModCamera extends Camera {
    private Vec3d fixedPos;
    private float fixedPitch;
    private float fixedYaw;

    public ModCamera(Vec3d pos, float pitch, float yaw) {
        super();
        this.fixedPos = pos;
        this.fixedPitch = pitch;
        this.fixedYaw = yaw;
    }

    @Override
    public Vec3d getPos() {
        return fixedPos;
    }

    @Override
    public float getPitch() {
        return fixedPitch;
    }

    @Override
    public float getYaw() {
        return fixedYaw;
    }

    @Override
    public void update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta) {
        this.setPos(fixedPos);
        this.setRotation(fixedPitch, fixedYaw);
    }
}
