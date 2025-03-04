package fr.iglee42.createqualityoflife.client.screens.widgets.entries;

import fr.iglee42.createqualityoflife.client.screens.ArmorConfigScreen;
import fr.iglee42.createqualityoflife.client.screens.widgets.ArmorConfigScreenList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.util.List;

public class ValueEntry<T> extends ArmorConfigScreenList.LabeledEntry {

	protected T value;
	protected DataComponentType<?> component;
	protected boolean editable = true;

	public ValueEntry(String label, T value, DataComponentType<?> component) {
		super(label);
		this.value = value;
		this.component = component;

		labelTooltip.add(Component.literal(label).withStyle(ChatFormatting.WHITE));
	}

	@Override
	protected void setEditable(boolean b) {
		editable = b;
	}


	@Override
	public void render(GuiGraphics graphics, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean p_230432_9_, float partialTicks) {
		super.render(graphics, index, y, x, width, height, mouseX, mouseY, p_230432_9_, partialTicks);
	}

	@Override
	protected int getLabelWidth(int totalWidth) {
		return (int) (totalWidth * labelWidthMult) + 30;
	}

	public void setValue(@Nonnull T value) {
		this.value = value;
		onValueChange(value);
	}

	@Nonnull
	public T getValue() {
		return value;
	}


	public void onReset() {
		onValueChange(getValue());
	}

	public void onValueChange() {
		onValueChange(getValue());
	}
	public void onValueChange(T newValue) {
		List<Integer> armors = ((ArmorConfigScreen)Minecraft.getInstance().screen).getArmors();
		int selected = ((ArmorConfigScreen)Minecraft.getInstance().screen).getSelectedItem();
		Minecraft.getInstance().player.getInventory().getArmor(armors.get(selected)).set(((DataComponentType<? super T>) component), value);
	}

	protected void bumpCog() {bumpCog(10f);}
	protected void bumpCog(float force) {
		ArmorConfigScreen.cogSpin.bump(3, force);
	}
}
