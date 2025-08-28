package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import fr.iglee42.createqualityoflife.menus.ChooseLogisticNetworkMenu;
import fr.iglee42.createqualityoflife.utils.LogisticsNetworkExtension;
import fr.iglee42.createqualityoflife.utils.NetworkPermission;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class OpenSwitchLogisticNetworkScreenPacket extends BlockEntityConfigurationPacket<StockManagerBlockEntity> {
    public OpenSwitchLogisticNetworkScreenPacket(BlockPos pos) {
        super(pos);
    }

    public OpenSwitchLogisticNetworkScreenPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    protected void applySettings(ServerPlayer player, StockManagerBlockEntity stbe) {
        List<ChooseLogisticNetworkMenu.LogisticNetworksInfos> infos = Create.LOGISTICS.logisticsNetworks.values().stream().filter(n->((LogisticsNetworkExtension)n).createQOL$hasPlayerPermission(player, NetworkPermission.MEMBER) || n.owner == null).map(ChooseLogisticNetworkMenu.LogisticNetworksInfos::fromLogisticNetwork).toList();
        NetworkHooks.openScreen(player,stbe.new ChooseNetworkProvider(), buf -> {
            buf.writeCollection(infos, (b,i)->i.write(b));
            buf.writeBlockPos(pos);
        });
    }

    @Override
    protected void applySettings(StockManagerBlockEntity stockManagerBlockEntity) {}

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {}

    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {}
}
