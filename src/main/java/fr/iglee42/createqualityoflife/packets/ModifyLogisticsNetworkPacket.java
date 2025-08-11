package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import fr.iglee42.createqualityoflife.menus.ChooseLogisticNetworkMenu;
import fr.iglee42.createqualityoflife.registries.QOLPackets;
import fr.iglee42.createqualityoflife.utils.LogisticsNetworkExtension;
import fr.iglee42.createqualityoflife.utils.NetworkPermission;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class ModifyLogisticsNetworkPacket extends BlockEntityConfigurationPacket<StockManagerBlockEntity> {
    public static final StreamCodec<ByteBuf, ModifyLogisticsNetworkPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet->packet.pos,
            ByteBufCodecs.STRING_UTF8, packet->packet.name,
            ModifyLogisticsNetworkPacket::new
    );

    private final String name;

    public ModifyLogisticsNetworkPacket(BlockPos pos,String name) {
        super(pos);
        this.name = name;
    }

    @Override
    protected void applySettings(ServerPlayer player, StockManagerBlockEntity stbe) {
        LogisticsNetwork network = Create.LOGISTICS.logisticsNetworks.get(stbe.behaviour.freqId);
        if (network != null){
            if (!name.isEmpty() && ((LogisticsNetworkExtension)network).createQOL$hasPlayerPermission(player, NetworkPermission.OWNER))((LogisticsNetworkExtension)network).createQOL$setName(name);
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return QOLPackets.MODIFY_LOGISTICS_NETWORK;
    }
}
