package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.equipment.armor.DivingBootsItem;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLItems;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = DivingBootsItem.class,remap = false)
public class DivingBootsItemMixin {


    @Inject(remap = true, method = "getWornItem",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;",shift = At.Shift.BEFORE),locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private static void createqol$getWornItem(Entity entity, CallbackInfoReturnable<ItemStack> cir, LivingEntity livingEntity, ItemStack stack){
        if (QOLItems.SHADOW_RADIANCE_BOOTS.is(stack.getItem())) cir.setReturnValue(stack);
    }

    @Inject(method = "affects",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/equipment/armor/DivingBootsItem;isWornBy(Lnet/minecraft/world/entity/Entity;)Z",shift = At.Shift.BEFORE),locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private static void createqol$affects(LivingEntity entity, CallbackInfoReturnable<Boolean> cir){
        if (DivingBootsItem.getWornItem(entity).is(QOLItems.SHADOW_RADIANCE_BOOTS.asItem())) {
            if (!NBTConstants.getOrDefault(DivingBootsItem.getWornItem(entity),NBTConstants.NBT_DIVING,false) || !CreateQOLConfigs.server().bootsDiving.get()) {
                entity.getPersistentData()
                        .remove("HeavyBoots");
                cir.setReturnValue(false);
            }
        }
    }
}
