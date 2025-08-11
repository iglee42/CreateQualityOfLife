package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.AllPackets;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;

import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import fr.iglee42.createqualityoflife.registries.QOLPackets;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class StockManagerLockPacket extends BlockEntityConfigurationPacket<StockManagerBlockEntity> {
	public static final StreamCodec<ByteBuf, StockManagerLockPacket> STREAM_CODEC = StreamCodec.composite(
	    BlockPos.STREAM_CODEC, p -> p.pos,
		ByteBufCodecs.BOOL, p -> p.lock,
	    StockManagerLockPacket::new
	);

	private final boolean lock;

	public StockManagerLockPacket(BlockPos pos, boolean lock) {
		super(pos);
		this.lock = lock;
	}

	@Override
	public PacketTypeProvider getTypeProvider() {
		return QOLPackets.LOCK_STOCK_MANAGER;
	}

	@Override
	protected void applySettings(ServerPlayer player, StockManagerBlockEntity be) {
		if (!be.behaviour.mayAdministrate(player))
			return;
		LogisticsNetwork network = Create.LOGISTICS.logisticsNetworks.get(be.behaviour.freqId);
		if (network != null) {
			network.locked = lock;
			Create.LOGISTICS.markDirty();
		}
	}

}
