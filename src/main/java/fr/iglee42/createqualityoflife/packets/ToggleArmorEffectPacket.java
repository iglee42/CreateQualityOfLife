package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class ToggleArmorEffectPacket extends SimplePacketBase {
    private final EquipmentSlot slot;

    public ToggleArmorEffectPacket(EquipmentSlot slot){
        this.slot = slot;
    }

    public ToggleArmorEffectPacket(FriendlyByteBuf buf){
        slot = buf.readEnum(EquipmentSlot.class);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeEnum(slot);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player != null) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) return true;
            if (!(stack.getItem() instanceof QOLConfigurableItem it)) return true;
            if (it.providedEffect(stack) == null) return true;
            boolean enable = !NBTConstants.getOrDefault(stack,NBTConstants.NBT_EFFECTS,true);
            stack.getOrCreateTag().putBoolean(NBTConstants.NBT_EFFECTS,enable);
            player.displayClientMessage(Component.translatable(stack.getDescriptionId()).append(Component.literal( " Effect : ").append(QOLConfigurableItem.chooseState(true,true,enable,false,true))).withStyle(enable ? ChatFormatting.GREEN : ChatFormatting.RED), true);
        }
        return true;
    }
}
