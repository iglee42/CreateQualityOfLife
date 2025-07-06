package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.equipment.armor.NetheriteDivingHandler;
import fr.iglee42.createqualityoflife.registries.QOLArmorMaterials;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NetheriteDivingHandler.class,remap = false)
public class NetheriteDivingHandlerMixin {

    @Inject(method = "isNetheriteArmor", at = @At("HEAD"),cancellable = true)
    private static void inject(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof ArmorItem armorItem && (armorItem.getMaterial() == QOLArmorMaterials.SHADOW_RADIANCE || armorItem.getMaterial() == QOLArmorMaterials.SHADOW_STEEL || armorItem.getMaterial() == QOLArmorMaterials.REFINED_RADIANCE))
            cir.setReturnValue(true);
    }
}
