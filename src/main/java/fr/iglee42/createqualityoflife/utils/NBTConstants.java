package fr.iglee42.createqualityoflife.utils;

import net.minecraft.world.item.ItemStack;

public class NBTConstants {

    public static final String NBT_LINKED_PLAYER = "linkedPlayer";
    public static final String NBT_EFFECTS = "effects";
    public static final String NBT_GOGGLES = "goggles";
    public static final String NBT_PROPELLERS = "propellers";
    public static final String NBT_FANS = "fans";
    public static final String NBT_HOVER = "hover";
    public static final String NBT_LAVA = "lava";
    public static final String NBT_DIVING = "diving";
    public static final String NBT_ARMS = "arms";
    public static final String NBT_RENDER_TYPE = "renderType";
    public static final String NBT_ELYTRA = "elytra";
    public static final String NBT_ELYTRA_STATE = "elytraState";


    public static boolean getOrDefault(ItemStack stack,String nbt,boolean defaultValue){
        return stack.getOrCreateTag().contains(nbt) ? stack.getOrCreateTag().getBoolean(nbt) : defaultValue;
    }

    public static ArmorRenderType getOrDefault(ItemStack stack,String nbt){
        return stack.getOrCreateTag().contains(nbt) ? ArmorRenderType.BY_ID.apply(stack.getOrCreateTag().getInt(nbt)) : ArmorRenderType.ALL;
    }
}
