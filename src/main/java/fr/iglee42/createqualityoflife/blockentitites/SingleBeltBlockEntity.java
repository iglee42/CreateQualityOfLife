package fr.iglee42.createqualityoflife.blockentitites;

import com.jozufozu.flywheel.light.LightListener;
import com.jozufozu.flywheel.light.LightUpdater;
import com.jozufozu.flywheel.util.box.GridAlignedBB;
import com.jozufozu.flywheel.util.box.ImmutableBox;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.content.kinetics.belt.transport.BeltMovementHandler;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import fr.iglee42.createqualityoflife.blocks.SingleBeltBlock;
import fr.iglee42.createqualityoflife.registries.ModBlocks;
import fr.iglee42.createqualityoflife.utils.ItemHandlerSingleBeltSegment;
import fr.iglee42.createqualityoflife.utils.SingleBeltInteractionHandlers;
import fr.iglee42.createqualityoflife.utils.SingleBeltInventory;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.items.IItemHandler;

import java.util.*;
import java.util.function.Function;

import static com.simibubi.create.content.kinetics.belt.BeltSlope.HORIZONTAL;
import static net.minecraft.core.Direction.AxisDirection.NEGATIVE;
import static net.minecraft.core.Direction.AxisDirection.POSITIVE;
import static net.minecraft.world.entity.MoverType.SELF;

public class SingleBeltBlockEntity extends KineticBlockEntity {

	public static final ModelProperty<CasingType> CASING_PROPERTY = new ModelProperty<>();
	public static final ModelProperty<Boolean> COVER_PROPERTY = new ModelProperty<>();


	public Map<Entity, TransportedEntityInfo> passengers;
	public Optional<DyeColor> color;
	public int beltLength;
	public int index;
	public CasingType casing;
	public boolean covered;

	protected SingleBeltInventory inventory;
	protected LazyOptional<IItemHandler> itemHandler;

	public CompoundTag trackerUpdateTag;

	@OnlyIn(Dist.CLIENT)
	public BeltLighter lighter;

	public static enum CasingType {
		NONE, ANDESITE, BRASS;
	}

	public SingleBeltBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		itemHandler = LazyOptional.empty();
		casing = CasingType.NONE;
		color = Optional.empty();
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		behaviours.add(new DirectBeltInputBehaviour(this).onlyInsertWhen(this::canInsertFrom)
			.setInsertionHandler(this::tryInsertingFromSide).allowingBeltFunnels());
		behaviours.add(new TransportedItemStackHandlerBehaviour(this, this::applyToAllItems)
			.withStackPlacement(this::getWorldPositionOf));
	}

	@Override
	public void tick() {
		// Init belt
		if (beltLength == 0)
			SingleBeltBlock.initBelt(level, worldPosition);

		super.tick();

		if (!ModBlocks.SINGLE_BELT.has(level.getBlockState(worldPosition))) return;

		initializeItemHandler();

		// Move Items
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			if (beltLength > 0 && lighter == null) {
				lighter = new BeltLighter();
			}
		});
		invalidateRenderBoundingBox();

		getInventory().tick();

		if (getSpeed() == 0)
			return;

		// Move Entities
		if (passengers == null)
			passengers = new HashMap<>();

		List<Entity> toRemove = new ArrayList<>();
		passengers.forEach((entity, info) -> {
			boolean canBeTransported = BeltMovementHandler.canBeTransported(entity);
			boolean leftTheBelt =
				info.getTicksSinceLastCollision() >  1;
			if (!canBeTransported || leftTheBelt) {
				toRemove.add(entity);
				return;
			}

			info.tick();
			transportEntity(this, entity, info);
		});
		toRemove.forEach(passengers::remove);
	}

	@Override
	public float calculateStressApplied() {
		return super.calculateStressApplied();
	}

	@Override
	public AABB createRenderBoundingBox() {
		return super.createRenderBoundingBox();
	}

	protected void initializeItemHandler() {
		if (level.isClientSide || itemHandler.isPresent())
			return;
		if (beltLength == 0)
			return;
		if (!level.isLoaded(getBlockPos()))
			return;
		SingleBeltInventory inventory = getInventory();
		if (inventory == null)
			return;
		IItemHandler handler = new ItemHandlerSingleBeltSegment(inventory, index);
		itemHandler = LazyOptional.of(() -> handler);
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
		getInventory().ejectAll();
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		itemHandler.invalidate();
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		compound.putInt("Length", beltLength);
		compound.putInt("Index", index);
		NBTHelper.writeEnum(compound, "Casing", casing);
		compound.putBoolean("Covered", covered);

		if (color.isPresent())
			NBTHelper.writeEnum(compound, "Dye", color.get());

		compound.put("Inventory", getInventory().write());
		super.write(compound, clientPacket);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		int prevBeltLength = beltLength;
		super.read(compound, clientPacket);

		color = compound.contains("Dye") ? Optional.of(NBTHelper.readEnum(compound, "Dye", DyeColor.class))
			: Optional.empty();

		if (!wasMoved) {
			trackerUpdateTag = compound;
			index = compound.getInt("Index");
			beltLength = compound.getInt("Length");
			if (prevBeltLength != beltLength) {
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
					if (lighter != null) {
						lighter.initializeLight();
					}
				});
			}
		}

		getInventory().read(compound.getCompound("Inventory"));

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
		beltLength = 0;
		index = 0;
		trackerUpdateTag = new CompoundTag();
	}

	public boolean applyColor(DyeColor colorIn) {
		if (colorIn == null) {
			if (!color.isPresent())
				return false;
		} else if (color.isPresent() && color.get() == colorIn)
			return false;
		if (level.isClientSide())
			return true;

		color = Optional.ofNullable(colorIn);
		setChanged();
		sendData();

		return true;
	}



	public float getBeltMovementSpeed() {
		return getSpeed() / 480f;
	}

	public float getDirectionAwareBeltMovementSpeed() {
		int offset = getBeltFacing().getAxisDirection()
			.getStep();
		if (getBeltFacing().getAxis() == Axis.X)
			offset *= -1;
		return getBeltMovementSpeed() * offset;
	}


	public Vec3i getMovementDirection(boolean ignoreHalves) {
		if (getSpeed() == 0)
			return BlockPos.ZERO;

		final BlockState blockState = getBlockState();
		final Direction beltFacing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
		final Axis axis = beltFacing.getAxis();

		Direction movementFacing = Direction.get(axis == Axis.X ? NEGATIVE : POSITIVE, axis);
		if (getSpeed() < 0)
			movementFacing = movementFacing.getOpposite();

        return movementFacing.getNormal();
	}

	public Direction getMovementFacing() {
		Axis axis = getBeltFacing().getAxis();
		return Direction.fromAxisAndDirection(axis, getBeltMovementSpeed() < 0 ^ axis == Axis.X ? NEGATIVE : POSITIVE);
	}

	public Direction getBeltFacing() {
		return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
	}

	public SingleBeltInventory getInventory() {
		if (inventory == null) {
			inventory = new SingleBeltInventory(this);
		}
		return inventory;
	}

	private void applyToAllItems(float maxDistanceFromCenter,
		Function<TransportedItemStack, TransportedResult> processFunction) {
		SingleBeltInventory inventory = getInventory();
		if (inventory != null)
			inventory.applyToEachWithin(index + .5f, maxDistanceFromCenter, processFunction);
	}

	private Vec3 getWorldPositionOf(TransportedItemStack transported) {
		Vec3 vec = VecHelper.getCenterOf(getBlockPos());
		Vec3 horizontalMovement = Vec3.atLowerCornerOf(getBeltFacing()
						.getNormal())
				.scale(transported.beltPosition - .5f);
		vec = vec.add(horizontalMovement);
		return vec;
	}

	public void setCasingType(CasingType type) {
		if (casing == type)
			return;
		
		BlockState blockState = getBlockState();
		boolean shouldBlockHaveCasing = type != CasingType.NONE;

		if (level.isClientSide) {
			casing = type;
			level.setBlock(worldPosition, blockState.setValue(SingleBeltBlock.CASING, shouldBlockHaveCasing), 0);
			requestModelDataUpdate();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 16);
			return;
		}
		
		if (casing != CasingType.NONE)
			level.levelEvent(2001, worldPosition,
				Block.getId(casing == CasingType.ANDESITE ? AllBlocks.ANDESITE_CASING.getDefaultState()
					: AllBlocks.BRASS_CASING.getDefaultState()));
		if (blockState.getValue(SingleBeltBlock.CASING) != shouldBlockHaveCasing)
			KineticBlockEntity.switchToBlockState(level, worldPosition,
				blockState.setValue(SingleBeltBlock.CASING, shouldBlockHaveCasing));
		casing = type;
		setChanged();
		sendData();
	}

	private boolean canInsertFrom(Direction side) {
		if (getSpeed() == 0)
			return false;
		return getMovementFacing() != side.getOpposite();
	}
	
	private boolean isOccupied(Direction side) {
		SingleBeltInventory nextInventory = getInventory();
		if (nextInventory == null)
			return true;
		if (getSpeed() == 0)
			return true;
		if (getMovementFacing() == side.getOpposite())
			return true;
		if (!nextInventory.canInsertAtFromSide(index, side))
			return true;
		return false;
	}

	private ItemStack tryInsertingFromSide(TransportedItemStack transportedStack, Direction side, boolean simulate) {
		ItemStack inserted = transportedStack.stack;
		ItemStack empty = ItemStack.EMPTY;

		SingleBeltInventory nextInventory = getInventory();
		if (nextInventory == null)
			return inserted;

		BlockEntity teAbove = level.getBlockEntity(worldPosition.above());
		if (teAbove instanceof BrassTunnelBlockEntity) {
			BrassTunnelBlockEntity tunnelBE = (BrassTunnelBlockEntity) teAbove;
			if (tunnelBE.hasDistributionBehaviour()) {
				if (!tunnelBE.getStackToDistribute()
					.isEmpty())
					return inserted;
				if (!tunnelBE.testFlapFilter(side.getOpposite(), inserted))
					return inserted;
				if (!simulate) {
					SingleBeltInteractionHandlers.flapTunnel(nextInventory, index, side.getOpposite(), true);
					tunnelBE.setStackToDistribute(inserted, side.getOpposite());
				}
				return empty;
			}
		}

		if (isOccupied(side))
			return inserted;
		if (simulate)
			return empty;

		transportedStack = transportedStack.copy();
		transportedStack.beltPosition = index + .5f - Math.signum(getDirectionAwareBeltMovementSpeed()) / 16f;

		Direction movementFacing = getMovementFacing();
		if (!side.getAxis()
			.isVertical()) {
			if (movementFacing != side) {
				transportedStack.sideOffset = side.getAxisDirection()
					.getStep() * .35f;
				if (side.getAxis() == Axis.X)
					transportedStack.sideOffset *= -1;
			} else
				transportedStack.beltPosition = getDirectionAwareBeltMovementSpeed() > 0 ? index : index + 1;
		}

		transportedStack.prevSideOffset = transportedStack.sideOffset;
		transportedStack.insertedAt = index;
		transportedStack.insertedFrom = side;
		transportedStack.prevBeltPosition = transportedStack.beltPosition;

		SingleBeltInteractionHandlers.flapTunnel(nextInventory, index, side.getOpposite(), true);

		nextInventory.addItem(transportedStack);
		setChanged();
		sendData();
		return empty;
	}

	@Override
	public ModelData getModelData() {
		return ModelData.builder()
			.with(CASING_PROPERTY, casing)
			.with(COVER_PROPERTY, covered)
			.build();
	}


	public void invalidateItemHandler() {
		itemHandler.invalidate();
	}

	public boolean shouldRenderNormally() {
		return true;
	}

	/**
	 * Hide this behavior in an inner class to avoid loading LightListener on servers.
	 */
	@OnlyIn(Dist.CLIENT)
	public class BeltLighter implements LightListener {
		private byte[] light;

		public BeltLighter() {
			initializeLight();
			LightUpdater.get(level)
					.addListener(this);
		}

		/**
		 * Get the number of belt segments represented by the lighter.
		 * @return The number of segments.
		 */
		public int lightSegments() {
			return light == null ? 0 : light.length / 2;
		}

		/**
		 * Get the light value for a given segment.
		 * @param segment The segment to get the light value for.
		 * @return The light value.
		 */
		public int getPackedLight(int segment) {
			return light == null ? 0 : LightTexture.pack(light[segment * 2], light[segment * 2 + 1]);
		}

		@Override
		public GridAlignedBB getVolume() {
			BlockPos pos = getBlockPos();
			Vec3i vec = getBeltFacing()
					.getNormal();


			BlockPos endPos = pos.offset((beltLength - 1) * vec.getX(), 0,
					(beltLength - 1) * vec.getZ());
			GridAlignedBB bb = GridAlignedBB.from(worldPosition, endPos);
			bb.fixMinMax();
			return bb;
		}

		@Override
		public boolean isListenerInvalid() {
			return remove;
		}

		@Override
		public void onLightUpdate(LightLayer type, ImmutableBox changed) {
			if (remove)
				return;
			if (level == null)
				return;

			GridAlignedBB beltVolume = getVolume();

			if (beltVolume.intersects(changed)) {
				if (type == LightLayer.BLOCK)
					updateBlockLight();

				if (type == LightLayer.SKY)
					updateSkyLight();
			}
		}

		private void initializeLight() {
			light = new byte[beltLength * 2];

			Vec3i vec = getBeltFacing().getNormal();

			MutableBlockPos pos = new MutableBlockPos(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
			for (int i = 0; i < beltLength * 2; i += 2) {
				light[i] = (byte) level.getBrightness(LightLayer.BLOCK, pos);
				light[i + 1] = (byte) level.getBrightness(LightLayer.SKY, pos);
				pos.move(vec.getX(), 0, vec.getZ());
			}
		}

		private void updateBlockLight() {
			Vec3i vec = getBeltFacing().getNormal();

			MutableBlockPos pos = new MutableBlockPos(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
			for (int i = 0; i < beltLength * 2; i += 2) {
				light[i] = (byte) level.getBrightness(LightLayer.BLOCK, pos);

				pos.move(vec.getX(), 0, vec.getZ());
			}
		}

		private void updateSkyLight() {
			Vec3i vec = getBeltFacing().getNormal();

			MutableBlockPos pos = new MutableBlockPos(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
			for (int i = 1; i < beltLength * 2; i += 2) {
				light[i] = (byte) level.getBrightness(LightLayer.SKY, pos);

				pos.move(vec.getX(), 0, vec.getZ());
			}
		}
	}

	public void setCovered(boolean blockCoveringBelt) {
		if (blockCoveringBelt == covered)
			return;
		covered = blockCoveringBelt;
		notifyUpdate();
	}

	public static void transportEntity(SingleBeltBlockEntity beltBE, Entity entityIn, TransportedEntityInfo info) {
		BlockPos pos = info.lastCollidedPos;
		Level world = beltBE.getLevel();
		BlockEntity be = world.getBlockEntity(pos);
		BlockEntity blockEntityBelowPassenger = world.getBlockEntity(entityIn.blockPosition());
		BlockState blockState = info.lastCollidedState;

        boolean collidedWithBelt = be instanceof SingleBeltBlockEntity;
		boolean betweenBelts = blockEntityBelowPassenger instanceof SingleBeltBlockEntity && blockEntityBelowPassenger != be;

		// Don't fight other Belts
		if (!collidedWithBelt || betweenBelts) {
			return;
		}

		// Too slow
        if (Math.abs(beltBE.getSpeed()) < 1)
			return;

		// Not on top
		if (entityIn.getY() - .25f < pos.getY())
			return;

		// Lock entities in place
		boolean isPlayer = entityIn instanceof Player;
		if (entityIn instanceof LivingEntity && !isPlayer)
			((LivingEntity) entityIn).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 1, false, false));

		final Direction beltFacing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
		final Axis axis = beltFacing.getAxis();
		float movementSpeed = beltBE.getBeltMovementSpeed();
		final Direction movementDirection = Direction.get(axis == Axis.X ? NEGATIVE : POSITIVE, axis);

		Vec3i centeringDirection = Direction.get(POSITIVE, beltFacing.getClockWise()
						.getAxis())
				.getNormal();
		Vec3 movement = Vec3.atLowerCornerOf(movementDirection.getNormal())
				.scale(movementSpeed);

		double diffCenter =
				axis == Axis.Z ? (pos.getX() + .5f - entityIn.getX()) : (pos.getZ() + .5f - entityIn.getZ());
		if (Math.abs(diffCenter) > 48 / 64f)
			return;

        Vec3 centering = Vec3.atLowerCornerOf(centeringDirection).scale(diffCenter * Math.min(Math.abs(movementSpeed), .1f) * 4);

		if (!(entityIn instanceof LivingEntity)
				|| ((LivingEntity) entityIn).zza == 0 && ((LivingEntity) entityIn).xxa == 0)
			movement = movement.add(centering);

		float step = entityIn.maxUpStep;
		if (!isPlayer)
			entityIn.maxUpStep = 1;

		// Entity Collisions
		if (Math.abs(movementSpeed) < .5f) {
			Vec3 checkDistance = movement.normalize()
					.scale(0.5);
			AABB bb = entityIn.getBoundingBox();
			AABB checkBB = new AABB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
			checkBB = checkBB.move(checkDistance)
					.inflate(-Math.abs(checkDistance.x), -Math.abs(checkDistance.y), -Math.abs(checkDistance.z));
			List<Entity> list = world.getEntities(entityIn, checkBB);
			list.removeIf(e -> BeltMovementHandler.shouldIgnoreBlocking(entityIn, e));
			if (!list.isEmpty()) {
				entityIn.setDeltaMovement(0, 0, 0);
				info.ticksSinceLastCollision--;
				return;
			}
		}

		entityIn.fallDistance = 0;


		entityIn.move(SELF, movement);

		entityIn.setOnGround(true);

		if (!isPlayer)
			entityIn.maxUpStep = step;

    }

	public static class TransportedEntityInfo {
		int ticksSinceLastCollision;
		BlockPos lastCollidedPos;
		BlockState lastCollidedState;

		public TransportedEntityInfo(BlockPos collision, BlockState belt) {
			refresh(collision, belt);
		}

		public void refresh(BlockPos collision, BlockState belt) {
			ticksSinceLastCollision = 0;
			lastCollidedPos = new BlockPos(collision).immutable();
			lastCollidedState = belt;
		}

		public TransportedEntityInfo tick() {
			ticksSinceLastCollision++;
			return this;
		}

		public int getTicksSinceLastCollision() {
			return ticksSinceLastCollision;
		}
	}

	public Vec3 getVectorForOffset(float offset) {
		Vec3 vec = VecHelper.getCenterOf(getBlockPos());
		Vec3 horizontalMovement = Vec3.atLowerCornerOf(getBeltFacing()
						.getNormal())
				.scale(offset - .5f);


		vec = vec.add(horizontalMovement);
		return vec;
	}

	public BlockPos getPositionForOffset(int offset) {
		BlockPos pos = getBlockPos();
		Vec3i vec = getBeltFacing()
				.getNormal();


		return pos.offset(offset * vec.getX(), 0,
				offset * vec.getZ());
	}

}
