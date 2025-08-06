package fr.iglee42.createqualityoflife.packets;

import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.registries.QOLPackets;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import fr.iglee42.createqualityoflife.utils.PreferredRender;
import fr.iglee42.createqualityoflife.utils.ShadowRadianceEffects;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ChangeItemTooltipsPacket implements ServerboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeItemTooltipsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, p->p.slot,
            ByteBufCodecs.BOOL, p->p.value,
            ItemTooltips.Tooltip.STREAM_CODEC, p->p.tooltip,
            ChangeItemTooltipsPacket::new
    );

    private int slot;
    private boolean value;
    private ItemTooltips.Tooltip tooltip;

    public ChangeItemTooltipsPacket(int slot, boolean value, ItemTooltips.Tooltip tooltip) {
        this.slot = slot;
        this.value = value;
        this.tooltip = tooltip;
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return QOLPackets.CHANGE_TOOLTIP;
    }

    @Override
    public void handle(ServerPlayer player) {
        ItemTooltips.Mutable tooltips = new ItemTooltips.Mutable(player.getInventory().getItem(slot).getOrDefault(QOLDataComponents.ITEM_TOOLTIPS,ItemTooltips.DEFAULT));
        tooltips.set(tooltip,value);
        player.getInventory().getItem(slot).set(QOLDataComponents.ITEM_TOOLTIPS,tooltips.toImmutable());
    }
}
