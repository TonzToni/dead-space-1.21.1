package net.tonz.deadspace.displayblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tonz.deadspace.block.ModBlockEntities;
import net.tonz.deadspace.camera.CameraStorage;
import net.tonz.deadspace.camera.ModCamera;

public class DisplayBlock extends Block implements BlockEntityProvider {
    public DisplayBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DisplayBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) return null;
        if (type == ModBlockEntities.DISPLAY_BLOCK_ENTITY) {
            return (w, pos, st, blockEntity) -> DisplayBlockEntity.tick(w, pos, st, (DisplayBlockEntity) blockEntity);
        }
        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!world.isClient) return;  // only spawn camera on client

        Vec3d camPos = new Vec3d(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
        //CameraStorage.camera = new ModCamera(camPos, 0f, 0f);
        System.out.println("Camera placed at " + camPos);
    }
}