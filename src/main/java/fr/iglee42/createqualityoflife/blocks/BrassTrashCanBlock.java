package fr.iglee42.createqualityoflife.blocks;

import com.simibubi.create.AllBlockEntityTypes;

import fr.iglee42.createqualityoflife.blockentitites.TrashCanBlockEntity;
import fr.iglee42.createqualityoflife.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class BrassTrashCanBlock extends TrashCanBlock {

	public BrassTrashCanBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(OPEN, false));
	}

	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		if (context.getClickedFace() == Direction.UP)
			state = state.setValue(OPEN,!state.getValue(OPEN));
		return super.onWrenched(state, context);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext p_196258_1_) {
		return super.getStateForPlacement(p_196258_1_).setValue(OPEN, false);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return true;
	}

	@Override
	public BlockEntityType<? extends TrashCanBlockEntity> getBlockEntityType() {
		return ModBlockEntities.BRASS_TRASH_CAN.get();
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> p_206840_1_) {
		super.createBlockStateDefinition(p_206840_1_.add(OPEN));
	}


}
