package fr.iglee42.createqualityoflife.registries;

import com.mojang.blaze3d.systems.RenderSystem;
import fr.iglee42.createqualityoflife.CreateQOL;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public enum ModGuiTextures implements ScreenElement {
    DISPLAY_BOARD("display_board", 256, 89),
    STATUE("statue_gui", 226, 220),
    TEXT_BOX("statue_gui",0,220,131, 18),
    COORDINATES("statue_gui",131,220,118, 18),
    SLIDER("statue_gui",0,238,119, 18),
    SLOT("slot",18,18),

    POSE_BUTTON("statue_pose_button",0,0,45,60),
    POSE_BUTTON_HOVER("statue_pose_button",45,0,45,60),
    POSE_BUTTON_CLICKED("statue_pose_button",90,0,45,60),
    POSE_BUTTON_DISABLED("statue_pose_button",135,0,45,60),

    INFO_ICON("info_icon",0,0,16,16),
    INFO_ICON_HOVER("info_icon",16,0,16,16)

    ;

    public static final int FONT_COLOR = 0x575F7A;

    public final ResourceLocation location;
    public int width, height;
    public int startX, startY;

    private ModGuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    private ModGuiTextures(int startX, int startY) {
        this("icons", startX * 16, startY * 16, 16, 16);
    }

    private ModGuiTextures(String location, int startX, int startY, int width, int height) {
        this(CreateQOL.MODID, location, startX, startY, width, height);
    }

    private ModGuiTextures(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = ResourceLocation.fromNamespaceAndPath(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    @OnlyIn(Dist.CLIENT)
    public void bind() {
        RenderSystem.setShaderTexture(0, location);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(location, x, y, startX, startY, width, height);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(graphics, c, x, y, startX, startY, width, height);
    }


}
