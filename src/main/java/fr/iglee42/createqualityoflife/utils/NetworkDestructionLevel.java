package fr.iglee42.createqualityoflife.utils;

import com.mojang.serialization.Codec;
import com.simibubi.create.Create;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.IntFunction;

public enum NetworkDestructionLevel implements StringRepresentable {

    ALL((network,player)->true),
    MEMBERS(Create.LOGISTICS::mayInteract),
    ADMINS(Create.LOGISTICS::mayAdministrate)
    ;

    public static final Codec<NetworkDestructionLevel> CODEC = StringRepresentable.fromValues(NetworkDestructionLevel::values);
    public static final IntFunction<NetworkDestructionLevel> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, NetworkDestructionLevel> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);

    private final BiPredicate<UUID, Player> canDestroy;

    NetworkDestructionLevel(BiPredicate<UUID, Player> canDestroy) {
        this.canDestroy = canDestroy;
    }

    public NetworkDestructionLevel next(){
        int nextOrdinal = (this.ordinal() + 1) % values().length;
        return values()[nextOrdinal];
    }

    public boolean canDestroy(UUID networkId, Player player){
        return canDestroy.test(networkId,player);
    }

    public Component getName() {
        return CreateQOLLang.translate("gui.stock_manager.destruction_level."+getSerializedName()).component();
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}
