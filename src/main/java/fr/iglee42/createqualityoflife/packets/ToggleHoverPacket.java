package fr.iglee42.createqualityoflife.packets;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.items.armors.ShadowRadianceChestplate;
import fr.iglee42.createqualityoflife.registries.QOLItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class ToggleHoverPacket extends SimplePacketBase {


    public ToggleHoverPacket(){}


    public ToggleHoverPacket(FriendlyByteBuf buffer) {}


    @Override
    public void write(FriendlyByteBuf buffer) {}

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(()->{
            ServerPlayer player = context.getSender();
            if (player != null) {
                ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
                Item backtank = BacktankItem.getWornBy(player);

                if (backtank == null) return;
                if (QOLItems.SHADOW_RADIANCE_CHESTPLATE.is(backtank) && ShadowRadianceChestplate.hasPropeller(chestplate)){
                    ShadowRadianceChestplate.toggleHover(chestplate,player);
                } else if (QOLItems.SHADOW_RADIANCE_CHESTPLATE.is(backtank) && !ShadowRadianceChestplate.hasPropeller(chestplate)){
                    player.sendSystemMessage(CreateQOLLang.translateDirect("armor.ability.no_propeller").withStyle(ChatFormatting.RED),true);
                }
            }
        });
        return true;
    }
}
