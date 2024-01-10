package fr.iglee42.createqualityoflife.utils;

import fr.iglee42.createqualityoflife.blockentitites.InventoryLinkerBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class InventoryLinkerStacksHandler extends ItemStackHandler {

    private InventoryLinkerBlockEntity be;

    public InventoryLinkerStacksHandler(int size,InventoryLinkerBlockEntity be) {
        super(size);
        this.be = be;
    }
    public InventoryLinkerStacksHandler(NonNullList<ItemStack> stacks, InventoryLinkerBlockEntity be) {
        super(stacks);
        this.be = be;
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (be.getPlayerPaperItemStack().isEmpty()) return stack;
        return super.insertItem(slot, stack, simulate);
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (be.getPlayerPaperItemStack().isEmpty()) return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }
}
