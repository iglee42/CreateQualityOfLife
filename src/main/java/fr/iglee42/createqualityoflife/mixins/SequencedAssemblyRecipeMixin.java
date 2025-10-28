package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import fr.iglee42.createqualityoflife.utils.CopyNBTsExtension;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = SequencedAssemblyRecipe.class,remap = false)
public abstract class SequencedAssemblyRecipeMixin implements CopyNBTsExtension {

    @Shadow protected abstract int getStep(ItemStack input);

    @Shadow protected List<SequencedRecipe<?>> sequence;

    @Shadow protected int loops;

    @Inject(method = "advance",at = @At(value = "RETURN"), cancellable = true)
    private void createQOL$copyComponents(ItemStack input, CallbackInfoReturnable<ItemStack> cir){
        ItemStack returned = cir.getReturnValue();
        if (input.getOrCreateTag().contains("SequencedAssembly")){
            int step = getStep(input);
            if ((step + 1) / sequence.size() >= loops){
                CompoundTag comps = input.getOrCreateTag().getCompound(NBTConstants.NBT_COPIED_DATAS);
                if (comps != null) {
                    comps.getAllKeys().forEach(k -> {
                        returned.getOrCreateTag().put(k, comps.get(k).copy());
                    });
                }
            } else {
                if (input.getOrCreateTag().contains(NBTConstants.NBT_COPIED_DATAS)) returned.getOrCreateTag().put(NBTConstants.NBT_COPIED_DATAS,input.getOrCreateTag().get(NBTConstants.NBT_COPIED_DATAS).copy());
            }

        } else {
            List<String> copiedComponents = createQOL$copiedNBTs();
            CompoundTag compoundTag = new CompoundTag();

            input.getOrCreateTag().getAllKeys()
                    .stream().filter(copiedComponents::contains)
                    .forEach(t -> {
                        compoundTag.put(t, input.getOrCreateTag().get(t).copy());
                    });

            returned.getOrCreateTag().put(NBTConstants.NBT_COPIED_DATAS,compoundTag);
        }

        cir.setReturnValue(returned);
    }




    @Unique
    private List<String> createQOL$copiedNBTs = List.of();
    @Override
    public List<String> createQOL$copiedNBTs() {
        return createQOL$copiedNBTs;
    }
    @Override
    public void createQOL$setCopiedNBTs(List<String> list) {
        createQOL$copiedNBTs = list;
    }

}
