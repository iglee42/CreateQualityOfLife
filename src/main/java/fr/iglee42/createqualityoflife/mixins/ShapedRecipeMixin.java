package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import fr.iglee42.createqualityoflife.utils.CopyComponentsExtension;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(value = ShapedRecipe.class,remap = false)
public abstract class ShapedRecipeMixin<I extends RecipeInput> {

    @Inject(method = "assemble*", at = @At("RETURN"), cancellable = true)
    private void createQOL$copyComponent(I t, HolderLookup.Provider provider, CallbackInfoReturnable<ItemStack> cir){
        Object obj = this;
        ShapedRecipe recipe = (ShapedRecipe) obj;
        if(!(recipe instanceof MechanicalCraftingRecipe mcr))return;
        if (mcr == null) return;
        int slot = ((CopyComponentsExtension) mcr).createQOL$copiedSlot();
        List<ResourceLocation> copiedComponents = ((CopyComponentsExtension)mcr).createQOL$copiedComponents();
        ItemStack returned = cir.getReturnValue();
        if (!t.getItem(slot).isEmpty()){
            ItemStack from = t.getItem(slot);
           copiedComponents.stream().map(BuiltInRegistries.DATA_COMPONENT_TYPE::get)
                    .filter(Objects::nonNull)
                    .forEach(d->{
                        if (from.has(d)) createQualityOfLife$copyComponent(from,returned,d);
                    });
        }
        cir.setReturnValue(returned);
    }

    @Unique
    private static <T> void createQualityOfLife$copyComponent(ItemStack source, ItemStack target, DataComponentType<T> componentType) {
        T componentValue = source.get(componentType);
        if (componentValue != null) {
            target.set(componentType, componentValue);
        }
    }
}
