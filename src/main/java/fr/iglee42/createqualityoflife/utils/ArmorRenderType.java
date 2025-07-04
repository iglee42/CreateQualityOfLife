package fr.iglee42.createqualityoflife.utils;

import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ArmorItem;

import java.util.Arrays;
import java.util.function.IntFunction;

public enum ArmorRenderType implements StringRepresentable{

    ALL(true,true),
    ARMOR_ONLY(true,false),
    ADDITION_ONLY(false,true),
    NONE(false,false);


    public static final IntFunction<ArmorRenderType> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    private final boolean renderArmor,renderAddition;

    ArmorRenderType(boolean renderArmor, boolean renderAddition) {
        this.renderArmor = renderArmor;
        this.renderAddition = renderAddition;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }

    public boolean shouldRenderArmor() {
        return renderArmor;
    }

    public boolean shouldRenderAddition() {
        return renderAddition;
    }
}
