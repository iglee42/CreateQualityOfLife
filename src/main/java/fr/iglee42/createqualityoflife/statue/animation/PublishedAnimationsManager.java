package fr.iglee42.createqualityoflife.statue.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.packets.SyncAnimationsConfigPacket;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.SerializationException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;

public class PublishedAnimationsManager extends SavedData {

    public static final StreamCodec<FriendlyByteBuf, List<PublishedAnimation>> STREAM_CODEC =
            StreamCodec.of(
                    (buf, list) -> {
                        buf.writeInt(list.size());
                        for (PublishedAnimation anim : list) {
                            PublishedAnimation.STREAM_CODEC.encode(buf, anim);
                        }
                    },
                    buf -> {
                        int size = buf.readInt();
                        List<PublishedAnimation> list = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            list.add(PublishedAnimation.STREAM_CODEC.decode(buf));
                        }
                        return list;
                    }
            );

    @OnlyIn(Dist.CLIENT)
    public static List<PublishedAnimation> CLIENT_ANIMATIONS = new ArrayList<>();

    private final List<PublishedAnimation> animations = new ArrayList<>();


    public PublishedAnimationsManager() {
    }

    public PublishedAnimationsManager(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag list = tag.getList("animations", Tag.TAG_COMPOUND);
        list.stream().map(CompoundTag.class::cast).forEach(ct->{
            animations.add(PublishedAnimation.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE,ct)).getOrThrow());
        });
        setDirty();
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag list = new ListTag();
        animations.forEach(a->{
            list.add(PublishedAnimation.CODEC.encodeStart(NbtOps.INSTANCE,a).getOrThrow());
        });
        tag.put("animations",list);
        return tag;
    }


    @Nonnull
    public static PublishedAnimationsManager get(Level level) {
        if (level.isClientSide) {
            throw new RuntimeException("You can't access to client side!");
        }
        DimensionDataStorage storage = ((ServerLevel)level).getDataStorage();
        return storage.computeIfAbsent(new Factory<>(PublishedAnimationsManager::new, PublishedAnimationsManager::new, DataFixTypes.LEVEL), CreateQOL.MODID + "_published_animations");
    }

    public void publishAnimation(UUID publisher,String name,StatueAnimation animation){
        animations.add(new PublishedAnimation(publisher,UUID.randomUUID(),name,animation));
        setDirty();
    }



    public void tick() {
        if (isDirty()) {
            CatnipServices.NETWORK.sendToAllClients(new SyncAnimationsConfigPacket(animations));
        }
    }

    public void deleteAnimation(UUID animation) {
        animations.removeIf(a->a.id().equals(animation));
        setDirty();
    }

    public record PublishedAnimation(UUID publisher,UUID id, String name,StatueAnimation animation){

        public static final Codec<PublishedAnimation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("publisher").forGetter(PublishedAnimation::publisher),
                UUIDUtil.CODEC.fieldOf("id").forGetter(PublishedAnimation::id),
                Codec.STRING.fieldOf("name").forGetter(PublishedAnimation::name),
                StatueAnimation.CODEC.fieldOf("animation").forGetter(PublishedAnimation::animation)
        ).apply(instance, PublishedAnimation::new));

        public static final StreamCodec<FriendlyByteBuf, PublishedAnimation> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, PublishedAnimation::publisher,
                UUIDUtil.STREAM_CODEC, PublishedAnimation::id,
                ByteBufCodecs.STRING_UTF8, PublishedAnimation::name,
                StatueAnimation.STREAM_CODEC, PublishedAnimation::animation,
                PublishedAnimation::new
        );
    }

}