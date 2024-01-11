package fr.iglee42.createqualityoflife.blocks;

import java.util.List;
import java.util.function.Predicate;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.drill.DrillBlock;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.damageTypes.CreateDamageSources;
import com.simibubi.create.foundation.placement.IPlacementHelper;
import com.simibubi.create.foundation.placement.PlacementHelpers;
import com.simibubi.create.foundation.placement.PlacementOffset;

import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.blockentitites.ChippedSawBlockEntity;
import fr.iglee42.createqualityoflife.registries.ModBlockEntities;
import fr.iglee42.createqualityoflife.utils.Features;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ChippedSawBlock extends HorizontalKineticBlock implements IBE<ChippedSawBlockEntity> {

	public static final BooleanProperty FLIPPED = BooleanProperty.create("flipped");

	private static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());

	public ChippedSawBlock(Properties properties) {
		super(properties);
	}



	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(FLIPPED));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState stateForPlacement = super.getStateForPlacement(context);
		return stateForPlacement.setValue(FLIPPED, context.getHorizontalDirection()
				.getAxisDirection() == AxisDirection.POSITIVE);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		BlockState newState = super.rotate(state, rot);


		if (rot.ordinal() % 2 == 1)
			newState = newState.cycle(FLIPPED);
		if (rot == Rotation.CLOCKWISE_180)
			newState = newState.cycle(FLIPPED);

		return newState;
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		BlockState newState = super.mirror(state, mirrorIn);

		if (mirrorIn == Mirror.FRONT_BACK)
			newState = newState.cycle(FLIPPED);
		if (mirrorIn == Mirror.LEFT_RIGHT)
			newState = newState.cycle(FLIPPED);

		return newState;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return AllShapes.CASING_12PX.get(Direction.UP);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
		BlockHitResult hit) {
		ItemStack heldItem = player.getItemInHand(handIn);
		IPlacementHelper placementHelper = PlacementHelpers.get(placementHelperId);
		if (!player.isShiftKeyDown() && player.mayBuild()) {
			if (placementHelper.matchesItem(heldItem) && placementHelper.getOffset(player, worldIn, state, pos, hit)
				.placeInWorld(worldIn, (BlockItem) heldItem.getItem(), player, handIn, hit)
				.consumesAction())
				return InteractionResult.SUCCESS;
		}

		if (player.isSpectator() || !player.getItemInHand(handIn)
			.isEmpty())
			return InteractionResult.PASS;

		return onBlockEntityUse(worldIn, pos, be -> {
			for (int i = 0; i < be.inventory.getSlots(); i++) {
				ItemStack heldItemStack = be.inventory.getStackInSlot(i);
				if (!worldIn.isClientSide && !heldItemStack.isEmpty())
					player.getInventory()
						.placeItemBackInInventory(heldItemStack);
			}
			be.inventory.clear();
			be.notifyUpdate();
			return InteractionResult.SUCCESS;
		});
	}

	@Override
	public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
		if (entityIn instanceof ItemEntity)
			return;
		if (!new AABB(pos).deflate(.1f)
			.intersects(entityIn.getBoundingBox()))
			return;
		withBlockEntityDo(worldIn, pos, be -> {
			if (be.getSpeed() == 0)
				return;
			entityIn.hurt(CreateDamageSources.saw(worldIn), (float) DrillBlock.getDamage(be.getSpeed()));
		});
	}

	@Override
	public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
		super.updateEntityAfterFallOn(worldIn, entityIn);
		if (!(entityIn instanceof ItemEntity))
			return;
		if (entityIn.level().isClientSide)
			return;

		BlockPos pos = entityIn.blockPosition();
		withBlockEntityDo(entityIn.level(), pos, be -> {
			if (be.getSpeed() == 0)
				return;
			be.insertItem((ItemEntity) entityIn);
		});
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.NORMAL;
	}



	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(HORIZONTAL_FACING)
			.getAxis();
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.getValue(HORIZONTAL_FACING)
			.getOpposite() || face == state.getValue(HORIZONTAL_FACING);
	}

	@Override
	public Class<ChippedSawBlockEntity> getBlockEntityClass() {
		return ChippedSawBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends ChippedSawBlockEntity> getBlockEntityType() {
		return ModBlockEntities.CHIPPED_SAW.get();
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}

	@MethodsReturnNonnullByDefault
	private static class PlacementHelper implements IPlacementHelper {

		@Override
		public Predicate<ItemStack> getItemPredicate() {
			return itemStack -> itemStack.getItem() instanceof BlockItem b && b.getBlock() instanceof ChippedSawBlock;
		}

		@Override
		public Predicate<BlockState> getStatePredicate() {
			return state -> state.getBlock() instanceof ChippedSawBlock;
		}

		@Override
		public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos,
			BlockHitResult ray) {
			List<Direction> directions = IPlacementHelper.orderedByDistanceExceptAxis(pos, ray.getLocation(),
				Axis.Y,
				dir -> world.getBlockState(pos.relative(dir))
					.canBeReplaced());

			if (directions.isEmpty())
				return PlacementOffset.fail();
			else {
				return PlacementOffset.success(pos.relative(directions.get(0)),
					s -> s.setValue(HORIZONTAL_FACING, state.getValue(HORIZONTAL_FACING))
					.setValue(FLIPPED, state.getValue(FLIPPED)));
			}
		}
		
	}

}
