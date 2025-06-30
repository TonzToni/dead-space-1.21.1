package net.tonz.deadspace;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class DisplayBlockEntity extends BlockEntity {
    public DisplayBlockEntity(BlockPos pos, BlockState state) {
        super(DeadSpace.DISPLAY_BLOCK_ENTITY, pos, state);
    }
}