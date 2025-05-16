package fr.iglee42.createqualityoflife.registries;

import com.mojang.authlib.GameProfile;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.statue.animation.StatueAnimation;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


import java.util.Optional;
import java.util.UUID;

public class ModEntityDataSerializers {


    public static final EntityDataSerializer<Optional<GameProfile>> PROFILE_ENTITY_DATA_SERIALIZER = EntityDataSerializer.optional((friendlyByteBuf, gameProfile) -> {
        if (!gameProfile.isComplete()) {
            UUID uuid = UUIDUtil.createOfflinePlayerUUID(gameProfile.getName());
            gameProfile = new GameProfile(uuid, gameProfile.getName());
        }
        friendlyByteBuf.writeGameProfile(gameProfile);
    },FriendlyByteBuf::readGameProfile);

    public static final EntityDataSerializer<Optional<StatueAnimation>> ANIMATION_DATA_SERIALIZER = EntityDataSerializer.optional(StatueAnimation::encode,StatueAnimation::decode);
}
