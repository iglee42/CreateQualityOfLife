package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.equipment.armor.DivingBootsItem;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import fr.iglee42.createqualityoflife.items.armors.ShadowRadianceArmorItem;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BeltBlock.class,remap = true)
public class BeltBlockMixin {

    @Redirect(method = "entityInside",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/equipment/armor/DivingBootsItem;isWornBy(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean qol$disableBoots(Entity entity){
        ItemStack wornItem = DivingBootsItem.getWornItem(entity);
        if (wornItem.getItem() instanceof ShadowRadianceArmorItem )return NBTConstants.getOrDefault(wornItem,NBTConstants.NBT_BELT,true);
        return !wornItem.isEmpty();
    }
}
