package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import fr.iglee42.createqualityoflife.statue.animation.PublishedAnimationsManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;

public class SyncAnimationsConfigPacket extends SimplePacketBase {

    private final List<PublishedAnimationsManager.PublishedAnimation> animations;

    public SyncAnimationsConfigPacket(List<PublishedAnimationsManager.PublishedAnimation> animations) {
        this.animations = animations;
    }

    public SyncAnimationsConfigPacket(FriendlyByteBuf buf){
        this(PublishedAnimationsManager.PublishedAnimation.decodeList(buf));
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        PublishedAnimationsManager.PublishedAnimation.encodeList(buffer,animations);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(()->{
            PublishedAnimationsManager.CLIENT_ANIMATIONS = animations;
        });
        return true;
    }
}
