package fr.iglee42.createqualityoflife.client.screens.widgets.entries;

import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.client.screens.itemsconfig.ItemConfigScreen;
import fr.iglee42.createqualityoflife.packets.ChangeArmorTagPacket;
import fr.iglee42.createqualityoflife.registries.QOLPackets;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.RenderElement;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.createmod.catnip.gui.widget.BoxWidget;
import net.createmod.ponder.enums.PonderGuiTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;

public class BooleanEntry extends ValueEntry<Boolean> {

	RenderElement enabled;
	RenderElement disabled;
	BoxWidget button;

	public BooleanEntry(String label, Boolean value, String key,String[] comments,BiFunction<ValueEntry<?>,List<ValueEntry<?>>,Boolean> enable) {
		super(label, value, key,comments,enable);

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
	public void setEditable(boolean b) {
		super.setEditable(b);
		button.active = b;
	}

	@Override
	public void tick() {
		super.tick();
		button.tick();
	}

	@Override
	public void render(GuiGraphics graphics, int index, int y, int x, int width, int height, int mouseX, int mouseY,
					   boolean p_230432_9_, float partialTicks) {
		super.render(graphics, index, y, x, width, height, mouseX, mouseY, p_230432_9_, partialTicks);

		button.setX(x + width - 80 - 28);
		button.setY(y + 10);
		button.setWidth(35);
		button.setHeight(height - 20);
		button.updateGradientFromState();
		button.render(graphics, mouseX, mouseY, partialTicks);
	}

	@Override
	public void onValueChange(Boolean newValue) {
		super.onValueChange(newValue);
		button.showingElement(newValue ? enabled : disabled);
		bumpCog(newValue ? 15f : -16f);
		if (Minecraft.getInstance().screen == null) {
			CreateQOL.LOGGER.error("Cannot change nbt on a ValueEntry because the screen is null");
			return;
		}
		int slot = ((ItemConfigScreen)Minecraft.getInstance().screen).getItemSlot();
		Minecraft.getInstance().player.getInventory().getItem(slot).getOrCreateTag().putBoolean(nbtKey,value);
	}

	@Override
	public void setValue(@NotNull Boolean value) {
		if (Minecraft.getInstance().screen == null) {
			CreateQOL.LOGGER.error("Cannot change nbt on a ValueEntry because the screen is null");
			return;
		}
		int slot = ((ItemConfigScreen)Minecraft.getInstance().screen).getItemSlot();
        QOLPackets.getChannel().sendToServer(new ChangeArmorTagPacket(slot, !value ? 0 : 1, nbtKey));
		super.setValue(value);
	}
}
