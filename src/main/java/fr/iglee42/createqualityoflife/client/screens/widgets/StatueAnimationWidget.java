package fr.iglee42.createqualityoflife.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.gui.AllIcons;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.client.screens.tabs.PublishedAnimationsTab;
import fr.iglee42.createqualityoflife.packets.DeleteAnimationPacket;
import fr.iglee42.createqualityoflife.registries.ModEntityTypes;
import fr.iglee42.createqualityoflife.registries.ModGuiTextures;
import fr.iglee42.createqualityoflife.registries.ModIcons;
import fr.iglee42.createqualityoflife.registries.ModPackets;
import fr.iglee42.createqualityoflife.statue.Statue;
import fr.iglee42.createqualityoflife.statue.animation.PublishedAnimationsManager.PublishedAnimation;
import fr.iglee42.createqualityoflife.statue.animation.StatueAnimation;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.joml.Quaternionf;

import java.util.List;
import java.util.Objects;

public class StatueAnimationWidget extends AbstractSimiWidget {

    private final PublishedAnimation animation;
    private final PublishedAnimationsTab parent;
    private final Statue statue;

    public StatueAnimationWidget(int x, int y, PublishedAnimation animation, PublishedAnimationsTab parent) {
        super(x, y,45,60);
        this.animation = animation;
        this.parent = parent;
        statue = new Statue(ModEntityTypes.STATUE.get(), Minecraft.getInstance().level);
        CompoundTag tag = new CompoundTag();
        parent.getExampleStatue().saveWithoutId(tag);
        statue.load(tag);
        statue.setCustomNameVisible(false);
        StatueAnimation anim = animation.animation();
        anim.setLoop(true);
        statue.setAnimation(anim);
        statue.setAnimationProgress(0);
        statue.setAnimationPlaying(true);
    }

    @Override
    public void tick() {
        super.tick();
        statue.tickAnimation();
    }

    @Override
    protected void doRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.doRender(graphics, mouseX, mouseY, partialTicks);

        drawScrollingString(graphics,Minecraft.getInstance().font, Component.literal(animation.name()),getX(),getX() + getWidth(),getY() - 9,0xFFFFFF);
        graphics.pose().pushPose();
        graphics.pose().scale(0.8f,0.8f,0.8f);
        String player = Minecraft.getInstance().level.getPlayerByUUID(animation.publisher()) != null ? Minecraft.getInstance().level.getPlayerByUUID(animation.publisher()).getName().getString() : "Unknown";
        //graphics.drawScrollingString(Minecraft.getInstance().font, Component.literal(player), (int) ((getX() + 2) *1.25), (int) ((getX() + getWidth() - 12) *1.25), (int) ((getY() + getHeight() + 3) *1.25), ModGuiTextures.FONT_COLOR);

        graphics.pose().popPose();
        ModGuiTextures button = !active ? ModGuiTextures.POSE_BUTTON_DISABLED
                : isHovered && AllKeys.isMouseButtonDown(0) ? ModGuiTextures.POSE_BUTTON_CLICKED
                : isHovered ? ModGuiTextures.POSE_BUTTON_HOVER
                : ModGuiTextures.POSE_BUTTON;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        button.render(graphics,getX(),getY());
        float renderTime = AnimationTickHolder.getRenderTime();


        int posX = getX() + 22;
        int posY = getY() + 51;
        InventoryScreen.renderEntityInInventory(graphics, posX,posY, 24, new Quaternionf().rotationXYZ((float) Math.toRadians(180), (float) Math.toRadians(renderTime / 96 * 360), 0),null,statue);

        if (Minecraft.getInstance().player.getUUID().equals(animation.publisher()) || Minecraft.getInstance().player.hasPermissions(1)) {
            boolean hovered = mouseX >= getX() + width -14 && mouseX <= getX() + getWidth() && mouseY >= getY() + getHeight() - 1 && mouseY <= getY() + getHeight() + 11;

            graphics.pose().pushPose();
            graphics.pose().translate(getX() + getWidth() - 11, getY() + getHeight() - 1,100);
            graphics.pose().scale(11,11,11);
            if (hovered) ModIcons.I_DISCARD_HOVER.render(graphics.pose(),graphics.bufferSource(),0xffffff);
            AllIcons.I_CONFIG_DISCARD.render(graphics.pose(),graphics.bufferSource(),0xff0000);
            graphics.pose().popPose();
            if (hovered)graphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(CreateQOLLang.translateDirect("statue.animation.delete")),mouseX,mouseY);
        }

    }


    private void drawScrollingString(GuiGraphics graphics, Font font, Component text, int minX, int maxX, int y, int color) {
        int maxWidth = maxX - minX;
        int textWidth = font.width(text.getVisualOrderText());
        if (textWidth <= maxWidth) {
            graphics.drawCenteredString(font, text, (minX + maxX) / 2, y, color);
        } else {
            Objects.requireNonNull(font);
            AbstractWidget.renderScrollingString(graphics, font, text, minX, y - 1, maxX, y + 8, color);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean hovered = mouseX >= getX() + width -14 && mouseX <= getX() + getWidth() && mouseY >= getY() + getHeight() - 1 && mouseY <= getY() + getHeight() + 11;

        if (button == 0 && hovered){
            if (animation.publisher().equals(Minecraft.getInstance().player.getUUID()) || Minecraft.getInstance().player.hasPermissions(1)){
                ModPackets.getChannel().sendToServer(new DeleteAnimationPacket(animation.id()));
                parent.getAnimations().remove(this);
                parent.getParent().removeWidget(this);
                parent.updateAnimationsPos(parent.getParent().getGuiLeft() + 2*10 + 67 + (parent.getParent().isHideBackground()?110:0), parent.getParent().getGuiTop() + 30);
                playDownSound(Minecraft.getInstance().getSoundManager());
                return true;
            }
        } else if (isHovered() && button == 0) {
            parent.getExampleStatue().setAnimation(animation.animation());
            parent.getParent().sendUpdatePacket();
            playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }
        return false;
    }
}
