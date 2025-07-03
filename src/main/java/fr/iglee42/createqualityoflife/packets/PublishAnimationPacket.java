package fr.iglee42.createqualityoflife.packets;

import fr.iglee42.createqualityoflife.registries.QOLPackets;
import fr.iglee42.createqualityoflife.statue.animation.PublishedAnimationsManager;
import fr.iglee42.createqualityoflife.statue.animation.StatueAnimation;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public record PublishAnimationPacket(UUID publisher,String name, StatueAnimation animation) implements ServerboundPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, PublishAnimationPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, PublishAnimationPacket::publisher,
            ByteBufCodecs.STRING_UTF8, PublishAnimationPacket::name,
            StatueAnimation.STREAM_CODEC, PublishAnimationPacket::animation,
            PublishAnimationPacket::new
    );

    @Override
    public void handle(ServerPlayer player) {
        if (player != null) {
            PublishedAnimationsManager manager = PublishedAnimationsManager.get(player.level());
            manager.publishAnimation(publisher(),name(),animation());
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return QOLPackets.PUBLISH_ANIMATION;
    }
}
