package fr.iglee42.createqualityoflife.client.screens.widgets.entries;

import fr.iglee42.createqualityoflife.client.screens.ArmorConfigScreen;
import fr.iglee42.createqualityoflife.packets.ChangeArmorTagPacket;
import fr.iglee42.createqualityoflife.registries.QOLPackets;
import net.createmod.catnip.config.ui.ConfigScreen;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.BoxElement;
import net.createmod.catnip.gui.element.DelegatedStencilElement;
import net.createmod.catnip.gui.element.TextStencilElement;
import net.createmod.catnip.gui.widget.BoxWidget;
import net.createmod.ponder.enums.PonderGuiTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class EnumEntry extends ValueEntry<Enum<?>> {

	protected static final int cycleWidth = 34;

	protected TextStencilElement valueText;
	protected BoxWidget cycleLeft;
	protected BoxWidget cycleRight;

	public EnumEntry(String label, Enum<?> value, String key,String... comments) {
		super(label, value, key,comments);

		valueText = new TextStencilElement(Minecraft.getInstance().font, "YEP").centered(true, true);
		valueText.withElementRenderer((ms, width, height, alpha) -> UIRenderHelper.angledGradient(ms, 0, 0, height / 2,
			height, width, UIRenderHelper.COLOR_TEXT));

		DelegatedStencilElement l = PonderGuiTextures.ICON_CONFIG_PREV.asStencil();
		cycleLeft = new BoxWidget(0, 0, cycleWidth + 8, 16)
				.withCustomBackground(BoxElement.COLOR_BACKGROUND_FLAT)
				.showingElement(l)
				.withCallback(() -> cycleValue(-1));
		l.withElementRenderer(BoxWidget.gradientFactory.apply(cycleLeft));

		DelegatedStencilElement r = PonderGuiTextures.ICON_CONFIG_NEXT.asStencil();
		cycleRight = new BoxWidget(0, 0, cycleWidth + 8, 16)
				.withCustomBackground(BoxElement.COLOR_BACKGROUND_FLAT)
				.showingElement(r)
				.withCallback(() -> cycleValue(1));
		r.at(cycleWidth - 8, 0);
		r.withElementRenderer(BoxWidget.gradientFactory.apply(cycleRight));

		listeners.add(cycleLeft);
		listeners.add(cycleRight);

		onReset();
	}

	protected void cycleValue(int direction) {

		Enum<?> e = getValue();
		Enum<?>[] options = e.getDeclaringClass().getEnumConstants();
		e = options[Math.floorMod(e.ordinal() + direction, options.length)];
		setValue(e);
		bumpCog(direction * 15f);
	}

	@Override
	public void setEditable(boolean b) {
		super.setEditable(b);
		cycleLeft.active = b;
		cycleLeft.animateGradientFromState();
		cycleRight.active = b;
		cycleRight.animateGradientFromState();
	}

	@Override
	public void tick() {
		super.tick();
		cycleLeft.tick();
		cycleRight.tick();
	}

	@Override
	public void render(GuiGraphics graphics, int index, int y, int x, int width, int height, int mouseX, int mouseY,
					   boolean p_230432_9_, float partialTicks) {
		super.render(graphics, index, y, x, width, height, mouseX, mouseY, p_230432_9_, partialTicks);

		cycleLeft.setX(x + getLabelWidth(width) + 4);
		cycleLeft.setY(y + 10);
		cycleLeft.render(graphics, mouseX, mouseY, partialTicks);

		valueText.at(cycleLeft.getX() + cycleWidth - 8, y + 10, 200)
				.withBounds(width - getLabelWidth(width) - 2 * cycleWidth - 4-28, 16)
				.render(graphics);

		cycleRight.setX(x + width - cycleWidth * 2 - 28 + 10);
		cycleRight.setY(y + 10);
		cycleRight.render(graphics, mouseX, mouseY, partialTicks);

		new BoxElement()
				.withBackground(BoxElement.COLOR_BACKGROUND_FLAT)
				.flatBorder(0x01_000000)
				.withBounds(48, 6)
				.at(cycleLeft.getX() + 22, cycleLeft.getY() + 5)
				.render(graphics);
	}

	@Override
	public void onValueChange(Enum<?> newValue) {
		super.onValueChange(newValue);
		valueText.withText(ConfigScreen.toHumanReadable(newValue.name().toLowerCase(Locale.ROOT)));
		List<Integer> armors = ((ArmorConfigScreen)Minecraft.getInstance().screen).getArmors();
		int selected = ((ArmorConfigScreen)Minecraft.getInstance().screen).getSelectedItem();
		Minecraft.getInstance().player.getInventory().getArmor(armors.get(selected)).getOrCreateTag().putInt(nbtKey,newValue.ordinal());
	}

	@Override
	public void setValue(@NotNull Enum<?> value) {
		List<Integer> armors = ((ArmorConfigScreen)Minecraft.getInstance().screen).getArmors();
		int selected = ((ArmorConfigScreen)Minecraft.getInstance().screen).getSelectedItem();
		QOLPackets.getChannel().sendToServer(new ChangeArmorTagPacket(armors.get(selected),value.ordinal(), nbtKey));
		super.setValue(value);
	}
}