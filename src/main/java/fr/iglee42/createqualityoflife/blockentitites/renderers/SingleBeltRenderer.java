package fr.iglee42.createqualityoflife.blockentitites.renderers;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
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
import fr.iglee42.createqualityoflife.blockentitites.SingleBeltBlockEntity;
import fr.iglee42.createqualityoflife.blocks.SingleBeltBlock;
import fr.iglee42.createqualityoflife.registries.ModBlocks;
import fr.iglee42.createqualityoflife.registries.ModPartialModels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.function.Supplier;

public class SingleBeltRenderer extends SafeBlockEntityRenderer<SingleBeltBlockEntity> {

	public SingleBeltRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public boolean shouldRenderOffScreen(SingleBeltBlockEntity be) {
		return true;
	}

	@Override
	protected void renderSafe(SingleBeltBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
		int light, int overlay) {

		if (!Backend.canUseInstancing(be.getLevel())) {

			BlockState blockState = be.getBlockState();
			if (!ModBlocks.SINGLE_BELT.has(blockState)) return;

			Direction facing = blockState.getValue(SingleBeltBlock.HORIZONTAL_FACING);
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

			DyeColor color = be.color.orElse(null);

			for (boolean bottom : Iterate.trueAndFalse) {

				PartialModel beltPartial = getBeltPartial(bottom);

				SuperByteBuffer beltBuffer = CachedBufferer.partial(beltPartial, blockState)
						.light(light);

				SpriteShiftEntry spriteShift = getSpriteShiftEntry(color, bottom);

				// UV shift
				float speed = be.getSpeed();
				if (speed != 0 || be.color.isPresent()) {
					float time = renderTick * axisDirection.getStep();
					if (alongX && axisDirection == AxisDirection.NEGATIVE)
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

				Direction dir = blockState.getValue(SingleBeltBlock.HORIZONTAL_FACING).getClockWise();

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

		renderItems(be, partialTicks, ms, buffer, light, overlay);
	}

	public static SpriteShiftEntry getSpriteShiftEntry(DyeColor color, boolean bottom) {
		if (color != null) {
			return (bottom ? AllSpriteShifts.DYED_OFFSET_BELTS : AllSpriteShifts.DYED_BELTS).get(color);
		} else
			return bottom ? AllSpriteShifts.BELT_OFFSET : AllSpriteShifts.BELT;
	}

	public static PartialModel getBeltPartial(boolean bottom) {
		return bottom ? ModPartialModels.SINGLE_BELT_BOTTOM : ModPartialModels.SINGLE_BELT_TOP;
	}

	protected void renderItems(SingleBeltBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
		int light, int overlay) {
		if (be.beltLength == 0)
			return;

		ms.pushPose();

		Direction beltFacing = be.getBeltFacing();
		Vec3i directionVec = beltFacing
							   .getNormal();
		Vec3 beltStartOffset = Vec3.atLowerCornerOf(directionVec).scale(-.5)
			.add(.5, 15 / 16f, .5);
		ms.translate(beltStartOffset.x, beltStartOffset.y, beltStartOffset.z);

        boolean onContraption = be.getLevel() instanceof WrappedWorld;

		for (TransportedItemStack transported : be.getInventory()
			.getTransportedItems()) {
			ms.pushPose();
            TransformStack.cast(ms)
				.nudge(transported.angle);

			float offset;
			float sideOffset;

			if (be.getSpeed() == 0) {
				offset = transported.beltPosition;
				sideOffset = transported.sideOffset;
			} else {
				offset = Mth.lerp(partialTicks, transported.prevBeltPosition, transported.beltPosition);
				sideOffset = Mth.lerp(partialTicks, transported.prevSideOffset, transported.sideOffset);
			}

			Vec3 offsetVec = Vec3.atLowerCornerOf(directionVec).scale(offset);

			ms.translate(offsetVec.x, offsetVec.y, offsetVec.z);

			boolean alongX = beltFacing
							   .getClockWise()
							   .getAxis() == Direction.Axis.X;
			if (!alongX)
				sideOffset *= -1;
			ms.translate(alongX ? sideOffset : 0, 0, alongX ? 0 : sideOffset);

			int stackLight = onContraption ? light : getPackedLight(be, offset);
			ItemRenderer itemRenderer = Minecraft.getInstance()
				.getItemRenderer();
			boolean renderUpright = BeltHelper.isItemUpright(transported.stack);
			boolean blockItem = itemRenderer.getModel(transported.stack, be.getLevel(), null, 0)
				.isGui3d();
			int count = (int) (Mth.log2((int) (transported.stack.getCount()))) / 2;
			Random r = new Random(transported.angle);

			ms.pushPose();
			ms.translate(0, -1 / 8f + 0.005f, 0);
			ShadowRenderHelper.renderShadow(ms, buffer, .75f, .2f);
			ms.popPose();

			if (renderUpright) {
				Entity renderViewEntity = Minecraft.getInstance().cameraEntity;
				if (renderViewEntity != null) {
					Vec3 positionVec = renderViewEntity.position();
					Vec3 vectorForOffset = be.getVectorForOffset(offset);
					Vec3 diff = vectorForOffset.subtract(positionVec);
					float yRot = (float) (Mth.atan2(diff.x, diff.z) + Math.PI);
					ms.mulPose(Vector3f.YP.rotation(yRot));
				}
				ms.translate(0, 3 / 32d, 1 / 16f);
			}

			for (int i = 0; i <= count; i++) {
				ms.pushPose();

				ms.mulPose(Vector3f.YP.rotationDegrees(transported.angle));
				if (!blockItem && !renderUpright) {
					ms.translate(0, -.09375, 0);
					ms.mulPose(Vector3f.XP.rotationDegrees(90));
				}

				if (blockItem) {
					ms.translate(r.nextFloat() * .0625f * i, 0, r.nextFloat() * .0625f * i);
				}

				ms.scale(.5f, .5f, .5f);
				itemRenderer.renderStatic(null, transported.stack, ItemTransforms.TransformType.FIXED, false, ms, buffer, be.getLevel(), stackLight, overlay, 0);
				ms.popPose();

				if (!renderUpright) {
					if (!blockItem)
						ms.mulPose(Vector3f.YP.rotationDegrees(10));
					ms.translate(0, blockItem ? 1 / 64d : 1 / 16d, 0);
				} else
					ms.translate(0, 0, -1 / 16f);

			}

			ms.popPose();
		}
		ms.popPose();
	}

	protected int getPackedLight(SingleBeltBlockEntity controller, float beltPos) {
		int segment = (int) Math.floor(beltPos);
		if (controller.lighter == null || segment >= controller.lighter.lightSegments() || segment < 0)
			return 0;

		return controller.lighter.getPackedLight(segment);
	}

}
