package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.logistics.packagerLink.GlobalLogisticsManager;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import fr.iglee42.createqualityoflife.utils.LogisticsNetworkExtension;
import fr.iglee42.createqualityoflife.utils.NetworkPermission;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.UUID;

@Mixin(value = GlobalLogisticsManager.class,remap = false)
public class GlobalLogisticsManagerMixin {

    @Inject(method = "mayInteract",at =@At("RETURN"),cancellable = true,locals = LocalCapture.CAPTURE_FAILSOFT)
    private void createQOL$interactWithPermissions(UUID networkId, Player player, CallbackInfoReturnable<Boolean> cir, LogisticsNetwork network){
        cir.setReturnValue(cir.getReturnValue() || ((LogisticsNetworkExtension)network).createQOL$hasPlayerPermission(player, NetworkPermission.MEMBER));
    }

    @Inject(method = "mayAdministrate",at =@At("RETURN"),cancellable = true,locals = LocalCapture.CAPTURE_FAILSOFT)
    private void createQOL$administrateWithPermissions(UUID networkId, Player player, CallbackInfoReturnable<Boolean> cir, LogisticsNetwork network){
        cir.setReturnValue(cir.getReturnValue() || ((LogisticsNetworkExtension)network).createQOL$hasPlayerPermission(player, NetworkPermission.ADMIN));
    }

}
