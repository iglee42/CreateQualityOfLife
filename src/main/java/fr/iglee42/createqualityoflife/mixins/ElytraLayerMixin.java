package fr.iglee42.createqualityoflife.mixins;

import fr.iglee42.createqualityoflife.items.ShadowRadianceChestplate;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.utils.PreferredRender;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
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
        if (stack.getItem() instanceof ShadowRadianceChestplate && ShadowRadianceChestplate.hasElytra(stack) && stack.getOrDefault(QOLDataComponents.PREFERRED_RENDER, PreferredRender.BOTH).shouldRenderElytra()){
            cir.setReturnValue(true);
            return;
        }
    }

}
