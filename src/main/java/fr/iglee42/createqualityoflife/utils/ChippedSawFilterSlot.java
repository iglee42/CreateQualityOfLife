package fr.iglee42.createqualityoflife.utils;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.VecHelper;
import fr.iglee42.createqualityoflife.blockentitites.ChippedSawBlockEntity;
import fr.iglee42.createqualityoflife.blocks.ChippedSawBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ChippedSawFilterSlot extends ValueBoxTransform {

	@Override
	public Vec3 getLocalOffset(BlockState state) {
		int offset = state.getValue(ChippedSawBlock.HORIZONTAL_FACING) == Direction.NORTH ||state.getValue(ChippedSawBlock.HORIZONTAL_FACING) == Direction.WEST ? -3 : 3;
		Vec3 x = VecHelper.voxelSpace(8, 12.6f, 8 + offset);
		Vec3 z = VecHelper.voxelSpace(8 + offset, 12.6f, 8);
		return state.getValue(ChippedSawBlock.HORIZONTAL_FACING) == Direction.NORTH ||state.getValue(ChippedSawBlock.HORIZONTAL_FACING) == Direction.SOUTH ? x : z;
	}

	@Override
	public void rotate(BlockState state, PoseStack ms) {
		int yRot = (state.getValue(ChippedSawBlock.HORIZONTAL_FACING) == Direction.NORTH ||state.getValue(ChippedSawBlock.HORIZONTAL_FACING) == Direction.SOUTH ? 0 : 90)
			+ (state.getValue(ChippedSawBlock.HORIZONTAL_FACING) == Direction.NORTH ||state.getValue(ChippedSawBlock.HORIZONTAL_FACING) == Direction.WEST ? 0 : 180);
		TransformStack.cast(ms)
			.rotateY(yRot)
			.rotateX(90);
	}

}
