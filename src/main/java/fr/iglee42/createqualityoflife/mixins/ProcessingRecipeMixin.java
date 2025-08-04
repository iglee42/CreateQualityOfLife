package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import fr.iglee42.createqualityoflife.utils.CopyComponentsExtension;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(value = ProcessingRecipe.class,remap = false)
public abstract class ProcessingRecipeMixin<I extends RecipeInput, P extends ProcessingRecipeParams> {

    @Shadow public abstract P getParams();

    @Inject(method = "assemble", at = @At("RETURN"), cancellable = true)
    private void createQOL$copyComponent(I t, HolderLookup.Provider provider, CallbackInfoReturnable<ItemStack> cir){
        ItemStack returned = cir.getReturnValue();
        if (!t.getItem(((CopyComponentsExtension)getParams()).createQOL$copiedSlot()).isEmpty()){
           ItemStack from = t.getItem(((CopyComponentsExtension)getParams()).createQOL$copiedSlot());
            ((CopyComponentsExtension)getParams()).createQOL$copiedComponents().stream().map(BuiltInRegistries.DATA_COMPONENT_TYPE::get)
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
