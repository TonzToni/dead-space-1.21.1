package net.tonz.deadspace.displayblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tonz.deadspace.block.ModBlockEntities;

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
}