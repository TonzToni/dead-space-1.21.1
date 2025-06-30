package net.tonz.deadspace;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.AbstractBlock;
import net.minecraft.util.math.BlockPos;

public class DisplayBlock extends Block implements BlockEntityProvider {
    public DisplayBlock() {
        super(AbstractBlock.Settings.create().strength(1.0f));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DisplayBlockEntity(pos, state);
    }
}