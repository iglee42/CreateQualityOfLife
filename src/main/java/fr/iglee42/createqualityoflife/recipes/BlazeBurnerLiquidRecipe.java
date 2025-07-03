package fr.iglee42.createqualityoflife.recipes;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.registries.QOLRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

/**
 * Helper recipe type for displaying an item relationship in JEI
 */
@ParametersAreNonnullByDefault
public class BlazeBurnerLiquidRecipe extends StandardProcessingRecipe<RecipeInput> {

	static int counter = 0;
	private final BlazeBurnerBlock.HeatLevel burnerLevel;

	public static RecipeHolder<BlazeBurnerLiquidRecipe> create(Fluid from, BlazeBurnerBlock.HeatLevel to) {
		ResourceLocation recipeId = CreateQOL.asResource("blaze_burner_liquid_" + counter++);
		BlazeBurnerLiquidRecipe recipe = new StandardProcessingRecipe.Builder<>(p->new BlazeBurnerLiquidRecipe(p,to), recipeId)
			.withFluidIngredients(FluidIngredient.fromFluid(from,1000))
			.build();
		return new RecipeHolder<>(recipeId, recipe);
	}

	public BlazeBurnerLiquidRecipe(ProcessingRecipeParams params) {
		super(QOLRecipeTypes.BLAZE_BURNER_LIQUIDS, params);
		this.burnerLevel = BlazeBurnerBlock.HeatLevel.NONE;
	}

	public BlazeBurnerLiquidRecipe(ProcessingRecipeParams params, BlazeBurnerBlock.HeatLevel burnerLevel) {
		super(QOLRecipeTypes.BLAZE_BURNER_LIQUIDS, params);
		this.burnerLevel = burnerLevel;
	}


	public BlazeBurnerBlock.HeatLevel getBurnerLevel() {
		return burnerLevel;
	}

	@Override
	protected int getMaxInputCount() {
		return 1;
	}

	@Override
	protected int getMaxOutputCount() {
		return 1;
	}

	@Override
	public boolean matches(RecipeInput recipeInput, Level level) {
		return false;
	}
}
