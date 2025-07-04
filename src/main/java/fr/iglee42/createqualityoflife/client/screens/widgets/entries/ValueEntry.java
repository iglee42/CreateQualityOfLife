package fr.iglee42.createqualityoflife.client.screens.widgets.entries;

import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.client.screens.itemsconfig.ItemConfigScreen;
import fr.iglee42.createqualityoflife.client.screens.widgets.ArmorConfigScreenList;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class ValueEntry<T> extends ArmorConfigScreenList.LabeledEntry {

	protected T value;
	protected DataComponentType<?> component;
	protected boolean editable = true;
	protected List<String> commentLines = new ArrayList<>(List.of("."));
	private final BiFunction<ValueEntry<?>, List<ValueEntry<?>>, Boolean> enable;


	public ValueEntry(String label, T value, DataComponentType<?> component, String[] comments, BiFunction<ValueEntry<?>,List<ValueEntry<?>>,Boolean> enable) {
		super(label);
		this.value = value;
		this.component = component;
		this.enable = enable;

		labelTooltip.add(Component.literal(label).withStyle(ChatFormatting.WHITE));

		commentLines.addAll(Arrays.stream(comments).toList());

		labelTooltip.addAll(commentLines.stream()
				.map(s -> s.equals(".") ? " " : s)
				.map(Component::literal)
				.flatMap(stc -> FontHelper.cutTextComponent(stc, FontHelper.Palette.GRAY_AND_RED).stream())
				.toList()
		);
	}


	@Override
	public void setEditable(boolean b) {
		editable = b;
		labelTooltip.clear();
		labelTooltip.add(label.getComponent().copy().withStyle(ChatFormatting.WHITE));
		labelTooltip.addAll(commentLines.stream()
				.map(s -> s.equals(".") ? " " : s)
				.map(Component::literal)
				.flatMap(stc -> FontHelper.cutTextComponent(stc, FontHelper.Palette.GRAY_AND_RED).stream())
				.toList()
		);
		if (!b)labelTooltip.add(Component.literal("Disabled by the config").withStyle(ChatFormatting.RED));
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
		if (Minecraft.getInstance().screen == null) {
			CreateQOL.LOGGER.error("Cannot change component on a ValueEntry because the screen is null");
			return;
		}
		int slot = ((ItemConfigScreen)Minecraft.getInstance().screen).getItemSlot();
		Minecraft.getInstance().player.getInventory().getItem(slot).set(((DataComponentType<? super T>) component), value);
	}

	protected void bumpCog() {bumpCog(10f);}
	protected void bumpCog(float force) {
		ItemConfigScreen.cogSpin.bump(3, force);
	}

	public DataComponentType<?> getComponent() {
		return component;
	}

	public boolean isEditable() {
		return editable;
	}

	public BiFunction<ValueEntry<?>, List<ValueEntry<?>>, Boolean> getEnableFunction() {
		return enable;
	}
}
