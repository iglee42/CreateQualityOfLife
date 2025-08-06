package fr.iglee42.createqualityoflife.mixins;

import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import net.neoforged.neoforge.common.util.AttributeUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(value = AttributeUtil.class,remap = false)
public class AttributeUtilMixin {

    @Inject(method = "addAttributeTooltips", at =@At("HEAD"),cancellable = true)
    private static void disableAttributesWithQOL(ItemStack stack, Consumer<Component> tooltip, AttributeTooltipContext ctx, CallbackInfo ci){
        if (!stack.getOrDefault(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT).isEnable(ItemTooltips.Tooltip.ATTRIBUTE_MODIFIERS)) ci.cancel();
    }

}
