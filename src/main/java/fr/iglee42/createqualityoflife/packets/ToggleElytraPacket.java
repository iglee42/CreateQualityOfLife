package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import fr.iglee42.createqualityoflife.items.ShadowRadianceChestplate;
import fr.iglee42.createqualityoflife.registries.ModItems;
import fr.iglee42.createqualityoflife.registries.ModPackets;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ToggleElytraPacket implements ServerboundPacketPayload {

    public static final ToggleElytraPacket INSTANCE = new ToggleElytraPacket();
    public static final StreamCodec<FriendlyByteBuf, ToggleElytraPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public void handle(ServerPlayer player) {
        if (player != null) {
            ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
            Item backtank = BacktankItem.getWornBy(player);

            if (backtank == null) return;
            if (ModItems.SHADOW_RADIANCE_CHESTPLATE.is(backtank) && ShadowRadianceChestplate.hasElytra(chestplate)){
                ShadowRadianceChestplate.toggleElytra(chestplate,player);
            }
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return ModPackets.TOGGLE_ELYTRA;
    }
}
