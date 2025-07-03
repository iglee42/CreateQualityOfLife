package fr.iglee42.createqualityoflife.client.screens.tabs;

import com.simibubi.create.AllItems;
import fr.iglee42.createqualityoflife.client.screens.ConfigureStatueScreen;
import fr.iglee42.createqualityoflife.registries.QOLGuiTextures;
import fr.iglee42.createqualityoflife.statue.StatueMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.function.Consumer;

public class InventoryTab extends StatueTab {
    public InventoryTab(int index, ConfigureStatueScreen parent) {
        super(index, AllItems.CARDBOARD_SWORD.asItem(), parent,"statue.inventoryTab");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partial, int x, int y) {
        StatueMenu menu = getParent().getMenu();
        if (menu.isShowSlots()) {
            menu.slots.forEach(s -> {
                QOLGuiTextures.SLOT.render(graphics, s.x + getParent().getGuiLeft() - 1, s.y + getParent().getGuiTop() - 1);
            });
        }
    }

    @Override
    public void forEachWidgets(Consumer<AbstractWidget> function) {
        getParent().getMenu().setShowSlots(true);
    }

    @Override
    public void initWidgets(int x, int y) {
    }

    @Override
    public void onQuit() {
        super.onQuit();
        getParent().getMenu().setShowSlots(false);
    }
}
