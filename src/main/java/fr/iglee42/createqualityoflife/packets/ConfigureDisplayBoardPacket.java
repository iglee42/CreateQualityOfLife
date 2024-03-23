package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.foundation.gui.widget.Indicator;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import fr.iglee42.createqualityoflife.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.network.NetworkEvent;

public class ConfigureDisplayBoardPacket extends BlockEntityConfigurationPacket<FlapDisplayBlockEntity> {

    private int lineIndex,dyeColor;
    private String text;
    private boolean glowing;

    public ConfigureDisplayBoardPacket(BlockPos pos,int lineIndex, String text, boolean glowing,int dyeColor) {
        super(pos);
        this.lineIndex = lineIndex;
        this.dyeColor = dyeColor;
        this.text = text;
        this.glowing = glowing;
    }

    public ConfigureDisplayBoardPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    protected void readSettings(FriendlyByteBuf buffer) {
        this.lineIndex = buffer.readInt();
        this.text = Utils.readStringFromBuffer(buffer);
        this.glowing = buffer.readBoolean();
        this.dyeColor = buffer.readInt();

    }


    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeInt(lineIndex);
        Utils.saveStringToBuffer(buffer,text);
        buffer.writeBoolean(glowing);
        buffer.writeInt(dyeColor);
    }

    @Override
    protected void applySettings(FlapDisplayBlockEntity be) {
        be.setColour(lineIndex, DyeColor.byId(dyeColor));
        be.glowingLines[lineIndex] = glowing;
        be.applyTextManually(lineIndex,text);
        be.notifyUpdate();
    }
}
