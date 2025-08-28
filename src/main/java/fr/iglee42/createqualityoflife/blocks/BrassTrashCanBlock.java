package fr.iglee42.createqualityoflife.blocks;

import com.simibubi.create.foundation.block.render.ReducedDestroyEffects;
import fr.iglee42.createqualityoflife.blockentitites.TrashCanBlockEntity;
import fr.iglee42.createqualityoflife.registries.QOLBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

import java.util.function.Consumer;

public class BrassTrashCanBlock extends TrashCanBlock {

	public BrassTrashCanBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(OPEN, false).setValue(POWERED,false));
	}

	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		state = state.setValue(OPEN,!state.getValue(OPEN));
		return super.onWrenched(state, context);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
								boolean isMoving) {
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);
		if (level.isClientSide)
			return;
		if (!level.getBlockTicks()
				.willTickThisTick(pos, this))
			level.scheduleTick(pos, this, 1);
	}


	@Override
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource r) {
		boolean previouslyPowered = state.getValue(POWERED);
		if (previouslyPowered != worldIn.hasNeighborSignal(pos))
			worldIn.setBlock(pos, state.cycle(POWERED), 2);
	}


	@Override
	public BlockState getStateForPlacement(BlockPlaceContext p_196258_1_) {
		return super.getStateForPlacement(p_196258_1_).setValue(OPEN, false).setValue(POWERED,p_196258_1_.getLevel()
				.hasNeighborSignal(p_196258_1_.getClickedPos()));
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return true;
	}

	@Override
	public BlockEntityType<? extends TrashCanBlockEntity> getBlockEntityType() {
		return QOLBlockEntities.BRASS_TRASH_CAN.get();
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> p_206840_1_) {
		super.createBlockStateDefinition(p_206840_1_.add(OPEN,POWERED));
	}

	@OnlyIn(Dist.CLIENT)
	public void initializeClient(Consumer<IClientBlockExtensions> consumer) {
		consumer.accept(new ReducedDestroyEffects());
	}


}
