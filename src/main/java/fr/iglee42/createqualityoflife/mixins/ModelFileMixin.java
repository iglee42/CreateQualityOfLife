package fr.iglee42.createqualityoflife.mixins;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(value = ModelFile.class,remap = false)
public abstract class ModelFileMixin {



    /**
     * @author iglee
     * @reason Remove File Exist Condition
     */
    @Overwrite
    public void assertExistence() {
    }
}
