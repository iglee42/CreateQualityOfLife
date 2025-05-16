package fr.iglee42.createqualityoflife.blocks;

import java.util.HashMap;
import java.util.Map;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.content.logistics.chute.ChuteShapes;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;

import fr.iglee42.createqualityoflife.blockentitites.TrashCanBlockEntity;
import fr.iglee42.createqualityoflife.registries.ModBlockEntities;
import fr.iglee42.createqualityoflife.registries.ModShapes;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrashCanBlock extends Block implements ProperWaterloggedBlock, IWrenchable,IBE<TrashCanBlockEntity> {


	public TrashCanBlock(Properties p_i48440_1_) {
		super(p_i48440_1_);
		registerDefaultState(defaultBlockState().setValue(WATERLOGGED,false));
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return fluidState(pState);
	}

	@Override
	protected VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
		return ModShapes.TRASH_CAN;
	}

	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		IBE.onRemove(state, world, pos, newState);
	}


	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return withWater(super.getStateForPlacement(ctx), ctx);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState above, LevelAccessor world,
		BlockPos pos, BlockPos p_196271_6_) {
		updateWater(world, state, pos);
		return super.updateShape(state, direction, above, world, pos, p_196271_6_);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> p_206840_1_) {
		super.createBlockStateDefinition(p_206840_1_.add(WATERLOGGED));
	}



	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
		return false;
	}

	@Override
	public Class<TrashCanBlockEntity> getBlockEntityClass() {
		return TrashCanBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends TrashCanBlockEntity> getBlockEntityType() {
		return ModBlockEntities.TRASH_CAN.get();
	}

}
