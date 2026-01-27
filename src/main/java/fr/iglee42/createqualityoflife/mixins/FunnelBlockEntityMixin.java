package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import fr.iglee42.createqualityoflife.blockentitites.InventoryLinkerBlockEntity;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.ref.WeakReference;

@Mixin(value = FunnelBlockEntity.class,remap = false)
public abstract class FunnelBlockEntityMixin{

    @Shadow
    private InvManipulationBehaviour invManipulation;

    @Shadow
    private WeakReference<Entity> lastObserved;

    @Shadow
    protected abstract AABB getEntityOverflowScanningArea();

    @Inject(method = "activateExtractor",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/inventory/InvManipulationBehaviour;extract(Lcom/simibubi/create/foundation/item/ItemHelper$ExtractionCountMode;I)Lnet/minecraft/world/item/ItemStack;",ordinal = 1),locals = LocalCapture.CAPTURE_FAILSOFT,cancellable = true)
    private void qol$addPickupDelayIfFromLinker(CallbackInfo ci, BlockState blockState, Direction facing, Entity lastEntity, int amountToExtract, ItemHelper.ExtractionCountMode mode){
        if (invManipulation.blockEntity.getLevel().getBlockEntity(invManipulation.getTarget().getConnectedPos()) instanceof InventoryLinkerBlockEntity)
            for (Entity entity : invManipulation.blockEntity.getLevel().getEntities(null, getEntityOverflowScanningArea().inflate(1))) {
                if (entity instanceof Player) {
                    lastObserved = new WeakReference<>(entity);
                    ci.cancel();
                    return;
                }
            }
    }

}
