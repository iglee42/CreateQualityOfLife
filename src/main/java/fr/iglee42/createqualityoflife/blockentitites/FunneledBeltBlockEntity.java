package fr.iglee42.createqualityoflife.blockentitites;

import static net.minecraft.core.Direction.AxisDirection.NEGATIVE;
import static net.minecraft.core.Direction.AxisDirection.POSITIVE;

import java.util.List;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.content.kinetics.belt.transport.BeltTunnelInteractionHandler;
import com.simibubi.create.content.kinetics.belt.transport.ItemHandlerBeltSegment;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.BlockFace;
import com.simibubi.create.foundation.utility.NBTHelper;

import fr.iglee42.createqualityoflife.blocks.FunneledBeltBlock;
import fr.iglee42.createqualityoflife.registries.ModBlocks;
import fr.iglee42.createqualityoflife.utils.FunneledBeltInventory;
import fr.iglee42.createqualityoflife.utils.FunneledBeltItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class FunneledBeltBlockEntity extends KineticBlockEntity {

	public static final ModelProperty<CasingType> CASING_PROPERTY = new ModelProperty<>();
	public static final ModelProperty<Boolean> COVER_PROPERTY = new ModelProperty<>();

	public CasingType casing;
	public boolean covered;

	protected FunneledBeltInventory inventory;
	protected LazyOptional<IItemHandler> itemHandler;

	public CompoundTag trackerUpdateTag;

	private InvManipulationBehaviour insertBehaviour;
	private InvManipulationBehaviour extractBehaviour;



	public static enum CasingType {
		NONE, ANDESITE, BRASS;
	}

	public FunneledBeltBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		itemHandler = LazyOptional.empty();
		casing = CasingType.NONE;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		insertBehaviour = new InvManipulationBehaviour(this,(w,p,s)->new BlockFace(p.offset(0,1,0),s.getValue(FunneledBeltBlock.HORIZONTAL_FACING)));
		behaviours.add(insertBehaviour);
		extractBehaviour = new InvManipulationBehaviour(this,(w,p,s)->new BlockFace(p.offset(0,1,0),s.getValue(FunneledBeltBlock.HORIZONTAL_FACING).getOpposite()));
		behaviours.add(extractBehaviour);
	}

	@Override
	public void tick() {
		super.tick();

		if (!ModBlocks.FUNNELED_BELT.has(level.getBlockState(worldPosition)))
			return;

		if (inventory == null){
			initializeItemHandler();
		}


		invalidateRenderBoundingBox();

		getInventory().tick();

		activateExtracting();
	}

	public float getBeltMovementSpeed() {
		return getSpeed() / 480f;
	}


	private void initializeItemHandler() {
		if (level.isClientSide || itemHandler.isPresent())
			return;
		if (inventory == null)
			return;
		IItemHandler handler = new FunneledBeltItemHandler(inventory);
		itemHandler = LazyOptional.of(() -> handler);
	}


	@Override
	public AABB createRenderBoundingBox() {
		return super.createRenderBoundingBox().inflate(0,1,0);
	}



	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!isItemHandlerCap(cap))
			return super.getCapability(cap, side);
		if (!isRemoved() && !itemHandler.isPresent())
			initializeItemHandler();
		return itemHandler.cast();
	}

	@Override
	public void destroy() {
		super.destroy();
		//getInventory().ejectAll();
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		itemHandler.invalidate();
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {

		NBTHelper.writeEnum(compound, "Casing", casing);
		compound.putBoolean("Covered", covered);

		super.write(compound, clientPacket);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);

		CasingType casingBefore = casing;
		boolean coverBefore = covered;
		casing = NBTHelper.readEnum(compound, "Casing", CasingType.class);
		covered = compound.getBoolean("Covered");

		if (!clientPacket)
			return;

		if (casingBefore == casing && coverBefore == covered)
			return;
		if (!isVirtual())
			requestModelDataUpdate();
		if (hasLevel())
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
	}

	@Override
	public void clearKineticInformation() {
		super.clearKineticInformation();
		trackerUpdateTag = new CompoundTag();
	}


	public FunneledBeltInventory getInventory() {
		if (inventory == null) {
			inventory = new FunneledBeltInventory(this);
		}
		return inventory;
	}



	public void setCasingType(CasingType type) {
		if (casing == type)
			return;
		
		BlockState blockState = getBlockState();
		boolean shouldBlockHaveCasing = type != CasingType.NONE;

		if (level.isClientSide) {
			casing = type;
			level.setBlock(worldPosition, blockState.setValue(FunneledBeltBlock.CASING, shouldBlockHaveCasing), 0);
			requestModelDataUpdate();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 16);
			return;
		}
		
		if (casing != CasingType.NONE)
			level.levelEvent(2001, worldPosition,
				Block.getId(casing == CasingType.ANDESITE ? AllBlocks.ANDESITE_CASING.getDefaultState()
					: AllBlocks.BRASS_CASING.getDefaultState()));
		if (blockState.getValue(FunneledBeltBlock.CASING) != shouldBlockHaveCasing)
			KineticBlockEntity.switchToBlockState(level, worldPosition,
				blockState.setValue(FunneledBeltBlock.CASING, shouldBlockHaveCasing));
		casing = type;
		setChanged();
		sendData();
	}

	@Override
	public ModelData getModelData() {
		return ModelData.builder()
			.with(CASING_PROPERTY, casing)
			.with(COVER_PROPERTY, covered)
			.build();
	}

	public void setCovered(boolean blockCoveringBelt) {
		if (blockCoveringBelt == covered)
			return;
		covered = blockCoveringBelt;
		notifyUpdate();
	}

	public Direction getBeltFacing(){
		return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
	}

	public float getDirectionAwareBeltMovementSpeed() {
		int offset = getBeltFacing().getAxisDirection()
				.getStep();
		if (getBeltFacing().getAxis() == Axis.X)
			offset *= -1;
		return getBeltMovementSpeed() * offset;
	}

	public Direction getMovementFacing() {
		Axis axis = getBeltFacing().getAxis();
		return Direction.fromAxisAndDirection(axis, getBeltMovementSpeed() < 0 ^ axis == Axis.X ? NEGATIVE : POSITIVE);
	}

	public InvManipulationBehaviour getInsertBehaviour() {
		return insertBehaviour;
	}

	private void activateExtracting() {

		BlockState blockState = getBlockState();
		Direction facing = blockState.getValue(BeltFunnelBlock.HORIZONTAL_FACING);

		int amountToExtract = 64;
		ItemHelper.ExtractionCountMode mode = ItemHelper.ExtractionCountMode.UPTO;
		MutableBoolean deniedByInsertion = new MutableBoolean(false);
		ItemStack stack = extractBehaviour.extract(mode, amountToExtract, s -> {
			ItemStack handleInsertion = tryInsert(new TransportedItemStack(s),facing,true);
			if (handleInsertion.isEmpty())
				return true;
			deniedByInsertion.setTrue();
			return false;
		});
		if (deniedByInsertion.booleanValue()) return;
		inventory.addItem(new TransportedItemStack(stack));
		//startCooldown();
	}

	private ItemStack tryInsert(TransportedItemStack transportedStack, Direction side, boolean simulate) {
		ItemStack inserted = transportedStack.stack;
		ItemStack empty = ItemStack.EMPTY;

		if (inventory == null)
			return inserted;
		if (!inventory.canInsert())
			return inserted;
		if (simulate)
			return empty;

		transportedStack = transportedStack.copy();
		transportedStack.beltPosition = .5f - Math.signum(getDirectionAwareBeltMovementSpeed()) / 16f;

		Direction movementFacing = getMovementFacing();
		if (!side.getAxis()
				.isVertical()) {
			if (movementFacing != side) {
				transportedStack.sideOffset = side.getAxisDirection()
						.getStep() * .35f;
				if (side.getAxis() == Axis.X)
					transportedStack.sideOffset *= -1;
			} else
				transportedStack.beltPosition = getDirectionAwareBeltMovementSpeed() > 0 ? 0 : 1;
		}

		transportedStack.prevSideOffset = transportedStack.sideOffset;
		transportedStack.insertedAt = 0;
		transportedStack.insertedFrom = side;
		transportedStack.prevBeltPosition = transportedStack.beltPosition;


		inventory.addItem(transportedStack);
		return empty;
	}
}
