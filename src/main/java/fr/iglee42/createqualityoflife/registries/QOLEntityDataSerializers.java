package fr.iglee42.createqualityoflife.registries;

import com.mojang.authlib.GameProfile;
import fr.iglee42.createqualityoflife.statue.animation.StatueAnimation;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;


import java.util.Optional;
import java.util.UUID;

public class QOLEntityDataSerializers {


    public static final EntityDataSerializer<Optional<GameProfile>> PROFILE_ENTITY_DATA_SERIALIZER = EntityDataSerializer.optional((friendlyByteBuf, gameProfile) -> {
        if (!gameProfile.isComplete()) {
            UUID uuid = UUIDUtil.createOfflinePlayerUUID(gameProfile.getName());
            gameProfile = new GameProfile(uuid, gameProfile.getName());
        }
        friendlyByteBuf.writeGameProfile(gameProfile);
    },FriendlyByteBuf::readGameProfile);

    public static final EntityDataSerializer<Optional<StatueAnimation>> ANIMATION_DATA_SERIALIZER = EntityDataSerializer.optional(StatueAnimation::encode,StatueAnimation::decode);
}
