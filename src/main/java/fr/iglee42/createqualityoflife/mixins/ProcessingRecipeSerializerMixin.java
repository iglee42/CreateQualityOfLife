package fr.iglee42.createqualityoflife.mixins;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import fr.iglee42.createqualityoflife.utils.CopyNBTsExtension;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(value = ProcessingRecipeSerializer.class,remap = false)
public class ProcessingRecipeSerializerMixin <T extends ProcessingRecipe<?>>{

    @Inject(method = "readFromJson",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/recipe/ProcessingRecipeBuilder;build()Lcom/simibubi/create/content/processing/recipe/ProcessingRecipe;"),locals = LocalCapture.CAPTURE_FAILSOFT)
    private void createQOL$readCopiedNbtsFromParams(ResourceLocation recipeId, JsonObject json, CallbackInfoReturnable<T> cir, ProcessingRecipeBuilder builder, NonNullList ingredients, NonNullList fluidIngredients, NonNullList results, NonNullList fluidResults){
        ((CopyNBTsExtension)builder).createQOL$setCopiedNBTs(json.has("copied_nbts") ? json.getAsJsonArray("copied_nbts").asList().stream().map(JsonElement::getAsString).toList() : List.of());
        ((CopyNBTsExtension)builder).createQOL$setCopiedSlot(json.has("copied_slot") ? json.get("copied_slot").getAsInt() : 0);
    }

    @Inject(method = "readFromBuffer",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/recipe/ProcessingRecipe;readAdditional(Lnet/minecraft/network/FriendlyByteBuf;)V"),locals = LocalCapture.CAPTURE_FAILSOFT)
    private void createQOL$readCopiedNbtsFromBuffer(ResourceLocation recipeId, FriendlyByteBuf buffer, CallbackInfoReturnable<T> cir, NonNullList ingredients, NonNullList fluidIngredients, NonNullList results, NonNullList fluidResults, int size,T recipe){
        ((CopyNBTsExtension)recipe).createQOL$setCopiedNBTs(buffer.readList(FriendlyByteBuf::readUtf));
        ((CopyNBTsExtension)recipe).createQOL$setCopiedSlot(buffer.readInt());
    }

    @Inject(method = "writeToBuffer",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/recipe/ProcessingRecipe;writeAdditional(Lnet/minecraft/network/FriendlyByteBuf;)V"),locals = LocalCapture.CAPTURE_FAILSOFT)
    private void createQOL$writeCopiedNbtsToBuffer(FriendlyByteBuf buffer, T recipe, CallbackInfo ci, NonNullList ingredients, NonNullList fluidIngredients, NonNullList outputs, NonNullList fluidOutputs){
        buffer.writeCollection(((CopyNBTsExtension)recipe).createQOL$copiedNBTs(), FriendlyByteBuf::writeUtf);
        buffer.writeInt(((CopyNBTsExtension)recipe).createQOL$copiedSlot());
    }


}
