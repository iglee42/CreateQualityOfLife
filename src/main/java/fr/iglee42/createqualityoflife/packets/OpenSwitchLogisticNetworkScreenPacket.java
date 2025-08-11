package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import fr.iglee42.createqualityoflife.menus.ChooseLogisticNetworkMenu;
import fr.iglee42.createqualityoflife.registries.QOLPackets;
import fr.iglee42.createqualityoflife.utils.CommonKeysHandler;
import fr.iglee42.createqualityoflife.utils.LogisticsNetworkExtension;
import fr.iglee42.createqualityoflife.utils.NetworkPermission;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class OpenSwitchLogisticNetworkScreenPacket extends BlockEntityConfigurationPacket<StockManagerBlockEntity> {
    public static final StreamCodec<ByteBuf, OpenSwitchLogisticNetworkScreenPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet->packet.pos,
            OpenSwitchLogisticNetworkScreenPacket::new
    );

    public OpenSwitchLogisticNetworkScreenPacket(BlockPos pos) {
        super(pos);
    }

    @Override
    protected void applySettings(ServerPlayer player, StockManagerBlockEntity stbe) {
        List<ChooseLogisticNetworkMenu.LogisticNetworksInfos> infos = Create.LOGISTICS.logisticsNetworks.values().stream().filter(n->((LogisticsNetworkExtension)n).createQOL$hasPlayerPermission(player, NetworkPermission.MEMBER) || n.owner == null).map(ChooseLogisticNetworkMenu.LogisticNetworksInfos::fromLogisticNetwork).toList();
        player.openMenu(stbe.new ChooseNetworkProvider(), buf -> {
            ChooseLogisticNetworkMenu.LogisticNetworksInfos.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf,infos);
            buf.writeBlockPos(pos);
        });
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return QOLPackets.OPEN_CHOOSE_NETWORK;
    }
}
