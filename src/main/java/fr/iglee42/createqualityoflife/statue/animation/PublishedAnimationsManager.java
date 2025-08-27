package fr.iglee42.createqualityoflife.statue.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.packets.SyncAnimationsConfigPacket;
import fr.iglee42.createqualityoflife.registries.QOLPackets;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PublishedAnimationsManager extends SavedData {




    public static List<PublishedAnimation> CLIENT_ANIMATIONS = new ArrayList<>();

    private final List<PublishedAnimation> animations = new ArrayList<>();


    public PublishedAnimationsManager() {
    }

    public void loadFromBuffer(FriendlyByteBuf buf) {
        this.animations.clear();
        this.animations.addAll(PublishedAnimation.decodeList(buf));
    }

    public void writeToBuffer(FriendlyByteBuf buf) {
        PublishedAnimation.encodeList(buf, this.animations);
    }


    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        animations.forEach(a->{
            list.add(PublishedAnimation.CODEC.encodeStart(NbtOps.INSTANCE,a).getOrThrow(false,(s)->{}));
        });
        tag.put("animations",list);
        return tag;
    }

    public PublishedAnimationsManager(CompoundTag tag) {
        ListTag list = tag.getList("animations", Tag.TAG_COMPOUND);
        list.stream().map(CompoundTag.class::cast).forEach(ct->{
            animations.add(PublishedAnimation.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE,ct)).getOrThrow(false,(s)->{}));
        });
        setDirty();
    }


    @Nonnull
    public static PublishedAnimationsManager get(Level level) {
        if (level.isClientSide) {
            throw new RuntimeException("You can't access to client side!");
        }
        DimensionDataStorage storage = ((ServerLevel)level).getDataStorage();
        return storage.computeIfAbsent(PublishedAnimationsManager::new, PublishedAnimationsManager::new, CreateQOL.MODID + "_published_animations");
    }

    public void publishAnimation(UUID publisher,String name,StatueAnimation animation){
        animations.add(new PublishedAnimation(publisher,UUID.randomUUID(),name,animation));
        setDirty();
    }



    public void tick(ServerLevel level) {
        if (isDirty()) {
            level.players().forEach(sp->{
                QOLPackets.sendToPlayer(sp,new SyncAnimationsConfigPacket(animations));
            });
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

        public static void encode(FriendlyByteBuf buf, PublishedAnimation anim) {
            buf.writeUUID(anim.publisher);
            buf.writeUUID(anim.id);
            buf.writeUtf(anim.name);
            StatueAnimation.encode(buf, anim.animation);
        }

        public static PublishedAnimation decode(FriendlyByteBuf buf) {
            UUID publisher = buf.readUUID();
            UUID id = buf.readUUID();
            String name = buf.readUtf();
            StatueAnimation animation = StatueAnimation.decode(buf);
            return new PublishedAnimation(publisher, id, name, animation);
        }

        public static void encodeList(FriendlyByteBuf buf, List<PublishedAnimation> list) {
            buf.writeInt(list.size());
            for (PublishedAnimation anim : list) {
                encode(buf, anim);
            }
        }

        public static List<PublishedAnimation> decodeList(FriendlyByteBuf buf) {
            int size = buf.readInt();
            List<PublishedAnimation> list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                list.add(decode(buf));
            }
            return list;
        }
    }

}