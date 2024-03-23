package fr.iglee42.createqualityoflife.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Indicator;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.utility.Components;
import fr.iglee42.createqualityoflife.packets.ConfigureDisplayBoardPacket;
import fr.iglee42.createqualityoflife.registries.ModGuiTextures;
import fr.iglee42.createqualityoflife.registries.ModPackets;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.DyeColor;

import java.util.Arrays;

public class DisplayBoardEditScreen extends AbstractSimiScreen {


    private FlapDisplayBlockEntity be;
    private final int lineIndex;

    private String text;

    private EditBox textBox;

    private IconButton glowingButton;
    private Indicator glowingIndicator;


    private SelectionScrollInput colorScrollInput;
    private Label colorScrollInputLabel;



    public DisplayBoardEditScreen(FlapDisplayBlockEntity be, int lineIndex) {
        super(Component.translatable(AllBlocks.DISPLAY_BOARD.get().getDescriptionId()));
        this.be = be;
        this.lineIndex = lineIndex;
    }

    @Override
    protected void init() {
        ModGuiTextures bg = ModGuiTextures.DISPLAY_BOARD;
        setWindowSize(bg.width,bg.height);
        setWindowOffset(0,0);
        super.init();

        text = be.getLines().get(lineIndex).getSections().get(0).getText() != null ? be.getLines().get(lineIndex).getSections().get(0).getText().getString() : "";
        textBox = new EditBox(this.font,guiLeft + 58,guiTop + 29,121,8,Components.immutableEmpty());
        textBox.setValue(text);
        textBox.setResponder(s->text = s);
        textBox.setBordered(false);
        textBox.setTextColor(0xffffff);
        textBox.setMaxLength(be.getMaxCharCount());

        addRenderableWidget(textBox);

        glowingButton = new IconButton(guiLeft + 53, guiTop + 46, be.glowingLines[lineIndex] ? AllIcons.I_FX_SURFACE_ON : AllIcons.I_FX_SURFACE_OFF);
        glowingButton.withCallback(()-> {
            glowingButton.setIcon(glowingIndicator.state == Indicator.State.OFF ? AllIcons.I_FX_SURFACE_ON : AllIcons.I_FX_SURFACE_OFF);
            glowingIndicator.state = glowingIndicator.state == Indicator.State.OFF ? Indicator.State.GREEN : Indicator.State.OFF;
        });
        glowingButton.setToolTip(Component.literal("Glowing"));

        glowingIndicator = new Indicator(guiLeft + 53,guiTop + 64,Components.immutableEmpty());
        glowingIndicator.state = be.glowingLines[lineIndex] ? Indicator.State.GREEN : Indicator.State.OFF;

        addRenderableWidget(glowingButton);
        addRenderableWidget(glowingIndicator);

        colorScrollInput = new SelectionScrollInput(guiLeft + 144, guiTop + 49, 55, 16);
        colorScrollInputLabel = new Label(guiLeft + 144, guiTop + 52, Components.immutableEmpty()).withShadow();
        colorScrollInput.forOptions(Arrays.stream(DyeColor.values()).map(DyeColor::getSerializedName).map(s->{
            String firstLetter = String.valueOf(s.charAt(0)).toUpperCase();
            return firstLetter + s.substring(1).replace("_"," ");
        }).map(s->Component.literal(s).withStyle(Style.EMPTY.withColor(DyeColor.byName(!s.equals("Black") ?s.replace(" ","_").toLowerCase() : "gray",DyeColor.WHITE).getTextColor()))).toList()).calling(i->{
            DyeColor color = DyeColor.byId(i);
            colorScrollInputLabel.colored(color != DyeColor.BLACK ? color.getTextColor() : DyeColor.GRAY.getTextColor());
        }).writingTo(colorScrollInputLabel);

        colorScrollInput.setState(be.colour[lineIndex] != null ? be.colour[lineIndex].getId() : 0);
        DyeColor color = DyeColor.byId(colorScrollInput.getState());
        colorScrollInputLabel.colored(color != DyeColor.BLACK ? color.getTextColor() : DyeColor.GRAY.getTextColor());
        addRenderableWidget(colorScrollInput);
    }

    @Override
    protected void renderWindow(PoseStack graphics, int mouseX, int mouseY, float partialTicks) {
        fillGradient(graphics,0, 0, this.width, this.height, -1072689136, -804253680);
        ModGuiTextures.DISPLAY_BOARD.render(graphics, guiLeft - 2, guiTop);
        colorScrollInput.render(graphics, mouseX, mouseY, partialTicks);
        colorScrollInputLabel.render(graphics, mouseX, mouseY, partialTicks);

    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        ModPackets.getChannel().sendToServer(new ConfigureDisplayBoardPacket(be.getBlockPos(),lineIndex,text,glowingIndicator.state != Indicator.State.OFF,colorScrollInput.getState()));

        super.onClose();

    }
}
