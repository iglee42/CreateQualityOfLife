package fr.iglee42.createqualityoflife.blockentitites;

import com.simibubi.create.content.logistics.chute.SmartChuteFilterSlotPositioning;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelModeSlot;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.blocks.BrassTrashCanBlock;
import fr.iglee42.createqualityoflife.registries.ModBlockEntities;
import fr.iglee42.createqualityoflife.registries.ModIcons;
import fr.iglee42.createqualityoflife.utils.BrassTrashCanFilterSlotPositioning;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class BrassTrashCanBlockEntity extends TrashCanBlockEntity{
    FilteringBehaviour filtering;
    protected ScrollOptionBehaviour<Mode> selectionMode;


    public BrassTrashCanBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.BRASS_TRASH_CAN.get(),
                (be, context) -> be.itemHandler
        );
    }

    @Override
    public boolean canAcceptItem(ItemStack stack) {
        return super.canAcceptItem(stack) && canActivate() && filtering.test(stack);
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
        if (!clientSide) handleInputFromAbove();
    }

    public enum Mode implements INamedIconOptions {
        VOID(0, AllIcons.I_TRASH),
        KEEP_64(1, AllIcons.I_3x3)
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
