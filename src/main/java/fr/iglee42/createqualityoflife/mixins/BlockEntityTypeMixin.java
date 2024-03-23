package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.AllBlockEntityTypes;
import fr.iglee42.createqualityoflife.registries.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {

    @Inject(method = "isValid",at = @At("HEAD"),cancellable = true)
    private void inject(BlockState state, CallbackInfoReturnable<Boolean> cir){
        if (this.equals(AllBlockEntityTypes.BACKTANK.get()) && ModBlocks.SHADOW_RADIANCE_CHESTPLATE.has(state)) cir.setReturnValue(true);
    }

}
