package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import fr.iglee42.createqualityoflife.registries.QOLPackets;
import fr.iglee42.createqualityoflife.utils.LogisticsNetworkExtension;
import fr.iglee42.createqualityoflife.utils.NetworkPermission;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class ModifyPlayerNetworkPermissionPacket extends BlockEntityConfigurationPacket<StockManagerBlockEntity> {
	public static final StreamCodec<ByteBuf, ModifyPlayerNetworkPermissionPacket> STREAM_CODEC = StreamCodec.composite(
	    	BlockPos.STREAM_CODEC, p -> p.pos,
			UUIDUtil.STREAM_CODEC, p -> p.modifiedPlayer,
			NetworkPermission.STREAM_CODEC, p -> p.permission,
			ModifyPlayerNetworkPermissionPacket::new
	);

	private final UUID modifiedPlayer;
	private final NetworkPermission permission;

	public ModifyPlayerNetworkPermissionPacket(BlockPos pos, UUID modifiedPlayer, NetworkPermission permission) {
		super(pos);
        this.modifiedPlayer = modifiedPlayer;
        this.permission = permission;
	}

	@Override
	public PacketTypeProvider getTypeProvider() {
		return QOLPackets.MODIFY_PERMISSION;
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

}
