package fr.iglee42.createqualityoflife.blockentitites;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelModeSlot;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.iglee42.createqualityoflife.registries.ModBlockEntities;
import fr.iglee42.createqualityoflife.registries.ModDataComponents;
import fr.iglee42.createqualityoflife.registries.ModIcons;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.PlayerArmorInvWrapper;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;
import net.neoforged.neoforge.items.wrapper.PlayerOffhandInvWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InventoryLinkerBlockEntity extends KineticBlockEntity {

    private ItemStack playerPaperItemStack = ItemStack.EMPTY;
    private UUID linkedPlayer;


    protected ScrollOptionBehaviour<Mode> selectionMode;

    public InventoryLinkerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.INVENTORY_LINKER.get(),
                (be, context) -> {
                    if (context != Direction.DOWN)
                        return be.getPlayerInventory(be.getLevel());
                    return null;
                }
        );
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.add(selectionMode = new ScrollOptionBehaviour<>(Mode.class,
                CreateLang.translateDirect("options.inventory_linker.label"), this, new BrassTunnelModeSlot()));
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
            return "options.inventory_linker."+name().toLowerCase();
        }
    }



    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide()) return;
        if (!isSpeedRequirementFulfilled()) return;

        if (!playerPaperItemStack.isEmpty()){
            linkedPlayer = playerPaperItemStack.get(ModDataComponents.LINKED_PLAYER);
        } else {
            linkedPlayer = null;
        }
    }


    @Override
    public void write(CompoundTag tag, HolderLookup.Provider provider, boolean clientPacket) {
        super.write(tag,provider,clientPacket);
        tag.putInt("mode",selectionMode.getValue());
        tag.put("paper",playerPaperItemStack.saveOptional(provider));
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider provider, boolean clientPacket) {
        super.read(compound,provider, clientPacket);
        selectionMode.setValue(compound.getInt("mode"));
        playerPaperItemStack = ItemStack.parseOptional(provider,compound.getCompound("paper"));
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
        Block.popResource(level,worldPosition,playerPaperItemStack);
    }

    public IItemHandler getPlayerInventory(Level level){
        if (linkedPlayer != null && level.getPlayerByUUID(linkedPlayer) != null){
             return switch (selectionMode.get()) {
                case INVENTORY -> new PlayerMainInvWrapper(level.getPlayerByUUID(linkedPlayer).getInventory());
                case ARMOR ->  new PlayerArmorInvWrapper(level.getPlayerByUUID(linkedPlayer).getInventory());
                case OFF_HAND ->  new PlayerOffhandInvWrapper(level.getPlayerByUUID(linkedPlayer).getInventory());
            };
        }
        return new ItemStackHandler(0);
    }
}
