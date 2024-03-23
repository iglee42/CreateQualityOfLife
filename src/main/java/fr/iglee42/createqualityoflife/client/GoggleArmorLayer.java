package fr.iglee42.createqualityoflife.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllPartialModels;
import fr.iglee42.createqualityoflife.registries.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class GoggleArmorLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public GoggleArmorLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack ms, MultiBufferSource buffer, int light, LivingEntity entity, float yaw, float pitch, float pt, float p_117356_, float p_117357_, float p_117358_) {
        if (entity.getPose() == Pose.SLEEPING)
            return;
        if (!ModItems.SHADOW_RADIANCE_HELMET.isIn(entity.getItemBySlot(EquipmentSlot.HEAD)))
            return;

        M entityModel = getParentModel();
        if (!(entityModel instanceof HumanoidModel<?> model))
            return;

        ItemStack stack = new ItemStack(AllItems.GOGGLES.get());

        // Translate and rotate with our head
        ms.pushPose();
        ms.translate(model.head.x / 16.0, model.head.y / 16.0, model.head.z / 16.0);
        ms.mulPose(Vector3f.YP.rotation(model.head.yRot));
        ms.mulPose(Vector3f.XP.rotation(model.head.xRot));

        // Translate and scale to our head
        ms.translate(0, -0.30, -0.045);
        ms.mulPose(Vector3f.ZP.rotationDegrees(180.0f));
        ms.scale(0.625f, 0.625f, 0.625f);


        // Render
        //Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.HEAD, light, OverlayTexture.NO_OVERLAY, ms, buffer, 0);
        Minecraft.getInstance().getItemRenderer().render(stack, ItemTransforms.TransformType.HEAD, false, ms, buffer, light, OverlayTexture.NO_OVERLAY, AllPartialModels.GOGGLES.get());
        ms.popPose();
    }

    public static void registerOnAll(EntityRenderDispatcher renderManager) {
        for (EntityRenderer<? extends Player> renderer : renderManager.getSkinMap().values())
            registerOn(renderer);
        for (EntityRenderer<?> renderer : renderManager.renderers.values())
            registerOn(renderer);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void registerOn(EntityRenderer<?> entityRenderer) {
        if (!(entityRenderer instanceof LivingEntityRenderer<?, ?> livingRenderer))
            return;
        if (!(livingRenderer.getModel() instanceof HumanoidModel))
            return;
        GoggleArmorLayer<?,?> layer = new GoggleArmorLayer<>(livingRenderer);
        livingRenderer.addLayer((GoggleArmorLayer)layer);
    }
}