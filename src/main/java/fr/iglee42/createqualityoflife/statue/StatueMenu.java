package fr.iglee42.createqualityoflife.statue;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.foundation.gui.menu.GhostItemMenu;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import fr.iglee42.createqualityoflife.registries.ModMenuTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerCopySlot;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class StatueMenu extends GhostItemMenu<Statue> {

    public static final ResourceLocation EMPTY_ARMOR_SLOT_HELMET = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_helmet");
    public static final ResourceLocation EMPTY_ARMOR_SLOT_CHESTPLATE = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_chestplate");
    public static final ResourceLocation EMPTY_ARMOR_SLOT_LEGGINGS = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_leggings");
    public static final ResourceLocation EMPTY_ARMOR_SLOT_BOOTS = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_boots");
    public static final ResourceLocation EMPTY_ARMOR_SLOT_SHIELD = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_shield");
    public static final ResourceLocation EMPTY_ARMOR_SLOT_SWORD = ResourceLocation.withDefaultNamespace("item/empty_slot_sword");

    boolean showSlots = true;

    public StatueMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public StatueMenu(int id, Inventory inv, Statue contentHolder) {
        super(ModMenuTypes.STATUE.get(), id, inv, contentHolder);
    }

    @Override
    protected Statue createOnClient(RegistryFriendlyByteBuf extraData) {
        if (Minecraft.getInstance().level == null) return null;
        return (Statue) Minecraft.getInstance().level.getEntity(extraData.readInt());
    }


    public boolean isShowSlots() {
        return showSlots;
    }

    @Override
    protected void addSlots() {
        addPlayerSlots(145,25);
        int x = 23;
        int y = 25;
        for (int row = 0; row < 2; ++row)
            for (int col = 0; col < 3; ++col) {
                int index = col + row * 3;
                SlotItemHandler slot = new SlotItemHandler(ghostInventory, index, 89 + (index > 1 ? 20 : 0), y + (index < 2 ? (index + 1) * 20 : ((4 - (index - 2)) -1) * 20)) {
                    @Override
                    public boolean isActive() {
                        return showSlots;
                    }

                };
                ResourceLocation back = switch (index){
                    case 0 -> EMPTY_ARMOR_SLOT_SWORD;
                    case 1-> EMPTY_ARMOR_SLOT_SHIELD;
                    case 5-> EMPTY_ARMOR_SLOT_HELMET;
                    case 4-> EMPTY_ARMOR_SLOT_CHESTPLATE;
                    case 3-> EMPTY_ARMOR_SLOT_LEGGINGS;
                    case 2-> EMPTY_ARMOR_SLOT_BOOTS;
                    default -> null;
                };
                if (back != null)slot.setBackground(InventoryMenu.BLOCK_ATLAS,back);
                this.addSlot(slot);
            }
    }

    @Override
    protected void addPlayerSlots(int x, int y) {
        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot)
            this.addSlot(new Slot(playerInventory, hotbarSlot, x + 58, y + hotbarSlot * 18){
                @Override
                public boolean isActive() {
                    return showSlots;
                }
            });
        for (int row = 0; row < 3; ++row)
            for (int col = 0; col < 9; ++col)
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, x + row * 18, y + col * 18){
                    @Override
                    public boolean isActive() {
                        return showSlots;
                    }
                });
    }

    public void setShowSlots(boolean showSlots) {
        this.showSlots = showSlots;
    }

    @Override
    protected void saveData(Statue contentHolder) {
        for (int i = 0; i < ghostInventory.getSlots(); i++) {
            contentHolder.setItemSlot(EquipmentSlot.values()[i],ghostInventory.getStackInSlot(i));
        }
    }

    @Override
    protected ItemStackHandler createGhostInventory() {
        ItemStackHandler ghostItems = new ItemStackHandler(6){
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                EquipmentSlot equipmentSlot = EquipmentSlot.values()[slot];
                if (!equipmentSlot.isArmor()) return super.isItemValid(slot, stack);
                return super.isItemValid(slot, stack) && (stack.canEquip(equipmentSlot,contentHolder) || stack.isEmpty());
            }

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                contentHolder.setItemSlot(EquipmentSlot.values()[slot],getStackInSlot(slot));
            }
        };
        EquipmentSlot[] slots = new EquipmentSlot[]{EquipmentSlot.MAINHAND,EquipmentSlot.OFFHAND,EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET};
        for (EquipmentSlot slot : slots) {
            ghostItems.setStackInSlot(slot.ordinal(),contentHolder.getItemBySlot(slot));
        }
        return ghostItems;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        if (slotId < 36) {
            super.clicked(slotId, dragType, clickTypeIn, player);
            return;
        }
        if (clickTypeIn == ClickType.THROW)
            return;

        ItemStack held = getCarried();
        int slot = slotId - 36;
        if (clickTypeIn == ClickType.CLONE) {
            if (player.isCreative() && held.isEmpty()) {
                ItemStack stackInSlot = ghostInventory.getStackInSlot(slot)
                        .copy();
                stackInSlot.setCount(stackInSlot.getOrDefault(DataComponents.MAX_STACK_SIZE, 64));
                setCarried(stackInSlot);
                return;
            }
            return;
        }

        ItemStack insert;
        if (held.isEmpty()) {
            insert = ItemStack.EMPTY;
        } else {
            insert = held.copy();
            insert.setCount(1);
        }
        if (ghostInventory.isItemValid(slot,insert)) {
            ghostInventory.setStackInSlot(slot, insert);
            getSlot(slotId).setChanged();
        }
    }


    @Override
    protected boolean allowRepeats() {
        return false;
    }

}
