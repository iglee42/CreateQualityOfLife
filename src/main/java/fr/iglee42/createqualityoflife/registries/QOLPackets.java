package fr.iglee42.createqualityoflife.registries;

import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.packets.*;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Locale;

public enum QOLPackets implements BasePacketPayload.PacketTypeProvider {

    //Client to Server

    CONFIGURE_DISPLAY_BOARD(ConfigureDisplayBoardPacket.class,ConfigureDisplayBoardPacket.STREAM_CODEC),
    TOGGLE_FANS(ToggleFansPacket.class,ToggleFansPacket.STREAM_CODEC),
    TOGGLE_HOVER(ToggleHoverPacket .class, ToggleHoverPacket.STREAM_CODEC),
    TOGGLE_ELYTRA(ToggleElytraPacket .class, ToggleElytraPacket.STREAM_CODEC),
    INPUTS_UPDATE(UpdateInputsPacket.class, UpdateInputsPacket.STREAM_CODEC),
    CHANGE_ARMOR_COMPONENT(ChangeArmorComponentPacket.class, ChangeArmorComponentPacket.STREAM_CODEC),
    SAVE_STATUE_CONFIG(SaveStatueConfigPacket.class, SaveStatueConfigPacket.STREAM_CODEC),
    PUBLISH_ANIMATION(PublishAnimationPacket.class, PublishAnimationPacket.STREAM_CODEC),
    DELETE_ANIMATION(DeleteAnimationPacket.class, DeleteAnimationPacket.STREAM_CODEC),
    //Server To Client

    SYNC_ANIMATIONS(SyncAnimationsConfigPacket.class, SyncAnimationsConfigPacket.STREAM_CODEC),

    ;
    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> QOLPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
                new CustomPacketPayload.Type<>(CreateQOL.asResource(name)),
                clazz, codec
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) this.type.type();
    }

    public static void register() {
        CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(CreateQOL.MODID, 1);
        for (QOLPackets packet : QOLPackets.values()) {
            packetRegistry.registerPacket(packet.type);
        }
        packetRegistry.registerAllPackets();
    }
}
