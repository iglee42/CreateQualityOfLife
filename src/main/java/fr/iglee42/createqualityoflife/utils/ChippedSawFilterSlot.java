package fr.iglee42.createqualityoflife.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import fr.iglee42.createqualityoflife.blockentitites.ChippedSawBlockEntity;
import fr.iglee42.createqualityoflife.blocks.ChippedSawBlock;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ChippedSawFilterSlot extends ValueBoxTransform {


	@Override
	public Vec3 getLocalOffset(LevelAccessor levelAccessor, BlockPos blockPos, BlockState state) {
		int offset = state.getValue(ChippedSawBlock.HORIZONTAL_FACING) == Direction.NORTH ||state.getValue(ChippedSawBlock.HORIZONTAL_FACING) == Direction.WEST ? -3 : 3;
		Vec3 x = VecHelper.voxelSpace(8, 12.5f, 8 + offset);
		Vec3 z = VecHelper.voxelSpace(8 + offset, 12.5f, 8);
		return state.getValue(ChippedSawBlock.HORIZONTAL_FACING).getAxis().equals(Direction.Axis.Z) ? x : z;
	}

	@Override
	public void rotate(LevelAccessor levelAccessor, BlockPos blockPos, BlockState state, PoseStack poseStack) {
		int yRot = (state.getValue(ChippedSawBlock.HORIZONTAL_FACING) == Direction.NORTH ||state.getValue(ChippedSawBlock.HORIZONTAL_FACING) == Direction.SOUTH ? 0 : 90)
				+ (state.getValue(ChippedSawBlock.HORIZONTAL_FACING) == Direction.NORTH ||state.getValue(ChippedSawBlock.HORIZONTAL_FACING) == Direction.WEST ? 0 : 180);
		TransformStack.of(poseStack)
				.rotateYDegrees(yRot)
				.rotateXDegrees(90);
	}
}
