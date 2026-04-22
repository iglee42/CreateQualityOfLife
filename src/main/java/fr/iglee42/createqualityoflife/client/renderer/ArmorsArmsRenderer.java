package fr.iglee42.createqualityoflife.client.renderer;

import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.registries.QOLItems;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderArmEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class ArmorsArmsRenderer {

	private static final ResourceLocation SHADOW_RADIANCE_ARMS =
		CreateQOL.asResource("textures/models/armor/shadow_radiance_arm.png");
	private static final ResourceLocation SHADOW_STEEL_ARMS =
		CreateQOL.asResource("textures/models/armor/shadow_steel_arm.png");
	private static final ResourceLocation REFINED_RADIANCE_ARMS =
		CreateQOL.asResource("textures/models/armor/refined_radiance_arm.png");

	private static boolean rendererActive = false;

	public static void clientTick() {
		Minecraft mc = Minecraft.getInstance();
		rendererActive =
			mc.player != null
					&& (QOLItems.SHADOW_RADIANCE_CHESTPLATE.isIn(mc.player.getItemBySlot(EquipmentSlot.CHEST))
						|| QOLItems.REFINED_RADIANCE_CHESTPLATE.isIn(mc.player.getItemBySlot(EquipmentSlot.CHEST))
						|| QOLItems.SHADOW_STEEL_CHESTPLATE.isIn(mc.player.getItemBySlot(EquipmentSlot.CHEST)))
					&& mc.player.getItemBySlot(EquipmentSlot.CHEST).getOrDefault(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL).shouldRenderArmor()
					&& mc.player.getItemBySlot(EquipmentSlot.CHEST).getOrDefault(QOLDataComponents.BACKTANK_ARMS,true);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onRenderPlayerHand(RenderArmEvent event) {
		if (!rendererActive)
			return;

		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		MultiBufferSource buffer = event.getMultiBufferSource();
		if (!(mc.getEntityRenderDispatcher()
			.getRenderer(player) instanceof PlayerRenderer pr))
			return;

		PlayerModel<AbstractClientPlayer> model = pr.getModel();
		model.attackTime = 0.0F;
		model.crouching = false;
		model.swimAmount = 0.0F;
		model.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		ModelPart armPart = event.getArm() == HumanoidArm.LEFT ? model.leftSleeve : model.rightSleeve;
		armPart.xRot = 0.0F;
		ResourceLocation texture = QOLItems.SHADOW_RADIANCE_CHESTPLATE.isIn(mc.player.getItemBySlot(EquipmentSlot.CHEST)) ? SHADOW_RADIANCE_ARMS
				: (QOLItems.SHADOW_STEEL_CHESTPLATE.isIn(mc.player.getItemBySlot(EquipmentSlot.CHEST)) ? SHADOW_STEEL_ARMS : REFINED_RADIANCE_ARMS);
		boolean hasGlint = mc.player.getItemBySlot(EquipmentSlot.CHEST).hasFoil();
		armPart.render(event.getPoseStack(), ItemRenderer.getFoilBuffer(buffer,RenderType.entitySolid(texture),false,hasGlint),
			LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
		event.setCanceled(true);
	}

}
