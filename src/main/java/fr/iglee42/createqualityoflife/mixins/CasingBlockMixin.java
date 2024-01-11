package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.decoration.encasing.CasingBlock;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.utils.Features;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CasingBlock.class)
public class CasingBlockMixin {

    @Inject(method = "fillItemCategory(Lnet/minecraft/world/item/CreativeModeTab;Lnet/minecraft/core/NonNullList;)V",at = @At("HEAD"),cancellable = true)
    private void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems, CallbackInfo ci){
        pItems.add(new ItemStack((Block)(Object)this));
        ci.cancel();
    }
}
