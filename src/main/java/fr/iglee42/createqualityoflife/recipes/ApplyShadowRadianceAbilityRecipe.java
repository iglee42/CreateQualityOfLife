package fr.iglee42.createqualityoflife.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.chipped.common.registry.ModRecipeSerializers;
import fr.iglee42.createqualityoflife.registries.QOLItems;
import fr.iglee42.createqualityoflife.registries.QOLRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Stream;

public record ApplyShadowRadianceAbilityRecipe(Ingredient template,
                                               Ingredient base,
                                               Ingredient addition,
                                               DataComponentType<?> component) implements SmithingRecipe {

    @Override
    public boolean matches(@NotNull SmithingRecipeInput recipeInput, @NotNull Level level) {
        return this.isTemplateIngredient(recipeInput.template()) && this.isAdditionIngredient(recipeInput.addition()) && this.isBaseIngredient(recipeInput.base()) && (!recipeInput.base().has(component) || recipeInput.base().get(component) != Boolean.TRUE);
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull SmithingRecipeInput recipeInput, @NotNull HolderLookup.Provider provider) {
        ItemStack stack = recipeInput.base().copyWithCount(1);
        DataComponentType<Boolean> comp = (DataComponentType<Boolean>) component();
        stack.set(comp, Boolean.TRUE);
        return stack;
    }

    @NotNull
    @Override
    public ItemStack getResultItem(@NotNull HolderLookup.Provider provider) {
        ItemStack stack = new ItemStack(QOLItems.SHADOW_RADIANCE_CHESTPLATE.asItem());
        DataComponentType<Boolean> comp = (DataComponentType<Boolean>) component();
        stack.set(comp, Boolean.TRUE);
        return stack;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return QOLRecipeTypes.APPLY_SHADOW_RADIANCE_ABILITY.get();
    }


    @Override
    public boolean isTemplateIngredient(@NotNull ItemStack stack) {
        return this.template.test(stack);
    }

    @Override
    public boolean isBaseIngredient(@NotNull ItemStack stack) {
        return this.base.test(stack);
    }

    @Override
    public boolean isAdditionIngredient(@NotNull ItemStack stack) {
        return this.addition.test(stack);
    }

    @Override
    public boolean isIncomplete() {
        return Stream.of(this.template, this.addition).anyMatch(Ingredient::hasNoItems);
    }

    public static class Serializer implements RecipeSerializer<ApplyShadowRadianceAbilityRecipe> {
        private static final MapCodec<ApplyShadowRadianceAbilityRecipe> CODEC = RecordCodecBuilder.mapCodec(
                p_340782_ -> p_340782_.group(
                                Ingredient.CODEC.fieldOf("template").forGetter(p_301310_ -> p_301310_.template),
                                Ingredient.CODEC.fieldOf("base").forGetter(p_300938_ -> p_300938_.base),
                                Ingredient.CODEC.fieldOf("addition").forGetter(p_301153_ -> p_301153_.addition),
                                BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec().fieldOf("component").validate((type)->{
                                    if (Objects.equals(type.codec(), Codec.BOOL)) return DataResult.success(type);
                                    else return DataResult.error(()->"The 'component' field must be a data component that is a boolean");
                                }).forGetter(p_300935_ -> p_300935_.component)
                        )
                        .apply(p_340782_, ApplyShadowRadianceAbilityRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, ApplyShadowRadianceAbilityRecipe> STREAM_CODEC = StreamCodec.of(
                ApplyShadowRadianceAbilityRecipe.Serializer::toNetwork, ApplyShadowRadianceAbilityRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<ApplyShadowRadianceAbilityRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ApplyShadowRadianceAbilityRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static ApplyShadowRadianceAbilityRecipe fromNetwork(RegistryFriendlyByteBuf p_320375_) {
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(p_320375_);
            Ingredient ingredient1 = Ingredient.CONTENTS_STREAM_CODEC.decode(p_320375_);
            Ingredient ingredient2 = Ingredient.CONTENTS_STREAM_CODEC.decode(p_320375_);
            DataComponentType<?> componentType = ByteBufCodecs.registry(Registries.DATA_COMPONENT_TYPE).decode(p_320375_);
            return new ApplyShadowRadianceAbilityRecipe(ingredient, ingredient1, ingredient2, componentType);
        }

        private static void toNetwork(RegistryFriendlyByteBuf p_320743_, ApplyShadowRadianceAbilityRecipe p_319840_) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(p_320743_, p_319840_.template);
            Ingredient.CONTENTS_STREAM_CODEC.encode(p_320743_, p_319840_.base);
            Ingredient.CONTENTS_STREAM_CODEC.encode(p_320743_, p_319840_.addition);
            ByteBufCodecs.registry(Registries.DATA_COMPONENT_TYPE).encode(p_320743_, p_319840_.component);
        }
    }
}