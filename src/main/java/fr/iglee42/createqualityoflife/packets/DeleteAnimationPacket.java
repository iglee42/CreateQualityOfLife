package fr.iglee42.createqualityoflife.packets;

import fr.iglee42.createqualityoflife.registries.ModPackets;
import fr.iglee42.createqualityoflife.statue.animation.PublishedAnimationsManager;
import fr.iglee42.createqualityoflife.statue.animation.StatueAnimation;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public record DeleteAnimationPacket(UUID animation) implements ServerboundPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, DeleteAnimationPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, DeleteAnimationPacket::animation,
            DeleteAnimationPacket::new
    );

    @Override
    public void handle(ServerPlayer player) {
        if (player != null) {
            PublishedAnimationsManager manager = PublishedAnimationsManager.get(player.level());
            manager.deleteAnimation(animation());
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return ModPackets.DELETE_ANIMATION;
    }
}
