package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.items.armors.ShadowRadianceChestplate;
import fr.iglee42.createqualityoflife.registries.QOLItems;
import fr.iglee42.createqualityoflife.registries.QOLPackets;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ToggleFansPacket implements ServerboundPacketPayload {

    public static final ToggleFansPacket INSTANCE = new ToggleFansPacket();
    public static final StreamCodec<FriendlyByteBuf,ToggleFansPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);


    @Override
    public void handle(ServerPlayer player) {
            if (player != null) {
                ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
                Item backtank = BacktankItem.getWornBy(player);

                if (backtank == null) return;
                if (QOLItems.SHADOW_RADIANCE_CHESTPLATE.is(backtank) && ShadowRadianceChestplate.hasPropeller(chestplate)){
                    ShadowRadianceChestplate.toggleFans(chestplate,player);
                }else if (QOLItems.SHADOW_RADIANCE_CHESTPLATE.is(backtank) && !ShadowRadianceChestplate.hasPropeller(chestplate)){
                    player.sendSystemMessage(CreateQOLLang.translateDirect("chestplate.no_propeller").withStyle(ChatFormatting.RED),true);
                }
            }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return QOLPackets.TOGGLE_FANS;
    }
}
