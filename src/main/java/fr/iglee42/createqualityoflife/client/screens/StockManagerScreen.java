package fr.iglee42.createqualityoflife.client.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.menus.StockManagerMenu;
import fr.iglee42.createqualityoflife.packets.*;
import fr.iglee42.createqualityoflife.registries.QOLGuiTextures;
import fr.iglee42.createqualityoflife.utils.NetworkDestructionLevel;
import fr.iglee42.createqualityoflife.utils.NetworkPermission;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StockManagerScreen extends AbstractSimiContainerScreen<StockManagerMenu>{

	private EditBox nameBox;

	private static final AllGuiTextures NUMBERS = AllGuiTextures.NUMBERS;
	private static final QOLGuiTextures HEADER = QOLGuiTextures.STOCK_MANAGER_HEADER;
	private static final QOLGuiTextures UPPER_BODY = QOLGuiTextures.STOCK_MANAGER_UPPER_BODY;
	private static final QOLGuiTextures SEPARATION = QOLGuiTextures.STOCK_MANAGER_SEPARATION;
	private static final QOLGuiTextures LOWER_BODY = QOLGuiTextures.STOCK_MANAGER_LOWER_BODY;
	private static final QOLGuiTextures FOOTER = QOLGuiTextures.STOCK_MANAGER_FOOTER;

	StockManagerBlockEntity blockEntity;
	public LerpedFloat itemScroll;

	int lockX;
	int lockY;
	int destroyX;
	int destroyY;
	int switchX;
	int switchY;
	int windowWidth;
	int windowHeight;

	private boolean isAdmin;
	private boolean isOwner;
	private boolean isLocked;
	private String networkName;
	private int links;
	private List<LogisticallyLinkedBehaviour> behaviours;
	private NetworkDestructionLevel destructionLevel;
	private boolean mayDestroy;
	private boolean scrollHandleActive;
	private Map<UUID, NetworkPermission> permissions;

	private List<Rect2i> extraAreas = Collections.emptyList();

	public LerpedFloat blocksScroll = LerpedFloat.linear().startWithValue((double)0.0F);
	public LerpedFloat playersScroll = LerpedFloat.linear().startWithValue((double)0.0F);

	private int lowerBodyStartY;



	public StockManagerScreen(StockManagerMenu container, Inventory inv, Component title) {
		super(container, inv, title);
		isAdmin = menu.isAdmin;
		isOwner = menu.isOwner;
		isLocked = menu.isLocked;
		mayDestroy = menu.mayDestroy;
		networkName = menu.name;
		links = menu.links;
		destructionLevel = menu.destructionLevel;
		permissions = menu.permissions.entrySet()
				.stream()
				.sorted((e1,e2)-> e2.getValue().ordinal() - e1.getValue().ordinal())
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(oldValue, newValue) -> oldValue, LinkedHashMap::new));

		blockEntity = container.contentHolder;
		behaviours = new ArrayList<>(LogisticallyLinkedBehaviour.getAllPresent(blockEntity.behaviour.freqId,true,true));
		behaviours.sort((a,b)->{
			if (a.getPos().equals(blockEntity.getBlockPos()) && !b.getPos().equals(blockEntity.getBlockPos())){
				return -1;
			} else if (!a.getPos().equals(blockEntity.getBlockPos()) && b.getPos().equals(blockEntity.getBlockPos())){
				return 1;
			} else {
				return 0;
			}
		});

		itemScroll = LerpedFloat.linear()
			.startWithValue(0);
	}

	@Override
	protected void init() {
		int appropriateHeight = Minecraft.getInstance()
				.getWindow()
				.getGuiScaledHeight() - 10;
		appropriateHeight -=
				Mth.positiveModulo(appropriateHeight - HEADER.getHeight() - FOOTER.getHeight(), LOWER_BODY.getHeight());
		appropriateHeight =
				Math.min(appropriateHeight, HEADER.getHeight() + FOOTER.getHeight() + LOWER_BODY.getHeight() * 17);

		setWindowSize(windowWidth = 226, windowHeight = appropriateHeight);
		super.init();
		clearWidgets();

		int x = getGuiLeft();
		int y = getGuiTop();

		lockX = x + 184;
		lockY = y + 18;

		destroyX = lockX + 16;
		destroyY = y + 18;

		switchX = x + 25;
		switchY = y + 18;


		extraAreas = new ArrayList<>();
		int leftHeight = 40;
		int rightHeight = 50;
		Consumer<String> onTextChanged;
		onTextChanged = s -> nameBox.setX(nameBoxX(s, nameBox));

		nameBox = new EditBox(new NoShadowFontWrapper(font), x + 10, y + 4, LOWER_BODY.getWidth() - 15 - 20, 10,
			Component.literal(networkName));
		nameBox.setBordered(false);
		nameBox.setMaxLength(40);
		nameBox.setTextColor(0x592424);
		nameBox.setValue(networkName);
		nameBox.setFocused(false);
		nameBox.mouseClicked(0, 0, 0);
		nameBox.setResponder(onTextChanged);
		nameBox.setX(nameBoxX(nameBox.getValue(), nameBox));
		nameBox.setEditable(isOwner);
		addRenderableWidget(nameBox);

	}

	private int nameBoxX(String s, EditBox nameBox) {
		return getGuiLeft() - 15 + LOWER_BODY.getWidth() / 2 - (Math.min(font.width(s), nameBox.getWidth())) / 2;
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		blocksScroll.tickChaser();
		playersScroll.tickChaser();
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		PoseStack ms = guiGraphics.pose();
		ms.pushPose();
		ms.translate(0, 0, -300);
		super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
		ms.popPose();
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		if (this != minecraft.screen)
			return; // stencil buffer does not cooperate with ponders gui fade out

		PoseStack ms = graphics.pose();
		ms.pushPose();
		float currentScroll = itemScroll.getValue(partialTicks);

		int x = getGuiLeft();
		int y = getGuiTop();

		// BG
		HEADER.render(graphics, x - 15, y);
		y += HEADER.getHeight();
		for (int i = 0; i < (windowHeight - HEADER.getHeight() - FOOTER.getHeight() - SEPARATION.getHeight()) / 2 / UPPER_BODY.getHeight(); i++) {
			UPPER_BODY.render(graphics, x - 15, y);
			y += UPPER_BODY.getHeight();
		}
		SEPARATION.render(graphics,x-15,y);
		y+=SEPARATION.getHeight();
		lowerBodyStartY = new AtomicInteger(y).get();
		for (int i = 0; i < (windowHeight - HEADER.getHeight() - FOOTER.getHeight() - SEPARATION.getHeight()) / 2 / LOWER_BODY.getHeight(); i++) {
			LOWER_BODY.render(graphics, x - 15, y);
			y += LOWER_BODY.getHeight();
		}
		FOOTER.render(graphics, x - 15, y);
		y = getGuiTop();


		ms.pushPose();
		renderBehaviours(graphics, mouseX, mouseY, partialTicks);
		renderPlayers(graphics, mouseX, mouseY, partialTicks,lowerBodyStartY);
		ms.popPose();


		// Render lock option
		if (isAdmin)
			(isLocked ? QOLGuiTextures.STOCK_MANAGER_LOCKED : QOLGuiTextures.STOCK_MANAGER_UNLOCKED)
				.render(graphics, lockX, lockY);

		if (isAdmin) {
			(switch (destructionLevel) {
				case MEMBERS -> QOLGuiTextures.STOCK_MANAGER_DESTRUCTION_MEMBERS;
				case ADMINS -> QOLGuiTextures.STOCK_MANAGER_DESTRUCTION_ADMINS;
				default -> QOLGuiTextures.STOCK_MANAGER_DESTRUCTION_ALLOW;
			}).render(graphics, destroyX, destroyY);
		}


		QOLGuiTextures.STOCK_MANAGER_SWITCH_NETWORK.render(graphics, switchX, switchY);
		String text = nameBox.getValue();

		if (!nameBox.isFocused() && isOwner)
			QOLGuiTextures.STOCK_MANAGER_EDIT_NAME.render(graphics, nameBoxX(text, nameBox) + font.width(text) + 5, y + 1);
		ms.popPose();


		//UIRenderHelper.swapAndBlitColor(UIRenderHelper.framebuffer, minecraft.getMainRenderTarget());
	}


	protected void renderBehaviours(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		PoseStack matrixStack = graphics.pose();
		int yOffset = getGuiTop() + HEADER.getHeight() - 8;

		float scrollOffset = -this.blocksScroll.getValue(partialTicks);

		for(int i = 0; i < behaviours.size(); ++i) {
			LogisticallyLinkedBehaviour entry = behaviours.get(i);
            int itemWindowX = leftPos + 3;
            int itemWindowY = topPos + HEADER.getHeight();
            int itemWindowX2 = itemWindowX + 210;
            int itemWindowY2 = (int) (itemWindowY + ((windowHeight - HEADER.getHeight() - FOOTER.getHeight() - SEPARATION.getHeight()) / 2f / LOWER_BODY.getHeight()) * UPPER_BODY.getHeight());

            graphics.enableScissor(itemWindowX - 5, itemWindowY, itemWindowX2 + 10, itemWindowY2);
			matrixStack.pushPose();
			matrixStack.translate(0.0F, scrollOffset, 40.0F);

			int cardHeight = this.renderBehaviourEntry(graphics, i, entry, yOffset, mouseX, mouseY, partialTicks);
			yOffset += cardHeight;
			matrixStack.popPose();
			graphics.disableScissor();
		}

	}

	public int renderBehaviourEntry(GuiGraphics graphics, int i, LogisticallyLinkedBehaviour entry, int yOffset, int mouseX, int mouseY, float partialTicks) {
		int cardWidth = 160;
		int cardHeader = 20;
		PoseStack matrixStack = graphics.pose();
		matrixStack.pushPose();
		matrixStack.translate((float)(this.leftPos + 18), (float)(this.topPos + yOffset), 0.0F);
		QOLGuiTextures.CHOOSE_NETWORK_ENTRY.render(graphics, 0, 0);
		if (Minecraft.getInstance().hitResult != null && Minecraft.getInstance().player != null)graphics.renderItem(Minecraft.getInstance().level.getBlockState(entry.getPos()).getCloneItemStack(Minecraft.getInstance().hitResult, Minecraft.getInstance().level,entry.getPos(),Minecraft.getInstance().player), 10, 1);
		Component name = Component.literal(entry.getPos().toShortString());
		graphics.drawString(this.font, name.getString(20).stripTrailing() + (name.getString(20).stripTrailing().length() > 20 ? "..." : ""), 30, 5, 6645093, false);

		if (!entry.getPos().equals(blockEntity.getBlockPos()))(canDestroyEntry(entry)? QOLGuiTextures.CHOOSE_NETWORK_DELETE: QOLGuiTextures.CHOOSE_NETWORK_DELETE_DISABLED).render(graphics, 171 ,4);
		//if (entry.owner() != null &&  Minecraft.getInstance().level != null){
		//	Component playerName = Minecraft.getInstance().level.getPlayerByUUID(entry.owner()) != null ? Minecraft.getInstance().level.getPlayerByUUID(entry.owner()).getName() :CreateQOLLang.translateDirect( "statue.unknow_owner");
		//	PlayerInfo info = Minecraft.getInstance().player.connection.getPlayerInfo(entry.owner());
		//	if (info != null){
		//		PlayerFaceRenderer.draw(graphics,info.getSkin(),166 - font.width(playerName) - 13,3,11);
		//	}
		//	graphics.drawString(this.font, playerName.getString(18).stripTrailing() + (playerName.getString().length() > 18 ? "..." : "") , 166 - font.width(playerName), 5, 6645093, false);
		//}
		matrixStack.popPose();
		return cardHeader;
	}

	private boolean canDestroyEntry(LogisticallyLinkedBehaviour behaviour){
		return mayDestroy && Minecraft.getInstance().player.blockPosition()
				.closerThan(behaviour.getPos(), CreateQOLConfigs.server().logistics.stockManagerMaxDestroyDistance.get());
	}

	protected void renderPlayers(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, int lowerBodyStart) {
		PoseStack matrixStack = graphics.pose();
		int yOffset = lowerBodyStart;

		float scrollOffset = -this.playersScroll.getValue(partialTicks);

		List<Map.Entry<UUID,NetworkPermission>> entries = new ArrayList<>(permissions.entrySet());

		for(int i = 0; i < entries.size(); ++i) {
            int itemWindowX = leftPos + 3;
            int itemWindowY = lowerBodyStart;
            int itemWindowX2 = itemWindowX + 210;
            int itemWindowY2 = (int) (itemWindowY + ((windowHeight - HEADER.getHeight() - FOOTER.getHeight() - SEPARATION.getHeight()) / 2f / LOWER_BODY.getHeight()) * UPPER_BODY.getHeight());

            graphics.enableScissor(itemWindowX - 5, itemWindowY, itemWindowX2 + 10, itemWindowY2);
			matrixStack.pushPose();
			matrixStack.translate(0.0F, scrollOffset, 0.0F);

			int cardHeight = this.renderPlayerEntry(graphics, i, entries.get(i), yOffset, mouseX, mouseY, partialTicks);
			yOffset += cardHeight;
			matrixStack.popPose();
			graphics.disableScissor();
		}

	}
	public int renderPlayerEntry(GuiGraphics graphics, int i, Map.Entry<UUID,NetworkPermission> entry, int yOffset, int mouseX, int mouseY, float partialTicks) {
		int cardWidth = 160;
		int cardHeader = 20;
		PoseStack matrixStack = graphics.pose();
		matrixStack.pushPose();
		matrixStack.translate((float)(this.leftPos + 18), (float)(this.topPos + yOffset), 0.0F);
		QOLGuiTextures.CHOOSE_NETWORK_ENTRY.render(graphics, 0, 0);

		if (Minecraft.getInstance().level != null){
			Component playerName =  Minecraft.getInstance().player != null && Minecraft.getInstance().player.connection.getPlayerInfo(entry.getKey()) != null ? Component.literal(Minecraft.getInstance().player.connection.getPlayerInfo(entry.getKey()).getProfile().getName()) :Component.empty();
			if (playerName.equals(Component.empty())){
				ProfileResult result = Minecraft.getInstance().getMinecraftSessionService().fetchProfile(entry.getKey(),false);
				if (result != null && result.profile() != null){
					playerName = Component.literal(result.profile().getName());
				} else {
					playerName = CreateQOLLang.translateDirect("gui.stock_manager.unknow_player");
				}
			}

			PlayerInfo info = Minecraft.getInstance().player.connection.getPlayerInfo(entry.getKey());
			boolean hasDrawn = false;
			if (info != null){
				PlayerFaceRenderer.draw(graphics,info.getSkin(),12,3,11);
				hasDrawn = true;
			}
			if (!hasDrawn) {
				ProfileResult result = Minecraft.getInstance().getMinecraftSessionService().fetchProfile(entry.getKey(),false);
				if (result != null && result.profile() != null){
					PlayerFaceRenderer.draw(graphics, Minecraft.getInstance().getSkinManager().getInsecureSkin(result.profile()),12,3,11);
					hasDrawn = true;
				}
			}

			if (!hasDrawn) {
				PlayerFaceRenderer.draw(graphics, DefaultPlayerSkin.get(entry.getKey()),12,3,11);
			}
			graphics.drawString(this.font, playerName.getString(23).stripTrailing() + (playerName.getString().length() > 23 ? "..." : "") , 24, 5, 6645093, false);
			graphics.drawString(this.font, CreateQOLLang.translateDirect("gui.stock_manager.permission."+entry.getValue().getSerializedName()) , 124, 5, 6645093, false);
			if (isAdmin && entry.getValue().next().equals(NetworkPermission.MEMBER)) QOLGuiTextures.CHOOSE_NETWORK_ADD_PLAYER.render(graphics, 161,4);
			if (isAdmin && entry.getValue().previous().equals(NetworkPermission.NONE)) QOLGuiTextures.CHOOSE_NETWORK_DELETE_PLAYER.render(graphics,161+13,4);

			if (isOwner && entry.getValue().next().equals(NetworkPermission.ADMIN)) QOLGuiTextures.CHOOSE_NETWORK_PROMOTE_PLAYER.render(graphics, 161,4);
			if (isOwner && entry.getValue().previous().equals(NetworkPermission.MEMBER)) QOLGuiTextures.CHOOSE_NETWORK_DEMOTE_PLAYER.render(graphics,161+13,4);
		}
		matrixStack.popPose();
		return cardHeader;
	}





	@Override
	protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.renderForeground(graphics, mouseX, mouseY, partialTicks);
		float currentScroll = itemScroll.getValue(partialTicks);

		// Render tooltip of lock option
		if (currentScroll < 1 && isAdmin && mouseX > lockX && mouseX <= lockX + 15 && mouseY > lockY
			&& mouseY <= lockY + 15) {
			graphics.renderComponentTooltip(font,
				List.of(
					CreateLang.translate(isLocked ? "gui.stock_keeper.network_locked" : "gui.stock_keeper.network_open")
						.component(),
					CreateLang.translate("gui.stock_keeper.network_lock_tip")
						.style(ChatFormatting.GRAY)
						.component(),
					CreateLang.translate("gui.stock_keeper.network_lock_tip_1")
						.style(ChatFormatting.GRAY)
						.component(),
					CreateLang.translate("gui.stock_keeper.network_lock_tip_2")
						.style(ChatFormatting.DARK_GRAY)
						.style(ChatFormatting.ITALIC)
						.component()),
				mouseX, mouseY);
		}

		if (currentScroll < 1 && isAdmin && mouseX > destroyX && mouseX <= destroyX + 15 && mouseY > destroyY
				&& mouseY <= destroyY + 15) {
			graphics.renderComponentTooltip(font,
					List.of(
							CreateQOLLang.translate("gui.stock_manager.destruction_level").add(destructionLevel.getName())
									.component(),
							CreateQOLLang.translate("gui.stock_manager.destruction_level_tip")
									.style(ChatFormatting.GRAY)
									.component(),
							CreateQOLLang.translate("gui.stock_manager.destruction_level_tip_1")
									.style(ChatFormatting.GRAY)
									.component(),
							CreateLang.translate("gui.stock_keeper.network_lock_tip_2")
									.style(ChatFormatting.DARK_GRAY)
									.style(ChatFormatting.ITALIC)
									.component()),
					mouseX, mouseY);
		}

		if (currentScroll < 1 && mouseX > switchX && mouseX <= switchX + 15 && mouseY > switchY
				&& mouseY <= switchY + 15) {
			List<Component> components = new ArrayList<>(List.of(
					CreateQOLLang.translate( "gui.stock_manager.network_switch")
							.component(),
					CreateQOLLang.translate("gui.stock_manager.network_switch_tip")
							.style(ChatFormatting.DARK_GRAY)
							.style(ChatFormatting.ITALIC)
							.component()));
			if (links <=1 ){
				components.addAll(1,List.of(CreateQOLLang.translate("gui.choose_network.delete_warning")
								.style(ChatFormatting.RED)
								.component(),
						CreateQOLLang.translate("gui.choose_network.delete_warning_1")
								.style(ChatFormatting.RED)
								.component()));
			}
			graphics.renderComponentTooltip(font,components,mouseX, mouseY);
		}

		action(graphics, mouseX, mouseY, -1);
	}



	private final Component clickToEdit = CreateQOLLang.translateDirect("gui.choose_network.lmb_manage")
			.withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);
	public boolean action(@Nullable GuiGraphics graphics, double mouseX, double mouseY, int click) {
		// Prevent actions outside the window for them
		if (mouseX < leftPos || mouseX >= leftPos + imageWidth || mouseY < topPos + 15 || mouseY >= topPos + windowHeight)
			return false;


		int mx = (int) mouseX;
		int my = (int) mouseY;
		int x = mx - leftPos - 20;
		int y = my - topPos - HEADER.getHeight();
		if (x < 0 || x >= 196)
			return false;
		if (y < 0 )
			return false;

		if (y < (( windowHeight - FOOTER.getHeight() - SEPARATION.getHeight()) / 2 / LOWER_BODY.getHeight())* UPPER_BODY.getHeight() - 20) {
			y += blocksScroll.getValue(0);

			List<LogisticallyLinkedBehaviour> entries = behaviours;
			for (int i = 0; i < entries.size(); i++) {
				LogisticallyLinkedBehaviour entry = entries.get(i);
				int cardHeight = 20;

				if (y >= cardHeight) {
					y -= cardHeight;
					if (y < 0)
						return false;
					continue;
				}

				int fieldSize = 165;
				if (x > 0 && x <= 30 && y > 0 && y <= 16) {
					if (Minecraft.getInstance().hitResult != null && Minecraft.getInstance().player != null) {
						ItemStack stack = Minecraft.getInstance().level.getBlockState(entry.getPos()).getCloneItemStack(Minecraft.getInstance().hitResult, Minecraft.getInstance().level, entry.getPos(), Minecraft.getInstance().player);

						if (graphics != null) graphics.renderTooltip(font,stack, (int) mouseX, (int) mouseY);
						return true;
					}
				}

				if (x > fieldSize && x <= fieldSize + 16 && y > 0 && y <= 16) {
					if (mayDestroy)
					{
						if (Minecraft.getInstance().player.blockPosition()
								.closerThan(entry.getPos(), CreateQOLConfigs.server().logistics.stockManagerMaxDestroyDistance.get())){
							renderActionTooltip(graphics, ImmutableList.of(CreateQOLLang.translate("gui.stock_manager.break_block")
									.component()), mx, my);
							if (click == 0) {
								CatnipServices.NETWORK.sendToServer(new DestroyLogisticsNetworkComponentPacket(menu.contentHolder.getBlockPos(), entry.getPos()));
								behaviours.remove(entry);
							}
							return true;
						} else {
							renderActionTooltip(graphics, ImmutableList.of(
									CreateQOLLang.translate("gui.stock_manager.too_far")
											.style(ChatFormatting.RED)
											.component(),
									CreateQOLLang.translate("gui.stock_manager.too_far_1")
											.style(ChatFormatting.RED)
											.component()
							), mx, my);
					}
					}else {
						renderActionTooltip(graphics, ImmutableList.of(
								CreateQOLLang.translate("gui.stock_manager.destroy_no_permission")
										.style(ChatFormatting.RED)
										.component(),
								CreateQOLLang.translate("gui.stock_manager.destroy_no_permission_1")
										.style(ChatFormatting.RED)
										.component()
						), mx, my);
					}
				}

				x -= 18;
				y -= 28;

				if (x < 0 || y < 0 || x > 160)
					return false;
			}
		}

		x = mx - leftPos - 20;
		y = my - topPos - lowerBodyStartY;
		if (x < 0 || x >= 196)
			return false;
		if (y < 0)
			return false;

		y += playersScroll.getValue(0); // Scroll spécifique au bas si existant

		List<Map.Entry<UUID,NetworkPermission>> entries = new ArrayList<>(permissions.entrySet());
		for (int i = 0; i < entries.size(); i++) {
			Map.Entry<UUID, NetworkPermission> entry = entries.get(i);
			int cardHeight = 20;

			if (y >= cardHeight) {
				y -= cardHeight;
				if (y < 0)
					return false;
				continue;
			}

			int fieldSize = 165;
			if (x > 0 && x <= 30 && y > 0 && y <= 16) {
				//if (Minecraft.getInstance().hitResult != null && Minecraft.getInstance().player != null) {
				//	ItemStack stack = Minecraft.getInstance().level.getBlockState(entry.getPos()).getCloneItemStack(Minecraft.getInstance().hitResult, Minecraft.getInstance().level, entry.getPos(), Minecraft.getInstance().player);
				//	if (graphics != null) graphics.renderTooltip(font, stack, (int) mouseX, (int) mouseY);
				//	return true;
				//}
			}

			if (x > fieldSize - 9  && x <= fieldSize + 4 && y > 0 && y <= 16) {
				NetworkPermission next = entry.getValue().next();
				if (next == NetworkPermission.MEMBER && isAdmin) {
					renderActionTooltip(graphics, ImmutableList.of(CreateQOLLang.translate("gui.stock_manager.add_user").component()), mx, my);
					if (click == 0){
						permissions.put(entry.getKey(),next);
						CatnipServices.NETWORK.sendToServer(new ModifyPlayerNetworkPermissionPacket(blockEntity.getBlockPos(),entry.getKey(),next));
					}
					return true;
				}
				if (next == NetworkPermission.ADMIN && isOwner){
					renderActionTooltip(graphics, ImmutableList.of(CreateQOLLang.translate("gui.stock_manager.promote_to").component().append(CreateQOLLang.translateDirect("gui.stock_manager.permission."+next.getSerializedName()))), mx, my);
					if (click == 0){
						permissions.put(entry.getKey(),next);
						CatnipServices.NETWORK.sendToServer(new ModifyPlayerNetworkPermissionPacket(blockEntity.getBlockPos(),entry.getKey(),next));
					}
					return true;
				}
			}

			if (x > fieldSize + 4 && x <= fieldSize + 16 && y > 0 && y <= 16) {
				NetworkPermission previous = entry.getValue().previous();
				if (previous == NetworkPermission.NONE && isAdmin) {
					renderActionTooltip(graphics, ImmutableList.of(CreateQOLLang.translate("gui.stock_manager.remove_user").component()), mx, my);
					if (click == 0){
						permissions.put(entry.getKey(),previous);
						CatnipServices.NETWORK.sendToServer(new ModifyPlayerNetworkPermissionPacket(blockEntity.getBlockPos(),entry.getKey(),previous));
					}
					return true;
				} else if (previous == NetworkPermission.MEMBER && isOwner){
					renderActionTooltip(graphics, ImmutableList.of(CreateQOLLang.translate("gui.stock_manager.demote_to").component().append(CreateQOLLang.translateDirect("gui.stock_manager.permission."+previous.getSerializedName()))), mx, my);
					if (click == 0){
						permissions.put(entry.getKey(),previous);
						CatnipServices.NETWORK.sendToServer(new ModifyPlayerNetworkPermissionPacket(blockEntity.getBlockPos(),entry.getKey(),previous));
					}
					return true;
				}
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
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
		boolean lmb = pButton == GLFW.GLFW_MOUSE_BUTTON_LEFT;
		boolean rmb = pButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT;

		// Scroll bar
		int barX = 1;
		if (getMaxScroll() > 0 && lmb && pMouseX > barX && pMouseX <= barX + 8 && pMouseY > getGuiTop() + 15
			&& pMouseY < getGuiTop() + windowHeight - 82) {
			scrollHandleActive = true;
			if (minecraft.isWindowActive())
				GLFW.glfwSetInputMode(minecraft.getWindow()
					.getWindow(), 208897, GLFW.GLFW_CURSOR_HIDDEN);
			return true;
		}


		// Lock
		if (isAdmin && itemScroll.getChaseTarget() == 0 && lmb && pMouseX > lockX && pMouseX <= lockX + 15
			&& pMouseY > lockY && pMouseY <= lockY + 15) {
			isLocked = !isLocked;
			CatnipServices.NETWORK.sendToServer(new StockManagerLockPacket(blockEntity.getBlockPos(), isLocked));
			playUiSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
			return true;
		}

		// Destruction Level
		if (isAdmin && itemScroll.getChaseTarget() == 0 && lmb && pMouseX > destroyX && pMouseX <= destroyX + 15
				&& pMouseY > destroyY && pMouseY <= destroyY + 15) {
			destructionLevel = destructionLevel.next();
			CatnipServices.NETWORK.sendToServer(new StockManagerDestructionLevelPacket(blockEntity.getBlockPos(), destructionLevel));
			playUiSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
			return true;
		}

		// Switch
		if (itemScroll.getChaseTarget() == 0 && lmb && pMouseX > switchX && pMouseX <= switchX + 15
				&& pMouseY > switchY && pMouseY <= switchY + 15) {
			CatnipServices.NETWORK.sendToServer(new OpenSwitchLogisticNetworkScreenPacket(blockEntity.getBlockPos()));
			playUiSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
			return true;
		}
		if (isOwner) {
			if (!nameBox.isFocused() && pMouseY > getGuiTop() && pMouseY < getGuiTop() + 14 && pMouseX > getGuiLeft()
					&& pMouseX < getGuiLeft() + HEADER.getWidth()) {
				nameBox.setFocused(true);
				nameBox.setHighlightPos(0);
				setFocused(nameBox);
				return true;
			}
		}

		if (action(null, pMouseX, pMouseY, pButton)) {
			playUiSound(SoundEvents.UI_BUTTON_CLICK.value(), 1f, 1f);
			return true;
		}
		return true;
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
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (mouseY < lowerBodyStartY) {
			float chaseTarget = this.blocksScroll.getChaseTarget();
			float max = (float) (40 - (3 + AllGuiTextures.STOCK_KEEPER_CATEGORY.getHeight() * 4));
			max += (float) (behaviours.size() * 20 + 24);
			if (max > 0.0F) {
				chaseTarget -= (float) (scrollY * (double) 12.0F);
				chaseTarget = Mth.clamp(chaseTarget, 0.0F, max);
				this.blocksScroll.chase((double) ((int) chaseTarget), (double) 0.7F, Chaser.EXP);
			} else {
				this.blocksScroll.chase((double) 0.0F, (double) 0.7F, Chaser.EXP);
			}
		} else {
			float chaseTarget = this.playersScroll.getChaseTarget();
			float max = (float) (40 - (3 + AllGuiTextures.STOCK_KEEPER_CATEGORY.getHeight() * 4));
			max += (float) (permissions.size() * 20 + 24);
			if (max > 0.0F) {
				chaseTarget -= (float) (scrollY * (double) 12.0F);
				chaseTarget = Mth.clamp(chaseTarget, 0.0F, max);
				this.playersScroll.chase((double) ((int) chaseTarget), (double) 0.7F, Chaser.EXP);
			} else {
				this.playersScroll.chase((double) 0.0F, (double) 0.7F, Chaser.EXP);
			}

		}

		return true;
	}

	private void clampScrollBar() {
		int maxScroll = getMaxScroll();
		float prevTarget = itemScroll.getChaseTarget();
		float newTarget = Mth.clamp(prevTarget, 0, maxScroll);
		if (prevTarget != newTarget)
			itemScroll.startWithValue(newTarget);
	}

	private int getMaxScroll() {
		int visibleHeight = windowHeight - 84;
		int totalRows = 2;
		int maxScroll = (int) Math.max(0, (totalRows /** rowHeight */- visibleHeight + 50)/* / rowHeight*/);
		return maxScroll;
	}

	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
		if (pButton != GLFW.GLFW_MOUSE_BUTTON_LEFT || !scrollHandleActive)
			return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);

		Window window = minecraft.getWindow();
		double scaleX = window.getGuiScaledWidth() / (double) window.getScreenWidth();
		double scaleY = window.getGuiScaledHeight() / (double) window.getScreenHeight();

		int windowH = windowHeight - 92;
		int totalH = getMaxScroll() /** rowHeight*/ + windowH;
		int barSize = Math.max(5, Mth.floor((float) windowH / totalH * (windowH - 2)));

		int minY = getGuiTop() + 15 + barSize / 2;
		int maxY = getGuiTop() + 15 + windowH - barSize / 2;

		if (barSize >= windowH - 2)
			return true;

		int barX = /*itemsX + cols * colWidth*/0;
		double target = (pMouseY - getGuiTop() - 15 - barSize / 2.0) * totalH / (windowH - 2) /*/ rowHeight*/;
		itemScroll.chase(Mth.clamp(target, 0, getMaxScroll()), 0.8, Chaser.EXP);

		if (minecraft.isWindowActive()) {
			double forceX = (barX + 2) / scaleX;
			double forceY = Mth.clamp(pMouseY, minY, maxY) / scaleY;
			GLFW.glfwSetCursorPos(window.getWindow(), forceX, forceY);
		}

		return true;
	}


	@Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
		boolean hitEnter = getFocused() instanceof EditBox
				&& (pKeyCode == InputConstants.KEY_RETURN || pKeyCode == InputConstants.KEY_NUMPADENTER);

		if (hitEnter && nameBox.isFocused()) {
			nameBox.setFocused(false);
			if (!nameBox.getValue()
					.equals(networkName)) {
				CatnipServices.NETWORK.sendToServer(
						new ModifyLogisticsNetworkPacket(blockEntity.getBlockPos(), nameBox.getValue()));
				networkName = nameBox.getValue();
				return true;
			}
		}

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

	private record PlayerEntry(UUID player, Component name, NetworkPermission permission){}

}
