package fr.iglee42.createqualityoflife.packets;

import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.registries.QOLPackets;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
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

public class ChangeItemComponentPacket implements ServerboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeItemComponentPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, p->p.slot,
            ByteBufCodecs.INT, p->p.value,
            ByteBufCodecs.STRING_UTF8,p->p.componentId,
            ChangeItemComponentPacket::new
    );

    private int slot,value;
    private String componentId;

    public ChangeItemComponentPacket(int slot, int value, String componentId) {
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
        if (component.equals(QOLDataComponents.ARMOR_RENDER_TYPE)){
          player.getInventory().getItem(slot).set(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.BY_ID.apply(value));
        } else if (component.equals(QOLDataComponents.PREFERRED_RENDER)){
          player.getInventory().getItem(slot).set(QOLDataComponents.PREFERRED_RENDER, PreferredRender.BY_ID.apply(value));
        } else if (component.equals(QOLDataComponents.EFFECT)){
          player.getInventory().getItem(slot).set(QOLDataComponents.EFFECT, ShadowRadianceEffects.BY_ID.apply(value));
        } else {
            DataComponentType<Boolean> bComponent = (DataComponentType<Boolean>) component;
            player.getInventory().getItem(slot).set(bComponent, value != 0);
        }
    }
}
