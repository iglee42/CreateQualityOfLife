package fr.iglee42.createqualityoflife.blockentitites;

import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import fr.iglee42.createqualityoflife.blocks.ShadowRadianceBacktankBlock;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ShadowRadianceBacktankBE extends BacktankBlockEntity {

    private boolean propeller;

    public ShadowRadianceBacktankBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void setPropeller(boolean propeller) {
        this.propeller = propeller;
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putBoolean(NBTConstants.NBT_PROPELLERS,propeller);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        propeller = compound.getBoolean(NBTConstants.NBT_PROPELLERS);
    }

    public boolean hasPropeller() {
        return propeller;
    }


    @Override
    public void tick() {
        super.tick();
        if (getBlockState().getValue(ShadowRadianceBacktankBlock.PROPELLER) != propeller){
            level.setBlockAndUpdate(getBlockPos(),getBlockState().setValue(ShadowRadianceBacktankBlock.PROPELLER,propeller));
        }
    }
}
