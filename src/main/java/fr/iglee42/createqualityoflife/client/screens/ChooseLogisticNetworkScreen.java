package fr.iglee42.createqualityoflife.client.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import fr.iglee42.createqualityoflife.menus.ChooseLogisticNetworkMenu;
import fr.iglee42.createqualityoflife.packets.ModifyStockManagerLogisticNetworkPacket;
import fr.iglee42.createqualityoflife.registries.QOLGuiTextures;
import fr.iglee42.createqualityoflife.registries.QOLPackets;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.createmod.catnip.gui.UIRenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ChooseLogisticNetworkScreen extends AbstractSimiContainerScreen<ChooseLogisticNetworkMenu> {


	private static final AllGuiTextures NUMBERS = AllGuiTextures.NUMBERS;
	private static final QOLGuiTextures HEADER = QOLGuiTextures.CHOOSE_NETWORK_HEADER;
	private static final QOLGuiTextures BODY = QOLGuiTextures.CHOOSE_NETWORK_BODY;
	private static final QOLGuiTextures FOOTER = QOLGuiTextures.CHOOSE_NETWORK_FOOTER;

	StockManagerBlockEntity blockEntity;
	public LerpedFloat scroll = LerpedFloat.linear().startWithValue((double)0.0F);

	int lockX;
	int lockY;
	int switchX;
	int switchY;
	int windowWidth;
	int windowHeight;

	private List<ChooseLogisticNetworkMenu.LogisticNetworksInfos> networks;

	private boolean scrollHandleActive;

	private List<Rect2i> extraAreas = Collections.emptyList();


	public ChooseLogisticNetworkScreen(ChooseLogisticNetworkMenu container, Inventory inv, Component title) {
		super(container, inv, title);
		blockEntity = menu.contentHolder;
		networks = new ArrayList<>(menu.networksInfos);
	}

	@Override
	protected void init() {
		int appropriateHeight = Minecraft.getInstance()
				.getWindow()
				.getGuiScaledHeight() - 10;
		appropriateHeight -=
				Mth.positiveModulo(appropriateHeight - HEADER.getHeight() - FOOTER.getHeight(), BODY.getHeight());
		appropriateHeight =
				Math.min(appropriateHeight, HEADER.getHeight() + FOOTER.getHeight() + BODY.getHeight() * 17);

		setWindowSize(windowWidth = 226, windowHeight = appropriateHeight);
		super.init();
		clearWidgets();

		networks.sort((a, b) -> {
			if (Minecraft.getInstance().getUser().getGameProfile().getId().equals(a.owner()) && !Minecraft.getInstance().getUser().getGameProfile().getId().equals(b.owner())) {
				return -1;
			} else if (!Minecraft.getInstance().getUser().getGameProfile().getId().equals(a.owner()) && Minecraft.getInstance().getUser().getGameProfile().getId().equals(b.owner())) {
				return 1;
			} else {
				if (!a.locked() && b.locked()) {
					return -1;
				} else if (a.locked() && !b.locked()) {
					return 1;
				} else {
					return a.name().compareTo(b.name());
				}
			}
		});
		int x = getGuiLeft();
		int y = getGuiTop();

		lockX = x + 186;
		lockY = y + 18;

		switchX = x + 25;
		switchY = y + 18;


		extraAreas = new ArrayList<>();
		int leftHeight = 40;
		int rightHeight = 50;

	}

	@Override
	protected void containerTick() {
		super.containerTick();
		scroll.tickChaser();
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics) {
		PoseStack ms = guiGraphics.pose();
		ms.pushPose();
		ms.translate(0, 0, -300);
		super.renderBackground(guiGraphics);
		ms.popPose();
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		if (this != minecraft.screen)
			return; // stencil buffer does not cooperate with ponders gui fade out

		PoseStack ms = graphics.pose();
		ms.pushPose();

		int x = getGuiLeft();
		int y = getGuiTop();

		// BG
		HEADER.render(graphics, x - 15, y);
		y += HEADER.getHeight();
		for (int i = 0; i < (windowHeight - HEADER.getHeight() - FOOTER.getHeight())/ BODY.getHeight(); i++) {
			BODY.render(graphics, x - 15, y);
			y += BODY.getHeight();
		}
		FOOTER.render(graphics, x - 15, y);
		y = getGuiTop();


		ms.pushPose();
		this.renderNetworks(graphics, mouseX, mouseY, partialTicks);
		ms.popPose();

		int center = leftPos + windowWidth / 2;
		Component component = CreateQOLLang.translateDirect("gui.choose_network.choose_a_network");
		graphics.drawString(font, component.getString(), (float) (center - font.width(component) / 2 + 2),
				(float) topPos + 6, 0x3D3C48,false);
		ms.popPose();


		//UIRenderHelper.swapAndBlitColor(UIRenderHelper.framebuffer, minecraft.getMainRenderTarget());
	}

	@Override
	protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.renderForeground(graphics, mouseX, mouseY, partialTicks);
		action(graphics, mouseX, mouseY, -1);
	}

	protected void renderNetworks(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		PoseStack matrixStack = graphics.pose();
		int yOffset = getGuiTop() + HEADER.getHeight() - 4;

		float scrollOffset = -this.scroll.getValue(partialTicks);

		for(int i = 0; i < networks.size(); ++i) {
			ChooseLogisticNetworkMenu.LogisticNetworksInfos entry = networks.get(i);
            int itemWindowX = leftPos + 3;
            int itemWindowY = topPos + HEADER.getHeight();
            int itemWindowX2 = itemWindowX + 210;
            int itemWindowY2 = itemWindowY + (windowHeight - HEADER.getHeight() - FOOTER.getHeight());

            graphics.enableScissor(itemWindowX - 5, itemWindowY, itemWindowX2 + 10, itemWindowY2);
			matrixStack.pushPose();
			matrixStack.translate(0.0F, scrollOffset, 0.0F);
			/*if (i == entries.size()) {
				AllGuiTextures.STOCK_KEEPER_CATEGORY_NEW.render(graphics, this.leftPos + 7, this.topPos + yOffset);
				matrixStack.popPose();
				this.endStencil();
				break;
			}*/

			int cardHeight = this.renderScheduleEntry(graphics, i, entry, yOffset, mouseX, mouseY, partialTicks);
			yOffset += cardHeight;
			matrixStack.popPose();
			graphics.disableScissor();
		}

	}


	public int renderScheduleEntry(GuiGraphics graphics, int i, ChooseLogisticNetworkMenu.LogisticNetworksInfos entry, int yOffset, int mouseX, int mouseY, float partialTicks) {
		int cardWidth = 160;
		int cardHeader = 20;
		PoseStack matrixStack = graphics.pose();
		matrixStack.pushPose();
		matrixStack.translate((float)(this.leftPos + 20), (float)(this.topPos + yOffset), 0.0F);
		QOLGuiTextures.CHOOSE_NETWORK_ENTRY.render(graphics, 0, 0);
		(entry.locked()? QOLGuiTextures.CHOOSE_NETWORK_LOCKED: QOLGuiTextures.CHOOSE_NETWORK_UNLOCKED).render(graphics, 171 + (!entry.locked()?2 :0),4);
		//graphics.renderItem(entry, 14, 1);
		Component name = Component.literal(entry.name());
		graphics.drawString(this.font, entry.name().isEmpty() ? CreateLang.translate("gui.stock_ticker.empty_category_name_placeholder").string() : name.getString(20).stripTrailing() + (name.getString(20).stripTrailing().length() > 20 ? "..." : ""), 12, 5, 6645093, false);


		if (entry.owner() != null &&  Minecraft.getInstance().level != null){
			Component playerName = Minecraft.getInstance().level.getPlayerByUUID(entry.owner()) != null ? Minecraft.getInstance().level.getPlayerByUUID(entry.owner()).getName() :CreateQOLLang.translateDirect( "statue.unknow_owner");
			PlayerInfo info = Minecraft.getInstance().player.connection.getPlayerInfo(entry.owner());
			if (info != null){
				PlayerFaceRenderer.draw(graphics,info.getSkinLocation(),166 - font.width(playerName) - 13,3,11);
			}
			graphics.drawString(this.font, playerName.getString(18).stripTrailing() + (playerName.getString().length() > 18 ? "..." : "") , 166 - font.width(playerName), 5, 6645093, false);
		}
		matrixStack.popPose();
		return cardHeader;
	}


	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
		if (action(null, pMouseX, pMouseY, pButton)) {
			playUiSound(SoundEvents.UI_BUTTON_CLICK.value(), 1f, 1f);
			return true;
		}
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}


	private final Component clickToEdit = CreateQOLLang.translateDirect("gui.choose_network.lmb_manage")
			.withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);
	public boolean action(@Nullable GuiGraphics graphics, double mouseX, double mouseY, int click) {
		// Prevent actions outside the window for them
		if (mouseX < leftPos || mouseX >= leftPos + imageWidth || mouseY < topPos + 15 || mouseY >= topPos + 99)
			return false;


		int mx = (int) mouseX;
		int my = (int) mouseY;
		int x = mx - leftPos - 20;
		int y = my - topPos - 24;
		if (x < 0 || x >= 196)
			return false;
		if (y < 0 || y >= 143)
			return false;
		y += scroll.getValue(0);

		List<ChooseLogisticNetworkMenu.LogisticNetworksInfos> entries = networks;
		for (int i = 0; i < entries.size(); i++) {
			ChooseLogisticNetworkMenu.LogisticNetworksInfos entry = entries.get(i);
			int cardHeight = 20;

			if (y >= cardHeight) {
				y -= cardHeight;
				if (y < 0)
					return false;
				continue;
			}

			int fieldSize = 165;
			if (x > 0 && x <= fieldSize && y > 0 && y <= 16) {
				List<Component> components = new ArrayList<>();
				components
						.add(entry.name().isEmpty() ? CreateLang.translate("gui.stock_ticker.empty_category_name_placeholder")
								.component() : Component.literal(entry.name()));
				components.add(clickToEdit);
				renderActionTooltip(graphics, components, mx, my);
				if (click == 0 && (!entry.locked() || entry.owner() == null || Minecraft.getInstance().getUser().getGameProfile().getId().equals(entry.owner())))
					QOLPackets.getChannel().sendToServer(new ModifyStockManagerLogisticNetworkPacket(blockEntity.getBlockPos(),entry.id()));
				/*if (click == 0)
					startEditing(i);*/
				return true;
			}

			if (x > fieldSize && x <= fieldSize + 16 && y > 0 && y <= 16) {
				renderActionTooltip(graphics, ImmutableList.of(CreateLang.translate(entry.locked() ? "gui.stock_keeper.network_locked" : "gui.stock_keeper.network_open")
						.component()), mx, my);
				if (click == 0) {
					/*if (!entry.isEmpty())
						CatnipServices.NETWORK.sendToServer(new StockKeeperCategoryRefundPacket(menu.contentHolder.getBlockPos(), entry));*/
				}
				return true;
			}

			x -= 18;
			y -= 28;

			if (x < 0 || y < 0 || x > 160)
				return false;
		}

		return false;
	}

	private void renderActionTooltip(@Nullable GuiGraphics graphics, List<Component> tooltip, int mx, int my) {
		if (graphics != null)
			graphics.renderTooltip(font, tooltip, Optional.empty(), mx, my);
	}


	@Override
	public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
		if (pButton == GLFW.GLFW_MOUSE_BUTTON_LEFT && scrollHandleActive) {
			scrollHandleActive = false;
			if (minecraft.isWindowActive())
				GLFW.glfwSetInputMode(minecraft.getWindow()
					.getWindow(), 208897, GLFW.GLFW_CURSOR_NORMAL);
		}
		return super.mouseReleased(pMouseX, pMouseY, pButton);
	}
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
		float chaseTarget = this.scroll.getChaseTarget();
		float max = (float)(40 - (3 + AllGuiTextures.STOCK_KEEPER_CATEGORY.getHeight() * 4));
		max += (float)(networks.size() * 20 + 24);
		if (max > 0.0F) {
			chaseTarget -= (float)(scrollY * (double)12.0F);
			chaseTarget = Mth.clamp(chaseTarget, 0.0F, max);
			this.scroll.chase((double)((int)chaseTarget), (double)0.7F, Chaser.EXP);
		} else {
			this.scroll.chase((double)0.0F, (double)0.7F, Chaser.EXP);
		}

		return super.mouseScrolled(mouseX, mouseY, scrollY);
	}

	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
		return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
	}


	@Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {


		return super.keyPressed(pKeyCode,pScanCode,pModifiers);
	}

	@Override
	public void removed() {
		BlockPos pos = blockEntity.getBlockPos();
		super.removed();
	}


	@Override
	public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
		return super.keyReleased(pKeyCode, pScanCode, pModifiers);
	}

	@Override
	public List<Rect2i> getExtraAreas() {
		return extraAreas;
	}

}
