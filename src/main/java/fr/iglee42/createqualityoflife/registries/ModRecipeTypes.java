package fr.iglee42.createqualityoflife.registries;

import com.mojang.serialization.Codec;
import com.simibubi.create.Create;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.recipes.BlazeBurnerLiquidRecipe;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        id = Create.asResource(name);
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
        id = Create.asResource(name);
        this.serializerSupplier = serializerSupplier;
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        typeObject = Registers.TYPE_REGISTER.register(name, () -> RecipeType.simple(id));
        type = typeObject;
        isProcessingRecipe = false;
    }

    ModRecipeTypes(StandardProcessingRecipe.Factory<?> processingFactory) {
        this(() -> new StandardProcessingRecipe.Serializer<>(processingFactory));
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


    private static class Registers {
        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, CreateQOL.MODID);
        private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, CreateQOL.MODID);
    }

}