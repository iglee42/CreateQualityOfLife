package fr.iglee42.createqualityoflife.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ItemButton extends AbstractSimiWidget {

	protected ItemStack icon;

	public boolean green;

	private final OnClick clicked;


	public ItemButton(int x, int y, ItemStack icon,OnClick onClick) {
		this(x, y, 18, 18, icon,onClick);
	}

	public ItemButton(int x, int y, int w, int h, ItemStack icon,OnClick onClick) {
		super(x, y, w, h);
		this.icon = icon;
		this.clicked = onClick;
	}

	@Override
	public void doRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;

			AllGuiTextures button = !active ? AllGuiTextures.BUTTON_DISABLED
				: isHovered && AllKeys.isMouseButtonDown(0) ? AllGuiTextures.BUTTON_DOWN
					: isHovered ? AllGuiTextures.BUTTON_HOVER
						: green ? AllGuiTextures.BUTTON_GREEN : AllGuiTextures.BUTTON;

			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			drawBg(graphics, button);
			GuiGameElement.of(icon).render(graphics,getX() + 1,getY() + 1);
			//icon.render(graphics, getX() + 1, getY() + 1);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (visible && active &&  mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height){
			if (button == 0) {
				clicked.onClick(this);
				this.playDownSound(Minecraft.getInstance().getSoundManager());
				return true;
			}
		}
		return false;
	}

	protected void drawBg(GuiGraphics graphics, AllGuiTextures button) {
		graphics.blit(button.location, getX(), getY(), button.getStartX(), button.getStartY(), button.getWidth(),
			button.getHeight());
	}

	public void setToolTip(Component text) {
		toolTip.clear();
		toolTip.add(text);
	}

	public void setIcon(ItemStack icon) {
		this.icon = icon;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public interface OnClick{
		void onClick(ItemButton button);
	}
}
