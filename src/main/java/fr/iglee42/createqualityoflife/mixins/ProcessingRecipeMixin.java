package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import fr.iglee42.createqualityoflife.utils.CopyNBTsExtension;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = ProcessingRecipe.class,remap = false)
public class ProcessingRecipeMixin<T extends Container> implements CopyNBTsExtension {

    @Inject(method = "<init>",at = @At(value = "RETURN"))
    private void createQOL$copyComponentFromParams(IRecipeTypeInfo typeInfo, ProcessingRecipeBuilder.ProcessingRecipeParams params, CallbackInfo ci){
        createQOL$setCopiedNBTs(((CopyNBTsExtension)params).createQOL$copiedNBTs());
        createQOL$setCopiedSlot(((CopyNBTsExtension)params).createQOL$copiedSlot());
    }

    @Inject(method = "assemble", at = @At("RETURN"), cancellable = true)
    private void createQOL$copyComponent(T inv, RegistryAccess registryAccess, CallbackInfoReturnable<ItemStack> cir){
        ItemStack returned = cir.getReturnValue();
        if (!inv.getItem(createQOL$copiedSlot()).isEmpty()){
           ItemStack from = inv.getItem(createQOL$copiedSlot());
           createQOL$copiedNBTs().stream()
                    .filter(Predicate.not(String::isEmpty))
                    .forEach(d->{
                        if (from.getOrCreateTag().contains(d)) returned.getOrCreateTag().put(d,from.getOrCreateTag().get(d).copy());
                    });
        }
        cir.setReturnValue(returned);
    }


    @Unique
    private List<String> createQOL$copiedComponent = List.of();

    @Unique
    private int createQOL$copiedSlot = 0;

    @Override
    public List<String> createQOL$copiedNBTs() {
        return createQOL$copiedComponent;
    }

    @Override
    public void createQOL$setCopiedNBTs(List<String> list) {
        createQOL$copiedComponent = list;
    }

    public int createQOL$copiedSlot() {
        return createQOL$copiedSlot;
    }

    @Override
    public void createQOL$setCopiedSlot(int slot) {
        createQOL$copiedSlot = slot;
    }
}
