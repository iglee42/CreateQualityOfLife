package fr.iglee42.createqualityoflife.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.simibubi.create.Create;
import fr.iglee42.createqualityoflife.CreateQOL;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.IntFunction;

public enum NetworkPermission implements StringRepresentable{

    NONE(),
    MEMBER(),
    ADMIN(),
    OWNER();




    public static final IntFunction<NetworkPermission> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, NetworkPermission> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);
    public static final Codec<NetworkPermission> CODEC = Codec.INT.flatXmap(
            id -> {
                NetworkPermission p = BY_ID.apply(id);
                if (p == null) {
                    return DataResult.error(()->"Unknown NetworkPermission id: " + id);
                }
                return DataResult.success(p);
            },
            perm -> DataResult.success(perm.ordinal())
    );

    public NetworkPermission next(){
        int nextOrdinal = (this.ordinal() + 1) % values().length;
        return values()[nextOrdinal];
    }

    public NetworkPermission previous() {
        int prevOrdinal = (this.ordinal() - 1 + values().length) % values().length;
        return values()[prevOrdinal];
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}
