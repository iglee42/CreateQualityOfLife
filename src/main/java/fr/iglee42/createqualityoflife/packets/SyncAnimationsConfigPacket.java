package fr.iglee42.createqualityoflife.packets;

import fr.iglee42.createqualityoflife.registries.QOLPackets;
import fr.iglee42.createqualityoflife.statue.animation.PublishedAnimationsManager;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SyncAnimationsConfigPacket(List<PublishedAnimationsManager.PublishedAnimation> animations) implements ClientboundPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, SyncAnimationsConfigPacket> STREAM_CODEC = StreamCodec.composite(
            PublishedAnimationsManager.STREAM_CODEC, SyncAnimationsConfigPacket::animations,
            SyncAnimationsConfigPacket::new
    );

    @Override
    public void handle(LocalPlayer player) {
        PublishedAnimationsManager.CLIENT_ANIMATIONS = animations();
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return QOLPackets.SYNC_ANIMATIONS;
    }
}
