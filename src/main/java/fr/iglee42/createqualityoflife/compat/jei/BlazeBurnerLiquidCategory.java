package fr.iglee42.createqualityoflife.compat.jei;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import fr.iglee42.createqualityoflife.recipes.BlazeBurnerLiquidRecipe;
import fr.iglee42.createqualityoflife.utils.liquidblazeburners.LiquidBlazeBurnerManager;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@ParametersAreNonnullByDefault
public class BlazeBurnerLiquidCategory extends CreateRecipeCategory<BlazeBurnerLiquidRecipe> {

	private final AnimatedBlazeBurner burner = new AnimatedBlazeBurner();

	public BlazeBurnerLiquidCategory(Info<BlazeBurnerLiquidRecipe> info) {
		super(info);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, BlazeBurnerLiquidRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 51, 5)
				.setBackground(getRenderedSlot(), -1, -1)
				.addFluidStack(recipe.getFluidIngredients().get(0).getFluids()[0].getFluid(),recipe.getFluidIngredients().get(0).amount());
        Arrays.stream(recipe.getFluidIngredients().get(0).getFluids()).forEach(fs->{
            fs.getFluid().getBucket();
            if (!fs.getFluid().getBucket().equals(Items.AIR)) builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemLike(fs.getFluid().getBucket());
		});
	}

	@Override
	public void draw(BlazeBurnerLiquidRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_SHADOW.render(graphics, 62, 47);
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 74, 10);

		burner.withHeat(recipe.getBurnerLevel()).draw(graphics,73,12);

		if (recipe.getFluidIngredients().get(0).getFluids().length > 0 && recipe.getFluidIngredients().get(0).getFluids()[0] != null) {
			LiquidBlazeBurnerManager.LiquidEntry entry = LiquidBlazeBurnerManager.BLAZE_BURNER_LIQUIDS.get(recipe.getFluidIngredients().get(0).getFluids()[0].getFluid() );
			int time = (entry.burnTime() * entry.consumption() * 1000 / 20);
            Component burnerLevel = Component.translatable("createqol.recipe.blaze_burner_liquids.burner_level."
                    + ((recipe.getBurnerLevel().name().equalsIgnoreCase("seething")) ? "superheat" : "heat"));

			graphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("createqol.recipe.blaze_burner_liquids.hint", formatDuration(time), burnerLevel), 94,60,0xffffff);

		}
		//matrixStack.translate(74, 51, 100);

	}

	public static String formatDuration(int totalSeconds) {
		int hours = totalSeconds / 3600;
		int minutes = (totalSeconds % 3600) / 60;
		int seconds = totalSeconds % 60;
		StringBuilder result = new StringBuilder();

		if (hours > 0) {
			result.append(Component.translatable("createqol.recipe.blaze_burner_liquids.duration.hours", hours).getString());
		}
		if (minutes > 0) {
			result.append(Component.translatable("createqol.recipe.blaze_burner_liquids.duration.minutes", minutes).getString());
		}
		if (seconds > 0 || result.isEmpty()) { // always show at least seconds
			result.append(Component.translatable("createqol.recipe.blaze_burner_liquids.duration.seconds", seconds).getString());
		}

		return result.toString().trim();
	}

}
