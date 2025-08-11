package fr.iglee42.createqualityoflife.registries;

import com.mojang.blaze3d.systems.RenderSystem;
import fr.iglee42.createqualityoflife.CreateQOL;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum QOLGuiTextures implements ScreenElement {
    DISPLAY_BOARD("display_board", 256, 89),
    STATUE("statue_gui", 226, 220),
    TEXT_BOX("statue_gui",0,220,131, 18),
    COORDINATES("statue_gui",131,220,118, 18),
    ROTATIONS("statue_gui",131,238,88, 18),
    SIMPLE_EDIT_BOX("statue_gui", 219,238,26, 18),
    SLIDER("statue_gui",0,238,119, 18),
    SLOT("slot",18,18),
    NAME_EDIT_BOX("name_edit_box",111,18),

    POSE_BUTTON("statue_pose_button",0,0,45,60),
    POSE_BUTTON_HOVER("statue_pose_button",45,0,45,60),
    POSE_BUTTON_CLICKED("statue_pose_button",90,0,45,60),
    POSE_BUTTON_DISABLED("statue_pose_button",135,0,45,60),

    INFO_ICON("info_icon",0,0,16,16),
    INFO_ICON_HOVER("info_icon",16,0,16,16),

    STOCK_MANAGER_HEADER("stock_manager", 0, 0, 256, 39),
    STOCK_MANAGER_UPPER_BODY("stock_manager", 0, 48, 256, 20),
    STOCK_MANAGER_SEPARATION("stock_manager", 0, 80, 256, 2),
    STOCK_MANAGER_LOWER_BODY("stock_manager", 0, 94, 256, 20),
    STOCK_MANAGER_FOOTER("stock_manager", 0, 126, 256, 17),
    STOCK_MANAGER_LOCKED("stock_manager", 16, 176, 15, 16),
    STOCK_MANAGER_UNLOCKED("stock_manager", 32, 176, 15, 16),
    STOCK_MANAGER_SWITCH_NETWORK("stock_manager", 48, 176, 15, 16),
    STOCK_MANAGER_DESTRUCTION_ALLOW("stock_manager", 64, 176, 15, 16),
    STOCK_MANAGER_DESTRUCTION_MEMBERS("stock_manager", 80, 176, 15, 16),
    STOCK_MANAGER_DESTRUCTION_ADMINS("stock_manager", 96, 176, 15, 16),
    STOCK_MANAGER_EDIT_NAME("stock_manager", 0, 239, 13, 13),

    CHOOSE_NETWORK_HEADER("choose_logistic_network", 0, 15, 256, 24),
    CHOOSE_NETWORK_BODY("choose_logistic_network", 0, 48, 256, 20),
    CHOOSE_NETWORK_FOOTER("choose_logistic_network", 0, 80, 256, 17),
    CHOOSE_NETWORK_ENTRY("choose_logistic_network", 40, 164, 185, 18),
    CHOOSE_NETWORK_LOCKED("choose_logistic_network", 39, 182, 9, 9),
    CHOOSE_NETWORK_UNLOCKED("choose_logistic_network", 48, 182, 9, 9),
    CHOOSE_NETWORK_DELETE("choose_logistic_network", 57, 182, 9, 9),
    CHOOSE_NETWORK_DELETE_DISABLED("choose_logistic_network", 66, 182, 9, 9),
    CHOOSE_NETWORK_DEMOTE_PLAYER("choose_logistic_network", 75, 182, 9, 9),
    CHOOSE_NETWORK_PROMOTE_PLAYER("choose_logistic_network", 84, 182, 9, 9),
    CHOOSE_NETWORK_ADD_PLAYER("choose_logistic_network", 93, 182, 9, 9),
    CHOOSE_NETWORK_DELETE_PLAYER("choose_logistic_network", 102, 182, 9, 9),
    ;

    public static final int FONT_COLOR = 0x575F7A;

    public final ResourceLocation location;
    public int width, height;
    public int startX, startY;

    private QOLGuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    private QOLGuiTextures(int startX, int startY) {
        this("icons", startX * 16, startY * 16, 16, 16);
    }

    private QOLGuiTextures(String location, int startX, int startY, int width, int height) {
        this(CreateQOL.MODID, location, startX, startY, width, height);
    }

    private QOLGuiTextures(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = new ResourceLocation(namespace, "textures/gui/" + location + ".png");
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

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
