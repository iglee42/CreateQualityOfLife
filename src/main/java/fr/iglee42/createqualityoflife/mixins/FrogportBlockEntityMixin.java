package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import fr.iglee42.createqualityoflife.utils.EnderPackagerItemHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = FrogportBlockEntity.class,remap = false)
public class FrogportBlockEntityMixin {

    @Inject(method = "lambda$tryPullingFrom$0", at =@At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT,cancellable = true)
    private void qol$alwaysWhenEnderPackager(IItemHandler handler, ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(cir.getReturnValue() || (handler instanceof EnderPackagerItemHandler eh && !eh.getBlockEntity().isTransmitter()));
    }

}
