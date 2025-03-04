package fr.iglee42.createqualityoflife.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Rarity;

import java.util.Arrays;
import java.util.function.IntFunction;

public enum ArmorRenderType implements StringRepresentable{

    ALL(true,true),
    ARMOR_ONLY(true,false,ArmorItem.Type.HELMET,ArmorItem.Type.CHESTPLATE),
    ADDITION_ONLY(false,true,ArmorItem.Type.HELMET,ArmorItem.Type.CHESTPLATE),
    NONE(false,false);


    public static final Codec<ArmorRenderType> CODEC = StringRepresentable.fromValues(ArmorRenderType::values);
    public static final IntFunction<ArmorRenderType> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, ArmorRenderType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);


    private final ArmorItem.Type[] allowedTypes;
    private final boolean renderArmor,renderAddition;

    ArmorRenderType(boolean renderArmor, boolean renderAddition,ArmorItem.Type... allowedTypes) {
        this.allowedTypes = allowedTypes;
        this.renderArmor = renderArmor;
        this.renderAddition = renderAddition;
    }

    ArmorRenderType(boolean renderArmor, boolean renderAddition){
        this(renderArmor,renderAddition,ArmorItem.Type.values());
    }

    public boolean canBeSelected(ArmorItem item){
        return Arrays.stream(allowedTypes).anyMatch(t->item.getType().equals(t));
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
