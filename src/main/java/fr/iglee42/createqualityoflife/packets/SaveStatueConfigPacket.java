package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class SaveStatueConfigPacket extends SimplePacketBase {

    private final int id;
    private final CompoundTag nbts;

    public SaveStatueConfigPacket(int id, CompoundTag nbts) {
        this.id = id;
        this.nbts = nbts;
    }

    public SaveStatueConfigPacket(FriendlyByteBuf buf){
        this.id = buf.readInt();
        this.nbts = buf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
        buffer.writeNbt(nbts);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player != null) {
            if (player.level().getEntity(id) != null){
                player.level().getEntity(id).load(nbts);
            }
        }
        return true;
    }
}
