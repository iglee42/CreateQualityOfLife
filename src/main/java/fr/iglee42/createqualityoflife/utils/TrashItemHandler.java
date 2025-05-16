package fr.iglee42.createqualityoflife.utils;

import fr.iglee42.createqualityoflife.blockentitites.TrashCanBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class TrashItemHandler extends ItemStackHandler {

    private final TrashCanBlockEntity be;

    public TrashItemHandler(TrashCanBlockEntity be) {
        super(1);
        this.be = be;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (!be.canAcceptItem(stack)) return stack;
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }
}
