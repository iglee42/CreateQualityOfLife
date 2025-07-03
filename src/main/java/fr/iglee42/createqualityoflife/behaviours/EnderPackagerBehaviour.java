package fr.iglee42.createqualityoflife.behaviours;

import java.util.function.*;

import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.blockentitites.EnderPackagerBlockEntity;
import fr.iglee42.createqualityoflife.utils.EnderPackagerItemHandler;
import fr.iglee42.createqualityoflife.utils.EnderPackagersNetworkHandler;
import fr.iglee42.createqualityoflife.utils.IEnderLinkable;
import fr.iglee42.createqualityoflife.utils.EnderPackagersNetworkHandler.Frequency;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;

import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EnderPackagerBehaviour extends BlockEntityBehaviour implements IEnderLinkable, ClipboardCloneable {

	public static final BehaviourType<EnderPackagerBehaviour> TYPE = new BehaviourType<>();

	enum Mode {
		TRANSMIT, RECEIVE
	}

	public Frequency frequencyFirst;
	public Frequency frequencyLast;
	public ValueBoxTransform firstSlot;
	public ValueBoxTransform secondSlot;
	Vec3 textShift;

	public boolean newPosition;
	private Mode mode;
	private Supplier<EnderPackagerItemHandler> handlerSupplier;

	protected EnderPackagerBehaviour(SmartBlockEntity be, Pair<ValueBoxTransform, ValueBoxTransform> slots) {
		super(be);
		frequencyFirst = Frequency.EMPTY;
		frequencyLast = Frequency.EMPTY;
		firstSlot = slots.getLeft();
		secondSlot = slots.getRight();
		textShift = Vec3.ZERO;
		newPosition = true;
	}

	public static EnderPackagerBehaviour receiver(SmartBlockEntity be, Pair<ValueBoxTransform, ValueBoxTransform> slots,
												  Supplier<EnderPackagerItemHandler> handlerSupplier) {
		EnderPackagerBehaviour behaviour = new EnderPackagerBehaviour(be, slots);
		behaviour.handlerSupplier = handlerSupplier;
		behaviour.mode = Mode.RECEIVE;
		return behaviour;
	}

	public static EnderPackagerBehaviour transmitter(SmartBlockEntity be, Pair<ValueBoxTransform, ValueBoxTransform> slots) {
		EnderPackagerBehaviour behaviour = new EnderPackagerBehaviour(be, slots);
		behaviour.mode = Mode.TRANSMIT;
		return behaviour;
	}

	public EnderPackagerBehaviour moveText(Vec3 shift) {
		textShift = shift;
		return this;
	}

	public void copyItemsFrom(EnderPackagerBehaviour behaviour) {
		if (behaviour == null)
			return;
		frequencyFirst = behaviour.frequencyFirst;
		frequencyLast = behaviour.frequencyLast;
	}

	public void sendPackage(ItemStack pItem){
		getHandler().sendPackage(getWorld(),this,pItem);
	}

	public boolean canSendPackage(ItemStack pItem){
		return getHandler().canSend(getWorld(),this,pItem);
	}


	@Override
	public boolean isListening() {
		return mode == Mode.RECEIVE;
	}

	@Override
	public void setReceivedPackage(ItemStack packageStack) {
		handlerSupplier.get().insertItem(packageStack,false,true);
	}

	@Override
	public void initialize() {
		super.initialize();
		if (getWorld().isClientSide)
			return;
		getHandler().addToNetwork(getWorld(), this);
		newPosition = true;
	}

	@Override
	public Couple<Frequency> getNetworkKey() {
		return Couple.create(frequencyFirst, frequencyLast);
	}

	@Override
	public void unload() {
		super.unload();
		if (getWorld().isClientSide)
			return;
		getHandler().removeFromNetwork(getWorld(), this);
	}

	@Override
	public boolean isSafeNBT() {
		return true;
	}

	@Override
	public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(nbt, registries, clientPacket);
		nbt.put("FrequencyFirst", frequencyFirst.getStack()
			.saveOptional(registries));
		nbt.put("FrequencyLast", frequencyLast.getStack()
			.saveOptional(registries));
		nbt.putLong("LastKnownPosition", blockEntity.getBlockPos()
			.asLong());
	}

	@Override
	public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
		long positionInTag = blockEntity.getBlockPos()
			.asLong();
		long positionKey = nbt.getLong("LastKnownPosition");
		newPosition = positionInTag != positionKey;

		super.read(nbt, registries, clientPacket);
		frequencyFirst = Frequency.of(ItemStack.parseOptional(registries, nbt.getCompound("FrequencyFirst")));
		frequencyLast = Frequency.of(ItemStack.parseOptional(registries, nbt.getCompound("FrequencyLast")));
	}

	public void setFrequency(boolean first, ItemStack stack) {
		stack = stack.copy();
		stack.setCount(1);
		ItemStack toCompare = first ? frequencyFirst.getStack() : frequencyLast.getStack();
		boolean changed = !ItemStack.isSameItemSameComponents(stack, toCompare);

		if (changed)
			getHandler().removeFromNetwork(getWorld(), this);

		if (first)
			frequencyFirst = Frequency.of(stack);
		else
			frequencyLast = Frequency.of(stack);

		if (!changed)
			return;

		blockEntity.sendData();
		getHandler().addToNetwork(getWorld(), this);
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

	private EnderPackagersNetworkHandler getHandler() {
		return CreateQOL.ENDER_PACKAGER_NETWORK_HANDLER;
	}

	public static class SlotPositioning {
		Function<BlockState, Pair<Vec3, Vec3>> offsets;
		Function<BlockState, Vec3> rotation;
		float scale;

		public SlotPositioning(Function<BlockState, Pair<Vec3, Vec3>> offsetsForState,
			Function<BlockState, Vec3> rotationForState) {
			offsets = offsetsForState;
			rotation = rotationForState;
			scale = 1;
		}

		public SlotPositioning scale(float scale) {
			this.scale = scale;
			return this;
		}

	}

	public boolean testHit(Boolean first, Vec3 hit) {
		BlockState state = this.blockEntity.getBlockState();
		Vec3 localHit = hit.subtract(Vec3.atLowerCornerOf(this.blockEntity.getBlockPos()));
		return (first ? this.firstSlot : this.secondSlot).testHit(this.getWorld(), this.getPos(), state, localHit);
	}

	@Override
	public boolean isAlive() {
		Level level = getWorld();
		BlockPos pos = getPos();
		if (blockEntity.isChunkUnloaded())
			return false;
		if (blockEntity.isRemoved())
			return false;
		if (!level.isLoaded(pos))
			return false;
		return level.getBlockEntity(pos) == blockEntity;
	}

	@Override
	public BlockPos getLocation() {
		return getPos();
	}

	@Override
	public boolean canAcceptPackage(ItemStack pItem) {
		if (blockEntity instanceof EnderPackagerBlockEntity be && !be.doesMatchAddress(pItem)) return false;
		EnderPackagerItemHandler handler = handlerSupplier.get();
		return handler.insertItem(pItem,true,true).isEmpty();
	}

	@Override
	public String getClipboardKey() {
		return "Frequencies";
	}

	@Override
	public boolean writeToClipboard(@NotNull HolderLookup.Provider registries, CompoundTag tag, Direction side) {
		tag.put("First", frequencyFirst.getStack()
			.saveOptional(registries));
		tag.put("Last", frequencyLast.getStack()
			.saveOptional(registries));
		return true;
	}

	@Override
	public boolean readFromClipboard(@NotNull HolderLookup.Provider registries, CompoundTag tag, Player player, Direction side, boolean simulate) {
		if (!tag.contains("First") || !tag.contains("Last"))
			return false;
		if (simulate)
			return true;
		setFrequency(true, ItemStack.parseOptional(registries, tag.getCompound("First")));
		setFrequency(false, ItemStack.parseOptional(registries, tag.getCompound("Last")));
		return true;
	}

}
