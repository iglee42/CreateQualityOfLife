package fr.iglee42.createqualityoflife.statue;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class StatueArmorModel<T extends Statue> extends HumanoidModel<T> {

    public StatueArmorModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        StatueModel.setupPoseAnim(this, entity);
    }
}