package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.logistics.stockTicker.StockTickerInteractionHandler;
import fr.iglee42.createqualityoflife.statue.Statue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = StockTickerInteractionHandler.class,remap = false)
public class StockTickerInteractionHandlerMixin {

    @Inject(method = "interactWithLogisticsManager",at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/entity/player/PlayerInteractEvent$EntityInteractSpecific;getLevel()Lnet/minecraft/world/level/Level;",shift = At.Shift.BEFORE),locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private static void createQOL$ignoreIfStatueShifting(PlayerInteractEvent.EntityInteractSpecific event, CallbackInfo ci, Entity entity, Player player){
        if (entity instanceof Statue && player.isCrouching()) ci.cancel();
    }

}
