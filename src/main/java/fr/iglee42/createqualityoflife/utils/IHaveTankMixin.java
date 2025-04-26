package fr.iglee42.createqualityoflife.utils;

import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;

public interface IHaveTankMixin {

    default SmartFluidTankBehaviour createQOL$behaviour() {
        return null;
    }
    default SmartFluidTank createQOL$tank() {
        return null;
    }

}
