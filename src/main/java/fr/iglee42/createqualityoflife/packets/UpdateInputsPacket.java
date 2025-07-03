package fr.iglee42.createqualityoflife.packets;

import fr.iglee42.createqualityoflife.registries.QOLPackets;
import fr.iglee42.createqualityoflife.utils.CommonKeysHandler;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public record UpdateInputsPacket(boolean up, boolean down, boolean forwards, boolean backwards, boolean left, boolean right) implements ServerboundPacketPayload {
    public static final StreamCodec<ByteBuf, UpdateInputsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,UpdateInputsPacket::up,
            ByteBufCodecs.BOOL,UpdateInputsPacket::down,
            ByteBufCodecs.BOOL,UpdateInputsPacket::forwards,
            ByteBufCodecs.BOOL,UpdateInputsPacket::backwards,
            ByteBufCodecs.BOOL,UpdateInputsPacket::left,
            ByteBufCodecs.BOOL,UpdateInputsPacket::right,
            UpdateInputsPacket::new
    );

    @Override
    public void handle(ServerPlayer player) {
        if (player != null) {
            CommonKeysHandler.update(player, up, down, forwards, backwards, left, right);
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return QOLPackets.INPUTS_UPDATE;
    }
}
