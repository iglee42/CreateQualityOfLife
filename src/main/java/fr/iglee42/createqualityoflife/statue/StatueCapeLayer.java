package fr.iglee42.createqualityoflife.statue;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Optional;

public class StatueCapeLayer extends RenderLayer<Statue, StatueModel> {

    public StatueCapeLayer(RenderLayerParent<Statue, StatueModel> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, Statue livingEntity, float limbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        Optional<ResourceLocation> texture = StatueRenderer.getPlayerProfileTexture(livingEntity).map(PlayerSkin::capeTexture);
        if (texture.isPresent() && !livingEntity.isInvisible() && livingEntity.isPartShown(PlayerModelPart.CAPE)) {
            ItemStack itemstack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
            if (!itemstack.is(Items.ELYTRA)) {
                matrixStack.pushPose();
                if (this.getParentModel().young) {
                    matrixStack.translate(0.0, 0.75, 0.0);
                    matrixStack.scale(0.5F, 0.5F, 0.5F);
                }
                matrixStack.translate(0.0D, 0.0D, 0.125D);
                double d0 = 0.0;
                double d1 = 0.0;
                double d2 = 0.0;
                float f = livingEntity.yBodyRotO + (livingEntity.yBodyRot - livingEntity.yBodyRotO);
                double d3 = Mth.sin(f * ((float)Math.PI / 180F));
                double d4 = -Mth.cos(f * ((float)Math.PI / 180F));
                float f1 = (float)d1 * 10.0F;
                f1 = Mth.clamp(f1, -6.0F, 32.0F);
                float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
                f2 = Mth.clamp(f2, 0.0F, 150.0F);
                float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
                f3 = Mth.clamp(f3, -20.0F, 20.0F);
                if (f2 < 0.0F) {
                    f2 = 0.0F;
                }

                float f4 = 0.0F;
                f1 += Mth.sin(Mth.lerp(pPartialTicks, livingEntity.walkDistO, livingEntity.walkDist) * 6.0F) * 32.0F * f4;
                if (livingEntity.isCrouching()) {
                    f1 += 25.0F;
                }

                matrixStack.mulPose(Axis.XP.rotationDegrees(6.0F + f2 / 2.0F + f1));
                matrixStack.mulPose(Axis.ZP.rotationDegrees(f3 / 2.0F));
                matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - f3 / 2.0F));
                VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entitySolid(texture.get()));
                this.getParentModel().renderCloak(matrixStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
                matrixStack.popPose();
            }
        }
    }
}