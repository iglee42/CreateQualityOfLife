package fr.iglee42.createqualityoflife.registries;

import fr.iglee42.createqualityoflife.CreateQOL;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;

public class ModEntityDataSerializers {

    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, CreateQOL.MODID);

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Optional<ResolvableProfile>>> RESOLVABLE_PROFILE_ENTITY_DATA_SERIALIZER = ENTITY_SERIALIZERS.register("profile",()->EntityDataSerializer.forValueType(ResolvableProfile.STREAM_CODEC.apply(ByteBufCodecs::optional)));
}
