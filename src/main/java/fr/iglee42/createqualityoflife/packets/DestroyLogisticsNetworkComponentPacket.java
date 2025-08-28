package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import com.simibubi.create.foundation.utility.BlockHelper;
import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.utils.LogisticsNetworkExtension;
import fr.iglee42.createqualityoflife.utils.NetworkDestructionLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class DestroyLogisticsNetworkComponentPacket extends BlockEntityConfigurationPacket<StockManagerBlockEntity> {
    private BlockPos destroyedPos;

    public DestroyLogisticsNetworkComponentPacket(BlockPos pos,BlockPos destroyedPos) {
        super(pos);
        this.destroyedPos = destroyedPos;
    }

    public DestroyLogisticsNetworkComponentPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(destroyedPos);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        destroyedPos = buffer.readBlockPos();
    }

    @Override
    protected void applySettings(ServerPlayer player, StockManagerBlockEntity stbe) {
        LogisticsNetwork network = Create.LOGISTICS.logisticsNetworks.get(stbe.behaviour.freqId);
        if (network != null){
            NetworkDestructionLevel level = ((LogisticsNetworkExtension)network).createQOL$getDestructionLevel();
            if (destroyedPos.closerThan(player.blockPosition(), CreateQOLConfigs.server().logistics.stockManagerMaxDestroyDistance.get())) {
                if (level.canDestroy(network.id, player)) {
                    BlockHelper.destroyBlockAs(player.level(), destroyedPos, player, ItemStack.EMPTY, 1, is ->
                            Block.popResource(player.level(), destroyedPos, is));
                }
            }
        }
    }

    @Override
    protected void applySettings(StockManagerBlockEntity stockManagerBlockEntity) {}

}
