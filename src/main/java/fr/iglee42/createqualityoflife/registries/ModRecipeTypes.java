package fr.iglee42.createqualityoflife.registries;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.*;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.recipes.BlazeBurnerLiquidRecipe;
import fr.iglee42.createqualityoflife.recipes.QOLProcessingRecipeSerializer;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public enum ModRecipeTypes implements IRecipeTypeInfo,StringRepresentable {
    BLAZE_BURNER_LIQUIDS(BlazeBurnerLiquidRecipe::new),
    ;

    public final ResourceLocation id;
    public final Supplier<RecipeSerializer<?>> serializerSupplier;
    private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializerObject;
    @Nullable
    private final DeferredHolder<RecipeType<?>, RecipeType<?>> typeObject;
    private final Supplier<RecipeType<?>> type;

    private boolean isProcessingRecipe;

    public static final Codec<ModRecipeTypes> CODEC = StringRepresentable.fromEnum(ModRecipeTypes::values);

    ModRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier, boolean registerType) {
        String name = Lang.asId(name());
        id = CreateQOL.asResource(name);
        this.serializerSupplier = serializerSupplier;
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        if (registerType) {
            typeObject = Registers.TYPE_REGISTER.register(name, typeSupplier);
            type = typeObject;
        } else {
            typeObject = null;
            type = typeSupplier;
        }
        isProcessingRecipe = false;
    }

    ModRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = Lang.asId(name());
        id = CreateQOL.asResource(name);
        this.serializerSupplier = serializerSupplier;
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        typeObject = Registers.TYPE_REGISTER.register(name, () -> RecipeType.simple(id));
        type = typeObject;
        isProcessingRecipe = false;
    }

    ModRecipeTypes(ProcessingRecipeBuilder.ProcessingRecipeFactory<?> processingFactory) {
        this(() -> new QOLProcessingRecipeSerializer<>(processingFactory));
        isProcessingRecipe = true;
    }

    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        ShapedRecipePattern.setCraftingSize(9, 9);
        Registers.SERIALIZER_REGISTER.register(modEventBus);
        Registers.TYPE_REGISTER.register(modEventBus);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializerObject.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) type.get();
    }

    public <I extends RecipeInput, R extends Recipe<I>> Optional<RecipeHolder<R>> find(I inv, Level world) {
        return world.getRecipeManager()
                .getRecipeFor(getType(), inv, world);
    }


    @Override
    public @NotNull String getSerializedName() {
        return id.toString();
    }

    public <T extends ProcessingRecipe<?>> MapCodec<T> processingCodec() {
        if (!isProcessingRecipe)
            throw new AssertionError("ModRecipeTypes#processingCodec called on "+name()+", which is not a processing recipe");
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.either(Ingredient.CODEC, FluidIngredient.CODEC).listOf().fieldOf("ingredients").forGetter(i -> {
                    List<Either<Ingredient, FluidIngredient>> list = new ArrayList<>();
                    i.getIngredients().forEach(o -> list.add(Either.left(o)));
                    i.getFluidIngredients().forEach(o -> list.add(Either.right(o)));
                    return list;
                }),
                Codec.either(ProcessingOutput.CODEC, FluidStack.CODEC).listOf().fieldOf("results").forGetter(i -> {
                    List<Either<ProcessingOutput, FluidStack>> list = new ArrayList<>();
                    i.getRollableResults().forEach(o -> list.add(Either.left(o)));
                    i.getFluidResults().forEach(o -> list.add(Either.right(o)));
                    return list;
                }),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("processing_time", 0).forGetter(T::getProcessingDuration),
                StringRepresentable.fromEnum(HeatCondition::values).optionalFieldOf("heat_requirement", HeatCondition.NONE).forGetter(T::getRequiredHeat)
        ).apply(instance, (ingredients, results, processingTime, heatRequirement) -> {
            if (!(serializerSupplier.get() instanceof ProcessingRecipeSerializer processingRecipeSerializer))
                throw new RuntimeException("Not a processing recipe serializer " + serializerSupplier.get());

            ProcessingRecipeBuilder<T> builder = new ProcessingRecipeBuilder<T>(processingRecipeSerializer.getFactory(), this.id);

            NonNullList<Ingredient> ingredientList = NonNullList.create();
            NonNullList<FluidIngredient> fluidIngredientList = NonNullList.create();

            NonNullList<ProcessingOutput> processingOutputList = NonNullList.create();
            NonNullList<FluidStack> fluidStackOutputList = NonNullList.create();

            for (Either<Ingredient, FluidIngredient> either : ingredients) {
                either.left().ifPresent(ingredientList::add);
                either.right().ifPresent(fluidIngredientList::add);
            }

            for (Either<ProcessingOutput, FluidStack> either : results) {
                either.left().ifPresent(processingOutputList::add);
                either.right().ifPresent(fluidStackOutputList::add);
            }

            builder.withItemIngredients(ingredientList)
                    .withItemOutputs(processingOutputList)
                    .withFluidIngredients(fluidIngredientList)
                    .withFluidOutputs(fluidStackOutputList)
                    .duration(processingTime)
                    .requiresHeat(heatRequirement);

            return builder.build();
        }));
    }

    private static class Registers {
        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, CreateQOL.MODID);
        private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, CreateQOL.MODID);
    }

}