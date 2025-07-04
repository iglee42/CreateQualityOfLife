package fr.iglee42.createqualityoflife.utils;

import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ArmorItem;

import java.util.Arrays;
import java.util.function.IntFunction;

public enum PreferredRender implements StringRepresentable{

    BOTH(true,true),
    BACKTANK(true,false),
    ELYTRA(false,true);


    public static final IntFunction<PreferredRender> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);


    private final boolean renderBacktank, renderElytra;

    PreferredRender(boolean renderBacktank, boolean renderElytra) {
        this.renderBacktank = renderBacktank;
        this.renderElytra = renderElytra;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }

    public boolean shouldRenderBacktank() {
        return renderBacktank;
    }

    public boolean shouldRenderElytra() {
        return renderElytra;
    }
}
