package fr.iglee42.createqualityoflife.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.equipment.armor.BacktankArmorLayer;
import com.simibubi.create.content.equipment.armor.BacktankItem;

import fr.iglee42.createqualityoflife.CreateQOLClient;
import fr.iglee42.createqualityoflife.blocks.ShadowRadianceBacktankBlock;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.items.ShadowRadianceChestplate;
import fr.iglee42.createqualityoflife.registries.ModItems;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import fr.iglee42.createqualityoflife.utils.PreferredRender;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.Direction;
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
public class BacktankArmorLayerMixin {

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at= @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",ordinal = 0,shift = At.Shift.BEFORE),locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void inject(PoseStack ms, MultiBufferSource buffer, int light, LivingEntity entity, float yaw, float pitch, float pt, float p_225628_8_, float p_225628_9_, float p_225628_10_, CallbackInfo ci, BacktankItem item, EntityModel entityModel, HumanoidModel model, VertexConsumer vc, BlockState renderedState, SuperByteBuffer backtank, SuperByteBuffer cogs, SuperByteBuffer nob){
        if (ModItems.SHADOW_RADIANCE_CHESTPLATE.is(item)){
            ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
            if (!NBTConstants.getOrDefault(stack,NBTConstants.NBT_RENDER_TYPE).shouldRenderAddition()){
                ci.cancel();
                return;
            }
            if (!stack.getOrDefault(ModDataComponents.PREFERRED_RENDER, PreferredRender.BOTH).shouldRenderBacktank()){
                ci.cancel();
                return;
            }
            renderedState = renderedState.setValue(ShadowRadianceBacktankBlock.PROPELLER,ShadowRadianceChestplate.hasPropeller(stack) && CreateQOLConfigs.server().propellerAllowed.get());
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
                CreateQOLClient.showPropellers(renderedState, light, ms, buffer, Sheets.cutoutBlockSheet(), entity.level());
            }
            ms.popPose();
            ci.cancel();
        }
    }



}
