package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.utils.LogisticsNetworkExtension;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = LogisticallyLinkedBlockItem.class,remap = false)
public class LogisticallyLinkedBlockItemMixin {

    @Inject(method = "assignFrequency",at = @At(value = "RETURN"))
    private static void createQOL$showNetworkName(ItemStack stack, Player player, UUID frequency, CallbackInfo ci){
        LogisticsNetwork network = Create.LOGISTICS.logisticsNetworks.get(frequency);
        if (network != null ){
            player.displayClientMessage(CreateQOLLang.translateDirect("logistically_linked.tuned_to",((LogisticsNetworkExtension)network).createQOL$getName()),true);
        }
    }

}
