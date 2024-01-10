package fr.iglee42.createqualityoflife.blockentitites;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelModeSlot;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.Lang;
import fr.iglee42.createqualityoflife.utils.ArmorItemStackHandler;
import fr.iglee42.createqualityoflife.utils.InventoryLinkerStacksHandler;
import fr.iglee42.createqualityoflife.utils.ModIcons;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class InventoryLinkerBlockEntity extends KineticBlockEntity {

    private ItemStack playerPaperItemStack = ItemStack.EMPTY;
    private String linkedPlayerName = "";
    private InventoryLinkerStacksHandler linkedInventoryContent = new InventoryLinkerStacksHandler(0,this);
    private LazyOptional<?> inventoryOptional = LazyOptional.empty();


    protected ScrollOptionBehaviour<Mode> selectionMode;

    public InventoryLinkerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.add(selectionMode = new ScrollOptionBehaviour<>(Mode.class,
                Lang.translateDirect("options.createqol.inventory_linker.label"), this, new BrassTunnelModeSlot()));
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

        if (!playerPaperItemStack.isEmpty()){
            linkedPlayerName = playerPaperItemStack.getOrCreateTag().getString("linkedPlayer");
        } else {
            linkedPlayerName = "";
        }

        if (!linkedPlayerName.isEmpty() && level.getServer().getPlayerList().getPlayerByName(linkedPlayerName) != null){
            linkedInventoryContent = switch (selectionMode.get()) {
                case INVENTORY -> new InventoryLinkerStacksHandler(level.getServer().getPlayerList().getPlayerByName(linkedPlayerName).getInventory().items,this);
                case ARMOR -> new ArmorItemStackHandler(level.getServer().getPlayerList().getPlayerByName(linkedPlayerName).getInventory().armor,this);
                case OFF_HAND -> new InventoryLinkerStacksHandler(level.getServer().getPlayerList().getPlayerByName(linkedPlayerName).getInventory().offhand,this);
            };
            inventoryOptional = LazyOptional.of(()->linkedInventoryContent);
        } else {
            linkedInventoryContent = new InventoryLinkerStacksHandler(0,this);
            inventoryOptional = LazyOptional.of(()->linkedInventoryContent);
        }
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
        linkedInventoryContent = new InventoryLinkerStacksHandler(0,this);
        Block.popResource(level,worldPosition,playerPaperItemStack);
    }
}
