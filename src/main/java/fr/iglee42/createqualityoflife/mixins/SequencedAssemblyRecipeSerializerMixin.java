package fr.iglee42.createqualityoflife.mixins;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeSerializer;
import fr.iglee42.createqualityoflife.utils.CopyComponentsExtension;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(value = SequencedAssemblyRecipeSerializer.class,remap = false)
public class SequencedAssemblyRecipeSerializerMixin {

    @Inject(method = "codec",at = @At("RETURN"),cancellable = true)
    private static void createQOL$addCopiedComponentsToCodec(CallbackInfoReturnable<MapCodec<SequencedAssemblyRecipe>> cir){
        MapCodec<SequencedAssemblyRecipe> originalCodec = cir.getReturnValue();
        MapCodec<SequencedAssemblyRecipe> extendedCodec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                originalCodec.forGetter(Function.identity()),
                ResourceLocation.CODEC.listOf().optionalFieldOf("copied_components", List.of()).forGetter(p->((CopyComponentsExtension)p).createQOL$copiedComponents())
        ).apply(instance, (params, copiedComponents) -> {
            ((CopyComponentsExtension)params).createQOL$setCopiedComponents(copiedComponents);
            return params;
        }));
        cir.setReturnValue(extendedCodec);
    }

    @Inject(method = "toNetwork",at = @At("TAIL"))
    private void createQOL$addCopiedComponentsToEncode(RegistryFriendlyByteBuf buffer, SequencedAssemblyRecipe recipe, CallbackInfo ci){
        ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer,((CopyComponentsExtension)recipe).createQOL$copiedComponents());
    }

    @Inject(method = "fromNetwork",at = @At("TAIL"), cancellable = true)
    private void createQOL$retrieveCopiedComponentsFromDecode(RegistryFriendlyByteBuf buffer, CallbackInfoReturnable<SequencedAssemblyRecipe> cir){
        SequencedAssemblyRecipe returned = cir.getReturnValue();
        ((CopyComponentsExtension)returned).createQOL$setCopiedComponents(ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer));
        cir.setReturnValue(returned);
    }

}
