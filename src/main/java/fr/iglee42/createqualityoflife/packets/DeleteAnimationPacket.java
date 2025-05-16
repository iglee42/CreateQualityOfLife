package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import fr.iglee42.createqualityoflife.statue.animation.PublishedAnimationsManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class DeleteAnimationPacket extends SimplePacketBase {

    public final UUID animation;

    public DeleteAnimationPacket(UUID animation) {
        this.animation = animation;
    }

    public DeleteAnimationPacket(FriendlyByteBuf buf){
        this(buf.readUUID());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(animation);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(()->{
            if (context.getSender() != null){
                PublishedAnimationsManager manager = PublishedAnimationsManager.get(context.getSender().level());
                manager.deleteAnimation(animation);
            }
        });
        return true;
    }
}
