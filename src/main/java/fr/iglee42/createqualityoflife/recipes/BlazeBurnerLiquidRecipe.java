package fr.iglee42.createqualityoflife.recipes;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.registries.QOLRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Helper recipe type for displaying an item relationship in JEI
 */
@ParametersAreNonnullByDefault
public class BlazeBurnerLiquidRecipe extends ProcessingRecipe<RecipeWrapper> {

	static int counter = 0;
	private final BlazeBurnerBlock.HeatLevel burnerLevel;

	public static BlazeBurnerLiquidRecipe create(Fluid from, BlazeBurnerBlock.HeatLevel to) {
		ResourceLocation recipeId = CreateQOL.asResource("blaze_burner_liquid_" + counter++);
        return new ProcessingRecipeBuilder<>(p->new BlazeBurnerLiquidRecipe(p,to), recipeId)
            .withFluidIngredients(FluidIngredient.fromFluid(from,1000))
            .build();
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
	public boolean matches(RecipeWrapper inv, Level worldIn) {
		return false;
	}

	@Override
	protected int getMaxInputCount() {
		return 1;
	}

	@Override
	protected int getMaxOutputCount() {
		return 1;
	}

}
