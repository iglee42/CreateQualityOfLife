package fr.iglee42.createqualityoflife.packets;

import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.registries.QOLPackets;
import fr.iglee42.createqualityoflife.utils.CommonKeysHandler;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public record ToggleArmorEffectPacket(EquipmentSlot slot) implements ServerboundPacketPayload {
    public static final StreamCodec<ByteBuf, ToggleArmorEffectPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, p->(byte)p.slot().ordinal(),
            ToggleArmorEffectPacket::new
    );

    public ToggleArmorEffectPacket(byte slot){
        this(EquipmentSlot.values()[slot]);
    }

    @Override
    public void handle(ServerPlayer player) {
        if (player != null) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) return;
            if (!(stack.getItem() instanceof QOLConfigurableItem it)) return;
            if (it.providedEffect(stack) == null) return;
            boolean enable = !stack.getOrDefault(QOLDataComponents.ARMOR_EFFECT,true);
            stack.set(QOLDataComponents.ARMOR_EFFECT,enable);
            player.displayClientMessage(Component.translatable("createqol.ability.armor.effect_toggle_message",
                                    Component.translatable(stack.getDescriptionId()))
                    .withStyle(enable ? ChatFormatting.GREEN : ChatFormatting.RED)
                    .append(QOLConfigurableItem.chooseState(true ,true, enable, false, true)),
                    true);
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return QOLPackets.TOGGLE_ARMOR_EFFECT;
    }
}
