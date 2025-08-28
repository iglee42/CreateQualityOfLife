package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ChangeItemTooltipsPacket extends SimplePacketBase {

    private final int slot;
    private final boolean value;
    private final ItemTooltips.Tooltip tooltip;

    public ChangeItemTooltipsPacket(int slot, boolean value, ItemTooltips.Tooltip tooltip) {
        this.slot = slot;
        this.value = value;
        this.tooltip = tooltip;
    }

    public ChangeItemTooltipsPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt(),buffer.readBoolean(),buffer.readEnum(ItemTooltips.Tooltip.class));
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(slot);
        buffer.writeBoolean(value);
        buffer.writeEnum(tooltip);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        ItemTooltips.Mutable tooltips = new ItemTooltips.Mutable(NBTConstants.getTooltipOrDefault(context.getSender().getInventory().getItem(slot)));
        tooltips.set(tooltip,value);
        context.getSender().getInventory().getItem(slot).getOrCreateTag().put(NBTConstants.NBT_TOOLTIPS,tooltips.toImmutable().save());
        return true;
    }
}
