package fr.iglee42.createqualityoflife.client.screens;

import com.google.common.collect.Lists;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.client.screens.widgets.ArmorConfigScreenList;
import fr.iglee42.createqualityoflife.client.screens.widgets.ItemButton;
import fr.iglee42.createqualityoflife.client.screens.widgets.ItemConfigButton;
import fr.iglee42.createqualityoflife.client.screens.widgets.entries.BooleanEntry;
import fr.iglee42.createqualityoflife.client.screens.widgets.entries.EnumEntry;
import fr.iglee42.createqualityoflife.client.screens.widgets.entries.ValueEntry;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.items.ShadowRadianceChestplate;
import fr.iglee42.createqualityoflife.registries.ModArmorMaterials;

import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import net.createmod.catnip.config.ui.ConfigScreen;
import net.createmod.catnip.config.ui.ConfigScreenList;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

		list = new ArmorConfigScreenList(minecraft, listWidth, height - 80, 35, height - 45,40,this);
		list.setLeftPos(listL);

		addRenderableWidget(list);

		armors = new ArrayList<>();

		Minecraft.getInstance().player.getInventory().armor.forEach(it->{
			if (!(it.getItem() instanceof ArmorItem))return;
			if (((ArmorItem)it.getItem()).getMaterial().equals(ModArmorMaterials.SHADOW_RADIANCE)) armors.add(Minecraft.getInstance().player.getInventory().armor.indexOf(it));
		});
		armors = Lists.reverse(armors);
		for (int index = 0; index < armors.size(); index++) {
			int finalIndex = index;
			addRenderableWidget(new ItemConfigButton(listL - 24,35 + list.getHeight() / 2 +(( index - 2) * 30), btn->{
				onSelectedChange(finalIndex);
			},finalIndex).showing(
					Minecraft.getInstance().player.getInventory().getArmor(armors.get(index))));
		}

	}

	@Override
	public void tick() {
		super.tick();
		cogSpin.tick();

		children().stream().filter(w->w instanceof ItemConfigButton).forEach(btn->{
			((ItemConfigButton) btn).setActive(((ItemConfigButton) btn).getIndex() != selectedItem);
		});

		list.children().stream()
				.filter(e->e instanceof ValueEntry<?>)
				.map(e->(ValueEntry<?>)e)
				.forEach(entry->{
			if (entry.getNbtKey().equals(NBTConstants.NBT_GOGGLES)){
				if (!CreateQOLConfigs.server().helmetHaveGoggles.get() && entry.isEditable()){
					((BooleanEntry)entry).setValue(false);
				}
				entry.setEditable(CreateQOLConfigs.server().helmetHaveGoggles.get());
			}
			if (entry.getNbtKey().equals(NBTConstants.NBT_FANS)){
				if (!CreateQOLConfigs.server().propellersAllowed.get() && entry.isEditable()){
					((BooleanEntry)entry).setValue(false);
				}
				entry.setEditable(CreateQOLConfigs.server().propellersAllowed.get());
			}
			if (entry.getNbtKey().equals(NBTConstants.NBT_HOVER)){
				if ((!CreateQOLConfigs.server().hoverAllowed.get() || !CreateQOLConfigs.server().propellersAllowed.get() )&& entry.isEditable()){
					((BooleanEntry)entry).setValue(false);
				}
				entry.setEditable(CreateQOLConfigs.server().propellersAllowed.get() && CreateQOLConfigs.server().hoverAllowed.get());
			}

			if (entry.getNbtKey().equals(NBTConstants.NBT_ELYTRA_STATE)){
				if (!CreateQOLConfigs.server().elytraAllowed.get()&& entry.isEditable()){
					((BooleanEntry)entry).setValue(false);
				}
				entry.setEditable(CreateQOLConfigs.server().elytraAllowed.get());
			}

			if (entry.getNbtKey().equals(NBTConstants.NBT_EFFECTS)){
				if (!CreateQOLConfigs.server().armorEffects.get()&& entry.isEditable()){
					((BooleanEntry)entry).setValue(false);
				}
				entry.setEditable(CreateQOLConfigs.server().armorEffects.get());
			}

			if (entry.getNbtKey().equals(NBTConstants.NBT_DIVING)){
				if (!CreateQOLConfigs.server().bootsDiving.get()&& entry.isEditable()){
					((BooleanEntry)entry).setValue(false);
				}
				entry.setEditable(CreateQOLConfigs.server().bootsDiving.get());
			}
			if (entry.getNbtKey().equals(NBTConstants.NBT_LAVA)){
				if (!CreateQOLConfigs.server().bootsLavaWalking.get()&& entry.isEditable()){
					((BooleanEntry)entry).setValue(false);
				}
				entry.setEditable(CreateQOLConfigs.server().bootsLavaWalking.get());
			}
		});
	}

	@Override
	public void renderBackground(GuiGraphics p_283688_) {
		super.renderBackground(p_283688_);
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

		graphics.drawCenteredString(minecraft.font,"Configure Shadow Radiance Armor" , x, 15, UIRenderHelper.COLOR_TEXT.getFirst().getRGB());

	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		cogSpin.bump(3, -delta * 5);

		return super.mouseScrolled(mouseX, mouseY, delta);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	protected static void renderCog(GuiGraphics graphics) {
		float partialTicks = Minecraft.getInstance().getFrameTime();
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
			case HELMET -> list.children().add(new BooleanEntry("Enable Goggles", NBTConstants.getOrDefault(armor,NBTConstants.NBT_GOGGLES,true),NBTConstants.NBT_GOGGLES,
                    "Should engineer's goggle's information be displayed"));
			case CHESTPLATE -> {
				list.children().add(new BooleanEntry("Enable Custom Arms", NBTConstants.getOrDefault(armor,NBTConstants.NBT_ARMS,true),NBTConstants.NBT_ARMS,
						"Should the player's arms be replaced with the armor in first person"));

				if (ShadowRadianceChestplate.hasPropeller(armor)){
					list.children().add(new BooleanEntry("Enable Fans", NBTConstants.getOrDefault(armor,NBTConstants.NBT_FANS,true),NBTConstants.NBT_FANS,
                            "Activate the propeller on the backtank"));
					list.children().add(new BooleanEntry("Enable Hover",NBTConstants.getOrDefault(armor,NBTConstants.NBT_HOVER,false),NBTConstants.NBT_HOVER,
                            "Activate the hover mode"));
				}

				if (ShadowRadianceChestplate.hasElytra(armor)){
					list.children().add(new BooleanEntry("Enable Elytra", NBTConstants.getOrDefault(armor,NBTConstants.NBT_ELYTRA_STATE,false),NBTConstants.NBT_ELYTRA_STATE,
							"Activate the elytra on the backtank"));
				}
			}
			case BOOTS -> {
				list.children().add(new BooleanEntry("Enable Diving", NBTConstants.getOrDefault(armor,NBTConstants.NBT_DIVING,false),NBTConstants.NBT_DIVING,
                        "Enable diving, which makes the player descends quicker in liquids"));
                list.children().add(new BooleanEntry("Enable Lava Walking", NBTConstants.getOrDefault(armor,NBTConstants.NBT_LAVA,true),NBTConstants.NBT_LAVA,
                        "Enable walking under lava, which makes the player walks normally under lava"));

			}
			default -> {}
		}
        MobEffect effect = switch (((ArmorItem) armor.getItem()).getType()){
            case BOOTS -> MobEffects.JUMP;
            case LEGGINGS -> MobEffects.MOVEMENT_SPEED;
            case CHESTPLATE-> MobEffects.DAMAGE_BOOST;
            case HELMET -> MobEffects.NIGHT_VISION;
            default -> MobEffects.DIG_SLOWDOWN;
        };
		list.children().add(new BooleanEntry("Apply Potion Effect",NBTConstants.getOrDefault(armor,NBTConstants.NBT_EFFECTS,true),NBTConstants.NBT_EFFECTS,
                "Enable the potion effect granted by the armor piece",
                "For this piece, the effect is " + Component.translatable(effect.getDescriptionId()).getString()));
		list.children().add(new EnumEntry("Render Type",NBTConstants.getOrDefault(armor,NBTConstants.NBT_RENDER_TYPE),NBTConstants.NBT_RENDER_TYPE,
                "Define how the armor piece should be rendered.",
                "\"Armor only\" renders only the armor",
                "\"Addition only\" renders only the additions (E.g. Backtank, Goggles)"){
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
