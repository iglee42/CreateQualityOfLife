package fr.iglee42.createqualityoflife.utils;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

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
    public static final String NBT_BELT = "belt";
    public static final String NBT_PREFERRED_RENDER = "preferredRender";
    public static final String NBT_TOOLTIPS = "tooltips";
    public static final String NBT_REACH = "reach";
    public static final String NBT_STEP_HEIGHT = "step_height";
    public static final String NBT_VOID_WALK = "void_walk";
    public static final String NBT_CHOOSABLE_EFFECTS = "choosable_effects";
    public static final String NBT_BOOST_ON_LAUNCH = "boost_on_launch";
    public static final String NBT_DASH = "dash";
    public static final String NBT_DIGGING = "digging";
    public static final String NBT_VEIN_MINE = "vein_mine";
    public static final String NBT_TREE_DECAPITATION = "tree_decapitation";
    public static final String NBT_CASINGIFIER = "casingifier";
    public static final String NBT_SMELTING = "smelting";
    public static final String NBT_PLOUGHING = "ploughing";
    public static final String NBT_HARVESTING = "harvesting";
    public static final String NBT_COPIED_DATAS = "copied_datas";


    public static boolean getOrDefault(ItemStack stack,String nbt,boolean defaultValue){
        return stack.getOrCreateTag().contains(nbt) ? stack.getOrCreateTag().getBoolean(nbt) : defaultValue;
    }

    public static ArmorRenderType getOrDefault(ItemStack stack,String nbt){
        return stack.getOrCreateTag().contains(nbt) ? ArmorRenderType.BY_ID.apply(stack.getOrCreateTag().getInt(nbt)) : ArmorRenderType.ALL;
    }

    public static PreferredRender getOrDefault(String nbt, ItemStack stack){
        return stack.getOrCreateTag().contains(nbt) ? PreferredRender.BY_ID.apply(stack.getOrCreateTag().getInt(nbt)) : PreferredRender.BOTH;
    }

    public static ItemTooltips getTooltipOrDefault(ItemStack stack){
        if (stack == null || stack.isEmpty()) return ItemTooltips.DEFAULT;
        if (stack.getTag() == null) return ItemTooltips.DEFAULT;
        return stack.getTag().contains(NBT_TOOLTIPS) ? ItemTooltips.of(stack.getOrCreateTag().getCompound(NBT_TOOLTIPS)) : ItemTooltips.DEFAULT;
    }

    public static ShadowRadianceEffects getEffectsOrDefault(ItemStack stack,ShadowRadianceEffects defaultValue){
        return stack.getOrCreateTag().contains(NBT_CHOOSABLE_EFFECTS) ? ShadowRadianceEffects.values()[stack.getOrCreateTag().getInt(NBT_CHOOSABLE_EFFECTS)] : defaultValue;
    }
}
