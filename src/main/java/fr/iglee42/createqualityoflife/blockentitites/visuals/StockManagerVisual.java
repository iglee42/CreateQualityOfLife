package fr.iglee42.createqualityoflife.blockentitites.visuals;

import java.util.function.Consumer;

import com.simibubi.create.content.processing.burner.BlazeBurnerRenderer;
import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import fr.iglee42.createqualityoflife.registries.QOLPartialModels;
import net.createmod.catnip.animation.AnimationTickHolder;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.render.AllInstanceTypes;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visual.TickableVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.Translate;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class StockManagerVisual extends AbstractBlockEntityVisual<StockManagerBlockEntity> implements SimpleDynamicVisual, SimpleTickableVisual {


	private final TransformedInstance head;


	@Nullable
	private TransformedInstance smallRods;
	@Nullable
	private TransformedInstance largeRods;

	@Nullable
	private TransformedInstance hat;


	public StockManagerVisual(VisualizationContext ctx, StockManagerBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);


		PartialModel blazeModel = AllPartialModels.BLAZE_IDLE;

		head = instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(blazeModel))
				.createInstance();

		head.light(LightTexture.FULL_BRIGHT);


		PartialModel rodsModel = QOLPartialModels.STOCK_MANAGER_RODS_1;
		PartialModel rodsModel2 = QOLPartialModels.STOCK_MANAGER_RODS_2;
		smallRods = instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(rodsModel))
				.createInstance();
		largeRods = instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(rodsModel2))
				.createInstance();
		smallRods.light(LightTexture.FULL_BRIGHT);
		largeRods.light(LightTexture.FULL_BRIGHT);

		hat = instancerProvider()
				.instancer(InstanceTypes.TRANSFORMED,
						Models.partial(
								AllPartialModels.LOGISTICS_HAT))
				.createInstance();
		hat.light(LightTexture.FULL_BRIGHT);

		animate(partialTick);
	}

	@Override
	public void tick(TickableVisual.Context context) {
		blockEntity.tickAnimation();
	}

	@Override
	public void beginFrame(DynamicVisual.Context ctx) {
		if (!isVisible(ctx.frustum()) || doDistanceLimitThisFrame(ctx)) {
			return;
		}

		animate(ctx.partialTick());
	}

	private void animate(float partialTicks) {
		float animation = blockEntity.headAnimation.getValue(partialTicks) * .175f;

		var hashCode = blockEntity.hashCode();
		float time = AnimationTickHolder.getRenderTime(level);
		float renderTick = time + (hashCode % 13) * 16f;
		float offsetMult =  64;
		float offset = Mth.sin((float) ((renderTick / 16f) % (2 * Math.PI))) / offsetMult;
		float headY = offset - (animation * .75f);

		float horizontalAngle = AngleHelper.rad(blockEntity.headAngle.getValue(partialTicks));

		head.setIdentityTransform()
				.translate(getVisualPosition())
				.translateY(0.025f)
				.translateY(headY)
				.translate(Translate.CENTER)
				.rotateY(horizontalAngle)
				.translateBack(Translate.CENTER)
				.setChanged();

		if (hat != null) {
			hat.setIdentityTransform()
					.translate(getVisualPosition())
					.translateY(0.025f)
					.translateY(headY)
					.translateY(0.75f);
			hat.rotateCentered(horizontalAngle + Mth.PI, Direction.UP)
					.translate(0.5f, 0, 0.5f)
					.light(LightTexture.FULL_BRIGHT);

			hat.setChanged();
		}

		if (smallRods != null) {
			float offset1 = Mth.sin((float) ((renderTick / 16f + Math.PI) % (2 * Math.PI))) / offsetMult;

			smallRods.setIdentityTransform()
					.translate(getVisualPosition())
					.translateY(0.025f)
					.translateY(offset1 + animation + .125f)
					.setChanged();
		}

		if (largeRods != null) {
			float offset2 = Mth.sin((float) ((renderTick / 16f + Math.PI / 2) % (2 * Math.PI))) / offsetMult;

			largeRods.setIdentityTransform()
					.translate(getVisualPosition())
					.translateY(0.025f)
					.translateY(offset2 + animation - 3 / 16f)
					.setChanged();
		}
	}

	@Override
	public void updateLight(float partialTick) {
	}

	@Override
	public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {

	}

	@Override
	protected void _delete() {
		head.delete();
		if (smallRods != null) {
			smallRods.delete();
		}
		if (largeRods != null) {
			largeRods.delete();
		}
		if (hat != null) {
			hat.delete();
		}
	}
}
