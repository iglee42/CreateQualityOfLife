package fr.iglee42.createqualityoflife.mixins;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import fr.iglee42.createqualityoflife.utils.CopyComponentsExtension;
import fr.iglee42.createqualityoflife.utils.Utils;
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

@Mixin(value = MechanicalCraftingRecipe.Serializer.class,remap = false)
public class MechanicalCraftingRecipeSerializerMixin{

    @Inject(method = "codec",at = @At("RETURN"),cancellable = true)
    private static void createQOL$addCopiedComponentsToCodec(CallbackInfoReturnable<MapCodec<MechanicalCraftingRecipe>> cir){
        MapCodec<MechanicalCraftingRecipe> originalCodec = cir.getReturnValue();
        MapCodec<MechanicalCraftingRecipe> extendedCodec = RecordCodecBuilder.mapCodec(instance -> instance.group(
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


    @Inject(method = "streamCodec",at = @At("RETURN"),cancellable = true)
    private static void createQOL$addCopiedComponentsToStreamCodec(CallbackInfoReturnable<StreamCodec<RegistryFriendlyByteBuf, MechanicalCraftingRecipe>> cir){
        StreamCodec<RegistryFriendlyByteBuf, MechanicalCraftingRecipe> originalCodec = cir.getReturnValue();
        StreamCodec<RegistryFriendlyByteBuf, MechanicalCraftingRecipe> extendedCodec = StreamCodec.composite(
                originalCodec, i->i,
                ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()),i->((CopyComponentsExtension)i).createQOL$copiedComponents(),
                ByteBufCodecs.INT,i->((CopyComponentsExtension)i).createQOL$copiedSlot(),
                Utils::mechanicalCraftingRecipeFromShapedAndComponents
        );
        cir.setReturnValue(extendedCodec);
    }

}
