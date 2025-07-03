package fr.iglee42.createqualityoflife.packets;

import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.registries.QOLPackets;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import fr.iglee42.createqualityoflife.utils.PreferredRender;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ChangeArmorComponentPacket implements ServerboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeArmorComponentPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, p->p.slot,
            ByteBufCodecs.INT, p->p.value,
            ByteBufCodecs.STRING_UTF8,p->p.componentId,
            ChangeArmorComponentPacket::new
    );

    private int slot,value;
    private String componentId;

    public ChangeArmorComponentPacket(int slot, int value, String componentId) {
        this.slot = slot;
        this.value = value;
        this.componentId = componentId;
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return QOLPackets.CHANGE_ARMOR_COMPONENT;
    }

    @Override
    public void handle(ServerPlayer player) {
        DataComponentType<?> component = BuiltInRegistries.DATA_COMPONENT_TYPE.get(ResourceLocation.parse(componentId));
        if (component == null)return;
        if (component.equals(QOLDataComponents.ARMOR_EFFECT)){
          player.getInventory().getArmor(slot).set(QOLDataComponents.ARMOR_EFFECT, value != 0);
        } else if (component.equals(QOLDataComponents.BACKTANK_FANS)){
            player.getInventory().getArmor(slot).set(QOLDataComponents.BACKTANK_FANS, value != 0);
        } else if (component.equals(QOLDataComponents.BACKTANK_HOVER)){
            player.getInventory().getArmor(slot).set(QOLDataComponents.BACKTANK_HOVER, value != 0);
        } else if (component.equals(QOLDataComponents.BACKTANK_ELYTRA_STATE)){
            player.getInventory().getArmor(slot).set(QOLDataComponents.BACKTANK_ELYTRA_STATE, value != 0);
        } else if (component.equals(QOLDataComponents.BOOTS_BELT)){
            player.getInventory().getArmor(slot).set(QOLDataComponents.BOOTS_BELT, value != 0);
        } else if (component.equals(QOLDataComponents.BOOTS_DIVING)){
            player.getInventory().getArmor(slot).set(QOLDataComponents.BOOTS_DIVING, value != 0);
        } else if (component.equals(QOLDataComponents.BOOTS_LAVA)){
            player.getInventory().getArmor(slot).set(QOLDataComponents.BOOTS_LAVA, value != 0);
        } else if (component.equals(QOLDataComponents.HELMET_GOGGLES)){
            player.getInventory().getArmor(slot).set(QOLDataComponents.HELMET_GOGGLES, value != 0);
        } else if (component.equals(QOLDataComponents.ARMOR_RENDER_TYPE)){
          player.getInventory().getArmor(slot).set(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.BY_ID.apply(value));
        } else if (component.equals(QOLDataComponents.PREFERRED_RENDER)){
          player.getInventory().getArmor(slot).set(QOLDataComponents.PREFERRED_RENDER, PreferredRender.BY_ID.apply(value));
        }
    }
}
