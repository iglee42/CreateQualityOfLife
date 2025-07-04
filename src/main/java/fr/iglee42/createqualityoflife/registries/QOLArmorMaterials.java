package fr.iglee42.createqualityoflife.registries;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

import com.simibubi.create.AllItems;
import fr.iglee42.createqualityoflife.CreateQOL;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class QOLArmorMaterials {

    public static final Holder<ArmorMaterial> SHADOW_RADIANCE = register(
            CreateQOL.asResource("shadow_radiance"),
            Util.make(new EnumMap<>(ArmorItem.Type.class), p_323384_ -> {
                p_323384_.put(ArmorItem.Type.BOOTS, 5);
                p_323384_.put(ArmorItem.Type.LEGGINGS, 8);
                p_323384_.put(ArmorItem.Type.CHESTPLATE, 10);
                p_323384_.put(ArmorItem.Type.HELMET, 5);
                p_323384_.put(ArmorItem.Type.BODY, 10);
            }),
            25,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            4.0F,
            0.2F,
            () -> Ingredient.of(QOLItems.SHADOW_RADIANCE),
            List.of(
                    new ArmorMaterial.Layer(CreateQOL.asResource("shadow_radiance"))
            )
    );

    public static final Holder<ArmorMaterial> SHADOW_STEEL = register(
            CreateQOL.asResource("shadow_steel"),
            Util.make(new EnumMap<>(ArmorItem.Type.class), p_323384_ -> {
                p_323384_.put(ArmorItem.Type.BOOTS, 4);
                p_323384_.put(ArmorItem.Type.LEGGINGS, 7);
                p_323384_.put(ArmorItem.Type.CHESTPLATE, 9);
                p_323384_.put(ArmorItem.Type.HELMET, 4);
                p_323384_.put(ArmorItem.Type.BODY, 9);
            }),
            20,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            4.0F,
            0.2F,
            () -> Ingredient.of(AllItems.SHADOW_STEEL),
            List.of(
                    new ArmorMaterial.Layer(CreateQOL.asResource("shadow_steel"))
            )
    );

    public static final Holder<ArmorMaterial> REFINED_RADIANCE = register(
            CreateQOL.asResource("refined_radiance"),
            Util.make(new EnumMap<>(ArmorItem.Type.class), p_323384_ -> {
                p_323384_.put(ArmorItem.Type.BOOTS, 4);
                p_323384_.put(ArmorItem.Type.LEGGINGS, 7);
                p_323384_.put(ArmorItem.Type.CHESTPLATE, 9);
                p_323384_.put(ArmorItem.Type.HELMET, 4);
                p_323384_.put(ArmorItem.Type.BODY, 9);
            }),
            20,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            4.0F,
            0.2F,
            () -> Ingredient.of(AllItems.REFINED_RADIANCE),
            List.of(
                    new ArmorMaterial.Layer(CreateQOL.asResource("refined_radiance"))
            )
    );

    private static Holder<ArmorMaterial> register(
            ResourceLocation p_323865_,
            EnumMap<ArmorItem.Type, Integer> p_324599_,
            int p_324319_,
            Holder<SoundEvent> p_324145_,
            float p_323494_,
            float p_324549_,
            Supplier<Ingredient> p_323845_,
            List<ArmorMaterial.Layer> p_323990_
    ) {
        EnumMap<ArmorItem.Type, Integer> enummap = new EnumMap<>(ArmorItem.Type.class);

        for (ArmorItem.Type armoritem$type : ArmorItem.Type.values()) {
            enummap.put(armoritem$type, p_324599_.get(armoritem$type));
        }

        return Registry.registerForHolder(
                BuiltInRegistries.ARMOR_MATERIAL,
                p_323865_,
                new ArmorMaterial(enummap, p_324319_, p_324145_, p_323845_, p_323990_, p_323494_, p_324549_)
        );
    }
}
