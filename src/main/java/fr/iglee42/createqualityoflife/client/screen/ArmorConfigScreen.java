package fr.iglee42.createqualityoflife.client.screen;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import fr.iglee42.createqualityoflife.client.screen.widgets.ArmorConfigScreenList;
import fr.iglee42.createqualityoflife.client.screen.widgets.ItemButton;
import fr.iglee42.createqualityoflife.client.screen.widgets.entries.BooleanEntry;
import fr.iglee42.createqualityoflife.client.screen.widgets.entries.EnumEntry;
import fr.iglee42.createqualityoflife.items.ShadowRadianceChestplate;
import fr.iglee42.createqualityoflife.registries.ModArmorMaterials;
import fr.iglee42.createqualityoflife.registries.ModDataComponents;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import net.createmod.catnip.config.ui.ConfigScreen;
import net.createmod.catnip.config.ui.ConfigScreenList;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL30;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.DelegatedStencilElement;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.animation.Force;
import net.createmod.catnip.animation.PhysicalFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ArmorConfigScreen extends AbstractSimiScreen {

	public static final PhysicalFloat cogSpin = PhysicalFloat.create().withLimit(10f).withDrag(0.3).addForce(new Force.Static(.2f));

	public static DelegatedStencilElement shadowElement = new DelegatedStencilElement(
			(graphics, x, y, alpha) -> renderCog(graphics),
			(graphics, x, y, alpha) -> graphics.fill(-200, -200, 200, 200, 0x60_000000)
	);

	protected ArmorConfigScreenList list;
	protected int listWidth;

	private int selectedItem = -1;
	private List<Integer> armors;



	public ArmorConfigScreen() {
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

		armors = new ArrayList<>();

		Minecraft.getInstance().player.getInventory().armor.forEach(it->{
			if (!(it.getItem() instanceof ArmorItem))return;
			if (((ArmorItem)it.getItem()).getMaterial().equals(ModArmorMaterials.SHADOW_RADIANCE)) armors.add(Minecraft.getInstance().player.getInventory().armor.indexOf(it));
		});
		armors = armors.reversed();
		for (int index = 0; index < armors.size(); index++) {
			int finalIndex = index;
			addRenderableWidget(new ItemButton(listL - 24,35 + list.getHeight() / 2 +(( index - 2) * 30), btn->{
				onSelectedChange(finalIndex);
			},finalIndex).showing(
					Minecraft.getInstance().player.getInventory().getArmor(armors.get(index))));
		}

	}

	@Override
	public void tick() {
		super.tick();
		cogSpin.tick();

		children().stream().filter(w->w instanceof ItemButton).forEach(btn->{
			((ItemButton) btn).setActive(((ItemButton) btn).getIndex() != selectedItem);
		});
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

		graphics.drawCenteredString(minecraft.font,"Configure Shadow Radiance Armor" , x, 15, UIRenderHelper.COLOR_TEXT.getFirst().getRGB());

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

	public List<Integer> getArmors() {
		return armors;
	}

	public int getSelectedItem() {
		return selectedItem;
	}

	public void onSelectedChange(int index){
		this.selectedItem = index;
		list.children().clear();
		if (selectedItem == -1) return;
		ItemStack armor = Minecraft.getInstance().player.getInventory().getArmor(armors.get(selectedItem));

		switch (((ArmorItem)armor.getItem()).getType()){
			case HELMET -> list.children().add(new BooleanEntry("Enable Goggles", armor.getOrDefault(ModDataComponents.HELMET_GOGGLES,true),ModDataComponents.HELMET_GOGGLES));
			case CHESTPLATE -> {
				if (ShadowRadianceChestplate.hasPropeller(armor)){
					list.children().add(new BooleanEntry("Enable Fans", armor.getOrDefault(ModDataComponents.BACKTANK_FANS,true),ModDataComponents.BACKTANK_FANS));
					list.children().add(new BooleanEntry("Enable Hover", armor.getOrDefault(ModDataComponents.BACKTANK_HOVER,false),ModDataComponents.BACKTANK_HOVER));
				}
			}
			case BOOTS -> {
				list.children().add(new BooleanEntry("Enable Diving", armor.getOrDefault(ModDataComponents.BOOTS_DIVING,false),ModDataComponents.BOOTS_DIVING));
				list.children().add(new BooleanEntry("Enable Lava Walking", armor.getOrDefault(ModDataComponents.BOOTS_LAVA,true),ModDataComponents.BOOTS_LAVA));

			}
			default -> {}
		}
		list.children().add(new BooleanEntry("Apply Potion Effect",armor.getOrDefault(ModDataComponents.ARMOR_EFFECT,true),ModDataComponents.ARMOR_EFFECT));
		list.children().add(new EnumEntry("Render Type",armor.getOrDefault(ModDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL),
				ModDataComponents.ARMOR_RENDER_TYPE){
			@Override
			protected void cycleValue(int direction) {
				List<Integer> armors = ((ArmorConfigScreen)Minecraft.getInstance().screen).getArmors();
				int selected = ((ArmorConfigScreen)Minecraft.getInstance().screen).getSelectedItem();
				ArmorItem item = (ArmorItem) Minecraft.getInstance().player.getInventory().getArmor(armors.get(selected)).getItem();
				ArmorRenderType e = (ArmorRenderType) getValue();
				ArmorRenderType[] options = Arrays.stream(ArmorRenderType.values()).filter(it->it.canBeSelected(item)).toArray(ArmorRenderType[]::new);
				e = options[Math.floorMod(e.ordinal() + direction, options.length)];
				setValue(e);
				bumpCog(direction * 15f);
			}
		});


	}
}
