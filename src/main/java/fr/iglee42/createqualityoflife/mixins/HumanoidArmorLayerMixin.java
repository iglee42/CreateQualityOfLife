package fr.iglee42.createqualityoflife.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> {

    @Inject( method = "renderArmorPiece",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;",shift = At.Shift.BEFORE),cancellable = true,locals = LocalCapture.CAPTURE_FAILSOFT)
    private void createqol$hideArmor(PoseStack p_117119_, MultiBufferSource p_117120_, T p_117121_, EquipmentSlot p_117122_, int p_117123_, A p_117124_, CallbackInfo ci, ItemStack itemstack){
        if (!NBTConstants.getOrDefault(itemstack,NBTConstants.NBT_RENDER_TYPE).shouldRenderArmor()) ci.cancel();
        if (p_117121_.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof QOLConfigurableItem it && it.providedEffect(p_117121_.getItemBySlot(EquipmentSlot.HEAD)).equals(MobEffects.INVISIBILITY) && NBTConstants.getOrDefault(p_117121_.getItemBySlot(EquipmentSlot.HEAD),NBTConstants.NBT_EFFECTS,true))ci.cancel();

    }

}
