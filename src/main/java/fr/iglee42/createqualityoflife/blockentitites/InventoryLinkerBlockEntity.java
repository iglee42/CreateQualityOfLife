package fr.iglee42.createqualityoflife.blockentitites;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelModeSlot;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.iglee42.createqualityoflife.registries.ModIcons;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraftforge.items.wrapper.PlayerArmorInvWrapper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import net.minecraftforge.items.wrapper.PlayerOffhandInvWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InventoryLinkerBlockEntity extends KineticBlockEntity {

    private ItemStack playerPaperItemStack = ItemStack.EMPTY;
    private IItemHandler linkedInventoryContent = new ItemStackHandler(0);
    private LazyOptional<?> inventoryOptional = LazyOptional.empty();
    private UUID linkedPlayer;


    protected ScrollOptionBehaviour<Mode> selectionMode;

    public InventoryLinkerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.add(selectionMode = new ScrollOptionBehaviour<>(Mode.class,
                CreateLang.translateDirect("options.createqol.inventory_linker.label"), this, new BrassTunnelModeSlot()));
    }

    public UUID getLinkedPlayer() {
        return linkedPlayer;
    }

    public enum Mode implements INamedIconOptions {
        INVENTORY(0,36, ModIcons.I_INVENTORY),
        ARMOR(1,4,ModIcons.I_ARMOR),
        OFF_HAND(2,1,ModIcons.I_OFF_HAND);

        private int id;
        private int slotCount;
        private AllIcons icon;

        Mode(int id, int slotCount, AllIcons icon) {
            this.id = id;
            this.slotCount = slotCount;
            this.icon = icon;
        }

        public int getSlotCount() {
            return slotCount;
        }

        public int getId() {
            return id;
        }

        public static Mode getById(int id){
            return Arrays.stream(values()).filter(m->m.getId() == id).findFirst().orElse(Mode.INVENTORY);
        }

        @Override
        public AllIcons getIcon() {
            return icon;
        }

        @Override
        public String getTranslationKey() {
            return "options.createqol.inventory_linker."+name().toLowerCase();
        }
    }



    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide()) return;
        if (!isSpeedRequirementFulfilled()) return;

        if (!playerPaperItemStack.isEmpty() && playerPaperItemStack.getOrCreateTag().contains(NBTConstants.NBT_LINKED_PLAYER)){
            linkedPlayer = playerPaperItemStack.getOrCreateTag().getUUID(NBTConstants.NBT_LINKED_PLAYER);
        } else {
            linkedPlayer = null;
        }
        if (linkedPlayer != null && level.getServer().getPlayerList().getPlayer(linkedPlayer) != null){
            linkedInventoryContent = switch (selectionMode.get()) {
                case INVENTORY -> new PlayerMainInvWrapper(level.getPlayerByUUID(linkedPlayer).getInventory());
                case ARMOR ->  new PlayerArmorInvWrapper(level.getPlayerByUUID(linkedPlayer).getInventory());
                case OFF_HAND ->  new PlayerOffhandInvWrapper(level.getPlayerByUUID(linkedPlayer).getInventory());
            };
        }
        inventoryOptional = LazyOptional.of(()->linkedInventoryContent);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ForgeCapabilities.ITEM_HANDLER ? (side != Direction.DOWN ? inventoryOptional.cast() : super.getCapability(cap,side)) : super.getCapability(cap,side);
    }

    @Override
    public void write(CompoundTag tag,boolean clientPacket) {
        super.write(tag,clientPacket);
        tag.putInt("mode",selectionMode.getValue());
        tag.put("paper",playerPaperItemStack.serializeNBT());
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        selectionMode.setValue(compound.getInt("mode"));
        playerPaperItemStack = ItemStack.of(compound.getCompound("paper"));
    }

    public void setPlayerPaperItemStack(ItemStack playerPaperItemStack) {
        this.playerPaperItemStack = playerPaperItemStack;
    }

    public ItemStack getPlayerPaperItemStack() {
        return playerPaperItemStack;
    }

    @Override
    public void remove() {
        super.remove();
        linkedInventoryContent = new ItemStackHandler(0);
        Block.popResource(level,worldPosition,playerPaperItemStack);
    }
}
