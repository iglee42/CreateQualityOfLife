package fr.iglee42.createqualityoflife.blockentitites.renderers;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import fr.iglee42.createqualityoflife.registries.QOLPartialModels;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class StockManagerRenderer extends SafeBlockEntityRenderer<StockManagerBlockEntity> {

	public StockManagerRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	protected void renderSafe(StockManagerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource,
		int light, int overlay) {

		if (!be.hasBlazeFromBlock()) return;

		Level level = be.getLevel();
		BlockState blockState = be.getBlockState();
		float animation = be.headAnimation.getValue(partialTicks) * .175f;
		float horizontalAngle = AngleHelper.rad(be.headAngle.getValue(partialTicks));
		PartialModel drawHat =  AllPartialModels.LOGISTICS_HAT;
		int hashCode = be.hashCode();

		renderShared(ms, null, bufferSource,
			level, blockState, animation, horizontalAngle,
				drawHat, hashCode);
	}

	public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
										   ContraptionMatrices matrices, MultiBufferSource bufferSource, LerpedFloat headAngle, boolean conductor) {
		BlockState state = context.state;
		HeatLevel heatLevel = BlazeBurnerBlock.getHeatLevelOf(state);
		if (heatLevel == HeatLevel.NONE)
			return;

		if (!heatLevel.isAtLeast(HeatLevel.FADING))
			heatLevel = HeatLevel.FADING;

		Level level = context.world;
		float horizontalAngle = AngleHelper.rad(headAngle.getValue(AnimationTickHolder.getPartialTicks(level)));
		boolean drawGoggles = context.blockEntityData.contains("Goggles");
		boolean drawHat = conductor || context.blockEntityData.contains("TrainHat");
		int hashCode = context.hashCode();

		renderShared(matrices.getViewProjection(), matrices.getModel(), bufferSource,
				level, state, 0, horizontalAngle,
				drawHat ? AllPartialModels.TRAIN_HAT : null, hashCode);
	}

	public static void renderShared(PoseStack ms, @Nullable PoseStack modelTransform, MultiBufferSource bufferSource,
									Level level, BlockState blockState, float animation, float horizontalAngle,
									PartialModel drawHat, int hashCode) {

		float time = AnimationTickHolder.getRenderTime(level);
		float renderTick = time + (hashCode % 13) * 16f;
		float offsetMult = 64;
		float offset = Mth.sin((float) ((renderTick / 16f) % (2 * Math.PI))) / offsetMult;
		float offset1 = Mth.sin((float) ((renderTick / 16f + Math.PI) % (2 * Math.PI))) / offsetMult;
		float offset2 = Mth.sin((float) ((renderTick / 16f + Math.PI / 2) % (2 * Math.PI))) / offsetMult;
		float headY = offset - (animation * .75f);

		ms.pushPose();

		ms.translate(0,0.025f,0);

		var blazeModel = AllPartialModels.BLAZE_IDLE;

		SuperByteBuffer blazeBuffer = CachedBuffers.partial(blazeModel, blockState);
		if (modelTransform != null)
			blazeBuffer.transform(modelTransform);
		blazeBuffer.translate(0, headY, 0);
		draw(blazeBuffer, horizontalAngle, ms, bufferSource.getBuffer(RenderType.solid()));
		if (drawHat != null) {
			SuperByteBuffer hatBuffer = CachedBuffers.partial(drawHat, blockState);
			if (modelTransform != null)
				hatBuffer.transform(modelTransform);
			hatBuffer.translate(0, headY, 0);
			hatBuffer.translateY(0.75f);
			VertexConsumer cutout = bufferSource.getBuffer(RenderType.cutoutMipped());
			hatBuffer
					.rotateCentered(horizontalAngle + Mth.PI, Direction.UP)
					.translate(0.5f, 0, 0.5f)
					.light(LightTexture.FULL_BRIGHT)
					.renderInto(ms, cutout);
		}

			PartialModel rodsModel = QOLPartialModels.STOCK_MANAGER_RODS_1;
			PartialModel rodsModel2 = QOLPartialModels.STOCK_MANAGER_RODS_2;

			SuperByteBuffer rodsBuffer = CachedBuffers.partial(rodsModel, blockState);
			if (modelTransform != null)
				rodsBuffer.transform(modelTransform);
			rodsBuffer.translate(0, offset1 + animation + .125f, 0)
					.light(LightTexture.FULL_BRIGHT)
					.renderInto(ms, bufferSource.getBuffer(RenderType.solid()));

			SuperByteBuffer rodsBuffer2 = CachedBuffers.partial(rodsModel2, blockState);
			if (modelTransform != null)
				rodsBuffer2.transform(modelTransform);
			rodsBuffer2.translate(0, offset2 + animation - 3 / 16f, 0)
					.light(LightTexture.FULL_BRIGHT)
					.renderInto(ms, bufferSource.getBuffer(RenderType.solid()));

		ms.popPose();
	}

	private static void draw(SuperByteBuffer buffer, float horizontalAngle, PoseStack ms, VertexConsumer vc) {
		buffer.rotateCentered(horizontalAngle, Direction.UP)
				.light(LightTexture.FULL_BRIGHT)
				.renderInto(ms, vc);
	}
}
