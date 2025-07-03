package fr.iglee42.createqualityoflife.blockentitites;

import java.util.List;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import fr.iglee42.createqualityoflife.behaviours.EnderPackagerBehaviour;
import fr.iglee42.createqualityoflife.blocks.EnderPackagerBlock;
import fr.iglee42.createqualityoflife.registries.QOLBlockEntities;
import fr.iglee42.createqualityoflife.utils.EnderPackagerFrequencySlot;
import fr.iglee42.createqualityoflife.utils.EnderPackagerItemHandler;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.HolderLookup;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.apache.commons.lang3.tuple.Pair;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class EnderPackagerBlockEntity extends SmartBlockEntity {

	private boolean receivedSignalChanged;
	private int receivedSignal;
	private int transmittedSignal;
	private EnderPackagerBehaviour link;
	private boolean transmitter;

	public ItemStack heldBox;
	public final EnderPackagerItemHandler inventory;

	public static final int CYCLE = 20;
	public int animationTicks;
	public boolean animationInward;
	public String addressFilter;

	public EnderPackagerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		addressFilter = "";
		heldBox = ItemStack.EMPTY;
		inventory = new EnderPackagerItemHandler(this);
		animationTicks = 0;
		animationInward = true;
	}
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(
				Capabilities.ItemHandler.BLOCK,
				QOLBlockEntities.ENDER_PACKAGER.get(),
				(be, context) -> be.inventory
		);
	}
	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

	}


	@Override
	public void addBehavioursDeferred(List<BlockEntityBehaviour> behaviours) {
		createLink();
		behaviours.add(link);
	}

	protected void createLink() {
		Pair<ValueBoxTransform, ValueBoxTransform> slots =
			ValueBoxTransform.Dual.makeSlots(EnderPackagerFrequencySlot::new);
		link = transmitter ? EnderPackagerBehaviour.transmitter(this, slots)
			: EnderPackagerBehaviour.receiver(this, slots, ()->inventory);
	}

	@Override
	public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		compound.putBoolean("Transmitter", transmitter);
		compound.putInt("Receive", getReceivedSignal());
		compound.putBoolean("ReceivedChanged", receivedSignalChanged);
		compound.putInt("Transmit", transmittedSignal);
		super.write(compound, registries, clientPacket);
		compound.putBoolean("AnimationInward", animationInward);
		compound.putInt("AnimationTicks", animationTicks);
		compound.put("HeldBox", heldBox.saveOptional(registries));
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		transmitter = compound.getBoolean("Transmitter");
		super.read(compound, registries, clientPacket);

		animationInward = compound.getBoolean("AnimationInward");
		animationTicks = compound.getInt("AnimationTicks");
		heldBox = ItemStack.parseOptional(registries, compound.getCompound("HeldBox"));
		receivedSignal = compound.getInt("Receive");
		receivedSignalChanged = compound.getBoolean("ReceivedChanged");
		if (level == null || level.isClientSide || !link.newPosition)
			transmittedSignal = compound.getInt("Transmit");
	}

	@Override
	public void tick() {
		super.tick();

		if (isTransmitterBlock() != transmitter) {
			transmitter = isTransmitterBlock();
			EnderPackagerBehaviour prevlink = link;
			removeBehaviour(EnderPackagerBehaviour.TYPE);
			createLink();
			link.copyItemsFrom(prevlink);
			attachBehaviourLate(link);
		}

		if (animationTicks == 0) return;

		if (level.isClientSide) {
			if (animationTicks == CYCLE - (animationInward ? 5 : 1))
				AllSoundEvents.PACKAGER.playAt(level, worldPosition, 1, 1, true);
			if (animationTicks == (animationInward ? 1 : 5))
				level.playLocalSound(worldPosition, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 0.25f, 0.75f,
						true);
		}

		animationTicks--;

		if (animationTicks == 0 && !level.isClientSide()) {
			if (!isTransmitter())
				wakeTheFrogs();
			else sendPackage();
			setChanged();
		}

	}

	private void sendPackage() {
		if (heldBox.isEmpty()) return;
		getBehaviour(EnderPackagerBehaviour.TYPE).sendPackage(heldBox);
		inventory.extractItem(0,64,false);
	}

	protected void wakeTheFrogs() {
		if (level.getBlockEntity(worldPosition.relative(Direction.UP)) instanceof FrogportBlockEntity port)
			port.tryPullingFromOwnAndAdjacentInventories();
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		if (level.isClientSide())
			return;
		if (!transmitter){
			updateSignAddress();
			return;
		}
		attemptToSend();
	}

	public void attemptToSend() {
		if (heldBox.isEmpty() || animationTicks != 0 )
			return;

		if (!getBehaviour(EnderPackagerBehaviour.TYPE).canSendPackage(heldBox))
			return;
		animationInward = true;
		animationTicks = CYCLE;

		notifyUpdate();
	}

	@Override
	public void remove() {
		super.remove();

	}

	public float getTrayOffset(float partialTicks) {
		float tickCycle = animationInward ? animationTicks - partialTicks : animationTicks - 5 - partialTicks;
		float progress = Mth.clamp(tickCycle / (CYCLE - 5) * 2 - 1, -1, 1);
		progress = 1 - progress * progress;
		return progress * progress;
	}

	public ItemStack getRenderedBox() {
		if (animationTicks == 0) return heldBox;
		if (animationInward)
			return animationTicks <= CYCLE / 2 ? ItemStack.EMPTY : heldBox;
		return animationTicks >= CYCLE / 2 ? ItemStack.EMPTY : heldBox;
	}

	protected Boolean isTransmitterBlock() {
		return !getBlockState().getValue(EnderPackagerBlock.POWERED);
	}

	public boolean isTransmitter() {
		return transmitter;
	}

	public int getReceivedSignal() {
		return receivedSignal;
	}

	public boolean doesMatchAddress(ItemStack stack){
		return PackageItem.matchAddress(stack, addressFilter);
	}

	public void receivedBox() {
		if (heldBox.isEmpty() || animationTicks != 0 )
			return;
		animationInward = false;
		animationTicks = CYCLE;

		notifyUpdate();
	}

	protected void updateSignAddress() {
		addressFilter = "";
		for (Direction side : Iterate.directions) {
			String address = getSign(side);
			if (address == null || address.isBlank())
				continue;
			addressFilter = address;
		}
	}

	protected String getSign(Direction side) {
		BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(side));
		if (!(blockEntity instanceof SignBlockEntity sign))
			return null;
		for (boolean front : Iterate.trueAndFalse) {
			SignText text = sign.getText(front);
			String address = "";
			for (Component component : text.getMessages(false)) {
				String string = component.getString();
				if (!string.isBlank())
					address += string.trim() + " ";
			}
			if (!address.isBlank())
				return address.trim();
		}
		return null;
	}
}
