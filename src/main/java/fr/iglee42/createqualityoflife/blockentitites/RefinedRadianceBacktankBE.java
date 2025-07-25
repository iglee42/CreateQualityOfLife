package fr.iglee42.createqualityoflife.blockentitites;

import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import fr.iglee42.createqualityoflife.blocks.ShadowRadianceBacktankBlock;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RefinedRadianceBacktankBE extends BacktankBlockEntity {


    private boolean elytra;

    public RefinedRadianceBacktankBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    public void setElytra(boolean elytra) {
        this.elytra = elytra;
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider provider, boolean clientPacket) {
        super.write(compound,provider, clientPacket);
        compound.putBoolean("elytra",elytra);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider provider, boolean clientPacket) {
        super.read(compound,provider, clientPacket);
        elytra = compound.getBoolean("elytra");
    }
    public boolean hasElytra(){
        return elytra;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        elytra = componentInput.getOrDefault(QOLDataComponents.BACKTANK_ELYTRA,false);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(QOLDataComponents.BACKTANK_ELYTRA,elytra);
    }
}
