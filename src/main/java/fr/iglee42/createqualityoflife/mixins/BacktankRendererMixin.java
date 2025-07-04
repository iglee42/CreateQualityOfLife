package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.equipment.armor.BacktankRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import fr.iglee42.createqualityoflife.registries.QOLBlocks;
import fr.iglee42.createqualityoflife.registries.QOLPartialModels;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BacktankRenderer.class,remap = false)
public class BacktankRendererMixin {

    @Inject(method = "getCogsModel", at=@At("HEAD"),cancellable = true)
    private static void getCogsModel(BlockState state, CallbackInfoReturnable<PartialModel> cir){
        if (QOLBlocks.SHADOW_RADIANCE_CHESTPLATE.has(state)) cir.setReturnValue(QOLPartialModels.SHADOW_RADIANCE_TANK_COGS);
        if (QOLBlocks.SHADOW_STEEL_CHESTPLATE.has(state)) cir.setReturnValue(QOLPartialModels.SHADOW_STEEL_TANK_COGS);
        if (QOLBlocks.REFINED_RADIANCE_CHESTPLATE.has(state)) cir.setReturnValue(QOLPartialModels.REFINED_RADIANCE_TANK_COGS);
    }
    @Inject(method = "getShaftModel", at=@At("HEAD"),cancellable = true)
    private static void getShaftModel(BlockState state, CallbackInfoReturnable<PartialModel> cir){
        if (QOLBlocks.SHADOW_RADIANCE_CHESTPLATE.has(state)) cir.setReturnValue(QOLPartialModels.SHADOW_RADIANCE_TANK_SHAFT);
        if (QOLBlocks.SHADOW_STEEL_CHESTPLATE.has(state)) cir.setReturnValue(QOLPartialModels.SHADOW_STEEL_TANK_SHAFT);
        if (QOLBlocks.REFINED_RADIANCE_CHESTPLATE.has(state)) cir.setReturnValue(QOLPartialModels.REFINED_RADIANCE_TANK_SHAFT);
    }

}
