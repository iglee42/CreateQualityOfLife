package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.kinetics.deployer.BeltDeployerCallbacks;
import fr.iglee42.createqualityoflife.CreateQOL;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BeltDeployerCallbacks.class)
public class BeltDeployerCallbacksMixin {

    @Redirect(method = "activate",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isDamageableItem()Z"))
    private static boolean destroyIfFromQOL(ItemStack instance){
        if (!BuiltInRegistries.ITEM.getKey(instance.getItem()).getNamespace().equals(CreateQOL.MODID)) return instance.isDamageableItem();
        return false;
    }
}
