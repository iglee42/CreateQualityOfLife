package fr.iglee42.createqualityoflife.mixins.liquidblazeburners;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import fr.iglee42.createqualityoflife.utils.IHaveTankMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SmartBlockEntity.class,remap = false)
public class SmartBlockEntityMixin {

    @Inject(method = "invalidate",at = @At("TAIL"))
    private void createQOL$invalidateBlazeBurner(CallbackInfo ci){
        SmartBlockEntity be = (SmartBlockEntity) (Object)this;
        if (be instanceof BlazeBurnerBlockEntity && be.getLevel() != null) be.getLevel().invalidateCapabilities(be.getBlockPos());
    }
}
