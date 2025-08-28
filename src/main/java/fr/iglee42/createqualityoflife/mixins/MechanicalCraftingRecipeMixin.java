package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import fr.iglee42.createqualityoflife.utils.CopyNBTsExtension;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = MechanicalCraftingRecipe.class,remap = false)
public class MechanicalCraftingRecipeMixin implements CopyNBTsExtension {

    @Unique
    private List<String> createQOL$copiedNBTs = List.of();

    @Unique
    private int createQOL$copiedSlot = 0;

    @Override
    public List<String> createQOL$copiedNBTs() {
        return createQOL$copiedNBTs;
    }

    @Override
    public void createQOL$setCopiedNBTs(List<String> list) {
        createQOL$copiedNBTs = list;
    }

    public int createQOL$copiedSlot() {
        return createQOL$copiedSlot;
    }

    @Override
    public void createQOL$setCopiedSlot(int slot) {
        createQOL$copiedSlot = slot;
    }
}
