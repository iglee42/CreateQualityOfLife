package fr.iglee42.createqualityoflife.client.screens.tabs;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.client.screens.ConfigureStatueScreen;
import fr.iglee42.createqualityoflife.registries.ModGuiTextures;
import fr.iglee42.createqualityoflife.registries.ModIcons;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MainStatueTab extends AbstractStatueTab{
    public static final int NAME_Y = 0;
    public static final int SHOW_NAME_Y = 22;
    public static final int SMALL_Y = 44;
    public static final int SLIM_ARMS_Y = 66;
    public static final int NO_GRAVITY_Y = 88;
    public static final int INVUlNERABLE_Y = 110;
    public static final int INVISIBLE_Y = 132;

    private EditBox nameTextBox;

    private IconButton showNameButton;
    private Label showNameLabel;

    private IconButton smallButton;
    private Label smallLabel;

    private IconButton slimArmsButton;
    private Label slimArmsLabel;

    private IconButton ngButton;
    private Label ngLabel;

    private IconButton invulnerableButton;
    private Label invulnerableLabel;

    private IconButton invisibleButton;
    private Label invisibleLabel;

    public MainStatueTab(int index, ConfigureStatueScreen parent) {
        super(index, Items.NAME_TAG, parent,"statue.mainTab");
    }

    @Override
    public void initWidgets(int x, int y) {

        nameTextBox = new EditBox(Minecraft.getInstance().font,x + TEXT_BOX_X_OFFSET, y + NAME_Y + TEXT_Y_OFFSET,TEXT_BOX_WIDTH,20,Component.empty());
        nameTextBox.setValue((getExampleStatue().getCustomName() == null ? Component.empty() : getExampleStatue().getCustomName()).getString().replace('§','&'));
        nameTextBox.setResponder(s->{
            getExampleStatue().setCustomName(Component.literal(s.replace('&','§')));
            getParent().sendUpdatePacket();
        });
        nameTextBox.setBordered(false);
        nameTextBox.setTextColor(0xffffff);
        nameTextBox.setFocused(false);
        nameTextBox.setHint(Component.literal("Name"));

        List<Component> tip = new ArrayList<>();
        tip.add(CreateQOLLang.translateDirect("statue.showName"));
        tip.add(CreateLang.translateDirect("gui.schematicannon.optionDisabled")
                .withStyle(ChatFormatting.RED));
        List<Component> tipEnabled = new ArrayList<>(tip);
        tipEnabled.set(1, CreateLang.translateDirect("gui.schematicannon.optionEnabled")
               .withStyle(ChatFormatting.DARK_GREEN));

        showNameButton = new IconButton(x + BASE_X_OFFSET, y + SHOW_NAME_Y, AllIcons.I_PASSIVE);
        List<Component> finalTipEnabled1 = tipEnabled;
        showNameButton.withCallback(() -> {
            getExampleStatue().setCustomNameVisible(!getExampleStatue().isCustomNameVisible());
            showNameButton.green = getExampleStatue().isCustomNameVisible();
            showNameButton.getToolTip().clear();
            showNameButton.getToolTip().addAll(getExampleStatue().isCustomNameVisible() ? finalTipEnabled1 : tip);
            getParent().sendUpdatePacket();
        });
        showNameButton.green = getExampleStatue().isCustomNameVisible();
        showNameButton.getToolTip().clear();
        showNameButton.getToolTip().addAll(getExampleStatue().isCustomNameVisible() ? tipEnabled : tip);
        showNameLabel = new Label(x + LABEL_X_OFFSET,y + SHOW_NAME_Y + LABEL_Y_OFFSET,Component.literal("Show Name"));
        showNameLabel.text = Component.literal("Show Name");

        tip.clear();
        tip.add(CreateQOLLang.translateDirect("statue.small"));
        tip.add(CreateLang.translateDirect("gui.schematicannon.optionDisabled")
                .withStyle(ChatFormatting.RED));
        tipEnabled = new ArrayList<>(tip);
        tipEnabled.set(1, CreateLang.translateDirect("gui.schematicannon.optionEnabled")
                .withStyle(ChatFormatting.DARK_GREEN));

        smallButton = new IconButton(x + BASE_X_OFFSET, y + SMALL_Y, AllIcons.I_PRIORITY_LOW);
        List<Component> finalTipEnabled = tipEnabled;
        smallButton.withCallback(() -> {
            getExampleStatue().setSmall(!getExampleStatue().isSmall());
            smallButton.green = getExampleStatue().isSmall();
            smallButton.getToolTip().clear();
            smallButton.getToolTip().addAll(getExampleStatue().isSmall() ? finalTipEnabled : tip);
            getParent().sendUpdatePacket();
        });
        smallButton.green = getExampleStatue().isSmall();
        smallButton.getToolTip().clear();
        smallButton.getToolTip().addAll( getExampleStatue().isSmall() ? tipEnabled : tip);
        smallLabel = new Label(x + LABEL_X_OFFSET,y + SMALL_Y + LABEL_Y_OFFSET,Component.literal("Small"));
        smallLabel.text = Component.literal("Small");

        tip.clear();
        tip.add(CreateQOLLang.translateDirect("statue.slimArms"));
        tip.add(CreateLang.translateDirect("gui.schematicannon.optionDisabled")
                .withStyle(ChatFormatting.RED));
        tipEnabled = new ArrayList<>(tip);
        tipEnabled.set(1, CreateLang.translateDirect("gui.schematicannon.optionEnabled")
                .withStyle(ChatFormatting.DARK_GREEN));
        slimArmsButton = new IconButton(x + BASE_X_OFFSET, y + SLIM_ARMS_Y, AllIcons.I_TOOL_MOVE_XZ);
        List<Component> finalTipEnabled2 = tipEnabled;
        slimArmsButton.withCallback(() -> {
            getExampleStatue().setSlimArms(!getExampleStatue().isSlimArms());
            slimArmsButton.green = getExampleStatue().isSlimArms();
            slimArmsButton.getToolTip().clear();
            slimArmsButton.getToolTip().addAll(getExampleStatue().isSlimArms() ? finalTipEnabled2 : tip);
            getParent().sendUpdatePacket();
        });
        slimArmsButton.green = getExampleStatue().isSlimArms();
        slimArmsButton.getToolTip().clear();
        slimArmsButton.getToolTip().addAll( getExampleStatue().isSlimArms() ? tipEnabled : tip);
        slimArmsLabel = new Label(x + LABEL_X_OFFSET,y + SLIM_ARMS_Y + LABEL_Y_OFFSET,Component.literal("Slim Arms"));
        slimArmsLabel.text = Component.literal("Slim Arms");

        tip.clear();
        tip.add(CreateQOLLang.translateDirect("statue.noGravity"));
        tip.add(CreateLang.translateDirect("gui.schematicannon.optionDisabled")
                .withStyle(ChatFormatting.RED));
        tipEnabled = new ArrayList<>(tip);
        tipEnabled.set(1, CreateLang.translateDirect("gui.schematicannon.optionEnabled")
                .withStyle(ChatFormatting.DARK_GREEN));
        ngButton = new IconButton(x + BASE_X_OFFSET, y + NO_GRAVITY_Y, !getExampleStatue().isNoGravity() ? AllIcons.I_WHITELIST_OR : AllIcons.I_WHITELIST_NOT);
        List<Component> finalTipEnabled3 = tipEnabled;
        ngButton.withCallback(() -> {
            getExampleStatue().setNoGravity(!getExampleStatue().isNoGravity());
            ngButton.green = getExampleStatue().isNoGravity();
            ngButton.getToolTip().clear();
            ngButton.getToolTip().addAll(getExampleStatue().isNoGravity() ? finalTipEnabled3 : tip);
            ngButton.setIcon(!getExampleStatue().isNoGravity() ? AllIcons.I_WHITELIST_OR : AllIcons.I_WHITELIST_NOT);
            getParent().sendUpdatePacket();
        });
        ngButton.green = getExampleStatue().isNoGravity();
        ngButton.getToolTip().clear();
        ngButton.getToolTip().addAll( getExampleStatue().isNoGravity() ? tipEnabled : tip);
        ngLabel = new Label(x + LABEL_X_OFFSET,y + NO_GRAVITY_Y + LABEL_Y_OFFSET,Component.literal("No Gravity"));
        ngLabel.text = Component.literal("No Gravity");

        tip.clear();
        tip.add(CreateQOLLang.translateDirect("statue.invulnerable"));
        tip.add(CreateLang.translateDirect("gui.schematicannon.optionDisabled")
                .withStyle(ChatFormatting.RED));
        tip.add(CreateQOLLang.translateDirect("statue.invulnerable1"));
        tip.add(CreateQOLLang.translateDirect("statue.invulnerable2"));
        tip.add(CreateQOLLang.translateDirect("statue.invulnerable3"));
        tipEnabled = new ArrayList<>(tip);
        tipEnabled.set(1, CreateLang.translateDirect("gui.schematicannon.optionEnabled")
                .withStyle(ChatFormatting.DARK_GREEN));
        invulnerableButton = new IconButton(x + BASE_X_OFFSET, y + INVUlNERABLE_Y, getExampleStatue().isInvulnerable() ? ModIcons.I_LOCKED_STATUE : ModIcons.I_UNLOCKED_STATUE);
        List<Component> finalTipEnabled4 = tipEnabled;
        invulnerableButton.withCallback(() -> {
            getExampleStatue().setInvulnerable(!getExampleStatue().isInvulnerable());
            invulnerableButton.green = getExampleStatue().isInvulnerable();
            invulnerableButton.getToolTip().clear();
            invulnerableButton.getToolTip().addAll(getExampleStatue().isInvulnerable() ? finalTipEnabled4 : tip);
            invulnerableButton.setIcon(getExampleStatue().isInvulnerable() ? ModIcons.I_LOCKED_STATUE : ModIcons.I_UNLOCKED_STATUE);
            getParent().sendUpdatePacket();
        });
        invulnerableButton.green = getExampleStatue().isInvulnerable();
        invulnerableButton.getToolTip().clear();
        invulnerableButton.getToolTip().addAll( getExampleStatue().isInvulnerable() ? tipEnabled : tip);
        invulnerableLabel = new Label(x + LABEL_X_OFFSET,y + INVUlNERABLE_Y + LABEL_Y_OFFSET,Component.literal("Invulnerable"));
        invulnerableLabel.text = Component.literal("Invulnerable");

        if (getExampleStatue().hasOwner() && !Minecraft.getInstance().player.getUUID().equals(getExampleStatue().getOwner().get())){
            invulnerableButton.active = false;
            invulnerableButton.getToolTip().clear();
            invulnerableButton.getToolTip().add(CreateQOLLang.translateDirect("statue.ownerInvulnerable"));
            invulnerableButton.getToolTip().add(CreateQOLLang.translateDirect("statue.ownerInvulnerable1"));
            invulnerableButton.getToolTip().add(CreateQOLLang.translateDirect("statue.ownerInvulnerable2"));
        }

        tip.clear();
        tip.add(CreateQOLLang.translateDirect("statue.invisible"));
        tip.add(CreateLang.translateDirect("gui.schematicannon.optionDisabled")
                .withStyle(ChatFormatting.RED));
        tipEnabled = new ArrayList<>(tip);
        tipEnabled.set(1, CreateLang.translateDirect("gui.schematicannon.optionEnabled")
                .withStyle(ChatFormatting.DARK_GREEN));
        invisibleButton = new IconButton(x + BASE_X_OFFSET, y + INVISIBLE_Y, !getExampleStatue().isInvisible() ? AllIcons.I_FX_SURFACE_ON : AllIcons.I_FX_SURFACE_OFF);
        List<Component> finalTipEnabled5 = tipEnabled;
        invisibleButton.withCallback(() -> {
            getExampleStatue().setInvisible(!getExampleStatue().isInvisible());
            invisibleButton.green = getExampleStatue().isInvisible();
            invisibleButton.getToolTip().clear();
            invisibleButton.getToolTip().addAll(getExampleStatue().isInvisible() ? finalTipEnabled5 : tip);
            invisibleButton.setIcon(!getExampleStatue().isInvisible() ? AllIcons.I_FX_SURFACE_ON : AllIcons.I_FX_SURFACE_OFF);
            getParent().sendUpdatePacket();
        });
        invisibleButton.green = getExampleStatue().isInvisible();
        invisibleButton.getToolTip().clear();
        invisibleButton.getToolTip().addAll( getExampleStatue().isInvisible() ? tipEnabled : tip);
        invisibleLabel = new Label(x + LABEL_X_OFFSET,y + INVISIBLE_Y + LABEL_Y_OFFSET,Component.literal("Invisible"));
        invisibleLabel.text = Component.literal("Invisible");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partial, int x, int y) {
        ModGuiTextures.TEXT_BOX.render(graphics,x + BASE_X_OFFSET, y + NAME_Y);
    }


    @Override
    public void forEachWidgets(Consumer<AbstractWidget> function) {
        function.accept(nameTextBox);
        function.accept(showNameButton);
        function.accept(showNameLabel);
        function.accept(smallButton);
        function.accept(smallLabel);
        function.accept(slimArmsButton);
        function.accept(slimArmsLabel);
        function.accept(ngButton);
        function.accept(ngLabel);
        function.accept(invulnerableButton);
        function.accept(invulnerableLabel);
        function.accept(invisibleButton);
        function.accept(invisibleLabel);
    }
}
