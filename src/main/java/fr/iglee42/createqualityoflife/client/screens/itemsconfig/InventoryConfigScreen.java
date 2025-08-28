package fr.iglee42.createqualityoflife.client.screens.itemsconfig;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.iglee42.createqualityoflife.client.screens.widgets.ItemConfigButton;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.createmod.catnip.gui.widget.BoxWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class InventoryConfigScreen extends AbstractSimiScreen {


    private long scrollStartTime = System.currentTimeMillis();
    private List<Component> components = null;
    private int lastBtnIndex = -1;

    public void setDisplayedComponents(List<Component> newComponents) {
        this.components = newComponents;
        this.scrollStartTime = System.currentTimeMillis();
    }
    @Override
    protected void init() {
        setWindowOffset(-126, -98);
        super.init();

        Player player = Minecraft.getInstance().player;
        for (int slot = 0; slot < player.getInventory().items.size(); slot++) {
            //Hotbar
            int finalSlot = slot;
            if (slot < 9){
                ItemConfigButton btn = new ItemConfigButton(guiLeft + (slot * 28),guiTop + 7 * 28 + 4, b->Minecraft.getInstance().setScreen(new ItemConfigScreen(finalSlot)),slot);
                btn = btn.showing(player.getInventory().getItem(slot));
                addRenderableWidget(btn);
            }
            //Inventory
            else {
                int row = (slot - 9) / 9;
                int col = (slot - 9) % 9;
                ItemConfigButton btn = new ItemConfigButton(guiLeft + (col * 28),guiTop + 2 + (row + 4) * 28, b->Minecraft.getInstance().setScreen(new ItemConfigScreen(finalSlot)),slot);
                btn = btn.showing(player.getInventory().getItem(slot));
                addRenderableWidget(btn);
            }
        }

        for (int slot = 0; slot < player.getInventory().armor.size(); slot++) {
            int finalSlot = slot;
            ItemConfigButton btn = new ItemConfigButton(guiLeft,guiTop + (3-slot) * 28, b->Minecraft.getInstance().setScreen(new ItemConfigScreen(finalSlot + 36)),slot + 36);
            btn = btn.showing(player.getInventory().getItem(slot + 36));
            addRenderableWidget(btn);
        }
        ItemConfigButton btn = new ItemConfigButton(guiLeft + 112-28,guiTop + 84, b->Minecraft.getInstance().setScreen(new ItemConfigScreen(40)),40);
        btn = btn.showing(player.getInventory().getItem(40));
        addRenderableWidget(btn);
        int boxWidth = font.width("Choose an item to configure") + 10;
        int boxHeight = 19;
        int boxPadding = 4;
        BoxWidget title = new BoxWidget(width / 2 - boxWidth / 2, height / 2 - 130, boxWidth, boxHeight)
                //.withCustomBackground(new Color(0x20_000000, true))
                .<BoxWidget>setActive(false)
                .withBorderColors(AbstractSimiWidget.COLOR_IDLE)
                .withPadding(0, boxPadding)
                .rescaleElement(boxWidth / 2f, (boxHeight - 2 * boxPadding) / 2f);

        addRenderableWidget(title);

        BoxWidget infos = new BoxWidget(guiLeft + 140 - 28, guiTop, 132, 104)
                //.withCustomBackground(new Color(0x20_000000, true))
                .<BoxWidget>setActive(false)
                .withBorderColors(AbstractSimiWidget.COLOR_IDLE)
                .withPadding(0, boxPadding);

        addRenderableWidget(infos);


    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        children().stream().filter(c->c instanceof ItemConfigButton).map(ItemConfigButton.class::cast).forEach(btn->{
            btn.showing( Minecraft.getInstance().player.getInventory().getItem(btn.getIndex()));
            btn.active = Minecraft.getInstance().player.getInventory().getItem(btn.getIndex()).getItem() instanceof QOLConfigurableItem;
            btn.updateGradientFromState();
        });
        //graphics.fill(guiLeft + 24, guiTop - 2 , guiLeft + 106-24, guiTop + 106,0xff00ff00);
        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, guiLeft + 51, guiTop + 95, 48, (float)(guiLeft + 51) - mouseX, (float)(guiTop + 85 - 50) - mouseY, this.minecraft.player);

        graphics.drawCenteredString(font, "Choose an item to configure", width / 2, height / 2 - 125, UIRenderHelper.COLOR_TEXT_STRONG_ACCENT.getFirst().getRGB());
        AtomicReference<ItemConfigButton> button = new AtomicReference<>();
        children().stream().filter(c->c instanceof ItemConfigButton).map(ItemConfigButton.class::cast).forEach(btn->{
            if (btn.isHovered() && btn.active) button.set(btn);
        });

        if (button.get() != null && lastBtnIndex != button.get().getIndex()  && button.get().getItem() != null){
            setDisplayedComponents(Screen.getTooltipFromItem(this.minecraft, button.get().getItem()));
        }
        if( button.get() != null && button.get().getItem() != null)
            renderAutoScrollingComponents(graphics,font, components, guiLeft + 140 - 28, guiTop,guiLeft + 140 - 28 + 132, guiTop + 104,scrollStartTime);
        if (button.get() != null){
            lastBtnIndex = button.get().getIndex();
        } else {
            lastBtnIndex = -1;
        }
    }

    public void renderAutoScrollingComponents(
            GuiGraphics guiGraphics,
            Font font,
            List<Component> lines,
            int minX,
            int minY,
            int maxX,
            int maxY,
            long scrollStartTime
    ) {
        int maxWidth = maxX - minX;
        int clipHeight = maxY - minY;

        List<FormattedCharSequence> wrappedLines = new ArrayList<>();
        for (Component comp : lines) {
            wrappedLines.addAll(font.split(comp, maxWidth));
        }

        int lineHeight = font.lineHeight;
        int maxVisibleLines = clipHeight / lineHeight;

        final long pauseStartMillis = 2000;
        final long pauseEndMillis = 2000;
        final long scrollDurationMillis = wrappedLines.size() * 750L;
        final long totalCycle = pauseStartMillis + scrollDurationMillis + pauseEndMillis;

        if (wrappedLines.size() <= maxVisibleLines) {
            int y = minY;
            for (FormattedCharSequence line : wrappedLines) {
                guiGraphics.drawString(font, line, minX, y, 0xFFFFFF);
                y += lineHeight;
            }
            return;
        }

        long elapsed = System.currentTimeMillis() - scrollStartTime;
        long t = elapsed % totalCycle;

        float offsetY;
        if (t < pauseStartMillis) {
            offsetY = 0f;
        } else if (t < pauseStartMillis + scrollDurationMillis) {
            float progress = (float)(t - pauseStartMillis) / scrollDurationMillis;
            int scrollRange = (wrappedLines.size() - maxVisibleLines) * lineHeight;
            offsetY = progress * scrollRange;
        } else {
            offsetY = (wrappedLines.size() - maxVisibleLines) * lineHeight;
        }

        guiGraphics.enableScissor(minX, minY, maxX, maxY);

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        for (int i = 0; i < wrappedLines.size(); i++) {
            float drawY = minY + i * lineHeight - offsetY;
            if (drawY + lineHeight < minY || drawY > maxY) continue;
            guiGraphics.drawString(font, wrappedLines.get(i), minX, (int) drawY, 0xFFFFFF);
        }

        poseStack.popPose();
        guiGraphics.disableScissor();
    }


    @Override
    protected void renderWindowBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.fill(0, 0, this.width, this.height, 0xb0_282c34);

        super.renderWindowBackground(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void prepareFrame() {
        UIRenderHelper.swapAndBlitColor(minecraft.getMainRenderTarget(), UIRenderHelper.framebuffer);
        RenderSystem.clear(GL30.GL_STENCIL_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
    }

    @Override
    protected void endFrame() {
        UIRenderHelper.swapAndBlitColor(UIRenderHelper.framebuffer, minecraft.getMainRenderTarget());
    }
    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
