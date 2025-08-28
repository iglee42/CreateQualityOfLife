package fr.iglee42.createqualityoflife.mixins;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import fr.iglee42.createqualityoflife.utils.CopyNBTsExtension;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = MechanicalCraftingRecipe.Serializer.class,remap = false)
public class MechanicalCraftingRecipeSerializerMixin{

    @Inject(method = "fromJson(Lnet/minecraft/resources/ResourceLocation;Lcom/google/gson/JsonObject;)Lnet/minecraft/world/item/crafting/ShapedRecipe;",at = @At("RETURN"),cancellable = true)
    private void createQOL$addCopiedNBTsToJSON(ResourceLocation recipeId, JsonObject json, CallbackInfoReturnable<ShapedRecipe> cir){
        MechanicalCraftingRecipe originalRecipe = (MechanicalCraftingRecipe)cir.getReturnValue();
        ((CopyNBTsExtension)originalRecipe).createQOL$setCopiedNBTs(json.has("copied_nbts") ? json.getAsJsonArray("copied_nbts").asList().stream().map(JsonElement::getAsString).toList() : List.of());
        ((CopyNBTsExtension)originalRecipe).createQOL$setCopiedSlot(json.has("copied_slot") ? json.get("copied_slot").getAsInt() : 0);
        cir.setReturnValue(originalRecipe);
    }


    @Inject(method = "fromNetwork(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/network/FriendlyByteBuf;)Lnet/minecraft/world/item/crafting/ShapedRecipe;",at = @At("RETURN"),cancellable = true)
    private void createQOL$addCopiedNBTsToRead(ResourceLocation recipeId, FriendlyByteBuf buffer, CallbackInfoReturnable<ShapedRecipe> cir){
        MechanicalCraftingRecipe originalRecipe = (MechanicalCraftingRecipe)cir.getReturnValue();
        if (buffer.readBoolean()) {
            ((CopyNBTsExtension) originalRecipe).createQOL$setCopiedNBTs(buffer.readList(FriendlyByteBuf::readUtf));
            ((CopyNBTsExtension) originalRecipe).createQOL$setCopiedSlot(buffer.readInt());
        }
        cir.setReturnValue(originalRecipe);
    }

    @Inject(method = "toNetwork(Lnet/minecraft/network/FriendlyByteBuf;Lnet/minecraft/world/item/crafting/ShapedRecipe;)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeBoolean(Z)Lio/netty/buffer/ByteBuf;",ordinal = 1,shift = At.Shift.AFTER))
    private void createQOL$addCopiedNBTsToWrite(FriendlyByteBuf buf, ShapedRecipe recipe, CallbackInfo ci){
        buf.writeBoolean(true);
        buf.writeCollection(((CopyNBTsExtension)recipe).createQOL$copiedNBTs(), FriendlyByteBuf::writeUtf);
        buf.writeInt(((CopyNBTsExtension)recipe).createQOL$copiedSlot());
}

    @Inject(method = "toNetwork(Lnet/minecraft/network/FriendlyByteBuf;Lnet/minecraft/world/item/crafting/ShapedRecipe;)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeBoolean(Z)Lio/netty/buffer/ByteBuf;",ordinal = 2,shift = At.Shift.AFTER))
    private void createQOL$addSecondBooleanToWrite(FriendlyByteBuf buf, ShapedRecipe recipe, CallbackInfo ci){
        buf.writeBoolean(false);
    }


}
