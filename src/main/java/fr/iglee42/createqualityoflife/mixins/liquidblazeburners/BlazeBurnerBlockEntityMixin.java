package fr.iglee42.createqualityoflife.mixins.liquidblazeburners;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.utils.Features;
import fr.iglee42.createqualityoflife.utils.IHaveTankMixin;
import fr.iglee42.createqualityoflife.utils.liquidblazeburners.LiquidBlazeBurnerManager;
import fr.iglee42.createqualityoflife.utils.liquidblazeburners.LiquidBlazeBurnerManager.LiquidEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = BlazeBurnerBlockEntity.class,remap = false)
public abstract class BlazeBurnerBlockEntityMixin implements IHaveTankMixin {

    @Shadow protected abstract void setBlockHeat(BlazeBurnerBlock.HeatLevel heat);

    @Shadow public abstract int getRemainingBurnTime();

    @Shadow protected int remainingBurnTime;

    @Shadow protected abstract void playSound();

    @Shadow public abstract void updateBlockState();

    @Shadow public abstract BlazeBurnerBlock.HeatLevel getHeatLevelFromBlock();

    @Shadow public abstract void spawnParticleBurst(boolean soulFlame);

    @Shadow protected BlazeBurnerBlockEntity.FuelType activeFuel;
    @Unique
    protected SmartFluidTank createQOL$tank;

    @Inject(method = "addBehaviours",at = @At("TAIL"))
    private void createQOL$addTank(List<BlockEntityBehaviour> behaviours, CallbackInfo ci){
        createQOL$tank = new SmartFluidTank(1000,(stack)->{}) {
            @Override
            public boolean isFluidValid(FluidStack stack) {
                return LiquidBlazeBurnerManager.BLAZE_BURNER_LIQUIDS.containsKey(stack.getFluid());
            }
        };
    }

    @Override
    public SmartFluidTank createQOL$tank() {
        return createQOL$tank;
    }



    @Inject(method = "read", at = @At("TAIL"))
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (createQOL$tank != null && nbt.contains("tank")) {
            createQOL$tank.readFromNBT(registries,nbt.getCompound("tank"));
        }
    }

    @Inject(method = "write", at = @At("TAIL"))
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (createQOL$tank != null) {
            nbt.put("tank", createQOL$tank.writeToNBT(registries,new CompoundTag()));
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo info) {

        if (!CreateQOL.isActivate(Features.LIQUID_BLAZE_BURNER)) return;

        if (createQOL$tank == null)
            return;

        if (createQOL$tank.getFluid().getAmount() <= 0) return;

        LiquidEntry fluidProperties = LiquidBlazeBurnerManager.BLAZE_BURNER_LIQUIDS.getOrDefault(createQOL$tank.getFluid().getFluid(),null);

        if (fluidProperties == null)
            return;

        boolean superHeated = fluidProperties.superHeated();

        int consumption = fluidProperties.consumption();

        if (createQOL$tank.getFluid().getAmount() < consumption)
            return;

        if (superHeated)
            setBlockHeat(BlazeBurnerBlock.HeatLevel.SEETHING);
        else
            setBlockHeat(BlazeBurnerBlock.HeatLevel.FADING);

        int newBurnTime = getRemainingBurnTime() + fluidProperties.burnTime();

        if (newBurnTime > BlazeBurnerBlockEntity.MAX_HEAT_CAPACITY)
            return;

        remainingBurnTime = newBurnTime;

        createQOL$tank.drain(consumption, IFluidHandler.FluidAction.EXECUTE);
    }

    @Inject(method = "tryUpdateFuel", at = @At("HEAD"), cancellable = true)
    public void tryUpdateFuel(ItemStack itemStack, boolean forceOverflow, boolean simulate, CallbackInfoReturnable<Boolean> cir) {

        if (!CreateQOL.isActivate(Features.LIQUID_BLAZE_BURNER)) return;

        if (createQOL$tank == null)
            return;

        if (itemStack.getCapability(Capabilities.FluidHandler.ITEM) == null) return;

        IFluidHandler handler = itemStack.getCapability(Capabilities.FluidHandler.ITEM);

        if (handler == null) return;

        if (!createQOL$tank.getFluid().isEmpty() && handler.getFluidInTank(0).getFluid() != createQOL$tank.getFluid().getFluid()) return;

        if (handler.getTanks() != 1) return;
        FluidStack fluidStack = handler.getFluidInTank(0);
        if (fluidStack.isEmpty()) return;
        if (!LiquidBlazeBurnerManager.BLAZE_BURNER_LIQUIDS.containsKey(fluidStack.getFluid()))
            return;

        if (createQOL$tank.getFluid().getAmount() + fluidStack.getAmount() > createQOL$tank.getCapacity() && !forceOverflow) return;

        if (!simulate) {
            if (createQOL$tank.getFluid().isEmpty())
                createQOL$tank.setFluid(fluidStack.copy());
            else
                createQOL$tank.getFluid().grow(fluidStack.getAmount());
        }

        BlazeBurnerBlockEntity be = (BlazeBurnerBlockEntity) (Object)this;
        Level level = be.getLevel();
        if (level==null){
            cir.setReturnValue(true);
            return;
        }
        BlockPos worldPosition = be.getBlockPos();


        if (level.isClientSide) {
            spawnParticleBurst(activeFuel == BlazeBurnerBlockEntity.FuelType.SPECIAL);
            cir.setReturnValue(true);
        }

        BlazeBurnerBlock.HeatLevel prev = getHeatLevelFromBlock();
        playSound();
        updateBlockState();

        if (prev != getHeatLevelFromBlock())
            level.playSound(null, worldPosition, SoundEvents.BLAZE_AMBIENT, SoundSource.BLOCKS,
                    .125f + level.random.nextFloat() * .125f, 1.15f - level.random.nextFloat() * .25f);
        cir.setReturnValue(true);
    }
}
