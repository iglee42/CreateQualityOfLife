package fr.iglee42.createqualityoflife.utils;

import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ArmorItem;

import java.util.Arrays;
import java.util.function.IntFunction;

public enum PreferredRender implements StringRepresentable{

    BOTH(true,true),
    BACKTANK(true,false,ArmorItem.Type.CHESTPLATE),
    ELYTRA(false,true,ArmorItem.Type.CHESTPLATE);


    public static final IntFunction<PreferredRender> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);


    private final ArmorItem.Type[] allowedTypes;
    private final boolean renderBacktank, renderElytra;

    PreferredRender(boolean renderBacktank, boolean renderElytra, ArmorItem.Type... allowedTypes) {
        this.allowedTypes = allowedTypes;
        this.renderBacktank = renderBacktank;
        this.renderElytra = renderElytra;
    }

    PreferredRender(boolean renderBacktank, boolean renderElytra){
        this(renderBacktank, renderElytra,ArmorItem.Type.values());
    }

    public boolean canBeSelected(ArmorItem item){
        return Arrays.stream(allowedTypes).anyMatch(t->item.getType().equals(t));
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
