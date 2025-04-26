package fr.iglee42.createqualityoflife.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import fr.iglee42.createqualityoflife.recipes.BlazeBurnerLiquidRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

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
				.addFluidStack(recipe.getFluidIngredients().get(0).getMatchingFluidStacks().getFirst().getFluid(),recipe.getFluidIngredients().get(0).getRequiredAmount());
		recipe.getFluidIngredients().get(0).getMatchingFluidStacks().forEach(fs->{
            fs.getFluid().getBucket();
            if (!fs.getFluid().getBucket().equals(Items.AIR)) builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemLike(fs.getFluid().getBucket());
		});
	}

	@Override
	public void draw(BlazeBurnerLiquidRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_SHADOW.render(graphics, 62, 47);
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 74, 10);

		burner.withHeat(recipe.getBurnerLevel()).draw(graphics,73,12);


		//matrixStack.translate(74, 51, 100);

	}

}
