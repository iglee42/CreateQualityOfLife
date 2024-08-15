package fr.iglee42.createqualityoflife.mixins;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(value = ModelBuilder.class,remap = false)
public abstract class ModelBuilderMixin<T extends ModelBuilder<T>> {

    @Shadow @Final protected Map<String, String> textures;


    @Shadow protected abstract T self();

    /**
     * @author iglee
     * @reason Remove Texture Exist Condition
     */
    @Overwrite
    public T texture(String key, ResourceLocation texture) {
        Preconditions.checkNotNull(key, "Key must not be null");
        Preconditions.checkNotNull(texture, "Texture must not be null");
        this.textures.put(key, texture.toString());
        return this.self();
    }
}
