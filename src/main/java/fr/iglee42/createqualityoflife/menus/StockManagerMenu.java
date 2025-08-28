package fr.iglee42.createqualityoflife.menus;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import fr.iglee42.createqualityoflife.registries.QOLMenuTypes;
import fr.iglee42.createqualityoflife.utils.NetworkDestructionLevel;
import fr.iglee42.createqualityoflife.utils.NetworkPermission;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StockManagerMenu extends MenuBase<StockManagerBlockEntity> {

	public boolean isAdmin;
	public boolean isOwner;
	public boolean isLocked;
	public boolean mayDestroy;
	public String name;
	public NetworkDestructionLevel destructionLevel;
	public int links;
	public Map<UUID, NetworkPermission> permissions;

	public Object screenReference;

	public StockManagerMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
		super(type, id, inv, extraData);
	}

	public StockManagerMenu(MenuType<?> type, int id, Inventory inv, StockManagerBlockEntity contentHolder) {
		super(type, id, inv, contentHolder);
	}

	public static AbstractContainerMenu create(int pContainerId, Inventory pPlayerInventory,
											   StockManagerBlockEntity stockTickerBlockEntity) {
		return new StockManagerMenu(QOLMenuTypes.STOCK_MANAGER.get(), pContainerId, pPlayerInventory,
			stockTickerBlockEntity);
	}

	@Override
	protected StockManagerBlockEntity createOnClient(FriendlyByteBuf extraData) {
		isAdmin = extraData.readBoolean();
		isOwner = extraData.readBoolean();
		isLocked = extraData.readBoolean();
		name = extraData.readUtf();
		links = extraData.readInt();
		destructionLevel = NetworkDestructionLevel.values()[extraData.readByte()];
		mayDestroy = extraData.readBoolean();
		permissions = extraData.readMap(FriendlyByteBuf::readUUID,buf-> NetworkPermission.values()[buf.readByte()]);
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

}
