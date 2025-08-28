package fr.iglee42.createqualityoflife.mixins;

import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    protected static boolean shouldShowInTooltip(int p_41627_, ItemStack.TooltipPart p_41628_) {
        return false;
    }

    @Shadow public abstract CompoundTag getOrCreateTag();

    @Redirect(method = "getTooltipLines",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z"))
    private boolean qol$disableSomeTooltips(int p_41627_, ItemStack.TooltipPart part){
        if (part.equals(ItemStack.TooltipPart.MODIFIERS) && shouldShowInTooltip(p_41627_,part))
            return  NBTConstants.getTooltipOrDefault((ItemStack) (Object)this).isEnable(ItemTooltips.Tooltip.ATTRIBUTE_MODIFIERS);
        return shouldShowInTooltip(p_41627_,part);
    }

}
