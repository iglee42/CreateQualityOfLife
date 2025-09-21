package fr.iglee42.createqualityoflife.utils;

import fr.iglee42.createqualityoflife.blockentitites.TrashCanBlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class TrashFluidTank extends FluidTank {

    private final TrashCanBlockEntity be;

    public TrashFluidTank(TrashCanBlockEntity be) {
        super(1000);
        this.be = be;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (!be.canAcceptFluid(resource)) return 0;
        return resource.getAmount();

    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack getFluid() {
        return FluidStack.EMPTY;
    }
}
