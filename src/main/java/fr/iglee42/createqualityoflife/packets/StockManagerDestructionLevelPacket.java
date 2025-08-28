package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import fr.iglee42.createqualityoflife.utils.LogisticsNetworkExtension;
import fr.iglee42.createqualityoflife.utils.NetworkDestructionLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class StockManagerDestructionLevelPacket extends BlockEntityConfigurationPacket<StockManagerBlockEntity> {

	private NetworkDestructionLevel level;

	public StockManagerDestructionLevelPacket(BlockPos pos, NetworkDestructionLevel level) {
		super(pos);
		this.level = level;
	}

	public StockManagerDestructionLevelPacket(FriendlyByteBuf buffer) {
		super(buffer);
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

	@Override
	protected void applySettings(StockManagerBlockEntity stockManagerBlockEntity) {}

	@Override
	protected void readSettings(FriendlyByteBuf buffer) {
		level = buffer.readEnum(NetworkDestructionLevel.class);
	}

	@Override
	protected void writeSettings(FriendlyByteBuf buffer) {
		buffer.writeEnum(level);
	}
}
