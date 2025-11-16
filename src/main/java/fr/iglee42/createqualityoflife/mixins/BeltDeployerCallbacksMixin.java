package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.kinetics.deployer.BeltDeployerCallbacks;
import fr.iglee42.createqualityoflife.CreateQOL;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BeltDeployerCallbacks.class,remap = false)
public class BeltDeployerCallbacksMixin {

    @Redirect(method = "activate",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getMaxDamage()I"))
    private static int destroyIfFromQOL(ItemStack instance){
        if (!BuiltInRegistries.ITEM.getKey(instance.getItem()).getNamespace().equals(CreateQOL.MODID)) return instance.getMaxDamage();
        return 0;
    }
}
