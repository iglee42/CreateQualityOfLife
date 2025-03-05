package fr.iglee42.createqualityoflife.client.screens.widgets.entries;

import fr.iglee42.createqualityoflife.client.screens.ArmorConfigScreen;
import fr.iglee42.createqualityoflife.client.screens.widgets.ArmorConfigScreenList;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValueEntry<T> extends ArmorConfigScreenList.LabeledEntry {

	protected T value;
	protected String nbtKey;
	protected boolean editable = true;
	protected List<String> commentLines = new ArrayList<>(List.of("."));

	public ValueEntry(String label, T value, String nbtKey,String... comments) {
		super(label);
		this.value = value;
		this.nbtKey = nbtKey;

		labelTooltip.add(Component.literal(label).withStyle(ChatFormatting.WHITE));

		commentLines.addAll(Arrays.stream(comments).toList());

		labelTooltip.addAll(commentLines.stream()
				.map(s -> s.equals(".") ? " " : s)
				.map(Component::literal)
				.flatMap(stc -> FontHelper.cutTextComponent(stc, FontHelper.Palette.ALL_GRAY).stream())
				.toList()
		);
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

	}

	protected void bumpCog() {bumpCog(10f);}
	protected void bumpCog(float force) {
		ArmorConfigScreen.cogSpin.bump(3, force);
	}
}
