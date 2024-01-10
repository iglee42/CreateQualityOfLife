package fr.iglee42.createqualityoflife.compat.jei.jei;

import earth.terrarium.chipped.common.compat.jei.ChippedRecipeCategory;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.registries.ModBlocks;
import fr.iglee42.createqualityoflife.utils.Features;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation("createqol:jei");
    }

    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (CreateQOL.isChippedLoaded() && CreateQOL.isActivate(Features.CHIPPED_SAW)) {
            registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.BOTANIST_SAW.get()), ChippedRecipeCategory.BOTANIST_WORKBENCH_RECIPE);
            registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.GLASSBLOWER_SAW.get()), ChippedRecipeCategory.GLASSBLOWER_RECIPE);
            registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.CARPENTERS_SAW.get()), ChippedRecipeCategory.CARPENTERS_TABLE_RECIPE);
            registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.LOOM_SAW.get()), ChippedRecipeCategory.LOOM_TABLE_RECIPE);
            registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.MASON_SAW.get()), ChippedRecipeCategory.MASON_TABLE_RECIPE);
            registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.ALCHEMY_SAW.get()), ChippedRecipeCategory.ALCHEMY_BENCH_RECIPE);
            registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.TINKERING_SAW.get()), ChippedRecipeCategory.TINKERING_TABLE_RECIPE);
        }
    }

}
