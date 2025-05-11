package fr.iglee42.createqualityoflife.client.screens;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.client.screens.tabs.*;
import fr.iglee42.createqualityoflife.client.screens.widgets.ItemButton;
import fr.iglee42.createqualityoflife.packets.SaveStatueConfigPacket;
import fr.iglee42.createqualityoflife.registries.ModEntityTypes;
import fr.iglee42.createqualityoflife.registries.ModGuiTextures;
import fr.iglee42.createqualityoflife.registries.ModItems;
import fr.iglee42.createqualityoflife.registries.ModPackets;
import fr.iglee42.createqualityoflife.statue.Statue;
import fr.iglee42.createqualityoflife.statue.StatueMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;

import java.util.ArrayList;
import java.util.List;

public class ConfigureStatueScreen extends AbstractSimiContainerScreen<StatueMenu> {

    private Statue exampleStatue;
    private List<StatueTab> tabs;
    private int currentTab;
    private int oCurrentTab = -1;
    private IconButton possesButton;
    private IconButton confirmButton;
    private IconButton hideButton;
    private boolean hideBackground = false;
    private boolean wasAnimationPlaying;

    public ConfigureStatueScreen(StatueMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    }


    @Override
    protected void init() {
        ModGuiTextures bg = ModGuiTextures.STATUE;
        setWindowSize(bg.width,bg.height);
        setWindowOffset(0,0);
        super.init();

        wasAnimationPlaying = menu.contentHolder.isAnimationPlaying();
        menu.contentHolder.setAnimationPlaying(false);

        confirmButton = new IconButton(leftPos + bg.width - 25, topPos + bg.height - 24, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> {
            onClose();
            minecraft.player.closeContainer();
        });

        hideButton = new IconButton(leftPos + bg.width - 55, topPos + bg.height - 24, AllIcons.I_MTD_SCAN);
        hideButton.withCallback(() -> {
            setHideBackground(!isHideBackground());
            int currentTab = this.currentTab;
            resize(Minecraft.getInstance(),width,height);
            this.oCurrentTab = 0;
            this.currentTab = currentTab;
        });
        hideButton.getToolTip().add(CreateQOLLang.translateDirect("statue.hideBackground"));
        hideButton.getToolTip().add(CreateQOLLang.translateDirect("statue.hideBackground1"));
        addRenderableWidget(confirmButton);
        addRenderableWidget(hideButton);
        this.exampleStatue = new Statue(ModEntityTypes.STATUE.get(),Minecraft.getInstance().level);
        if (menu.contentHolder != null) {
            CompoundTag tag = new CompoundTag();
            menu.contentHolder.saveWithoutId(tag);
            this.exampleStatue.load(tag);
        }
        tabs = new ArrayList<>();
        tabs.add(new MainStatueTab(tabs.size(),this));
        tabs.add(new SkinStatueTab(tabs.size(),this));
        tabs.add(new StatueTransformTab(tabs.size(), this));
        tabs.add(new PartsRotationTab(tabs.size(), Items.PLAYER_HEAD.asItem(), this, PlayerModelPart.HAT));
        tabs.add(new PartsRotationTab(tabs.size(), ModItems.SHADOW_RADIANCE_CHESTPLATE.asItem(), this, PlayerModelPart.LEFT_SLEEVE,PlayerModelPart.RIGHT_SLEEVE));
        tabs.add(new PartsRotationTab(tabs.size(), ModItems.SHADOW_RADIANCE_LEGGINGS.asItem(), this, PlayerModelPart.LEFT_PANTS_LEG,PlayerModelPart.RIGHT_PANTS_LEG));
        tabs.add(new InventoryTab(tabs.size(), this));
        tabs.add(new RotationPresetsTab(tabs.size(), this));
        tabs.add(new AnimationTab(tabs.size(),this));
        tabs.add(new PublishedAnimationsTab(tabs.size(),this));
        tabs.forEach(t->{
            ItemButton btn = addRenderableWidget(new ItemButton(leftPos + imageWidth +2,topPos + 10+ t.getIndex() * 20, t.getItem().getDefaultInstance(),b->currentTab = t.getIndex()));
            if (btn.getIcon().is(Items.PLAYER_HEAD)){
                ItemStack stack = Items.PLAYER_HEAD.getDefaultInstance();
                CompoundTag nbt = new CompoundTag();
                nbt.putString("SkullOwner",Minecraft.getInstance().player.getGameProfile().getName());
                btn.setIcon(stack);
            }
            btn.getToolTip().add(t.getTooltips());
            t.initWidgets(leftPos + 87 + (hideBackground ? 110 : 0), topPos + 30);
        });
        currentTab = 0;
        if (!exampleStatue.hasOwner())
        {
            possesButton = new IconButton(leftPos + 7 + font.width("No Owner"), topPos + bg.height - 26,AllIcons.I_TARGET).withCallback(()->exampleStatue.setOwner(Minecraft.getInstance().player.getUUID()));
            possesButton.getToolTip().add(CreateQOLLang.translateDirect("statue.ownStatue"));
            possesButton.getToolTip().add(CreateQOLLang.translateDirect("statue.ownStatue1"));
            addRenderableWidget(possesButton);
        }

        getMenu().setShowSlots(false);
    }


    @Override
    public void renderBackground(GuiGraphics p_283688_) {
        if (!hideBackground)super.renderBackground(p_283688_);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (renderables.contains(possesButton) && exampleStatue.hasOwner()) removeWidget(possesButton);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float v, int i, int i1) {
        if (!hideBackground)ModGuiTextures.STATUE.render(graphics, leftPos, topPos);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {

        for (int slot = 0; slot < getMenu().ghostInventory.getSlots(); slot++) {
            getExampleStatue().setItemSlot(EquipmentSlot.values()[slot],getMenu().ghostInventory.getStackInSlot(slot));
        }
        if (!hideBackground){
            graphics.drawString(Minecraft.getInstance().font,"Customize your Statue",getGuiLeft() + 5, getGuiTop() + 3,0xffffff);
        }

        int offsetX = 10;
        int offsetY = 30;
        int size = 160;
        if (oCurrentTab != currentTab){
            tabs.stream().filter(t->t.getIndex() == oCurrentTab).forEach(t->{
                t.forEachWidgets(this::removeWidget);
                t.forEachWidgets(this::removeWidget);
                t.onQuit();
            });
            tabs.stream().filter(t->t.getIndex() == currentTab).findFirst().ifPresent(t->t.forEachWidgets(this::addRenderableWidget));
            oCurrentTab = currentTab;
        }
        if (exampleStatue != null && !hideBackground)InventoryScreen.renderEntityInInventoryFollowsMouse(graphics,getGuiLeft() + 40,getGuiTop() + 150,50, (getGuiLeft() + 40) -mouseX ,(getGuiLeft() + offsetY) -mouseY,exampleStatue);
        tabs.stream().filter(t->t.getIndex() == currentTab).findAny().ifPresent(t->{
            t.render(graphics,mouseX,mouseY,partialTicks,leftPos + 2*offsetX + 67 + (hideBackground?110:0), topPos + offsetY);

        });
        children().stream().filter(c->c instanceof ItemButton)
                .map(ItemButton.class::cast)
                .forEach(btn->btn.setActive(tabs.stream().noneMatch(tab->btn.getIcon().is(tab.getItem()) && currentTab == tab.getIndex())));

        children().stream().filter(c->c instanceof ItemButton)
                .map(ItemButton.class::cast)
                .forEach(btn->btn.visible = !hideBackground);
        confirmButton.visible = !hideBackground;
        String toDraw = exampleStatue.hasOwner() ? "Owner: " + Minecraft.getInstance().level.getPlayerByUUID(exampleStatue.getOwner().get()).getName().getString() : "No Owner";
        if (!hideBackground)graphics.drawString(font,toDraw, leftPos + 5, topPos + ModGuiTextures.STATUE.height - 21, 0xffffff);

        if (!CreateQOLLang.translateDirect(tabs.get(currentTab).getKey()+".desc").getString().isEmpty() && !hideBackground) {
            boolean infoHovered = mouseX >= getGuiLeft() + imageWidth - 18 && mouseX <= getGuiLeft() + imageWidth - 2 && mouseY >= getGuiTop() && mouseY <= getGuiTop() + 16;
            if (infoHovered)
                ModGuiTextures.INFO_ICON_HOVER.render(graphics, getGuiLeft() + imageWidth - 18, getGuiTop());
            else ModGuiTextures.INFO_ICON.render(graphics, getGuiLeft() + imageWidth - 18, getGuiTop());

            if (infoHovered)
                graphics.renderComponentTooltip(font, List.of(CreateQOLLang.translateDirect(tabs.get(currentTab).getKey() + ".desc")), mouseX, mouseY);
        }


        super.renderForeground(graphics, mouseX, mouseY, partialTicks);


    }



    public Statue getExampleStatue() {
        return exampleStatue;
    }

    @Override
    public void onClose() {
        tabs.stream().filter(t->t.getIndex() == currentTab).forEach(StatueTab::onQuit);
        getExampleStatue().setAnimationPlaying(wasAnimationPlaying);
        sendUpdatePacket();
        super.onClose();
    }

    public void sendUpdatePacket(){
        CompoundTag nbt = new CompoundTag();
        exampleStatue.saveWithoutId(nbt);
        if (menu.contentHolder != null) {
            if (getExampleStatue().hasOwner() && !Minecraft.getInstance().player.getUUID().equals(getExampleStatue().getOwner().get())){
                nbt.putBoolean("Invulnerable",menu.contentHolder.isInvulnerable());
            }
            ModPackets.getChannel().sendToServer(new SaveStatueConfigPacket(menu.contentHolder.getId(),nbt));
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (tabs.get(currentTab) instanceof StatueTransformTab tab){
            if (tab.keyPressed(pKeyCode, pScanCode, pModifiers)) return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    public void setHideBackground(boolean hideBackground) {
        this.hideBackground = hideBackground;
    }

    public boolean isHideBackground() {
        return hideBackground;
    }

    @Override
    public void resize(Minecraft p_96575_, int p_96576_, int p_96577_) {
        tabs.stream().filter(t->t.getIndex() == currentTab).findFirst().ifPresent(t->t.forEachWidgets(this::removeWidget));
        super.resize(p_96575_, p_96576_, p_96577_);
        tabs.stream().filter(t->t.getIndex() == currentTab).findFirst().ifPresent(t->t.forEachWidgets(this::addRenderableWidget));
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        if (!hideBackground)return super.getExtraAreas();
        int x = leftPos + 20 + 67 + 110;
        return List.of(new Rect2i(x, topPos + 30,imageWidth - 67,160));
    }
}
