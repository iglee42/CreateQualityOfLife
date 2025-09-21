package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import net.minecraft.core.GlobalPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = LogisticallyLinkedBehaviour.class)
public abstract class LogisticallyLinkedBehaviourMixin {
    @Shadow public UUID freqId;

    @Shadow protected abstract GlobalPos getGlobalPos();

    @Inject(method = "initialize",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/SmartBlockEntity;setChanged()V",shift = At.Shift.AFTER))
    private void createQOL$stockManagerAddOwner(CallbackInfo ci){
        LogisticallyLinkedBehaviour behaviour = (LogisticallyLinkedBehaviour) (Object) this;
        if (behaviour.blockEntity instanceof StockManagerBlockEntity plbe)
            Create.LOGISTICS.linkAdded(freqId, getGlobalPos(), plbe.placedBy);
    }
}
