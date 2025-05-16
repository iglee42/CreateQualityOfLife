package fr.iglee42.createqualityoflife.mixins;

import fr.iglee42.createqualityoflife.registries.ModEntityDataSerializers;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityDataSerializers.class)
public abstract class EntityDataSerializersMixin {

    @Shadow
    public static void registerSerializer(EntityDataSerializer<?> p_135051_) {
    }

    @Inject(method = "<clinit>",at = @At("TAIL"))
    private static void createQOL$addCustomSerializers(CallbackInfo ci){
        registerSerializer(ModEntityDataSerializers.PROFILE_ENTITY_DATA_SERIALIZER);
        registerSerializer(ModEntityDataSerializers.ANIMATION_DATA_SERIALIZER);
    }
}
