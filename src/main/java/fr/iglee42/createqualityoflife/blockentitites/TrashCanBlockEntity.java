package fr.iglee42.createqualityoflife.blockentitites;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.ParametersAreNonnullByDefault;

import fr.iglee42.createqualityoflife.registries.QOLBlockEntities;
import fr.iglee42.createqualityoflife.utils.TrashItemHandler;
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
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TrashCanBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation { // , IAirCurrentSource {

	TrashItemHandler itemHandler;

	private final EnumMap<Direction, BlockCapabilityCache<IItemHandler, @Nullable Direction>> capCaches = new EnumMap<>(Direction.class);

	public TrashCanBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		itemHandler = new TrashItemHandler(this);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(
				Capabilities.ItemHandler.BLOCK,
				QOLBlockEntities.TRASH_CAN.get(),
				(be, context) -> be.itemHandler
		);
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
		handleInput(grabCapability(Direction.UP));
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

	protected @Nullable IItemHandler grabCapability(@NotNull Direction side) {
		BlockPos pos = this.worldPosition.relative(side);
		if (level == null)
			return null;
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof TrashCanBlockEntity) {
			if (side != Direction.DOWN)
				return null;
		}
		if (capCaches.get(side) == null) {
			if (level instanceof ServerLevel serverLevel) {
				BlockCapabilityCache<IItemHandler, @Nullable Direction> cache = BlockCapabilityCache.create(
						Capabilities.ItemHandler.BLOCK,
						serverLevel,
						pos,
						side.getOpposite()
				);
				capCaches.put(side, cache);
				return cache.getCapability();
			} else {
				return level.getCapability(Capabilities.ItemHandler.BLOCK, pos, side.getOpposite());
			}
		} else {
			return capCaches.get(side).getCapability();
		}
	}

	@Override
	public void invalidate() {
		if (itemHandler != null)
			invalidateCapabilities();
		capCaches.clear();
		super.invalidate();
	}

	@Override
	public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(compound, registries, clientPacket);
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
	}


	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		return true;
	}


}
