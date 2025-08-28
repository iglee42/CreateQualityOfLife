package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class StockManagerLockPacket extends BlockEntityConfigurationPacket<StockManagerBlockEntity> {
	private boolean lock;

	public StockManagerLockPacket(BlockPos pos, boolean lock) {
		super(pos);
		this.lock = lock;
	}

	public StockManagerLockPacket(FriendlyByteBuf buffer) {
		super(buffer);
	}

	@Override
	protected void readSettings(FriendlyByteBuf buffer) {
		lock = buffer.readBoolean();
	}

	@Override
	protected void writeSettings(FriendlyByteBuf buffer) {
		buffer.writeBoolean(lock);
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

	@Override
	protected void applySettings(StockManagerBlockEntity stockManagerBlockEntity) {}
}
