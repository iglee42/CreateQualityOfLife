package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import fr.iglee42.createqualityoflife.items.armors.ShadowSteelChestplate;
import fr.iglee42.createqualityoflife.registries.QOLItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class DashPacket extends SimplePacketBase {

    public static final DashPacket INSTANCE = new DashPacket();

    public DashPacket() {}
    public DashPacket(FriendlyByteBuf buffer) {}
    @Override
    public void write(FriendlyByteBuf buffer) {}

    @Override
    public boolean handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player != null) {
            ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
            Item backtank = BacktankItem.getWornBy(player);

            if (backtank == null) return true;
            if (QOLItems.SHADOW_STEEL_CHESTPLATE.is(backtank) || QOLItems.SHADOW_RADIANCE_CHESTPLATE.is(backtank)){
                ShadowSteelChestplate.dash(chestplate,player);
            }
        }
        return true;
    }
}
