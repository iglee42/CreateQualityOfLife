package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import fr.iglee42.createqualityoflife.registries.QOLPackets;
import fr.iglee42.createqualityoflife.utils.LogisticsNetworkExtension;
import fr.iglee42.createqualityoflife.utils.NetworkDestructionLevel;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class StockManagerDestructionLevelPacket extends BlockEntityConfigurationPacket<StockManagerBlockEntity> {
	public static final StreamCodec<ByteBuf, StockManagerDestructionLevelPacket> STREAM_CODEC = StreamCodec.composite(
	    BlockPos.STREAM_CODEC, p -> p.pos,
		NetworkDestructionLevel.STREAM_CODEC, p -> p.level,
	    StockManagerDestructionLevelPacket::new
	);

	private final NetworkDestructionLevel level;

	public StockManagerDestructionLevelPacket(BlockPos pos, NetworkDestructionLevel level) {
		super(pos);
		this.level = level;
	}

	@Override
	public PacketTypeProvider getTypeProvider() {
		return QOLPackets.DESTRUCTION_LEVEL_STOCK_MANAGER;
	}

	@Override
	protected void applySettings(ServerPlayer player, StockManagerBlockEntity be) {
		if (!be.behaviour.mayAdministrate(player))
			return;
		LogisticsNetwork network = Create.LOGISTICS.logisticsNetworks.get(be.behaviour.freqId);
		if (network != null) {
			((LogisticsNetworkExtension)network).createQOL$setDestructionLevel(level);
			Create.LOGISTICS.markDirty();
		}
	}

}
