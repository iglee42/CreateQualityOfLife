package fr.iglee42.createqualityoflife.blockentitites.renderers;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.belt.*;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.ShadowRenderHelper;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.worldWrappers.WrappedWorld;
import fr.iglee42.createqualityoflife.blockentitites.FunneledBeltBlockEntity;
import fr.iglee42.createqualityoflife.blocks.FunneledBeltBlock;
import fr.iglee42.createqualityoflife.registries.ModBlocks;
import fr.iglee42.createqualityoflife.registries.ModPartialModels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.function.Supplier;

public class FunneledBeltRenderer extends SafeBlockEntityRenderer<FunneledBeltBlockEntity> {

	public FunneledBeltRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	protected void renderSafe(FunneledBeltBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
		int light, int overlay) {

		if (!Backend.canUseInstancing(be.getLevel())) {

			BlockState blockState = be.getBlockState();
			if (!ModBlocks.FUNNELED_BELT.has(blockState)) return;

			Direction facing = blockState.getValue(FunneledBeltBlock.HORIZONTAL_FACING);
			AxisDirection axisDirection = facing.getAxisDirection();

			boolean alongX = facing.getAxis() == Direction.Axis.X;

			PoseStack localTransforms = new PoseStack();
            TransformStack msr = TransformStack.cast(localTransforms);
			VertexConsumer vb = buffer.getBuffer(RenderType.solid());
			float renderTick = AnimationTickHolder.getRenderTime(be.getLevel());

			msr.centre()
					.rotateY(AngleHelper.horizontalAngle(facing))
					.rotateZ(0)
					.rotateX(0)
					.unCentre();

			for (boolean bottom : Iterate.trueAndFalse) {

				PartialModel beltPartial = bottom ? ModPartialModels.FUNNELED_BELT_BOTTOM : ModPartialModels.FUNNELED_BELT_TOP;

				SuperByteBuffer beltBuffer = CachedBufferer.partial(beltPartial, blockState)
						.light(light);

				SpriteShiftEntry spriteShift = bottom ? AllSpriteShifts.BELT_OFFSET : AllSpriteShifts.BELT;

				// UV shift
				float speed = be.getSpeed();
				if (speed != 0) {
					float time = renderTick * axisDirection.getStep();
					if (alongX || axisDirection == AxisDirection.NEGATIVE)
						speed = -speed;

					float scrollMult = 0.5f;

					float spriteSize = spriteShift.getTarget().getV1() - spriteShift.getTarget().getV0();

					double scroll = speed * time / (31.5 * 16) + (bottom ? 0.5 : 0.0);
					scroll = scroll - Math.floor(scroll);
					scroll = scroll * spriteSize * scrollMult;

					beltBuffer.shiftUVScrolling(spriteShift, (float) scroll);
				}

				beltBuffer
						.transform(localTransforms)
						.renderInto(ms, vb);

			}

				Direction dir = blockState.getValue(FunneledBeltBlock.HORIZONTAL_FACING).getClockWise();

				Supplier<PoseStack> matrixStackSupplier = () -> {
					PoseStack stack = new PoseStack();
                    TransformStack stacker = TransformStack.cast(stack);
					stacker.centre();
					if (dir.getAxis() == Direction.Axis.X) stacker.rotateY(90);
					if (dir.getAxis() == Direction.Axis.Y) stacker.rotateX(90);
					stacker.rotateX(90);
					stacker.unCentre();
					return stack;
				};

				SuperByteBuffer superBuffer = CachedBufferer.partialDirectional(AllPartialModels.BELT_PULLEY, blockState, dir, matrixStackSupplier);
				KineticBlockEntityRenderer.standardKineticRotationTransform(superBuffer, be, light).renderInto(ms, vb);
		}

	}




}
