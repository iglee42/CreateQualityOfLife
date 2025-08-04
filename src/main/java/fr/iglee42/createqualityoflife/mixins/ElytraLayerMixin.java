package fr.iglee42.createqualityoflife.mixins;

import fr.iglee42.createqualityoflife.items.armors.RefinedRadianceChestplate;
import fr.iglee42.createqualityoflife.items.armors.ShadowRadianceChestplate;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import fr.iglee42.createqualityoflife.utils.PreferredRender;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ElytraLayer.class,remap = false)
public class ElytraLayerMixin {

    @Inject(method = "shouldRender", at = @At("HEAD"),cancellable = true)
    private <T extends LivingEntity> void qol$shadowRadianceRenderElytra(ItemStack stack, T entity, CallbackInfoReturnable<Boolean> cir){
        if (entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof QOLConfigurableItem it && it.providedEffect(entity.getItemBySlot(EquipmentSlot.HEAD)).equals(MobEffects.INVISIBILITY) && NBTConstants.getOrDefault(entity.getItemBySlot(EquipmentSlot.HEAD),NBTConstants.NBT_EFFECTS,true)){
            cir.setReturnValue(false);
            return;
        }
        if ((stack.getItem() instanceof ShadowRadianceChestplate || stack.getItem() instanceof RefinedRadianceChestplate )&& ShadowRadianceChestplate.hasElytra(stack)&& NBTConstants.getOrDefault(stack,NBTConstants.NBT_RENDER_TYPE).shouldRenderAddition() && NBTConstants.getOrDefault(NBTConstants.NBT_PREFERRED_RENDER,stack).shouldRenderElytra()){
            cir.setReturnValue(true);
            return;
        }
    }

}
