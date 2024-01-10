package fr.iglee42.createqualityoflife.compat.jei.jei;

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
            registration.addRecipeCatalyst(new ItemStack((ItemLike) ModBlocks.BOTANIST_SAW.get()), new ResourceLocation[]{getChippedUidFromId("botanist_workbench")});
            registration.addRecipeCatalyst(new ItemStack((ItemLike) ModBlocks.GLASSBLOWER_SAW.get()), new ResourceLocation[]{getChippedUidFromId("glassblower")});
            registration.addRecipeCatalyst(new ItemStack((ItemLike) ModBlocks.CARPENTERS_SAW.get()), new ResourceLocation[]{getChippedUidFromId("carpenters_table")});
            registration.addRecipeCatalyst(new ItemStack((ItemLike) ModBlocks.LOOM_SAW.get()), new ResourceLocation[]{getChippedUidFromId("loom_table")});
            registration.addRecipeCatalyst(new ItemStack((ItemLike) ModBlocks.MASON_SAW.get()), new ResourceLocation[]{getChippedUidFromId("mason_table")});
            registration.addRecipeCatalyst(new ItemStack((ItemLike) ModBlocks.ALCHEMY_SAW.get()), new ResourceLocation[]{getChippedUidFromId("alchemy_bench")});
            registration.addRecipeCatalyst(new ItemStack((ItemLike) ModBlocks.TINKERING_SAW.get()), new ResourceLocation[]{getChippedUidFromId("mechanist_workbench")});
        }
    }

    private ResourceLocation getChippedUidFromId(String type) {
        return new ResourceLocation("chipped",type);
    }
}
