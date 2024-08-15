package fr.iglee42.createqualityoflife.blockentitites;

import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import fr.iglee42.createqualityoflife.blocks.ShadowRadianceBacktankBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ShadowRadianceBacktankBE extends BacktankBlockEntity {

    private boolean propeller;
    private boolean fans;
    private boolean hover;

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

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putBoolean("propeller",propeller);
        compound.putBoolean("fans",fans);
        compound.putBoolean("hover",hover);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        propeller = compound.getBoolean("propeller");
        fans = compound.getBoolean("fans");
        hover = compound.getBoolean("hover");
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

    @Override
    public void tick() {
        super.tick();
        if (getBlockState().getValue(ShadowRadianceBacktankBlock.PROPELLER) != propeller){
            level.setBlockAndUpdate(getBlockPos(),getBlockState().setValue(ShadowRadianceBacktankBlock.PROPELLER,propeller));
        }
    }
}
