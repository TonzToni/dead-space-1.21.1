package net.tonz.deadspace.displayblock;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tonz.deadspace.block.ModBlockEntities;

public class DisplayBlockEntity extends BlockEntity {

    private int counter = 0;
    private int tickCount = 0;

    public DisplayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DISPLAY_BLOCK_ENTITY, pos, state);
    }

    public int getCounter() {
        return counter;
    }

    public void increment() {
        tickCount++;
        if (tickCount >= 20)
        {
            counter++;
            System.out.println("Counter: " + counter);
            if (counter >= 10) {
                counter = 0;
            }
            markDirty();
            tickCount = 0;
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("Counter", counter);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        counter = nbt.getInt("Counter");
    }

    public static void tick(World world, BlockPos pos, BlockState state, DisplayBlockEntity blockEntity) {
        blockEntity.increment(); // or whatever you want it to do each tick
    }
}