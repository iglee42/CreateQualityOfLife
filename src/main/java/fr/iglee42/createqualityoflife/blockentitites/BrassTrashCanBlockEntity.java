package fr.iglee42.createqualityoflife.blockentitites;

import com.simibubi.create.content.logistics.tunnel.BrassTunnelModeSlot;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.iglee42.createqualityoflife.blocks.BrassTrashCanBlock;
import fr.iglee42.createqualityoflife.registries.QOLBlockEntities;
import fr.iglee42.createqualityoflife.utils.BrassTrashCanFilterSlotPositioning;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class BrassTrashCanBlockEntity extends TrashCanBlockEntity{
    FilteringBehaviour filtering;
    protected ScrollOptionBehaviour<Mode> selectionMode;


    public BrassTrashCanBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                QOLBlockEntities.BRASS_TRASH_CAN.get(),
                (be, context) -> be.itemHandler
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                QOLBlockEntities.BRASS_TRASH_CAN.get(),
                (be, context) -> be.tankInventory
        );
    }

    @Override
    public boolean canAcceptItem(ItemStack stack) {
        return super.canAcceptItem(stack) && canActivate() && filtering.test(stack) && !getBlockState().getValue(BrassTrashCanBlock.POWERED);
    }

    @Override
    public boolean canAcceptFluid(FluidStack stack) {
        return super.canAcceptFluid(stack) && canActivate() && filtering.test(stack) && !getBlockState().getValue(BrassTrashCanBlock.POWERED);
    }

    @Override
    protected int getExtractionAmount() {
        return filtering.isCountVisible() && !filtering.anyAmount() ? filtering.getAmount() : 64;
    }

    @Override
    protected @NotNull ItemHelper.ExtractionCountMode getExtractionMode() {
        return filtering.isCountVisible() && !filtering.anyAmount() && !filtering.upTo ? ItemHelper.ExtractionCountMode.EXACTLY
                : ItemHelper.ExtractionCountMode.UPTO;
    }


    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(filtering =
                new FilteringBehaviour(this, new BrassTrashCanFilterSlotPositioning()).showCountWhen(()->getBlockState().getValue(BrassTrashCanBlock.OPEN)));
        behaviours.add(selectionMode = (ScrollOptionBehaviour<Mode>) new ScrollOptionBehaviour<>(Mode.class,
                CreateLang.translateDirect("options.brass_trash_can.label"), this, new BrassTunnelModeSlot()).onlyActiveWhen(()->!getBlockState().getValue(BrassTrashCanBlock.OPEN)));
        super.addBehaviours(behaviours);
    }


    @Override
    public void tick() {
        super.tick();
        boolean clientSide = level != null && level.isClientSide && !isVirtual();
        if (!clientSide && getBlockState().getValue(BrassTrashCanBlock.OPEN) && !getBlockState().getValue(BrassTrashCanBlock.POWERED)){
            //HANDLERS
            if (selectionMode.get().equals(Mode.KEEP_64)){
                IItemHandler handler = grabCapability(Direction.UP);
                if (handler != null) {
                    List<Item> savedItems = new ArrayList<>();
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack stack = handler.getStackInSlot(i);
                        if (!canAcceptItem(stack)) continue;
                        if (!savedItems.contains(stack.getItem())) savedItems.add(stack.getItem());
                        else {
                            handler.extractItem(i,filtering.count,false);
                        }
                    }
                }
            } else if (selectionMode.get().equals(Mode.VOID_OVERFLOW)){
                IItemHandler handler = grabCapability(Direction.UP);
                if (handler != null) {
                    boolean hasAnEmptySlot = false;
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack stack = handler.getStackInSlot(i);
                        if (hasAnEmptySlot) break;
                        if (stack.isEmpty()){
                            hasAnEmptySlot = true;
                            continue;
                        }
                        if (!canAcceptItem(stack)) continue;
                        handler.extractItem(i,filtering.count,false);
                        hasAnEmptySlot = true;
                    }
                }
            } else {
                handleInputFromAbove();
            }

            if (selectionMode.get().equals(Mode.VOID_OVERFLOW)) {
                IFluidHandler handler = grabFluidCapability(Direction.UP);
                if (handler != null){
                    for (int t = 0; t < handler.getTanks(); t++){
                        FluidStack stack = handler.getFluidInTank(t);
                        if (stack.getAmount() > handler.getTankCapacity(t) - 1000){
                            int toDrain = stack.getAmount() - ( handler.getTankCapacity(t) - 1000);
                            handler.drain(stack.copyWithAmount(toDrain), IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                }
            } else {
                handleFluidInputFromAbove();
            }



            //SUCK ITEMS
            boolean flag = level.getBlockState(getBlockPos().above()).isCollisionShapeFullBlock(level, getBlockPos().above())
                    && ! level.getBlockState(getBlockPos().above()).is(BlockTags.DOES_NOT_BLOCK_HOPPERS);
            if (!flag) {
                getItemsAtAndAbove(level, getBlockPos()).forEach(ie -> {
                    if (canAcceptItem(ie.getItem())) ie.discard();
                });
            }
        }
    }

    private static List<ItemEntity> getItemsAtAndAbove(Level p_155590_, BlockPos p_155591_) {
        AABB aabb = Block.box(0,14,0,16,18,16).toAabbs().get(0).move(p_155591_.getX(), p_155591_.getY(), p_155591_.getZ() );
        return p_155590_.getEntitiesOfClass(ItemEntity.class, aabb, EntitySelector.ENTITY_STILL_ALIVE);
    }

    public enum Mode implements INamedIconOptions {
        VOID(0, AllIcons.I_TRASH),
        KEEP_64(1, AllIcons.I_3x3),
        VOID_OVERFLOW(2,AllIcons.I_SKIP_BLOCK_ENTITIES)
        ;

        private int id;
        private AllIcons icon;

        Mode(int id, AllIcons icon) {
            this.id = id;
            this.icon = icon;
        }


        public int getId() {
            return id;
        }

        public static Mode getById(int id){
            return Arrays.stream(values()).filter(m->m.getId() == id).findFirst().orElse(VOID);
        }

        @Override
        public AllIcons getIcon() {
            return icon;
        }

        @Override
        public String getTranslationKey() {
            return "options.brass_trash_can."+name().toLowerCase();
        }
    }
}
