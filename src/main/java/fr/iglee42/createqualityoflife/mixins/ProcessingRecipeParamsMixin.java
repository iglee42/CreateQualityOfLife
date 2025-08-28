package fr.iglee42.createqualityoflife.mixins;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import fr.iglee42.createqualityoflife.utils.CopyNBTsExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(value = ProcessingRecipeBuilder.ProcessingRecipeParams.class,remap = false)
public class ProcessingRecipeParamsMixin implements CopyNBTsExtension {

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
