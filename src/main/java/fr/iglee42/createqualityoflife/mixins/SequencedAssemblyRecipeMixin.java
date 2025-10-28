package fr.iglee42.createqualityoflife.mixins;

import com.mojang.serialization.Codec;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.utils.CopyComponentsExtension;
import fr.iglee42.createqualityoflife.utils.Utils;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(value = SequencedAssemblyRecipe.class,remap = false)
public abstract class SequencedAssemblyRecipeMixin implements CopyComponentsExtension {

    @Shadow protected abstract int getStep(ItemStack input);

    @Shadow protected List<SequencedRecipe<?>> sequence;

    @Shadow protected int loops;

    @Inject(method = "advance",at = @At(value = "RETURN"), cancellable = true)
    private void createQOL$copyComponents(ResourceLocation id, ItemStack input, RandomSource random, CallbackInfoReturnable<ItemStack> cir){
        ItemStack returned = cir.getReturnValue();
        if (input.has(AllDataComponents.SEQUENCED_ASSEMBLY)){
            int step = getStep(input);
            if ((step + 1) / sequence.size() >= loops){
                CompoundTag comps = input.get(QOLDataComponents.COPIED_DATAS);
                if (comps != null) {
                    Level level;
                    if (ServerLifecycleHooks.getCurrentServer() != null && ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD) != null) level = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD);
                    else {
                        level = null;
                    }
                    comps.getAllKeys().forEach(k -> {
                        if (ResourceLocation.tryParse(k) != null && BuiltInRegistries.DATA_COMPONENT_TYPE.get(ResourceLocation.tryParse(k)) != null) {
                            Utils.applyComponent(returned,comps,BuiltInRegistries.DATA_COMPONENT_TYPE.get(ResourceLocation.tryParse(k)),level != null ? level.registryAccess() : null );
                        }
                    });
                }
            } else {
                if (input.has(QOLDataComponents.COPIED_DATAS)) returned.set(QOLDataComponents.COPIED_DATAS,input.get(QOLDataComponents.COPIED_DATAS));
            }

        } else {
            List<ResourceLocation> copiedComponents = createQOL$copiedComponents();
            CompoundTag compoundTag = new CompoundTag();

            Level level;
            if (ServerLifecycleHooks.getCurrentServer() != null && ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD) != null) level = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD);
            else {
                level = null;
            }
            input.getComponents()
                    .filter(t -> copiedComponents.contains(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(t)))
                    .forEach(tdc -> {
                        Utils.encodeComponent(compoundTag,tdc.type(),tdc.value(),level != null ? level.registryAccess() : null);
                    });

            returned.set(QOLDataComponents.COPIED_DATAS,compoundTag);
        }

        cir.setReturnValue(returned);
    }




    @Unique
    private List<ResourceLocation> createQOL$copiedComponent = List.of();
    @Override
    public List<ResourceLocation> createQOL$copiedComponents() {
        return createQOL$copiedComponent;
    }

    @Override
    public void createQOL$setCopiedComponents(List<ResourceLocation> list) {
        createQOL$copiedComponent = list;
    }

}
