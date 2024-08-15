package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.compat.jei.category.MysteriousItemConversionCategory;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.utils.Features;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = MysteriousItemConversionCategory.class,remap = false)
public class MysteriousItemConversionCategoryMixin {


    @Inject(method = "<clinit>",at = @At("RETURN"))
    private static void inject(CallbackInfo ci){
        if (CreateQOL.isActivate(Features.SHADOW_RADIANCE)) {
            MysteriousItemConversionCategory.RECIPES.add(ConversionRecipe.create(AllItems.CHROMATIC_COMPOUND.asStack(), AllItems.SHADOW_STEEL.asStack()));
            MysteriousItemConversionCategory.RECIPES.add(ConversionRecipe.create(AllItems.CHROMATIC_COMPOUND.asStack(), AllItems.REFINED_RADIANCE.asStack()));
        }
	}

}
