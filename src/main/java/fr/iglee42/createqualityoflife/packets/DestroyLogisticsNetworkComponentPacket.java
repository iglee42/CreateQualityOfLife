package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import com.simibubi.create.foundation.utility.BlockHelper;
import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLPackets;
import fr.iglee42.createqualityoflife.utils.DestroyUtils;
import fr.iglee42.createqualityoflife.utils.LogisticsNetworkExtension;
import fr.iglee42.createqualityoflife.utils.NetworkDestructionLevel;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class DestroyLogisticsNetworkComponentPacket extends BlockEntityConfigurationPacket<StockManagerBlockEntity> {
    public static final StreamCodec<ByteBuf, DestroyLogisticsNetworkComponentPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet->packet.pos,
            BlockPos.STREAM_CODEC, packet->packet.destroyedPos,
            DestroyLogisticsNetworkComponentPacket::new
    );

    private final BlockPos destroyedPos;

    public DestroyLogisticsNetworkComponentPacket(BlockPos pos,BlockPos destroyedPos) {
        super(pos);
        this.destroyedPos = destroyedPos;
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
    public PacketTypeProvider getTypeProvider() {
        return QOLPackets.DESTROY_LOGISTICS_NETWORK_COMPONENT;
    }
}
