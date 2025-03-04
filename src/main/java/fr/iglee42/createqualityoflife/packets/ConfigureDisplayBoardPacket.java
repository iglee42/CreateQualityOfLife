package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import fr.iglee42.createqualityoflife.registries.ModPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;

public class ConfigureDisplayBoardPacket extends BlockEntityConfigurationPacket<FlapDisplayBlockEntity> {

    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigureDisplayBoardPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,p->p.pos,
            ByteBufCodecs.INT, p->p.lineIndex,
            ByteBufCodecs.INT, p->p.dyeColor,
            ByteBufCodecs.STRING_UTF8,p->p.text,
            ByteBufCodecs.BOOL,p->p.glowing,
            ConfigureDisplayBoardPacket::new
    );

    private int lineIndex,dyeColor;
    private String text;
    private boolean glowing;

    public ConfigureDisplayBoardPacket(BlockPos pos,int lineIndex,int dyeColor, String text, boolean glowing) {
        super(pos);
        this.lineIndex = lineIndex;
        this.dyeColor = dyeColor;
        this.text = text;
        this.glowing = glowing;
    }


    @Override
    protected void applySettings(ServerPlayer serverPlayer, FlapDisplayBlockEntity be) {
        be.setColour(lineIndex, DyeColor.byId(dyeColor));
        be.glowingLines[lineIndex] = glowing;
        be.applyTextManually(lineIndex,Component.literal(text));
        be.notifyUpdate();
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return ModPackets.CONFIGURE_DISPLAY_BOARD;
    }
}
