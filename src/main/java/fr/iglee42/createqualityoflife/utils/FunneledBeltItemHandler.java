package fr.iglee42.createqualityoflife.utils;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class FunneledBeltItemHandler implements IItemHandler {

	private final FunneledBeltInventory beltInventory;

	public FunneledBeltItemHandler(FunneledBeltInventory beltInventory) {
		this.beltInventory = beltInventory;
	}

	@Override
	public int getSlots() {
		return 2;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		TransportedItemStack stackAtOffset = this.beltInventory.getStack(slot);
		if (stackAtOffset == null)
			return ItemStack.EMPTY;
		return stackAtOffset.stack;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (this.beltInventory.canInsert()) {
			if (!simulate) {
				TransportedItemStack newStack = new TransportedItemStack(stack);
				newStack.insertedAt = 0;
				newStack.beltPosition = .5f + (beltInventory.beltMovementPositive ? -1 : 1) / 16f;
				newStack.prevBeltPosition = newStack.beltPosition;
				this.beltInventory.addItem(newStack);
				this.beltInventory.belt.setChanged();
				this.beltInventory.belt.sendData();
			}
			return ItemStack.EMPTY;
		}
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		TransportedItemStack transported = this.beltInventory.getStack(slot);
		if (transported == null)
			return ItemStack.EMPTY;

		amount = Math.min(amount, transported.stack.getCount());
		ItemStack extracted = simulate ? transported.stack.copy().split(amount) : transported.stack.split(amount);
		if (!simulate) {
			if (transported.stack.isEmpty())
				this.beltInventory.toRemove.add(transported);
			this.beltInventory.belt.setChanged();
			this.beltInventory.belt.sendData();
		}
		return extracted;
	}

	@Override
	public int getSlotLimit(int slot) {
		return Math.min(getStackInSlot(slot).getMaxStackSize(), 64);
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return true;
	}

}