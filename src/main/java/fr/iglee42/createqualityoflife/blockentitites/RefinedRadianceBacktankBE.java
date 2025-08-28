package fr.iglee42.createqualityoflife.blockentitites;

import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import net.minecraft.core.BlockPos;
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
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putBoolean(NBTConstants.NBT_ELYTRA,elytra);
    }

    @Override
    protected void read(CompoundTag compound,  boolean clientPacket) {
        super.read(compound, clientPacket);
        elytra = compound.getBoolean(NBTConstants.NBT_ELYTRA);
    }
    public boolean hasElytra(){
        return elytra;
    }
}
