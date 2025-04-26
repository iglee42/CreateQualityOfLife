package fr.iglee42.createqualityoflife.recipes;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import fr.iglee42.createqualityoflife.registries.ModRecipeTypes;

public class QOLProcessingRecipeSerializer<T extends ProcessingRecipe<?>> extends ProcessingRecipeSerializer<T> {

    public final MapCodec<T> CODEC = ModRecipeTypes.CODEC.dispatchMap(t -> (ModRecipeTypes) t.getTypeInfo(), ModRecipeTypes::processingCodec);


    public QOLProcessingRecipeSerializer(ProcessingRecipeBuilder.ProcessingRecipeFactory<T> factory) {
        super(factory);
    }


    @Override
    public MapCodec<T> codec() {
        return CODEC;
    }
}