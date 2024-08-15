package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import fr.iglee42.createqualityoflife.items.ShadowRadianceChestplate;
import fr.iglee42.createqualityoflife.registries.ModItems;
import fr.iglee42.createqualityoflife.utils.CommonKeysHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class UpdateInputsPacket extends SimplePacketBase {

    private final boolean up;
    private final boolean down;
    private final boolean forwards;
    private final boolean backwards;
    private final boolean left;
    private final boolean right;


    public UpdateInputsPacket(boolean up, boolean down, boolean forwards, boolean backwards, boolean left, boolean right) {
        this.up = up;
        this.down = down;
        this.forwards = forwards;
        this.backwards = backwards;
        this.left = left;
        this.right = right;
    }

    public UpdateInputsPacket(FriendlyByteBuf buffer) {
        this(buffer.readBoolean(),buffer.readBoolean(),buffer.readBoolean(),buffer.readBoolean(),buffer.readBoolean(),buffer.readBoolean());
    }


    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(up);
        buffer.writeBoolean(down);
        buffer.writeBoolean(forwards);
        buffer.writeBoolean(backwards);
        buffer.writeBoolean(left);
        buffer.writeBoolean(right);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(()->{
            ServerPlayer player = context.getSender();
            if (player != null) {
                CommonKeysHandler.update(player,up,down,forwards,backwards,left,right);
            }
        });
        return true;
    }
}
