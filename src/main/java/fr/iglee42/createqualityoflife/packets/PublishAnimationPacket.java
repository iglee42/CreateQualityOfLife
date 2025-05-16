package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import fr.iglee42.createqualityoflife.statue.animation.PublishedAnimationsManager;
import fr.iglee42.createqualityoflife.statue.animation.StatueAnimation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class PublishAnimationPacket extends SimplePacketBase {

    private final UUID publisher;
    private final String name;
    private final StatueAnimation animation;

    public PublishAnimationPacket(UUID publisher, String name, StatueAnimation animation) {
        this.publisher = publisher;
        this.name = name;
        this.animation = animation;
    }

    public PublishAnimationPacket(FriendlyByteBuf buf){
        this(buf.readUUID(),buf.readUtf(),StatueAnimation.decode(buf));
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(publisher);
        buffer.writeUtf(name);
        StatueAnimation.encode(buffer,animation);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(()->{
            if (context.getSender() != null){
                PublishedAnimationsManager manager = PublishedAnimationsManager.get(context.getSender().level());
                manager.publishAnimation(publisher,name,animation);
            }
        });
        return true;
    }
}
