package fr.iglee42.createqualityoflife.utils;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public enum ShadowRadianceEffects implements StringRepresentable{

    NIGHT_VISION(MobEffects.NIGHT_VISION, ArmorItem.Type.HELMET),
    INVISIBILITY(MobEffects.INVISIBILITY, ArmorItem.Type.HELMET),
    STRENGTH(MobEffects.DAMAGE_BOOST, ArmorItem.Type.CHESTPLATE),
    REGENERATION(MobEffects.REGENERATION, ArmorItem.Type.CHESTPLATE),
    SATURATION(MobEffects.SATURATION, ArmorItem.Type.LEGGINGS),
    SPEED(MobEffects.MOVEMENT_SPEED, ArmorItem.Type.LEGGINGS),
    JUMP_BOOST(MobEffects.JUMP, ArmorItem.Type.BOOTS),
    HASTE(MobEffects.DIG_SPEED, ArmorItem.Type.BOOTS);


    private final MobEffect effectHolder;
    private final ArmorItem.Type type;

    ShadowRadianceEffects(MobEffect effectHolder, ArmorItem.Type type) {
        this.effectHolder = effectHolder;
        this.type = type;
    }

    public MobEffect getEffectHolder() {
        return effectHolder;
    }

    public ArmorItem.Type getType() {
        return type;
    }

    public boolean isValidForItem(ItemStack stack){
        if (!(stack.getItem() instanceof ArmorItem it)) return false;
        return it.getType().equals(getType());
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}
