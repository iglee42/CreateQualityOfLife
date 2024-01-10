package fr.iglee42.createqualityoflife.utils;

import fr.iglee42.createqualityoflife.blockentitites.InventoryLinkerBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class ArmorItemStackHandler extends InventoryLinkerStacksHandler {

    public ArmorItemStackHandler(NonNullList<ItemStack> stacks,InventoryLinkerBlockEntity be) {
        super(stacks,be);
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.getItem() instanceof ArmorItem armor){
            if (slot != armor.getSlot().getIndex()) return stack;
        } else return stack;
        return super.insertItem(slot, stack, simulate);
    }
}
