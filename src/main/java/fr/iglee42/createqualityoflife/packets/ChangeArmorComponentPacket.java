package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import fr.iglee42.createqualityoflife.registries.ModDataComponents;
import fr.iglee42.createqualityoflife.registries.ModPackets;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import fr.iglee42.createqualityoflife.utils.PreferredRender;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;

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
        return ModPackets.CHANGE_ARMOR_COMPONENT;
    }

    @Override
    public void handle(ServerPlayer player) {
        DataComponentType<?> component = BuiltInRegistries.DATA_COMPONENT_TYPE.get(ResourceLocation.parse(componentId));
        if (component == null)return;
        if (component.equals(ModDataComponents.ARMOR_EFFECT)){
          player.getInventory().getArmor(slot).set(ModDataComponents.ARMOR_EFFECT, value != 0);
        } else if (component.equals(ModDataComponents.BACKTANK_FANS)){
            player.getInventory().getArmor(slot).set(ModDataComponents.BACKTANK_FANS, value != 0);
        } else if (component.equals(ModDataComponents.BACKTANK_HOVER)){
            player.getInventory().getArmor(slot).set(ModDataComponents.BACKTANK_HOVER, value != 0);
        } else if (component.equals(ModDataComponents.BACKTANK_ELYTRA_STATE)){
            player.getInventory().getArmor(slot).set(ModDataComponents.BACKTANK_ELYTRA_STATE, value != 0);
        } else if (component.equals(ModDataComponents.BOOTS_BELT)){
            player.getInventory().getArmor(slot).set(ModDataComponents.BOOTS_BELT, value != 0);
        } else if (component.equals(ModDataComponents.BOOTS_DIVING)){
            player.getInventory().getArmor(slot).set(ModDataComponents.BOOTS_DIVING, value != 0);
        } else if (component.equals(ModDataComponents.BOOTS_LAVA)){
            player.getInventory().getArmor(slot).set(ModDataComponents.BOOTS_LAVA, value != 0);
        } else if (component.equals(ModDataComponents.HELMET_GOGGLES)){
            player.getInventory().getArmor(slot).set(ModDataComponents.HELMET_GOGGLES, value != 0);
        } else if (component.equals(ModDataComponents.ARMOR_RENDER_TYPE)){
          player.getInventory().getArmor(slot).set(ModDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.BY_ID.apply(value));
        } else if (component.equals(ModDataComponents.PREFERRED_RENDER)){
          player.getInventory().getArmor(slot).set(ModDataComponents.PREFERRED_RENDER, PreferredRender.BY_ID.apply(value));
        }
    }
}
