package fr.iglee42.createqualityoflife.blockentitites;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.content.logistics.chute.SmartChuteBlockEntity;
import fr.iglee42.createqualityoflife.utils.TrashItemHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.ItemHelper.ExtractionCountMode;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;


@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TrashCanBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation { // , IAirCurrentSource {

	TrashItemHandler itemHandler;

	LazyOptional<IItemHandler> lazyHandler;

	LazyOptional<IItemHandler> capAbove;

	public TrashCanBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		itemHandler = new TrashItemHandler(this);
		lazyHandler = LazyOptional.of(() -> itemHandler);

		capAbove = LazyOptional.empty();

	}


	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return cap == ForgeCapabilities.ITEM_HANDLER ? lazyHandler.cast() : super.getCapability(cap, side);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(new DirectBeltInputBehaviour(this));
	}

	public boolean canAcceptItem(ItemStack stack) {
		return true;
	}
	protected int getExtractionAmount() {
		return 16;
	}

	protected ExtractionCountMode getExtractionMode() {
		return ExtractionCountMode.UPTO;
	}

	protected boolean canActivate() {
		return true;
	}

	protected void handleInputFromAbove() {
		if (!capAbove.isPresent())
			capAbove = grabCapability(Direction.UP);
		handleInput(capAbove.orElse(null));
	}
	protected void handleInput(@Nullable IItemHandler inv) {
		if (inv == null)
			return;
		if (!canActivate())
			return;
		Predicate<ItemStack> canAccept = this::canAcceptItem;
		int count = getExtractionAmount();
		ExtractionCountMode mode = getExtractionMode();
		if (mode == ExtractionCountMode.UPTO || !ItemHelper.extract(inv, canAccept, mode, count, true)
				.isEmpty()) {
			ItemHelper.extract(inv, canAccept, mode, count, false);
		}
	}
	private LazyOptional<IItemHandler> grabCapability(Direction side) {
		BlockPos pos = this.worldPosition.relative(side);
		if (level == null)
			return LazyOptional.empty();
		BlockEntity be = level.getBlockEntity(pos);
		if (be == null)
			return LazyOptional.empty();
		if (be instanceof ChuteBlockEntity) {
			if (side != Direction.DOWN)
				return LazyOptional.empty();
		}
		return be.getCapability(ForgeCapabilities.ITEM_HANDLER, side.getOpposite());
	}

	@Override
	public void invalidate() {
		if (lazyHandler != null)
			lazyHandler.invalidate();
		super.invalidate();
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
	}


	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		return true;
	}


}
