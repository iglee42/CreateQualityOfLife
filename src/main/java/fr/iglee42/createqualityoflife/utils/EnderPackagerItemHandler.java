package fr.iglee42.createqualityoflife.utils;

import com.simibubi.create.content.logistics.box.PackageItem;

import fr.iglee42.createqualityoflife.blockentitites.EnderPackagerBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class EnderPackagerItemHandler implements IItemHandlerModifiable {

	private EnderPackagerBlockEntity blockEntity;

	public EnderPackagerItemHandler(EnderPackagerBlockEntity blockEntity) {
		this.blockEntity = blockEntity;
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return blockEntity.heldBox;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (slot != 0)
			return;
		blockEntity.heldBox = stack;
		blockEntity.notifyUpdate();
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return insertItem(stack,simulate,false);
	}

	public ItemStack insertItem(ItemStack stack,boolean simulate, boolean fromNetwork){
		if (!blockEntity.heldBox.isEmpty())
			return stack;
		if (!blockEntity.isTransmitter() && !fromNetwork)
			return stack;
		if (!isItemValid(0, stack))
			return stack;
		if (!simulate) {
			setStackInSlot(0,stack.copyWithCount(1));
			if (!blockEntity.isTransmitter())blockEntity.receivedBox();
			else blockEntity.attemptToSend();
		}
		return stack.copyWithCount(stack.getCount() - 1);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (blockEntity.animationTicks != 0)
			return ItemStack.EMPTY;
		ItemStack box = blockEntity.heldBox;
		if (!simulate)
			setStackInSlot(slot, ItemStack.EMPTY);
		return box;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return PackageItem.isPackage(stack);
	}

	public EnderPackagerBlockEntity getBlockEntity() {
		return blockEntity;
	}
}
