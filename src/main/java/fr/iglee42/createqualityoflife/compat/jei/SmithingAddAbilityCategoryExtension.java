package fr.iglee42.createqualityoflife.compat.jei;

import fr.iglee42.createqualityoflife.recipes.ApplyShadowRadianceAbilityRecipe;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SmithingRecipeInput;

import java.util.List;

public class SmithingAddAbilityCategoryExtension implements ISmithingCategoryExtension<ApplyShadowRadianceAbilityRecipe> {

	@Override
	public void onDisplayedIngredientsUpdate(
			ApplyShadowRadianceAbilityRecipe recipe,
		IRecipeSlotDrawable templateSlot,
		IRecipeSlotDrawable baseSlot,
		IRecipeSlotDrawable additionSlot,
		IRecipeSlotDrawable outputSlot,
		IFocusGroup focuses
	) {
		List<IFocus<?>> outputFocuses = focuses.getFocuses(RecipeIngredientRole.OUTPUT).toList();
		if (outputFocuses.isEmpty()) {
			ItemStack template = templateSlot.getDisplayedItemStack().orElse(ItemStack.EMPTY);
			ItemStack base = baseSlot.getDisplayedItemStack().orElse(ItemStack.EMPTY);
			ItemStack addition = additionSlot.getDisplayedItemStack().orElse(ItemStack.EMPTY);

			SmithingRecipeInput recipeInput = new SmithingRecipeInput(template, base, addition);
			ItemStack output = assembleResultItem(recipeInput, recipe);
			outputSlot.createDisplayOverrides()
				.addItemStack(output);
		} else {
			ItemStack output = outputSlot.getDisplayedItemStack().orElse(ItemStack.EMPTY);
			ItemStack base = new ItemStack(output.getItem());
			ItemStack template = templateSlot.getDisplayedItemStack().orElse(ItemStack.EMPTY);
			ItemStack addition = additionSlot.getDisplayedItemStack().orElse(ItemStack.EMPTY);

			baseSlot.createDisplayOverrides()
				.addItemStack(base);

			SmithingRecipeInput recipeInput = new SmithingRecipeInput(template, base, addition);
			output = assembleResultItem(recipeInput, recipe);
			outputSlot.createDisplayOverrides()
				.addItemStack(output);
		}
	}

	public static <I extends RecipeInput> ItemStack assembleResultItem(I input, Recipe<I> recipe) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		if (level == null) {
			throw new NullPointerException("level must not be null.");
		}
		RegistryAccess registryAccess = level.registryAccess();
		return recipe.assemble(input, registryAccess);
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setTemplate(ApplyShadowRadianceAbilityRecipe recipe, T ingredientAcceptor) {
		ingredientAcceptor.addIngredients(recipe.template());
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setBase(ApplyShadowRadianceAbilityRecipe recipe, T ingredientAcceptor) {
		ingredientAcceptor.addIngredients(recipe.base());
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setAddition(ApplyShadowRadianceAbilityRecipe recipe, T ingredientAcceptor) {
		ingredientAcceptor.addIngredients(recipe.addition());
	}
}
