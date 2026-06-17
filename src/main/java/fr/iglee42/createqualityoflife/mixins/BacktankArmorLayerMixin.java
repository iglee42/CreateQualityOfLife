package fr.iglee42.createqualityoflife.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.equipment.armor.BacktankArmorLayer;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import fr.iglee42.createqualityoflife.CreateQOLClient;
import fr.iglee42.createqualityoflife.blocks.ShadowRadianceBacktankBlock;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.items.armors.ShadowRadianceChestplate;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.registries.QOLItems;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import fr.iglee42.createqualityoflife.utils.PreferredRender;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = BacktankArmorLayer.class,remap = true)
public class BacktankArmorLayerMixin<T extends LivingEntity> {

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",remap = false, at= @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",ordinal = 0,shift = At.Shift.BEFORE),locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void inject(PoseStack ms, MultiBufferSource buffer, int light, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci, BacktankItem item, EntityModel entityModel, HumanoidModel model, boolean hasGlint, VertexConsumer vc, BlockState renderedState, SuperByteBuffer backtank, SuperByteBuffer cogs, SuperByteBuffer nob){
        if (entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof QOLConfigurableItem it && entity.getItemBySlot(EquipmentSlot.HEAD).getOrDefault(QOLDataComponents.INVISIBLE_ARMOR,false) && it.providedEffect(entity.getItemBySlot(EquipmentSlot.HEAD)).equals(MobEffects.INVISIBILITY) && entity.getItemBySlot(EquipmentSlot.HEAD).getOrDefault(QOLDataComponents.ARMOR_EFFECT,true)){
            ci.cancel();
            return;
        }
        if (QOLItems.SHADOW_RADIANCE_CHESTPLATE.is(item) || QOLItems.REFINED_RADIANCE_CHESTPLATE.is(item) || QOLItems.SHADOW_STEEL_CHESTPLATE.is(item)){
            ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
            if (!stack.getOrDefault(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL).shouldRenderAddition()){
                ci.cancel();
                return;
            }
            if (!stack.getOrDefault(QOLDataComponents.PREFERRED_RENDER, PreferredRender.BOTH).shouldRenderBacktank()){
                ci.cancel();
                return;
            }


            if (QOLItems.SHADOW_RADIANCE_CHESTPLATE.is(item))renderedState = renderedState.setValue(ShadowRadianceBacktankBlock.PROPELLER,ShadowRadianceChestplate.hasPropeller(stack) && CreateQOLConfigs.server().equipments.armors.propellerAllowed.get());
            backtank = CachedBuffers.block(renderedState);

            ms.pushPose();

            model.body.translateAndRotate(ms);
            ms.translate(-1 / 2f, 10 / 16f, 1f);
            ms.scale(1, -1, -1);

            backtank.disableDiffuse()
                    .light(light)
                    .renderInto(ms, vc);

            nob.disableDiffuse()
                    .translate(0, -3f / 16, 0)
                    .light(light)
                    .renderInto(ms, vc);

            cogs.center()
                    .rotateYDegrees(180)
                    .uncenter()
                    .translate(0, 6.5f / 16, 11f / 16)
                    .rotate(AngleHelper.rad(2 * AnimationTickHolder.getRenderTime(entity.level()) % 360), Direction.EAST)
                    .translate(0, -6.5f / 16, -11f / 16);

            cogs.disableDiffuse()
                    .light(light)
                    .renderInto(ms, vc);

            if (ShadowRadianceChestplate.hasPropeller(stack)) {
                CreateQOLClient.showPropellers(renderedState, light, ms, buffer, Sheets.cutoutBlockSheet(), entity.level(),hasGlint);
            }
            ms.popPose();
            ci.cancel();
        }
    }



}
