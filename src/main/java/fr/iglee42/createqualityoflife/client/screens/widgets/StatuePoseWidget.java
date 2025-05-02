package fr.iglee42.createqualityoflife.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import fr.iglee42.createqualityoflife.client.screens.ConfigureStatueScreen;
import fr.iglee42.createqualityoflife.registries.ModEntityTypes;
import fr.iglee42.createqualityoflife.registries.ModGuiTextures;
import fr.iglee42.createqualityoflife.statue.Statue;
import fr.iglee42.createqualityoflife.statue.StatueDefaultRotations;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.createmod.catnip.gui.widget.BoxWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Objects;

public class StatuePoseWidget extends AbstractSimiWidget {

    private final StatueDefaultRotations pose;
    private final ConfigureStatueScreen parent;

    public StatuePoseWidget(int x, int y, StatueDefaultRotations pose, ConfigureStatueScreen parent) {
        super(x, y,45,60);
        this.pose = pose;
        this.parent = parent;
        withCallback(()->{
            pose.applyToStatue(parent.getExampleStatue());
            parent.sendUpdatePacket();
        });
    }

    @Override
    protected void doRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.doRender(graphics, mouseX, mouseY, partialTicks);

        drawScrollingString(graphics,Minecraft.getInstance().font, pose.getName(),getX(),getX() + getWidth(),getY() - 11,0xFFFFFF);
        graphics.pose().pushPose();
        graphics.pose().scale(0.5f,0.5f,0.5f);
        if ( mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY > getY() + getHeight() && mouseY <= getY() + getHeight() + 11 && pose.getSource().getString().equals("Vanilla Tweaks")){
            drawScrollingString(graphics,Minecraft.getInstance().font, pose.getSource().withStyle(ChatFormatting.UNDERLINE),getX() * 2,(getX() + getWidth()) * 2,(getY()+ getHeight() + 2) * 2,ModGuiTextures.FONT_COLOR);
        } else {
            drawScrollingString(graphics,Minecraft.getInstance().font, pose.getSource(),getX() * 2,(getX() + getWidth()) * 2,(getY()+ getHeight() + 2) * 2,ModGuiTextures.FONT_COLOR);

        }
        graphics.pose().popPose();
        ModGuiTextures button = !active ? ModGuiTextures.POSE_BUTTON_DISABLED
                : isHovered && AllKeys.isMouseButtonDown(0) ? ModGuiTextures.POSE_BUTTON_CLICKED
                : isHovered ? ModGuiTextures.POSE_BUTTON_HOVER
                : ModGuiTextures.POSE_BUTTON;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        button.render(graphics,getX(),getY());
        float renderTime = AnimationTickHolder.getRenderTime();
        Statue example = new Statue(ModEntityTypes.STATUE.get(), Minecraft.getInstance().level);
        CompoundTag tag = new CompoundTag();
        parent.getExampleStatue().saveWithoutId(tag);
        example.load(tag);
        example.setCustomNameVisible(false);
        pose.applyToStatue(example);
        int posX = getX() + 10;
        int posY = getY() + 7;
        InventoryScreen.renderEntityInInventory(graphics, posX,posY, 24, new Vector3f(example.getBbWidth(),example.getBbHeight() ,0), new Quaternionf().rotationXYZ((float) Math.toRadians(180), (float) Math.toRadians(renderTime / 96 * 360), 0),null,example);

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
        if (button == 0 && mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY > getY() + getHeight() && mouseY <= getY() + getHeight() + 11){
            if (pose.getSource().getString().equals("Vanilla Tweaks")){
                ConfirmLinkScreen.confirmLinkNow(parent,"https://vanillatweaks.net/");
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
