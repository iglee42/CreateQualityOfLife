package fr.iglee42.createqualityoflife.statue;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.CreateQOLClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class StatueRenderer extends LivingEntityRenderer<Statue, StatueModel> {
    public static final ResourceLocation STATUE_LOCATION = CreateQOL.asResource("textures/entity/statue.png");
    public static final ResourceLocation STATUE_SPECIAL_1 = CreateQOL.asResource("textures/entity/statue_delta.png");
    public static final ResourceLocation STATUE_SPECIAL_2 = CreateQOL.asResource("textures/entity/statue_sus.png");

    public StatueRenderer(EntityRendererProvider.Context context) {
        super(context, new StatueModel(context.bakeLayer(CreateQOLClient.STATUE), false), 0.0F);
        this.addLayer(new HumanoidArmorLayer<>(this, new StatueArmorModel<>(context.bakeLayer(CreateQOLClient.STATUE_INNER_ARMOR)), new StatueArmorModel<>(context.bakeLayer(CreateQOLClient.STATUE_OUTER_ARMOR)), context.getModelManager()));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new ElytraLayer<>(this, context.getModelSet()));
       //this.addLayer(new StrawStatueDeadmau5EarsLayer(this));
        this.addLayer(new StatueCapeLayer(this));
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
    }

    public static Optional<PlayerSkin> getPlayerProfileTexture(Statue statue) {
        return statue.getProfile().map(ResolvableProfile::gameProfile).map((GameProfile gameProfile) -> {
            Minecraft minecraft = Minecraft.getInstance();
            return minecraft.getSkinManager().getInsecureSkin(gameProfile);
        });
    }

    @Override
    public void render(Statue entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        this.setModelProperties(entity);
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public Vec3 getRenderOffset(Statue entity, float partialTicks) {
        return entity.isCrouching() ? new Vec3(0.0, -0.125, 0.0) : super.getRenderOffset(entity, partialTicks);
    }

    private void setModelProperties(Statue entity) {
        StatueModel model = this.getModel();
        model.setAllVisible(true);
        model.hat.visible = entity.isPartShown(PlayerModelPart.HAT);
        model.jacket.visible = entity.isPartShown(PlayerModelPart.JACKET);
        model.leftPants.visible = entity.isPartShown(PlayerModelPart.LEFT_PANTS_LEG);
        model.rightPants.visible = entity.isPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
        model.leftSleeve.visible = model.slimLeftSleeve.visible = entity.isPartShown(PlayerModelPart.LEFT_SLEEVE);
        model.rightSleeve.visible = model.slimRightSleeve.visible = entity.isPartShown(PlayerModelPart.RIGHT_SLEEVE);
        model.crouching = entity.isCrouching();
    }

    @Override
    public ResourceLocation getTextureLocation(Statue entity) {
        if (entity.getSkin() == 1) return STATUE_SPECIAL_1;
        if (entity.getSkin() == 2) return STATUE_SPECIAL_2;
        return getPlayerProfileTexture(entity).map(PlayerSkin::texture).orElse(STATUE_LOCATION);
    }

    @Override
    protected void scale(Statue livingEntity, PoseStack matrixStack, float partialTickTime) {
        float modelScale = Mth.lerp(partialTickTime, livingEntity.entityScaleO, livingEntity.getEntityScale());
        modelScale *= 0.9375F;
        matrixStack.scale(modelScale, modelScale, modelScale);
    }

    @Override
    protected void setupRotations(Statue entityLiving, PoseStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks, float scale) {
        float entityZRotation = Mth.lerp(partialTicks, entityLiving.entityRotationsO.getZ(), entityLiving.getEntityZRotation());
        float entityYRotation = Mth.lerp(partialTicks, entityLiving.entityRotationsO.getY(), entityLiving.getYRot());
        float entityXRotation = Mth.lerp(partialTicks, entityLiving.entityRotationsO.getX(), entityLiving.getEntityXRotation());
        matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0F - entityZRotation));
        matrixStack.mulPose(Axis.YP.rotationDegrees(entityYRotation));
        matrixStack.mulPose(Axis.XP.rotationDegrees(180.0F - entityXRotation));
        float hurtAmount = (float) (entityLiving.level().getGameTime() - entityLiving.lastHit) + partialTicks;
        if (hurtAmount < 5.0F) {
            matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(hurtAmount / 1.5F * 3.1415927F) * 3.0F));
        }
        if (isEntityUpsideDown(entityLiving)) {
            matrixStack.translate(0.0, entityLiving.getBbHeight() - 0.0625F, 0.0);
            matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
    }

    @Override
    protected boolean shouldShowName(Statue entity) {
        double d = this.entityRenderDispatcher.distanceToSqr(entity);
        float f = entity.isCrouching() ? 32.0F : 64.0F;
        return !(d >= f * f) && entity.isCustomNameVisible();
    }

    @Override
    @Nullable
    protected RenderType getRenderType(Statue livingEntity, boolean bodyVisible, boolean translucent, boolean glowing) {
        if (!livingEntity.isMarker()) {
            return super.getRenderType(livingEntity, bodyVisible, translucent, glowing);
        } else {
            ResourceLocation resourceLocation = this.getTextureLocation(livingEntity);
            if (translucent) {
                return RenderType.entityTranslucent(resourceLocation, false);
            } else {
                return bodyVisible ? RenderType.entityCutoutNoCull(resourceLocation, false) : null;
            }
        }
    }


}