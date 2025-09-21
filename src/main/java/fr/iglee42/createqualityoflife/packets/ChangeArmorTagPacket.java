package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class ChangeArmorTagPacket extends SimplePacketBase {


    private int slot,value;
    private String nbtKey;

    public ChangeArmorTagPacket(int slot, int value, String nbtKey) {
        this.slot = slot;
        this.value = value;
        this.nbtKey = nbtKey;
    }

    public ChangeArmorTagPacket(FriendlyByteBuf buf){
        this(buf.readInt(),buf.readInt(),buf.readUtf());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(slot);
        buffer.writeInt(value);
        buffer.writeUtf(nbtKey);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(()->{
            ServerPlayer player = context.getSender();
            if (player != null) {
                if (NBTConstants.NBT_RENDER_TYPE.equals(nbtKey) || NBTConstants.NBT_CHOOSABLE_EFFECTS.equals(nbtKey)){
                    player.getInventory().getItem(slot).getOrCreateTag().putInt(nbtKey,value);
                } else {
                    player.getInventory().getItem(slot).getOrCreateTag().putBoolean(nbtKey, value != 0);
                }
            }
        });
        return true;
    }
}
