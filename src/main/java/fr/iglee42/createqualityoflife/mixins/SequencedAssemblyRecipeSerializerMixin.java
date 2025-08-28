package fr.iglee42.createqualityoflife.mixins;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeSerializer;
import fr.iglee42.createqualityoflife.utils.CopyNBTsExtension;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = SequencedAssemblyRecipeSerializer.class,remap = false)
public class SequencedAssemblyRecipeSerializerMixin {

    @Inject(method = "readFromJson",at = @At("RETURN"),cancellable = true)
    private void createQOL$addCopiedNBTsToJSON(ResourceLocation recipeId, JsonObject json, CallbackInfoReturnable<SequencedAssemblyRecipe> cir){
        SequencedAssemblyRecipe originalRecipe = cir.getReturnValue();
        ((CopyNBTsExtension)originalRecipe).createQOL$setCopiedNBTs(json.has("copied_nbts") ? json.getAsJsonArray("copied_nbts").asList().stream().map(JsonElement::getAsString).toList() : List.of());
        ((CopyNBTsExtension)originalRecipe).createQOL$setCopiedSlot(json.has("copied_slot") ? json.get("copied_slot").getAsInt() : 0);
        cir.setReturnValue(originalRecipe);
    }


    @Inject(method = "readFromBuffer",at = @At("RETURN"),cancellable = true)
    private void createQOL$addCopiedNBTsToRead(ResourceLocation recipeId, FriendlyByteBuf buffer, CallbackInfoReturnable<SequencedAssemblyRecipe> cir){
        SequencedAssemblyRecipe originalRecipe = cir.getReturnValue();
        ((CopyNBTsExtension) originalRecipe).createQOL$setCopiedNBTs(buffer.readList(FriendlyByteBuf::readUtf));
        ((CopyNBTsExtension) originalRecipe).createQOL$setCopiedSlot(buffer.readInt());
        cir.setReturnValue(originalRecipe);
    }

    @Inject(method = "writeToBuffer",at = @At(value = "RETURN"))
    private void createQOL$addCopiedNBTsToWrite(FriendlyByteBuf buffer, SequencedAssemblyRecipe recipe, CallbackInfo ci){
        buffer.writeCollection(((CopyNBTsExtension)recipe).createQOL$copiedNBTs(), FriendlyByteBuf::writeUtf);
        buffer.writeInt(((CopyNBTsExtension)recipe).createQOL$copiedSlot());
    }

    @Inject(method = "writeToJson",at = @At(value = "RETURN"))
    private void createQOL$addSecondBooleanToWrite(JsonObject json, SequencedAssemblyRecipe recipe, CallbackInfo ci){
        json.add("copied_nbts", new Gson().toJsonTree(((CopyNBTsExtension)recipe).createQOL$copiedNBTs()).getAsJsonArray());
        json.addProperty("copied_slot", ((CopyNBTsExtension)recipe).createQOL$copiedSlot());
    }

}
