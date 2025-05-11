package fr.iglee42.createqualityoflife.client.screens.tabs;

import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.client.screens.ConfigureStatueScreen;
import fr.iglee42.createqualityoflife.statue.Statue;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.function.Consumer;

public abstract class StatueTab {

    public static final int BASE_OFFSET = 2;
    public static final int TEXT_BOX_X_OFFSET = BASE_OFFSET + 2;
    public static final int TEXT_BOX_WIDTH = 121 - BASE_OFFSET;
    public static final int LABEL_X_OFFSET = BASE_OFFSET * 2 + 20;
    public static final int TEXT_Y_OFFSET = 5;
    public static final int INDICATOR_Y_OFFSET = 18;
    public static final int LABEL_Y_OFFSET = TEXT_Y_OFFSET;


    private final int index;
    private final Item item;
    private final ConfigureStatueScreen parent;
    private final String key;

    public StatueTab(int index, Item item, ConfigureStatueScreen parent, String key) {
        this.index = index;
        this.item = item;
        this.parent = parent;
        this.key = key;
    }

    public Component getTooltips() {
        return CreateQOLLang.translateDirect(key);
    }

    public int getIndex() {
        return index;
    }

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, float partial,int x,int y);

    public Item getItem() {
        return item;
    }

    public ConfigureStatueScreen getParent() {
        return parent;
    }

    public Statue getExampleStatue(){
        return getParent().getExampleStatue();
    }

    public void onQuit(){}

    public abstract void forEachWidgets(Consumer<AbstractWidget> function);
    public abstract void initWidgets(int x,int y);


    public String getKey() {
        return key;
    }
}
