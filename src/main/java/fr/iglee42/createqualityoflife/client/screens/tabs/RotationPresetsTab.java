package fr.iglee42.createqualityoflife.client.screens.tabs;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.client.screens.ConfigureStatueScreen;
import fr.iglee42.createqualityoflife.client.screens.widgets.StatuePoseWidget;
import fr.iglee42.createqualityoflife.registries.ModEntityTypes;
import fr.iglee42.createqualityoflife.statue.Statue;
import fr.iglee42.createqualityoflife.statue.StatueDefaultRotations;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Items;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RotationPresetsTab extends AbstractStatueTab{

    private int selected = 6;
    private List<StatuePoseWidget> poses;
    private IconButton previousButton;
    private IconButton nextButton;

    public RotationPresetsTab(int index, ConfigureStatueScreen parent) {
        super(index, Items.ARMOR_STAND, parent, "statue.rotationPresetTab");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partial, int x, int y) {
        poses.forEach(p->p.visible = poses.indexOf(p) >= selected - 6 && poses.indexOf(p) < selected);
    }

    @Override
    public void forEachWidgets(Consumer<AbstractWidget> function) {
        poses.forEach(function);
        function.accept(nextButton);
        function.accept(previousButton);
    }

    @Override
    public void initWidgets(int x, int y) {
        poses = new ArrayList<>();
        for (StatueDefaultRotations pose : StatueDefaultRotations.values()) {
            int index = pose.ordinal() % 6;
            int indexInPos = index % 3;
            poses.add(new StatuePoseWidget(x + indexInPos * 45, y + (index > 2 ? 80 : 0),pose,getParent()));
        }

        previousButton = new IconButton(getParent().getGuiLeft() + getParent().imageWidth - 95, getParent().getGuiTop() + getParent().imageHeight - 24, AllIcons.I_CONFIG_BACK);
        previousButton.withCallback(() -> {
            if (selected > 6){
                selected -= 6;
            }
        });

        nextButton = new IconButton(getParent().getGuiLeft() + getParent().imageWidth - 75, getParent().getGuiTop() + getParent().imageHeight - 24, AllIcons.I_CONFIG_OPEN);
        nextButton.withCallback(() -> {
            int maxIndex = (poses.size() - 1) / 6 * 6;
            if (selected + 6 <= maxIndex + 6) {
                selected += 6;
            }
        });

    }

    @Override
    public void onQuit() {
        super.onQuit();
    }
}
