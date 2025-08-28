package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import fr.iglee42.createqualityoflife.utils.CopyNBTsExtension;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = ShapedRecipe.class,remap = false)
public abstract class ShapedRecipeMixin<I extends CraftingContainer> {

    @Inject(method = "assemble(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"), cancellable = true)
    private void createQOL$copyComponent(CraftingContainer t, RegistryAccess p_266725_, CallbackInfoReturnable<ItemStack> cir){
        Object obj = this;
        ShapedRecipe recipe = (ShapedRecipe) obj;
        if(!(recipe instanceof MechanicalCraftingRecipe mcr))return;
        if (mcr == null) return;
        int slot = ((CopyNBTsExtension) mcr).createQOL$copiedSlot();
        List<String> copiedComponents = ((CopyNBTsExtension)mcr).createQOL$copiedNBTs();
        ItemStack returned = cir.getReturnValue();
        if (!t.getItem(slot).isEmpty()){
            ItemStack from = t.getItem(slot);
            copiedComponents.stream()
                    .filter(Predicate.not(String::isEmpty))
                    .forEach(d->{
                        if (from.getOrCreateTag().contains(d)) returned.getOrCreateTag().put(d, from.getOrCreateTag().get(d).copy());
                    });
        }
        cir.setReturnValue(returned);
    }

}
