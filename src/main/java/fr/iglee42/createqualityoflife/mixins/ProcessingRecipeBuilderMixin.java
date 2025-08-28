package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import fr.iglee42.createqualityoflife.utils.CopyNBTsExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = ProcessingRecipeBuilder.class,remap = false)
public class ProcessingRecipeBuilderMixin implements CopyNBTsExtension {

    @Shadow protected ProcessingRecipeBuilder.ProcessingRecipeParams params;

    @Override
    public List<String> createQOL$copiedNBTs() {
        return List.of();
    }

    @Override
    public void createQOL$setCopiedNBTs(List<String> list) {
        ((CopyNBTsExtension)params).createQOL$setCopiedNBTs(list);
    }

    @Override
    public void createQOL$setCopiedSlot(int slot) {
        ((CopyNBTsExtension)params).createQOL$setCopiedSlot(slot);
    }
}
