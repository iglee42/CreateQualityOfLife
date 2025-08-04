package fr.iglee42.createqualityoflife.utils;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.foundation.mixin.accessor.ShapedRecipeAccessor;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class Utils {

    public static void saveStringToBuffer(FriendlyByteBuf buf , String s){
        buf.writeInt(s.length());
        for (char c : s.toCharArray()){
            buf.writeChar(c);
        }
    }

    public static String readStringFromBuffer(FriendlyByteBuf buf){
        int length = buf.readInt();
        char[] word = new char[length];
        for (int i = 0; i < length; i++){
            word[i] = buf.readChar();
        }
        return String.valueOf(word);
    }

    public static <T> void encodeComponent(CompoundTag compoundTag, DataComponentType<T> componentType, Object value, @Nullable HolderLookup.Provider provider) {
        Codec<T> codec = componentType.codec();
        if (codec != null) {
            try {
                codec.encodeStart(provider == null ? NbtOps.INSTANCE : RegistryOps.create(NbtOps.INSTANCE,provider), (T) value)
                        .result()
                        .ifPresent(nbtElement -> {
                            String key = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(componentType).toString();
                            compoundTag.put(key, nbtElement);
                        });
            } catch (ClassCastException ignored) {
            }
        }
    }

    public static <T> void applyComponent(ItemStack stack, CompoundTag compoundTag, @Nullable DataComponentType<T> componentType, @Nullable HolderLookup.Provider provider) {
        if (componentType == null ) return;
        Codec<T> codec = componentType.codec();
        if (codec != null) {
            String key = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(componentType).toString();
            if (compoundTag.contains(key)) {
                Tag nbtElement = compoundTag.get(key);
                codec.parse(provider == null ? NbtOps.INSTANCE : RegistryOps.create(NbtOps.INSTANCE,provider), nbtElement)
                        .result()
                        .ifPresent(value -> stack.set(componentType, value));
            }
        }
    }

    public static MechanicalCraftingRecipe mechanicalCraftingRecipeFromShapedAndComponents(MechanicalCraftingRecipe recipe, List<ResourceLocation> components, int slotToCopy) {
        ((CopyComponentsExtension)recipe).createQOL$setCopiedComponents(components);
        ((CopyComponentsExtension)recipe).createQOL$setCopiedSlot(slotToCopy);
        return recipe;
    }
}
