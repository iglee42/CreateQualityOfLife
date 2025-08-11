package fr.iglee42.createqualityoflife.menus;

import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import fr.iglee42.createqualityoflife.registries.QOLMenuTypes;
import fr.iglee42.createqualityoflife.utils.LogisticsNetworkExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ChooseLogisticNetworkMenu extends MenuBase<StockManagerBlockEntity> {

	public List<LogisticNetworksInfos> networksInfos;

	public Object screenReference;

	public ChooseLogisticNetworkMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
		super(type, id, inv, extraData);
	}

	public ChooseLogisticNetworkMenu(MenuType<?> type, int id, Inventory inv, StockManagerBlockEntity contentHolder) {
		super(type, id, inv, contentHolder);
	}

	public static AbstractContainerMenu create(int pContainerId, Inventory pPlayerInventory,
											   StockManagerBlockEntity stockTickerBlockEntity) {
		return new ChooseLogisticNetworkMenu(QOLMenuTypes.CHOOSE_NETWORK.get(), pContainerId, pPlayerInventory,
			stockTickerBlockEntity);
	}

	@Override
	protected StockManagerBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
		networksInfos = LogisticNetworksInfos.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(extraData);
		if (Minecraft.getInstance().level
			.getBlockEntity(extraData.readBlockPos()) instanceof StockManagerBlockEntity stbe)
			return stbe;
		return null;
	}

	@Override
	protected void initAndReadInventory(StockManagerBlockEntity contentHolder) {}

	@Override
	public void initializeContents(int pStateId, List<ItemStack> pItems, ItemStack pCarried) {}

	@Override
	protected void addSlots() {
		addPlayerSlots(-1000, 0);
	}

	@Override
	protected void saveData(StockManagerBlockEntity contentHolder) {}

	@Override
	public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
		return ItemStack.EMPTY;
	}

	public record LogisticNetworksInfos(UUID id, String name, @Nullable  UUID owner, boolean locked){

		public static final StreamCodec<RegistryFriendlyByteBuf,LogisticNetworksInfos> STREAM_CODEC = StreamCodec.composite(
				UUIDUtil.STREAM_CODEC, LogisticNetworksInfos::id,
				ByteBufCodecs.STRING_UTF8, LogisticNetworksInfos::name,
				UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs::optional), n->Optional.ofNullable(n.owner),
				ByteBufCodecs.BOOL, LogisticNetworksInfos::locked,
				LogisticNetworksInfos::new
		);

		public LogisticNetworksInfos(UUID id, String name, Optional<UUID> owner, boolean locked) {
			this(id,name,owner.orElse(null),locked);
		}

		public static LogisticNetworksInfos fromLogisticNetwork(LogisticsNetwork network){
			return new LogisticNetworksInfos(network.id,((LogisticsNetworkExtension)network).createQOL$getName(),network.owner,network.locked);
		}

	}

}
