package fr.iglee42.createqualityoflife.client.screens;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Indicator;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.packets.ConfigureDisplayBoardPacket;
import fr.iglee42.createqualityoflife.registries.QOLGuiTextures;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.network.PacketDistributor;

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
        QOLGuiTextures bg = QOLGuiTextures.DISPLAY_BOARD;
        setWindowSize(bg.width,bg.height);
        setWindowOffset(0,0);
        super.init();

        text = be.getLines().get(lineIndex).getSections().get(0).getText() != null ? be.getLines().get(lineIndex).getSections().get(0).getText().getString() : "";
        textBox = new EditBox(this.font,guiLeft + 58,guiTop + 29,121,8,Component.empty());
        textBox.setValue(text);
        textBox.setResponder(s->text = s);
        textBox.setBordered(false);
        textBox.setTextColor(0xffffff);
        textBox.setFocused(false);
        textBox.setMaxLength(be.getMaxCharCount());

        addRenderableWidget(textBox);

        glowingButton = new IconButton(guiLeft + 53, guiTop + 46, be.glowingLines[lineIndex] ? AllIcons.I_FX_SURFACE_ON : AllIcons.I_FX_SURFACE_OFF);
        glowingButton.withCallback(()-> {
            glowingButton.setIcon(glowingIndicator.state == Indicator.State.OFF ? AllIcons.I_FX_SURFACE_ON : AllIcons.I_FX_SURFACE_OFF);
            glowingIndicator.state = glowingIndicator.state == Indicator.State.OFF ? Indicator.State.GREEN : Indicator.State.OFF;
        });
        glowingButton.setToolTip(CreateQOLLang.translateDirect("gui.display_board.glowing"));

        glowingIndicator = new Indicator(guiLeft + 53,guiTop + 64,Component.empty());
        glowingIndicator.state = be.glowingLines[lineIndex] ? Indicator.State.GREEN : Indicator.State.OFF;

        addRenderableWidget(glowingButton);
        addRenderableWidget(glowingIndicator);

        colorScrollInput = new SelectionScrollInput(guiLeft + 144, guiTop + 49, 55, 16);
        colorScrollInputLabel = new Label(guiLeft + 144, guiTop + 52, Component.empty()).withShadow();
        colorScrollInput.forOptions(Arrays.stream(DyeColor.values()).map(DyeColor::getSerializedName)
                .map(s -> Component.translatable("color.minecraft." + s.toLowerCase())
                        .withStyle(Style.EMPTY.withColor(DyeColor.byName(!s.equals("black") ?s.toLowerCase() : "gray",DyeColor.WHITE).getTextColor()))).toList()).calling(i->{
            DyeColor color = DyeColor.byId(i);
            colorScrollInputLabel.colored(color != DyeColor.BLACK ? color.getTextColor() : DyeColor.GRAY.getTextColor());
        }).writingTo(colorScrollInputLabel);

        colorScrollInput.setState(be.colour[lineIndex] != null ? be.colour[lineIndex].getId() : 0);
        DyeColor color = DyeColor.byId(colorScrollInput.getState());
        colorScrollInputLabel.colored(color != DyeColor.BLACK ? color.getTextColor() : DyeColor.GRAY.getTextColor());
        addRenderableWidget(colorScrollInput);
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        QOLGuiTextures.DISPLAY_BOARD.render(graphics, guiLeft - 2, guiTop);
        colorScrollInput.render(graphics, mouseX, mouseY, partialTicks);
        colorScrollInputLabel.render(graphics, mouseX, mouseY, partialTicks);

    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        PacketDistributor.sendToServer(new ConfigureDisplayBoardPacket(be.getBlockPos(),lineIndex,colorScrollInput.getState(),text,glowingIndicator.state != Indicator.State.OFF));

        super.onClose();

    }
}
