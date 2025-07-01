package net.tonz.deadspace.camera;


import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

public class ModCamera extends Camera {
    private Vec3d fixedPos;
    private float fixedPitch;
    private float fixedYaw;
    private static ModCamera modCamera;

    public ModCamera(Vec3d pos, float pitch, float yaw) {
        super();
        this.fixedPos = pos;
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

    // Override update to keep fixed pos/rot even if Minecraft tries to update it
    @Override
    public void update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta) {
        super.update(area, focusedEntity, thirdPerson, inverseView, tickDelta);
        // forcibly reset to fixed values to override any change
        this.setPos(fixedPos);
        this.setRotation(fixedPitch, fixedYaw);

        System.out.println("Pos: " + fixedPos);
    }
}
