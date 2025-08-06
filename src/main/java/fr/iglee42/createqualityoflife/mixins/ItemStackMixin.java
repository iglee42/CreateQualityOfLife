package fr.iglee42.createqualityoflife.mixins;

import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import net.neoforged.neoforge.common.util.AttributeUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "addToTooltip", at =@At("HEAD"),cancellable = true,remap = false)
    private <T extends TooltipProvider> void disableEnchantmentsWithQOL(DataComponentType<T> p_331344_, Item.TooltipContext p_341231_, Consumer<Component> p_331885_, TooltipFlag p_331177_, CallbackInfo ci){
        ItemStack it = (ItemStack) (Object) this;
        if (p_331344_.equals(DataComponents.ENCHANTMENTS) && !it.getOrDefault(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT).isEnable(ItemTooltips.Tooltip.ENCHANTMENT)) ci.cancel();
    }

}
