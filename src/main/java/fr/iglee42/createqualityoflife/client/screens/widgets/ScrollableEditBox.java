package fr.iglee42.createqualityoflife.client.screens.widgets;

import com.simibubi.create.AllKeys;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.createmod.catnip.gui.widget.AbstractSimiWidget.HEADER_RGB;

public class ScrollableEditBox extends EditBox {

    private final List<Component> toolTips = new ArrayList<>();

    public ScrollableEditBox(Font font, int x, int y, Component text) {
        super(font, x, y, text);
    }

    public ScrollableEditBox(Font font, int x, int y, int width, int height, Component text) {
        super(font, x, y, width, height, text);
    }

    public ScrollableEditBox(Font font, int x, int y, int width, int height, @Nullable EditBox copiable, Component text) {
        super(font, x, y, width, height, copiable, text);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        int state = getCastedValue();
        int priorState = getCastedValue();
        boolean shifted = AllKeys.shiftDown();
        int step = (int) Math.signum(deltaY);

        if (AllKeys.ctrlDown()){
            step *= 90;
        } else if (shifted) step*= 45;

        state += step;

        if (state < -180 || state >= 180) state = priorState;
        if (priorState != state) {
            Minecraft.getInstance()
                    .getSoundManager()
                    .play(SimpleSoundInstance.forUI(AllSoundEvents.SCROLL_VALUE.getMainEvent(),
                            1.5f + 0.1f * (state + 180) / (180 + 180)));
            setValue(state + "");
        }
        return priorState != state;
    }

    @Override
    public void setValue(String p_94145_) {
        super.setValue(p_94145_);
        updateTooltip();
    }

    public int getCastedValue(){
        try  {
            return Integer.parseInt(getValue());
        } catch (NumberFormatException exception){
            return 0;
        }
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float p_283101_) {
        super.renderWidget(graphics, mouseX, mouseY, p_283101_);
        if (isHovered) {
                List<Component> tooltip = toolTips;
                if (tooltip.isEmpty()) return;
                graphics.renderComponentTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
            }
    }

    protected void updateTooltip() {
        toolTips.clear();
        Component title = Component.literal(getValue() + "°");
        final Component scrollToModify = CreateLang.translateDirect("gui.scrollInput.scrollToModify");
        toolTips.add(title.plainCopy()
                .withStyle(s -> s.withColor(HEADER_RGB.getRGB())));
        toolTips.add(scrollToModify.plainCopy()
                .withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));
    }

}
