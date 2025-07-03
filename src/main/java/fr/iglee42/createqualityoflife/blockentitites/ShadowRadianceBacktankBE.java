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

public class ShadowRadianceBacktankBE extends BacktankBlockEntity {

    private boolean propeller;
    private boolean fans;
    private boolean hover;
    private boolean elytra;

    public ShadowRadianceBacktankBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void setPropeller(boolean propeller) {
        this.propeller = propeller;
    }

    public void setFans(boolean fans) {
        this.fans = fans;
    }

    public void setHover(boolean hover) {
        this.hover = hover;
    }

    public void setElytra(boolean elytra) {
        this.elytra = elytra;
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider provider, boolean clientPacket) {
        super.write(compound,provider, clientPacket);
        compound.putBoolean("propeller",propeller);
        compound.putBoolean("fans",fans);
        compound.putBoolean("hover",hover);
        compound.putBoolean("elytra",elytra);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider provider, boolean clientPacket) {
        super.read(compound,provider, clientPacket);
        propeller = compound.getBoolean("propeller");
        fans = compound.getBoolean("fans");
        hover = compound.getBoolean("hover");
        elytra = compound.getBoolean("elytra");
    }

    public boolean hasPropeller() {
        return propeller;
    }

    public boolean isFans() {
        return fans;
    }

    public boolean isHover() {
        return hover;
    }

    public boolean hasElytra(){
        return elytra;
    }

    @Override
    public void tick() {
        super.tick();
        if (getBlockState().getValue(ShadowRadianceBacktankBlock.PROPELLER) != propeller){
            level.setBlockAndUpdate(getBlockPos(),getBlockState().setValue(ShadowRadianceBacktankBlock.PROPELLER,propeller));
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        propeller = componentInput.getOrDefault(QOLDataComponents.BACKTANK_PROPELLERS,false);
        fans = componentInput.getOrDefault(QOLDataComponents.BACKTANK_FANS,true);
        hover = componentInput.getOrDefault(QOLDataComponents.BACKTANK_HOVER,false);
        elytra = componentInput.getOrDefault(QOLDataComponents.BACKTANK_ELYTRA,false);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(QOLDataComponents.BACKTANK_PROPELLERS,propeller);
        components.set(QOLDataComponents.BACKTANK_FANS,fans);
        components.set(QOLDataComponents.BACKTANK_HOVER,hover);
        components.set(QOLDataComponents.BACKTANK_ELYTRA,elytra);
    }
}
