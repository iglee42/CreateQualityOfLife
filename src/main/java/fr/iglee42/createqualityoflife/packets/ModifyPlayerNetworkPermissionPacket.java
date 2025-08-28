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

import java.util.UUID;

public class ModifyPlayerNetworkPermissionPacket extends BlockEntityConfigurationPacket<StockManagerBlockEntity> {

	private UUID modifiedPlayer;
	private NetworkPermission permission;

	public ModifyPlayerNetworkPermissionPacket(BlockPos pos, UUID modifiedPlayer, NetworkPermission permission) {
		super(pos);
        this.modifiedPlayer = modifiedPlayer;
        this.permission = permission;
	}

	public ModifyPlayerNetworkPermissionPacket(FriendlyByteBuf buffer) {
		super(buffer);
	}

	@Override
	protected void writeSettings(FriendlyByteBuf buffer) {
		buffer.writeUUID(modifiedPlayer);
		buffer.writeEnum(permission);
	}

	@Override
	protected void readSettings(FriendlyByteBuf buffer) {
		modifiedPlayer = buffer.readUUID();
		permission = buffer.readEnum(NetworkPermission.class);
	}

	@Override
	protected void applySettings(ServerPlayer player, StockManagerBlockEntity be) {
		LogisticsNetwork network = Create.LOGISTICS.logisticsNetworks.get(be.behaviour.freqId);
		if (network != null) {
			LogisticsNetworkExtension extendedNetwork = (LogisticsNetworkExtension) network;
			NetworkPermission currentPerm = extendedNetwork.createQOL$getPlayerPermission(modifiedPlayer);
			if (currentPerm.equals(NetworkPermission.NONE) && permission == NetworkPermission.MEMBER && extendedNetwork.createQOL$hasPlayerPermission(player,NetworkPermission.ADMIN)){
				((LogisticsNetworkExtension)network).createQOL$modifyPlayerPermission(modifiedPlayer,permission);
				Create.LOGISTICS.markDirty();
			}
			if (currentPerm.equals(NetworkPermission.ADMIN) && permission == NetworkPermission.MEMBER && player.getUUID().equals(network.owner)){
				((LogisticsNetworkExtension)network).createQOL$modifyPlayerPermission(modifiedPlayer,permission);
				Create.LOGISTICS.markDirty();
			}
			if (currentPerm.equals(NetworkPermission.MEMBER) && permission == NetworkPermission.NONE && extendedNetwork.createQOL$hasPlayerPermission(player,NetworkPermission.ADMIN)){
				((LogisticsNetworkExtension)network).createQOL$modifyPlayerPermission(modifiedPlayer,permission);
				Create.LOGISTICS.markDirty();
			}
			if (currentPerm.equals(NetworkPermission.MEMBER) && permission == NetworkPermission.ADMIN && player.getUUID().equals(network.owner)){
				((LogisticsNetworkExtension)network).createQOL$modifyPlayerPermission(modifiedPlayer,permission);
				Create.LOGISTICS.markDirty();
			}
		}
	}

	@Override
	protected void applySettings(StockManagerBlockEntity stockManagerBlockEntity) {}
}
