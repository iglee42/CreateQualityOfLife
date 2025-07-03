package fr.iglee42.createqualityoflife.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import fr.iglee42.createqualityoflife.blocks.EnderPackagerBlock;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EnderPackagerFrequencySlot extends ValueBoxTransform.Dual {
	public EnderPackagerFrequencySlot(boolean first) {
		super(first);
	}

	Vec3 horizontal = VecHelper.voxelSpace(10f, 5.5f, 2.5f);
	Vec3 vertical = VecHelper.voxelSpace(10f, 2.5f, 5.5f);

	@Override
	public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
		Direction facing = state.getValue(EnderPackagerBlock.FACING);
		Vec3 location = VecHelper.voxelSpace(8f, 16.51f, 5f);

		if (facing.getAxis()
				.isHorizontal()) {
			location = VecHelper.voxelSpace(8f, 6f, 16.51f);
			if (isFirst())
				location = location.add(0, 6 / 16f, 0);
			return rotateHorizontally(state, location);
		}

		if (isFirst())
			location = location.add(0, 0, 6 / 16f);
		location = VecHelper.rotateCentered(location, facing == Direction.DOWN ? 180 : 0, Axis.X);
		return location;
	}

	@Override
	public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
		Direction facing = state.getValue(EnderPackagerBlock.FACING);
		float yRot = facing.getAxis()
				.isVertical() ? 0 : AngleHelper.horizontalAngle(facing) + 180;
		float xRot = facing == Direction.UP ? 90 : facing == Direction.DOWN ? 270 : 0;
		TransformStack.of(ms)
				.rotateYDegrees(yRot)
				.rotateXDegrees(xRot);
	}

	@Override
	public float getScale() {
		return .4975f;
	}
}
