package fr.iglee42.createqualityoflife.blocks;

import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import fr.iglee42.createqualityoflife.blockentitites.FunneledBeltBlockEntity;
import fr.iglee42.createqualityoflife.registries.ModBlockEntities;
import fr.iglee42.createqualityoflife.registries.ModShapes;
import fr.iglee42.createqualityoflife.blockentitites.FunneledBeltBlockEntity.CasingType;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FunneledBeltBlock extends HorizontalAxisKineticBlock
	implements IBE<FunneledBeltBlockEntity>, ProperWaterloggedBlock {

	public static final BooleanProperty CASING = BooleanProperty.create("casing");

	public FunneledBeltBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
			.setValue(CASING, false)
			.setValue(WATERLOGGED, false));
	}


	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() != getRotationAxis(state);
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(HORIZONTAL_AXIS) == Axis.X ? Axis.Z : Axis.X;
	}


	@Override
	public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return false;
	}




	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn,
		BlockHitResult hit) {
		if (player.isShiftKeyDown() || !player.mayBuild())
			return InteractionResult.PASS;
		ItemStack heldItem = player.getItemInHand(handIn);

		if (AllBlocks.BRASS_CASING.isIn(heldItem)) {
			withBlockEntityDo(world, pos, be -> be.setCasingType(CasingType.BRASS));
			return InteractionResult.SUCCESS;
		}

		if (AllBlocks.ANDESITE_CASING.isIn(heldItem)) {
			withBlockEntityDo(world, pos, be -> be.setCasingType(CasingType.ANDESITE));
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		Level world = context.getLevel();
		Player player = context.getPlayer();
		BlockPos pos = context.getClickedPos();

		if (state.getValue(CASING)) {
			if (world.isClientSide)
				return InteractionResult.SUCCESS;
			withBlockEntityDo(world, pos, be -> be.setCasingType(CasingType.NONE));
			return InteractionResult.SUCCESS;
		}


		return InteractionResult.PASS;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(CASING, WATERLOGGED);
		super.createBlockStateDefinition(builder);
	}

	@Override
	public BlockPathTypes getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
		return BlockPathTypes.RAIL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return ModShapes.FUNNELED_BELT.get(state.getValue(HORIZONTAL_AXIS) == Axis.X ? Axis.Z : Axis.X);
	}


	@Override
	public RenderShape getRenderShape(BlockState state) {
		//return state.getValue(CASING) ? RenderShape.MODEL : RenderShape.ENTITYBLOCK_ANIMATED;
		return RenderShape.MODEL;
	}


	@Override
	public BlockState updateShape(BlockState state, Direction side, BlockState p_196271_3_, LevelAccessor world,
		BlockPos pos, BlockPos p_196271_6_) {
		updateWater(world, state, pos);
		return state;
	}



	@Override
	public Class<FunneledBeltBlockEntity> getBlockEntityClass() {
		return FunneledBeltBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends FunneledBeltBlockEntity> getBlockEntityType() {
		return ModBlockEntities.FUNNELED_BELT.get();
	}



	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}
	
	@Override
	public FluidState getFluidState(BlockState pState) {
		return fluidState(pState);
	}



}
