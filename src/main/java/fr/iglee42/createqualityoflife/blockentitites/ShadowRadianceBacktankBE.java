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

    public ShadowRadianceBacktankBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (getBlockState().getValue(ShadowRadianceBacktankBlock.PROPELLER) != propeller){
            level.setBlock(getBlockPos(),getBlockState().setValue(ShadowRadianceBacktankBlock.PROPELLER,propeller), ShadowRadianceBacktankBlock.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        propeller = componentInput.getOrDefault(QOLDataComponents.BACKTANK_PROPELLERS,false);
    }

}
