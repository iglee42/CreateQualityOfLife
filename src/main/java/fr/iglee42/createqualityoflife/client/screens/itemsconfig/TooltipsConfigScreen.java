package fr.iglee42.createqualityoflife.client.screens.itemsconfig;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.foundation.gui.AllIcons;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.client.screens.widgets.ArmorConfigScreenList;
import fr.iglee42.createqualityoflife.client.screens.widgets.entries.BooleanEntry;
import fr.iglee42.createqualityoflife.client.screens.widgets.entries.ValueEntry;
import fr.iglee42.createqualityoflife.packets.ChangeItemComponentPacket;
import fr.iglee42.createqualityoflife.packets.ChangeItemTooltipsPacket;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.createmod.catnip.animation.Force;
import net.createmod.catnip.animation.PhysicalFloat;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.DelegatedStencilElement;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.element.RenderElement;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.createmod.catnip.gui.widget.BoxWidget;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.ponder.enums.PonderGuiTextures;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.opengl.GL30;

import javax.annotation.Nonnull;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class TooltipsConfigScreen extends AbstractSimiScreen {

	public static final PhysicalFloat cogSpin = PhysicalFloat.create().withLimit(10f).withDrag(0.3).addForce(new Force.Static(.2f));

	public static DelegatedStencilElement shadowElement = new DelegatedStencilElement(
			(graphics, x, y, alpha) -> renderCog(graphics),
			(graphics, x, y, alpha) -> graphics.fill(-200, -200, 200, 200, 0x60_000000)
	);
	private final ItemStack item;
	private final int itemSlot;

	protected ArmorConfigScreenList list;
	protected int listWidth;



	public TooltipsConfigScreen(int slot) {
		this.itemSlot = slot;
		this.item = Minecraft.getInstance().player.getInventory().getItem(slot);
	}

	@Override
	protected void init() {
		super.init();
		listWidth = Math.min(width - 80, 300);

		int yCenter = height / 2;
		int listL = this.width / 2 - listWidth / 2;
		int listR = this.width / 2 + listWidth / 2;

		list = new ArmorConfigScreenList(minecraft, listWidth, height - 80, 35, 40,this);
		list.setX(listL);

		addRenderableWidget(list);

		list.children().clear();
		BoxWidget goBack = new BoxWidget(width / 2 - listWidth / 2 - 30, height / 2 - 30, 20, 20).withPadding(2, 2)
				.withCallback(() -> ScreenOpener.open(new ItemConfigScreen(itemSlot)));
		goBack.showingElement(PonderGuiTextures.ICON_CONFIG_BACK.asStencil()
				.withElementRenderer(BoxWidget.gradientFactory.apply(goBack)));
		goBack.getToolTip()
				.add(Component.literal("Go Back"));
		addRenderableWidget(goBack);

		ItemTooltips tooltips = item.getOrDefault(QOLDataComponents.ITEM_TOOLTIPS,ItemTooltips.DEFAULT);

		for (ItemTooltips.Tooltip t : ItemTooltips.Tooltip.values()) {
			list.children().add(new TooltipEntry(t.getDisplayName(),tooltips.isEnable(t),t,"Disable " + t.getDisplayName() + " in the item's tooltips"));
		}

		BoxWidget enableAll = new BoxWidget(width / 2 + listWidth / 2 + 8, height / 2 - 45, 20, 20).withPadding(2, 2)
				.withCallback(() -> {
					list.children().stream().map(TooltipEntry.class::cast).forEach(e->e.setValue(true));
				});
		enableAll.showingElement(AllIcons.I_CONFIRM.asStencil()
				.withElementRenderer(BoxWidget.gradientFactory.apply(enableAll)));
		enableAll.getToolTip()
				.add(Component.literal("Enable All"));
		addRenderableWidget(enableAll);


		BoxWidget disableAll = new BoxWidget(width / 2 + listWidth / 2 + 8, height / 2 - 15, 20, 20).withPadding(2, 2)
				.withCallback(() -> {
					list.children().stream().map(TooltipEntry.class::cast).forEach(e->e.setValue(false));
				});
		disableAll.showingElement(AllIcons.I_DISABLE.asStencil()
				.withElementRenderer(BoxWidget.gradientFactory.apply(disableAll)));
		disableAll.getToolTip()
				.add(Component.literal("Disable All"));
		addRenderableWidget(disableAll);



	}

	@Override
	public void tick() {
		super.tick();
		cogSpin.tick();
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

	@Override
	protected void renderWindowBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		graphics.fill(0, 0, this.width, this.height, 0xb0_282c34);

		shadowElement
				.at(width * 0.5f, height * 0.5f, 0)
				.render(graphics);

		super.renderWindowBackground(graphics, mouseX, mouseY, partialTicks);

	}

	@Override
	protected void prepareFrame() {
		UIRenderHelper.swapAndBlitColor(minecraft.getMainRenderTarget(), UIRenderHelper.framebuffer);
		RenderSystem.clear(GL30.GL_STENCIL_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
	}

	@Override
	protected void endFrame() {
		UIRenderHelper.swapAndBlitColor(UIRenderHelper.framebuffer, minecraft.getMainRenderTarget());
	}

	@Override
	protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		int x = width / 2;

		graphics.drawCenteredString(minecraft.font, Component.literal("Configure " ).append(item.getHoverName()).append(" Tooltips") , x, 15, UIRenderHelper.COLOR_TEXT.getFirst().getRGB());

	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		cogSpin.bump(3, -scrollY * 5);

		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	protected static void renderCog(GuiGraphics graphics) {
		float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
		PoseStack poseStack = graphics.pose();
		poseStack.pushPose();

		poseStack.translate(-100, 100, -100);
		poseStack.scale(200, 200, 1);
		GuiGameElement.of(AllBlocks.LARGE_COGWHEEL.getDefaultState().setValue(CogWheelBlock.AXIS, Direction.Axis.Y))
			.rotateBlock(22.5, cogSpin.getValue(partialTicks), 22.5)
			.render(graphics);

		poseStack.popPose();
	}

	public ItemStack getItem() {
		return item;
	}

	public int getItemSlot() {
		return itemSlot;
	}


	@Override
	public void resize(Minecraft p_96575_, int p_96576_, int p_96577_) {
		super.resize(p_96575_, p_96576_, p_96577_);
	}

	public class TooltipEntry extends ArmorConfigScreenList.LabeledEntry {

		protected boolean value;
		protected List<String> commentLines = new ArrayList<>(List.of("."));
		protected ItemTooltips.Tooltip tooltip;

		RenderElement enabled;
		RenderElement disabled;
		BoxWidget button;

		public TooltipEntry(String label, boolean value, ItemTooltips.Tooltip tooltip, String... comments) {
			super(label);
			this.value = value;
			this.tooltip = tooltip;

			labelTooltip.add(Component.literal(label).withStyle(ChatFormatting.WHITE));

			commentLines.addAll(Arrays.stream(comments).toList());

			labelTooltip.addAll(commentLines.stream()
					.map(s -> s.equals(".") ? " " : s)
					.map(Component::literal)
					.flatMap(stc -> FontHelper.cutTextComponent(stc, FontHelper.Palette.GRAY_AND_RED).stream())
					.toList()
			);

			enabled = PonderGuiTextures.ICON_CONFIRM.asStencil()
					.withElementRenderer((ms, width, height, alpha) -> UIRenderHelper.angledGradient(ms, 0, 0, height / 2, height, width, AbstractSimiWidget.COLOR_SUCCESS))
					.at(10, 0);

			disabled = PonderGuiTextures.ICON_DISABLE.asStencil()
					.withElementRenderer((ms, width, height, alpha) -> UIRenderHelper.angledGradient(ms, 0, 0, height / 2, height, width, AbstractSimiWidget.COLOR_FAIL))
					.at(10, 0);

			button = new BoxWidget().showingElement(enabled)
					.withCallback(() -> setValue(!getValue()));

			listeners.add(button);
			onReset();
		}


		@Override
		public void render(GuiGraphics graphics, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean p_230432_9_, float partialTicks) {
			super.render(graphics, index, y, x, width, height, mouseX, mouseY, p_230432_9_, partialTicks);
			button.setX(x + width - 80 - 28);
			button.setY(y + 10);
			button.setWidth(35);
			button.setHeight(height - 20);
			button.updateGradientFromState();
			button.render(graphics, mouseX, mouseY, partialTicks);
		}

		@Override
		protected int getLabelWidth(int totalWidth) {
			return (int) (totalWidth * labelWidthMult) + 30;
		}

		public void setValue(@Nonnull boolean value) {
			this.value = value;
			onValueChange(value);
		}

		@Nonnull
		public boolean getValue() {
			return value;
		}


		public void onReset() {
			onValueChange(getValue());
		}

		public void onValueChange() {
			onValueChange(getValue());
		}
		public void onValueChange(boolean newValue) {
			button.showingElement(newValue ? enabled : disabled);
			bumpCog(newValue ? 15f : -16f);
			if (Minecraft.getInstance().screen == null) {
				CreateQOL.LOGGER.error("Cannot change component on a ValueEntry because the screen is null");
				return;
			}
			int slot = getItemSlot();
			PacketDistributor.sendToServer(new ChangeItemTooltipsPacket(slot, value, tooltip));
		}

		protected void bumpCog() {bumpCog(10f);}
		protected void bumpCog(float force) {
			ItemConfigScreen.cogSpin.bump(3, force);
		}
	}

}
