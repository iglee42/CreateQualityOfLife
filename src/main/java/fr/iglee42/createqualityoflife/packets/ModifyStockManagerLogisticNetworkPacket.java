package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import fr.iglee42.createqualityoflife.menus.ChooseLogisticNetworkMenu;
import fr.iglee42.createqualityoflife.registries.QOLPackets;
import fr.iglee42.createqualityoflife.utils.LogisticsNetworkExtension;
import fr.iglee42.createqualityoflife.utils.NetworkDestructionLevel;
import fr.iglee42.createqualityoflife.utils.NetworkPermission;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ModifyStockManagerLogisticNetworkPacket extends BlockEntityConfigurationPacket<StockManagerBlockEntity> {
    public static final StreamCodec<ByteBuf, ModifyStockManagerLogisticNetworkPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet->packet.pos,
            UUIDUtil.STREAM_CODEC, packet->packet.networkId,
            ModifyStockManagerLogisticNetworkPacket::new
    );

    private final UUID networkId;

    public ModifyStockManagerLogisticNetworkPacket(BlockPos pos,UUID networkId) {
        super(pos);
        this.networkId = networkId;
    }

    @Override
    protected void applySettings(ServerPlayer player, StockManagerBlockEntity stbe) {
        LogisticsNetwork network = Create.LOGISTICS.logisticsNetworks.get(networkId);
        if (network != null){
            stbe.behaviour.unload();
            stbe.behaviour.destroy();
            stbe.behaviour.freqId = networkId;
            stbe.behaviour.initialize();
            Create.LOGISTICS.linkAdded(networkId, GlobalPos.of(stbe.getLevel().dimension(), stbe.getBlockPos()),null);
            Create.LOGISTICS.linkLoaded(networkId, GlobalPos.of(stbe.getLevel().dimension(), stbe.getBlockPos()));
            stbe.setChanged();
            network = Create.LOGISTICS.logisticsNetworks.get(networkId);
            boolean showLockOption =
                    stbe.behaviour.mayAdministrate(player) && Create.LOGISTICS.isLockable(stbe.behaviour.freqId);
            boolean isCurrentlyLocked = Create.LOGISTICS.isLocked(stbe.behaviour.freqId);
            String name = ((LogisticsNetworkExtension) network).createQOL$getName();

            int links = network.totalLinks.size();
            Map<UUID, NetworkPermission> permissions = new HashMap<>(((LogisticsNetworkExtension) network).createQOL$getPlayersPermission());
            if (network.owner != null) permissions.put(network.owner,NetworkPermission.OWNER);
            player.level().players().stream().filter(p->!permissions.containsKey(p.getUUID()))
                    .forEach(p->permissions.put(p.getUUID(),NetworkPermission.NONE));
            NetworkDestructionLevel desLevel = ((LogisticsNetworkExtension)network).createQOL$getDestructionLevel();
            boolean isOwner = player.getUUID().equals(network.owner);

            player.openMenu(stbe.new StockManagerProvider(), buf -> {
                buf.writeBoolean(showLockOption);
                buf.writeBoolean(isOwner);
                buf.writeBoolean(isCurrentlyLocked);
                buf.writeUtf(name);
                buf.writeInt(links);
                NetworkDestructionLevel.STREAM_CODEC.encode(buf,desLevel);
                buf.writeBoolean(desLevel.canDestroy(stbe.behaviour.freqId,player));
                LogisticsNetworkExtension.PERMISSIONS_STREAM_CODEC.encode(buf,permissions);
                buf.writeBlockPos(pos);
            });
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return QOLPackets.MODIFY_STOCK_MANAGER_LOGISTICS_NETWORK;
    }
}
