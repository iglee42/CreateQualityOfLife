package fr.iglee42.createqualityoflife.client.screens.tabs;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import fr.iglee42.createqualityoflife.client.screens.ConfigureStatueScreen;
import fr.iglee42.createqualityoflife.client.screens.widgets.StatueAnimationWidget;
import fr.iglee42.createqualityoflife.statue.animation.PublishedAnimationsManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PublishedAnimationsTab extends StatueTab {

    private int selected = 6;
    private List<StatueAnimationWidget> animations;
    private IconButton previousButton;
    private IconButton nextButton;

    public PublishedAnimationsTab(int index, ConfigureStatueScreen parent) {
        super(index, Items.BOOKSHELF, parent, "statue.publishedAnimationTab");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partial, int x, int y) {
        animations.forEach(p->p.visible = animations.indexOf(p) >= selected - 6 && animations.indexOf(p) < selected);
        previousButton.active = selected > 6;
        nextButton.active = selected < animations.size();
    }

    @Override
    public void forEachWidgets(Consumer<AbstractWidget> function) {
        animations.forEach(function);
        function.accept(nextButton);
        function.accept(previousButton);
    }

    @Override
    public void initWidgets(int x, int y) {
        animations = new ArrayList<>();
        for (PublishedAnimationsManager.PublishedAnimation anim : PublishedAnimationsManager.CLIENT_ANIMATIONS) {
            int index = animations.size() % 6;
            int indexInPos = index % 3;
            animations.add(new StatueAnimationWidget(x + indexInPos * 45, y + (index > 2 ? 80 : 0),anim,this));
        }

        previousButton = new IconButton(getParent().getGuiLeft() + getParent().imageWidth - 95, getParent().getGuiTop() + getParent().imageHeight - 24, AllIcons.I_CONFIG_BACK);
        previousButton.withCallback(() -> {
            if (selected > 6){
                selected -= 6;
            }
        });

        nextButton = new IconButton(getParent().getGuiLeft() + getParent().imageWidth - 75, getParent().getGuiTop() + getParent().imageHeight - 24, AllIcons.I_CONFIG_OPEN);
        nextButton.withCallback(() -> {
            int maxIndex = (animations.size() - 1) / 6 * 6;
            if (selected + 6 <= maxIndex + 6) {
                selected += 6;
            }
        });

    }

    public void updateAnimationsPos(int x, int y){
        for (int index = 0; index < animations.size(); index++) {
            int indexInPos = index % 3;
            animations.get(index).setX(x + indexInPos * 45);
            animations.get(index).setY(y + (index > 2 ? 80 : 0));
        }
    }

    public List<StatueAnimationWidget> getAnimations() {
        return animations;
    }

    @Override
    public void onQuit() {
        super.onQuit();
    }
}
