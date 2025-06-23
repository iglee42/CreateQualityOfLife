package fr.iglee42.createqualityoflife.compat.jei;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.compat.jei.*;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CRecipes;
import earth.terrarium.chipped.common.compat.jei.WorkbenchCategory;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.client.screens.ConfigureStatueScreen;
import fr.iglee42.createqualityoflife.recipes.BlazeBurnerLiquidRecipe;
import fr.iglee42.createqualityoflife.registries.ModBlocks;
import fr.iglee42.createqualityoflife.statue.StatueMenu;
import fr.iglee42.createqualityoflife.utils.Features;
import fr.iglee42.createqualityoflife.utils.liquidblazeburners.LiquidBlazeBurnerManager;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    private final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();
    private IIngredientManager ingredientManager;

    @Override
    public ResourceLocation getPluginUid() {
        return CreateQOL.asResource("jei");
    }

    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (CreateQOL.isChippedLoaded() && CreateQOL.isActivate(Features.CHIPPED_SAW)) {
            registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.BOTANIST_SAW.get()), WorkbenchCategory.RECIPE);
            registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.GLASSBLOWER_SAW.get()), WorkbenchCategory.RECIPE);
            registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.CARPENTERS_SAW.get()), WorkbenchCategory.RECIPE);
            registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.LOOM_SAW.get()), WorkbenchCategory.RECIPE);
            registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.MASON_SAW.get()), WorkbenchCategory.RECIPE);
            registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.ALCHEMY_SAW.get()), WorkbenchCategory.RECIPE);
            registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.TINKERING_SAW.get()), WorkbenchCategory.RECIPE);
        }
        allCategories.forEach(c -> c.registerCatalysts(registration));

    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(ConfigureStatueScreen.class, new QOLGhostIngredientHandler());
    }


    private void loadCategories() {
        allCategories.clear();

        CreateRecipeCategory<?>

                blazeBurnerLiquids = builder(BlazeBurnerLiquidRecipe.class)
                .enableWhen(()->CreateQOL.isActivate(Features.LIQUID_BLAZE_BURNER))
                .addRecipes(()->{
                    List<RecipeHolder<BlazeBurnerLiquidRecipe>> recipes = new ArrayList<>();
                    LiquidBlazeBurnerManager.BLAZE_BURNER_LIQUIDS.forEach((fluid,entry)->{
                        recipes.add(BlazeBurnerLiquidRecipe.create(fluid,entry.superHeated() ? BlazeBurnerBlock.HeatLevel.SEETHING : BlazeBurnerBlock.HeatLevel.FADING));
                    });
                    return recipes;
                })
                .catalystStack(AllBlocks.BLAZE_BURNER::asStack)
                .doubleItemIcon(AllBlocks.BLAZE_BURNER.get(), Items.LAVA_BUCKET)
                .emptyBackground(178, 72)
                .build("blaze_burner_liquids", BlazeBurnerLiquidCategory::new);

    }

    private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    }
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        loadCategories();
        registration.addRecipeCategories(allCategories.toArray(IRecipeCategory[]::new));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ingredientManager = registration.getIngredientManager();

        allCategories.forEach(c -> c.registerRecipes(registration));
    }





    private class CategoryBuilder<T extends Recipe<?>> extends CreateRecipeCategory.Builder<T> {
        public CategoryBuilder(Class<? extends T> recipeClass) {
            super(recipeClass);
        }

        @Override
        public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
            return build(CreateQOL.asResource(name), factory);
        }

        @Override
        public CreateRecipeCategory<T> build(ResourceLocation id, CreateRecipeCategory.Factory<T> factory) {
            CreateRecipeCategory<T> category = super.build(id, factory);
            allCategories.add(category);
            return category;
        }
    }


    public static void consumeAllRecipes(Consumer<? super RecipeHolder<?>> consumer) {
        Minecraft.getInstance()
                .getConnection()
                .getRecipeManager()
                .getRecipes()
                .forEach(consumer);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends Recipe<?>> void consumeTypedRecipes(Consumer<RecipeHolder<?>> consumer, RecipeType<?> type) {
        List<? extends RecipeHolder<?>> map = Minecraft.getInstance()
                .getConnection()
                .getRecipeManager().getAllRecipesFor((RecipeType) type);
        if (!map.isEmpty())
            map.forEach(consumer);
    }

    public static List<RecipeHolder<?>> getTypedRecipes(RecipeType<?> type) {
        List<RecipeHolder<?>> recipes = new ArrayList<>();
        consumeTypedRecipes(recipes::add, type);
        return recipes;
    }

    public static List<RecipeHolder<?>> getTypedRecipesExcluding(RecipeType<?> type, Predicate<RecipeHolder<?>> exclusionPred) {
        List<RecipeHolder<?>> recipes = getTypedRecipes(type);
        recipes.removeIf(exclusionPred);
        return recipes;
    }

    public static boolean doInputsMatch(Recipe<?> recipe1, Recipe<?> recipe2) {
        if (recipe1.getIngredients()
                .isEmpty()
                || recipe2.getIngredients()
                .isEmpty()) {
            return false;
        }
        ItemStack[] matchingStacks = recipe1.getIngredients()
                .getFirst()
                .getItems();
        if (matchingStacks.length == 0) {
            return false;
        }
        return recipe2.getIngredients()
                .getFirst()
                .test(matchingStacks[0]);
    }

    public static boolean doOutputsMatch(Recipe<?> recipe1, Recipe<?> recipe2) {
        RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
        return ItemHelper.sameItem(recipe1.getResultItem(registryAccess), recipe2.getResultItem(registryAccess));
    }

}
