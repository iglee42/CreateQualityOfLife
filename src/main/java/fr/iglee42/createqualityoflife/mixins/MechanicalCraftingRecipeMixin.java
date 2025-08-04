package fr.iglee42.createqualityoflife.mixins;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;

@Mixin(value = MechanicalCraftingRecipe.class,remap = false)
public class MechanicalCraftingRecipeMixin implements CopyComponentsExtension{

    @Unique
    private List<ResourceLocation> createQOL$copiedComponent = List.of();

    @Unique
    private int createQOL$copiedSlot = 0;

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
