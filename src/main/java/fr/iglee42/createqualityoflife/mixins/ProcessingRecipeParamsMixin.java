package fr.iglee42.createqualityoflife.mixins;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import fr.iglee42.createqualityoflife.utils.CopyComponentsExtension;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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

@Mixin(value = ProcessingRecipeParams.class,remap = false)
public class ProcessingRecipeParamsMixin implements CopyComponentsExtension{

    @Unique
    private List<ResourceLocation> createQOL$copiedComponent = List.of();

    @Unique
    private int createQOL$copiedSlot = 0;

    @Inject(method = "codec",at = @At("RETURN"),cancellable = true)
    private static <P extends ProcessingRecipeParams> void createQOL$addCopiedComponentsToCodec(Supplier<P> factory, CallbackInfoReturnable<MapCodec<P>> cir){
        MapCodec<P> originalCodec = cir.getReturnValue();
        MapCodec<P> extendedCodec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                originalCodec.forGetter(Function.identity()),
                ResourceLocation.CODEC.listOf().optionalFieldOf("copied_components", List.of()).forGetter(p->((CopyComponentsExtension)p).createQOL$copiedComponents()),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("copied_components_slot", 0).forGetter(p->((CopyComponentsExtension)p).createQOL$copiedSlot())
        ).apply(instance, (params, copiedComponents,copiedSlot) -> {
            ((CopyComponentsExtension)params).createQOL$setCopiedComponents(copiedComponents);
            ((CopyComponentsExtension)params).createQOL$setCopiedSlot(copiedSlot);
            return params;
        }));
        cir.setReturnValue(extendedCodec);
    }

    @Inject(method = "encode",at = @At("TAIL"))
    private void createQOL$addCopiedComponentsToEncode(RegistryFriendlyByteBuf buffer, CallbackInfo ci){
        ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer,createQOL$copiedComponents());
    }

    @Inject(method = "decode",at = @At("TAIL"))
    private void createQOL$retrieveCopiedComponentsFromDecode(RegistryFriendlyByteBuf buffer, CallbackInfo ci){
        createQOL$setCopiedComponents(ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer));
    }

    @Override
    public List<ResourceLocation> createQOL$copiedComponents() {
        return createQOL$copiedComponent;
    }

    @Override
    public void createQOL$setCopiedComponents(List<ResourceLocation> list) {
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
