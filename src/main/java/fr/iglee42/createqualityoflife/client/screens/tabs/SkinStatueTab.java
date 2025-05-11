package fr.iglee42.createqualityoflife.client.screens.tabs;

import com.mojang.authlib.properties.PropertyMap;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.client.screens.ConfigureStatueScreen;
import fr.iglee42.createqualityoflife.registries.ModGuiTextures;
import fr.iglee42.createqualityoflife.registries.ModIcons;
import fr.iglee42.createqualityoflife.statue.Statue;
import fr.iglee42.createqualityoflife.statue.StatueRenderer;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SkinStatueTab extends StatueTab {
    public static final int SKIN_Y = -12;

    private EditBox playerSkinTextBox;
    private int lastInputSkinBox;

    private List<IconButton> partsButtons;
    private List<Label> partsLabels;

    private IconButton capeButton;

    public SkinStatueTab(int index, ConfigureStatueScreen parent) {
        super(index, AllItems.GOGGLES.asItem(), parent,"statue.skinTab");
    }

    @Override
    public void initWidgets(int x, int y) {
        playerSkinTextBox = new EditBox(Minecraft.getInstance().font,x + TEXT_BOX_X_OFFSET, y + SKIN_Y + TEXT_Y_OFFSET,TEXT_BOX_WIDTH,20,Component.empty());
        Optional<ResolvableProfile> profile = getExampleStatue().getProfile();
        profile.ifPresent(rp->{
            playerSkinTextBox.setValue(rp.gameProfile().getName());
        });
        playerSkinTextBox.setResponder(s->{
            lastInputSkinBox = 100;
        });
        playerSkinTextBox.setMaxLength(16);
        playerSkinTextBox.setBordered(false);
        playerSkinTextBox.setTextColor(0xffffff);
        playerSkinTextBox.setFocused(false);
        playerSkinTextBox.setHint(Component.literal("Player Name"));
        playerSkinTextBox.setFilter(StringUtil::isValidPlayerName);

        partsButtons = new ArrayList<>();
        partsLabels = new ArrayList<>();
        addPartWidget(x,y,PlayerModelPart.CAPE,ModIcons.I_CAPE_OFF);
        addPartWidget(x,y,PlayerModelPart.HAT, ModIcons.I_HAT);
        addPartWidget(x,y,PlayerModelPart.JACKET, ModIcons.I_JACKET);
        addPartWidget(x,y,PlayerModelPart.LEFT_SLEEVE, ModIcons.I_LEFT_SLEEVE);
        addPartWidget(x,y,PlayerModelPart.RIGHT_SLEEVE, ModIcons.I_RIGHT_SLEEVE);
        addPartWidget(x,y,PlayerModelPart.LEFT_PANTS_LEG, ModIcons.I_LEFT_PANTS);
        addPartWidget(x,y,PlayerModelPart.RIGHT_PANTS_LEG, ModIcons.I_RIGHT_PANTS);
    }

    private void addPartWidget(int x, int y, PlayerModelPart part, ScreenElement icon){
        Predicate<Statue> isPartActive = s->s.isPartShown(part);
        int index = partsButtons.size();
        y = y + SKIN_Y -2 + (index+1) * 22;
        List<Component> tip = new ArrayList<>();
        tip.add(part.getName());
        tip.add(CreateLang.translateDirect("gui.schematicannon.optionDisabled")
                .withStyle(ChatFormatting.RED));
        List<Component> tipEnabled = new ArrayList<>(tip);
        tipEnabled.set(1, CreateLang.translateDirect("gui.schematicannon.optionEnabled")
                .withStyle(ChatFormatting.DARK_GREEN));

        IconButton btn = new IconButton(x + BASE_OFFSET, y, icon);
        if (part == PlayerModelPart.CAPE) {
            btn.setIcon(isPartActive.test(getExampleStatue()) ? ModIcons.I_CAPE_ON : ModIcons.I_CAPE_OFF);
            capeButton = btn;
        }
        btn.withCallback(() -> {
            getExampleStatue().setPartVisibility(part,!isPartActive.test(getExampleStatue()));
            btn.green = isPartActive.test(getExampleStatue());
            btn.getToolTip().clear();
            btn.getToolTip().addAll(isPartActive.test(getExampleStatue()) ? tipEnabled : tip);
            if (part == PlayerModelPart.CAPE) btn.setIcon(isPartActive.test(getExampleStatue())? ModIcons.I_CAPE_ON : ModIcons.I_CAPE_OFF);
            getParent().sendUpdatePacket();
        });
        btn.green = isPartActive.test(getExampleStatue());
        btn.getToolTip().clear();
        btn.getToolTip().addAll(isPartActive.test(getExampleStatue()) ? tipEnabled : tip);
        Label label = new Label(x + LABEL_X_OFFSET,y + LABEL_Y_OFFSET,part.getName());
        label.text = part.getName();
        Optional<ResourceLocation> texture = StatueRenderer.getPlayerProfileTexture(getExampleStatue()).map(PlayerSkin::capeTexture);
        if (part == PlayerModelPart.CAPE && texture.isEmpty()){
            btn.active = false;
            btn.getToolTip().clear();
            btn.getToolTip().add(CreateQOLLang.translateDirect("statue.noCape"));
            btn.getToolTip().add(CreateQOLLang.translateDirect("statue.noCape1"));
        }
        partsButtons.add(btn);
        partsLabels.add(label);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partial, int x, int y) {
        if (lastInputSkinBox > 0) lastInputSkinBox--;
        if( lastInputSkinBox == 0 ){
            String value = playerSkinTextBox.getValue();
            ResolvableProfile resolvableProfile;
            if (value.isEmpty()) {
                resolvableProfile = null;
            } else {
                resolvableProfile = new ResolvableProfile(Optional.of(value),
                        Optional.empty(),
                        new PropertyMap());
            }
            getExampleStatue().setProfile(resolvableProfile);
            lastInputSkinBox = -1;

            Optional<ResourceLocation> texture = StatueRenderer.getPlayerProfileTexture(getExampleStatue()).map(PlayerSkin::capeTexture);
            if (capeButton != null){
                capeButton.active = texture.isPresent();
            }
            getParent().sendUpdatePacket();
        }

        ModGuiTextures.TEXT_BOX.render(graphics,x + BASE_OFFSET, y + SKIN_Y);
    }


    @Override
    public void forEachWidgets(Consumer<AbstractWidget> function) {
        function.accept(playerSkinTextBox);
        partsButtons.forEach(function);
        partsLabels.forEach(function);
    }

    @Override
    public void onQuit() {
        //This makes that the skin is saved even if the timer is not ended
        String value = playerSkinTextBox.getValue();
        ResolvableProfile resolvableProfile;
        if (value.isEmpty()) {
            resolvableProfile = null;
        } else {
            resolvableProfile = new ResolvableProfile(Optional.of(value),
                    Optional.empty(),
                    new PropertyMap());
        }
        getExampleStatue().setProfile(resolvableProfile);
    }
}
