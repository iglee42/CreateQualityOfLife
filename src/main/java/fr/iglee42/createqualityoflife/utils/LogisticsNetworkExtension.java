package fr.iglee42.createqualityoflife.utils;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;

public interface LogisticsNetworkExtension {

    private LogisticsNetwork self() {
        return (LogisticsNetwork)this;
    }

    String createQOL$getName();
    void createQOL$setName(String name);

    NetworkDestructionLevel createQOL$getDestructionLevel();
    void createQOL$setDestructionLevel(NetworkDestructionLevel level);

    Map<UUID, NetworkPermission> createQOL$getPlayersPermission();
    void createQOL$setPlayersPermission(Map<UUID, NetworkPermission> permissions);
    default NetworkPermission createQOL$getPlayerPermission(UUID player){
        return createQOL$getPlayersPermission().getOrDefault(player,NetworkPermission.NONE);
    }
    default NetworkPermission createQOL$getPlayerPermission(Player player){
        return createQOL$getPlayerPermission(player.getUUID());
    }
    default boolean createQOL$hasPlayerPermission(Player player,NetworkPermission permission){
        return createQOL$hasPlayerPermission(player.getUUID(),permission);
    }
    default boolean createQOL$hasPlayerPermission(UUID player,NetworkPermission permission){
        return player.equals(self().owner) || createQOL$getPlayerPermission(player).ordinal() >= permission.ordinal();
    }
    default void createQOL$modifyPlayerPermission(Player player, NetworkPermission permission){
        createQOL$modifyPlayerPermission(player.getUUID(),permission);
    }
    default void createQOL$modifyPlayerPermission(UUID player, NetworkPermission permission){
        createQOL$getPlayersPermission().put(player,permission);
    }

}
