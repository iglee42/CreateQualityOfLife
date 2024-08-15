package fr.iglee42.createqualityoflife.utils;

import static com.simibubi.create.content.kinetics.belt.transport.BeltTunnelInteractionHandler.flapTunnel;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;

import fr.iglee42.createqualityoflife.blockentitites.FunneledBeltBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemHandlerHelper;

public class FunneledBeltInventory {

	final FunneledBeltBlockEntity belt;
	private final List<TransportedItemStack> items;
	final List<TransportedItemStack> toInsert;
	final List<TransportedItemStack> toRemove;
	boolean beltMovementPositive;
	final float SEGMENT_WINDOW = .75f;

	public FunneledBeltInventory(FunneledBeltBlockEntity be) {
		this.belt = be;
		items = new LinkedList<>();
		toInsert = new LinkedList<>();
		toRemove = new LinkedList<>();
	}

	public void tick() {

		// Added/Removed items from previous cycle
		if (!toInsert.isEmpty() || !toRemove.isEmpty()) {
			toInsert.forEach(this::insert);
			toInsert.clear();
			items.removeAll(toRemove);
			toRemove.clear();
			belt.setChanged();
			belt.sendData();
		}

		if (belt.getSpeed() == 0)
			return;

		// Reverse item collection if belt just reversed
		if (beltMovementPositive != belt.getDirectionAwareBeltMovementSpeed() > 0) {
			beltMovementPositive = !beltMovementPositive;
			Collections.reverse(items);
			belt.setChanged();
			belt.sendData();
		}

		// Assuming the first entry is furthest on the belt
		TransportedItemStack stackInFront = null;
		TransportedItemStack currentItem = null;
		Iterator<TransportedItemStack> iterator = items.iterator();

		// Useful stuff
		float beltSpeed = belt.getDirectionAwareBeltMovementSpeed();
		Direction movementFacing = belt.getMovementFacing();
		float spacing = 1;
		Level world = belt.getLevel();
		boolean onClient = world.isClientSide && !belt.isVirtual();


		// Loop over items
		while (iterator.hasNext()) {
			stackInFront = currentItem;
			currentItem = iterator.next();
			currentItem.prevBeltPosition = currentItem.beltPosition;
			currentItem.prevSideOffset = currentItem.sideOffset;

			if (currentItem.stack.isEmpty()) {
				iterator.remove();
				currentItem = null;
				continue;
			}

			float movement = beltSpeed;
			if (onClient)
				movement *= ServerSpeedProvider.get();

			// Don't move if held by processing (client)
			if (world.isClientSide && currentItem.locked)
				continue;

			// Don't move if held by external components
			if (currentItem.lockedExternally) {
				currentItem.lockedExternally = false;
				continue;
			}

			// Don't move if other items are waiting in front
			boolean noMovement = false;
			float currentPos = currentItem.beltPosition;
			if (stackInFront != null) {
				float diff = stackInFront.beltPosition - currentPos;
				if (Math.abs(diff) <= spacing)
					noMovement = true;
				movement =
					beltMovementPositive ? Math.min(movement, diff - spacing) : Math.max(movement, diff + spacing);
			}


			float limitedMovement =
				beltMovementPositive ? movement : -movement;
			float nextOffset = currentItem.beltPosition + limitedMovement;

			if (noMovement)
				continue;


			// Apply Movement
			currentItem.beltPosition += limitedMovement;
			currentItem.sideOffset +=
				(currentItem.getTargetSideOffset() - currentItem.sideOffset) * Math.abs(limitedMovement) * 2f;
			currentPos = currentItem.beltPosition;

			// Movement successful
			if (onClient)
				continue;

			// End reached
			checkForFunnels(currentItem,nextOffset);
		}
	}

	public boolean canInsert() {
		return toInsert.isEmpty() && items.size() < 2;
	}



	private BlockPos getNextPos(){
		return belt.getBlockPos().offset((belt.getBeltFacing().getAxis()== Direction.Axis.X ?beltMovementPositive ? 1 : -1 : 0),0,(belt.getBeltFacing().getAxis()== Direction.Axis.Z ?beltMovementPositive ? 1 : -1 : 0));
	}


	//



	public void addItem(TransportedItemStack newStack) {
		toInsert.add(newStack);
	}

	private void insert(TransportedItemStack newStack) {
		if (items.isEmpty())
			items.add(newStack);
		else {
			int index = 0;
			for (TransportedItemStack stack : items) {
				if (stack.compareTo(newStack) > 0 == beltMovementPositive)
					break;
				index++;
			}
			items.add(index, newStack);
		}
	}

	public TransportedItemStack getStack(int index) {

		return items.get(index);
	}

	public void read(CompoundTag nbt) {
		items.clear();
		nbt.getList("Items", Tag.TAG_COMPOUND)
			.forEach(inbt -> items.add(TransportedItemStack.read((CompoundTag) inbt)));
		beltMovementPositive = nbt.getBoolean("PositiveOrder");
	}

	public CompoundTag write() {
		CompoundTag nbt = new CompoundTag();
		ListTag itemsNBT = new ListTag();
		items.forEach(stack -> itemsNBT.add(stack.serializeNBT()));
		nbt.put("Items", itemsNBT);
		nbt.putBoolean("PositiveOrder", beltMovementPositive);
		return nbt;
	}

	public void ejectAll() {
		//items.forEach(this::eject);
		items.clear();
	}

	public void applyToEachWithin(float position, float maxDistanceToPosition,
		Function<TransportedItemStack, TransportedResult> processFunction) {
		boolean dirty = false;
		for (TransportedItemStack transported : items) {
			if (toRemove.contains(transported))
				continue;
			ItemStack stackBefore = transported.stack.copy();
			if (Math.abs(position - transported.beltPosition) >= maxDistanceToPosition)
				continue;
			TransportedResult result = processFunction.apply(transported);
			if (result == null || result.didntChangeFrom(stackBefore))
				continue;

			dirty = true;
			if (result.hasHeldOutput()) {
				TransportedItemStack held = result.getHeldOutput();
				held.beltPosition = ((int) position) + .5f - (beltMovementPositive ? 1 / 512f : -1 / 512f);
				toInsert.add(held);
			}
			toInsert.addAll(result.getOutputs());
			toRemove.add(transported);
		}
		if (dirty) {
			belt.setChanged();
			belt.sendData();
		}
	}

	public List<TransportedItemStack> getTransportedItems() {
		return items;
	}

	private boolean checkForFunnels(TransportedItemStack currentItem,
										  float nextOffset) {


			if (belt.getLevel().isClientSide /*|| belt.getBlockState().getOptionalValue(BeltFunnelBlock.POWERED).orElse(false)*/)
					return false;

			InvManipulationBehaviour inserting = belt.getInsertBehaviour();

			if (inserting == null)
					return false;

			int amountToExtract = 1;

			ItemStack toInsert = currentItem.stack.copy();
			if (amountToExtract > toInsert.getCount())
					return false;

/*			if (amountToExtract != -1 && modeToExtract != ItemHelper.ExtractionCountMode.UPTO) {
				toInsert.setCount(Math.min(amountToExtract, toInsert.getCount()));
				ItemStack remainder = inserting.simulate()
						.insert(toInsert);
				if (!remainder.isEmpty())
					if (blocking)
						return true;
					else
						continue;
			}*/

			inserting.findNewCapability();
			ItemStack remainder = inserting.insert(toInsert);
			if (toInsert.equals(remainder, false))
					return false;

			int notFilled = currentItem.stack.getCount() - toInsert.getCount();
			if (!remainder.isEmpty()) {
				remainder.grow(notFilled);
			} else if (notFilled > 0)
				remainder = ItemHandlerHelper.copyStackWithSize(currentItem.stack, notFilled);

			currentItem.stack = remainder;
			belt.sendData();

		return false;
	}

}
