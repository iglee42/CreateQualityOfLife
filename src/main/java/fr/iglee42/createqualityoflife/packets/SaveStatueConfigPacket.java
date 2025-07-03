package fr.iglee42.createqualityoflife.packets;

import fr.iglee42.createqualityoflife.registries.QOLPackets;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public record SaveStatueConfigPacket(int id, CompoundTag nbts) implements ServerboundPacketPayload {
    public static final StreamCodec<ByteBuf, SaveStatueConfigPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SaveStatueConfigPacket::id,
            ByteBufCodecs.COMPOUND_TAG, SaveStatueConfigPacket::nbts,
            SaveStatueConfigPacket::new
    );

    @Override
    public void handle(ServerPlayer player) {
        if (player != null) {
            if (player.level().getEntity(id) != null){
                player.level().getEntity(id).load(nbts);
            }
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return QOLPackets.SAVE_STATUE_CONFIG;
    }
}
