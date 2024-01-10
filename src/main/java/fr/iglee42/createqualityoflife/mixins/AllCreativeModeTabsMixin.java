package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.utils.Features;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Set;
import java.util.function.Predicate;
import java.util.List;

@Mixin(targets = {"com.simibubi.create.AllCreativeModeTabs$RegistrateDisplayItemsGenerator"},remap = false)
public class AllCreativeModeTabsMixin {

    @Inject(method = "makeExclusionPredicate", at = @At("RETURN"),locals = LocalCapture.CAPTURE_FAILHARD)
    private static void makeExclusionPredication(CallbackInfoReturnable<Predicate<Item>> cir,Set exclusion){
        if (CreateQOL.isActivate(Features.SHADOW_RADIANCE)){
            exclusion.remove(AllItems.CHROMATIC_COMPOUND.asItem());
            exclusion.remove(AllItems.REFINED_RADIANCE.asItem());
            exclusion.remove(AllItems.SHADOW_STEEL.asItem());
            exclusion.remove(AllBlocks.SHADOW_STEEL_CASING.asItem());
            exclusion.remove(AllBlocks.REFINED_RADIANCE_CASING.asItem());
        }
    }
}
