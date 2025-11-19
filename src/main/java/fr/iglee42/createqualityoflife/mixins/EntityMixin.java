package fr.iglee42.createqualityoflife.mixins;

import fr.iglee42.createqualityoflife.registries.QOLFluids;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = Entity.class,remap = false)
public class EntityMixin {

    @Shadow
    protected Object2DoubleMap<FluidType> forgeFluidTypeHeight;

    @Shadow
    protected boolean firstTick;

    @Inject(method = "isInLava", at = @At("RETURN"),cancellable = true)
    private void qol$addSuperLava(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(cir.getReturnValue() || (!this.firstTick && this.forgeFluidTypeHeight.getDouble(QOLFluids.SUPERHEATED_LAVA.getType()) > (double)0.0F));
    }
}
