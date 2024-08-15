package fr.iglee42.createqualityoflife.blockentitites.instances;

import com.jozufozu.flywheel.api.InstanceData;
import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.BeltData;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import com.simibubi.create.foundation.utility.Iterate;
import fr.iglee42.createqualityoflife.blockentitites.SingleBeltBlockEntity;
import fr.iglee42.createqualityoflife.blockentitites.renderers.SingleBeltRenderer;
import fr.iglee42.createqualityoflife.blocks.SingleBeltBlock;
import fr.iglee42.createqualityoflife.registries.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.LightLayer;

import java.util.ArrayList;
import java.util.function.Supplier;

public class SingleBeltInstance extends KineticBlockEntityInstance<SingleBeltBlockEntity> {

    boolean alongX;
    boolean alongZ;
    Direction facing;
    protected ArrayList<BeltData> keys;
    protected RotatingData pulleyKey;

    public SingleBeltInstance(MaterialManager materialManager, SingleBeltBlockEntity blockEntity) {
        super(materialManager, blockEntity);

        if (!ModBlocks.SINGLE_BELT.has(blockState)) return;

        keys = new ArrayList<>(2);

        facing = blockState.getValue(SingleBeltBlock.HORIZONTAL_FACING);
        alongX = facing.getAxis() == Direction.Axis.X;
        alongZ = facing.getAxis() == Direction.Axis.Z;


        DyeColor color = blockEntity.color.orElse(null);

        for (boolean bottom : Iterate.trueAndFalse) {
            PartialModel beltPartial = SingleBeltRenderer.getBeltPartial(bottom);
            SpriteShiftEntry spriteShift = SingleBeltRenderer.getSpriteShiftEntry(color, bottom);

            Instancer<BeltData> beltModel = materialManager.defaultSolid()
                    .material(AllMaterialSpecs.BELTS)
                    .getModel(beltPartial, blockState);

            keys.add(setup(beltModel.createInstance(), bottom, spriteShift));

        }

        Instancer<RotatingData> pulleyModel = getPulleyModel();
        pulleyKey = setup(pulleyModel.createInstance());
    }

    @Override
    public void update() {
        DyeColor color = blockEntity.color.orElse(null);

        boolean bottom = true;
        for (BeltData key : keys) {

            SpriteShiftEntry spriteShiftEntry = SingleBeltRenderer.getSpriteShiftEntry(color, bottom);
            key.setScrollTexture(spriteShiftEntry)
               .setColor(blockEntity)
               .setRotationalSpeed(getScrollSpeed());
            bottom = false;
        }

        if (pulleyKey != null) {
            updateRotation(pulleyKey);
        }
    }

    @Override
    public void updateLight() {
        relight(pos, keys.stream());

        if (pulleyKey != null) relight(pos, pulleyKey);
    }

    @Override
    public void remove() {
        keys.forEach(InstanceData::delete);
        keys.clear();
        if (pulleyKey != null) pulleyKey.delete();
        pulleyKey = null;
    }

    private float getScrollSpeed() {
        float speed = -blockEntity.getSpeed();
        if (((facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE)) ^
                ((alongX))) {
            speed = -speed;
        }

        return speed;
    }

    private Instancer<RotatingData> getPulleyModel() {
        Direction dir = getOrientation();

        Direction.Axis axis = dir.getAxis();

        Supplier<PoseStack> ms = () -> {
            PoseStack modelTransform = new PoseStack();
            TransformStack msr = TransformStack.cast(modelTransform);
            msr.centre();
            if (axis == Direction.Axis.X)
                msr.rotateY(90);
            if (axis == Direction.Axis.Y)
                msr.rotateX(90);
            msr.rotateX(90);
            msr.unCentre();

            return modelTransform;
        };

        return getRotatingMaterial().getModel(AllPartialModels.BELT_PULLEY, blockState, dir, ms);
    }

    private Direction getOrientation() {
        return blockState.getValue(SingleBeltBlock.HORIZONTAL_FACING)
                                  .getClockWise();
    }

    private BeltData setup(BeltData key, boolean bottom, SpriteShiftEntry spriteShift) {
        float rotX = 0;
        float rotY = facing.toYRot();
        float rotZ = 0;

        Quaternion q = new Quaternion(rotX, rotY, rotZ, true);

		key.setScrollTexture(spriteShift)
				.setScrollMult(0.5f)
				.setRotation(q)
				.setRotationalSpeed(getScrollSpeed())
				.setRotationOffset(bottom ? 0.5f : 0f)
                .setColor(blockEntity)
                .setPosition(getInstancePosition())
                .setBlockLight(world.getBrightness(LightLayer.BLOCK, pos))
                .setSkyLight(world.getBrightness(LightLayer.SKY, pos));

        return key;
    }

}
