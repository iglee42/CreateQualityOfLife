package fr.iglee42.createqualityoflife.client.screens.itemsconfig;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import fr.iglee42.createqualityoflife.client.screens.widgets.ArmorConfigScreenList;
import fr.iglee42.createqualityoflife.client.screens.widgets.entries.BooleanEntry;
import fr.iglee42.createqualityoflife.client.screens.widgets.entries.ValueEntry;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.createmod.catnip.animation.Force;
import net.createmod.catnip.animation.PhysicalFloat;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.DelegatedStencilElement;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.widget.BoxWidget;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.ponder.enums.PonderGuiTextures;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL30;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemConfigScreen extends AbstractSimiScreen {

	public static final PhysicalFloat cogSpin = PhysicalFloat.create().withLimit(10f).withDrag(0.3).addForce(new Force.Static(.2f));

	public static DelegatedStencilElement shadowElement = new DelegatedStencilElement(
			(graphics, x, y, alpha) -> renderCog(graphics),
			(graphics, x, y, alpha) -> graphics.fill(-200, -200, 200, 200, 0x60_000000)
	);
	private final ItemStack item;
	private final int itemSlot;

	protected ArmorConfigScreenList list;
	protected int listWidth;

	private List<Integer> armors;



	public ItemConfigScreen(int slot) {
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

		list = new ArmorConfigScreenList(minecraft, listWidth, height - 80, 35,height-45 ,40,this);
		list.setLeftPos(listL);

		addRenderableWidget(list);

		list.children().clear();
		BoxWidget goBack = new BoxWidget(width / 2 - listWidth / 2 - 30, height / 2 - 30, 20, 20).withPadding(2, 2)
				.withCallback(() -> ScreenOpener.open(new InventoryConfigScreen()));
		goBack.showingElement(PonderGuiTextures.ICON_CONFIG_BACK.asStencil()
				.withElementRenderer(BoxWidget.gradientFactory.apply(goBack)));
		goBack.getToolTip()
				.add(Component.literal("Go Back"));
		addRenderableWidget(goBack);

		if (item.getItem() instanceof QOLConfigurableItem) list.children().add(new TooltipButtonEntry("Tooltips","Open the config menu to choose which tooltips are displayed"));

		if (item.getItem() instanceof QOLConfigurableItem configurableItem){
			try {
				List<QOLConfigurableItem.Configuration<?>> configurations = configurableItem.getConfigurations(item);
				list.children().addAll(configurations.stream().map(c->c.type().getWidget(c)).toList());
			} catch (InvalidClassException e) {
				throw new RuntimeException(e);
			}

		}

	}

	@Override
	public void tick() {
		super.tick();
		cogSpin.tick();

		list.children().stream()
				.filter(e -> e instanceof ValueEntry<?>)
				.map(e -> (ValueEntry<?>) e)
				.forEach(entry -> {
					List<ValueEntry<?>> entries = (List<ValueEntry<?>>) (List<?>) list.children().stream()
							.filter(e -> e instanceof ValueEntry)
							.map(e -> (ValueEntry<?>) e)
							.toList();
					if (entry.isEditable() && !entry.getEnableFunction().apply(entry, entries)) {
						if (entry instanceof BooleanEntry be) be.setValue(false);
					}
					entry.setEditable(entry.getEnableFunction().apply(entry, entries));
				});
	}

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

		graphics.drawCenteredString(minecraft.font, Component.literal("Configure " ).append(item.getHoverName()) , x, 15, UIRenderHelper.COLOR_TEXT.getFirst().getRGB());

	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
		cogSpin.bump(3, -scroll * 5);

		return super.mouseScrolled(mouseX,mouseY, scroll);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	protected static void renderCog(GuiGraphics graphics) {
		float partialTicks = Minecraft.getInstance().getPartialTick();
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

	public class TooltipButtonEntry extends ArmorConfigScreenList.LabeledEntry {

		protected List<String> commentLines = new ArrayList<>(List.of("."));
		BoxWidget button;

		public TooltipButtonEntry(String label, String... comments) {
			super(label);

			labelTooltip.add(Component.literal(label).withStyle(ChatFormatting.WHITE));

			commentLines.addAll(Arrays.stream(comments).toList());

			labelTooltip.addAll(commentLines.stream()
					.map(s -> s.equals(".") ? " " : s)
					.map(Component::literal)
					.flatMap(stc -> FontHelper.cutTextComponent(stc, FontHelper.Palette.GRAY_AND_RED).stream())
					.toList()
			);

			button = new BoxWidget()
					.withCallback(() -> ScreenOpener.open(new TooltipsConfigScreen(itemSlot)));
			button.showingElement(PonderGuiTextures.ICON_CONFIG_OPEN.asStencil().withElementRenderer(BoxWidget.gradientFactory.apply(button)).at(10,0));

			listeners.add(button);
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
	}
}
