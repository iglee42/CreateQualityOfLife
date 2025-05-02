package fr.iglee42.createqualityoflife.registries;

import com.simibubi.create.Create;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.packets.*;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Locale;

public enum ModPackets implements BasePacketPayload.PacketTypeProvider {

    //Client to Server

    CONFIGURE_DISPLAY_BOARD(ConfigureDisplayBoardPacket.class,ConfigureDisplayBoardPacket.STREAM_CODEC),
    TOGGLE_FANS(ToggleFansPacket.class,ToggleFansPacket.STREAM_CODEC),
    TOGGLE_HOVER(ToggleHoverPacket .class, ToggleHoverPacket.STREAM_CODEC),
    INPUTS_UPDATE(UpdateInputsPacket.class, UpdateInputsPacket.STREAM_CODEC),
    CHANGE_ARMOR_COMPONENT(ChangeArmorComponentPacket.class, ChangeArmorComponentPacket.STREAM_CODEC),
    SAVE_STATUE_CONFIG(SaveStatueConfigPacket.class, SaveStatueConfigPacket.STREAM_CODEC),
    //Server To Client

    ;
    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> ModPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
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
        for (ModPackets packet : ModPackets.values()) {
            packetRegistry.registerPacket(packet.type);
        }
        packetRegistry.registerAllPackets();
    }
}
