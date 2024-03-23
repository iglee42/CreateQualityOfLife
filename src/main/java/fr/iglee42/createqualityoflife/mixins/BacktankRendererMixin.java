package fr.iglee42.createqualityoflife.mixins;

import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.content.equipment.armor.BacktankRenderer;
import fr.iglee42.createqualityoflife.registries.ModBlocks;
import fr.iglee42.createqualityoflife.registries.ModPartialModels;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BacktankRenderer.class,remap = false)
public class BacktankRendererMixin {

    @Inject(method = "getCogsModel", at=@At("HEAD"),cancellable = true)
    private static void getCogsModel(BlockState state, CallbackInfoReturnable<PartialModel> cir){
        if (ModBlocks.SHADOW_RADIANCE_CHESTPLATE.has(state)) cir.setReturnValue(ModPartialModels.SHADOW_RADIANCE_TANK_COGS);
    }

}
