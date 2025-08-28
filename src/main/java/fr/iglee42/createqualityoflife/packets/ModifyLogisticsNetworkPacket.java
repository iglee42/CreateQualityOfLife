package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import fr.iglee42.createqualityoflife.utils.LogisticsNetworkExtension;
import fr.iglee42.createqualityoflife.utils.NetworkPermission;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class ModifyLogisticsNetworkPacket extends BlockEntityConfigurationPacket<StockManagerBlockEntity> {

    private String name;

    public ModifyLogisticsNetworkPacket(BlockPos pos,String name) {
        super(pos);
        this.name = name;
    }

    public ModifyLogisticsNetworkPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        name = buffer.readUtf();
    }

    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeUtf(name);
    }

    @Override
    protected void applySettings(ServerPlayer player, StockManagerBlockEntity stbe) {
        LogisticsNetwork network = Create.LOGISTICS.logisticsNetworks.get(stbe.behaviour.freqId);
        if (network != null){
            if (!name.isEmpty() && ((LogisticsNetworkExtension)network).createQOL$hasPlayerPermission(player, NetworkPermission.OWNER))((LogisticsNetworkExtension)network).createQOL$setName(name);
        }
    }

    @Override
    protected void applySettings(StockManagerBlockEntity stockManagerBlockEntity) {}
}
