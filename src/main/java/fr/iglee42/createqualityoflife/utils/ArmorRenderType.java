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
    ARMOR_ONLY(true,false),
    ADDITION_ONLY(false,true),
    NONE(false,false);


    public static final Codec<ArmorRenderType> CODEC = StringRepresentable.fromValues(ArmorRenderType::values);
    public static final IntFunction<ArmorRenderType> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, ArmorRenderType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);

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
